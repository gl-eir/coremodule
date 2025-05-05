package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Integer> {

//    @Query("Select b from BlackList b where (b.imei IS NULL OR b.imei = :imei) and (b.imsi IS NULL OR b.imsi = :imsi) and (b.msisdn IS NULL OR b.msisdn = :msisdn)")
//    Optional<BlackList> getByImeiAndImsiAndMsisnd(@Param("imei") String imei, @Param("imsi") String imsi, @Param("msisdn") String msisdn);

    Optional<BlackList> findByImeiAndImsiAndMsisdn(String imei, String imsi, String msisdn);
}
