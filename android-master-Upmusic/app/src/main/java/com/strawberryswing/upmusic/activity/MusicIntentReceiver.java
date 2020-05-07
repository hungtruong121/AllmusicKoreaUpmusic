package com.strawberryswing.upmusic.activity;

import android.content.Context;
import android.content.Intent;

public class MusicIntentReceiver extends android.content.BroadcastReceiver {

    private AudioService mService;

    public MusicIntentReceiver(AudioService mService) {
        this.mService = mService;
    }

    @Override public void onReceive(Context ctx, Intent intent) {

        if (intent.getAction().equals( android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // 서비스에 intent 등으로 음악 재생을 중지하라고 전달
                mService.getMediaPlayer().pause();
            }
    }
}

