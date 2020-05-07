package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.FeedComment;

public interface FeedCommentService {
	public Page<FeedComment> findAllByFeedId(Long feedId, Pageable pageable, Long id);
	public FeedComment insertFeedComment(FeedComment feedComment);
	public int countByFeedId(Long feedId);
	public void deleteByFeedCommentId(Long feedCommentId);
	public void deleteByFeedId(Long feedId);
	public List<FeedComment> findAllByFeedId(Long feedId, Long id);
	
	FeedComment findByFeedCommentId(Long feedCommentId);
}
