package com.upmusic.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.MusicRequest;


@Component
public class MusicRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return MusicRequest.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		MusicRequest request = (MusicRequest) o;
		
		// 상세내용
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "common.required_field");
		String description = request.getDescription();
		if (description != null && !description.isEmpty()) {
			String descriptionString = description.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (descriptionString.isEmpty()) errors.rejectValue("description", "common.request.character");
		}
		
		// 가격
		String price = request.getPrice();
		if (price != null && !price.isEmpty()) {
			String priceNummeric = price.trim().replaceAll("[^0-9\\-\\s\\.]", "");
			if (0 == priceNummeric.length()) {
				errors.rejectValue("price", "model.music.request.price");
			} else if (10 < priceNummeric.length()) {
				errors.rejectValue("price", "model.request.size.price");
			}
		}
		
		// 가격협의
		boolean discussion = request.isDiscussion();
		if ((price == null || price.isEmpty()) && !discussion) {
			errors.rejectValue("price", "model.request.price_or_discussion");
		}

	}

}
