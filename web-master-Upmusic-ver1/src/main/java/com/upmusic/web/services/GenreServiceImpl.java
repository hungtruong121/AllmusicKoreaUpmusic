package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upmusic.web.domain.Genre;
import com.upmusic.web.repositories.GenreRepository;


@Service
public class GenreServiceImpl implements GenreService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private GenreRepository genreRepository;
	
	@Override
	public Iterable<Genre> listAllGenres() {
		logger.debug("listAllGenres called");
        return genreRepository.findAll();
	}

	@Override
	public Genre findByName(String name) {
		logger.debug("findByName called");
        return genreRepository.findByName(name);
	}
	
	@Override
	public Genre findById(int id) {
		logger.debug("findById called");
        return genreRepository.findById(id).orElse(null);
	}

}
