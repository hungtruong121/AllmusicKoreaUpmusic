package com.upmusic.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.GuideVocal;
import com.upmusic.web.domain.Member;


public interface GuideVocalService {
	
	Iterable<GuideVocal> listAllGuideVocals();
	
	Page<GuideVocal> findAll(PageRequest pageOrderByNew);
	
	Page<GuideVocal> findByStatusIsUnderExam(PageRequest pageOrderNew);
	
	GuideVocal getGuideVocalById(Long id);
	
	GuideVocal findOneByMember(Member member);

	GuideVocal saveGuideVocal(GuideVocal album);

	void deleteGuideVocal(Long id);
	
	//
	// 관리자 영역
	//
	
	GuideVocal setGuideStatus(Long id, MusicTrackStatus status);
    
}
