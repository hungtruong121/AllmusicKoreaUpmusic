package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.Login;
import com.upmusic.web.services.SecurityService;


@Component
public class LoginValidator implements Validator {

	@Autowired
    private SecurityService securityService;
	
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Login.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Login login = (Login) o;

		if (!securityService.login(login.getEmail(), login.getPassword())) {
			errors.rejectValue("email", "common.login.error");
		}
		
	}

}
