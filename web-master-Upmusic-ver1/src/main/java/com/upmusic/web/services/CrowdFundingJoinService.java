package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.upmusic.web.domain.CrowdFundingJoin;

public interface CrowdFundingJoinService {
	public CrowdFundingJoin save(CrowdFundingJoin crowdFundingJoin);
	public int countByCreatedAt();
	public Page<CrowdFundingJoin> findByMemberId(Long id, Pageable pageable);
	public List<CrowdFundingJoin> findByCrowdFundingId(Long crowdFundingId);
	public int findByCrowdFundingIdAndMemberId(CrowdFundingJoin crowdFundingJoin);
	public List<CrowdFundingJoin> findByMemberId(Long id);
	public CrowdFundingJoin findByCrowdFundingJoinId(@Param("crowdFundingJoinId") String crowdFundingJoinId);
}
