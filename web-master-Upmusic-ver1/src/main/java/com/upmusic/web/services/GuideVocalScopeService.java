package com.upmusic.web.services;

import com.upmusic.web.domain.GuideVocalScope;


public interface GuideVocalScopeService {
	
	Iterable<GuideVocalScope> listAllGuideVocalScopes();
	
	GuideVocalScope getGuideVocalScopeById(int id);

}
