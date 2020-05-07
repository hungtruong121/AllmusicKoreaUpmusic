package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.MusicTrackType;

public class MusicTrackTypeAttributeConverter implements AttributeConverter<MusicTrackType, Integer> {
	
	@Override
    public Integer convertToDatabaseColumn(MusicTrackType type) {
		Integer code = 0;
		switch(type) {
		case MR:
			code = 1;
			break;
		case AR:
			code = 2;
			break;
		case AR_GUIDE:
			code = 3;
			break;
		}
        return code;
    }
 
    @Override
    public MusicTrackType convertToEntityAttribute(Integer code) {
    	MusicTrackType type = MusicTrackType.MR;
		switch(code) {
		case 1:
			type = MusicTrackType.MR;
			break;
		case 2:
			type = MusicTrackType.AR;
			break;
		case 3:
			type = MusicTrackType.AR_GUIDE;
			break;
		}
        return type;
    }
}
