package com.strawberryswing.upmusic.util.player.streaming_test;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.strawberryswing.upmusic.R;

public class main extends Activity {

	private Button streamButton;	
	private ImageButton playButton;	
	private TextView textStreamed;	
	private boolean isPlaying;
	
	private StreamingMediaPlayer audioStreamer;
	
    @Override
	public void onCreate(Bundle icicle) {
    	
        super.onCreate(icicle);

        setContentView(R.layout.activity_streaming);
        initControls();
    } 
    
    private void initControls() {
    	textStreamed = (TextView) findViewById(R.id.text_kb_streamed);
		streamButton = (Button) findViewById(R.id.button_stream);
		streamButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startStreamingAudio();
        }});

		playButton = (ImageButton) findViewById(R.id.button_play);
		playButton.setEnabled(false);
		playButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (audioStreamer.getMediaPlayer().isPlaying()) {
					audioStreamer.getMediaPlayer().pause();
					playButton.setImageResource(R.drawable.icon_play);
				} else {
					audioStreamer.getMediaPlayer().start();
					audioStreamer.startPlayProgressUpdater();
					playButton.setImageResource(R.drawable.icon_pause);
				}
				isPlaying = !isPlaying;
        }});
    }
    
    private void startStreamingAudio() {
    	try { 
    		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    		if ( audioStreamer != null) {
    			audioStreamer.interrupt();
    		}
    		audioStreamer = new StreamingMediaPlayer(this,textStreamed, playButton, streamButton,progressBar);
    		
    		//audioStreamer.startStreaming("http://www.pocketjourney.com/downloads/pj/tutorials/audio.mp3",1717, 214);
    		audioStreamer.startStreaming("http://internet.chonbuk.ac.kr/~songwei/DD/tellme.mp3",5208, 216);
    		streamButton.setEnabled(false);
    	} catch (IOException e) {
	    	Log.e(getClass().getName(), "Error starting to stream audio.", e);            		
    	}    	    	
    }
}