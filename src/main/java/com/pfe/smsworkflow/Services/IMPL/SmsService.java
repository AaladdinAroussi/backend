package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.CandidatRepository;
import com.pfe.smsworkflow.Repository.JobOfferRepository;
import com.pfe.smsworkflow.Repository.SendSmsRepository;
import com.pfe.smsworkflow.Repository.VerificationCodeRepository;
import com.pfe.smsworkflow.Security.Services.RecordNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private JobOfferRepository jobOfferRepository;
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private SendSmsRepository sendSmsRepository;

    private final RestTemplate restTemplate;
    @Value("${sms.api.url}")
    private String SMS_API_URL;

    @Value("${sms.dlr.url}")
    private String DLR_API_URL;

    @Value("${sms.token}")
    private String BEARER_TOKEN;
    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Transactional
    public ResponseEntity<?> notifyCandidates(Long jobOfferId) {
        JobOffer jobOffer = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new RecordNotFoundException("Job offer not found with id: " + jobOfferId));

        Set<Candidat> candidates = candidatRepository.findBySector(jobOffer.getSector());

        List<SendSms> smsToSend = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();

        String message = "Une nouvelle offre d'emploi a été approuvée : " + jobOffer.getTitle() + " Consultez-la sur notre site :" + jobOffer.getCompany().getWebsiteUrl();

        for (Candidat candidate : candidates) {

            phoneNumbers.add(candidate.getPhone());
        }

        SmsBatchResponse batchResponse = sendSmsToBatch(phoneNumbers, message);
        Iterator<SmsResponse> responseIterator = batchResponse.getResponses().iterator();
        Iterator<Candidat> candidateIterator = candidates.iterator();

        // assuming each SMS response corresponds to each candidate's phone number
        while (responseIterator.hasNext() && candidateIterator.hasNext()) {
            SmsResponse smsResponse = responseIterator.next();
            Candidat candidate = candidateIterator.next();

            if (smsResponse.getSmsId() != null) {
                LOGGER.info("SMS envoyé avec ID : {} à {}", smsResponse.getSmsId(), candidate.getPhone());

                SendSms sendSms = new SendSms();
                sendSms.setSms_id(smsResponse.getSmsId());
                sendSms.setText(message);
                sendSms.setPhone(candidate.getPhone());
                sendSms.setStatus(smsResponse.isSuccess() ? 1 : 0);
                sendSms.setDate_envoi(new Date());
                sendSms.setJobOffer(jobOffer);
                smsToSend.add(sendSms);
            } else {
                LOGGER.warn("Échec de l'envoi du SMS (car sms_id = null) à {}", candidate.getPhone());
            }
        }

        if (!smsToSend.isEmpty()) {
            sendSmsRepository.saveAll(smsToSend);
            LOGGER.info("{} SMS enregistrés dans la base de données.", smsToSend.size());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Candidates notified successfully!");
        response.put("status", HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    public SmsBatchResponse sendSmsToBatch(List<String> phoneNumbers, String message) {
        Map<String, Object> request = new HashMap<>();
        request.put("type", "55");
        request.put("sender", "TunSMS Test");

        List<Map<String, String>> smsList = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            Map<String, String> smsData = new HashMap<>();
            smsData.put("mobile", phoneNumber);
            smsData.put("sms", message);
            smsList.add(smsData);
        }

        request.put("sms", smsList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + BEARER_TOKEN);


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        SmsBatchResponse batchResponse = new SmsBatchResponse();

        try {
            ResponseEntity<String> response = restTemplate.exchange(SMS_API_URL, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                LOGGER.info("Raw SMS API response: {}", responseBody);

                for (String phoneNumber : phoneNumbers) {
                    Long smsId = extractSmsId(responseBody);

                    if (smsId != null) {
                        LOGGER.info("Successfully extracted SMS ID: {}", smsId);
                        batchResponse.getResponses().add(new SmsResponse(true, smsId));
                    } else {
                        LOGGER.error("SMS ID extraction error. Received response: {}", responseBody);
                        batchResponse.getResponses().add(new SmsResponse(false, null));
                    }
                }

                return batchResponse;
            } else {
                LOGGER.error("SMS API error, status: {}", response.getStatusCode());
                return batchResponse.setAllFailed();
            }
        } catch (Exception e) {
            LOGGER.error("Exception during SMS sending: {}", e.getMessage());
            return batchResponse.setAllFailed();
        }
    }
    @Scheduled(fixedRate = 300000) // Exécuter toutes les 5 minutes
    public void updateDlrForVerificationCodes() {
        List<VerificationCode> verificationCodes = verificationCodeRepository.findByCodeStatus(CodeStatus.NOT_SENT);

        for (VerificationCode verificationCode : verificationCodes) {
            if (verificationCode.getSmsId() != null) {
                SmsResponse dlrResponse = this.getDlr(verificationCode.getSmsId());

                if (dlrResponse.isSuccess()) {
                    verificationCode.setDlr(dlrResponse.getDlr());
                    verificationCode.setDateDlr(new Date());
                    verificationCode.setCodeStatus(CodeStatus.SENT);
                    verificationCodeRepository.save(verificationCode);
                } else {
                    LOGGER.error("Erreur récupération DLR pour ID: {}", verificationCode.getId());
                }
            }
        }
    }






    private Long extractSmsId(String responseBody) {
        String regex = "\"message_id\":(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    public SmsResponse getDlr(Long smsId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(BEARER_TOKEN);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", 55);
        requestBody.put("dlr", List.of(Map.of("message_id", smsId)));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(DLR_API_URL, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                String dlr = extractDlr(responseBody);
                Date dateDlr = extractDateDlr(responseBody);
                return new SmsResponse(true, smsId, dlr, dateDlr);
            } else {
                LOGGER.error("Erreur DLR: {}", response.getStatusCode());
                return new SmsResponse(false, null, null, null);
            }
        } catch (Exception e) {
            LOGGER.error("Exception DLR: {}", e.getMessage());
            return new SmsResponse(false, null, null, null);
        }
    }

    private Date extractDateDlr(String responseBody) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(responseBody);
        } catch (ParseException e) {
            LOGGER.error("Erreur parsing date DLR: {}", e.getMessage());
            return null;
        }
    }

    private String extractDlr(String responseBody) {
        return responseBody.replaceAll("\"dlr\":\"(.*?)\"", "$1");
    }

    public class SmsBatchResponse {

        private List<SmsResponse> responses = new ArrayList<>();

        public List<SmsResponse> getResponses() {
            return responses;
        }

        public void setResponses(List<SmsResponse> responses) {
            this.responses = responses;
        }

        public SmsBatchResponse setAllFailed() {
            this.responses.clear();
            this.responses.add(new SmsResponse(false, null));
            return this;
        }
    }
}
