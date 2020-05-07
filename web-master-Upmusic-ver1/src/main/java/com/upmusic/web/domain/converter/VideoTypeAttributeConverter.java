package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.VideoType;

public class VideoTypeAttributeConverter implements AttributeConverter<VideoType, Integer> {
	
	@Override
    public Integer convertToDatabaseColumn(VideoType type) {
        return VideoType.MV.equals(type) ? 1 : 2;
    }
 
    @Override
    public VideoType convertToEntityAttribute(Integer code) {
        return 1 == code ? VideoType.MV : VideoType.GV;
    }
}
