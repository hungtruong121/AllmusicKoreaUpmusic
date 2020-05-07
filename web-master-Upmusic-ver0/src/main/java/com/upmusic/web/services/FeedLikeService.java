package com.upmusic.web.services;

import com.upmusic.web.domain.FeedLike;

public interface FeedLikeService {
	public FeedLike save(FeedLike feedLike);
	public void delete(FeedLike feedLike);
	public int findByFeedIdAndMemberId(Long feedId, Long memberId);
	public void deleteByFeedId(Long feedId);
}
