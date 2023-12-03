package com.mobilesuit.mainserver.openaitest.entity.Document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParamRepository extends JpaRepository<Param, Integer> {
    @Query(value = "SELECT * FROM param where param.method_id = :methodId",nativeQuery = true)
    List<Param> getbyMehtodId(@org.springframework.data.repository.query.Param("methodId")int methodId);
}
