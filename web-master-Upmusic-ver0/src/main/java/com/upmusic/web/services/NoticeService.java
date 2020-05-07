package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.Notice;

public interface NoticeService {
	Page<Notice> findList(Pageable pageable);
	public Notice findByNoticeId(Long noticeId);
	public Page<Notice> findByCategory(String category, Pageable pageable);
	public void saveNotice(Notice notice);
	public void updateHitCnt(Notice notice);
	public void insertNotice(Notice notice);
	public void deleteNotice(Long id);
	public List<Notice> findList();
	public List<Notice> findByCategory(String category);
}
