package com.upmusic.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.FAQ;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long>{
	
	@Query("SELECT F FROM FAQ F WHERE F.parentFaqId IS NULL AND F.showYn = '1' AND F.category LIKE CONCAT('%', :category, '%') AND F.content LIKE CONCAT('%', :content, '%')")
	Page<FAQ> findByParentFaqIdIsNull(@Param("content") String content, @Param("category") String category, Pageable pageable);
	
	@Query("SELECT F FROM FAQ F WHERE F.parentFaqId IS NULL AND F.category LIKE CONCAT('%', :category, '%') AND F.content LIKE CONCAT('%', :content, '%')")
	Page<FAQ> findByParentFaqIdIsNullForAdmin(@Param("content") String content, @Param("category") String category, Pageable pageable);
	
	@Query("SELECT F FROM FAQ F WHERE F.parentFaqId = :parentFaqId")
	FAQ findByParentFaqId(@Param("parentFaqId") Long parentFaqId);
	
	@Query("SELECT F FROM FAQ F WHERE F.faqId = :faqId")
	FAQ findByFaqId(@Param("faqId") Long faqId);
	
	@Query("SELECT F FROM FAQ F WHERE F.parentFaqId IS NULL AND F.showYn = '1' AND F.category LIKE CONCAT('%', :category, '%') AND F.content LIKE CONCAT('%', :content, '%') ORDER BY F.createdAt DESC")
	List<FAQ> findByParentFaqIdIsNull(@Param("content") String content, @Param("category") String category);
}
