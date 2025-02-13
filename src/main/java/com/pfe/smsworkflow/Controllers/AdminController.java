package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.Admin;
import com.pfe.smsworkflow.Models.Role;
import com.pfe.smsworkflow.Repository.RoleRepository;
import com.pfe.smsworkflow.Services.AdminService;
import com.pfe.smsworkflow.Services.JobOfferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@RequestMapping("api/admin/")
public class AdminController {
    @Autowired
    private JobOfferService jobOfferService;

    @Autowired
    private AdminService adminService;
@Autowired
private RoleRepository roleRepository ;
    @PostMapping("create")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin, @RequestParam Long roleId) {
        try {
            // Récupérer le rôle par ID
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));

            // Assigner le rôle à l'admin (en enveloppant le rôle dans un Set)
            admin.setRoles(new HashSet<>(Collections.singletonList(role)));

            // Créer l'administrateur
            ResponseEntity<?> createdAdmin = adminService.create(admin);

            // Réponse de succès avec les informations de l'administrateur créé
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin created successfully!");
            response.put("admin", createdAdmin);
            response.put("status", HttpStatus.CREATED.value());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            // Cas où le rôle n'a pas été trouvé
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            // Gestion des autres exceptions
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @GetMapping("all")
    public ResponseEntity<?> getAllAdmins() {
        return adminService.getAll();
    }

    @GetMapping("byid/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateAdmin(@RequestBody Admin admin, @PathVariable Long id) {
        return adminService.updateAdmin(admin, id);
    }



    // Marquer une offre comme "pourvue" (Filled)
    @PutMapping("markFilled")
    public ResponseEntity<?> markJobAsFilled(@RequestParam Long id) {
        return jobOfferService.markJobAsFilled(id);
    }

    // Fermer une offre d'emploi
    @PutMapping("close")
    public ResponseEntity<?> closeJobOffer(@RequestParam Long id) {
        return jobOfferService.closeJobOffer(id);
    }

    // Récupérer les offres actives (OPEN & PENDING) par Admin ID
    @GetMapping("activeJobs")
    public ResponseEntity<?> getActiveJobOffersByAdmin(@RequestParam Long adminId) {
        return jobOfferService.getActiveJobOffersByAdminId(adminId);
    }

    //Récupérer les offres ouvertes (OPEN) par Admin ID
    @GetMapping("openJobs")
    public ResponseEntity<?> getOpenJobOffersByAdmin(@RequestParam Long adminId) {
        return jobOfferService.getOpenJobOffersByAdminId(adminId);
    }

    //Récupérer les offres en attente (PENDING) par Admin ID
    @GetMapping("pendingJobs")
    public ResponseEntity<?> getPendingJobOffersByAdmin(@RequestParam Long adminId) {
        return jobOfferService.getPendingJobOffersByAdminId(adminId);
    }




}
