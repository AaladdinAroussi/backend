package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Models.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat,Long> {
    Set<Candidat> findBySector(Sector sector);
}
