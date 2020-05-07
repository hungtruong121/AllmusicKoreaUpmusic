package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{
	
	@Query("select E from Event E where E.closeAt >= :today and E.showYn = '1' and E.openAt <= :today")
	Page<Event> findByCloseAtAndShowYn(@Param("today") Date today, Pageable pageable);
	
	@Query("select E from Event E where E.closeAt < :today and E.showYn = '1'")
	Page<Event> findByCloseAt(@Param("today") Date today, Pageable pageable);
	
	Event findByEventId(Long eventId);
	
	@Query("select E from Event E where E.closeAt >= :today and E.showYn = '1'")
	List<Event> findAllByCloseAtAndShowYn(@Param("today") Date today);
	
	@Query("select E from Event E where E.closeAt < :today and E.showYn = '1' ORDER BY E.createdAt DESC")
	List<Event> findByCloseAt(@Param("today") Date today);
	
	@Query("select E from Event E where E.closeAt >= :today and E.showYn = '1'")
	Page<Event> findByCloseAtAndShowYnForAdmin(@Param("today") Date today, Pageable pageable);
}
