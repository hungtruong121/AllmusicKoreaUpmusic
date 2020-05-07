package com.strawberryswing.upmusic.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.strawberryswing.upmusic.R;

public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
    AudioManager mAudioManager;
    private AudioService mService;
    Context mContext;
    MediaPlayer mMediaPlayer;

    public AudioFocusHelper(Context ctx, AudioService service) {
        mContext = ctx;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mService = service;
        mMediaPlayer = mService.getMediaPlayer();
        // ...

        AudioManager audioManager = (AudioManager) mService.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // 오디오 포커스를 획득할 수 없다.
            Toast.makeText(ctx, "[UPMUSIC] 다른 앱에서 음악이 재생되어서 정지를 하려 했으나, 기기에서 오류가 발생했어요.", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager
                .requestAudioFocus((AudioManager.OnAudioFocusChangeListener) mContext, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }

    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
//
//
//
//        Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
//        Intent actionForward = new Intent(CommandActions.FORWARD);
//        Intent actionRewind = new Intent(CommandActions.REWIND);
//        Intent actionClose = new Intent(CommandActions.CLOSE);
//
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // 다시 재생
                Log.e("[AudioManager] : ", "AudioManager.AUDIOFOCUS_GAIN");
                if (mMediaPlayer == null) {
//                    initMediaPlayer();

                } else if (!mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.e("[AudioManager] : ", "AudioManager.AUDIOFOCUS_LOSS");
                // 포커스를 아주 장기적으로 잃었다. 음악 재생을 멈추고, 미디어 플레이어 자원도 해제
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.e("[AudioManager] : ", "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT");
                // 잠시 포커스를 잃었지만, 음악을 중단해야 한다. 하지만 포커스가 금방 돌아올 것으로 예상하기 떄문에 미디어 플레이어는 해제 하지 않는다
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e("[AudioManager] : ", "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // 잠시 포커스를 잃었지만, 볼륨을 줄인 상태에서 음악 재생을 유지해도 된다.
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }


    }
}
