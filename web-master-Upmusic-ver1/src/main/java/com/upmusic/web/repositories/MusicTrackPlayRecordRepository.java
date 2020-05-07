package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicTrackPlayRecord;


@Repository
public interface MusicTrackPlayRecordRepository extends JpaRepository<MusicTrackPlayRecord, Long> {
	
	List<MusicTrackPlayRecord> findByMusicTrackId(Long musicTrackId);
	
	MusicTrackPlayRecord findByMusicTrackIdAndMemberId(Long musicTrackId, Long memberId);

}
