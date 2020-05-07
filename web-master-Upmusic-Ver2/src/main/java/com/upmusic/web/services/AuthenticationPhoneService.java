package com.upmusic.web.services;

import com.upmusic.web.domain.AuthenticationPhone;


public interface AuthenticationPhoneService {
	
	AuthenticationPhone saveAuthenticationPhone(String phoneNumber);
	
    void deleteAuthenticationPhone(Long id);

}
