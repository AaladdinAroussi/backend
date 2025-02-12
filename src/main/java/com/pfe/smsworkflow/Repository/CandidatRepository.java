package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat,Long> {
}
