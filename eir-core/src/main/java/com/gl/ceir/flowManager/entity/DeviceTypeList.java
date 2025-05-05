package com.gl.ceir.flowManager.entity;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "device_type")
public class DeviceTypeList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tac")
    private String tac;

    @Column(name = "device_type")
    private String deviceType;

}
