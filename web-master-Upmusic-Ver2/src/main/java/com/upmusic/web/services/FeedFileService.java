package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.domain.FeedFile;

public interface FeedFileService {
	public FeedFile insertFeedFile(FeedFile feedFile);
	public List<FeedFile> findAllByMemberId(Long id);
	public void deleteByFeedId(Long id);
	public List<FeedFile> findAllByFeedId(Long id);
	public List<FeedFile> findAllByFeedIdDESC(Long id);
}
