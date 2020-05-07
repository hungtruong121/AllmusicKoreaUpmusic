package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.MusicCooperatorRole;

public class MusicAlbumCooperatorRoleAttributeConverter implements AttributeConverter<MusicCooperatorRole, Integer> {
	
	@Override
    public Integer convertToDatabaseColumn(MusicCooperatorRole role) {
		Integer code = 0;
		switch(role) {
		case COMPOSER:
			code = 1;
			break;
		case LYRICIST:
			code = 2;
			break;
		case ARRANGER:
			code = 3;
			break;
		case VOCALIST:
			code = 4;
			break;
		case MUSICIAN:
			code = 5;
			break;
		}
        return code;
    }
 
    @Override
    public MusicCooperatorRole convertToEntityAttribute(Integer code) {
    	MusicCooperatorRole role = MusicCooperatorRole.COMPOSER;
		switch(code) {
		case 1:
			role = MusicCooperatorRole.COMPOSER;
			break;
		case 2:
			role = MusicCooperatorRole.LYRICIST;
			break;
		case 3:
			role = MusicCooperatorRole.ARRANGER;
			break;
		case 4:
			role = MusicCooperatorRole.VOCALIST;
			break;
		case 5:
			role = MusicCooperatorRole.MUSICIAN;
		}
        return role;
    }
}
