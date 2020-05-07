package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.upmusic.web.domain.Feed;

public interface FeedService {
	public Page<Feed> findByMemberId(Long id, Pageable pageable, Pageable commentPageable);
	public Feed insertFeed(Feed feed);
	public void updateByFeedId(Long feedId, int commentCnt);
	public Page<Feed> findAll(Long id, Pageable pageable, Pageable commentPageable);
	public Page<Feed> findByMemberIdAndLiker(Long id, Pageable pageable, Pageable commentPageable);
	public void deleteFeed(Feed feed);
	public void deleteFeedByMemberId(Long memberId);
	public Feed findByFeedId(Long id);
	
	//mobile
	public List<Feed> findByMemberIdAndLiker(Long id);
	public List<Feed> findByMemberId(Long id);
	public List<Feed> findAll(Long id);
	public Feed findByFeedId(Long feedId, Long id);
	
}
