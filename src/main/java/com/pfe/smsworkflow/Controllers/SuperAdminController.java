package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Services.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/superAdmin/")
//@PreAuthorize("hasRole('SUPERADMIN')") // ✅ Seuls les SUPERADMIN peuvent accéder à ce contrôleur
public class SuperAdminController {
    @Autowired
    private CandidatService candidatService;
    @Autowired
    private SuperadminRepository superadminRepository;
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
    @PutMapping("block/{userId}")
    public ResponseEntity<Map<String, String>> blockUser(@PathVariable Long userId) {
        Optional<User> user = userManagementService.blockUser(userId);
        Map<String, String> response = new HashMap<>();

        if (user.isPresent()) {
            response.put("message", "User has been blocked successfully.");
            return ResponseEntity.ok().body(response);
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    // Débloquer un utilisateur
    @PutMapping("unblock/{userId}")
    public ResponseEntity<Map<String, String>> unblockUser(@PathVariable Long userId) {
        Optional<User> user = userManagementService.unblockUser(userId);
        Map<String, String> response = new HashMap<>();

        if (user.isPresent()) {
            response.put("message", "User has been unblocked successfully.");
            return ResponseEntity.ok().body(response);
        } else {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @GetMapping("getSuperAdminById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id ) {
        try {
            Optional<SuperAdmin> SuperAdminOptional = superadminRepository.findById(id);
            if (SuperAdminOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "SuperAdminOptional found!");
                response.put("SuperAdminOptional", SuperAdminOptional.get());
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "SuperAdminOptional not found with id: " + id);
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
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateSuperAdmin(@RequestParam SuperAdmin superAdmin, Long id) {
        try {
            SuperAdmin existingSuperAdmin = superadminRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("SuperAdmin with ID " + id + " not found!"));

            // Mise à jour des champs de SuperAdmin
            existingSuperAdmin.setFullName(superAdmin.getFullName() == null ? existingSuperAdmin.getFullName() : superAdmin.getFullName());
            existingSuperAdmin.setPhone(superAdmin.getPhone() == null ? existingSuperAdmin.getPhone() : superAdmin.getPhone());
            existingSuperAdmin.setEmail(superAdmin.getEmail() == null ? existingSuperAdmin.getEmail() : superAdmin.getEmail());
            existingSuperAdmin.setCities(superAdmin.getCities() == null ? existingSuperAdmin.getCities() : superAdmin.getCities());

            SuperAdmin updatedSuperAdmin = superadminRepository.save(existingSuperAdmin);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin updated successfully!");
            response.put("Superadmin", updatedSuperAdmin);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}