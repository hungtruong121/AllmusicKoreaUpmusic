package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.PointRewardCondition;


@Repository
public interface PointRewardConditionRepository extends CrudRepository<PointRewardCondition, Integer> {
	
}
