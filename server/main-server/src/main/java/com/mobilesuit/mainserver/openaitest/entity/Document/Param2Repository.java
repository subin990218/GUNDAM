package com.mobilesuit.mainserver.openaitest.entity.Document;

import com.mobilesuit.mainserver.openaitest.dto.documentation.DocInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Param2Repository extends JpaRepository<Param2, Integer> {



    @Query(value = "SELECT * FROM param2 where param2.doc_id = :docId",nativeQuery = true)
    List<Param2> getbyDocId(@Param("docId")int docId);
}
