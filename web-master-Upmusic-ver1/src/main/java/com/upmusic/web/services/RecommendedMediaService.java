package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.Video;


public interface RecommendedMediaService {
	
	List<Video> getRecommendedVideo();
	
	List<MusicTrack> getRecommendedMusicTrack(Long memberId);
	
	boolean recommendedVideo(Long id);
	
	boolean recommendedTrack(Long id);
    
	boolean recommendVideo(Long id);
    
	boolean recommendTrack(Long id);
	
}
