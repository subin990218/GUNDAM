package com.mobilesuit.mainserver.openaitest.entity.Document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeSetRepository extends JpaRepository<TypeSet, Integer> {
    @Query(value = "SELECT * FROM type_set where type_set.doc_id = :docId",nativeQuery = true)
    List<TypeSet> getbyDocId(@Param("docId")int docId);
}
