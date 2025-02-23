package com.pfe.smsworkflow.Services.IMPL;

import com.pfe.smsworkflow.Models.*;
import com.pfe.smsworkflow.Repository.VerificationCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsService {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    private final RestTemplate restTemplate;
    private final String SMS_API_URL = "http://mystudents.tunisiesms.tn/api/sms";
    private final String BEARER_TOKEN = "27!WGFetrhepNwph3uDGU0QuUPswa5JUNPlh9160Lk444YNuUU2iK9yNZm6FzjTWdYo4ImMTwLG5kq7AJlhbCstJnFfsxzduejyyRI55";

    private final String dlrApiUrl = "http://mystudents.tunisiesms.tn/api/dlr";

    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void updateDlrForVerificationCodes() {

        List<VerificationCode> verificationCodes = verificationCodeRepository.findByCodeStatus(CodeStatus.NOT_SENT);



        for (VerificationCode verificationCode : verificationCodes) {

            if (verificationCode.getSmsId() != null) {

                SmsResponse dlrResponse = this.getDlr(verificationCode.getSmsId());

                if (dlrResponse.isSuccess()) {

                    verificationCode.setDlr(dlrResponse.getDlr());

                    verificationCode.setDateDlr(new Date()); // Set the current date as the delivery date

                    verificationCode.setCodeStatus(CodeStatus.SENT); // Optionally update the status

                    verificationCodeRepository.save(verificationCode);

                } else {

                    // Log the issue if DLR retrieval fails

                    System.out.println("Erreur lors de la récupération du DLR pour le code de vérification ID: " + verificationCode.getId());

                }

            }

        }

    }

    public SmsResponse sendSms(String phoneNumber, String message) {
        // Construction du JSON de la requête
        Map<String, Object> request = new HashMap<>();
        request.put("type", "55");
        request.put("sender", "TunSMS Test");

        Map<String, String> smsData = new HashMap<>();
        smsData.put("mobile", phoneNumber);
        smsData.put("sms", message);

        request.put("sms", new Map[]{smsData});

        // Configuration des en-têtes avec le Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(BEARER_TOKEN); // Ajout du token ici

        // Création de l'entité HTTP
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(SMS_API_URL, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response body to extract the message_id
                String responseBody = response.getBody();
                Long smsId = extractSmsId(responseBody);

                return new SmsResponse(true, smsId);
            } else {
                System.err.println("Erreur lors de l'envoi du SMS, statut: " + response.getStatusCode());
                return new SmsResponse(false, null);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS : " + e.getMessage());
            return new SmsResponse(false, null);
        }
    }

    private Long extractSmsId(String responseBody) {
        // Simple regex to extract message_id from the response
        String regex = "\"message_id\":(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    public SmsResponse getDlr(Long smsId) {
        // Create the HTTP request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(BEARER_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Prepare the request body for DLR
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("type", 55);
        requestBody.put("dlr", new Map[]{Map.of("message_id", smsId)});

        try {
            // Send the DLR request
            ResponseEntity<String> response = restTemplate.exchange(dlrApiUrl, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response to extract DLR information
                String responseBody = response.getBody();
                String dlr = extractDlr(responseBody); // Implement this method to parse the DLR
                Date dateDlr = extractDateDlr(responseBody); // Implement this method to parse the DLR date
                return new SmsResponse(true, smsId, dlr, dateDlr); // Return SMS ID, DLR, and DLR date
            } else {
                System.err.println("Erreur lors de la récupération du DLR, statut: " + response.getStatusCode());
                return new SmsResponse(false, null, null, null); // Return null for DLR and date
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du DLR : " + e.getMessage());
            return new SmsResponse(false, null, null, null); // Return null for DLR and date
        }
    }

    private Date extractDateDlr(String responseBody) {
        // Implement the logic to extract the DLR date from the response
        // This will depend on the format of the response you receive from the SMS API
        // Example:
        String regex = "\"date_dlr\":\"(.*?)\""; // Adjust regex based on actual response format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            String dateString = matcher.group(1);
            // Parse the date string to a Date object
            // You may need to adjust the date format based on the API response
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Adjust format as needed
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private String extractDlr(String responseBody) {
        // Implement the logic to extract DLR from the response
        // This will depend on the format of the response you receive from the SMS API
        // Example:
        String regex = "\"dlr\":\"(.*?)\""; // Adjust regex based on actual response format
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}