package com.strawberryswing.upmusic.util.player.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;

public class MainActivity extends AppCompatActivity {
    Button openActivityBtn, openBigContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_audio);

        openActivityBtn = (Button) findViewById(R.id.btnNotificationActivity);
        openBigContent = (Button) findViewById(R.id.bigContent);


        openActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("[DO]", " : " +openActivityBtn );
                NotificationGenerator.openActivityNotification(getApplicationContext());
            }
        });

        openBigContent.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.e("[DO]", " : " +openBigContent );
                NotificationGenerator.customBigNotification(getApplicationContext());
            }
        });
    }
}
