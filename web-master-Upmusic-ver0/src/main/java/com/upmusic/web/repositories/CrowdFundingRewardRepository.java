package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.CrowdFundingReward;

@Repository
public interface CrowdFundingRewardRepository extends JpaRepository<CrowdFundingReward, Long>{
	
	@Query("SELECT CFR FROM CrowdFundingReward CFR WHERE CFR.crowdFunding.crowdFundingId = :crowdFundingId")
	List<CrowdFundingReward> findByCrowdFundingId(@Param("crowdFundingId") Long crowdFundingId);
	
	@Query("SELECT CFR FROM CrowdFundingReward CFR WHERE CFR.crowdFundingRewardId = :crowdFundingRewardId")
	CrowdFundingReward findByCrowdFundingRewardId(@Param("crowdFundingRewardId") Long crowdFundingRewardId);
	
}
