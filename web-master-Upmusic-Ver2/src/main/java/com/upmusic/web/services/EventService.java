package com.upmusic.web.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.Event;

public interface EventService {
	public Page<Event> findAllNewEvent(Date today, Pageable pageable);
	public Page<Event> findByShowYnAndCloseAt(Date today, Pageable pageable);
	public Event findByEventId(Long eventId);
	public Event saveEvent(Event event);
	public void deleteEvent(Long id);
	List<Event> findAllByCloseAtAndShowYn(Date today);
	List<Event> findByCloseAt(Date today);
	public Page<Event> findAllNewEventForAdmin(Date today, Pageable pageable);
}
