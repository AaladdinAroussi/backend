package com.pfe.smsworkflow.Repository;

import com.pfe.smsworkflow.Models.SendSms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SendSmsRepository extends JpaRepository<SendSms,Long> {
    List<SendSms> findByStatus(Integer status);
}
