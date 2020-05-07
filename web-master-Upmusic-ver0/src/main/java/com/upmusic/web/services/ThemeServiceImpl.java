package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Theme;
import com.upmusic.web.repositories.ThemeRepository;


@Service
public class ThemeServiceImpl implements ThemeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private ThemeRepository themeRepository;
	
	@Override
	public Iterable<Theme> listAllThemes() {
		logger.debug("listAllThemes called");
        return themeRepository.findAll();
	}

	@Override
	public Theme findByName(String name) {
		logger.debug("findByName called");
        return themeRepository.findByName(name);
	}
	
	@Override
	public Theme findById(int id) {
		logger.debug("findById called");
        return themeRepository.findById(id).orElse(null);
	}

}
