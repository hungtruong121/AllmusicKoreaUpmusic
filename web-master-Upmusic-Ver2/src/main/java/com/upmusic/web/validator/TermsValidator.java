package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.Terms;
import com.upmusic.web.domain.Video;


@Component
public class TermsValidator implements Validator {

	@Autowired
	private MessageSource messageSource;
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Video.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Terms terms = (Terms) o;
		
		// 제목
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", messageSource.getMessage("common.required_field", null, null));
		String subject = terms.getSubject();
		if (subject != null && !subject.isEmpty()) {
			String subjectString = subject.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (subjectString.isEmpty()) errors.rejectValue("subject", messageSource.getMessage("common.request.character", null, null));
		}
		
		// 내용
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", messageSource.getMessage("common.required_field", null, null));
		
	}

}
