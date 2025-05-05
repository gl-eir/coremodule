package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.AllowedTAC;
import com.gl.ceir.flowManager.entity.BlackList;
import com.gl.ceir.flowManager.entity.DeviceTypeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceTypeListRepository extends JpaRepository<DeviceTypeList, Integer> {


    @Query("Select b from DeviceTypeList b where b.tac = :tac")
    Optional<DeviceTypeList> getByTac(@Param("tac") String tac);

}
