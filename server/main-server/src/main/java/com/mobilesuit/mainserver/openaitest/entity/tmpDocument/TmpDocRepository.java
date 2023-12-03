package com.mobilesuit.mainserver.openaitest.entity.tmpDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TmpDocRepository extends JpaRepository<TmpDoc, Integer> {


}
