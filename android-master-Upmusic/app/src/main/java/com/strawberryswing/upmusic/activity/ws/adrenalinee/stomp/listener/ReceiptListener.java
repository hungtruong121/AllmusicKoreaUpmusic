package com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.listener;


import com.strawberryswing.upmusic.activity.ws.adrenalinee.stomp.frame.Frame;

/**
 * 
 * @author shindongseong
 * @since 2015. 11. 22.
 */
public interface ReceiptListener {
	
	void onReceived(Frame frame);
}
