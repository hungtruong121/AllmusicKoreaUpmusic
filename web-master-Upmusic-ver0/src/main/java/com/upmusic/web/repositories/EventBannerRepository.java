package com.upmusic.web.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.EventBanner;

@Repository
public interface EventBannerRepository extends JpaRepository<EventBanner, Long>{


	@Query("select E from EventBanner E where E.showYn = '1'")
	Page<EventBanner> findAllByShowYn(Pageable pageable);

	EventBanner findByEventBannerId(Long eventBannerId);

}
