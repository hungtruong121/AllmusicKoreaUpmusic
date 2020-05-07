package com.upmusic.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.CrowdFundingBanner;
import com.upmusic.web.repositories.CrowdFundingBannerRepository;

@Service
public class CrowdFundingBannerServiceImpl implements CrowdFundingBannerService{

	@Autowired
	private CrowdFundingBannerRepository crowdFundingBannerRepository;
	
	
	@Override
	public Page<CrowdFundingBanner> findAll(Pageable pageable) {
		return crowdFundingBannerRepository.findAll(pageable);
	}


	@Override
	public CrowdFundingBanner save(CrowdFundingBanner crowdFundingBanner) {
		return crowdFundingBannerRepository.save(crowdFundingBanner);
	}


	@Override
	public CrowdFundingBanner findByCrowdFundingBannerId(Long crowdFundingBannerId) {
		return crowdFundingBannerRepository.findByCrowdFundingBannerId(crowdFundingBannerId);
	}

	@Override
	public void deleteByCrowdFundingBannerId(Long crowdFundingBannerId) {
		crowdFundingBannerRepository.deleteById(crowdFundingBannerId);
	}


	@Override
	public Page<CrowdFundingBanner> findByCrowdFundingBannerShowYn(Pageable pageable) {
		return crowdFundingBannerRepository.findByCrowdFundingBannerShowYn(pageable);
	}

}
