package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.CrowdNews;

@Repository
public interface CrowdNewsRepository extends JpaRepository<CrowdNews, Long>{
	
	@Query("SELECT CN FROM CrowdNews CN WHERE CN.crowdNewsId = :crowdNewsId")
	CrowdNews findByCrowdNewsId(@Param("crowdNewsId") Long crowdNewsId);
	
	@Query("SELECT CN FROM CrowdNews CN ORDER BY CN.createdAt DESC")
	List<CrowdNews> findByNewCrowdNews();
	
}
