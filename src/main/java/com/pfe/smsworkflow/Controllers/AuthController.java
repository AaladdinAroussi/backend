package com.pfe.smsworkflow.Controllers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.VerificationCodeRepository;
import com.pfe.smsworkflow.Security.Services.RefreshTokenService;
import com.pfe.smsworkflow.Security.Services.UserDetailsImpl;
import com.pfe.smsworkflow.Security.jwt.JwtUtils;
import com.pfe.smsworkflow.exception.TokenRefreshException;
import com.pfe.smsworkflow.payload.request.LoginRequest;
import com.pfe.smsworkflow.payload.request.SignupRequest;
import com.pfe.smsworkflow.payload.request.TokenRefreshRequest;
import com.pfe.smsworkflow.payload.response.JwtResponse;
import com.pfe.smsworkflow.payload.response.MessageResponse;
import com.pfe.smsworkflow.payload.response.TokenRefreshResponse;
import com.pfe.smsworkflow.Repository.RoleRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
//    @Autowired
//    private JavaMailSender emailSender ;
    @Autowired
    UsersRepository userRepository;
    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;


    private String generateVerificationCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000); // Generates a random number between 100000 and 999999
        return String.valueOf(code); // Convert the integer to a String
    }

    @PostMapping("/signupCandidat")
    public ResponseEntity<?> registerCandidat(@Valid @RequestBody SignupRequest signUpRequest) {
        // Vérifier si le numéro de téléphone existe déjà
        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Phone number is already in use!"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Créer un candidat
        Candidat candidat = new Candidat();
        candidat.setFullName(signUpRequest.getFullName()); // Ajoutez cette ligne
        candidat.setEmail(signUpRequest.getEmail());
        candidat.setPassword(encoder.encode(signUpRequest.getPassword()));
        candidat.setPhone(signUpRequest.getPhone());

        // Vérifier si le rôle ROLE_CANDIDAT existe
        Role roleCandidat = roleRepository.findByName(ERole.ROLE_CANDIDAT)
                .orElseThrow(() -> new RuntimeException("Error: Role CANDIDAT not found!"));

        // Assigner automatiquement le rôle CANDIDAT
        Set<Role> roles = new HashSet<>();
        roles.add(roleCandidat);
        candidat.setRoles(roles);  // ← Relation correctement assignée

        // Sauvegarder le candidat (enregistre d'abord dans `users`, puis `candidat`)
        userRepository.save(candidat);
        // Créer un code de vérification
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCandidat(candidat); // Associer le code de vérification à l'utilisateur
        verificationCode.setCode(generateVerificationCode()); // Méthode pour générer un code
        verificationCode.setCodeStatus(0); // 0 = non envoyé
        // Sauvegarder le code de vérification
        verificationCodeRepository.save(verificationCode);
        return ResponseEntity.ok(new MessageResponse("Candidat registered successfully with ROLE_CANDIDAT!"));
    }
    @PostMapping("/signupAdmin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        // Vérifier si le numéro de téléphone existe déjà
        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Phone number is already in use!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }



        // Créer un Admin
        Admin admin = new Admin();
        admin.setFullName(signUpRequest.getFullName());
        admin.setEmail(signUpRequest.getEmail());
        admin.setPassword(encoder.encode(signUpRequest.getPassword()));
        admin.setPhone(signUpRequest.getPhone());

        // Vérifier si le rôle ROLE_ADMIN existe
        Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found!"));

        // Assigner automatiquement le rôle ADMIN
        Set<Role> roles = new HashSet<>();
        roles.add(roleAdmin);
        admin.setRoles(roles);  // ← Relation correctement assignée

        // Sauvegarder l'admin (enregistre d'abord dans `users`, puis `Admin`)
        userRepository.save(admin);
        // Créer un code de vérification

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setAdmin(admin); // Associer le code de vérification à l'utilisateur
        verificationCode.setCode(generateVerificationCode()); // Méthode pour générer un code
        verificationCode.setCodeStatus(0); // 0 = non envoyé
        // Sauvegarder le code de vérification
        verificationCodeRepository.save(verificationCode);

        return ResponseEntity.ok(new MessageResponse("Admin registered successfully with ROLE_ADMIN!"));
    }
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser (@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> user;

        // Vérifier si l'utilisateur se connecte avec un téléphone ou un email
        if (loginRequest.getLogin() != null && !loginRequest.getLogin().isEmpty()) {
            // Check if the login is a phone number or an email
            if (loginRequest.getLogin().matches("\\d+")) { // If it's all digits, treat it as a phone number
                user = userRepository.findByPhone(loginRequest.getLogin());
            } else {
                user = Optional.ofNullable(userRepository.findByEmail(loginRequest.getLogin()));
            }
        } else {
            return ResponseEntity.badRequest().body("Veuillez fournir un email ou un numéro de téléphone.");
        }

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non trouvé");
        }

        // Authentifier avec l'utilisateur trouvé
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.get().getPhone(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getPhone(), userDetails.getEmail(), roles));
    }
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser (@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {
        String refreshToken = tokenRefreshRequest.getRefreshToken();

        // Validate the refresh token
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Refresh token is required!"));
        }

        // Check if the refresh token exists and is valid
        Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(refreshToken);
        if (tokenOptional.isPresent()) {
            // Invalidate the refresh token
            refreshTokenService.deleteByToken(refreshToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid refresh token!"));
        }

        // Optionally, you can clear the SecurityContext
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getPhone());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }



//    @GetMapping("/confirm")
//    public ResponseEntity<?> confirm(@RequestParam String email){
//        User u = userRepository.findByEmail(email);
//        if(u != null){
//            u.setConfirm(true);
//            userRepository.save(u);
//            return ResponseEntity.ok(new MessageResponse("confirm updated !!"));
//        }
//        else{
//            return ResponseEntity.ok(new MessageResponse("User not found !!"));
//        }
//    }
    @PostMapping("/resetPassword/{passwordResetToken}")
    public HashMap<String , String>resetPassword(@PathVariable String passwordResetToken , String newPassword){
        User userExisting = userRepository.findByPasswordResetToken(passwordResetToken);
        HashMap msg = new HashMap();
        if(userExisting != null){
            userExisting.setId(userExisting.getId());
            userExisting.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userExisting.setPasswordResetToken(null);
            userRepository.save(userExisting);
            msg.put("resetPassword","Proccesed");
            return msg;
        }
        else {
            msg.put("resetPassword","failed");
            return msg;
        }

    }



    @PostMapping("/forgetPassword")
    public HashMap<String,String> forgetPassword(String email) throws MessagingException {
        HashMap msg = new HashMap();
        User userexisting = userRepository.findByEmail(email);
        if(userexisting == null){
            msg.put("user" , "User not Found");
            return msg;
        }
        UUID token = UUID.randomUUID();
        userexisting.setPasswordResetToken(token.toString());
        userexisting.setId(userexisting.getId());
//        String from ="admin@gmail.com";
//        String to = userexisting.getEmail();
//        MimeMessage message = emailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//        helper.setSubject("Complete Registration !!");
//        helper.setFrom(from);
//        helper.setTo(to);
//        helper.setText("Votre code est  :   " + userexisting.getPasswordResetToken());
//        emailSender.send(message);
        userRepository.saveAndFlush(userexisting);
        msg.put("token" ,  userexisting.getPasswordResetToken());
        return msg;
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(String token, String oldPassword, String newPassword) {
        String email = jwtUtils.getEmailFromJwtToken(token);
        User user = userRepository.findByEmail(email);
        if (!encoder.matches(oldPassword,user.getPassword())) {
            throw new RuntimeException("Wrong password");
        } else if(newPassword.equals(oldPassword)){
            throw new RuntimeException("Write a new password");
        }

        else {
            //encode ta3mel el hachage mta3 password
            //user.setPassword(encoder.encode(newPassword));
            user.setPassword(new BCryptPasswordEncoder().encode((newPassword)));
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Password changed"));
        }
    }


}
