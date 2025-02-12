package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.SuperAdmin;
import com.pfe.smsworkflow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SuperadminRepository extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
