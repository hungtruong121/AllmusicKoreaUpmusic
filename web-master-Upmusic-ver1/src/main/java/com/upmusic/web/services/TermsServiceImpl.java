package com.upmusic.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Terms;
import com.upmusic.web.repositories.TermsRepositories;

@Service
public class TermsServiceImpl implements TermsService{

	@Autowired
	private TermsRepositories termsRepositories;

	@Override
	public List<Terms> findList() {
		return termsRepositories.findAll();
	}

	@Override
	public Terms findById(int id) {
		return termsRepositories.findById(id).orElse(null);
	}

	@Override
	public Terms saveTerms(Terms terms) {
		return termsRepositories.save(terms);
	}

	@Override
	public void deleteTerms(int id) {
		termsRepositories.deleteById(id);
	}

	@Override
	public void increaseHitCnt(Terms terms) {
		terms.setHitCnt(terms.getHitCnt() + 1);
		termsRepositories.save(terms);
	}

}
