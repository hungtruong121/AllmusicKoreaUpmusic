package com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.listener;

import com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.frame.Frame;

/**
 * 
 * @author shindongseong
 * @since 2015. 11. 7.
 */
public interface ErrorListener {
	
	void onError(final Frame frame);
}
