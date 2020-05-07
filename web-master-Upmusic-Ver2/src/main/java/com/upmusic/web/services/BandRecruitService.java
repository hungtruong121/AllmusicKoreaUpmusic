package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.BandRecruit;

public interface BandRecruitService {
	public Page<BandRecruit> findBandRecruitList(Pageable pageable);
	public BandRecruit findByBandRecruitId(Long bandRecruitId);
	public BandRecruit insertBandRecruit(BandRecruit bandRecruit);
	public void deleteBandRecruit(BandRecruit bandRecruit);
	public void deleteBandRecruitByMemberId(Long memberId);
	public List<BandRecruit> findBandRecruitList();
}
