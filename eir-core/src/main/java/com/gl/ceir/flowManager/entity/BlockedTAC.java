package com.gl.ceir.flowManager.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "blocked_tac")
public class BlockedTAC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tac")
    private String tac;

    @Column(name = "created_on")
    private LocalDateTime created_on;

    @Column(name = "request_date")
    private LocalDateTime requestDate;
}
