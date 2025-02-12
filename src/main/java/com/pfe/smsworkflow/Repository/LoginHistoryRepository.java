package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.LoginHistory;
import com.pfe.smsworkflow.Models.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    // Méthode pour récupérer l'historique des connexions d'un superadmin spécifique
    List<LoginHistory> findBySuperAdmin(SuperAdmin superAdmin);
}