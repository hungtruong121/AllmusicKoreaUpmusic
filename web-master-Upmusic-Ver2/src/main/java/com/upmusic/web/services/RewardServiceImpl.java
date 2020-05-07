package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Reward;
import com.upmusic.web.repositories.RewardRepository;


@Service
public class RewardServiceImpl implements RewardService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private RewardRepository rewardRepository;
	
	
	@Override
	public Iterable<Reward> listAllRewards() {
		logger.debug("listAllRewards called");
        return rewardRepository.findAll();
	}

	@Override
	public Reward findById(int id) {
		logger.debug("findById called");
        return rewardRepository.findById(id).orElse(null);
	}

}
