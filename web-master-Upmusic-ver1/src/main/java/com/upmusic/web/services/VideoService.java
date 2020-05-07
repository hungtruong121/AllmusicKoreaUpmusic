package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.Video;
import com.upmusic.web.domain.VideoComment;

public interface VideoService {
	
	Iterable<Video> listAllVideos();
	
	Video getTop1();
	
	Iterable<Video> getTop3();
	
	Iterable<Video> getTop4();
	
	Iterable<Video> getTop5();
	
	Page<Video> findTop100(PageRequest pageOrderByHot);
	
	List<Video> findTop5MV();
	
	Page<Video> findAllMV(PageRequest pageOrderByHot);

	List<Video> findTop3GV();
	
	List<Video> findTop4GV();
	
	List<Video> findTop5GV();
	
	Page<Video> findAllGV(PageRequest pageOrderByHot);

	Video getVideoById(Long id);

	Video saveVideo(Video video);
	
	void deleteVideo(Long id, Long memberId);
	
    void deleteVideo(Long id);
    
    int increaseHitCnt(Long id);
    
    boolean likedVideo(Long id, Long memberId);
    
    int increaseHeartCnt(Long id, Member member);
    
    int decreaseHeartCnt(Long id, Member member);
    
    Page<VideoComment> getComments(Long videoId, PageRequest pageOrderRequest);
    
    VideoComment getCommentById(Long commentId);
    
    VideoComment getCommentById(Long videoId, Long commentId);

	VideoComment addComment(Long videoId, Member member, String content, String ip);
	
	VideoComment updateComment(Long videoId, Long commentId, Member member, String content, String ip);

	Long deleteComment(Long videoId, Long commentId, Long memberId);
	
	// Play list
	Page<Video> findPlayedVideoByMemberId(Long memberId, PageRequest pageOrderRequest);
	void addVideoToPlaylist(Long id, Member member);
	void removeVideoFromPlaylist(Long id, Member member);
	
	// Like list
	Page<Video> findHeartVideoByMemberId(Long memberId, PageRequest pageOrderRequest);
	void removeVideoFromHeartlist(Long id, Member member);

	// 업로드 내역
	Long findCountByMemberId(Long memberId);
	List<Video> findTop4ByMemberId(Long memberId);
	Page<Video> findByMemberId(Long memberId, PageRequest pageRequest);
	Page<Video> findAllByMemberId(Long memberId, PageRequest pageRequest);

	// 아티스트 프로필 상세
	List<Video> findByMemberId(Long memberId);
	
	// 회원 탈퇴에 따른 비디오 제거
	void deleteVideosByMemberId(Long memberId);

	// 관리자 영역
	Page<Video> findAll(PageRequest pageOrderRequest);

}
