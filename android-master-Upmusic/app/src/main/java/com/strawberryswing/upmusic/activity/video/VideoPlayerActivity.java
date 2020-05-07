package com.strawberryswing.upmusic.activity.video;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.video.BigstarPlayerView;
import com.strawberryswing.upmusic.video.ErrorReason;
import com.strawberryswing.upmusic.video.OnPlayStateChangedListener;
import com.strawberryswing.upmusic.video.OnPlaybackEventListener;

import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_ADMIN_URL;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_ARTIST_NICK;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_ARTIST_URL;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_CREATED_AT;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_DESCRIPTION;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_DURATION;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_FILENAME;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_FILENAME_URL;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_GENRE_NAME;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_HEART_COUNT;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_HIT_COUNT;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_HOT_POINT;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_ID;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_LIKED;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_SUBJECT;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_THUMBNAIL;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_THUMBNAIL_URL;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_UPDATED_AT;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_URL;
import static com.strawberryswing.upmusic.activity.MainActivity.KEY_VIDEO_VIDEO_TYPE;

public class VideoPlayerActivity extends Activity {
    private static final String SAMPLE_URL = "https://walterebert.com/playground/video/hls/sintel-trailer.m3u8";

    private final float VIDEO_RATIO = (float) 9 / (float) 16;
    private final String KEY_CURRENT_POSITION = "CurrentPosition";

    private boolean isFullScreen = false;
    private boolean isRestore = false;

    private BigstarPlayerView playerView;
    String URL ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // FOR DISPATCHER's DISMISS
        VideoDispatchActivity videoDispatchActivity = (VideoDispatchActivity) VideoDispatchActivity.videoDispatchActivity;

        initVideo();

        if (savedInstanceState != null) {
            int currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            playerView.pause();
            playerView.seekTo(currentPosition);
            isRestore = true;
        }
    }


    private void initVideo() {
        playerView = (BigstarPlayerView) findViewById(R.id.bigstar_player_view);
//        playerView.setFullscreen(true);
        playerView.setOnPlayStateChangedListener(new OnPlayStateChangedListener() {
            @Override
            public void onPrepared() {
                Log.v("TAG", "onPrepared");
            }

            @Override
            public void onError(ErrorReason errorReason) {
                Log.v("TAG", "onError");
            }

            @Override
            public void onCompletion() {
                Log.v("TAG", "onCompletion");
            }

            @Override
            public void onReleased() {
                Log.v("TAG", "onReleased");
            }
        });
        playerView.setOnPlaybackEventListener(new OnPlaybackEventListener() {
            @Override
            public void onPlaying() {
                Log.v("TAG", "onPlaying");
            }

            @Override
            public void onPaused() {
                Log.v("TAG", "onPaused");
            }

            @Override
            public void onStopped() {
                Log.v("TAG", "onStopped");
            }

            @Override
            public void onPositionChanged(int i) {
//                Log.v("TAG", "onPosition Changed to " + i);
            }

            @Override
            public void onBufferingUpdate(int i) {
            }

            @Override
            public void onSeekComplete() {
                Log.v("TAG", "onSeekComplete");
            }
        });

//    DisplayMetrics metrics = new DisplayMetrics();
//    getWindowManager().getDefaultDisplay().getMetrics(metrics);
//    playerView.setVideoHeight((int) (metrics.widthPixels * VIDEO_RATIO));
//    playerView.setOnFullScreenListener(new BigstarPlayerView.OnFullScreenListener() {
//
//      @Override
//      public void onFullScreen(boolean isFullScreen) {
//        VideoPlayerActivity.this.isFullScreen = isFullScreen;
//      }
//    });
//    playerView.setOnPrepareCompleteListener(new BigstarPlayerView.OnPrepareCompleteListener() {
//
//      @Override
//      public void onPrepareComplete() {
//        if (isRestore) {
//          return;
//        }
//        playerView.start();
//      }
//    });
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

        if (URL.equals("")) {
            URL = SAMPLE_URL;
        }

        Uri vidUri = Uri.parse(URL);
        playerView.setVideoURI(vidUri, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_POSITION, playerView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }


}
