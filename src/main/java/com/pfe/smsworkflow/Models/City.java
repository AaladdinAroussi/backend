package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
public class City  extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

@JsonIgnore
    @ManyToMany(mappedBy = "cities")
    private Set<User> users = new HashSet<>();
@JsonIgnore
    @ManyToMany(mappedBy = "cities")
    private Set<Company> companies = new HashSet<>();
    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "city_id")
    private Set<JobOffer> jobOffers;
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;

    public void setName(String name) {
        this.name = name != null ? name.toLowerCase() : null;
    }
}
