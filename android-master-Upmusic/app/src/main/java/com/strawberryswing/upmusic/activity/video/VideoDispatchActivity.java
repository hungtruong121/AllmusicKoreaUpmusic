package com.strawberryswing.upmusic.activity.video;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class VideoDispatchActivity extends Activity {

    public static Activity videoDispatchActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoDispatchActivity = VideoDispatchActivity.this;
    }
}
