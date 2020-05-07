package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.PointRewardCondition;
import com.upmusic.web.repositories.PointRewardConditionRepository;


@Service
public class PointRewardConditionServiceImpl implements PointRewardConditionService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private PointRewardConditionRepository conditionRepository;
	
	
	@Override
	public PointRewardCondition getCondition() {
		logger.debug("getCondition called");
        return conditionRepository.findById(1).orElse( new PointRewardCondition());
	}
	
	@Override
	public PointRewardCondition saveCondition(PointRewardCondition condition) {
		logger.debug("saveCondition called");
        return conditionRepository.save(condition);
	}

}
