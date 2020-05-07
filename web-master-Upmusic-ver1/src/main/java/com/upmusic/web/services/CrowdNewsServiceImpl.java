package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.CrowdNews;
import com.upmusic.web.repositories.CrowdNewsRepository;

@Service
public class CrowdNewsServiceImpl implements CrowdNewsService{

	@Autowired
	private CrowdNewsRepository crowdNewsRepository;
	
	
	@Override
	public Page<CrowdNews> findAll(Pageable pageable) {
		return crowdNewsRepository.findAll(pageable);
	}

	@Override
	public CrowdNews findByCrowdNewsId(Long crowdNewsId) {
		return crowdNewsRepository.findByCrowdNewsId(crowdNewsId);
	}

	@Override
	public CrowdNews save(CrowdNews crowdNews) {
		return crowdNewsRepository.save(crowdNews);
	}

	@Override
	public void deleteByCrowdNewsId(Long crowdNewsId) {
		crowdNewsRepository.deleteById(crowdNewsId);
	}

	@Override
	public List<CrowdNews> findByNewCrowdNews() {
		return crowdNewsRepository.findByNewCrowdNews();
	}
	
}
