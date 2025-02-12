package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.User;
import com.pfe.smsworkflow.Models.UserStatus;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserManagementServiceIMPL implements UserManagementService {
    @Autowired
    private UsersRepository userRepository;

    // Méthode pour bloquer un utilisateur
    @Override
    public Optional<User> blockUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus(UserStatus.BLOCKED); // Remplace setBlocked(true)
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> unblockUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setStatus(UserStatus.ACTIVE); // Remplace setBlocked(false)
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }


}
