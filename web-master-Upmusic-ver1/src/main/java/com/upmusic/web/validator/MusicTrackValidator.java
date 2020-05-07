package com.upmusic.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.MusicTrack;


@Component
public class MusicTrackValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return MusicTrack.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		MusicTrack track = (MusicTrack) o;
		
		// 제목
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "common.required_field");
		String subject = track.getSubject();
		if (subject != null && !subject.isEmpty()) {
			String subjectString = subject.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (subjectString.isEmpty()) errors.rejectValue("subject", "common.request.character");
		}
		
		// 뮤직스토어에 등록할 경우 가격이 필수항목 - 별도상담 처리
		if (track.isInStore()) {
//			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rentalFee", "common.required_field");
//			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "common.required_field");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "extraSource", "common.required_extraSource");
		}

		if(track.isInCopyrighter()){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyrighterName","common.required_field");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyrighterCode","common.required_field");
		}
		
		// 음원파일
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "filename", "common.required_field");
		
	}

}
