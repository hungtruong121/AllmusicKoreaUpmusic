package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.RecommendedMusicTrack;


@Repository
public interface RecommendedMusicTrackRepository extends JpaRepository<RecommendedMusicTrack, Integer> {
	
	public List<RecommendedMusicTrack> findTop3ByOrderByCreatedAtDesc();

	public RecommendedMusicTrack findByMusicTrackId(Long id);
	
}
