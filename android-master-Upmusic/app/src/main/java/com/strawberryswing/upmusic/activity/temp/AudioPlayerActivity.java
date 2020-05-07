package com.strawberryswing.upmusic.activity.temp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.activity.MainActivity;
import com.strawberryswing.upmusic.util.image.BlurTransformation;

//TEMPORARY
public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView01, imageView02;
    private Button btnPlay, btnBack, btnFor;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;

    private Runnable runnable;
    private Handler handler;
    Handler updateHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_player);

        imageView01 = (ImageView) findViewById(R.id.imageView01);
        imageView02 = (ImageView) findViewById(R.id.imageView02);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnBack = (Button) findViewById(R.id.btnBackward);
        btnFor = (Button) findViewById(R.id.btnForward);
        seekBar = (SeekBar) findViewById(R.id.seekBar);


        handler = new Handler();
        mediaPlayer = new MediaPlayer();

        // LOCAL
        mediaPlayer = MediaPlayer.create(this, R.raw.play);

        // REMOTE

        Intent intent = getIntent();

        Log.d("RETROOOO :: : : " , "    " +

        String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_COVER_IMAGE_URL)) + " , \n" +
        String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_FILE_NAME_URL))

        );

//        Uri myUri = Uri.parse(String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_FILE_NAME_URL)));
//        myUri = Uri.parse("http://www.soundjay.com/transportation/sounds/car-door-open-1.mp3");
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(getApplicationContext(), myUri);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        Picasso.with(this)
                .load(String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_COVER_IMAGE_URL)))
                .into(imageView01);

        Picasso.with(this)
                .load(String.valueOf(intent.getStringExtra(MainActivity.KEY_TRACK_COVER_IMAGE_URL)))
                .transform(new BlurTransformation(this, 200))
                .into(imageView02);


        btnPlay.setOnClickListener(this);
        btnFor.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.start();
                long finalTime = mediaPlayer.getDuration();
//                TextView tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
//                tvTotalTime.setText(String.format("%d:%d",
//                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
//                );
                seekBar.setMax((int) finalTime);
                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);
                //Toast Box
                Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();



//                seekBar.setMax(mediaPlayer.getDuration());
//                mediaPlayer.start();
//                changeSeekbar();
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
//
//    private void changeSeekbar() {
//        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//
//        if (mediaPlayer.isPlaying()) {
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//                    changeSeekbar();
//                }
//            };
//            handler.postDelayed(runnable, 1000);
//        }
//    }

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
//                    changeSeekbar();
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


    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable(){
        public void run(){
            long currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);

        }
    };
}
