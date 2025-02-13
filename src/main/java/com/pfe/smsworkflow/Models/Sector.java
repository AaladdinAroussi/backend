package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Sector extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    // Champ pour stocker les IDs des catégories associées
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sector_category_ids", joinColumns = @JoinColumn(name = "sector_id"))
    @Column(name = "category_id")
    private List<Long> categoryIds = new ArrayList<>(); // Initialize to avoid null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;

    @JsonIgnore
    @OneToMany(mappedBy = "sector")
    private List<Candidat> candidats;

    @JsonIgnore
    @OneToMany(mappedBy = "sector")
    private List<JobOffer> jobOffers = new ArrayList<>();

}