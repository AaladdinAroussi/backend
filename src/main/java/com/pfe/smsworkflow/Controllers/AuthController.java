package com.pfe.smsworkflow.Controllers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.SectorRepository;
import com.pfe.smsworkflow.Repository.VerificationCodeRepository;
import com.pfe.smsworkflow.Security.Services.RefreshTokenService;
import com.pfe.smsworkflow.Security.Services.UserDetailsImpl;
import com.pfe.smsworkflow.Security.jwt.JwtUtils;
import com.pfe.smsworkflow.Services.CandidatService;
import com.pfe.smsworkflow.Services.IMPL.SmsService;
import com.pfe.smsworkflow.Services.UserService;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    SectorRepository sectorRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    UserService userService;
    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    CandidatService candidatService;

    @Autowired
    PasswordEncoder encoder;
    //@Autowired
    //private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    private SmsService smsService;
/*
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Vérification si login (email ou téléphone) est fourni
        if (loginRequest.getLogin() == null || loginRequest.getLogin().isEmpty()) {
            return ResponseEntity.badRequest().body("Veuillez fournir un email ou un numéro de téléphone.");
        }

        // Recherche de l'utilisateur par email ou téléphone
        Optional<User> optionalUser;
        if (loginRequest.getLogin().matches("\\d+")) { // Si c'est un numéro
            optionalUser = userRepository.findByPhone(loginRequest.getLogin());
        } else { // Sinon, c'est un email
            optionalUser = Optional.ofNullable(userRepository.findByEmail(loginRequest.getLogin()));
        }

        // Vérification si l'utilisateur existe
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erreur: Login ou mot de passe incorrect.");
        }
        else {
            // Vérification du mot de passe
            if (!isPasswordValid(optionalUser.get(), loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erreur: Login ou mot de passe incorrect.");
            }
            // Vérification si le compte est bloqué après validation du mot de passe
            if (optionalUser.get().getStatus() == UserStatus.BLOCKED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Erreur: Votre compte est bloqué. Contactez l'administrateur.");
            }
        }

        User user = optionalUser.get();





        // Vérification si le numéro est confirmé
        if (user.getIsConfirmMobile() == 0) {
            // Créer un code de vérification et l'associer avec le Candidat ou Admin
            VerificationCode verificationCode = new VerificationCode();
            String code = userService.generateVerificationCode(); // Générer le code de vérification
            verificationCode.setCode(code);

            if (user instanceof Candidat) {
                verificationCode.setCandidat((Candidat) user);
            } else if (user instanceof Admin) {
                verificationCode.setAdmin((Admin) user);
            }

            verificationCode.setCodeStatus(CodeStatus.NOT_SENT); // Vous pouvez ajuster en fonction de la logique de votre code

            verificationCodeRepository.save(verificationCode); // Sauvegarder le code

            // Envoyer le code par SMS
            String message = "Votre code de vérification est " + code; // Modifiez le message comme vous le souhaitez
            SmsResponse smsResponse = smsService.sendSms(user.getPhone(), message);
            if (smsResponse.isSuccess()) {
                // Le SMS a été correctement envoyé, vous pouvez ajouter la logique supplémentaire ici si nécessaire
            } else {
                // L'envoi du SMS a échoué. Veuillez traiter cette erreur comme il se doit.
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi du code de vérification par SMS.");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erreur: Numéro de mobile non confirmé.");
        }
        // Génération du token JWT
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getPhone());
        String jwt = jwtUtils.generateJwtToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        // Récupération des rôles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Retourner la réponse avec le token
        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getPhone(), userDetails.getEmail(), roles));
    }
*/
    // sign in without api

@PostMapping("/signin")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    // Vérification si login (email ou téléphone) est fourni
    if (loginRequest.getLogin() == null || loginRequest.getLogin().isEmpty()) {
        return ResponseEntity.badRequest().body("Veuillez fournir un email ou un numéro de téléphone.");
    }

    // Recherche de l'utilisateur par email ou téléphone
    Optional<User> optionalUser;
    if (loginRequest.getLogin().matches("\\d+")) { // Si c'est un numéro
        optionalUser = userRepository.findByPhone(loginRequest.getLogin());
    } else { // Sinon, c'est un email
        optionalUser = Optional.ofNullable(userRepository.findByEmail(loginRequest.getLogin()));
    }

    // Vérification si l'utilisateur existe
    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erreur: Login ou mot de passe incorrect.");
    }
    else {
        // Vérification du mot de passe
        if (!isPasswordValid(optionalUser.get(), loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Erreur: Login ou mot de passe incorrect.");
        }
        // Vérification si le compte est bloqué après validation du mot de passe
        if (optionalUser.get().getStatus() == UserStatus.BLOCKED) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erreur: Votre compte est bloqué. Contactez l'administrateur.");
        }
    }

    User user = optionalUser.get();





    // Vérification si le numéro est confirmé

    if (user.getIsConfirmMobile() == 0) {
        // Créer un code de vérification et l'associer avec le Candidat ou Admin
        String generatedCode = userService.generateVerificationCode(); // Générer le code de vérification
        user.setVerificationCode(generatedCode); // Set the generated code as a string
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(generatedCode); // Use the generated code
        if (user instanceof Candidat) {
            verificationCode.setCandidat((Candidat) user);
        } else if (user instanceof Admin) {
            verificationCode.setAdmin((Admin) user);
        }

        verificationCode.setCodeStatus(CodeStatus.NOT_SENT); // Vous pouvez ajuster en fonction de la logique de votre code

        verificationCodeRepository.save(verificationCode); // Sauvegarder le code
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Erreur: Numéro de mobile non confirmé.");
    }

    // Génération du token JWT
    UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getPhone());
    String jwt = jwtUtils.generateJwtToken(userDetails);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    // Récupération des rôles
    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    // Retourner la réponse avec le token
    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
            userDetails.getPhone(), userDetails.getEmail(), roles));
}


    //fin signin withhout api

    // debut verification code sansssss api

    @PostMapping("/verifyMobileCode")
    public ResponseEntity<?> verifyMobileCode(@RequestParam String phone, @RequestParam String code) {
        // Find the User by phone number
        Optional<User> optionalUser  = userRepository.findByPhone(phone);

        if (optionalUser .isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found!"));
        }

        User user = optionalUser .get();

        // Check if the user is a Candidat or Admin
        boolean isCodeValid = false;

        if (user instanceof Candidat) {
            Candidat candidat = (Candidat) user; // Cast to Candidat
            // Verify the code using the method in User class
            isCodeValid = user.verifyMobileCode(code, candidat.getVerificationCodes());
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user; // Cast to Admin
            // Assuming Admin also has verification codes, verify the code
            isCodeValid = user.verifyMobileCode(code, admin.getVerificationCodes());
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User type not supported for verification!"));
        }

        if (isCodeValid) {
            // Optionally, save the user if needed
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Mobile number confirmed successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid verification code!"));
        }
    }
    @PostMapping("/resendCode")
    public ResponseEntity<Map<String, String>> resendVerificationCode(@RequestParam Long userId) {
        try {
            userService.resendVerificationCode(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Verification code resent successfully.");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    // fin ancien envoi de sms

    // debut api
/*
    @PostMapping("/verifyMobileCode")
    public ResponseEntity<?> verifyMobileCode(@RequestParam String phone, @RequestParam String code) {
        // Récupérer l'utilisateur
        Optional<User> optionalUser  = userRepository.findByPhone(phone);
        if (optionalUser .isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur : Utilisateur non trouvé !"));
        }

        User user = optionalUser .get();

        // Log the stored verification code for debugging
        System.out.println("Stored verification code: " + user.getVerificationCode());
        System.out.println("Code provided for verification: " + code);

        // Vérifier si le code fourni correspond à celui enregistré
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code.trim())) {
            user.setVerificationCode(null); // Supprimer le code après vérification
            user.setIsConfirmMobile(1); // Marquer le numéro comme vérifié
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Numéro confirmé avec succès !"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur : Code invalide !"));
        }
    }


    @PostMapping("/resendCode")
    public ResponseEntity<Map<String, String>> resendVerificationCode(@RequestParam Long userId) {
        try {
            userService.resendVerificationCode(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Verification code resent successfully.");

            // Send the code via SMS
            /*String message = String.format("Bonjour,\n\nVotre code de vérification est : **%s**.\n\nVeuillez entrer ce code dans l'application pour vérifier votre numéro de téléphone. Si vous n'avez pas demandé ce code, veuillez ignorer ce message.\n\nMerci!", verificationCode);
            SmsResponse smsResponse = smsService.sendSms(user.getPhone(), message);

            if (!smsResponse.isSuccess()) {
                // Log the issue
                //logger.warn("Failed to send SMS to user with ID: " + userId + ". The phone number may be incorrect or closed.");
                response.put("message", "Le code de vérification a été généré, mais l'envoi par SMS a échoué. Veuillez vérifier votre numéro de téléphone.");
            } else {
                // Save the verification code in the User entity
                user.setVerificationCode(verificationCode);
                userRepository.save(user);
                response.put("message", "Le code de vérification a été envoyé avec succès.");
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

      */
    //fin api
    @PostMapping("/checkPhone")
    public ResponseEntity<String> checkPhone(@RequestBody String phone) {
        return userService.existsByPhone(phone);
    }
    @PostMapping("/checkEmail")
    public ResponseEntity<String> checkEmail(@RequestBody String email) {
        return userService.existsByEmail(email);
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
            // Assigner le secteur
            // Vérifier si le secteur existe déjà

            if (signUpRequest.getSector() != null) {
                Sector sector = sectorRepository.findById(signUpRequest.getSector())
                        .orElseThrow(() -> new RuntimeException("Error: Sector not found!"));
                candidat.setSector(sector); // Assigner le secteur récupéré
            }
            // Assigner l'expérience
            candidat.setExperience(signUpRequest.getExperience());
            // Vérifier si le rôle ROLE_CANDIDAT existe
            Role roleCandidat = roleRepository.findByName(ERole.ROLE_CANDIDAT)
                    .orElseThrow(() -> new RuntimeException("Error: Role CANDIDAT not found!"));

            // Assigner automatiquement le rôle CANDIDAT
            Set<Role> roles = new HashSet<>();
            roles.add(roleCandidat);
            candidat.setRoles(roles);  // ← Relation correctement assignée

            // Sauvegarder le candidat (enregistre d'abord dans `users`, puis `candidat`)
            userRepository.save(candidat);

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


        return ResponseEntity.ok(new MessageResponse("Admin registered successfully with ROLE_ADMIN!"));
    }

    private boolean isPasswordValid(User user, String rawPassword) {
        return encoder.matches(rawPassword, user.getPassword());
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
    public ResponseEntity<?> changePassword(@RequestParam String token, @RequestParam String oldPassword, @RequestParam String newPassword) {
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