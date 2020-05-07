package com.upmusic.web.services;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MusicRequest;
import com.upmusic.web.domain.MusicRequestComment;
import com.upmusic.web.repositories.MusicRequestCommentRepository;
import com.upmusic.web.repositories.MusicRequestRepository;


@Service
public class MusicRequestServiceImpl implements MusicRequestService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private MusicRequestRepository requestRepository;
    
	@Autowired
	private MusicRequestCommentRepository requestCommentRepository;
    


    @Override
    public Iterable<MusicRequest> listAllRequests() {
        logger.debug("listAllRequests called");
        return requestRepository.findAll();
    }
    
    @Override
    public Page<MusicRequest> findAll(PageRequest pageOrderByNew) {
    	logger.debug("findAll called");
    	return requestRepository.findAll(pageOrderByNew);
    }
    
    @Override
    public Page<MusicRequest> findAllWithCommentsCount(PageRequest pageOrderByNew) {
    	logger.debug("findAllWithCommentsCount called");
    	Page<MusicRequest> requests = requestRepository.findAll(pageOrderByNew);
    	for (MusicRequest request : requests) {
    		request.setCommentCnt(requestCommentRepository.countByMusicRequest(request));
    	}
    	return requests;
    }
    
    @Override
    public MusicRequest getRequestById(Long id) {
        logger.debug("getRequestById called");
        return requestRepository.findById(id).orElse(null);
    }

    @Override
    public MusicRequest saveRequest(MusicRequest request) {
        logger.debug("saveRequest called");
        return requestRepository.save(request);
    }

    @Override
    public void deleteRequest(Long id) {
        logger.debug("deleteRequest called");
        // TODO delete track, comment, heart too
        // 댓글 삭제
        List<MusicRequestComment> comments = requestCommentRepository.findByMusicRequestId(id);
        for (MusicRequestComment comment : comments) {
        	requestCommentRepository.delete(comment);
        }
        requestRepository.deleteById(id);
    }

    @Override
    public void deleteRequestByMemberId(Long memberId) {
        logger.debug("deleteRequestByMemberId called");
        // TODO delete track, comment, heart too
        List<MusicRequest> requests = requestRepository.findAllByMemberId(memberId);

        for (MusicRequest request : requests) {
            this.deleteRequest(request.getId());
        }

        //다른 회원의 게시물에 달았던 코멘트 삭제
        List<MusicRequestComment> requestComments = requestCommentRepository.findAllByMemberId(memberId);

        for (MusicRequestComment requestComment : requestComments) {
            requestCommentRepository.deleteById(requestComment.getId());
        }

    }
    
    /**
     * 조회수 추가
     */
    @Override
    public int increaseHitCnt(Long id) {
    	logger.debug("increaseHitCnt called");
    	MusicRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
        	request.setHitCnt(request.getHitCnt() + 1);
        	request = requestRepository.save(request);
        	return request.getHitCnt();
        }
        return 0;
    }
    
    /**
     * 댓글 목록 반환
     */
    @Override
    public Page<MusicRequestComment> getComments(Long requestId, PageRequest pageOrderRequest) {
    	logger.debug("getComments called");
    	return requestCommentRepository.findByMusicRequestId(requestId, pageOrderRequest);
    }
    
    /**
     * 댓글 반환
     */
    @Override
    public MusicRequestComment getCommentById(Long commentId) {
    	logger.debug("getCommentById called");
    	return requestCommentRepository.findById(commentId).orElse(null);
    }
    @Override
    public MusicRequestComment getCommentById(Long requestId, Long commentId) {
    	logger.debug("getCommentById called");
    	return requestCommentRepository.findById(commentId).orElse(null);
    }
    
    /**
     * 댓글 추가
     */
    @Override
    public MusicRequestComment addComment(Long id, Member member, String content, String ip) {
    	logger.debug("addComment called");
    	MusicRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
        	MusicRequestComment comment = new MusicRequestComment();
        	comment.setIp(ip);
        	comment.setMember(member);
        	comment.setContent(content);
        	comment.setMusicRequest(request);
        	comment = requestCommentRepository.save(comment);
        	return comment;
        }
        return null;
    }
    
    /**
     * 댓글 업데이트
     */
    @Override
    public MusicRequestComment updateComment(Long id, Long commentId, Member member, String content, String ip) {
    	logger.debug("updateComment called");
    	MusicRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
        	MusicRequestComment comment = requestCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(member.getId()))) {
    			comment.setIp(ip);
            	comment.setMember(member);
            	comment.setContent(content);
            	comment.setMusicRequest(request);
    			comment = requestCommentRepository.save(comment);
    			return comment;
    		}
        }
        return null;
    }
    
    /**
     * 댓글 삭제
     */
    @Override
    public Long deleteComment(Long id, Long commentId, Long memberId) {
    	logger.debug("deleteComment called");
    	MusicRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
        	MusicRequestComment comment = requestCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(memberId))) {
    			requestCommentRepository.delete(comment);
    		}
        	return commentId;
        }
        return null;
    }
    
    class MusicRequestCommentComparator implements Comparator<MusicRequestComment> {
    	@Override
    	public int compare(MusicRequestComment o1, MusicRequestComment o2) {
    		return o1.getId().compareTo(o2.getId());
    	}
    }
}
