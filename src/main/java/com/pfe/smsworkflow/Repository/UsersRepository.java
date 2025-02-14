package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUsername(String username);
    //Boolean existsByUsername(String username);

    User findByEmail(String email);
    Optional<User> findByPhone(String phone);

    User findByPasswordResetToken(String passwordResetToken);

    Boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}
