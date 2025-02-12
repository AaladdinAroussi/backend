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
public class SuperAdmin extends User{


    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin", fetch = FetchType.EAGER)
    private Set<CategoryOffer> categoryOffers = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin")
    private Set<City> cities = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin", fetch = FetchType.EAGER)
    private Set<Level> levels = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin")
    private Set<Sector> Sectors = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin", cascade = CascadeType.ALL)
    private Set<Company> companies = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "superAdmin", cascade = CascadeType.ALL)
    private Set<LoginHistory> loginHistories = new HashSet<>();
    /*
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;
    */
}
