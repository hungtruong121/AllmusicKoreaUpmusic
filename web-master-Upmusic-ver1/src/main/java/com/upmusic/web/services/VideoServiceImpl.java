package com.upmusic.web.services;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.upmusic.web.config.UPMusicConstants.VideoType;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.RecommendedVideo;
import com.upmusic.web.domain.Video;
import com.upmusic.web.domain.VideoComment;
import com.upmusic.web.domain.VideoHeartRecord;
import com.upmusic.web.domain.VideoPlayRecord;
import com.upmusic.web.repositories.MemberRepository;
import com.upmusic.web.repositories.RecommendedVideoRepository;
import com.upmusic.web.repositories.VideoCommentRepository;
import com.upmusic.web.repositories.VideoHeartRecordRepository;
import com.upmusic.web.repositories.VideoPlayRecordRepository;
import com.upmusic.web.repositories.VideoRepository;


@Service
public class VideoServiceImpl implements VideoService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private VideoHeartRecordRepository videoHeartRecordRepository;
	
	@Autowired
	private VideoPlayRecordRepository videoPlayRecordRepository;
    
	@Autowired
	private VideoCommentRepository videoCommentRepository;
	
	@Autowired
	private RecommendedVideoRepository recommendedVideoRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	

    @Override
    public Iterable<Video> listAllVideos() {
        logger.debug("listAllVideos called");
        return videoRepository.findAll();
    }
    
    @Override
    public Video getTop1() {
    	logger.debug("getTop1 called");
        return videoRepository.findFirstByOrderByHotPointDesc();
    }
    
    @Override
    public Iterable<Video> getTop3() {
    	logger.debug("getTop3 called");
        return videoRepository.findTop3ByOrderByHotPointDesc();
    }
    
    @Override
    public Iterable<Video> getTop4() {
    	logger.debug("getTop4 called");
        return videoRepository.findTop4ByOrderByHotPointDesc();
    }
    
    @Override
    public Iterable<Video> getTop5() {
    	logger.debug("getTop5 called");
        return videoRepository.findTop5ByOrderByHotPointDesc();
    }
    
    @Override
    public Page<Video> findTop100(PageRequest pageOrderByHot) {
    	logger.debug("findTop100 called");
    	//return videoRepository.findByOrderByHotPointDesc(pageOrderByHot);
    	// 일반영상에서만 선출
    	return videoRepository.findByVideoTypeOrderByHotPointDesc(VideoType.GV, pageOrderByHot);
    }
    
    @Override
    public List<Video> findTop5MV() {
    	return videoRepository.findTop5ByVideoTypeOrderByHotPointDesc(VideoType.MV);
    }
    
    @Override
    public Page<Video> findAllMV(PageRequest pageOrderByHot) {
    	return videoRepository.findAllByVideoType(VideoType.MV, pageOrderByHot);
    }
    
    @Override
    public List<Video> findTop3GV() {
    	return videoRepository.findTop3ByVideoTypeOrderByHotPointDesc(VideoType.GV);
    }
	
    @Override
    public List<Video> findTop4GV() {
    	return videoRepository.findTop4ByVideoTypeOrderByHotPointDesc(VideoType.GV);
    }
    
    @Override
    public List<Video> findTop5GV() {
    	return videoRepository.findTop5ByVideoTypeOrderByHotPointDesc(VideoType.GV);
    }
    
    @Override
    public Page<Video> findAllGV(PageRequest pageOrderByHot) {
    	return videoRepository.findAllByVideoType(VideoType.GV, pageOrderByHot);
    }

    @Override
    public Video getVideoById(Long id) {
        logger.debug("getVideoById called");
        return videoRepository.findById(id).orElse(null);
    }

    @Override
    public Video saveVideo(Video video) {
        logger.debug("saveVideo called");
        return videoRepository.save(video);
    }
    
    @Override
    public void deleteVideo(Long id, Long memberId) {
    	logger.debug("deleteVideo called");
    	Video video = getVideoById(id);
        if (video != null && 0 == (video.getMember().getId().compareTo(memberId))) {
        	// 좋아요 삭제
        	List<VideoHeartRecord> records = videoHeartRecordRepository.findByVideoId(id);
            for (VideoHeartRecord record : records) {
            	videoHeartRecordRepository.delete(record);
            }
            // 플레이 리스트 삭제
            List<VideoPlayRecord>  playRecords = videoPlayRecordRepository.findByVideoId(id);
            for (VideoPlayRecord record : playRecords) {
            	videoPlayRecordRepository.delete(record);
            }
            // 댓글 삭제
            List<VideoComment> comments = videoCommentRepository.findByVideoId(id);
            for (VideoComment comment : comments) {
            	videoCommentRepository.delete(comment);
            }
            // 추천영상 삭제
            RecommendedVideo recommendedVideo = recommendedVideoRepository.findByVideoId(id);
            if (recommendedVideo != null) {
            	recommendedVideoRepository.delete(recommendedVideo);
            }
            
            // 회원의 비디오수 감소
        	Member member = video.getMember();
            if (0 < member.getVideoCnt()) member.setVideoCnt(member.getVideoCnt() - 1);
            memberRepository.save(member);
            
            videoRepository.deleteById(id);
        }
    }

    @Override
    public void deleteVideo(Long id) {
        logger.debug("deleteVideo called");
        Video video = getVideoById(id);
        if (video != null) {
        	// 좋아요 삭제
        	List<VideoHeartRecord> records = videoHeartRecordRepository.findByVideoId(id);
            for (VideoHeartRecord record : records) {
            	videoHeartRecordRepository.delete(record);
            }
            // 플레이 리스트 삭제
            List<VideoPlayRecord>  playRecords = videoPlayRecordRepository.findByVideoId(id);
            for (VideoPlayRecord record : playRecords) {
            	videoPlayRecordRepository.delete(record);
            }
            // 댓글 삭제
            List<VideoComment> comments = videoCommentRepository.findByVideoId(id);
            for (VideoComment comment : comments) {
            	videoCommentRepository.delete(comment);
            }
            // 추천영상 삭제
            RecommendedVideo recommendedVideo = recommendedVideoRepository.findByVideoId(id);
            if (recommendedVideo != null) {
            	recommendedVideoRepository.delete(recommendedVideo);
            }
            
            // 회원의 비디오수 감소
        	Member member = video.getMember();
            if (0 < member.getVideoCnt()) member.setVideoCnt(member.getVideoCnt() - 1);
            memberRepository.save(member);
            
            videoRepository.deleteById(id);
        }
    }
    
    /**
     * 조회수 추가
     */
    @Override
    public int increaseHitCnt(Long id) {
    	logger.debug("increaseHitCnt called");
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
        	video.setHitCnt(video.getHitCnt() + 1);
        	video = videoRepository.save(video);
        	return video.getHitCnt();
        }
        return 0;
    }
    
    /**
     * 해당 비디오와 회원의 좋아요 여부
     */
    @Override
    public boolean likedVideo(Long id, Long memberId) {
    	logger.debug("likedVideo called : id is {} and member is {}", id, memberId);
    	VideoHeartRecord record = videoHeartRecordRepository.findByVideoIdAndMemberId(id, memberId);
    	return record != null;
    }
    
    /**
     * 좋아요 추가
     */
    @Override
    public int increaseHeartCnt(Long id, Member member) {
    	logger.debug("increaseHeartCnt called");
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
        	VideoHeartRecord record = new VideoHeartRecord();
        	record.setVideo(video);
    		record.setMember(member);
        	videoHeartRecordRepository.save(record);
        	video.setHeartCnt(video.getHeartCnt() + 1);
        	video = videoRepository.save(video);
        	return video.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 좋아요 취소
     */
    @Override
    public int decreaseHeartCnt(Long id, Member member) {
    	logger.debug("decreaseHeartCnt called");
        Video video = videoRepository.findById(id).orElse(null);
        if (video != null) {
        	VideoHeartRecord record = videoHeartRecordRepository.findByVideoIdAndMemberId(id, member.getId());
        	if (record != null) {
        		videoHeartRecordRepository.delete(record);
        		if (0 < video.getHeartCnt()) {
            		video.setHeartCnt(video.getHeartCnt() - 1);
            		video = videoRepository.save(video);
            	}
        	} else {
        		// something wrong
        	}
        	return video.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 댓글 목록 반환
     */
    @Override
    public Page<VideoComment> getComments(Long videoId, PageRequest pageOrderRequest) {
    	logger.debug("getComments called");
    	return videoCommentRepository.findByVideoId(videoId, pageOrderRequest);
    }
    
    /**
     * 댓글 반환
     */
    @Override
    public VideoComment getCommentById(Long commentId) {
    	logger.debug("getCommentById called");
    	return videoCommentRepository.findById(commentId).orElse(null);
    }
    @Override
    public VideoComment getCommentById(Long videoId, Long commentId) {
    	logger.debug("getCommentById called");
    	return videoCommentRepository.findById(commentId).orElse(null);
    }
    
    /**
     * 댓글 추가
     */
    @Override
    public VideoComment addComment(Long videoId, Member member, String content, String ip) {
    	logger.debug("addComment called");
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video != null) {
    		VideoComment comment = new VideoComment();
        	comment.setIp(ip);
        	comment.setMember(member);
        	comment.setContent(content);
        	comment.setVideo(video);
        	comment = videoCommentRepository.save(comment);
        	return comment;
        }
        return null;
    }
    
    /**
     * 댓글 추가
     */
    @Override
    public VideoComment updateComment(Long videoId, Long commentId, Member member, String content, String ip) {
    	logger.debug("updateComment called");
    	Video video = videoRepository.findById(videoId).orElse(null);
        if (video != null) {
        	VideoComment comment = videoCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(member.getId()))) {
    			comment.setIp(ip);
            	comment.setMember(member);
            	comment.setContent(content);
            	comment.setVideo(video);
    			comment = videoCommentRepository.save(comment);
    			return comment;
    		}
        }
        return null;
    }
    
    /**
     * 댓글 삭제
     */
    @Override
    public Long deleteComment(Long videoId, Long commentId, Long memberId) {
    	logger.debug("deleteComment called");
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video != null) {
    		VideoComment comment = videoCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(memberId))) {
    			videoCommentRepository.delete(comment);
    		}
        	return commentId;
        }
        return null;
    }
    
    /*
     * Play list
     */
    
    @Override
    public Page<Video> findPlayedVideoByMemberId(Long memberId, PageRequest pageOrderRequest) {
    	logger.debug("findPlayedVideoByMemberId called");
    	return videoRepository.findAllByPlayWithHeartByMember(memberId, pageOrderRequest);
    }
    
    @Override
    public void addVideoToPlaylist(Long id, Member member) {
    	logger.debug("addVideoToPlaylist called");
    	// 중복 검사
    	VideoPlayRecord record = videoPlayRecordRepository.findByVideoIdAndMemberId(id, member.getId());
    	if (record != null) videoPlayRecordRepository.delete(record);
    	Video video = videoRepository.findById(id).orElse(null);
		if (video != null) {
			record = new VideoPlayRecord();
			record.setVideo(video);
			record.setMember(member);
			videoPlayRecordRepository.save(record);
		}
    }
    
    @Override
    public void removeVideoFromPlaylist(Long id, Member member) {
    	logger.debug("removeVideoFromPlaylist called");
    	VideoPlayRecord record = videoPlayRecordRepository.findByVideoIdAndMemberId(id, member.getId());
    	if (record != null) {
    		videoPlayRecordRepository.delete(record);
    	}
    }
    
    /*
     * Like list
     */
    
    @Override
    public Page<Video> findHeartVideoByMemberId(Long memberId, PageRequest pageOrderRequest) {
    	logger.debug("findHeartVideoByMemberId called");
    	return videoRepository.findAllByMemberHeart(memberId, pageOrderRequest);
    }
    
    @Override
    public void removeVideoFromHeartlist(Long id, Member member) {
    	logger.debug("removeVideoFromHeartlist called");
    	decreaseHeartCnt(id, member);
    }
    
    @Override
    public List<Video> findTop4ByMemberId(Long memberId) {
    	logger.debug("findTop4ByMemberId called");
    	return videoRepository.findTop4ByMemberIdOrderByCreatedAtDesc(memberId);
    }
    
    @Override
    public Page<Video> findByMemberId(Long memberId, PageRequest pageRequest) {
    	logger.debug("findByMemberId called");
    	return videoRepository.findByMemberId(memberId, pageRequest);
    }

    @Override
    public Page<Video> findAllByMemberId(Long memberId, PageRequest pageRequest) {
        logger.debug("findAllByMemberId called");
        return videoRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, pageRequest);
    }




    /*
     * 아티스트 프로필 상세
     */
    
    @Override
    public Long findCountByMemberId(Long memberId) {
    	logger.debug("findCountByMemberId called");
    	return videoRepository.findCountByMemberId(memberId);
    }
    
    @Override
    public List<Video> findByMemberId(Long memberId) {
    	logger.debug("findByMemberId called");
    	return videoRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }
    
    @Override
    public void deleteVideosByMemberId(Long memberId) {
    	logger.debug("deleteVideosByMemberId called");
    	List<Video> videos = findByMemberId(memberId);
		for (Video video : videos) {
			this.deleteVideo(video.getId(), memberId);
		}

		//다른 회원의 게시물에 내가 남긴 댓글 삭제
		List<VideoComment> videoComments = videoCommentRepository.findByMemberId(memberId);

		for (VideoComment videoComment : videoComments) {
		    videoCommentRepository.deleteById(videoComment.getId());
        }
    }
    
    /*
     * 관리자 영역
     */
    
    @Override
    public Page<Video> findAll(PageRequest pageOrderRequest) {
    	logger.debug("listAllVideos called");
        return videoRepository.findAll(pageOrderRequest);
    }
    
    class VideoCommentComparator implements Comparator<VideoComment> {
    	@Override
    	public int compare(VideoComment o1, VideoComment o2) {
    		return o1.getId().compareTo(o2.getId());
    	}
    }
}
