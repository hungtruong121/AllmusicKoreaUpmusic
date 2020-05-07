package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.Member;


@Component
public class MemberValidator implements Validator {

	@Autowired
	private MessageSource messageSource;
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Member.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Member user = (Member) o;

		// 닉네임
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nick", messageSource.getMessage("common.required_field", null, null));
		String nick = user.getNick();
		if (nick != null && !nick.isEmpty()) {
			String nickString = nick.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (nickString.isEmpty()) {
				errors.rejectValue("nick", messageSource.getMessage("common.request.character", null, null));
			} else if (8 < nick.length()) {
				errors.rejectValue("nick", messageSource.getMessage("model.member.size.nick", null, null));
			}
			
		}
		
		// 프로필 이미지
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "profileImage", messageSource.getMessage("common.required_field", null, null));
		
	}

}
