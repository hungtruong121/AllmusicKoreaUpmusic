package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Genre;


@Repository
public interface GenreRepository extends CrudRepository<Genre, Integer> {
	
	Genre findByName(String name);
}
