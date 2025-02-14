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
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    private String name;
    private String description;
    private String email;
    private Long phone;
    private String image;
    private String postCode;
    private String websiteUrl;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "company_city",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private Set<City> cities = new HashSet<>();
   @JsonIgnore
   @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<JobOffer> jobOffers;

    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;

}
