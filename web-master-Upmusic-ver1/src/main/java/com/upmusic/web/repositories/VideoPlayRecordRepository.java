package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.VideoPlayRecord;


@Repository
public interface VideoPlayRecordRepository extends JpaRepository<VideoPlayRecord, Long> {
	
	VideoPlayRecord findByVideoIdAndMemberId(Long videoId, Long memberId);

	List<VideoPlayRecord> findByVideoId(Long id);
	
}
