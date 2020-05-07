package com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.listener;

import com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.StompHeaders;

/**
 * 
 * @author 신동성
 * @since 2015. 11. 6.
 */
public interface SubscribeHandler {
	
	void onReceived(final Object payload, StompHeaders stompHeaders);

}
