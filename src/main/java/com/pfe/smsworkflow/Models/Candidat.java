package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Candidat extends User{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int experience ;
    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @OneToMany(mappedBy = "candidat", fetch = FetchType.EAGER)
    private Set<Favoris> favoris = new HashSet<>();

}