package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class CategoryOffer extends BaseEntity {
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

    // Champ pour stocker les IDs des secteurs associés
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "category_offer_sector_ids", joinColumns = @JoinColumn(name = "category_offer_id"))
    @Column(name = "sector_id")
    private List<Long> sectorIds; // Liste des IDs des secteurs associés

    public void setName(String name) {
        this.name = name != null ? name.toLowerCase() : null;
    }
}