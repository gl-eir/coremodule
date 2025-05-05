package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.ExceptionList;
import com.gl.ceir.flowManager.entity.HlrData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HlrDataRepository extends JpaRepository<HlrData, Integer> {
    @Query("Select b from HlrData b where b.imsi = :imsi and b.msisdn = :msisdn")
    Optional<HlrData> getByImsiAndMsisnd(@Param("imsi") String imsi, @Param("msisdn") String msisdn);
}
