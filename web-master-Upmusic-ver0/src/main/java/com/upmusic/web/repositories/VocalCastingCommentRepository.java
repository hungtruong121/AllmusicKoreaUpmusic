package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.VocalCasting;
import com.upmusic.web.domain.VocalCastingComment;


@Repository
public interface VocalCastingCommentRepository extends JpaRepository<VocalCastingComment, Long> {
	
	public Page<VocalCastingComment> findByVocalCastingId(Long castingId, Pageable pageOrderRequest);
	
	Long countByVocalCasting(VocalCasting vocalCasting);

	public List<VocalCastingComment> findByVocalCastingId(Long castingId);

	public List<VocalCastingComment> findAllByMemberId(Long memberId);
	
}
