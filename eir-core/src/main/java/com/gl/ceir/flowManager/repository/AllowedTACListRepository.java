package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.entity.AllowedTAC;
import com.gl.ceir.flowManager.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllowedTACListRepository extends JpaRepository<AllowedTAC, Integer> {

    @Query("Select b from AllowedTAC b where b.tac = :tac")
    Optional<AllowedTAC> getByTac(@Param("tac") String tac);
    //BlackList getBymsisdn(String msisdn);

    //BlackList getBymsisdnAndimei(String msisdn);


    //@Query("SELECT a FROM BlackList a WHERE a.msisdn = ?1")
    //BlackList getRecordUsingMsisdn(String msisdn);

//    @Query(value = "SELECT a FROM black_list a WHERE MSISDN = ?1", nativeQuery = true)
 //   BlackList getRecordUsingMsisdnNative(String msisdn);
}
