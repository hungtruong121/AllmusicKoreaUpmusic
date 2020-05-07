package com.upmusic.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.CrowdFundingBanner;

public interface CrowdFundingBannerService {
	public Page<CrowdFundingBanner> findAll(Pageable pageable);
	public Page<CrowdFundingBanner> findByCrowdFundingBannerShowYn(Pageable pageable);
	public CrowdFundingBanner save(CrowdFundingBanner crowdFundingBanner);
	public CrowdFundingBanner findByCrowdFundingBannerId(Long crowdFundingBannerId);
	public void deleteByCrowdFundingBannerId(Long crowdFundingBannerId);
}
