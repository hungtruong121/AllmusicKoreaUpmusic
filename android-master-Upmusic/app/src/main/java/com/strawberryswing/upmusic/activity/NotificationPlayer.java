package com.strawberryswing.upmusic.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.squareup.picasso.Picasso;
import com.strawberryswing.upmusic.R;

import java.io.IOException;

import static android.app.Service.START_STICKY;


public class NotificationPlayer {


    private static final String CHANNEL_ID = "com.strawberryswing.upmusic.MUSIC_CHANNEL_ID";
    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private AudioService mService;
    private NotificationManager mNotificationManager;
    private NotificationManagerBuilder mNotificationManagerBuilder;
    private boolean isForeground;
    String channelId = "upmusic_service";
    String channelName = "Upmusic Service";

    public NotificationPlayer(AudioService service) {
        mService = service;
        mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);

            mChannel.setDescription(channelName);
            mChannel.enableLights(false);
            mChannel.enableVibration(false);
            mChannel.setLightColor(Color.BLUE);
            mChannel.setLockscreenVisibility(Notification.DEFAULT_ALL);
            mChannel.setVibrationPattern(new long[]{0});
            mChannel.setShowBadge(false);
            mChannel.setSound(null,null);
            mNotificationManager.createNotificationChannel(mChannel);

        }


//         Notification channels are only supported on Android O+.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel();
//        }
    }


    public void removeNotificationPlayer() {
        cancel();
        mService.stopForeground(true);
        isForeground = false;
    }

    private void cancel() {
        if (mNotificationManagerBuilder != null) {
            mNotificationManagerBuilder.cancel(true);
            mNotificationManagerBuilder = null;
        }
    }

    private class NotificationManagerBuilder extends AsyncTask<Void, Void, Notification> {
        private RemoteViews mRemoteViews;
        private NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendingIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainActivity = new Intent(mService, MainActivity.class);
//            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            mMainPendingIntent = PendingIntent.getService(mService, 0, mainActivity, 0);
            mRemoteViews = createRemoteView(R.layout.content_player_notification);
            mNotificationBuilder = new NotificationCompat.Builder(mService, CHANNEL_ID);
//                    .setChannelId(CHANNEL_ID); // MUST CONTAIN CHANNEL ID

            mNotificationBuilder.setSmallIcon(R.drawable.logo)
                    .setOngoing(true)
                    .setContentIntent(mMainPendingIntent)
                    .setContent(mRemoteViews);
            Notification notification = mNotificationBuilder.build();
//            notification.priority = Notification.PRIORITY_MAX;
            notification.priority = Notification.PRIORITY_HIGH;
//            notification.priority = Notification.PRIORITY_DEFAULT;
            notification.contentIntent = mMainPendingIntent;

            if (!isForeground) {
                isForeground = true;
                // 서비스를 Foreground 상태로 만든다
                mService.startForeground(NOTIFICATION_PLAYER_ID, notification);
            }
        }

        @Override
        protected Notification doInBackground(Void... params) {
            mNotificationBuilder.setContent(mRemoteViews);
            mNotificationBuilder.setContentIntent(mMainPendingIntent);
//            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);/**/
            mNotificationBuilder.setPriority(Notification.PRIORITY_HIGH);
//            mNotificationBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(mRemoteViews, notification);
            return notification;
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private RemoteViews createRemoteView(int layoutId) {
            RemoteViews remoteView = new RemoteViews(mService.getPackageName(), layoutId);
            Intent actionTogglePlay = new Intent(CommandActions.TOGGLEPLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSING);

            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);

            remoteView.setOnClickPendingIntent(R.id.btn_noti_play, togglePlay);
            remoteView.setOnClickPendingIntent(R.id.btn_noti_next, forward);
            remoteView.setOnClickPendingIntent(R.id.btn_noti_prev, rewind);
            remoteView.setOnClickPendingIntent(R.id.btn_noti_close, close);
            return remoteView;
        }

        private void updateRemoteView(RemoteViews remoteViews, Notification notification) {
            if (mService.isPlaying()) {
                remoteViews.setImageViewResource(R.id.btn_noti_play, R.drawable.uamp_ic_pause_white_24dp);
            } else {
                remoteViews.setImageViewResource(R.id.btn_noti_play, R.drawable.uamp_ic_play_arrow_white_24dp);
            }

            if(mService.getAudioItem() != null) {

                String title = mService.getAudioItem().getSubject();
                String artist = mService.getAudioItem().getArtistNick();

                remoteViews.setTextViewText(R.id.txt_noti_01, title);
                remoteViews.setTextViewText(R.id.txt_noti_02, artist);

//            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), mService.getAudioItem().getCoverImageUrl());
//                Picasso.with(mService)
//                        .load(mService.getAudioItem().getCoverImageUrl())
//                        .error(null)
//                        .into(remoteViews, R.id.imageView_noti, NOTIFICATION_PLAYER_ID, notification);

            } else {
                String title = "";
                String artist ="";

                remoteViews.setTextViewText(R.id.txt_noti_01, title);
                remoteViews.setTextViewText(R.id.txt_noti_02, artist);
            }

        }


//        private void newControlView() {
//            Intent actionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
//            Intent actionForward = new Intent(CommandActions.FORWARD);
//            Intent actionRewind = new Intent(CommandActions.REWIND);
//            Intent actionClose = new Intent(CommandActions.CLOSE);
//            PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
//            PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
//            PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
//            PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);
//
//
//
//            Notification notification = new NotificationCompat.Builder(mService)
//                    // Show controls on lock screen even when user hides sensitive content.
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                    .setSmallIcon(R.drawable.ic_launcher)
//                    // Add media control buttons that invoke intents in your media service
//                    .addAction(R.drawable.player_play_02, "Previous", rewind) // #0
//                    .addAction(R.drawable.player_play_01, "Pause", togglePlay)  // #1
//                    .addAction(R.drawable.player_play_03, "Next", forward)     // #2
//                    // Apply the media style template
//                    .setStyle(new NotificationCompat.MediaStyle()
//                            .setShowActionsInCompactView(1 /* #1: pause button */)
//                            .setMediaSession(mMediaSession.getSessionToken()))
//                    .setContentTitle("Wonderful music")
//                    .setContentText("My Awesome Band")
//                    .setLargeIcon(albumArtBitmap)
//                    .build();
//        }

    }

    public void updateNotificationPlayer() {

        // VER1
//        cancel();
//        mNotificationManagerBuilder = new NotificationManagerBuilder();
//        mNotificationManagerBuilder.execute();

        // VER2
        new AsyncTask<Void, Void, Void>() {




            @Override
            protected Void doInBackground(Void... params) {

                Bitmap largIcon = null;
                try {
                    largIcon = Picasso.with(mService).load(mService.getAudioItem().getCoverImageUrl()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent actionTogglePlay = new Intent(CommandActions.TOGGLEPLAY);
                Intent actionForward = new Intent(CommandActions.FORWARD);
                Intent actionRewind = new Intent(CommandActions.REWIND);
                Intent actionClose = new Intent(CommandActions.CLOSING);

                PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
                PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
                PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
                PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);

                Intent mainActityCall = new Intent(mService, MainActivity.class);
                mainActityCall.addFlags(
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, CHANNEL_ID);

                builder.setContentTitle(mService.getAudioItem().getSubject())
                        .setContentText(mService.getAudioItem().getArtistNick())
                        .setLargeIcon(largIcon)
                        .setContentIntent(PendingIntent.getActivity(
                                mService
                                , 0
                                , mainActityCall
                                , 0
                                )
                        )
//                        .setPriority( Notification.PRIORITY_MAX)
                        .setPriority( Notification.PRIORITY_DEFAULT)
//                        .setPriority( Notification.PRIORITY_DEFAULT)
                ;

                builder.addAction(new NotificationCompat.Action(R.drawable.music_play_02, "", rewind));
                builder.addAction(new NotificationCompat.Action(mService.isPlaying() ? R.drawable.uamp_ic_pause_white_24dp : R.drawable.uamp_ic_play_arrow_white_24dp, "", togglePlay));
                builder.addAction(new NotificationCompat.Action(R.drawable.music_play_03, "", forward));
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp, "", close));

                //setShowActionsInCompactView 메소드를 이용해서 최대 3개의 action (버튼)을 사용자에게 노출할 수 있게
                int[] actionsViewIndexs = new int[]{0,1,2};
                builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setCancelButtonIntent(close)
                        .setShowActionsInCompactView(actionsViewIndexs)
                        .setShowCancelButton(true)

                )
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setOnlyAlertOnce(true); //


                builder.setSmallIcon(R.drawable.logo);
                builder.setVibrate(null);
                builder.setSound(null);

                Notification notification = builder.build();
                NotificationManagerCompat.from(mService).notify(NOTIFICATION_PLAYER_ID, notification);

                if (!isForeground) {
                    isForeground = true;
                    // 서비스를 Foreground 상태로 만든다
                    mService.startForeground(NOTIFICATION_PLAYER_ID, notification);
                }

                return null;
            }
        }.execute();
    }


    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {


        Log.e("[ee]", "[createNotificationChannel]");
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
//            NotificationChannel notificationChannel =
//                    new NotificationChannel(channelId,
//                            mService.getString(R.string.notification_channel),
//                            NotificationManager.IMPORTANCE_LOW);

//
//            notificationChannel.setDescription(
//                    mService.getString(R.string.notification_channel_description));
//            mNotificationManager.createNotificationChannel(notificationChannel);
//
//
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(channelName);
            mChannel.enableLights(false);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(false);
            mChannel.setImportance(Notification.PRIORITY_DEFAULT);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
//            mChannel.setBypassDnd();
            mNotificationManager.createNotificationChannel(mChannel);

        }
    }



}
