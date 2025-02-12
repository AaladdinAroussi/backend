package com.pfe.smsworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmsWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsWorkflowApplication.class, args);
    }

}
