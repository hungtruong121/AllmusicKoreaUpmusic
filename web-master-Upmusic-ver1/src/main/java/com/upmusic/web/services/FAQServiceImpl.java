package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.FAQ;
import com.upmusic.web.repositories.FAQRepository;

@Service
public class FAQServiceImpl implements FAQService{

	@Autowired
	private FAQRepository FAQRepository;
	
	@Override
	public Page<FAQ> findByParentFaqIdIsNull(String content, String category, Pageable pageable) {
		
		Page<FAQ> result = FAQRepository.findByParentFaqIdIsNull(content, category, pageable);
		
		// 답변 조회
		for(FAQ f : result.getContent()) {
			f.setAnswer(FAQRepository.findByParentFaqId(f.getFaqId()));
			f.getAnswer().setContent(f.getAnswer().getContent().replace("<p>", "<p class='answer'>"));
		}
		
		return result;
	}

	@Override
	public Page<FAQ> findByParentFaqIdIsNullForAdmin(String content, String category, Pageable pageable) {
		Page<FAQ> result = FAQRepository.findByParentFaqIdIsNullForAdmin(content, category, pageable);
		
		// 답변 조회
		for(FAQ f : result.getContent()) {
			f.setAnswer(FAQRepository.findByParentFaqId(f.getFaqId()));
			f.getAnswer().setContent(f.getAnswer().getContent().replace("<p>", "<p class='answer'>"));
		}
		
		return result;
	}

	@Override
	public FAQ save(FAQ faq) {
		return FAQRepository.save(faq);
	}

	@Override
	public FAQ findByFaqId(Long faqId) {
		FAQ result = FAQRepository.findByFaqId(faqId);
		result.setAnswer(FAQRepository.findByParentFaqId(faqId));
		return result;
	}

	@Override
	public FAQ findByParentFaqId(Long parentFaqId) {
		return FAQRepository.findByParentFaqId(parentFaqId);
	}

	@Override
	public void delete(Long faqId) {
		FAQRepository.deleteById(faqId);
	}

	@Override
	public List<FAQ> findByParentFaqIdIsNull(String content, String category) {
		List<FAQ> result = FAQRepository.findByParentFaqIdIsNull(content, category);
		
		// 답변 조회
		for(FAQ f : result) {
			f.setAnswer(FAQRepository.findByParentFaqId(f.getFaqId()));
			f.getAnswer().setContent(f.getAnswer().getContent().replace("<p>", "<p class='answer'>"));
		}
		
		return result;
	}

}
