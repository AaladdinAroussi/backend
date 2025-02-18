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
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "users")
public class User  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String passwordResetToken;
    @Column(unique = true)
    private String phone;
    private String password;
    private String email;
    @Enumerated(EnumType.ORDINAL) // Stocke l'index (0, 1, 2) en base de données
    private UserStatus status = UserStatus.fromValue(1);

    @Column(columnDefinition = "SMALLINT")
    private Integer isConfirmMobile = 0;
    @Column(columnDefinition = "SMALLINT")
    private Integer isConfirmEmail = 0;


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_city",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id"))
    private Set<City> cities = new HashSet<>();

   // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private Set<VerificationCode> verificationCodes = new HashSet<>();

    public User() {}

    /*public User(String email, String password) {
        this.email = email;
        this.password = password;
    }*/
    public boolean verifyMobileCode(String inputCode, Set<VerificationCode> verificationCodes) {
        for (VerificationCode verificationCode : verificationCodes) {
            if (verificationCode.isCodeValid(inputCode)) {
                this.setIsConfirmMobile(1); // Update isConfirmMobile to 1
                return true; // Code is valid
            }
        }
        return false; // Code is invalid
    }
}
