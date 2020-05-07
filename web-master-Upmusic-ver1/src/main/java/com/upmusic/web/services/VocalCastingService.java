package com.upmusic.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.VocalCasting;
import com.upmusic.web.domain.VocalCastingComment;


public interface VocalCastingService {
	
	Iterable<VocalCasting> listAllVocalCastings();
	
	Page<VocalCasting> findAll(PageRequest pageOrderByNew);
	
	Page<VocalCasting> findAllWithCommentsCount(PageRequest pageOrderByNew);

	VocalCasting getVocalCastingById(Long id);

	VocalCasting saveVocalCasting(VocalCasting album);

	void deleteVocalCasting(Long id);

    void deleteVocalCastingByMemberId(Long memberId);
    
    int increaseHitCnt(Long id);
    
    boolean likedVocalCasting(Long id, Long memberId);
	
	int increaseHeartCnt(Long id, Member member);
    
    int decreaseHeartCnt(Long id, Member member);
    
    Page<VocalCastingComment> getComments(Long castingId, PageRequest pageOrderRequest);
    
    VocalCastingComment getCommentById(Long commentId);
    
    VocalCastingComment getCommentById(Long castingId, Long commentId);
    
    VocalCastingComment addComment(Long id, Member member, String content, String ip);
	
    VocalCastingComment updateComment(Long id, Long commentId, Member member, String content, String ip);

	Long deleteComment(Long id, Long commentId, Long memberId);

}
