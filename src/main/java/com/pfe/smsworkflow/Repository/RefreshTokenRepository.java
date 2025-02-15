package com.pfe.smsworkflow.Repository;

import java.util.Optional;

import com.pfe.smsworkflow.Models.RefreshToken;
import com.pfe.smsworkflow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  @Modifying
  int deleteByUser(User user);
}
