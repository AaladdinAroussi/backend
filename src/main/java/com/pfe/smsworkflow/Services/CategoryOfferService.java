package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.CategoryOffer;
import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.CategoryOffer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryOfferService {
    ResponseEntity<?> create(CategoryOffer categoryOffer, Long superadminId);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateCategoryOffer(CategoryOffer categoryOffer,Long id);
    ResponseEntity<?> delete(Long id);
}
