package com.strawberryswing.upmusic.util.player.streaming;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;

import java.util.concurrent.TimeUnit;

public class mp3StreamingActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {


    private ImageView imageView01, imageView02;
    private TextView textView01, textView02;
    private Button btnPlay, btnBack, btnFor;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;

    private Runnable runnable;
    private Handler handler;

    private int realtimeLength;
    private int mediaFileLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_player);

        imageView01 = (ImageView) findViewById(R.id.imageView01);
        imageView02 = (ImageView) findViewById(R.id.imageView02);
        textView01 = (TextView) findViewById(R.id.textView01);
        textView02 = (TextView) findViewById(R.id.textView02);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnBack = (Button) findViewById(R.id.btnBackward);
        btnFor = (Button) findViewById(R.id.btnForward);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        mediaPlayer = new MediaPlayer();

        seekBar.setMax(99); // 100% : 0~99
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(mediaPlayer.isPlaying()) {
                    SeekBar seekBar = (SeekBar) view;
                    int playPosition = (mediaFileLength/100) * seekBar.getProgress();
                    mediaPlayer.seekTo(playPosition);
                }

                return false;
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<String,String,String> mp3Play = new AsyncTask<String, String, String>() {

                    ProgressDialog mDialog = new ProgressDialog(mp3StreamingActivity.this);

                    @Override
                    protected void onPreExecute() {
                        mDialog.setMessage("Please Wait.....");
                        mDialog.show();
                    }

                    @Override
                    protected String doInBackground(String... strings) {

                        try {
                            mediaPlayer.setDataSource(strings[0]);
                            mediaPlayer.prepare();
                        } catch (Exception ex) {

                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();
                        mediaPlayer.start();
                    }
                };

                mp3Play.execute("http://www.soundjay.com/transportation/sounds/car-door-open-1.mp3"); // direct link for mp3 file
            }
        });


        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }


    private void updateSeekBar() {

        seekBar.setProgress((int)((float)mediaPlayer.getCurrentPosition() / mediaFileLength) * 100);

        if(mediaPlayer.isPlaying()) {
            Runnable updater = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                    realtimeLength -= 1000;
                    textView01.setText(String.format("%d %d", TimeUnit.MILLISECONDS.toMinutes(realtimeLength),
                            TimeUnit.MILLISECONDS.toSeconds(realtimeLength) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realtimeLength))));

                }
            };
            handler.postDelayed(updater,1000); // 1 sec
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        seekBar.setSecondaryProgress(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        btnPlay.setText(">");

    }
}
