package com.pfe.smsworkflow.Services.IMPL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Models.User;
import com.pfe.smsworkflow.Models.VerificationCode;
import com.pfe.smsworkflow.Repository.CandidatRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Services.CandidatService;
import com.pfe.smsworkflow.payload.response.MessageResponse;
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
public class CandidatServiceIMPL implements CandidatService {
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private UsersRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Créer un Candidat
    @Override
    public ResponseEntity<?> create(Candidat candidat) {
        try {
            Candidat savedCandidat = candidatRepository.save(candidat);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidat added successfully!");
            response.put("candidat", savedCandidat);
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

    // Obtenir la liste des Candidats
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Candidat> candidats = candidatRepository.findAll();
            if (candidats.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No candidates found.");
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des candidats
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidates fetched successfully!");
            response.put("candidats", candidats);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Obtenir un Candidat par ID
    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            Optional<Candidat> candidatOptional = candidatRepository.findById(id);
            if (candidatOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Candidat found!");
                response.put("candidat", candidatOptional.get());
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Candidat not found with id: " + id);
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

    // Mettre à jour un Candidat
    @Override
    public ResponseEntity<?> updateCandidat(Candidat candidat, Long id) {
        try {
            Candidat existingCandidat = candidatRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Candidat with ID " + id + " not found!"));

            // Update basic fields
            existingCandidat.setFullName(candidat.getFullName() != null ? candidat.getFullName() : existingCandidat.getFullName());
            existingCandidat.setPhone(candidat.getPhone() != null ? candidat.getPhone() : existingCandidat.getPhone());
            existingCandidat.setPassword(candidat.getPassword() != null ? candidat.getPassword() : existingCandidat.getPassword());
            existingCandidat.setEmail(candidat.getEmail() != null ? candidat.getEmail() : existingCandidat.getEmail());
            existingCandidat.setRoles(candidat.getRoles() != null ? candidat.getRoles() : existingCandidat.getRoles());
            existingCandidat.setCities(candidat.getCities() != null ? candidat.getCities() : existingCandidat.getCities());
            existingCandidat.setExperience(candidat.getExperience() != 0 ? candidat.getExperience() : existingCandidat.getExperience());
            existingCandidat.setLevel(candidat.getLevel() != null ? candidat.getLevel() : existingCandidat.getLevel());
            existingCandidat.setSector(candidat.getSector() != null ? candidat.getSector() : existingCandidat.getSector());

            // Update details using the provided methods
            if (candidat.getDetails() != null && !candidat.getDetails().isEmpty()) {
                // Assuming you have a method to extract details from the JSON string
                Map<String, Object> detailsMap = objectMapper.readValue(candidat.getDetails(), Map.class);
                existingCandidat.setDetailsFromForm(
                        (String) detailsMap.get("age"),
                        (String) detailsMap.get("city"),
                        (String) detailsMap.get("address"),
                        (String) detailsMap.get("education"),
                        (String) detailsMap.get("languages"),
                        (String) detailsMap.get("bio"),
                        (String) detailsMap.get("website"),
                        (String) detailsMap.get("github"),
                        (String) detailsMap.get("linkedin")
                );
            }

            Candidat updatedCandidat = candidatRepository.save(existingCandidat);

            // Create a success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidat updated successfully!");
            response.put("candidat", updatedCandidat);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (JsonProcessingException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error processing JSON: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    // Supprimer un Candidat
    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            Candidat candidat = candidatRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Candidat with ID " + id + " not found!"));

            candidatRepository.deleteById(id);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidat deleted successfully");
            response.put("status", HttpStatus.NO_CONTENT.value());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    public ResponseEntity<?> verifyMobileCode(Candidat candidat, String inputCode) {

        for (VerificationCode verificationCode : candidat.getVerificationCodes()) {

            if (verificationCode.isCodeValid(inputCode)) {

                candidat.setIsConfirmMobile(1); // Update isConfirmMobile to 1

                candidatRepository.save(candidat); // Save the updated Candidat

                Map<String, Object> response = new HashMap<>();

                response.put("message", "Mobile number confirmed successfully!");

                response.put("status", HttpStatus.OK.value());

                return ResponseEntity.ok(response); // Code is valid

            }

        }

        return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid verification code!")); // Code is invalid

    }

}
