package com.upmusic.web.services;

import com.upmusic.web.domain.Theme;


public interface ThemeService {

	Iterable<Theme> listAllThemes();
	
    Theme findByName(String name);

	Theme findById(int i);
}
