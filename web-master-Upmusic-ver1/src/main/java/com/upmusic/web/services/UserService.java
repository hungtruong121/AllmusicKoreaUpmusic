package com.upmusic.web.services;

import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.AuthenticationEmail;
import com.upmusic.web.domain.AuthenticationPhone;


public interface UserService {

	Member save(Member user);
	
	void updatePassword(String email, String newPassword);

    Member findByEmail(String email);
    
    Member findByPhoneNumber(String phoneNumber);
    
    AuthenticationPhone findAuthenticationPhoneByPhoneNumber(String phoneNumber);
    
    AuthenticationEmail findAuthenticationEmailByEmail(String email);
    
    boolean checkPassword(long id, String password);
    
}
