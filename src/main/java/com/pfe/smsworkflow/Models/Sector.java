package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    @Transient
    private List<Long> categoryIds = new ArrayList<>(); // Initialize to avoid null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;
    @JsonIgnore
    @OneToMany(mappedBy = "sector")
    private List<Candidat> candidats;
    @JsonIgnore
    @ManyToMany(mappedBy = "sectors")
    private Set<CategoryOffer> categoryOffers = new HashSet<>();


    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

}
