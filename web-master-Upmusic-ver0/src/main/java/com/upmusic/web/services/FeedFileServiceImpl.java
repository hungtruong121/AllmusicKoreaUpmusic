package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.FeedFile;
import com.upmusic.web.repositories.FeedFileRepository;

@Service
public class FeedFileServiceImpl implements FeedFileService{

	@Autowired
	private FeedFileRepository feedFileRepository;
	
	@Override
	public FeedFile insertFeedFile(FeedFile feedFile) {
		return feedFileRepository.save(feedFile);
	}

	@Override
	public List<FeedFile> findAllByMemberId(Long id) {
		return feedFileRepository.findAllByFeedId(id);
	}

	@Override
	public void deleteByFeedId(Long id) {
		feedFileRepository.deleteByFeedId(id);
	}

	@Override
	public List<FeedFile> findAllByFeedId(Long id) {
		return feedFileRepository.findAllByFeedId(id);
	}

	@Override
	public List<FeedFile> findAllByFeedIdDESC(Long id) {
		return feedFileRepository.findAllByFeedIdDESC(id);
	}

}
