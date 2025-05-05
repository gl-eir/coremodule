package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.AllowedDeviceType;
import com.gl.ceir.flowManager.entity.AllowedTAC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllowedDeviceTypeRepository extends JpaRepository<AllowedDeviceType, Integer> {

}
