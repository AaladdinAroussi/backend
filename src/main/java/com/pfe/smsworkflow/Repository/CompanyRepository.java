package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.Company;
import com.pfe.smsworkflow.Models.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    List<Company> findByAdminId(Long adminId);
    List<Company> findBySuperAdminId(Long superAdminId);
}
