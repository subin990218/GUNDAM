package com.mobilesuit.mainserver.openaitest.entity.Document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MethodRepository extends JpaRepository<Method, Integer> {

    @Query(value = "SELECT * FROM method where method.doc_id = :docId",nativeQuery = true)
    List<Method> getbyDocId(@Param("docId")int docId);

}
