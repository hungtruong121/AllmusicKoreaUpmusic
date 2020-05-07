package com.upmusic.web.services;

import com.upmusic.web.domain.Reward;


public interface RewardService {

	Iterable<Reward> listAllRewards();
	
	Reward findById(int id);
}
