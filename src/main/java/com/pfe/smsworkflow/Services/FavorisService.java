package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.Favoris;
import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.Favoris;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FavorisService {
    ResponseEntity<?> add(Long jobOfferId, Long CandidatId);
    ResponseEntity<?> getAllByfindByCandidatId(Long userId);
    ResponseEntity<?> delete(Long id);
}
