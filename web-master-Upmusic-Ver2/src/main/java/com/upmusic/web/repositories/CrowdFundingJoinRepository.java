package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.CrowdFunding;
import com.upmusic.web.domain.CrowdFundingJoin;

@Repository
public interface CrowdFundingJoinRepository extends JpaRepository<CrowdFundingJoin, Long>{

	@Query("SELECT COUNT(*) FROM CrowdFundingJoin CFJ WHERE DATE_FORMAT(CFJ.createdAt, '%y-%m-%d') = DATE_FORMAT(NOW(), '%y-%m-%d')")
	int countByCreatedAt();

	@Query("SELECT CFJ FROM CrowdFundingJoin CFJ WHERE CFJ.member.id = :id")
	Page<CrowdFundingJoin> findByMemberId(@Param("id") Long id, Pageable pageable);

	@Query("SELECT CFJ FROM CrowdFundingJoin CFJ WHERE CFJ.crowdFunding.crowdFundingId = :crowdFundingId")
	List<CrowdFundingJoin> findByCrowdFundingId(@Param("crowdFundingId") Long crowdFundingId);

	@Query("SELECT COUNT(*) FROM CrowdFundingJoin CFJ WHERE CFJ.crowdFunding.crowdFundingId = :#{#crowdFundingJoin.crowdFunding.crowdFundingId} AND CFJ.member.id = :#{#crowdFundingJoin.member.id}")
	int findByCrowdFundingIdAndMemberId(@Param("crowdFundingJoin") CrowdFundingJoin crowdFundingJoin);
	
	@Query("SELECT CFJ FROM CrowdFundingJoin CFJ WHERE CFJ.member.id = :id ORDER BY CFJ.createdAt DESC")
	List<CrowdFundingJoin> findByMemberId(@Param("id") Long id);
	
	@Query("SELECT CFJ FROM CrowdFundingJoin CFJ WHERE CFJ.crowdFundingJoinId = :crowdFundingJoinId")
	CrowdFundingJoin findByCrowdFundingJoinId(@Param("crowdFundingJoinId") String crowdFundingJoinId);

}
