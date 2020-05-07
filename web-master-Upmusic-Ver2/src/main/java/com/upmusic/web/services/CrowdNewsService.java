package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.CrowdNews;

public interface CrowdNewsService {
	public Page<CrowdNews> findAll(Pageable pageable);
	CrowdNews findByCrowdNewsId(Long crowdNewsId);
	public CrowdNews save(CrowdNews crowdNews);
	public void deleteByCrowdNewsId(Long crowdNewsId);
	
	List<CrowdNews> findByNewCrowdNews();
}
