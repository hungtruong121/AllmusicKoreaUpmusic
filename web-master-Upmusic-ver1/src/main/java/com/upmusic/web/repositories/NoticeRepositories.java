package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.upmusic.web.domain.Notice;

@Repository
public interface NoticeRepositories extends JpaRepository<Notice, Long>{
	
	public Notice findByNoticeId(Long noticeId);
	
	public Page<Notice> findByCategory(String category, Pageable pageable);
	
	@Query("SELECT N FROM Notice N WHERE N.category LIKE CONCAT('%', :category, '%') ORDER BY N.createdAt DESC")
	public List<Notice> findByCategoryMobile(@Param("category") String category);

}
