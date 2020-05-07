package com.upmusic.web.domain.converter;

import javax.persistence.AttributeConverter;

import com.upmusic.web.config.UPMusicConstants.StoreOrderStatus;


public class StoreOrderStatusAttributeConverter implements AttributeConverter<StoreOrderStatus, Integer> {

	@Override
    public Integer convertToDatabaseColumn(StoreOrderStatus status) {
		Integer code = 1;
		switch(status) {
		case PREPARE:
			code = 1;
			break;
		case CANCELED:
			code = 2;
			break;
		case PAY:
			code = 3;
			break;
		case COMPLETED:
			code = 4;
			break;
		case REFUNDED:
			code = 5;
			break;
		}
        return code;
    }
 
    @Override
    public StoreOrderStatus convertToEntityAttribute(Integer code) {
    	StoreOrderStatus status = StoreOrderStatus.PREPARE;
		switch(code) {
		case 1:
			status = StoreOrderStatus.PREPARE;
			break;
		case 2:
			status = StoreOrderStatus.CANCELED;
			break;
		case 3:
			status = StoreOrderStatus.PAY;
			break;
		case 4:
			status = StoreOrderStatus.COMPLETED;
			break;
		case 5:
			status = StoreOrderStatus.REFUNDED;
			break;
		}
        return status;
    }
}
