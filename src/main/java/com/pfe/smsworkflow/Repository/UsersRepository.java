package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    User findByPasswordResetToken(String passwordResetToken);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    boolean existsByPhone(Long phone);

    Optional<User> findByPhone(Long phone);
}
