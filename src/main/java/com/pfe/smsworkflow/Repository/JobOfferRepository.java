package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    List<JobOffer> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCritereContainingIgnoreCase(String titleKeyword, String descriptionKeyword, String critereKeyword);

    List<JobOffer> findBySalaryGreaterThanEqual(Float salary);

    List<JobOffer> findByExperienceGreaterThanEqual(int experience);

    List<JobOffer> findByAdminIdAndStatus(Long adminId, JobStatus status);

    List<JobOffer> findByDateCreationAfter(Date date);

    List<JobOffer> findByCity_NameIgnoreCase(String cityName);

    List<JobOffer> findByStatus(JobStatus status);
    List<JobOffer> findByCompany_PostCodeContainingIgnoreCase(String postcode);

    List<JobOffer> findByJobType(JobType jobType);

    List<JobOffer> findByStatusIn(List<JobStatus> statuses);

    List<JobOffer> findByCategoryOffer(CategoryOffer categoryOffer);

    List<JobOffer> findByStatusNot(JobStatus status);

    @Query("SELECT j FROM JobOffer j WHERE j.dateCreation >= :date")
    List<JobOffer> findByDateAfter(Date date);

    List<JobOffer> findByStatusAndJobType(JobStatus status, JobType jobType);

    List<JobOffer> findByCity_NameContainingIgnoreCase(String name);

    List<JobOffer> findByAdminAndStatusIn(Admin admin, List<JobStatus> statuses);

    List<JobOffer> findByAdminIdAndStatusIn(Long adminId, List<JobStatus> statuses);

    // Nouvelle méthode pour récupérer les offres avec un niveau d'expérience inférieur ou égal à celui spécifié
    List<JobOffer> findByExperienceLessThanEqual(int experience);

    List<JobOffer> findByClosingDateBeforeAndStatus(Date closingDate, JobStatus status);
    List<JobOffer> findByCompany_PostCodeIgnoreCase(String postCode);

}
