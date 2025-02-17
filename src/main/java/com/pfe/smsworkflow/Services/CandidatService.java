package com.pfe.smsworkflow.Services;


import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Models.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CandidatService {
     ResponseEntity<?> create(Candidat candidat);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateCandidat(Candidat candidat,Long id);
    ResponseEntity<?> delete(Long id);
    ResponseEntity<?> verifyMobileCode(Candidat candidat, String inputCode);
}
