package com.gl.ceir.flowManager.repository;


import com.gl.ceir.flowManager.entity.BlockedTAC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedTACListRepository extends JpaRepository<BlockedTAC, Integer> {
    @Query("Select b from BlockedTAC b where b.tac = :tac")
    Optional<BlockedTAC> getByTac(@Param("tac") String tac);
    //BlackList getBymsisdn(String msisdn);

    //BlackList getBymsisdnAndimei(String msisdn);


    //@Query("SELECT a FROM BlackList a WHERE a.msisdn = ?1")
    //BlackList getRecordUsingMsisdn(String msisdn);

//    @Query(value = "SELECT a FROM black_list a WHERE MSISDN = ?1", nativeQuery = true)
 //   BlackList getRecordUsingMsisdnNative(String msisdn);
}
