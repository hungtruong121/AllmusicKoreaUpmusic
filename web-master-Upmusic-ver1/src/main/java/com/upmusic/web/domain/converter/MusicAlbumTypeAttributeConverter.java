package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.MusicAlbumType;

public class MusicAlbumTypeAttributeConverter implements AttributeConverter<MusicAlbumType, Integer> {
	
	@Override
    public Integer convertToDatabaseColumn(MusicAlbumType type) {
        return MusicAlbumType.SA.equals(type) ? 1 : 2;
    }
 
    @Override
    public MusicAlbumType convertToEntityAttribute(Integer code) {
        return 1 == code ? MusicAlbumType.SA : MusicAlbumType.GA;
    }
}
