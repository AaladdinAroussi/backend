package com.pfe.smsworkflow.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CandidateDetails {
    private String linkedIn;
    private String website;
    private String github;
    private String age;
    private List<String> languages;
    private String bio;
    private String image; // URL or path to the image
}