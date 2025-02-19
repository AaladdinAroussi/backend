package com.pfe.smsworkflow.Services.IMPL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.CandidatRepository;
import com.pfe.smsworkflow.Repository.SectorRepository;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Services.CandidatService;
import com.pfe.smsworkflow.payload.response.MessageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CandidatServiceIMPL implements CandidatService {
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private SectorRepository sectorRepository;
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
            // Vérifiez l'unicité de l'email
            if (candidat.getEmail() != null && !candidat.getEmail().equals(existingCandidat.getEmail())) {
                if (userRepository.existsByEmail(candidat.getEmail())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Email already in use!", "status", HttpStatus.BAD_REQUEST.value()));
                }
            }
            // Vérifiez l'unicité du téléphone
            if (candidat.getPhone() != null && !candidat.getPhone().equals(existingCandidat.getPhone())) {
                if (userRepository.existsByPhone(candidat.getPhone())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Phone number already in use!", "status", HttpStatus.BAD_REQUEST.value()));
                }
            }
            // Mise à jour des champs de base
            if (candidat.getFullName() != null) existingCandidat.setFullName(candidat.getFullName());
            if (candidat.getPhone() != null) existingCandidat.setPhone(candidat.getPhone());
            if (candidat.getEmail() != null) existingCandidat.setEmail(candidat.getEmail());
            if (candidat.getExperience() != 0) existingCandidat.setExperience(candidat.getExperience());
            if (candidat.getLevel() != null) existingCandidat.setLevel(candidat.getLevel());
            if (candidat.getSector() != null) {

                Sector existingSector = sectorRepository.findById(candidat.getSector().getId())

                        .orElseThrow(() -> new EntityNotFoundException("Sector not found!"));

                existingCandidat.setSector(existingSector);

            }
            if (candidat.getCities() != null) existingCandidat.setCities(candidat.getCities());

            // Mise à jour des rôles uniquement si les rôles sont passés dans la requête
            if (candidat.getRoles() != null) {
                Set<Role> currentRoles = existingCandidat.getRoles();
                Set<Role> updatedRoles = candidat.getRoles();

                // Ajouter les nouveaux rôles sans supprimer les existants
                for (Role role : updatedRoles) {
                    if (!currentRoles.contains(role)) {
                        currentRoles.add(role); // Ajouter un rôle si il n'est pas déjà présent
                    }
                }
                existingCandidat.setRoles(currentRoles); // Mettre à jour les rôles
            }

            // Mise à jour des détails (OneToOne)
            if (candidat.getDetails() != null) {
                CandidateDetails existingDetails = existingCandidat.getDetails();
                CandidateDetails newDetails = candidat.getDetails();

                if (existingDetails == null) {
                    existingDetails = new CandidateDetails();
                }
                if (newDetails.getLanguages() != null) existingDetails.setLanguages(newDetails.getLanguages());
                if (newDetails.getAge() != null) existingDetails.setAge(newDetails.getAge());
                if (newDetails.getCity() != null) existingDetails.setCity(newDetails.getCity());
                if (newDetails.getAddress() != null) existingDetails.setAddress(newDetails.getAddress());
                if (newDetails.getEducation() != null) existingDetails.setEducation(newDetails.getEducation());
                if (newDetails.getLanguages() != null) existingDetails.setLanguages(newDetails.getLanguages());
                if (newDetails.getBio() != null) existingDetails.setBio(newDetails.getBio());
                if (newDetails.getWebsite() != null) existingDetails.setWebsite(newDetails.getWebsite());
                if (newDetails.getGithub() != null) existingDetails.setGithub(newDetails.getGithub());
                if (newDetails.getLinkedIn() != null) existingDetails.setLinkedIn(newDetails.getLinkedIn());

                existingCandidat.setDetails(existingDetails);
            }

            Candidat updatedCandidat = candidatRepository.save(existingCandidat);

            // Création de la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidat updated successfully!");
            response.put("candidat", updatedCandidat);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage(), "status", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error: " + e.getMessage(), "status", HttpStatus.BAD_REQUEST.value()));
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
