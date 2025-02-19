package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.Admin;
import com.pfe.smsworkflow.Repository.AdminRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Services.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServiceIMPL implements AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UsersRepository userRepository;

    // Créer un Admin
    @Override
    public ResponseEntity<?> create(Admin admin) {
        try {
            Admin savedAdmin = adminRepository.save(admin);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin added successfully!");
            response.put("admin", savedAdmin);
            response.put("status", HttpStatus.CREATED.value());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Créer une réponse d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Obtenir la liste des Admins
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Admin> admins = adminRepository.findAll();
            if (admins.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No admins found.");
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des admins
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admins fetched successfully!");
            response.put("admins", admins);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Obtenir un Admin par ID
    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            Optional<Admin> adminOptional = adminRepository.findById(id);
            if (adminOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Admin found!");
                response.put("admin", adminOptional.get());
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Admin not found with id: " + id);
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Mettre à jour un Admin
    @Override
    public ResponseEntity<?> updateAdmin(Admin admin, Long id) {
        try {
            Admin existingAdmin = adminRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Admin with ID " + id + " not found!"));
            // Vérifiez l'unicité de l'email
            if (admin.getEmail() != null && !admin.getEmail().equals(existingAdmin.getEmail())) {
                if (userRepository.existsByEmail(admin.getEmail())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Email already in use!", "status", HttpStatus.BAD_REQUEST.value()));
                }
            }
            // Vérifiez l'unicité du téléphone
            if (admin.getPhone() != null && !admin.getPhone().equals(existingAdmin.getPhone())) {
                if (userRepository.existsByPhone(admin.getPhone())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Phone number already in use!", "status", HttpStatus.BAD_REQUEST.value()));
                }
            }
            // Mise à jour des champs de l'Admin
            existingAdmin.setFullName(admin.getFullName() == null ? existingAdmin.getFullName() : admin.getFullName());
            existingAdmin.setPhone(admin.getPhone() == null ? existingAdmin.getPhone() : admin.getPhone());
            existingAdmin.setEmail(admin.getEmail() == null ? existingAdmin.getEmail() : admin.getEmail());
            existingAdmin.setCities(admin.getCities() == null ? existingAdmin.getCities() : admin.getCities());

            Admin updatedAdmin = adminRepository.save(existingAdmin);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin updated successfully!");
            response.put("admin", updatedAdmin);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Supprimer un Admin
    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            Admin admin = adminRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Admin with ID " + id + " not found!"));

            adminRepository.deleteById(id);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin deleted successfully");
            response.put("status", HttpStatus.NO_CONTENT.value());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
