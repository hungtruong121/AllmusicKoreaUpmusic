package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.ChargeType;

public class ChargeTypeConverter implements AttributeConverter<ChargeType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ChargeType type) {
        Integer code = 1;
        switch(type) {
            case CREDIT:
                code = 1;
                break;
            case MOBILE:
                code = 2;
                break;
            case ACCOUNT:
                code = 3;
                break;
        }
        return code;
    }

    @Override
    public ChargeType convertToEntityAttribute(Integer code) {
        ChargeType type = ChargeType.CREDIT;
        switch(code) {
            case 1:
                type = ChargeType.CREDIT;
                break;
            case 2:
                type = ChargeType.MOBILE;
                break;
            case 3:
                type = ChargeType.ACCOUNT;
                break;
        }
        return type;
    }
}
