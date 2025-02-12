package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.JobOffer;
import com.pfe.smsworkflow.Models.JobStatus;
import com.pfe.smsworkflow.Repository.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JobOfferScheduler {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Scheduled(cron = "0 0 0 * * ?") // This will run every day at midnight
    public void closeExpiredJobOffers() {
        Date currentDate = new Date();
        List<JobOffer> jobOffers = jobOfferRepository.findByClosingDateBeforeAndStatus(currentDate, JobStatus.OPEN);

        for (JobOffer jobOffer : jobOffers) {
            jobOffer.setStatus(JobStatus.CLOSED);
            jobOfferRepository.save(jobOffer);
        }
    }
}
