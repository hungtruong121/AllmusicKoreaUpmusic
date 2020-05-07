package com.upmusic.web.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.GuideVocal;


@Component
public class GuideVocalValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return GuideVocal.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		GuideVocal guide = (GuideVocal) o;
		
		// 나이
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "common.required_field");
		if (1 > guide.getAge()) {
			errors.rejectValue("age", "common.required_field");
		}
		
		// 음원파일
		if (StringUtils.isEmpty(guide.getFilelink()) && StringUtils.isEmpty(guide.getFilename())) {
			errors.rejectValue("filename", "common.required_guide_vocal_resource_field");
			errors.rejectValue("filelink", "common.required_guide_vocal_resource_field");
		}
		
		// 가능 장르
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "genre", "common.required_field");

		// 가이드 범위
		if (!guide.isGuideScopeChorus() && !guide.isGuideScopeGuide() && !guide.isGuideScopeRap()) {
			errors.rejectValue("vocalGuideScopes", "common.required_select_one");
		}
		
		// 비용
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cost", "common.required_field");
		if (1 > guide.getCost()) {
			errors.rejectValue("cost", "common.required_field");
		}
	}

}
