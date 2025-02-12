package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/superAdmin/")
@PreAuthorize("hasRole('SUPERADMIN')") // ✅ Seuls les SUPERADMIN peuvent accéder à ce contrôleur
public class SuperAdminController {
    @Autowired
    private CandidatService candidatService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private CategoryOfferService categoryOfferService;
    @Autowired
    private CityService cityService;
    @Autowired
    private SectorService sectorService;
    @Autowired
    private JobOfferService jobOfferService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserManagementService userManagementService;

    // LEVEL ENDPOINTS
    @PostMapping("saveLevel")
    public ResponseEntity<?> createLevel(@RequestBody Level level, @RequestParam Long superadminId) {
        return levelService.create(level, superadminId);
    }

    @PutMapping("updateLevel")
    public ResponseEntity<?> updateLevel(@RequestBody Level level, @RequestParam Long id, @RequestParam Long superadminId) {
        return levelService.updateLevel(level, id, superadminId);
    }

    @DeleteMapping("deleteLevel")
    public ResponseEntity<?> deleteLevel(@RequestParam Long id, @RequestParam Long superadminId) {
        return levelService.delete(id, superadminId);
    }

    // CATEGORY OFFER ENDPOINTS
    @PostMapping("saveCategoryOffer")
    public ResponseEntity<?> createCategoryOffer(@RequestBody CategoryOffer categoryOffer,@RequestParam Long superAdminId) {
        return categoryOfferService.create(categoryOffer,superAdminId);
    }

    @PutMapping("updateCategoryOffer")
    public ResponseEntity<?> updateCategoryOffer(@RequestBody CategoryOffer categoryOffer, @RequestParam Long id) {
        return categoryOfferService.updateCategoryOffer(categoryOffer, id);
    }

    @DeleteMapping("deleteCategoryOffer")
    public ResponseEntity<?> deleteCategoryOffer(@RequestParam Long id) {
        return categoryOfferService.delete(id);
    }

    // CITY ENDPOINTS
    @PostMapping("saveCity")
    public ResponseEntity<?> createCity(@RequestBody City city,@RequestParam Long superAdminId) {
        return cityService.create(city,superAdminId);
    }

    @PutMapping("updateCity")
    public ResponseEntity<?> updateCity(@RequestBody City city, @RequestParam Long id) {
        return cityService.updateCity(city, id);
    }

    @DeleteMapping("deleteCity")
    public ResponseEntity<?> deleteCity(@RequestParam Long id) {
        return cityService.delete(id);
    }

    // SECTOR ENDPOINTS
    @PostMapping("saveSector")
    public ResponseEntity<?> createSector(@RequestBody Sector sector,@RequestParam Long superAdminId) {
        return sectorService.create(sector,superAdminId);
    }

    @PutMapping("updateSector")
    public ResponseEntity<?> updateSector(@RequestBody Sector sector, @RequestParam Long id) {
        return sectorService.updateSector(sector, id);
    }

    @DeleteMapping("deleteSector")
    public ResponseEntity<?> deleteSector(@RequestParam Long id) {
        return sectorService.delete(id);
    }

    // JobOffer ENDPOINTS
    @PutMapping("markOpen")
    public ResponseEntity<?> markJobAsOpen(@RequestParam Long id) {
        return jobOfferService.markJobAsOpen(id);
    }

    @GetMapping("activeJob")
    public ResponseEntity<?> getActiveJobOffers() {
        return jobOfferService.getActiveJobOffers();
    }
    @GetMapping("pendingjobs")
    public ResponseEntity<?> getPendingJobOffers() {
        return jobOfferService.getPendingJobOffers();
    }

    //pending el kol

    // COMPANY ENDPOINTS
    @GetMapping("allCompanies")
    public ResponseEntity<?> getAllCompanies() {
        return companyService.getAll();
    }

    // USER MANAGEMENT ENDPOINTS

    // Endpoint pour bloquer un utilisateur
    @PutMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        Optional<User> user = userManagementService.blockUser(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok().body("User has been blocked successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    // Débloquer un utilisateur
    @PutMapping("/{userId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long userId) {
        Optional<User> user = userManagementService.unblockUser(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok().body("User has been unblocked successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @GetMapping("getAllCandidats")
    public ResponseEntity<?> getAllCandidats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("Authenticated user: " + authentication.getName());
            System.out.println("User  roles: " + authentication.getAuthorities());
        } else {
            System.out.println("No authentication found.");
        }
        return candidatService.getAll();
    }
    @GetMapping("getAllAdmins")
    public ResponseEntity<?> getAllAdmins() {
        return adminService.getAll();
    }


}