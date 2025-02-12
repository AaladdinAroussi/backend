package com.pfe.smsworkflow.Services;

import com.pfe.smsworkflow.Models.City;
import org.springframework.http.ResponseEntity;


import java.util.List;

public interface CityService {
    ResponseEntity<?> create(City city, Long superadminId) ;
    ResponseEntity<?> getAll();
    ResponseEntity<?> getById(Long id);
    ResponseEntity<?> updateCity(City city,Long id);
    ResponseEntity<?> delete(Long id);
}
