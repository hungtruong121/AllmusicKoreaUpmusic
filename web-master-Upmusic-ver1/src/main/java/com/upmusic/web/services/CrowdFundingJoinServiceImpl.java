package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.CrowdFundingJoin;
import com.upmusic.web.repositories.CrowdFundingJoinRepository;

@Service
public class CrowdFundingJoinServiceImpl implements CrowdFundingJoinService{

	@Autowired
	private CrowdFundingJoinRepository crowdFundingJoinRepository;


	@Override
	public CrowdFundingJoin save(CrowdFundingJoin crowdFundingJoin) {
		return crowdFundingJoinRepository.save(crowdFundingJoin);
	}


	@Override
	public int countByCreatedAt() {
		return crowdFundingJoinRepository.countByCreatedAt();
	}


	@Override
	public Page<CrowdFundingJoin> findByMemberId(Long id, Pageable pageable) {
		return crowdFundingJoinRepository.findByMemberId(id, pageable);
	}


	@Override
	public List<CrowdFundingJoin> findByCrowdFundingId(Long crowdFundingId) {
		return crowdFundingJoinRepository.findByCrowdFundingId(crowdFundingId);
	}


	@Override
	public int findByCrowdFundingIdAndMemberId(CrowdFundingJoin crowdFundingJoin) {
		return crowdFundingJoinRepository.findByCrowdFundingIdAndMemberId(crowdFundingJoin);
	}


	@Override
	public List<CrowdFundingJoin> findByMemberId(Long id) {
		return crowdFundingJoinRepository.findByMemberId(id);
	}


	@Override
	public CrowdFundingJoin findByCrowdFundingJoinId(String crowdFundingJoinId) {
		return crowdFundingJoinRepository.findByCrowdFundingJoinId(crowdFundingJoinId);
	}



}
