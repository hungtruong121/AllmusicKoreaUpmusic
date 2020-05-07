package com.upmusic.web.validator;

import com.upmusic.web.domain.Member;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class FamilyArtistValidator implements Validator {

	@Autowired
	private UserService userService;
	

	@Override
	public boolean supports(Class<?> aClass) {
		return Member.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Member user = (Member) o;

		// 이메일
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "common.required_field");
		if (!user.getEmail().isEmpty()) {
			if (!UPMusicHelper.validateEmail(user.getEmail())) {
				errors.rejectValue("email", "model.member.notmatch.email_regex");
			} else if (userService.findByEmail(user.getEmail()) != null) {
				errors.rejectValue("email", "model.member.duplicated.email");
			}
		}

		// 비밀번호
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "common.required_field");
		if (!user.getPassword().isEmpty()) {
			if (user.getPassword().length() < 8 || user.getPassword().length() > 50) {
				errors.rejectValue("password", "model.member.size.password");
			}
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "common.required_field");
		if (!user.getPassword().isEmpty() && !user.getPasswordConfirm().isEmpty()) {
			if (user.getPassword().isEmpty() || user.getPasswordConfirm().isEmpty() || !user.getPassword().equals(user.getPasswordConfirm())) {
				errors.rejectValue("passwordConfirm", "model.member.notmatch.password");
			}
		}
		
		// 닉네임
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nick", "common.required_field");
		String nick = user.getNick();
		if (nick != null && !nick.isEmpty()) {
			String nickString = nick.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (nickString.isEmpty()) {
				errors.rejectValue("nick", "common.request.character");
			} else if (8 < nick.length()) {
				errors.rejectValue("nick", "model.member.size.nick");
			}
			
		}
		
		// 생년월일
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "birthday", "common.request.cellphone.authentication");
		
		// 성별
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "genderNo", "common.request.cellphone.authentication");

		// 휴대폰번호
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "common.request.cellphone.authentication");
		if (!user.getPhoneNumber().isEmpty()) {
			try {
				if (userService.findByPhoneNumber(user.getPhoneNumber()) != null) {
					errors.rejectValue("phoneNumber", "model.member.duplicated.phone_number");
				}	
			} catch (Exception e) {
				errors.rejectValue("phoneNumber", "model.member.duplicated.phone_number");
			}
		}
	}

}
