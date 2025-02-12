package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.City;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Repository.CityRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Services.CityService;
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
public class CityServiceIMPL implements CityService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private SuperadminRepository superAdminRepository;


    // Créer une City
    @Override
    public ResponseEntity<?> create(City city, Long superadminId) {
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

            // Assigner le superAdmin à l'objet City
            city.setSuperAdmin(superAdmin);

            // Check if a City with the same name already exists
            if (cityRepository.existsByName(city.getName())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: City name must be unique.");
                errorResponse.put("status", HttpStatus.CONFLICT.value()); // 409 Conflict
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Sauvegarder l'objet City
            City savedCity = cityRepository.save(city);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "City added successfully!");
            response.put("city", savedCity);
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
    // Obtenir toutes les Cities
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<City> cities = cityRepository.findAll();
            if (cities.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No cities found.");
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des cities
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cities fetched successfully!");
            response.put("cities", cities);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Obtenir une City par ID
    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            Optional<City> cityOptional = cityRepository.findById(id);
            if (cityOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "City found!");
                response.put("city", cityOptional.get());
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "City not found with id: " + id);
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

    // Mettre à jour une City
    @Override
    public ResponseEntity<?> updateCity(City city, Long id) {
        try {
            City existingCity = cityRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("City with ID " + id + " not found!"));

            // Mise à jour des champs de la City
            existingCity.setName(city.getName() == null ? existingCity.getName() : city.getName());

            City updatedCity = cityRepository.save(existingCity);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "City updated successfully!");
            response.put("city", updatedCity);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Supprimer une City
    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            City city = cityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("City with ID " + id + " not found!"));

            cityRepository.deleteById(id);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "City deleted successfully");
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
