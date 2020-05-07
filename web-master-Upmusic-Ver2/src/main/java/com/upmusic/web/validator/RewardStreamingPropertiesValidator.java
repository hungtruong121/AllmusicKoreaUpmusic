package com.upmusic.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.PointRewardCondition;


@Component
public class RewardStreamingPropertiesValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return PointRewardCondition.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
//		PointRewardCondition condition = (PointRewardCondition) o;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenFirstStepPoint", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenFirstStepLimit", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenSecondStepPoint", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenSecondStepLimit", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenThirdStepPoint", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenThirdStepLimit", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenArtistPoint", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenArtistSelfPoint", "해당 사항을 입력해주세요.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "listenArtistSelfLimit", "해당 사항을 입력해주세요.");

	}

}
