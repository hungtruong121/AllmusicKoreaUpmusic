package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.domain.CrowdFundingReward;

public interface CrowdFundingRewardService {
	public CrowdFundingReward save(CrowdFundingReward crowdFundingReward);
	public List<CrowdFundingReward> findByCrowdFundingId(Long crowdFundingId);
	public void deleteByCrowdFundingRewardId(Long crowdFundingRewardId);
	CrowdFundingReward findByCrowdFundingRewardId(Long crowdFundingRewardId);
}
