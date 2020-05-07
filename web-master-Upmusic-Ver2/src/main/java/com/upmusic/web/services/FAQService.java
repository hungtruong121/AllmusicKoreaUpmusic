package com.upmusic.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.upmusic.web.domain.FAQ;

public interface FAQService {
	public Page<FAQ> findByParentFaqIdIsNull(String content, String category, Pageable pageable);
	public Page<FAQ> findByParentFaqIdIsNullForAdmin(String content, String category, Pageable pageable);
	public FAQ save(FAQ faq);
	public FAQ findByFaqId(Long faqId);
	public FAQ findByParentFaqId(Long parentFaqId);
	public void delete(Long faqId);
	public List<FAQ> findByParentFaqIdIsNull(String content, String category);
}
