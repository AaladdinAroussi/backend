package com.pfe.smsworkflow.Security.Services;

import com.pfe.smsworkflow.Models.LoginHistory;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Models.User;
import com.pfe.smsworkflow.Models.UserStatus;
import com.pfe.smsworkflow.Repository.LoginHistoryRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsersRepository userRepository;
    @Autowired
    private SuperadminRepository superadminRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private HttpServletRequest request;  // Pour récupérer l'adresse IP

    /*
     * La méthode loadUserByUsername de la classe UserDetailsServiceImpl retourne un objet de type UserDetailsImpl ,
     * qui contient les informations de l'utilisateur (comme le nom d'utilisateur, le mot de passe et les rôles) nécessaires
     * à Spring Security pour effectuer l'authentification et l'autorisation.
     *  Si l'utilisateur n'est pas trouvé, elle lance une exception UsernameNotFoundException.*/
    // Méthode pour récupérer l'adresse IP de la machine

    private String getServerIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress(); // Retourne l'adresse IP
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown IP"; // Gérer l'exception si l'adresse IP ne peut pas être récupérée
        }
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> userOptional;

        // Check if the user is logging in with a phone number or email
        if (login.matches("\\d+")) { // If it's a number
            userOptional = userRepository.findByPhone(login);
        } else {
            userOptional = Optional.ofNullable(userRepository.findByEmail(login));
        }

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("Utilisateur non trouvé avec : " + login)
        );

        // Check if the user is blocked
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UsernameNotFoundException("Ce compte est bloqué. Contactez l'administrateur."); // Custom message for blocked users
        }

        // Check if the user is a SuperAdmin
        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_SUPERADMIN"));

        if (isSuperAdmin && user instanceof SuperAdmin) {
            // Log the login history
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setSuperAdmin((SuperAdmin) user);
            loginHistory.setLoginDate(new Date());
            String serverIp = getServerIp();
            loginHistory.setLoginIp(serverIp);  // Save the server's IP address
            loginHistory.setUsername(login);

            loginHistoryRepository.save(loginHistory); // Save the login history
        }

        return UserDetailsImpl.build(user);
    }
}