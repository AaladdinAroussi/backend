package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.Company;
import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Models.User;
import com.pfe.smsworkflow.Repository.UsersRepository;
import com.pfe.smsworkflow.Services.AdminService;
import com.pfe.smsworkflow.Services.CompanyService;
import com.pfe.smsworkflow.Services.FavorisService;
import com.pfe.smsworkflow.Services.JobOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
//@CrossOrigin("*")
@RequestMapping("api/commonAdmin/")
public class CommonAdminController {
    @Autowired
    private JobOfferService jobOfferService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UsersRepository userRepository;

    // COMPANY ENDPOINTS
    @PostMapping("saveCompany/{adminId}")
    public ResponseEntity<?> createCompany(@PathVariable Long adminId, @RequestBody Company company) {
        return companyService.create(company, adminId);
    }
// Récupérer toutes les entreprises par adminId
@GetMapping("getAllCompanyByUserId/{userId}")
public ResponseEntity<?> getAllCompanyByUserId(@PathVariable Long userId) {
    Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "User not found", "status", HttpStatus.NOT_FOUND.value()));
    }

    String role = user.get() instanceof SuperAdmin ? "SUPER_ADMIN" : "ADMIN";
    return companyService.getAllCompanyByUserId(userId, role);
}


    @PutMapping("updateCompany")
    public ResponseEntity<?> updateCompany(@RequestBody Company company, @RequestParam Long id) {
        return companyService.updateCompany(company, id);
    }

    @DeleteMapping("deleteCompany/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        return companyService.delete(id);
    }



    // Ajouter une nouvelle offre d'emploi (Admin & SuperAdmin)
    //add adminId ou superAdminId
    @PostMapping("/create")
    public ResponseEntity<?> createJobOffer(@RequestBody JobOffer jobOffer,
                                            @RequestParam Long userId,
                                            @RequestParam Long companyId,
                                            @RequestParam Long categoryOfferId,
                                            @RequestParam Long cityId,
                                            @RequestParam Long sectorId) {
        return jobOfferService.create(jobOffer, userId, companyId, categoryOfferId, cityId, sectorId);
    }


    // Mettre à jour une offre d'emploi
    @PutMapping("update")
    public ResponseEntity<?> updateJobOffer(@RequestParam Long id,
                                            @RequestParam(required = false) Long adminId,
                                            @RequestParam(required = false) Long companyId,
                                            @RequestParam(required = false) Long categoryOfferId,
                                            @RequestParam(required = false) Long cityId,
                                            @RequestBody JobOffer jobOffer) {
        return jobOfferService.updateJobOffer(jobOffer, id, adminId, companyId, categoryOfferId, cityId);
    }

    // Supprimer une offre d'emploi
    @DeleteMapping("delete")
    public ResponseEntity<?> deleteJobOffer(@RequestParam Long id) {
        return jobOfferService.delete(id);
    }

    //kana nzal 3la apply button
    @GetMapping("getAllCandidatbyofferId")
    public ResponseEntity<?> getAllCandidatbyofferId(@RequestParam Long offerId) {
        return jobOfferService.getAllCandidatByOfferId(offerId);
    }
    @GetMapping("getCandidatbyId")
    public ResponseEntity<?> getCandidatbyId(@RequestParam Long candidatId) {
        return jobOfferService.getCandidatById(candidatId);
    }

    @DeleteMapping("deleteadmin/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        return adminService.delete(id);
    }
}
