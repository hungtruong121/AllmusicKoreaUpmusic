package com.strawberryswing.upmusic.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.activity.MainActivity;
import com.strawberryswing.upmusic.util.E777SharedPreferences;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private E777SharedPreferences pref;

    private String title, message, url;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        pref = E777SharedPreferences.getInstance(getApplicationContext());

        if (remoteMessage == null) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("sjw push not working");
                }
            }, 0);
            return;
        }

        title = TextUtils.isEmpty(remoteMessage.getData().get("title")) ? getString(R.string.app_name) : remoteMessage.getData().get("title");
        message = TextUtils.isEmpty(remoteMessage.getData().get("body")) ? "" : remoteMessage.getData().get("body");
        url = TextUtils.isEmpty(remoteMessage.getData().get("url")) ? "" : remoteMessage.getData().get("url");

        sendNotification();
    }

    private void sendNotification() {
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        Notification noti = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelMessage = new NotificationChannel(getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notiMgr.createNotificationChannel(channelMessage);
            builder = new NotificationCompat.Builder(this, channelMessage.getId());
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.push_icon) // 작은 아이콘 세팅
                .setContentTitle(title) // 노티바에 표시될 타이틀
                .setContentText(message) // 노티바에 표시된 Description
                .setAutoCancel(true) // 클릭하게 되면 사라지도록...
                .setVibrate(new long[]{1000, 1000}) // 노티가등록될때진동패턴1초씩두번.
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // BigTextStyle
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); // 노티가등록될때사운드와진동이오도록,기본사운드가울리도록.

        builder.setPriority(Notification.PRIORITY_HIGH);

//        Intent intent = new Intent(this, IntroActivity.class);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        noti = builder.build();

        notiMgr.notify((int) System.currentTimeMillis(), noti);
    }
}