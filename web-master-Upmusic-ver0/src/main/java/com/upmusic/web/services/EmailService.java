package com.upmusic.web.services;

import org.springframework.mail.SimpleMailMessage;


public interface EmailService {
	
	void sendEmail(String to, String subject, String text);
	
	void sendEmailUsingTemplate(String to, String subject, SimpleMailMessage template, String ...templateArgs);
	
}
