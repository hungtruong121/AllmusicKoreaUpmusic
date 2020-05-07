package com.strawberryswing.upmusic.util.player.video3;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;

public class MainActivity extends AppCompatActivity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_2);
 
        final VideoView videoView = findViewById(R.id.video_view);
//        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video;
        String videoPath =  "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";;
        Uri uri = Uri.parse(videoPath);

        videoView.setVideoURI(uri);
        videoView.requestFocus();
 
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

//        // 토스트 다이얼로그를 이용하여 버퍼링중임을 알린다.
//        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//
//                                        @Override
//                                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                                            switch(what){
//                                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                                                    // Progress Diaglog 출력
//
////                                                    btnPlay.setText(">");
//                                                    Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
//                                                    break;
//                                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                                                    // Progress Dialog 삭제
//                                                    Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
//                                                    videoView.start();
////                                                    btnPlay.setText("||");
//                                                    break;
//                                            }
//                                            return false;
//                                        }
//                                    }
//
//        );
    }
}