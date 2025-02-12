package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Models.Favoris;
import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Repository.CandidatRepository;
import com.pfe.smsworkflow.Repository.FavorisRepository;
import com.pfe.smsworkflow.Repository.JobOfferRepository;
import com.pfe.smsworkflow.Services.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavorisServiceIMPL implements FavorisService {

    @Autowired
    private FavorisRepository favorisRepository;
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private JobOfferRepository jobOfferRepository;

    // Ajouter un Favoris
    @Override
    public ResponseEntity<?> add(Long jobOfferId, Long candidatId) {
        try {
            JobOffer jobOffer = jobOfferRepository.findById(jobOfferId)
                    .orElseThrow(() -> new RuntimeException("JobOffer not found!"));
            Candidat candidat = candidatRepository.findById(candidatId)
                    .orElseThrow(() -> new RuntimeException("Candidat not found!"));

            Favoris favoris = new Favoris();
            favoris.setJobOffer(jobOffer);
            favoris.setCandidat(candidat);

            Favoris savedFavoris = favorisRepository.save(favoris);

            // Réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Favoris added successfully!");
            response.put("favoris", savedFavoris);
            response.put("status", HttpStatus.CREATED.value());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Réponse d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Obtenir tous les Favoris par Candidat
    @Override
    public ResponseEntity<?> getAllByfindByCandidatId(Long candidatId) {
        try {
            List<Favoris> favorisList = favorisRepository.findByCandidatId(candidatId);

            if (favorisList.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No favoris found for this candidat.");
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des favoris
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Favoris fetched successfully!");
            response.put("favoris", favorisList);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Supprimer un Favoris
    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            Favoris favoris = favorisRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Favoris with ID " + id + " not found!"));

            favorisRepository.deleteById(id);

            // Réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Favoris deleted successfully");
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
