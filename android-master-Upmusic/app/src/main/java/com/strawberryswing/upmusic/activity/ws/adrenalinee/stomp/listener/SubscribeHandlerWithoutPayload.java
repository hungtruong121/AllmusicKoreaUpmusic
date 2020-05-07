package com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.listener;

import com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.StompHeaders;

public interface SubscribeHandlerWithoutPayload {
	void onReceived(StompHeaders stompHeaders);
}
