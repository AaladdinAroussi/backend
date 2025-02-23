package com.pfe.smsworkflow.Controllers;

import com.pfe.smsworkflow.Models.CategoryOffer;
import com.pfe.smsworkflow.Models.Sector;
import com.pfe.smsworkflow.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/Common/")
public class CommonController {

    @Autowired
    private JobOfferService jobOfferService;
    @Autowired
    private UserService userService;
    @Autowired
    private SectorService sectorService;
    @Autowired
    private CityService cityService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CategoryOfferService categoryOfferService;
    @Autowired
    private CandidatService candidatService;





    // CATEGORYOFFER ENDPOINTS
    @GetMapping("allCategoryOffers")
    public ResponseEntity<?> getAllCategoryOffers() {
        return categoryOfferService.getAll();
    }

    @GetMapping("getByIdCategoryOffer/{id}")
    public ResponseEntity<?> getCategoryOfferById(@PathVariable Long id) {
        return categoryOfferService.getById(id);
    }
    // COMPANY ENDPOINTS
    @GetMapping("getCompanyById/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        return companyService.getById(id);
    }
    @GetMapping("getAllCompanies")
    public ResponseEntity<?> getAllCompany() {
        return companyService.getAll();
    }
    // CITY ENDPOINTS
    @GetMapping("allCities")
    public ResponseEntity<?> getAllCities() {
        return cityService.getAll();
    }
    @GetMapping("getByIdCity/{id}")
    public ResponseEntity<?> getCityById(@PathVariable Long id) {
        return cityService.getById(id);
    }
    // LEVEL ENDPOINTS
    @GetMapping("allLevel")
    public ResponseEntity<?> getAllLevels() {
        return levelService.getAll();
    }
    @GetMapping("getByIdLevel/{id}")
    public ResponseEntity<?> getLevelById(@PathVariable Long id) {
        return levelService.getById(id);
    }
    // SECTOR ENDPOINTS
    @GetMapping("allSectors")
    public ResponseEntity<?> getAllSectors() {
        return sectorService.getAll();
    }
    @GetMapping("getByIdSector/{id}")
    public ResponseEntity<?> getSectorById(@PathVariable Long id) {
        return sectorService.getById(id);
    }
    @GetMapping("allSectorsByCategory")
    public ResponseEntity<?> getSectorsByCategory(@RequestParam Long categoryId) {
        return sectorService.getSectorsByCategory(categoryId);
    }
    // Filter ENDPOINTS

    // Récupérer toutes les offres d'emploi qui n'ont pas status pending
    @GetMapping("getAllExceptPending")
    public ResponseEntity<?> getAllJobOffers() {
        return jobOfferService.getAll();
    }

    //Récupérer toutes les offres d'emploi qui n'ont pas status Open



    // Récupérer une offre par ID
    @GetMapping("getbyid/{id}")
    public ResponseEntity<?> getJobOfferById(@PathVariable Long id) {
        return jobOfferService.getById(id);
    }
    // Rechercher une offre d'emploi par mot-clé
    @GetMapping("search")
    public ResponseEntity<?> searchJobOffers(@RequestParam String keyword) {
        return jobOfferService.searchJobOffers(keyword);
    }

    // Filtrer les offres par type de contrat
//    @GetMapping("filter/jobType")
//    public ResponseEntity<?> filterJobOffersByJobType(@RequestParam String jobType) {
//        return jobOfferService.filterJobOffersByJobType(jobType);
//    }

    // Filtrer par type de job
    @GetMapping("/filter-by-job-type")
    public ResponseEntity<?> filterJobOffersByJobType(@RequestParam List<String> jobType) {
        System.out.println("🔍 Requête reçue avec jobType = " + jobType);
        return jobOfferService.filterJobOffersByJobType(jobType);
    }

    //Filtrer par catégorie
    //a verifie
    @GetMapping("filter-by-category")
    public ResponseEntity<?> filterByCategory(@RequestParam String categoryName) {
        return jobOfferService.filterByCategory(categoryName);
    }

    // Filtrer par localisation
    @GetMapping("filter-by-location")
    public ResponseEntity<?> filterByLocation(@RequestParam String location) {
        return jobOfferService.filterByLocation(location);
    }

    //Filtrer par salaire
    @GetMapping("filter-by-salary")
    public ResponseEntity<?> filterBySalary(@RequestParam Float salary) {
        return jobOfferService.filterBySalary(salary);
    }

    //Filtrer par niveau d'expérience
    @GetMapping("filter-by-experience")
    public ResponseEntity<?> filterByExperienceLevel(@RequestParam String experience) {
        return jobOfferService.filterByExperienceLevel(experience);
    }


    //Filtrer par date de publication (ex: "last week", "last month")
    @GetMapping("filter-by-date")
    public ResponseEntity<?> filterByDate(@RequestParam String timeFrame) {
        return jobOfferService.filterByDate(timeFrame);
    }


    @GetMapping("/filter-job-offers")
    public ResponseEntity<?> filterJobOffers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> jobTypes,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer experienceLevel,
            @RequestParam(required = false) Float salary) {
        return jobOfferService.filterJobOffers(keyword, jobTypes, category, location, experienceLevel, salary);
    }

    @GetMapping("/getUserByEmail")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }
    @GetMapping("/getUserByPhone")
    public ResponseEntity<?> getUserByPhone(@RequestParam String phone) {
        return userService.getUserByPhone(phone);
    }

}
