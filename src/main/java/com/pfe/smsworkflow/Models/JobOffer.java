package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class JobOffer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Date dateCreation;//automatique quand creer
    private Date closingDate;
    private String critere;
    private int experience;
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    private Float salary;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "category_offer_id")
    private CategoryOffer categoryOffer;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name= "city_id")
    private City city ;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "admin_id") // Assurez-vous que cette colonne existe dans la base de données
    private Admin admin;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "super_admin_id") // Ajoutez cette ligne pour référencer SuperAdmin
    private SuperAdmin superAdmin; // Ajoutez ce champ pour stocker le SuperAdmin

         @JsonIgnore   //new
        @ManyToMany
        @JoinTable(
                name = "job_offer_candidat",
                joinColumns = @JoinColumn(name = "job_offer_id"),
                inverseJoinColumns = @JoinColumn(name = "candidat_id")
        )
        private Set<Candidat> candidats;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SendSms> sendSms = new ArrayList<>();
}
