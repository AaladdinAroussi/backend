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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @OneToMany(mappedBy = "candidat", fetch = FetchType.EAGER)
    private Set<Favoris> favoris = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<VerificationCode> verificationCodes = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", referencedColumnName = "id")
    private CandidateDetails details;

}