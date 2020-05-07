package com.upmusic.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.EventBanner;
import com.upmusic.web.repositories.EventBannerRepository;

@Service
public class EventBannerServiceImpl implements EventBannerService{

	@Autowired
	private EventBannerRepository eventBannerRepository;

	@Override
	public Page<EventBanner> findEventList(Pageable pageable) {
		return eventBannerRepository.findAll(pageable);
	}

	@Override
	public Page<EventBanner> findUseEventList(Pageable pageable) {
		return eventBannerRepository.findAllByShowYn(pageable);
	}
	@Override
	public EventBanner findByEventBannerId(Long eventBannerId) {
		return eventBannerRepository.findByEventBannerId(eventBannerId);
	}

	@Override
	public EventBanner save(EventBanner eventBanner) {
		return eventBannerRepository.save(eventBanner);
	}

	@Override
	public void delete(Long eventBannerId) {
		eventBannerRepository.deleteById(eventBannerId);

	}

}
