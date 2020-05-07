package com.upmusic.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.FeedLike;
import com.upmusic.web.repositories.FeedLikeRepository;

@Service
public class FeedLikeServiceImpl implements FeedLikeService{

	@Autowired
	private FeedLikeRepository feedLikeRepository;
	
	
	@Override
	public FeedLike save(FeedLike feedLike) {
		return feedLikeRepository.save(feedLike);
	}

	@Override
	public void delete(FeedLike feedLike) {
		feedLikeRepository.delete(feedLike);
	}

	@Override
	public int findByFeedIdAndMemberId(Long feedId, Long memberId) {
		return feedLikeRepository.findByFeedIdAndMemberId(feedId, memberId);
	}

	@Override
	public void deleteByFeedId(Long feedId) {
		feedLikeRepository.deleteByFeedId(feedId);
	}

}
