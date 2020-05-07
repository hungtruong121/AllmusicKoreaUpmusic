package com.upmusic.web.services;

import java.util.List;

import com.upmusic.web.domain.Terms;


public interface TermsService {
	
	List<Terms> findList();
	
	public Terms findById(int id);
	
	public Terms saveTerms(Terms terms);
	
	public void increaseHitCnt(Terms terms);
	
	public void deleteTerms(int id);
	
}
