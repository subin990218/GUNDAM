package com.mobilesuit.mainserver.openaitest.entity.Document;

import com.mobilesuit.mainserver.openaitest.dto.documentation.DocInfo;
import com.mobilesuit.mainserver.openaitest.dto.documentation.RecentDocDto;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocRepository extends JpaRepository<Doc, Integer> {


//
//@Query(value = "SELECT * FROM type_set where type_set.doc_id = :docId",nativeQuery = true)
//List<TypeSet> getbyDocId(@Param("docId")int docId);
    @Query(value = "SELECT * FROM doc WHERE doc.user_id = :userId and doc.repo_name= :repoName",nativeQuery = true)
    List<Doc> findByUserIdRepoName(@Param("userId") String userId, @Param("repoName") String repoName);
    @Query(value = "SELECT DISTINCT doc.repo_name FROM doc WHERE doc.user_id = :userId",nativeQuery = true)
    List<String> findByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM doc WHERE doc.user_id = :userId and doc.repo_name = :repoName and doc.created_date= :todaydate",nativeQuery = true)
    List<Doc> getRecentDoc(@Param("userId") String userId, @Param("repoName") String repoName, @Param("todaydate") LocalDate todaydate );

}
