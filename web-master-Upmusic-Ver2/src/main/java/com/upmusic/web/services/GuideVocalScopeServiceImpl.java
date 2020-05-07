package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.GuideVocalScope;
import com.upmusic.web.repositories.GuideVocalScopeRepository;


@Service
public class GuideVocalScopeServiceImpl implements GuideVocalScopeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	private GuideVocalScopeRepository guideScopeRepository;
	

    @Override
    public Iterable<GuideVocalScope> listAllGuideVocalScopes() {
        logger.debug("listAllGuideVocalScopes called");
        return guideScopeRepository.findAll();
    }
    
    @Override
    public GuideVocalScope getGuideVocalScopeById(int id) {
        logger.debug("getGuideVocalScopeById called");
        return guideScopeRepository.findById(id).orElse(null);
    }

}
