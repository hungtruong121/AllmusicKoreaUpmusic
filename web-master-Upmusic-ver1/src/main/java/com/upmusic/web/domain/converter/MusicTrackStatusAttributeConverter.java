package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.MusicTrackStatus;


public class MusicTrackStatusAttributeConverter implements AttributeConverter<MusicTrackStatus, Integer> {

	@Override
    public Integer convertToDatabaseColumn(MusicTrackStatus status) {
		Integer code = 0;
		switch(status) {
		case BEFORE_EXAM:
			code = 1;
			break;
		case UNDER_EXAM:
			code = 2;
			break;
		case ACCEPTED:
			code = 3;
			break;
		case REJECTED:
			code = 4;
			break;
		}
        return code;
    }
 
    @Override
    public MusicTrackStatus convertToEntityAttribute(Integer code) {
    	MusicTrackStatus type = MusicTrackStatus.BEFORE_EXAM;
		switch(code) {
		case 1:
			type = MusicTrackStatus.BEFORE_EXAM;
			break;
		case 2:
			type = MusicTrackStatus.UNDER_EXAM;
			break;
		case 3:
			type = MusicTrackStatus.ACCEPTED;
			break;
		case 4:
			type = MusicTrackStatus.REJECTED;
			break;
		}
        return type;
    }
}
