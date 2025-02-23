package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.CategoryOffer;
import com.pfe.smsworkflow.Models.JobOffer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface JobOfferService {

    public ResponseEntity<?> create(JobOffer jobOffer, Long adminId, Long companyId, Long categoryOfferId, Long cityId, Long sectorId);
    ResponseEntity<?> getAll();
    public ResponseEntity<?> updateJobOffer(JobOffer jobOffer, Long id, Long adminId, Long companyId, Long categoryOfferId, Long cityId);
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> notifyCandidates(Long jobOfferId);


    ResponseEntity<?> getActiveJobOffers(); //Récupérer les offres actives uniquement( statut "OPEN"&"PENDING")//superadmin
    ResponseEntity<?> getOpenJobOffers(); //Récupérer les offres open uniquement( statut "OPEN")
    ResponseEntity<?> getPendingJobOffers();
    ResponseEntity<?> getActiveJobOffersByAdminId(Long adminId); // Active (OPEN & PENDING)
    ResponseEntity<?> getOpenJobOffersByAdminId(Long adminId); // Seulement OPEN
    ResponseEntity<?> getPendingJobOffersByAdminId(Long adminId); // Seulement PENDING
    ResponseEntity<?> getAllCandidatByOfferId(Long offerId);
    ResponseEntity<?> getCandidatById(Long candidatId);
     ResponseEntity<?> filterJobOffersByJobType(List<String> jobTypes);
    ResponseEntity<?> closeJobOffer(Long id); //Marquer une offre comme fermée
    ResponseEntity<?> markJobAsFilled(Long id); //pour indiquer qu'une offre a été pourvue (Filled)
    ResponseEntity<?> markJobAsOpen(Long id);

    ResponseEntity<?> searchJobOffers(String keyword);
    //Filtrer les offres par contractType
    public ResponseEntity<?> filterByCategory(String categoryName);
    ResponseEntity<?> filterByDate(String timeFrame);// Filtrer les offres d'emploi en fonction de la période spécifiée
    ResponseEntity<?> filterByLocation(String location);
    ResponseEntity<?> filterBySalary(Float salary);
    ResponseEntity<?> filterByExperienceLevel(String experience);

    ResponseEntity<?> filterJobOffers(String keyword, List<String> jobTypes, String category, String location, Integer experienceLevel, Float salary);


}
