package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.Admin;
import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.Admin;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {
    ResponseEntity<?> create(Admin admin);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateAdmin(Admin admin,Long id);
    ResponseEntity<?> delete(Long id);
}
