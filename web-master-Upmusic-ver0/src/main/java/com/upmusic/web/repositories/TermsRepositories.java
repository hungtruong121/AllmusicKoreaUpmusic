package com.upmusic.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Terms;


@Repository
public interface TermsRepositories extends JpaRepository<Terms, Integer>{
	
}
