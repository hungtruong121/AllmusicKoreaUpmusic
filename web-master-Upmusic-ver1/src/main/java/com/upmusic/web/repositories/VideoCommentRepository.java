package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.VideoComment;


@Repository
public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {

	public Page<VideoComment> findByVideoId(Long videoId, Pageable pageOrderRequest);

	public List<VideoComment> findByVideoId(Long id);

	public List<VideoComment> findByMemberId(Long memberId);
	
}
