package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.AuthenticationPhone;


@Repository
public interface AuthenticationPhoneRepository extends CrudRepository<AuthenticationPhone, Long> {
	
	AuthenticationPhone findByPhoneNumber(String phoneNumber);
}
