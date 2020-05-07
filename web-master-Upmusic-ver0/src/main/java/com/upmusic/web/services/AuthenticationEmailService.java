package com.upmusic.web.services;

import com.upmusic.web.domain.AuthenticationEmail;


public interface AuthenticationEmailService {
	
	AuthenticationEmail saveAuthenticationEmail(String email);
	
    void deleteAuthenticationEmail(Long id);

    void deleteAllAuthenticationEmail();
}
