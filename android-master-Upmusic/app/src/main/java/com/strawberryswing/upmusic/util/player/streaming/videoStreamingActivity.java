package com.strawberryswing.upmusic.util.player.streaming;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;

import java.util.concurrent.TimeUnit;


public class videoStreamingActivity extends AppCompatActivity implements View.OnClickListener {

    final static String SAMPLE_VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //https://www.youtube.com/watch?v=KQIsk0ba5q0
    VideoView videoView;
    SeekBar seekBar;
    Handler updateHandler = new Handler();


    private ImageView imageView01, imageView02;
    private TextView textView01, textView02;
    private Button btnPlay, btnBack, btnFor;
    private MediaPlayer mediaPlayer;

    private Runnable runnable;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
//        EditText tvURL = (EditText)findViewById(R.id.etVieoURL);
//        tvURL.setText(SAMPLE_VIDEO_URL);

        imageView01 = (ImageView) findViewById(R.id.imageView01);
        imageView02 = (ImageView) findViewById(R.id.imageView02);
        textView01 = (TextView) findViewById(R.id.textView01);
        textView02 = (TextView) findViewById(R.id.textView02);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnBack = (Button) findViewById(R.id.btnBackward);
        btnFor = (Button) findViewById(R.id.btnForward);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        videoView = (VideoView)findViewById(R.id.videoView);
        //MediaController mc = new MediaController(this);
        //videoView.setMediaController(mc);

        seekBar = (SeekBar)findViewById(R.id.seekBar);

        loadVideo();
        btnPlay.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadVideo() {
        //Sample video URL : http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4
        String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        Toast.makeText(getApplicationContext(), "Loading Video. Plz wait", Toast.LENGTH_LONG).show();
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();

        // 토스트 다이얼로그를 이용하여 버퍼링중임을 알린다.
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch(what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        // Progress Diaglog 출력

                        btnPlay.setText(">");
                        Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        // Progress Dialog 삭제
                        Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
                        videoView.start();
                        btnPlay.setText("||");
                        break;
                }
                return false;
            }
        }

        );

        // 플레이 준비가 되면, seekBar와 PlayTime을 세팅하고 플레이를 한다.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                long finalTime = videoView.getDuration();
                textView01 = (TextView) findViewById(R.id.textView01);
                textView01.setText(String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );
                seekBar.setMax((int) finalTime);
                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);
                //Toast Box
                Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void playVideo(){
        videoView.requestFocus();
        videoView.start();

    }

    public void pauseVideo(){
        videoView.pause();
    }

    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable(){
        public void run(){
            long currentPosition = videoView.getCurrentPosition();
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);

        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnPlay :

                if (videoView.isPlaying()) {
                    pauseVideo();
//                    videoView.pause();
                    btnPlay.setText(">");
                } else {
                    playVideo();
//                    videoView.start();
                    btnPlay.setText("||");
                }
                break;
        }

    }
}
