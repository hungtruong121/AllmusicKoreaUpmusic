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

import com.upmusic.web.domain.Feed;


@Repository
public interface FeedRepository extends JpaRepository<Feed, Long>{
	
	@Query("SELECT new com.upmusic.web.domain.Feed(F, M) FROM Feed F LEFT JOIN Member M ON F.member.id = M.id WHERE F.member.id = :id")
	Page<Feed> findByMemberId(@Param("id") Long id, Pageable pageable);
	
	@Query(value = "SELECT * FROM feed F LEFT JOIN member M ON F.member_id = M.id WHERE F.member_id = :id OR (F.member_id IN (SELECT M.id FROM member M INNER JOIN member_liker ML ON M.id = ML.member_id WHERE ML.liker_id = :id) AND F.public_range <> 'PRIVATE')", nativeQuery = true)
	Page<Feed> findByMemberIdAndLiker(@Param("id") Long id, Pageable pageable);
	
	@Query(value = "SELECT * FROM feed F LEFT JOIN member M ON F.member_id = M.id WHERE F.member_id = :id OR (F.member_id IN (SELECT M.id FROM member M INNER JOIN member_liker ML ON M.id = ML.member_id WHERE ML.liker_id = :id) AND F.public_range <> 'PRIVATE') OR F.public_range = 'ALL'", nativeQuery = true)
	Page<Feed> findAllByPublicRange(@Param("id") Long id, Pageable pageable);
	
	@Modifying
	@Transactional
	@Query("UPDATE Feed F SET F.commentCnt = :commentCnt WHERE F.feedId = :feedId")
	void updateByFeedId(@Param("feedId") Long feedId, @Param("commentCnt") int commentCnt);
	
	@Query("SELECT F FROM Feed F WHERE F.feedId = :feedId")
	Feed findByFeedId(@Param("feedId") Long feedId);
	
	// mobile
	@Query(value = "SELECT * FROM feed F LEFT JOIN member M ON F.member_id = M.id WHERE F.member_id = :id OR (F.member_id IN (SELECT M.id FROM member M INNER JOIN member_liker ML ON M.id = ML.member_id WHERE ML.liker_id = :id) AND F.public_range <> 'PRIVATE') ORDER BY F.created_at DESC", nativeQuery = true)
	List<Feed> findByMemberIdAndLiker(@Param("id") Long id);
	
	@Query("SELECT new com.upmusic.web.domain.Feed(F, M) FROM Feed F LEFT JOIN Member M ON F.member.id = M.id WHERE F.member.id = :id ORDER BY F.createdAt DESC")
	List<Feed> findByMemberId(@Param("id") Long id);
	
	@Query(value = "SELECT * FROM feed F LEFT JOIN member M ON F.member_id = M.id WHERE F.member_id = :id OR (F.member_id IN (SELECT M.id FROM member M INNER JOIN member_liker ML ON M.id = ML.member_id WHERE ML.liker_id = :id) AND F.public_range <> 'PRIVATE') OR F.public_range = 'ALL' ORDER BY F.created_at DESC", nativeQuery = true)
	List<Feed> findAllByPublicRange(@Param("id") Long id);
	
}
