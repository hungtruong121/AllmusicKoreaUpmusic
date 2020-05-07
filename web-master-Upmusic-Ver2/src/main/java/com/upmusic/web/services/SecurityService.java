package com.upmusic.web.services;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {

	String findLoggedInEmail();

	String autologin(String email, String password, HttpServletRequest request);
	
	boolean login(String email, String password);
}
