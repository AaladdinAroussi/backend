package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    String generateVerificationCode();
    boolean verifyMobileCode(String inputCode, Long userId);
    void resendVerificationCode(Long userId);
    ResponseEntity<User> getUserByEmail(String email);
    ResponseEntity<User> getUserByPhone(String phone);
    ResponseEntity<String> existsByPhone(String phone);
    ResponseEntity<String> existsByEmail(String email);
}
