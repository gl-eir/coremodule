package com.gl.ceir.flowManager.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "grey_list")
public class TrackedList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "imei")
    private String imei;

    @Column(name = "actual_imei")
    private String actualImei;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "imsi")
    private String imsi;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "created_on")
    private LocalDateTime created_on;
}
