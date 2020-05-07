package com.upmusic.web.repositories;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;
import com.upmusic.web.domain.GuideVocal;
import com.upmusic.web.domain.Member;


@Repository
public interface GuideVocalRepository extends JpaRepository<GuideVocal, Long> {

	GuideVocal findOneByMember(Member member);

	/*
	 * 관리자용
	 */

	Page<GuideVocal> findByGuideStatus(MusicTrackStatus beforeExam, Pageable pageOrderRequest);

	@Query(value = "SELECT * FROM guide_vocal WHERE guide_status = 1 or guide_status = 2", nativeQuery = true)
	Page<GuideVocal> findByGuideStatusIsUnderExamOrBeforeExam(Pageable pageOrderRequest);
}
