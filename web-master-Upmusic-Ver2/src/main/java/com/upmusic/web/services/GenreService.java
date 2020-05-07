package com.upmusic.web.services;

import com.upmusic.web.domain.Genre;


public interface GenreService {

	Iterable<Genre> listAllGenres();
	
	Genre findByName(String name);

	Genre findById(int id);
}
