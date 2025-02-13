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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Vérifier si l'utilisateur est bloqué
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UsernameNotFoundException("Ce compte est bloqué. Contactez l'administrateur.");
        }

        // Vérifie si l'utilisateur est un superadmin
        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_SUPERADMIN"));

        if (isSuperAdmin) {
            // Enregistre l'historique de connexion
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setSuperAdmin((SuperAdmin) user);
            loginHistory.setLoginDate(new Date());
            // Récupérer l'adresse IP de la machine

            String serverIp = getServerIp();

            loginHistory.setLoginIp(serverIp);  // Enregistre l'adresse IP du serveur
            loginHistory.setUsername(username);  // Ajoute l'utilisateur qui a initié la connexion

            loginHistoryRepository.save(loginHistory); // Sauvegarde l'historique
        }

        return UserDetailsImpl.build(user);
    }


}