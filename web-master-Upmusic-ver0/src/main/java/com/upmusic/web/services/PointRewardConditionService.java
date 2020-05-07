package com.upmusic.web.services;

import com.upmusic.web.domain.PointRewardCondition;


public interface PointRewardConditionService {

	PointRewardCondition getCondition();
	
	PointRewardCondition saveCondition(PointRewardCondition condition);
}
