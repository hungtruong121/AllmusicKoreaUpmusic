package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.upmusic.web.domain.FeedFile;

@Repository
public interface FeedFileRepository extends JpaRepository<FeedFile, Long>{
	
	@Query("SELECT FF FROM FeedFile FF WHERE FF.feed.feedId = :id ORDER BY FF.feedFileId ASC")
	List<FeedFile> findAllByFeedId(@Param("id") Long id);
	
	@Query("SELECT FF FROM FeedFile FF WHERE FF.feed.feedId = :id ORDER BY FF.feedFileId DESC")
	List<FeedFile> findAllByFeedIdDESC(@Param("id") Long id);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM FeedFile FF WHERE FF.feed.feedId = :id")
	void deleteByFeedId(@Param("id") Long id);
}
