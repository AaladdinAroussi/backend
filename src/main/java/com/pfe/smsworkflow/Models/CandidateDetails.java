package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class CandidateDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String linkedIn;
    private String website;
    private String github;
    private String age;
    private String city;
    private String address;
    private String education;
    private String languages;
    private String bio;
    private String image; // URL or path to the image
}