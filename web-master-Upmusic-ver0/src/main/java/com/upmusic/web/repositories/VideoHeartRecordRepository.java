package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.VideoHeartRecord;


@Repository
public interface VideoHeartRecordRepository extends JpaRepository<VideoHeartRecord, Long> {
	
	VideoHeartRecord findByVideoIdAndMemberId(Long videoId, Long memberId);
	
	List<VideoHeartRecord> findByVideoId(Long videoId);
}
