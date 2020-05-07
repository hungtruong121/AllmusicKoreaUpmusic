package com.upmusic.web.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.GuideVocalScope;


@Repository
public interface GuideVocalScopeRepository extends JpaRepository<GuideVocalScope, Integer> {

}
