package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.CategoryOffer;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Repository.CategoryOfferRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Services.CategoryOfferService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryOfferServiceIMPL implements CategoryOfferService {

    @Autowired
    private CategoryOfferRepository categoryOfferRepository;
    @Autowired
    private SuperadminRepository superAdminRepository;

    // Créer une CategoryOffer
    @Override
    public ResponseEntity<?> create(CategoryOffer categoryOffer, Long superadminId) {
        try {
            // Vérifier si le superadmin existe
            Optional<SuperAdmin> superAdminOptional = superAdminRepository.findById(superadminId);
            if (!superAdminOptional.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: Superadmin with ID " + superadminId + " does not exist.");
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Récupérer l'objet SuperAdmin
            SuperAdmin superAdmin = superAdminOptional.get();

            // Assigner le superAdmin à l'objet CategoryOffer
            categoryOffer.setSuperAdmin(superAdmin); // Ensure this method exists in CategoryOffer

            // Check if a CategoryOffer with the same name already exists
            if (categoryOfferRepository.existsByName(categoryOffer.getName())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: CategoryOffer name must be unique.");
                errorResponse.put("status", HttpStatus.CONFLICT.value()); // 409 Conflict
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Sauvegarder l'objet CategoryOffer
            CategoryOffer savedCategoryOffer = categoryOfferRepository.save(categoryOffer);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "CategoryOffer added successfully!");
            response.put("categoryOffer", savedCategoryOffer);
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
    // Obtenir toutes les CategoryOffers
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<CategoryOffer> categoryOffers = categoryOfferRepository.findAll();
            if (categoryOffers.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No category offers found.");
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des CategoryOffers
            Map<String, Object> response = new HashMap<>();
            response.put("message", "CategoryOffers fetched successfully!");
            response.put("categoryOffers", categoryOffers);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Obtenir une CategoryOffer par ID
    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            Optional<CategoryOffer> categoryOfferOptional = categoryOfferRepository.findById(id);
            if (categoryOfferOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "CategoryOffer found!");
                response.put("categoryOffer", categoryOfferOptional.get());
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "CategoryOffer not found with id: " + id);
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

    // Mettre à jour une CategoryOffer
    @Override
    public ResponseEntity<?> updateCategoryOffer(CategoryOffer categoryOffer, Long id) {
        try {
            CategoryOffer existingCategoryOffer = categoryOfferRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("CategoryOffer with ID " + id + " not found!"));

            // Mise à jour des champs de la CategoryOffer
            existingCategoryOffer.setName(categoryOffer.getName() == null ? existingCategoryOffer.getName() : categoryOffer.getName());

            CategoryOffer updatedCategoryOffer = categoryOfferRepository.save(existingCategoryOffer);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "CategoryOffer updated successfully!");
            response.put("categoryOffer", updatedCategoryOffer);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Supprimer une CategoryOffer
    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            CategoryOffer categoryOffer = categoryOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("CategoryOffer with ID " + id + " not found!"));

            categoryOfferRepository.deleteById(id);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "CategoryOffer deleted successfully");
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
