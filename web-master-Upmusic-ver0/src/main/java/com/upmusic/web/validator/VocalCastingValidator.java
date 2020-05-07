package com.upmusic.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.VocalCasting;


@Component
public class VocalCastingValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return VocalCasting.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		VocalCasting casting = (VocalCasting) o;
		
		// 제목
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "common.required_field");
		String subject = casting.getSubject();
		if (subject != null && !subject.isEmpty()) {
			String subjectString = subject.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (subjectString.isEmpty()) errors.rejectValue("subject", "common.request.character");
		}
		
		// 상세내용
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "common.required_field");
		String description = casting.getDescription();
		if (description != null && !description.isEmpty()) {
			String descriptionString = description.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (descriptionString.isEmpty()) errors.rejectValue("description", "common.request.character");
		}
		
		// 음원파일
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "filename", "common.required_field");

	}

}
