package com.upmusic.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.upmusic.web.domain.LeagueSeason;
import com.upmusic.web.services.LeagueSeasonService;


@Component
public class LeagueSeasonValidator implements Validator {

	@Autowired
	private LeagueSeasonService leagueSeasonService;
	
	
	@Override
	public boolean supports(Class<?> aClass) {
		return LeagueSeason.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		LeagueSeason season = (LeagueSeason) o;
		
		// 제목
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "common.required_field");
		String subject = season.getSubject();
		if (subject != null && !subject.isEmpty()) {
			String subjectString = subject.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
			if (subjectString.isEmpty()) { 
				errors.rejectValue("subject", "common.request.character");
			} else {
				LeagueSeason oldSeason = leagueSeasonService.getLeagueSeasonBySubject(subject);
				if (oldSeason != null) errors.rejectValue("subject", "model.season.duplicated.subject");
			}
		}
		
		// 시작일
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "openDate", "common.required_field");
		
		// 종료일
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "closeDate", "common.required_field");
		
		// 기존 시즌의 편집인 경우에는 필수항목이 아님
		if (season.getId() == null) {
			// 이미지
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "imageMultipart", "common.required_field");
			if (season.getImageMultipart() == null || StringUtils.isEmpty(season.getImageMultipart().getOriginalFilename())) {
				season.setImageMultipart(null);
				errors.rejectValue("imageMultipart", "common.required_field");
			}
		}
	}

}
