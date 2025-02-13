package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.*;
import com.pfe.smsworkflow.Services.JobOfferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class JobOfferServiceIMPL implements JobOfferService {
    @Autowired
    private JobOfferRepository jobOfferRepository;
    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CategoryOfferRepository categoryOfferRepository;
    @Autowired
    private AdminRepository adminRepository ;

    // Créer une CategoryOffer
    public ResponseEntity<?> create(JobOffer jobOffer, Long adminId, Long companyId, Long categoryOfferId, Long cityId, Long sectorId) {
        try {
            // Retrieve the admin using the provided ID
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            // Retrieve the company using the provided ID
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));

            // Check if the company belongs to the admin
            if (!company.getAdmin().getId().equals(admin.getId())) {
                throw new RuntimeException("Admin does not have permission to add a job offer for this company");
            }

            // Retrieve other entities using the provided IDs
            CategoryOffer categoryOffer = categoryOfferRepository.findById(categoryOfferId)
                    .orElseThrow(() -> new RuntimeException("CategoryOffer not found"));
            City city = cityRepository.findById(cityId)
                    .orElseThrow(() -> new RuntimeException("City not found"));

            // Retrieve the sector using the provided ID
            Sector sector = sectorRepository.findById(sectorId)
                    .orElseThrow(() -> new RuntimeException("Sector not found"));

            // Assign entities to the job offer
            jobOffer.setAdmin(admin);
            jobOffer.setCompany(company);
            jobOffer.setCategoryOffer(categoryOffer);
            jobOffer.setCity(city);
            jobOffer.setSector(sector); // Assign the sector to the job offer

            // Automatically assign the creation date
            jobOffer.setDateCreation(new Date());

            // Set the closing date to 30 days from now if not provided
            if (jobOffer.getClosingDate() == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 30);
                jobOffer.setClosingDate(calendar.getTime());
            }

            // Set the initial status of the job offer
            jobOffer.setStatus(JobStatus.PENDING);

            // Save the job offer in the database
            JobOffer savedJobOffer = jobOfferRepository.save(jobOffer);

            // Create a success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "JobOffer created successfully!");
            response.put("jobOffer", savedJobOffer);
            response.put("status", HttpStatus.CREATED.value());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Create an error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }    @Override
    public ResponseEntity<?> getAll() {
        try {
            // Récupérer toutes les offres d'emploi, sauf celles ayant le statut "PENDING"
            List<JobOffer> jobOffers = jobOfferRepository.findByStatusNot(JobStatus.PENDING);

            // Vérifier si la liste d'offres est vide
            if (jobOffers.isEmpty()) {
                throw new RuntimeException("No job offers found excluding 'PENDING' status!");
            }

            // Créer la réponse avec les offres d'emploi filtrées
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers retrieved successfully excluding 'PENDING' status!");
            response.put("jobOffers", jobOffers);
            response.put("status", HttpStatus.OK.value());

            // Retourner la réponse
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Override
    public ResponseEntity<?> getById(Long id) {
        try {
            JobOffer jobOffer = jobOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Job offer not found with id: " + id));

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offer retrieved successfully!");
            response.put("jobOffer", jobOffer);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> updateJobOffer(JobOffer jobOffer, Long id, Long adminId, Long companyId, Long categoryOfferId, Long cityId) {
        try {
            JobOffer existingJobOffer = jobOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("JobOffer ID not found!"));

            // Vérification et mise à jour de chaque champ s'il est fourni
            if (jobOffer.getTitle() != null) existingJobOffer.setTitle(jobOffer.getTitle());
            if (jobOffer.getCategoryOffer() != null) existingJobOffer.setCategoryOffer(jobOffer.getCategoryOffer());
            if (jobOffer.getCity() != null) existingJobOffer.setCity(jobOffer.getCity());
            if (jobOffer.getExperience() != 0) existingJobOffer.setExperience(jobOffer.getExperience());
            if (jobOffer.getStatus() != null) existingJobOffer.setStatus(jobOffer.getStatus());
            if (jobOffer.getDateCreation() != null) existingJobOffer.setDateCreation(jobOffer.getDateCreation());
            if (jobOffer.getJobType() != null) existingJobOffer.setJobType(jobOffer.getJobType());
            if (jobOffer.getCritere() != null) existingJobOffer.setCritere(jobOffer.getCritere());
            if (jobOffer.getDescription() != null) existingJobOffer.setDescription(jobOffer.getDescription());
            if (jobOffer.getSalary() != null) existingJobOffer.setSalary(jobOffer.getSalary());

            // Mise à jour des relations avec des IDs s'ils sont fournis
            if (adminId != null) {
                Admin admin = adminRepository.findById(adminId)
                        .orElseThrow(() -> new RuntimeException("Admin ID not found!"));
                existingJobOffer.setAdmin(admin);
            }

            if (companyId != null) {
                Company company = companyRepository.findById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company ID not found!"));
                existingJobOffer.setCompany(company);
            }

            if (categoryOfferId != null) {
                CategoryOffer categoryOffer = categoryOfferRepository.findById(categoryOfferId)
                        .orElseThrow(() -> new RuntimeException("CategoryOffer ID not found!"));
                existingJobOffer.setCategoryOffer(categoryOffer);
            }

            if (cityId != null) {
                City city = cityRepository.findById(cityId)
                        .orElseThrow(() -> new RuntimeException("City ID not found!"));
                existingJobOffer.setCity(city);
            }

            // Sauvegarde de l'offre d'emploi mise à jour
            JobOffer updatedJobOffer = jobOfferRepository.save(existingJobOffer);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offer updated successfully!");
            response.put("jobOffer", updatedJobOffer);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Override
    public ResponseEntity<?> delete(Long id) {
        try {
            JobOffer jobOffer = jobOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Job offer not found with id: " + id));

            jobOfferRepository.deleteById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offer deleted successfully!");
            response.put("status", HttpStatus.NO_CONTENT.value());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getActiveJobOffers() {
        try {
            List<JobOffer> activeJobOffers = jobOfferRepository.findByStatusIn(List.of(JobStatus.OPEN, JobStatus.PENDING));
            if (activeJobOffers.isEmpty()) {
                throw new RuntimeException("No active job offers found!");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Active job offers retrieved successfully!");
            response.put("jobOffers", activeJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getOpenJobOffers() {
        try {
            List<JobOffer> openJobOffers = jobOfferRepository.findByStatus(JobStatus.OPEN);

            if (openJobOffers.isEmpty()) {
                throw new RuntimeException("No open job offers found!");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Open job offers retrieved successfully!");
            response.put("jobOffers", openJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Override
    public ResponseEntity<?> getActiveJobOffersByAdminId(Long adminId) {
        try {
            List<JobOffer> activeJobOffers = jobOfferRepository.findByStatusIn(List.of(JobStatus.OPEN, JobStatus.PENDING));
            if (activeJobOffers.isEmpty()) {
                throw new RuntimeException("No active job offers found for admin with ID: " + adminId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Active job offers for admin retrieved successfully!");
            response.put("jobOffers", activeJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getOpenJobOffersByAdminId(Long adminId) {
        try {
            List<JobOffer> openJobOffers = jobOfferRepository.findByAdminIdAndStatus(adminId, JobStatus.OPEN);

            if (openJobOffers.isEmpty()) {
                throw new RuntimeException("No open job offers found for admin with ID: " + adminId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Open job offers for admin retrieved successfully!");
            response.put("jobOffers", openJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    //pour recruteur
    @Override
    public ResponseEntity<?> getPendingJobOffers() {
        try {
            List<JobOffer> pendingJobOffers = jobOfferRepository.findByStatus(JobStatus.PENDING);

            // Instead of throwing an error, return an empty list if no offers are found
            if (pendingJobOffers.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // Return an empty list with 200 OK
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pending job offers retrieved successfully!");
            response.put("jobOffers", pendingJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @Override
    public ResponseEntity<?> getPendingJobOffersByAdminId(Long adminId) {
        try {
            List<JobOffer> pendingJobOffers = jobOfferRepository.findByAdminIdAndStatus(adminId, JobStatus.PENDING);

            if (pendingJobOffers.isEmpty()) {
                throw new RuntimeException("No pending job offers found for admin with ID: " + adminId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pending job offers for admin retrieved successfully!");
            response.put("jobOffers", pendingJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Override
    public ResponseEntity<?> closeJobOffer(Long id) {
        try {
            JobOffer jobOffer = jobOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Job offer not found with id: " + id));

            jobOffer.setStatus(JobStatus.CLOSED);
            jobOfferRepository.save(jobOffer);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offer closed successfully!");
            response.put("jobOffer", jobOffer);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @Override
    public ResponseEntity<?> markJobAsFilled(Long id) {
        try {
            JobOffer jobOffer = jobOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Job offer not found with id: " + id));

            jobOffer.setStatus(JobStatus.FILLED);
            jobOfferRepository.save(jobOffer);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offer marked as filled successfully!");
            response.put("jobOffer", jobOffer);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> markJobAsOpen(Long id) {
        try {
            JobOffer jobOffer = jobOfferRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Job offer not found with id: " + id));

            jobOffer.setStatus(JobStatus.OPEN);
            jobOfferRepository.save(jobOffer);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offer marked as open successfully!");
            response.put("jobOffer", jobOffer);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @Override
    public ResponseEntity<List<JobOffer>> searchJobOffers(String keyword) {
        // Check if the keyword is null or empty
        if (keyword == null || keyword.trim().isEmpty()) {
            // Return an empty list with a 200 OK status
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK); // 200 OK with empty list
        }

        // Perform the search
        List<JobOffer> jobOffers = jobOfferRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCritereContainingIgnoreCase(
                keyword, keyword, keyword);

        // Check if any job offers were found
        if (jobOffers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }

        // Return the found job offers with a 200 OK status
        return new ResponseEntity<>(jobOffers, HttpStatus.OK); // 200 OK
    }


    @Override
    public ResponseEntity<?> filterJobOffersByJobType(List<String> jobTypes) {
        if (jobTypes == null || jobTypes.isEmpty()) {
            return createErrorResponse("At least one job type is required.", HttpStatus.BAD_REQUEST);
        }

        List<JobOffer> allJobOffers = new ArrayList<>();
        for (String jobType : jobTypes) {
            JobType jobTypeEnum;
            try {
                jobTypeEnum = JobType.valueOf(jobType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return createErrorResponse("Invalid job type provided: " + jobType, HttpStatus.BAD_REQUEST);
            }

            List<JobOffer> jobOffers = jobOfferRepository.findByJobType(jobTypeEnum);
            if (!jobOffers.isEmpty()) {
                allJobOffers.addAll(jobOffers);
            }
        }

        if (allJobOffers.isEmpty()) {
            return createErrorResponse("No job offers found for the specified job types.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(allJobOffers);
    }

    /**
     * Méthode utilitaire pour générer une réponse d'erreur formatée.
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        return ResponseEntity.status(status).body(errorResponse);
    }


    @Override
    public ResponseEntity<?> filterByCategory(String categoryName) {
        try {
            // Vérifier que le nom de la catégorie n'est pas vide
            if (categoryName == null || categoryName.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Category name must be provided.");
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Trouver la catégorie d'offre d'emploi par nom
            CategoryOffer categoryOffer = categoryOfferRepository.findByName(categoryName);
            if (categoryOffer == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Category not found: " + categoryName);
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Trouver les offres d'emploi correspondant à la catégorie
            List<JobOffer> jobOffers = jobOfferRepository.findByCategoryOffer(categoryOffer);
            if (jobOffers.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "No job offers found for category: " + categoryName);
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Réponse avec les offres d'emploi filtrées par catégorie
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers filtered by category retrieved successfully!");
            response.put("jobOffers", jobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Gestion des erreurs
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> filterByDate(String timeFrame) {
        try {
            Date currentDate = new Date();
            Date startDate;

            // Déterminer la plage de dates en fonction du timeFrame
            switch (timeFrame.toLowerCase()) {
                case "today":
                    startDate = currentDate;
                    break;
                case "this_week":
                    startDate = getStartOfWeek(currentDate);
                    break;
                case "this_month":
                    startDate = getStartOfMonth(currentDate);
                    break;
                default:
                    return createErrorResponse("Invalid time frame. Use 'today', 'this_week', or 'this_month'.", HttpStatus.BAD_REQUEST);
            }

            // Rechercher les offres d'emploi créées dans la plage de dates spécifiée
            List<JobOffer> jobOffers = jobOfferRepository.findByDateCreationAfter(startDate);

            if (jobOffers.isEmpty()) {
                return createErrorResponse("No job offers found for the specified date range.", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers filtered by date retrieved successfully!");
            response.put("jobOffers", jobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            return createErrorResponse("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to get start of the week
    private Date getStartOfWeek(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Set to the start of the week
        return calendar.getTime();
    }

    // Helper method to get start of the month
    private Date getStartOfMonth(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the start of the month
        return calendar.getTime();
    }


    /**
     * Méthode utilitaire pour générer une réponse d'erreur formatée.
     */


    // Helper method to get start of the week
//    private Date getStartOfWeek(Date currentDate) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(currentDate);
//        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Set to the start of the week
//        return calendar.getTime();
//    }
//
//    // Helper method to get start of the month
//    private Date getStartOfMonth(Date currentDate) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(currentDate);
//        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the start of the month
//        return calendar.getTime();
//    }



    @Override
    public ResponseEntity<?> filterByLocation(String locationOrPostcode) {
        try {
            // Check if the input is provided
            if (locationOrPostcode == null || locationOrPostcode.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "A valid city or postcode must be provided.");
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Search for job offers based on the city name
            List<JobOffer> jobOffersByCity = jobOfferRepository.findByCity_NameIgnoreCase(locationOrPostcode);
            // Search for job offers based on the company postcode
            List<JobOffer> jobOffersByPostcode = jobOfferRepository.findByCompany_PostCodeIgnoreCase(locationOrPostcode);

            // Combine the results
            List<JobOffer> combinedJobOffers = new ArrayList<>();
            combinedJobOffers.addAll(jobOffersByCity);
            combinedJobOffers.addAll(jobOffersByPostcode);

            if (combinedJobOffers.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "No job offers found for the specified city or postcode!");
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Response with the filtered job offers
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers filtered by city or postcode retrieved successfully!");
            response.put("jobOffers", combinedJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // General error handling
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> filterBySalary(Float salary) {
        try {
            // Vérification si le salaire est fourni
            if (salary == null || salary <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Valid salary must be provided.");
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Rechercher les offres d'emploi avec un salaire supérieur ou égal au salaire spécifié
            List<JobOffer> jobOffers = jobOfferRepository.findBySalaryGreaterThanEqual(salary);

            if (jobOffers.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "No job offers found for the specified salary!");
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Réponse avec les offres d'emploi filtrées par salaire
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers filtered by salary retrieved successfully!");
            response.put("jobOffers", jobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Gestion des erreurs générales
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @Override
    public ResponseEntity<?> filterByExperienceLevel(String experience) {
        try {
            // Vérifier si l'expérience est bien un nombre entier
            if (experience == null || experience.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Experience level must be provided.");
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            int experienceLevel;
            try {
                experienceLevel = Integer.parseInt(experience);
            } catch (NumberFormatException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid experience level: " + experience);
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Rechercher les offres d'emploi avec un niveau d'expérience inférieur ou égal
            List<JobOffer> jobOffers = jobOfferRepository.findByExperienceLessThanEqual(experienceLevel);
            if (jobOffers.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "No job offers found for the specified experience level: " + experienceLevel);
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Réponse avec les offres d'emploi filtrées par niveau d'expérience
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers filtered by experience level retrieved successfully!");
            response.put("jobOffers", jobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            // Gestion des erreurs générales
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @Override
    public ResponseEntity<?> getAllCandidatByOfferId(Long offerId) {
        try {
            // Recherche de l'offre d'emploi par son ID
            Optional<JobOffer> jobOfferOptional = jobOfferRepository.findById(offerId);

            // Si l'offre d'emploi n'existe pas, retourner une erreur
            if (!jobOfferOptional.isPresent()) {
                throw new RuntimeException("Job offer with id " + offerId + " not found.");
            }

            // Récupérer la liste des candidats associés à cette offre
            Set<Candidat> candidats = jobOfferOptional.get().getCandidats();

            // Vérifier si des candidats sont trouvés
            if (candidats.isEmpty()) {
                throw new RuntimeException("No candidates found for the specified job offer.");
            }

            // Préparer la réponse avec les candidats
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidates retrieved successfully for job offer ID: " + offerId);
            response.put("candidates", candidats);
            response.put("status", HttpStatus.OK.value());

            // Retourner la réponse avec les candidats
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    public ResponseEntity<?> getCandidatById(Long candidatId) {
        try {
            // Recherche du candidat par son ID
            Optional<Candidat> candidatOptional = candidatRepository.findById(candidatId);

            // Si le candidat n'existe pas, retourner une erreur
            if (!candidatOptional.isPresent()) {
                throw new RuntimeException("Candidat with id " + candidatId + " not found.");
            }

            // Préparer la réponse avec le candidat trouvé
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Candidat retrieved successfully for candidate ID: " + candidatId);
            response.put("candidate", candidatOptional.get());
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse d'erreur
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error: " + e.getMessage());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Helper method to get the start date of the current week
    @Override
    public ResponseEntity<?> filterJobOffers(String keyword, List<String> jobTypes, String category, String location, Integer experienceLevel, Float salary) {
        try {
            // Start with an empty list of job offers
            List<JobOffer> filteredJobOffers = new ArrayList<>();

            // If a keyword is provided, search for job offers by keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                filteredJobOffers = jobOfferRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCritereContainingIgnoreCase(
                        keyword, keyword, keyword);
            } else {
                // If no keyword, fetch all job offers
                filteredJobOffers = jobOfferRepository.findByStatusNot(JobStatus.PENDING);
            }

            // Apply job type filter
            if (jobTypes != null && !jobTypes.isEmpty()) {
                filteredJobOffers = filteredJobOffers.stream()
                        .filter(offer -> jobTypes.contains(offer.getJobType().name()))
                        .collect(Collectors.toList());
            }

            // Apply category filter
            if (category != null && !category.trim().isEmpty()) {
                filteredJobOffers = filteredJobOffers.stream()
                        .filter(offer -> offer.getCategoryOffer().getName().equalsIgnoreCase(category))
                        .collect(Collectors.toList());
            }

            // Apply location filter
            if (location != null && !location.trim().isEmpty()) {
                filteredJobOffers = filteredJobOffers.stream()
                        .filter(offer -> offer.getCity().getName().equalsIgnoreCase(location))
                        .collect(Collectors.toList());
            }

            // Apply experience level filter
            if (experienceLevel != null) {
                filteredJobOffers = filteredJobOffers.stream()
                        .filter(offer -> offer.getExperience() <= experienceLevel)
                        .collect(Collectors.toList());
            }

            // Apply salary filter
            if (salary != null) {
                filteredJobOffers = filteredJobOffers.stream()
                        .filter(offer -> offer.getSalary() >= salary)
                        .collect(Collectors.toList());
            }

            // Check if any job offers were found
            if (filteredJobOffers.isEmpty()) {
                return createErrorResponse("No job offers found for the specified criteria.", HttpStatus.NOT_FOUND);
            }

            // Create response with filtered job offers
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job offers filtered successfully!");
            response.put("jobOffers", filteredJobOffers);
            response.put("status", HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return createErrorResponse("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
