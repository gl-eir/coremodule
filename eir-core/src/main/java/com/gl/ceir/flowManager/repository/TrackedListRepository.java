package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.BlackList;
import com.gl.ceir.flowManager.entity.ExceptionList;
import com.gl.ceir.flowManager.entity.TrackedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackedListRepository extends JpaRepository<TrackedList, Integer> {

    //    @Query("Select b from TrackedList b where b.imei = :imei and b.imsi = :imsi and b.msisdn = :msisdn")
//    Optional<TrackedList> getByImeiAndImsiAndMsisnd(@Param("imei") String imei, @Param("imsi") String imsi, @Param("msisdn") String msisdn);
    Optional<TrackedList> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);
}
