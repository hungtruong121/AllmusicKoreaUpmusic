package com.upmusic.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upmusic.web.domain.FeedLike;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long>{
	
	@Query("SELECT COUNT(*) FROM FeedLike FL WHERE FL.id.feedId = :feedId AND FL.id.memberId = :memberId")
	int findByFeedIdAndMemberId(@Param("feedId") Long feedId, @Param("memberId") Long memberId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM FeedLike FL WHERE FL.id.feedId = :feedId")
	void deleteByFeedId(@Param("feedId") Long feedId);
	
}
