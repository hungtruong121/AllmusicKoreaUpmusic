package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.CrowdFundingReward;
import com.upmusic.web.repositories.CrowdFundingRewardRepository;

@Service
public class CrowdFundingRewardServiceImpl implements CrowdFundingRewardService{

	@Autowired
	private CrowdFundingRewardRepository crowdFundingRewardRepository;
	
	
	@Override
	public CrowdFundingReward save(CrowdFundingReward crowdFundingReward) {
		return crowdFundingRewardRepository.save(crowdFundingReward);
	}


	@Override
	public List<CrowdFundingReward> findByCrowdFundingId(Long crowdFundingId) {
		return crowdFundingRewardRepository.findByCrowdFundingId(crowdFundingId);
	}


	@Override
	public void deleteByCrowdFundingRewardId(Long crowdFundingRewardId) {
		crowdFundingRewardRepository.deleteById(crowdFundingRewardId);
	}


	@Override
	public CrowdFundingReward findByCrowdFundingRewardId(Long crowdFundingRewardId) {
		return crowdFundingRewardRepository.findByCrowdFundingRewardId(crowdFundingRewardId);
	}

}
