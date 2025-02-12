package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.Level;
import com.pfe.smsworkflow.Models.Level;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LevelService {
    ResponseEntity<?> create(Level level, Long superadminId);
    ResponseEntity<?> getAll();
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateLevel(Level level, Long id, Long superadminId);
    ResponseEntity<?> delete(Long id, Long superadminId);
}
