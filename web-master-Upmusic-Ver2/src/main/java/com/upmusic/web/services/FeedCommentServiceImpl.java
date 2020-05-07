package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.FeedComment;
import com.upmusic.web.repositories.FeedCommentRepository;

@Service
public class FeedCommentServiceImpl implements FeedCommentService{

	@Autowired
	private FeedCommentRepository feedCommentRepository;
	
	@Override
	public Page<FeedComment> findAllByFeedId(Long feedId, Pageable pageable, Long id) {
		Page<FeedComment> result = feedCommentRepository.findAllByFeedId(feedId, pageable);
		
		for(FeedComment feedComment : result) {
			if(id.equals(feedComment.getMember().getId())) {
				feedComment.setRegistrantFlag(true);
			}
			feedComment.setCreatedAtFormat(feedComment.getCreateAtFormat());
			feedComment.setCreateMemberUrl();
			feedComment.setMemberUrl(feedComment.getMember().getUrl());
		}
		
		return result;
	}

	@Override
	public FeedComment insertFeedComment(FeedComment feedComment) {
		return feedCommentRepository.save(feedComment);
	}

	@Override
	public int countByFeedId(Long feedId) {
		System.out.println("########################" + feedId);
		return feedCommentRepository.countByFeedId(feedId);
	}

	@Override
	public void deleteByFeedCommentId(Long feedCommentId) {
		feedCommentRepository.deleteByFeedCommentId(feedCommentId);
	}

	@Override
	public void deleteByFeedId(Long feedId) {
		feedCommentRepository.deleteByFeedId(feedId);
	}

	@Override
	public List<FeedComment> findAllByFeedId(Long feedId, Long id) {
		List<FeedComment> result = feedCommentRepository.findAllByFeedId(feedId);
		
		for(FeedComment feedComment : result) {
			if(id.equals(feedComment.getMember().getId())) {
				feedComment.setRegistrantFlag(true);
			}
			feedComment.setCreatedAtFormat(feedComment.getCreateAtFormat());
			feedComment.setCreateMemberUrl();
			feedComment.setMemberUrl(feedComment.getMember().getUrl());
		}
		
		return result;
	}

	@Override
	public FeedComment findByFeedCommentId(Long feedCommentId) {
		return feedCommentRepository.findByFeedCommentId(feedCommentId);
	}

}
