package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.BlackList;
import com.gl.ceir.flowManager.entity.ExceptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExceptionListRepository extends JpaRepository<ExceptionList, Integer> {
//    @Query("Select b from ExceptionList b where b.imei = :imei and b.imsi = :imsi and b.msisdn = :msisdn")
//    Optional<ExceptionList> getByImeiAndImsiAndMsisnd(@Param("imei") String imei, @Param("imsi") String imsi, @Param("msisdn") String msisdn);

    Optional<ExceptionList> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);
}
