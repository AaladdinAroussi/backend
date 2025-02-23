package com.pfe.smsworkflow.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "sms")
public class SendSms {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sms_id ;
    private String text ;
    private  String phone ;
    @Column(columnDefinition = "SMALLINT")
    private  Integer status  ;
    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = 0;
        }
    }
    private Date date_envoi ;
    @Enumerated(EnumType.STRING)
    private Dlr dlr;

    private Date date_dlr ;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private JobOffer jobOffer;




}
