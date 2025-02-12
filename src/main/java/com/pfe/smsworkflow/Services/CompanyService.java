package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.Company;
import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.Company;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompanyService {
    ResponseEntity<?> create(Company company, Long adminId);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getAllCompanyByAdminId(Long adminId);
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateCompany(Company company,Long id);
    ResponseEntity<?> delete(Long id);
}
