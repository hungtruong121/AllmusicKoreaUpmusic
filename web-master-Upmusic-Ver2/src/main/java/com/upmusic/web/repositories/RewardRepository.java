package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Reward;


@Repository
public interface RewardRepository extends CrudRepository<Reward, Integer> {
	
}
