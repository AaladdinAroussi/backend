package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.ERole;
import com.pfe.smsworkflow.Models.Level;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Models.User;
import com.pfe.smsworkflow.Repository.LevelRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Services.LevelService;
import jakarta.persistence.EntityExistsException;
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
public class LevelServiceIMPL implements LevelService {
    @Autowired
    private LevelRepository levelRepository ;
    @Autowired
    private SuperadminRepository superadminRepository ;
    @Autowired
    private UsersRepository usersRepository ;

    @Override
    public ResponseEntity<?> create(Level level, Long superadminId) {
        try {
            // Vérifier si le super administrateur existe dans la base de données
            SuperAdmin superAdmin = superadminRepository.findById(superadminId)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found with ID: " + superadminId));

            // Assigner le superAdmin à l'objet Level
            level.setSuperAdmin(superAdmin);

            // Check if a Level with the same name already exists
            if (levelRepository.existsByName(level.getName())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: Level name must be unique.");
                errorResponse.put("status", HttpStatus.CONFLICT.value()); // 409 Conflict
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Sauvegarder le niveau
            Level savedLevel = levelRepository.save(level);

            // Retourner un objet JSON avec un message de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Level added successfully!");
            response.put("level", savedLevel);
            response.put("status", HttpStatus.CREATED.value());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Retourner un JSON en cas d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    public ResponseEntity<?> getAll() {
        List<Level> levels = levelRepository.findAll();

        if (levels.isEmpty()) {
            // Création d'une réponse JSON avec un message et un statut
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No levels found.");
            response.put("status", HttpStatus.OK.value()); // 200 OK

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(levels);
    }



    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            // Vérifier si le niveau existe dans la base de données
            Level level = levelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Level not found with id: " + id));

            // Retourner le niveau avec un message de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Level found successfully!");
            response.put("data", level);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // Retourner un JSON en cas d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> updateLevel(Level level, Long id, Long superadminId) {
        try {
            // Vérifier si le super administrateur existe dans la base de données
            SuperAdmin superAdmin = superadminRepository.findById(superadminId)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found with ID: " + superadminId));

            // Vérifier si le niveau existe
            Level existingLevel = levelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Level with ID " + id + " not found"));

            // Mettre à jour le niveau existant
            if (level.getName() != null) {
                existingLevel.setName(level.getName());
            }
            // Sauvegarder le niveau mis à jour dans la base de données
            Level updatedLevel = levelRepository.save(existingLevel);

            // Retourner un objet JSON avec un message de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Level updated successfully!");
            response.put("level", updatedLevel);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // Retourner un JSON en cas d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Override
    public ResponseEntity<?> delete(Long id, Long superadminId) {
        try {
            // Vérifier si le super administrateur existe dans la base de données
            SuperAdmin superAdmin = superadminRepository.findById(superadminId)
                    .orElseThrow(() -> new RuntimeException("SuperAdmin not found with ID: " + superadminId));

            // Vérifier si le niveau (Level) existe dans la base de données
            Level level = levelRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Level with ID " + id + " not found!"));

            // Supprimer le niveau de la base de données
            levelRepository.deleteById(id);

            // Retourner un objet JSON avec un message de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Level with ID " + id + " deleted successfully!");
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // Retourner un JSON en cas d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }



}
