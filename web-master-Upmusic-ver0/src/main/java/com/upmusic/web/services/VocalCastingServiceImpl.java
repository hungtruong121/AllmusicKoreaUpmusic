package com.upmusic.web.services;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.VocalCasting;
import com.upmusic.web.domain.VocalCastingComment;
import com.upmusic.web.domain.VocalCastingHeartRecord;
import com.upmusic.web.repositories.VocalCastingCommentRepository;
import com.upmusic.web.repositories.VocalCastingHeartRecordRepository;
import com.upmusic.web.repositories.VocalCastingRepository;


@Service
public class VocalCastingServiceImpl implements VocalCastingService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private VocalCastingRepository castingRepository;
	
	@Autowired
	private VocalCastingHeartRecordRepository castingHeartRecordRepository;
    
	@Autowired
	private VocalCastingCommentRepository castingCommentRepository;
    


    @Override
    public Iterable<VocalCasting> listAllVocalCastings() {
        logger.debug("listAllVocalCastings called");
        return castingRepository.findAll();
    }
    
    @Override
    public Page<VocalCasting> findAll(PageRequest pageOrderByNew) {
    	logger.debug("findAll called");
    	return castingRepository.findAll(pageOrderByNew);
    }
    
    @Override
    public Page<VocalCasting> findAllWithCommentsCount(PageRequest pageOrderByNew) {
    	logger.debug("findAllWithCommentsCount called");
    	Date date = new Date();
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -3);
    	Page<VocalCasting> requests = castingRepository.findAll(pageOrderByNew);
    	for (VocalCasting request : requests) {
    		request.setCommentCnt(castingCommentRepository.countByVocalCasting(request));
    		request.setNewItem(request.getCreatedAt().after(cal.getTime()));
    	}
    	return requests;
    }
    
    @Override
    public VocalCasting getVocalCastingById(Long id) {
        logger.debug("getVocalCastingById called");
        return castingRepository.findById(id).orElse(null);
    }

    @Override
    public VocalCasting saveVocalCasting(VocalCasting request) {
        logger.debug("saveVocalCasting called");
        return castingRepository.save(request);
    }

    @Override
    public void deleteVocalCasting(Long id) {
        logger.debug("deleteVocalCasting called");
        // 좋아요 삭제
        List<VocalCastingHeartRecord> records = castingHeartRecordRepository.findByVocalCastingId(id);
        for (VocalCastingHeartRecord record : records) {
        	castingHeartRecordRepository.delete(record);
        }
        // 댓글 삭제
        List<VocalCastingComment> comments = castingCommentRepository.findByVocalCastingId(id);
        for (VocalCastingComment comment : comments) {
        	castingCommentRepository.delete(comment);
        }
        castingRepository.deleteById(id);
    }

    //회원 탈퇴에 따른 보컬캐스팅 삭제
    @Override
    public void deleteVocalCastingByMemberId(Long memberId) {
        logger.debug("deleteVocalCastingByMemberId called");

        List<VocalCasting> vocalCastings = castingRepository.findAllByMemberId(memberId);

        for (VocalCasting vocalCasting : vocalCastings) {
            this.deleteVocalCasting(vocalCasting.getId());
        }

        //다른 회원의 게시물에 코멘트 달았던 부분 삭제
        List<VocalCastingComment> vocalCastingComments = castingCommentRepository.findAllByMemberId(memberId);

        for (VocalCastingComment vocalCastingComment : vocalCastingComments) {
            castingCommentRepository.deleteById(vocalCastingComment.getId());
        }
    }

    /**
     * 조회수 추가
     */
    @Override
    public int increaseHitCnt(Long id) {
    	logger.debug("increaseHitCnt called");
    	VocalCasting request = castingRepository.findById(id).orElse(null);
        if (request != null) {
        	request.setHitCnt(request.getHitCnt() + 1);
        	request = castingRepository.save(request);
        	return request.getHitCnt();
        }
        return 0;
    }
    
    /**
     * 해당 보컬 캐스팅과 회원의 좋아요 여부
     */
    @Override
    public boolean likedVocalCasting(Long id, Long memberId) {
    	logger.debug("likedVocalCasting called : id is {} and member is {}", id, memberId);
    	List<VocalCastingHeartRecord> records = castingHeartRecordRepository.findAllByVocalCastingIdAndMemberId(id, memberId);
    	return 0 < records.size();
    }
    
    /**
     * 좋아요 추가
     */
    @Override
    public int increaseHeartCnt(Long id, Member member) {
    	logger.debug("increaseHeartCnt called");
    	VocalCasting casting = castingRepository.findById(id).orElse(null);
        if (casting != null) {
        	VocalCastingHeartRecord record = new VocalCastingHeartRecord();
        	record.setVocalCasting(casting);
    		record.setMember(member);
    		castingHeartRecordRepository.save(record);
    		casting.setHeartCnt(casting.getHeartCnt() + 1);
    		casting = castingRepository.save(casting);
        	return casting.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 좋아요 취소
     */
    @Override
    public int decreaseHeartCnt(Long id, Member member) {
    	logger.debug("decreaseHeartCnt called");
        VocalCasting casting = castingRepository.findById(id).orElse(null);
        if (casting != null) {
        	List<VocalCastingHeartRecord> records = castingHeartRecordRepository.findAllByVocalCastingIdAndMemberId(id, member.getId());
        	if (0 < records.size()) {
        		castingHeartRecordRepository.delete(records.get(0));
        		if (0 < casting.getHeartCnt()) {
        			casting.setHeartCnt(casting.getHeartCnt() - 1);
        			casting = castingRepository.save(casting);
            	}
        	} else {
        		// something wrong
        	}
        	return casting.getHeartCnt();
        }
        return 0;
    }
    
    /**
     * 댓글 목록 반환
     */
    @Override
    public Page<VocalCastingComment> getComments(Long castingId, PageRequest pageOrderRequest) {
    	logger.debug("getComments called");
    	return castingCommentRepository.findByVocalCastingId(castingId, pageOrderRequest);
    }
    
    /**
     * 댓글 반환
     */
    @Override
    public VocalCastingComment getCommentById(Long commentId) {
    	logger.debug("getCommentById called");
    	return castingCommentRepository.findById(commentId).orElse(null);
    }
    @Override
    public VocalCastingComment getCommentById(Long castingId, Long commentId) {
    	logger.debug("getCommentById called");
    	return castingCommentRepository.findById(commentId).orElse(null);
    }
    
    /**
     * 댓글 추가
     */
    @Override
    public VocalCastingComment addComment(Long id, Member member, String content, String ip) {
    	logger.debug("addComment called");
    	VocalCasting request = castingRepository.findById(id).orElse(null);
        if (request != null) {
        	VocalCastingComment comment = new VocalCastingComment();
        	comment.setIp(ip);
        	comment.setMember(member);
        	comment.setContent(content);
        	comment.setVocalCasting(request);
        	comment = castingCommentRepository.save(comment);
        	return comment;
        }
        return null;
    }
    
    /**
     * 댓글 업데이트
     */
    @Override
    public VocalCastingComment updateComment(Long id, Long commentId, Member member, String content, String ip) {
    	logger.debug("updateComment called");
    	VocalCasting request = castingRepository.findById(id).orElse(null);
        if (request != null) {
        	VocalCastingComment comment = castingCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(member.getId()))) {
    			comment.setIp(ip);
            	comment.setMember(member);
            	comment.setContent(content);
            	comment.setVocalCasting(request);
    			comment = castingCommentRepository.save(comment);
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
    	VocalCasting request = castingRepository.findById(id).orElse(null);
        if (request != null) {
        	VocalCastingComment comment = castingCommentRepository.findById(commentId).orElse(null);
    		if (comment != null && 0 == (comment.getMember().getId().compareTo(memberId))) {
    			castingCommentRepository.delete(comment);
    		}
        	return commentId;
        }
        return null;
    }
    
    class VocalCastingCommentComparator implements Comparator<VocalCastingComment> {
    	@Override
    	public int compare(VocalCastingComment o1, VocalCastingComment o2) {
    		return o1.getId().compareTo(o2.getId());
    	}
    }
}
