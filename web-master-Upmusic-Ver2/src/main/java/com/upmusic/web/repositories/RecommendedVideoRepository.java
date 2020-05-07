package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.RecommendedVideo;


@Repository
public interface RecommendedVideoRepository extends JpaRepository<RecommendedVideo, Integer> {
	
	public List<RecommendedVideo> findTop3ByOrderByCreatedAtDesc();

	public RecommendedVideo findByVideoId(Long id);
	
}
