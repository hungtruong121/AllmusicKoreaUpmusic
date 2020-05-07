package com.strawberryswing.upmusic.activity.video;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.util.player.video4.VideoControllerView;

import java.io.IOException;

import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_ADMIN_URL;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_ARTIST_NICK;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_ARTIST_URL;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_CREATED_AT;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_DESCRIPTION;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_DURATION;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_FILENAME;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_FILENAME_URL;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_GENRE_NAME;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_HEART_COUNT;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_HIT_COUNT;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_HOT_POINT;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_ID;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_LIKED;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_SUBJECT;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_THUMBNAIL;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_THUMBNAIL_URL;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_UPDATED_AT;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_URL;
import static com.strawberryswing.upmusic.activity.video.VideoPlaylistActivity.KEY_VIDEO_VIDEO_TYPE;

public class VideoPlayer2Activity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {

    final static String TEMP_VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private static final String TAG = "[VideoPlayerActivity] : " ;
    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    String URL ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_3);
        
        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this);


        Intent intent = getIntent();
        intent.getStringExtra(KEY_VIDEO_ADMIN_URL);
        intent.getStringExtra(KEY_VIDEO_ARTIST_NICK);
        intent.getStringExtra(KEY_VIDEO_ARTIST_URL);
        intent.getStringExtra(KEY_VIDEO_CREATED_AT);
        intent.getStringExtra(KEY_VIDEO_DESCRIPTION);
        intent.getStringExtra(KEY_VIDEO_DURATION);
        intent.getStringExtra(KEY_VIDEO_FILENAME);
        intent.getStringExtra(KEY_VIDEO_FILENAME_URL);
        intent.getStringExtra(KEY_VIDEO_GENRE_NAME);
        intent.getStringExtra(KEY_VIDEO_HEART_COUNT);
        intent.getStringExtra(KEY_VIDEO_HIT_COUNT);
        intent.getStringExtra(KEY_VIDEO_HOT_POINT);
        intent.getStringExtra(KEY_VIDEO_ID);
        intent.getStringExtra(KEY_VIDEO_LIKED);
        intent.getStringExtra(KEY_VIDEO_SUBJECT);
        intent.getStringExtra(KEY_VIDEO_THUMBNAIL);
        intent.getStringExtra(KEY_VIDEO_THUMBNAIL_URL);
        intent.getStringExtra(KEY_VIDEO_UPDATED_AT);
        intent.getStringExtra(KEY_VIDEO_URL);
        intent.getStringExtra(KEY_VIDEO_VIDEO_TYPE);


        URL = intent.getStringExtra(KEY_VIDEO_FILENAME_URL);

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(URL));
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.start();
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
        
    }
    // End VideoMediaController.MediaPlayerControl

}
