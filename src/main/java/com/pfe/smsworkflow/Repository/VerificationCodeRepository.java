package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.Admin;
import com.pfe.smsworkflow.Models.Candidat;
import com.pfe.smsworkflow.Models.CodeStatus;
import com.pfe.smsworkflow.Models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByCandidatId(Long candidatId);
    Optional<VerificationCode> findByAdminId(Long adminId);
    boolean existsByCandidat(Candidat candidat);
    boolean existsByAdmin(Admin admin);
    List<VerificationCode> findByCodeStatus(CodeStatus codeStatus);

}
