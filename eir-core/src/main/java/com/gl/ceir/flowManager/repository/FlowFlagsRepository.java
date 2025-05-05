package com.gl.ceir.flowManager.repository;

import com.gl.ceir.flowManager.contstants.ConfigFlag;
import com.gl.ceir.flowManager.entity.ConfigFlagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlowFlagsRepository extends JpaRepository<ConfigFlagEntity, Integer> {

    List<ConfigFlagEntity> findByModule(String module);
}
