package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.AuthenticationEmail;


@Repository
public interface AuthenticationEmailRepository extends CrudRepository<AuthenticationEmail, Long> {
	
	AuthenticationEmail findByEmail(String email);
}
