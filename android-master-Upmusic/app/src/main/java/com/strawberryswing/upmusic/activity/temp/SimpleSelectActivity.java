package com.strawberryswing.upmusic.activity.temp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.activity.video.VideoPlayerActivity;

/**
 * Created by bigstark on 15. 12. 21..
 */
public class SimpleSelectActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select);

    findViewById(R.id.btn_player).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(SimpleSelectActivity.this, VideoPlayerActivity.class));
      }
    });

    findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(SimpleSelectActivity.this, SimpleVideoActivity.class));
      }
    });
  }
}
