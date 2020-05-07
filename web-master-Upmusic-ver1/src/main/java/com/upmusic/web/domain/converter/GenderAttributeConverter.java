package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.Gender;

public class GenderAttributeConverter implements AttributeConverter<Gender, Integer> {
	
	@Override
    public Integer convertToDatabaseColumn(Gender gender) {
        return Gender.MALE.equals(gender) ? 1 : 2;
    }
 
    @Override
    public Gender convertToEntityAttribute(Integer code) {
        return 1 == code ? Gender.MALE : Gender.FEMALE;
    }
}
