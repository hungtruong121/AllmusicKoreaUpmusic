package com.upmusic.web.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Event;
import com.upmusic.web.repositories.EventRepository;

@Service
public class EventServiceImpl implements EventService{

	@Autowired
	private EventRepository eventRepository;


	@Override
	public Page<Event> findAllNewEvent(Date today, Pageable pageable) {
		Page<Event> temp = eventRepository.findByCloseAtAndShowYn(today, pageable);
		for(Event e : temp.getContent()) {
			System.out.println("################################## " + e);
		}
		
		return eventRepository.findByCloseAtAndShowYn(today, pageable);
	}


	@Override
	public Page<Event> findByShowYnAndCloseAt(Date today, Pageable pageable) {
		return eventRepository.findByCloseAt(today, pageable);
	}


	@Override
	public Event findByEventId(Long eventId) {
		return eventRepository.findByEventId(eventId);
	}


	@Override
	public Event saveEvent(Event event) {
		return eventRepository.save(event);
	}

	@Override
	public void deleteEvent(Long id) {
		eventRepository.deleteById(id);
	}


	@Override
	public List<Event> findAllByCloseAtAndShowYn(Date today) {
		return eventRepository.findAllByCloseAtAndShowYn(today);
	}


	@Override
	public List<Event> findByCloseAt(Date today) {
		return eventRepository.findByCloseAt(today);
	}


	@Override
	public Page<Event> findAllNewEventForAdmin(Date today, Pageable pageable) {
		return eventRepository.findByCloseAtAndShowYnForAdmin(today, pageable);
	}

}
