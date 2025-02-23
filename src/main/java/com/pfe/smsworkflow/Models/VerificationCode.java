package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "codes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"candidat_id"}),
        @UniqueConstraint(columnNames = {"admin_id"})
})

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
    @Enumerated(EnumType.ORDINAL) // Store the ordinal value in the database
    @Column(name = "code_status", nullable = false)
    private CodeStatus codeStatus; // 0 = not sent, 1 = sent,resent =  2
    @Column(name = "sms_id")
    private Long smsId;
    @Column(name = "dlr")
    private String dlr;
    @Column(name = "date_dlr")
    private Date dateDlr;

    public VerificationCode() {
    }
    public boolean isCodeValid(String inputCode) {
        return this.code.equals(inputCode);
    }
}