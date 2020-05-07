package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.Video;


@Component
public class VideoValidator implements Validator {

	@Autowired
	private MessageSource messageSource;
	
	@Override
	public boolean supports(Class<?> aClass) {
		return Video.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Video video = (Video) o;
		
		// 영상 종류
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "videoType", messageSource.getMessage("common.required_field", null, null));
		
		// 제목
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", messageSource.getMessage("common.required_field", null, null));
		String subject = video.getSubject();
		if (subject != null && !subject.isEmpty()) {
			String subjectString = subject.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (subjectString.isEmpty()) errors.rejectValue("subject", messageSource.getMessage("common.request.character", null, null));
		}
		
		// 영상 서비스
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "videoServiceObjectId", messageSource.getMessage("common.required_field", null, null));
		if (video.getVideoService() == null || video.getVideoService().isEmpty()) {
			errors.rejectValue("videoServiceObjectId", messageSource.getMessage("common.required_field", null, null));
		}
		if (video.getThumbnail()== null || video.getThumbnail().isEmpty()) {
			errors.rejectValue("videoServiceObjectId", messageSource.getMessage("common.required_field", null, null));
		}
		if (0 == video.getDuration()) {
			errors.rejectValue("videoServiceObjectId", messageSource.getMessage("common.required_field", null, null));
		}
		
	}

}
