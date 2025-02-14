package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "verification_codes")

public class VerificationCode extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) // Many verification codes can belong to one user
    @JoinColumn(name = "candidat_id") // Foreign key column in the verification_codes table , nullable = false
    private Candidat candidat; // The user associated with this verification code
    @ManyToOne(fetch = FetchType.LAZY) // Many verification codes can belong to one user
    @JoinColumn(name = "admin_id") // Foreign key column in the verification_codes table , nullable = false
    private Admin admin; // The user associated with this verification code
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "code_status", nullable = false)
    private int codeStatus; // 0 = not sent, 1 = sent
    public VerificationCode() {
    }
    /*public VerificationCode(User user, String code, int codeStatus) {
        this.user = user;
        this.code = code;
        this.codeStatus = codeStatus;
    }*/
}