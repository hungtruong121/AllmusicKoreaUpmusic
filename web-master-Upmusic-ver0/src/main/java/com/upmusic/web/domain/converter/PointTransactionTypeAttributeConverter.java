package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.PointTransactionType;


public class PointTransactionTypeAttributeConverter implements AttributeConverter<PointTransactionType, Integer> {

	@Override
    public Integer convertToDatabaseColumn(PointTransactionType type) {
		Integer code = 1;
		switch(type) {
		case LISTEN:
			code = 1;
			break;
		case LISTEN_ARTIST:
			code = 2;
			break;
		case UPLOAD:
			code = 3;
			break;
		case SHARE:
			code = 4;
			break;
		case EVENT:
			code = 5;
			break;
		case HYC:
			code = 6;
			break;
	    case FUNDING:
			code = 7;
			break;
	    case LISTEN_ARTIST_SELF:
	    	code = 8;
	    	break;
		case CHARGE:
			code = 9;
			break;
		}
        return code;
    }
 
    @Override
    public PointTransactionType convertToEntityAttribute(Integer code) {
    	PointTransactionType type = PointTransactionType.LISTEN;
		switch(code) {
		case 1:
			type = PointTransactionType.LISTEN;
			break;
		case 2:
			type = PointTransactionType.LISTEN_ARTIST;
			break;
		case 3:
			type = PointTransactionType.UPLOAD;
			break;
		case 4:
			type = PointTransactionType.SHARE;
			break;
		case 5:
			type = PointTransactionType.EVENT;
			break;
		case 6:
			type = PointTransactionType.HYC;
			break;
		case 7:
			type = PointTransactionType.FUNDING;
			break;
		case 8:
			type = PointTransactionType.LISTEN_ARTIST_SELF;
			break;
		case 9:
			type = PointTransactionType.CHARGE;
			break;
		}
        return type;
    }
}
