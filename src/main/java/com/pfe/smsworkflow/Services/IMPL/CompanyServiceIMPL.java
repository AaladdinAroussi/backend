package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.Admin;
import com.pfe.smsworkflow.Models.Company;
import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Repository.AdminRepository;
import com.pfe.smsworkflow.Repository.CompanyRepository;
import com.pfe.smsworkflow.Repository.SuperadminRepository;
import com.pfe.smsworkflow.Services.CompanyService;
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
public class CompanyServiceIMPL implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private SuperadminRepository superadminRepository;
    // Créer une Company
    public ResponseEntity<?> create(Company company, Long adminId) {
        // Vérifier si l'Admin ou le SuperAdmin existe
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        Optional<SuperAdmin> optionalSuperAdmin = superadminRepository.findById(adminId);

        if (optionalAdmin.isPresent()) {
            company.setAdmin(optionalAdmin.get());
        } else if (optionalSuperAdmin.isPresent()) {
            company.setSuperAdmin(optionalSuperAdmin.get()); // Associer le SuperAdmin si pas d'Admin
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Admin or SuperAdmin not found", "status", HttpStatus.NOT_FOUND.value()));
        }

        // Sauvegarder la nouvelle entreprise
        Company savedCompany = companyRepository.save(company);

        // Construire la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Company created successfully");
        response.put("company", savedCompany);
        response.put("status", HttpStatus.CREATED.value());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    // Obtenir toutes les Companies
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Company> companies = companyRepository.findAll();
            if (companies.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No companies found.");
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des companies
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Companies fetched successfully!");
            response.put("companies", companies);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @Override
    public ResponseEntity<?> getAllCompanyByAdminId(Long adminId) {
        try {
            List<Company> companies = companyRepository.findByAdminId(adminId);
            if (companies.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No companies found for admin ID: " + adminId);
                response.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Réponse avec la liste des companies
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Companies fetched successfully!");
            response.put("companies", companies);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    // Obtenir une Company par ID
    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            Optional<Company> companyOptional = companyRepository.findById(id);
            if (companyOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Company found!");
                response.put("company", companyOptional.get());
                response.put("status", HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Company not found with id: " + id);
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

    // Mettre à jour une Company
    @Override
    public ResponseEntity<?> updateCompany(Company company, Long id) {
        try {
            Company existingCompany = companyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Company with ID " + id + " not found!"));
            // Mise à jour des champs de la Company
            existingCompany.setName(company.getName() == null ? existingCompany.getName() : company.getName());
            existingCompany.setAddress(company.getAddress() == null ? existingCompany.getAddress() : company.getAddress());
            existingCompany.setDescription(company.getDescription() == null ? existingCompany.getDescription() : company.getDescription());
            existingCompany.setEmail(company.getEmail() == null ? existingCompany.getEmail() : company.getEmail());
            existingCompany.setPhone(company.getPhone() == null ? existingCompany.getPhone() : company.getPhone());
            existingCompany.setImage(company.getImage() == null ? existingCompany.getImage() : company.getImage());
            existingCompany.setPostCode(company.getPostCode() == null ? existingCompany.getPostCode() : company.getPostCode());
            existingCompany.setWebsiteUrl(company.getWebsiteUrl() == null ? existingCompany.getWebsiteUrl() : company.getWebsiteUrl());

            // Mise à jour des villes
            if (company.getCities() != null && !company.getCities().isEmpty()) {
                existingCompany.setCities(company.getCities());
            }

            Company updatedCompany = companyRepository.save(existingCompany);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Company updated successfully!");
            response.put("company", updatedCompany);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Supprimer une Company
    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            Company company = companyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found!"));

            companyRepository.deleteById(id);

            // Créer une réponse de succès
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Company deleted successfully");
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
