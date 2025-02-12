package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
public class CategoryOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;
    @JsonIgnore
    @OneToMany(mappedBy = "categoryOffer")
    private Set<JobOffer> jobOffers = new HashSet<>();
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "category_offer_sector",
            joinColumns = @JoinColumn(name = "category_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "sector_id")
    )
    private Set<Sector> sectors = new HashSet<>();
}
