package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upmusic.web.domain.FeedComment;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, Long>{

	@Query("SELECT new com.upmusic.web.domain.FeedComment(FC, M) FROM FeedComment FC LEFT JOIN Member M ON FC.member.id = M.id WHERE FC.feed.feedId = :feedId")
	Page<FeedComment> findAllByFeedId(@Param("feedId") Long feedId, Pageable Pageable);
	
	@Query("SELECT COUNT(*) FROM FeedComment FC WHERE FC.feed.feedId = :feedId")
	int countByFeedId(@Param("feedId") Long feedId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM FeedComment FC WHERE FC.feedCommentId = :feedCommentId")
	void deleteByFeedCommentId(@Param("feedCommentId") Long feedCommentId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM FeedComment FC WHERE FC.feed.feedId = :feedId")
	void deleteByFeedId(@Param("feedId") Long feedId);
	
	@Query("SELECT new com.upmusic.web.domain.FeedComment(FC, M) FROM FeedComment FC LEFT JOIN Member M ON FC.member.id = M.id WHERE FC.feed.feedId = :feedId ORDER BY FC.createdAt DESC")
	List<FeedComment> findAllByFeedId(@Param("feedId") Long feedId);
	
	@Query("SELECT FC FROM FeedComment FC WHERE FC.feedCommentId = :feedCommentId")
	FeedComment findByFeedCommentId(@Param("feedCommentId") Long feedCommentId);

	public List<FeedComment> findAllByMemberId(Long memberId);
}
