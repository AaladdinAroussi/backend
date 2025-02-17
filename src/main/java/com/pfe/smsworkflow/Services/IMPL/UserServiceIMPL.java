package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.AdminRepository;
import com.pfe.smsworkflow.Repository.CandidatRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Repository.VerificationCodeRepository;
import com.pfe.smsworkflow.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserServiceIMPL implements UserService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UsersRepository userRepository;

    @Override
    public ResponseEntity<User> getUserByPhone(String phone) {
        Optional<User> userOptional = userRepository.findByPhone(phone); // This finds the user by phone and returns an Optional
        if (userOptional.isPresent()) { // Check if the user is present
            return ResponseEntity.ok(userOptional.get()); // Return the user wrapped in a ResponseEntity
        } else {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response if user is not found
        }
    }


    @Override
    public ResponseEntity<User> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email); // This returns a User directly
        if (user != null) { // Check if the user is not null
            return ResponseEntity.ok(user); // Return the user wrapped in a ResponseEntity
        } else {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response}
        }
    }

    @Override
    public boolean verifyMobileCode(String inputCode, Long userId) {
        Optional<VerificationCode> verificationCodeOpt;

        // Check if the user is a Candidat or Admin based on userId
        verificationCodeOpt = verificationCodeRepository.findByCandidatId(userId);
        if (!verificationCodeOpt.isPresent()) {
            verificationCodeOpt = verificationCodeRepository.findByAdminId(userId);
        }

        if (verificationCodeOpt.isPresent()) {
            VerificationCode verificationCode = verificationCodeOpt.get();
            if (verificationCode.isCodeValid(inputCode)) {
                // Update the user's confirmation status
                if (verificationCode.getCandidat() != null) {
                    Candidat candidat = verificationCode.getCandidat();
                    candidat.setIsConfirmMobile(1); // Assuming Candidat has this method
                    // Save the updated Candidat entity if necessary
                } else if (verificationCode.getAdmin() != null) {
                    Admin admin = verificationCode.getAdmin();
                    admin.setIsConfirmMobile(1); // Assuming Admin has this method
                    // Save the updated Admin entity if necessary
                }

                // Update the verification code status to indicate it has been verified
                verificationCode.setCodeStatus(CodeStatus.SENT); // Set status to 'sent'
                verificationCodeRepository.save(verificationCode); // Save the updated verification code

                return true; // Code is valid
            }
        }
        return false; // Code is invalid
    }

    @Override
    public void resendVerificationCode(Long userId) {
        VerificationCode verificationCode;

        // Check if the user is a Candidat or Admin based on userId
        verificationCode = verificationCodeRepository.findByCandidatId(userId).orElse(null);
        if (verificationCode == null) {
            verificationCode = verificationCodeRepository.findByAdminId(userId).orElse(null);
        }

        // If no verification code exists for the user, create a new one
        if (verificationCode == null) {
            verificationCode = new VerificationCode();

            // Set the user (Candidat or Admin) based on userId
            // You may need to fetch the user entity here to set it correctly
            Candidat candidat = candidatRepository.findById(userId).orElse(null);
            if (candidat != null) {
                verificationCode.setCandidat(candidat);
            } else {
                Admin admin = adminRepository.findById(userId).orElse(null);
                if (admin != null) {
                    verificationCode.setAdmin(admin);
                } else {
                    throw new RuntimeException("User  not found.");
                }
            }
        }

        // Check if 30 seconds have passed since the last sent time
        Date now = new Date();
        if (verificationCode.getDateModification() != null) {
            long timeDiff = now.getTime() - verificationCode.getDateModification().getTime();
            if (timeDiff < 30000) { // 30 seconds in milliseconds
                throw new RuntimeException("You can only resend the verification code after 30 seconds.");
            }
        }

        // Generate a new code
        String newCode = generateVerificationCode();
        verificationCode.setCode(newCode);
        verificationCode.setCodeStatus(CodeStatus.RESENT); // Not sent yet
        verificationCode.setDateModification(now); // Update the last sent time

        // Save the verification code
        verificationCodeRepository.save(verificationCode);

        // Here you can add logic to send the code via SMS or email
    }
    @Override
    public String generateVerificationCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000); // Generates a random number between 100000 and 999999
        return String.valueOf(code); // Convert the integer to a String
    }
}