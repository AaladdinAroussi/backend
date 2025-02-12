package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.User;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserManagementService {
    // Méthode pour bloquer un utilisateur
    Optional<User> blockUser(Long userId);
    Optional<User> unblockUser(Long userId);
}
