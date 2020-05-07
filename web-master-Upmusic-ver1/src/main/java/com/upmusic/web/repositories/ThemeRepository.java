package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Theme;


@Repository
public interface ThemeRepository extends CrudRepository<Theme, Integer> {
	
	Theme findByName(String name);
}
