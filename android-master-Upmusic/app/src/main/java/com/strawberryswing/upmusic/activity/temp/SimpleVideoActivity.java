package com.strawberryswing.upmusic.activity.temp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.video.BigstarVideoView;

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


/**
 * Created by bigstark on 15. 12. 21..
 */
public class SimpleVideoActivity extends Activity {
    private static final String SAMPLE_URL = "SAMPLE";

    private ViewGroup container;
    private BigstarVideoView videoView;
    String URL ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_2);
        container = (ViewGroup) findViewById(R.id.layout_container);

        videoView = (BigstarVideoView) findViewById(R.id.bigstar_video_view);

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

        videoView.setVideoURI(Uri.parse(URL), true);
        videoView.setRetainPlayerInstance(false);



        findViewById(R.id.btn_appear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.getParent() != null) {
                    return;
                }

                Log.v("TAG", "appear");
                container.addView(videoView, 0);
            }
        });

        findViewById(R.id.btn_disappear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.getParent() == null) {
                    return;
                }

                Log.v("TAG", "disappear");
                container.removeView(videoView);
            }
        });
    }
}
