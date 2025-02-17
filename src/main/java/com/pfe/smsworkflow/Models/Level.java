package com.pfe.smsworkflow.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
public class Level extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @ManyToOne
    @JoinColumn(name = "super_admin_id")
    private SuperAdmin superAdmin;

    @JsonIgnore
    @OneToMany(mappedBy = "level")
    private List<Candidat> candidats;

    public void setName(String name) {
        this.name = name != null ? name.toLowerCase() : null;
    }
}
