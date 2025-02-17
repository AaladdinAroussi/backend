package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
public class Candidat extends User{


    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int experience ;
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @OneToMany(mappedBy = "candidat", fetch = FetchType.EAGER)
    private Set<Favoris> favoris = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<VerificationCode> verificationCodes = new HashSet<>();

    @Lob
    private String details;

    // Sérialiser tous les champs non attribués directement à Candidat dans l'attribut details

    public void setDetailsFromForm( String age, String city, String address, String education, String languages, String bio, String website, String github, String linkedin) throws IOException {
        Map<String, Object> detailsMap = new HashMap<>();

        detailsMap.put("age", age);
        detailsMap.put("city", city);
        detailsMap.put("address", address);
        // Ajoutez les champs de Candidat
        detailsMap.put("education", education);
        detailsMap.put("languages", languages);
        detailsMap.put("bio", bio);
        detailsMap.put("website", website);
        detailsMap.put("github", github);
        detailsMap.put("linkedin", linkedin);
        // Convertir le Map en JSON et l'enregistrer dans details
        this.details = objectMapper.writeValueAsString(detailsMap);
    }
    // Désérialiser l'attribut details en objet
    public <T> T getDetailsAsObject(Class<T> clazz) throws IOException {
        return objectMapper.readValue(this.details, clazz);
    }

























//encode
  /*  @Column(columnDefinition = "text")
    private String candidateDetails; // Store JSON as a string
    // Optionally, you can create a method to convert to/from CandidateDetails
    public CandidateDetails getCandidateDetailsAsObject() {
        try {
            return objectMapper.readValue(this.candidateDetails, CandidateDetails.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
   /* public CandidateDetails getCandidateDetailsAsObject() {
        CandidateDetails details = new CandidateDetails();
        details.setLinkedIn(this.candidateDetails.getLinkedIn());
        details.setWebsite(this.candidateDetails.getWebsite());
        details.setGithub(this.candidateDetails.getGithub());
        details.setAge(this.candidateDetails.getAge());
        details.setLanguages(this.candidateDetails.getLanguages());
        details.setBio(this.candidateDetails.getBio());
        details.setImage(this.candidateDetails.getImage());

        // Convert cities to a list of strings
        details.setCities(this.cities.stream()
                .map(City::getName) // Assuming City has a getName() method
                .collect(Collectors.toList()));

        return details;
    }

    public void setCandidateDetailsFromObject(CandidateDetails details) {
        try {
            this.candidateDetails = objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }*/


}