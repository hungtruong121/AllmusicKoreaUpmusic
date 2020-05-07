package com.upmusic.web.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.EventBanner;

public interface EventBannerService {

	public Page<EventBanner> findEventList(Pageable pageable);
	public Page<EventBanner> findUseEventList(Pageable pageable);
	public EventBanner findByEventBannerId(Long eventBannerId);
	public EventBanner save(EventBanner eventBanner);
	public void delete(Long eventBannerId);
}
