package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.Admin;
import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Services.CandidatService;
import com.pfe.smsworkflow.Services.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin("*")
@RestController
@RequestMapping("api/candidat/")
public class CandidatController {
    @Autowired
    private FavorisService favorisService;
    @Autowired
    private CandidatService candidatService;


    // Favoris ENDPOINTS
    @PostMapping("/{candidatId}/favoris/{jobOfferId}")
    public ResponseEntity<?> addFavori(@RequestParam Long candidatId, @RequestParam Long jobOfferId) {
        return favorisService.add(jobOfferId, candidatId);
    }
    @GetMapping("/{candidatId}/favoris")
    public ResponseEntity<?> getFavoris(@RequestParam Long candidatId) {
        return favorisService.getAllByfindByCandidatId(candidatId);
    }
    @DeleteMapping("/favoris/{favorisId}")
    public ResponseEntity<?> deleteFavori(@RequestParam Long favorisId) {
        return favorisService.delete(favorisId);
    }

    // Supprimer un Candidat
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteCandidat(@PathVariable Long id) {
        return candidatService.delete(id);
    }
    @GetMapping("getCandidatById/{id}")
    public ResponseEntity<?> getCandidatById(@PathVariable Long id) {
        return candidatService.getById(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateAdmin(@RequestBody Candidat candidat, @PathVariable Long id) {
        return candidatService.updateCandidat(candidat, id);
    }

}
