package com.strawberryswing.upmusic.util.player.local;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;

public class playerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlay, btnBack, btnFor;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_player);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnBack = (Button) findViewById(R.id.btnBackward);
        btnFor = (Button) findViewById(R.id.btnForward);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        handler = new Handler();

        mediaPlayer = MediaPlayer.create(this, R.raw.play);


        btnPlay.setOnClickListener(this);
        btnFor.setOnClickListener(this);
        btnBack.setOnClickListener(this);



        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                changeSeekbar();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changeSeekbar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnPlay :
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setText(">");
                } else {
                    mediaPlayer.start();
                    btnPlay.setText("||");
                    changeSeekbar();
                }
                break;

            case R.id.btnForward :
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                break;

            case R.id.btnBackward :
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                break;

        }



    }
}
