package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Notice;
import com.upmusic.web.repositories.NoticeRepositories;

@Service
public class NoticeServiceImpl implements NoticeService{

	@Autowired
	private NoticeRepositories noticeRepositories;

	@Override
	public Page<Notice> findList(Pageable pageable) {
		return noticeRepositories.findAll(pageable);
	}

	@Override
	public Notice findByNoticeId(Long noticeId) {
		return noticeRepositories.findByNoticeId(noticeId);
	}

	@Override
	public Page<Notice> findByCategory(String category, Pageable pageable) {
		Page<Notice> result = noticeRepositories.findByCategory(category, pageable);
		return result;
	}

	@Override
	public void saveNotice(Notice notice) {
		noticeRepositories.save(notice);
	}

	@Override
	public void deleteNotice(Long id) {
		noticeRepositories.deleteById(id);
	}

	@Override
	public void updateHitCnt(Notice notice) {
		noticeRepositories.save(notice);
	}

	@Override
	public void insertNotice(Notice notice) {
		noticeRepositories.save(notice);
	}

	@Override
	public List<Notice> findList() {
		return noticeRepositories.findAll();
	}

	@Override
	public List<Notice> findByCategory(String category) {
		return noticeRepositories.findByCategoryMobile(category);
	}
}
