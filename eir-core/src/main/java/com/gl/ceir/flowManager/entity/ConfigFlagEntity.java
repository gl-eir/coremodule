package com.gl.ceir.flowManager.entity;

import com.gl.ceir.flowManager.contstants.ConfigFlag;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
@Table(name = "sys_param")
public class ConfigFlagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tag")
    @Enumerated(EnumType.STRING)
    private ConfigFlag name;

    @Column(name = "value")
    private String value;

    @Column(name = "feature_name")
    private String module;
}
