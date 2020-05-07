package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.MusicRequest;
import com.upmusic.web.domain.MusicRequestComment;


@Repository
public interface MusicRequestCommentRepository extends JpaRepository<MusicRequestComment, Long> {
	
	Page<MusicRequestComment> findByMusicRequestId(Long requestId, Pageable pageOrderRequest);
	
	List<MusicRequestComment> findByMusicRequestId(Long requestId);
	
	Long countByMusicRequest(MusicRequest musicRequest);

	public List<MusicRequestComment> findAllByMemberId(Long memberId);
}
