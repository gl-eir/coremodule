package com.gl.ceir.flowManager.entity;

import com.gl.ceir.flowManager.contstants.DeviceType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "allowed_device_type")
public class AllowedDeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sno;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "created_on")
    private LocalDateTime created_on;

}
