package com.upmusic.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicRequest;
import com.upmusic.web.domain.MusicRequestComment;


public interface MusicRequestService {
	
	Iterable<MusicRequest> listAllRequests();
	
	Page<MusicRequest> findAll(PageRequest pageOrderByNew);
	
	Page<MusicRequest> findAllWithCommentsCount(PageRequest pageOrderByNew);

	MusicRequest getRequestById(Long id);

	MusicRequest saveRequest(MusicRequest album);

    void deleteRequest(Long id);

    void deleteRequestByMemberId(Long memberId);
    
    int increaseHitCnt(Long id);
    
    Page<MusicRequestComment> getComments(Long requestId, PageRequest pageOrderRequest);
    
    MusicRequestComment getCommentById(Long commentId);
    
    MusicRequestComment getCommentById(Long requestId, Long commentId);
    
    MusicRequestComment addComment(Long id, Member member, String content, String ip);
	
    MusicRequestComment updateComment(Long id, Long commentId, Member member, String content, String ip);

	Long deleteComment(Long id, Long commentId, Long memberId);

}
