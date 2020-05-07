package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.PasswordForgot;
import com.upmusic.web.domain.AuthenticationEmail;
import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.UserService;


@Component
public class PasswordForgotValidator implements Validator {

	@Autowired
	private UserService userService;
	

	@Override
	public boolean supports(Class<?> aClass) {
		return PasswordForgot.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		PasswordForgot passwordForgot = (PasswordForgot) o;
		
		/*
		 * 인증 전과 후 업데이트 파트
		 */
		if (!passwordForgot.isAuthenticated()) {
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "common.required_field");
			Member member = userService.findByEmail(passwordForgot.getEmail());
			if (!passwordForgot.getEmail().isEmpty()) {
				if (!UPMusicHelper.validateEmail(passwordForgot.getEmail())) {
					errors.rejectValue("email", "model.member.notmatch.email_regex");
				} else if (member == null) {
					errors.rejectValue("email", "model.member.notmatch.email");
				}
			}
			
			// 휴대폰번호 인증
			if (passwordForgot.isPhoneAuthentication()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "model.member.notsend.authentication_phone_code");
				String phoneNumber = passwordForgot.getPhoneNumber();
				if (!phoneNumber.isEmpty()) {
					phoneNumber = phoneNumber.replaceAll("[^\\d]", "" );
					if (phoneNumber.length() < 10 || phoneNumber.length() > 13) {
						errors.rejectValue("phoneNumber", "model.member.size.phone_number");
					} else if (!member.getPhoneNumber().equals(phoneNumber)) {
						errors.rejectValue("phoneNumber", "model.member.notmatch.phone_number");
					}
				}
			
			// 이메일 인증
			} else {
				// 이메일
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "authenticationCode", "common.required_field");
				if (!passwordForgot.getEmail().isEmpty()) {
					AuthenticationEmail authenticationEmail = userService.findAuthenticationEmailByEmail(passwordForgot.getEmail());
					if (authenticationEmail == null || authenticationEmail.getAuthenticationCode().isEmpty()) {
						errors.rejectValue("email", "model.member.notsend.authentication_code");
					}
					// 인증번호
					if (!passwordForgot.getAuthenticationCode().isEmpty()) {
						if (!authenticationEmail.getAuthenticationCode().equals(passwordForgot.getAuthenticationCode())) {
							errors.rejectValue("authenticationCode", "model.member.notmatch.authentication_code");
						}
					}
				}
			}
		} else {
			// 비밀번호
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "common.required_field");
			if (!passwordForgot.getNewPassword().isEmpty()) {
				if (passwordForgot.getNewPassword().length() < 8 || passwordForgot.getNewPassword().length() > 50) {
					errors.rejectValue("newPassword", "model.member.size.password");
				}
			}
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswordConfirm", "common.required_field");
			if (!passwordForgot.getNewPassword().isEmpty() && !passwordForgot.getNewPasswordConfirm().isEmpty()) {
				if (passwordForgot.getNewPassword().isEmpty() || passwordForgot.getNewPasswordConfirm().isEmpty() || !passwordForgot.getNewPassword().equals(passwordForgot.getNewPasswordConfirm())) {
					errors.rejectValue("newPasswordConfirm", "model.member.notmatch.password");
				}
			}
		}
		
	}

}
