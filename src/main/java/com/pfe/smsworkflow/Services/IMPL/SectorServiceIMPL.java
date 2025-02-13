package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.CategoryOffer;
import com.pfe.smsworkflow.Models.Sector;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Repository.CategoryOfferRepository;
import com.pfe.smsworkflow.Repository.SectorRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Services.SectorService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SectorServiceIMPL implements SectorService {

    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private SuperadminRepository superAdminRepository;
    @Autowired
    private CategoryOfferRepository categoryOfferRepository;

    // Method to create a new sector
    @Override
    public ResponseEntity<?> create(Sector sector, Long superadminId) {
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
            sector.setSuperAdmin(superAdmin);

            // Vérifier si un secteur avec le même nom existe déjà
            if (sectorRepository.existsByName(sector.getName())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: Sector name must be unique.");
                errorResponse.put("status", HttpStatus.CONFLICT.value()); // 409 Conflict
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Vérifier si les catégories associées existent
            for (Long categoryId : sector.getCategoryIds()) {
                Optional<CategoryOffer> categoryOptional = categoryOfferRepository.findById(categoryId);
                if (!categoryOptional.isPresent()) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", "Error: Category with ID " + categoryId + " does not exist.");
                    errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
            }

            // Sauvegarder l'objet Sector
            Sector savedSector = sectorRepository.save(sector);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sector added successfully!");
            response.put("sector", savedSector);
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
    // Method to get all sectors
    @Override
    public ResponseEntity<List<Sector>> getAll() {
        List<Sector> sectors = sectorRepository.findAll();
        if (sectors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Return 204 No Content if no sectors are found
        }
        return new ResponseEntity<>(sectors, HttpStatus.OK);  // Return 200 OK with sectors
    }
    @Override
    public ResponseEntity<?> getSectorsByCategory(Long categoryId) {
        // Utiliser une liste contenant l'ID de la catégorie
        List<Sector> sectors = sectorRepository.findByCategoryIdsIn(Collections.singletonList(categoryId));
        if (sectors.isEmpty()) {
            // Créer une réponse avec un message personnalisé
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No sectors found for the given category ID: " + categoryId);
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Retourner 404 Not Found avec le message
        }
        return new ResponseEntity<>(sectors, HttpStatus.OK); // Retourner 200 OK avec les secteurs
    }
    // Method to get a sector by its ID
    @Override
    public ResponseEntity<Sector> getById(Long id) {
        Optional<Sector> sectorOptional = sectorRepository.findById(id);
        if (sectorOptional.isPresent()) {
            return new ResponseEntity<>(sectorOptional.get(), HttpStatus.OK);  // Return 200 OK with the sector
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  // Return 404 Not Found if no sector found
        }
    }

    // Method to update an existing sector
    @Override
    public ResponseEntity<Sector> updateSector(Sector sector, Long id) {
        Sector existingSector = sectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id not found!"));

        try {
            if (sector.getName() != null) {
                existingSector.setName(sector.getName());  // Update only the name if provided
            }

            Sector updatedSector = sectorRepository.save(existingSector);
            return new ResponseEntity<>(updatedSector, HttpStatus.OK);  // Return 200 OK with the updated sector
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  // Return 400 Bad Request on failure
        }
    }

    // Method to delete a sector by its ID
    @Override
    public ResponseEntity<Void> delete(Long id) {
        if (!sectorRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Return 404 Not Found if sector doesn't exist
        }

        try {
            sectorRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Return 204 No Content on successful deletion
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 Internal Server Error on failure
        }
    }
}
