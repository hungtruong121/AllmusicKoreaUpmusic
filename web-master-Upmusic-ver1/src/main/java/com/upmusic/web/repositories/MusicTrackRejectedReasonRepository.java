package com.upmusic.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicTrackRejectedReason;


@Repository
public interface MusicTrackRejectedReasonRepository extends JpaRepository<MusicTrackRejectedReason, Long> {
	
	MusicTrackRejectedReason findByMusicTrackId(Long musicTrackId);
	
}
