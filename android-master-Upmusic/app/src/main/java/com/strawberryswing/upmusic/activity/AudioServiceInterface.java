package com.strawberryswing.upmusic.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.strawberryswing.upmusic.model.MusicTrack;

import java.util.ArrayList;

public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private AudioService mService;

    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class).setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public int getPlaylistItemCount() {
        if (mService != null) {
            return mService.getPlaylistItemCount();
        }
        return 0;
    }

    public void setPlayList(ArrayList<MusicTrack> audioTracks , boolean positionSetted, boolean isFirstLoad) {
        if (mService != null) {
            mService.setPlayList(audioTracks,positionSetted, isFirstLoad);
        }
    }

    public void setmCurrentPosition(int position) {
        if (mService != null) {
            mService.setmCurrentPosition(position);
        }
    }


    public void play(int position) {
//        Log.e("AudioServiceInterface", "play! Item : " + position);
        if (mService != null) {
            mService.play(position);
        }
    }

    public void releaseMediaPlayer() {
        if (mService != null) {
            mService.releaseMediaPlayer();
            return;
        }
        return;
    }

    public boolean getIsFirstPlayed() {
        if (mService != null) {
            return mService.getIsFirstPlayed();
        }
        return false;
    }

    public void prepare() {
        if (mService != null) {
            mService.prepare();
        }
    }

    public void initMediaPlayer() {
        if (mService != null) {
            mService.initMediaPlayer();
        }
    }

    public ArrayList<MusicTrack> getAudioTracks() {
        if (mService != null) {
            return mService.unshuffledAudioTracks;
        }
        return null;
    }



    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            Log.e("TAG", "[togglePlay] pause");
            mService.pause();
        } else {
            Log.e("TAG", "[togglePlay] play");
            mService.play();
        }
    }


    public boolean isPrepared() {
        if (mService != null) {
            return mService.isPrepared();
        }
        return false;
    }


    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public MusicTrack getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }


    public void shuffleOnOff() {
        if (mService != null) {
            mService.shuffleOnOff();
        }
        return;
    }


    public void pause() {
        if (mService != null) {
            mService.pause();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public int getIndexOfTrack(MusicTrack track) {
        if (mService != null) {
            return mService.getIndexOfTrack(track);
        }
        return 0;
    }

    public MediaPlayer getMediaPlayer() {
        if (mService != null) {
            return mService.getMediaPlayer();
        }
        return  null;
    }


    public void removeNotificationPlayer() {
        if (mService != null) {
            mService.removeNotificationPlayer();
        }
    }


    public boolean getIsItemDownloaded() {
        if (mService != null) {
            return mService.getIsItemDownloaded();
        }
        return false;
    }



}