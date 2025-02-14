package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

}
