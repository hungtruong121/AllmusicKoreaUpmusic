package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.MusicAlbum;


@Component
public class MusicAlbumValidator implements Validator {

	@Autowired
	private MessageSource messageSource;
	
	@Override
	public boolean supports(Class<?> aClass) {
		return MusicAlbum.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		MusicAlbum album = (MusicAlbum) o;
		
		// 제목
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", messageSource.getMessage("common.required_field", null, null));
		String subject = album.getSubject();
		if (subject != null && !subject.isEmpty()) {
			String subjectString = subject.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (subjectString.isEmpty()) errors.rejectValue("subject", messageSource.getMessage("common.request.character", null, null));
		}
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "imageFilename", messageSource.getMessage("common.required_field", null, null));
		
	}

}
