package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "superadmin_id", nullable = false)
    private SuperAdmin superAdmin;

    private Date loginDate;
    //private Date logoutDate;

    private String loginIp;

    private String username;


}
