package com.upmusic.web.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.RecommendedMusicTrack;
import com.upmusic.web.domain.RecommendedVideo;
import com.upmusic.web.domain.Video;
import com.upmusic.web.repositories.MusicTrackHeartRecordRepository;
import com.upmusic.web.repositories.MusicTrackRepository;
import com.upmusic.web.repositories.RecommendedMusicTrackRepository;
import com.upmusic.web.repositories.RecommendedVideoRepository;
import com.upmusic.web.repositories.VideoRepository;


@Service
public class RecommendedMediaServiceImpl implements RecommendedMediaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private RecommendedVideoRepository recommendedVideoRepository;
	
	@Autowired
	private RecommendedMusicTrackRepository recommendedMusicTrackRepository;
	
	@Autowired
	private MusicTrackHeartRecordRepository trackHeartRecordRepository;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private MusicTrackRepository trackRepository;
	
    
    @Override
    public List<Video> getRecommendedVideo() {
    	logger.debug("getRecommendedVideo called");
    	List<Video> videos = new ArrayList<Video>();
    	List<RecommendedVideo> recommendedVideos = recommendedVideoRepository.findTop3ByOrderByCreatedAtDesc();
    	for (RecommendedVideo recommendedVideo : recommendedVideos) {
    		videos.add(recommendedVideo.getVideo());
    	}
    	return videos;
    }

	@Override
	public List<MusicTrack> getRecommendedMusicTrack(Long memberId) {
		logger.debug("getRecommendedMusicTrack called");
		List<MusicTrack> tracks = new ArrayList<MusicTrack>();
		List<RecommendedMusicTrack> recommendedTracks = recommendedMusicTrackRepository.findTop3ByOrderByCreatedAtDesc();
		for (RecommendedMusicTrack recommendedTrack : recommendedTracks) {
    		MusicTrack track = recommendedTrack.getMusicTrack();
    		if (0 < memberId) track.setLiked(trackHeartRecordRepository.findByMusicTrackIdAndMemberId(track.getId(), memberId) != null);
    		tracks.add(track);
    	}
		return tracks;
	}
	
	@Override
	public boolean recommendedVideo(Long id) {
		RecommendedVideo video = recommendedVideoRepository.findByVideoId(id);
		return video != null;
	}
	
	@Override
	public boolean recommendedTrack(Long id) {
		RecommendedMusicTrack track = recommendedMusicTrackRepository.findByMusicTrackId(id);
		return track != null;
		
	}
    
	@Override
	public boolean recommendVideo(Long id) {
		RecommendedVideo recommendedVideo = recommendedVideoRepository.findByVideoId(id);
		if (recommendedVideo != null) {
			recommendedVideoRepository.delete(recommendedVideo);
		} else {
			Video video = videoRepository.findById(id).orElse(null);
			if (video != null) {
				recommendedVideo = new RecommendedVideo();
				recommendedVideo.setVideo(video);
				recommendedVideoRepository.save(recommendedVideo);
				return true;
			}
		}
		return false;
	}
    
	@Override
	public boolean recommendTrack(Long id) {
		RecommendedMusicTrack recommendedTrack = recommendedMusicTrackRepository.findByMusicTrackId(id);
		if (recommendedTrack != null) {
			recommendedMusicTrackRepository.delete(recommendedTrack);
		} else {
			MusicTrack track = trackRepository.findById(id).orElse(null);
			if (track != null) {
				recommendedTrack = new RecommendedMusicTrack();
				recommendedTrack.setMusicTrack(track);
				recommendedMusicTrackRepository.save(recommendedTrack);
				return true;
			}
		}
		return false;
	}
    
}
