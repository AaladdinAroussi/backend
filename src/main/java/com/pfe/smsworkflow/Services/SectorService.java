package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.Sector;
import com.pfe.smsworkflow.Models.Sector;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SectorService {
    ResponseEntity<?> create(Sector sector, Long superadminId);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getSectorsByCategory(Long categoryId);
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateSector(Sector sector,Long id);
    ResponseEntity<?> delete(Long id);
}
