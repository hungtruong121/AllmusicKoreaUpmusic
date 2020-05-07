package com.upmusic.web.services;

import java.io.IOException;
import java.util.List;

import com.upmusic.web.domain.FeedComment;
import com.upmusic.web.domain.FeedLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Feed;
import com.upmusic.web.repositories.FeedCommentRepository;
import com.upmusic.web.repositories.FeedFileRepository;
import com.upmusic.web.repositories.FeedLikeRepository;
import com.upmusic.web.repositories.FeedRepository;

@Service
public class FeedServiceImpl implements FeedService{

	@Autowired
	private FeedRepository feedRepository;
	
	@Autowired
	private FeedFileRepository feedFileRepository;

	@Autowired
	private FeedCommentRepository feedCommentRepository;

	@Autowired
	private FeedLikeRepository feedLikeRepository;

	@Autowired
	private FeedCommentService feedCommentService;

	@Override
	public Page<Feed> findByMemberId(Long id, Pageable pageable, Pageable commentPageable) {
		Page<Feed> result = feedRepository.findByMemberId(id, pageable);
		
		for(Feed feed : result.getContent()) {
			// 이미지 조회
			feed.setFeedFileList(feedFileRepository.findAllByFeedId(feed.getFeedId()));
			feed.setFileSize(feed.getFeedFileList().size());
			
			feed.setFeedCommentListDate(feedCommentService.findAllByFeedId(feed.getFeedId(), commentPageable, id));
			feed.setCommentTotalPage(feed.getFeedCommentListDate().getTotalPages());
			feed.setFirstImg(feed.getFirstImgUrl());
			feed.setCreateAtFomatStr(feed.getCreateAtFormat());
			feed.setMemberUrl(feed.getMember().getUrl());
			try {
				feed.setMemberProfileUrl(feed.getMember().getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public Feed insertFeed(Feed feed) {
		return feedRepository.save(feed);
	}

	@Override
	public void updateByFeedId(Long feedId, int commentCnt) {
		feedRepository.updateByFeedId(feedId, commentCnt);
	}

	@Override
	public Page<Feed> findAll(Long id, Pageable pageable, Pageable commentPageable) {
		Page<Feed> result = feedRepository.findAllByPublicRange(id, pageable);
		
		for(Feed feed : result.getContent()) {
			// 이미지 조회
			feed.setFeedFileList(feedFileRepository.findAllByFeedId(feed.getFeedId()));
			feed.setFileSize(feed.getFeedFileList().size());
			
			feed.setFeedCommentListDate(feedCommentService.findAllByFeedId(feed.getFeedId(), commentPageable, id));
			feed.setCommentTotalPage(feed.getFeedCommentListDate().getTotalPages());
			feed.setFirstImg(feed.getFirstImgUrl());
			feed.setCreateAtFomatStr(feed.getCreateAtFormat());
			feed.setMemberUrl(feed.getMember().getUrl());
			try {
				feed.setMemberProfileUrl(feed.getMember().getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public Page<Feed> findByMemberIdAndLiker(Long id, Pageable pageable, Pageable commentPageable) {
		Page<Feed> result = feedRepository.findByMemberIdAndLiker(id, pageable);
		
		for(Feed feed : result.getContent()) {
			// 이미지 조회
			feed.setFeedFileList(feedFileRepository.findAllByFeedId(feed.getFeedId()));
			feed.setFileSize(feed.getFeedFileList().size());
			
			feed.setFeedCommentListDate(feedCommentService.findAllByFeedId(feed.getFeedId(), commentPageable, id));
			feed.setCommentTotalPage(feed.getFeedCommentListDate().getTotalPages());
			feed.setFirstImg(feed.getFirstImgUrl());
			feed.setCreateAtFomatStr(feed.getCreateAtFormat());
			feed.setMemberUrl(feed.getMember().getUrl());
			try {
				feed.setMemberProfileUrl(feed.getMember().getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public void deleteFeed(Feed feed) {
		feedRepository.delete(feed);
	}

	@Override
	public void deleteFeedByMemberId(Long memberId) {

		// TODO delete track, comment, heart too
		List<Feed> feeds = feedRepository.findByMemberId(memberId);

		for (Feed feed : feeds) {
			//코멘트삭제
			feedCommentRepository.deleteByFeedId(feed.getFeedId());
			//좋아요 삭제
			feedLikeRepository.deleteByFeedId(feed.getFeedId());
			//피드 파일 삭제
			feedFileRepository.deleteByFeedId(feed.getFeedId());
			//피드 삭제
			feedRepository.deleteById(feed.getFeedId());
		}

		//다른 회원의 게시물에 달았던 코멘트 삭제
		List<FeedComment> feedComments = feedCommentRepository.findAllByMemberId(memberId);

		for (FeedComment feedComment : feedComments) {
			feedCommentRepository.deleteById(feedComment.getFeedCommentId());
		}

		//다른 회원의 게시물에 달았던 코멘트 업데이트
		List<Feed> feedCommentUpdates = feedRepository.findAll();

		for (Feed feedCommentUpdate : feedCommentUpdates) {
			int commentCnt = feedCommentRepository.countByFeedId(feedCommentUpdate.getFeedId());
			feedRepository.updateByFeedId(feedCommentUpdate.getFeedId(), commentCnt);
		}
	}

	@Override
	public Feed findByFeedId(Long id) {
		Feed result = feedRepository.findByFeedId(id);
		
		result.setFeedCommentListDateMobile(feedCommentService.findAllByFeedId(result.getFeedId(), id));
		result.setCreateAtFomatStr(result.getCreateAtFormat());
		result.setMemberUrl(result.getMember().getUrl());
		try {
			result.setMemberProfileUrl(result.getMember().getProfileImageUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public List<Feed> findByMemberIdAndLiker(Long id) {
		List<Feed> result = feedRepository.findByMemberIdAndLiker(id);
		
		for(Feed feed : result) {
			// 이미지 조회
			feed.setFeedFileList(feedFileRepository.findAllByFeedId(feed.getFeedId()));
			feed.setFileSize(feed.getFeedFileList().size());
			
			feed.setFeedCommentListDateMobile(feedCommentService.findAllByFeedId(feed.getFeedId(), id));
			feed.setFirstImg(feed.getFirstImgUrl());
			feed.setCreateAtFomatStr(feed.getCreateAtFormat());
			feed.setMemberUrl(feed.getMember().getUrl());
			try {
				feed.setMemberProfileUrl(feed.getMember().getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public List<Feed> findByMemberId(Long id) {
		List<Feed> result = feedRepository.findByMemberId(id);
		
		for(Feed feed : result) {
			// 이미지 조회
			feed.setFeedFileList(feedFileRepository.findAllByFeedId(feed.getFeedId()));
			feed.setFileSize(feed.getFeedFileList().size());
			
			feed.setFeedCommentListDateMobile(feedCommentService.findAllByFeedId(feed.getFeedId(), id));
			feed.setFirstImg(feed.getFirstImgUrl());
			feed.setCreateAtFomatStr(feed.getCreateAtFormat());
			feed.setMemberUrl(feed.getMember().getUrl());
			try {
				feed.setMemberProfileUrl(feed.getMember().getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public List<Feed> findAll(Long id) {
		List<Feed> result = feedRepository.findAllByPublicRange(id);
		
		for(Feed feed : result) {
			// 이미지 조회
			feed.setFeedFileList(feedFileRepository.findAllByFeedId(feed.getFeedId()));
			feed.setFileSize(feed.getFeedFileList().size());
			
			feed.setFeedCommentListDateMobile(feedCommentService.findAllByFeedId(feed.getFeedId(), id));
			feed.setFirstImg(feed.getFirstImgUrl());
			feed.setCreateAtFomatStr(feed.getCreateAtFormat());
			feed.setMemberUrl(feed.getMember().getUrl());
			try {
				feed.setMemberProfileUrl(feed.getMember().getProfileImageUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public Feed findByFeedId(Long feedId, Long id) {
		Feed result = feedRepository.findByFeedId(feedId);
		
		result.setFeedCommentListDateMobile(feedCommentService.findAllByFeedId(result.getFeedId(), id));
		result.setCreateAtFomatStr(result.getCreateAtFormat());
		result.setMemberUrl(result.getMember().getUrl());
		try {
			result.setMemberProfileUrl(result.getMember().getProfileImageUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
