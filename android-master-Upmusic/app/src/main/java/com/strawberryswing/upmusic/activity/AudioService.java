package com.strawberryswing.upmusic.activity;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.strawberryswing.upmusic.model.Collection;
import com.strawberryswing.upmusic.model.MusicTrack;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class AudioService extends Service implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "[ AudioService ]";

    private final IBinder mBinder = new AudioServiceBinder();

    ArrayList<MusicTrack> mAudioTracks = new ArrayList<>();
    ArrayList<MusicTrack> shuffledAudioTracks = new ArrayList<>();
    ArrayList<MusicTrack> unshuffledAudioTracks = new ArrayList<>();

    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    private int mCurrentPosition;
    private MusicTrack mAudioItem;
    private NotificationPlayer mNotificationPlayer;
    private BroadcastReceiver mBroadcastReceiver;
    private AudioFocusHelper mAudioFocusHelper;

    private boolean isSocketFirst = true;

    AudioManager mAudioManager;

    AmazonS3 s3;
    TransferUtility transferUtility;
    File downloadFromS3 = new File("/storage/emulated/0/AudioFiles/nowplaying.mp3");
    private boolean isItemDownloaded = false;
    String bucket = "upm-albums";
    String tempName = "now.jpg";
    String filename = "upmusicnowplaying.mp3";
    String objectName = "nowplaying.mp3";
    String objectId = "1";
    String albumId = "1";
    String previousSelectedId = "";

    String isPlayingForFocusing = "";

    boolean isFirstPlayed = false;


    public static String AccountName = "upmalbum";
    public static String AccountKey = "Nc9PdgAOAaKP6QmSndhNjuqWhqltwrTbisCMxq/QCiZ5XRPfSN0sKAuCk+i4f6EkcrV8mPgdjQs/n0fArAvkoA==";

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName="+ AccountName +";"
            + "AccountKey="+AccountKey + ";"
            + "EndpointSuffix=core.windows.net";
    //TEST

    private MediaSessionCompat mSession;

    public class AudioServiceBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }



    public void initMediaPlayer() {
//        if(mMediaPlayer != null ) {
////            mMediaPlayer.stop();
////            mMediaPlayer.release();
//        }

        mMediaPlayer = new MediaPlayer();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // [AZURE]
        try {
            initAzureStorage();
        } catch (StorageException e) {
            e.printStackTrace();
        }
//
//        // [AWS S3] callback method to call credentialsProvider method.
//        credentialsProvider();
//        // [AWS S3] callback method to call the setTransferUtility method
//        setTransferUtility();

//        mAudioItem = mAudioTracks.get(getPreferencesForLastPlayedTrackNumber());

        Log.e("[SERVICE]", " > onCreate()");

        mMediaPlayer = new MediaPlayer();


        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                isPrepared = true;
                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).setBtnPlayClicked();

                savePreferencesForLastPlayedTrackNumber(mCurrentPosition);
                savePreferencesForLastPlayedTrackId(mAudioItem.getId());

                if(mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }

                mp.start();
                isFirstPlayed = false;
                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).requestPlayerAddPlayCount(mAudioItem.getId());
                sendBroadcast(new Intent(BroadcastActions.PREPARED)); // prepared 전송
                updateNotificationPlayer();

                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();
                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).sendSocketPlayTime(0, mAudioItem.getId());
//                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).playerUpdateUI();
                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).setPlayerTimerTask();


                if (getPreferencesForPlayerFirstLoad()) {
                    pause();
                    savePreferencesForPlayerFirstLoad(false);
                }

            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                isPrepared = false;

                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();

                ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();

                if (getIsOnShuffleMode()) {
////
//                    Collections.shuffle(shuffledAudioTracks);
//                    mAudioTracks = shuffledAudioTracks;
                    mAudioTracks = shuffledAudioTracks;
                } else {
                    mAudioTracks = unshuffledAudioTracks;
                    mCurrentPosition = indexOfUnshuffled();

                    if (getRepeatMode().equals("NOR")) {

                        if (mAudioTracks.size() - 1 > mCurrentPosition) {
                            mCurrentPosition++; // 다음 포지션으로 이동.
                        } else {
//                        mCurrentPosition = 0; // 처음 포지션으로 이동.
                        }

                        if (mCurrentPosition != mAudioTracks.size()-1) {
                            play(mCurrentPosition);
                        }

                    }
                    if (getRepeatMode().equals("ONE")) {
                        mCurrentPosition--;
                        forward();
                    }

                    if (getRepeatMode().equals("ALL")) {
                        forward();
                    }
                }

            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
        mNotificationPlayer = new NotificationPlayer(this);

        mBroadcastReceiver = new MusicIntentReceiver(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // 오디오 포커스를 획득할 수 없다.
            Log.e(TAG, "[AudioManager] AUDIOFOCUS_REQUEST_GRANTED" );
            Toast.makeText(getApplicationContext(), "[UPMUSIC] 다른 앱에서 음악이 재생되어서 정지를 하려 했으나, 기기에서 오류가 발생했어요.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // 다시 재생
                Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_GAIN");
                if (mMediaPlayer == null) {
//                    initMediaPlayer();
                    Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_GAIN :: mMediaPlayer :  null");
                }

                if (mMediaPlayer!= null && !isPlaying()) {
                    //!mMediaPlayer.isPlaying()
                    Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_GAIN :: mMediaPlayer :  !isPlaying");
                    // 1. 아무 재생을 하지 않았을 때, 전화가 걸려오고 끊기고 난후 들어옴.
                    // 2. 앱이 topActivity일경우에는, 벗어나게 되므로 정지상태로 복귀. 즉 이 상태로 들어오게 됨.
                    // 3. 앱이 아래로 내려가 있을때에도 물론 거치게 됨.
                    // PLAYER UI-UPDATE WHEN TOP_ACTIVITY_OPEN
                    if (isPlayingForFocusing.equals("true")) {

                        ActivityManager activityManager = (ActivityManager) GlobalApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> services = activityManager
                                .getRunningTasks(Integer.MAX_VALUE);
                        boolean isActivityFound = false;

                        if (services.get(0).topActivity.getPackageName().toString()
                                .equalsIgnoreCase(GlobalApplication.getInstance().getPackageName().toString())) {
                            isActivityFound = true;
                        }

                        play();
                        ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).playerUpdateUI();
//
//                        if (isActivityFound) {
//
//                        } else {
//
//                        }
                        isPlayingForFocusing = "false"; // 원상태로
                    }


                }

                if (mMediaPlayer!=null && isPlaying()) {

                    Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_GAIN :: mMediaPlayer :  isPlaying");
//                    play(getPreferencesForLastPlayedTrackNumber());
                }


                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_LOSS");
//                 //포커스를 아주 장기적으로 잃었다. 음악 재생을 멈추고, 미디어 플레이어 자원도 해제
//                if (mMediaPlayer.isPlaying()) {
//
//                    Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_LOSS isPlaying");
//                    if (mMediaPlayer != null) {
//                        mMediaPlayer.stop();
//                        mMediaPlayer.release();
//                        mMediaPlayer = new MediaPlayer(); // 초기화.
//
//                    }
//                    removeNotificationPlayer();
//                }
//
////                mMediaPlayer.stop();
////                mMediaPlayer.release();
////                mMediaPlayer = null;
//                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_LOSS_TRANSIENT");
                // 잠시 포커스를 잃었지만, 음악을 중단해야 한다. 하지만 포커스가 금방 돌아올 것으로 예상하기 떄문에 미디어 플레이어는 해제 하지 않는다

                // TODO 재생 중이었다면 재생중이었음을 저장. => 복귀시에 초기화.
                if (mMediaPlayer.isPlaying()) {
                    isPlayingForFocusing = "true";
                    pause();

                } else {
                    isPlayingForFocusing = "false";

                }
//                      mMediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e("[focusChange]", " : AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // 잠시 포커스를 잃었지만, 볼륨을 줄인 상태에서 음악 재생을 유지해도 된다.
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }


    }

    //The way i use it is
    public void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "[onStartCommand]:::(intent)" + intent + ", (flags)" + flags + "(startId)" + startId);

        boolean isActivityFound = false;
        // PLAYER UI-UPDATE WHEN TOP_ACTIVITY_OPEN
        ActivityManager activityManager = (ActivityManager) GlobalApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(GlobalApplication.getInstance().getPackageName().toString())) {
            isActivityFound = true;
        }


        if (intent != null) {
            String action = intent.getAction();
            Log.e(TAG, "[onStartCommand]:::(action)" + action);
            if (CommandActions.TOGGLEPLAY.equals(action)) {
                Log.e(TAG, "[onStartCommand]:::(TOGGLEPLAY)");
                if (isPlaying()) {
                    if (isActivityFound) {
                        ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();
                        ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).setViewsAsPauseStatus();
                    } else {

                        ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();
                    }
                    pause();
                } else {
                    ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).setPlayerTimerTask();
                    play();
                }
            } else if (CommandActions.REWIND.equals(action)) {

                Log.e(TAG, "[onStartCommand]:::(REWIND)");
                rewind();
            } else if (CommandActions.FORWARD.equals(action)) {
                Log.e(TAG, "[onStartCommand]:::(FORWARD)");
                forward();
            } else if (CommandActions.CLOSING.equals(action)) {
                Log.e(TAG, "[onStartCommand]:::(CLOSING)");
                // EXCEPT PAUSE()

                if (isPrepared) {
                    mMediaPlayer.pause();
                }
                removeNotificationPlayer();

                if (isActivityFound) {
                    ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();
                    ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).setViewsAsPauseStatus();
                } else {

                    ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();

                    if ((((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext)
                            .getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
                        Log.e(TAG, "최근 실행 목록에서 실행...");
                    } else {
                        ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).releaseStomp();
                        mMediaPlayer.release();
                    }
                }


            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeNotificationPlayer();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }


    }

    private void updateNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    public void removeNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    private void queryAudioItem(int position) {
        Log.e("[AudioService] : ", "[queryAudioItem] , position :: " + position);
        mCurrentPosition = position;
        MusicTrack audioTrack = mAudioTracks.get(position);

        Log.e("[AudioService] : ", "[queryAudioItem] , audioTrack :: " + audioTrack.getFilenameUrl());

        mAudioItem = audioTrack;

//        indexOfUnshuffled();

    }

    public boolean isPrepared() {
        if (isPrepared) {
            return true;
        }
        return false;
    }

    public void prepare() {
        try {
            Log.e("TESt", "[prepare()] mAudioItem.getId() : " + mAudioItem.getId());
            Log.e("TESt", "[prepare()] mAudioItem.getArtistNick() : " + mAudioItem.getArtistNick());
            Log.e("TESt", "[prepare()] mAudioItem.getSubject() : " + mAudioItem.getSubject());
            Log.e("TESt", "[prepare()] mAudioItem.getFilenameUrl() : " + mAudioItem.getFilenameUrl());

            mMediaPlayer.setDataSource(mAudioItem.getFilenameUrl());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();

            // TODO **** MUST ADD, CHANGE MUSIC LISTEN (http get..resources)
            Thread.sleep(2300);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public int getPlaylistItemCount() {

        if (mAudioTracks == null) {
            return 0;
        }

        if (mAudioTracks.size() == 0) {
            return 0;
        }

        return mAudioTracks.size();
    }


    public void setPlayList(ArrayList<MusicTrack> audioTracks, boolean positionSetted, boolean isFirstLoad) {

//        Log.e("[AudioService] : ", "[start] : setPlayList" + audioTracks);

        if(!isFirstLoad) {
            if (isPlaying() && !positionSetted) {
                pause();
                stop();
            }
        }

        if (!unshuffledAudioTracks.equals(audioTracks)) {
            mAudioTracks.clear();
            shuffledAudioTracks.clear();
            unshuffledAudioTracks.clear();
            mAudioTracks.addAll(audioTracks);
            shuffledAudioTracks.addAll(audioTracks);
            unshuffledAudioTracks.addAll(audioTracks);

            if (getIsOnShuffleMode()) {
                Collections.shuffle(shuffledAudioTracks);
            }
        }
//        Log.e("[AudioService] : ", "[end] : setPlayList : " + mAudioTracks);

    }

    public int getIndexOfTrack(MusicTrack track){
        return mAudioTracks.indexOf(track);
    }

    public MusicTrack getAudioItem() {
        return mAudioItem;
    }

    public ArrayList<MusicTrack> getAudioTracks() {
        return unshuffledAudioTracks;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void play(int position) {

        // TODO
        Log.e(TAG, "[play] :: postion :: " + position);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }

        queryAudioItem(position);
        stop();
        prepare();
    }

    public void play() {

        if (mMediaPlayer == null) {
            Log.e("TEST", "[play()] new mMediaPlayer ");
            mMediaPlayer = new MediaPlayer();
        }
//        if(getPreferencesForPlayerFirstLoad() == true) {
//            mMediaPlayer = new MediaPlayer();
//            savePreferencesForPlayerFirstLoad(false);
//        }

        if (isPrepared) {
//            if(mMediaPlayer.isPlaying()) {
//                mMediaPlayer.pause();
//            }

            mMediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            updateNotificationPlayer();
            ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).setPlayerTimerTask();
        }
    }


    public void pause() {

        Log.e(TAG, "[pause] [0]");
        if (isPrepared) {
            Log.e(TAG, "[pause] [1]");
            mMediaPlayer.pause();

            Log.e(TAG, "[pause] [2]");
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송

            Log.e(TAG, "[pause] [3]");
            updateNotificationPlayer();
            ((com.strawberryswing.upmusic.activity.MainActivity) com.strawberryswing.upmusic.activity.MainActivity.mContext).stopPlayerTimer();
            mMediaPlayer.pause();

        }
    }

    public void forward() {

        Log.e(TAG, "[forward]:::getIsOnShuffleMode " + getIsOnShuffleMode());
        if (getIsOnShuffleMode()) {
            mAudioTracks = shuffledAudioTracks;
        } else {
            mAudioTracks = unshuffledAudioTracks;
            mCurrentPosition = indexOfUnshuffled();
        }


        if (mAudioTracks.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }


        play(mCurrentPosition);
    }




    public int indexOfUnshuffled() {

        Log.e(TAG,"[indexOfUnshuffled] : " + unshuffledAudioTracks.indexOf(mAudioItem) );

        return unshuffledAudioTracks.indexOf(mAudioItem);
    }

    public void rewind() {


        Log.e(TAG, "[rewind]:::getIsOnShuffleMode " + getIsOnShuffleMode());

        if (getIsOnShuffleMode()) {
            mAudioTracks = shuffledAudioTracks;
        } else {
            mAudioTracks = unshuffledAudioTracks;
            mCurrentPosition = indexOfUnshuffled();
        }


        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioTracks.size() - 1; // 마지막
            // 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public int randomizeShuffleNumber(int currentPosition) {
        int min = 0;
        int max = mAudioTracks.size();

        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;

//        if(i1==mAudioTracks.size()) {
//            i1--;
//            return i1;
//        }

        if(i1==mAudioTracks.size()-1 && currentPosition==i1) {
            i1--;
            return i1;
        }

        if(currentPosition==i1) {
            i1++;
        }


        Log.e(TAG, "[randomizeShuffleNumber]::"  +i1 );
        return i1;
    }

    public void shuffleOnOff() {



        if (getPreferencesForShuffle() == false) {
            shuffledAudioTracks.clear();
            shuffledAudioTracks.addAll(unshuffledAudioTracks);
            Collections.shuffle(shuffledAudioTracks);
            mAudioTracks = shuffledAudioTracks;

            Log.e(TAG, "[mAudioTracks] shuffle ON : ");
                    //+ mAudioTracks);
            savePreferencesForShuffle(true);
        } else {

            mAudioTracks = unshuffledAudioTracks;

            Log.e(TAG, "[mAudioTracks] shuffle OFF : ");
//            Log.e(TAG, "[mAudioTracks] shuffle OFF 1 : " + mAudioTracks);
//            Log.e(TAG, "[mAudioTracks] shuffle OFF 2 : " + unshuffledAudioTracks);
            savePreferencesForShuffle(false);
        }


//        Log.e(TAG, "[mAudioTracks] 1 : " + mAudioTracks);
//        Log.e(TAG, "[mAudioTracks] 2 : " + shuffledAudioTracks);
//        Log.e(TAG, "[mAudioTracks] 3 : " + unshuffledAudioTracks);

    }

    public void repeatOnOff(String mode) {

        if (mode.equals("NOR")) {
            savePreferencesForRepeat(mode);
        } else if (mode.equals("ONE")) {
            savePreferencesForRepeat(mode);
        } else if (mode.equals("ALL")) {
            savePreferencesForRepeat(mode);
        } else {


        }
    }

    public boolean getIsFirstPlayed() {
        return this.isFirstPlayed;
    }

    public boolean getIsOnShuffleMode() {
        return getPreferencesForShuffle();
    }

    public String getRepeatMode() {
        return getPreferencesForRepeat();
    }


    public MediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }

    public boolean getIsItemDownloaded() {
        return this.getIsItemDownloaded();
    }

    public void setmCurrentPosition(int position) {
        mCurrentPosition = position;
    }

    public void initAzureStorage() throws StorageException {
        Log.e(TAG, "[initAzureStorage] ");
        try {
            CloudStorageAccount account = CloudStorageAccount
                    .parse(storageConnectionString);
            CloudBlobClient blobClient = account.createCloudBlobClient();

            CloudBlobContainer container = blobClient.getContainerReference("upm-container-album");

            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
            containerPermissions
                    .setPublicAccess(BlobContainerPublicAccessType.CONTAINER);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    /**
     * [AWS S3]
     *  This method is used to Download the file to S3 by using transferUtility class
     **/
    public void readyForPrepare(){

//        Log.e(TAG, "[setFileToDownload] path : " + getFilesDir() );
//        Log.e(TAG, "[setFileToDownload] total : " + getFilesDir() + "/" + filename);

        downloadFromS3 = new File(getFilesDir() + "/" +  filename);

        if(downloadFromS3.exists()==true) {
            //파일이 있을시
//            Log.e(TAG, "[setFileToDownload] downloadFromS3.exists 1 : true");
            downloadFromS3.delete();
//            Log.e(TAG, "[setFileToDownload] downloadFromS3.exists 1 : deleted");
        } else {
            //파일이 없을시
//            Log.e(TAG, "[setFileToDownload] downloadFromS3.exists 1 : false");
        }

        if(downloadFromS3.exists()==true) {
            //파일이 있을시
//            Log.e(TAG, "[setFileToDownload] downloadFromS3.exists 2 : true");
        } else {
            //파일이 없을시
//            Log.e(TAG, "[setFileToDownload] downloadFromS3.exists 2 : false");
        }

        objectName = mAudioItem.getFilenameUrl()
                .replace("http://s3.ap-northeast-2.amazonaws.com/upm-albums/"
                        ,"");

        Log.e(TAG, "[setFileToDownload] bucket : " + bucket);
        Log.e(TAG, "[setFileToDownload] key : " + ""  + "" + objectName);

        TransferObserver transferObserver = transferUtility.download(
                bucket,     /* The bucket to download from */
                objectName,    /* The key for the object to download */
                downloadFromS3        /* The file to download the object to */
        );
        transferObserverListener(transferObserver);



    }

    /**
     * [AWS S3]
     * This is listener method of the TransferObserver
     * Within this listener method, we get the status of uploading and downloading the file,
     * it displays percentage part of the  file to be uploaded or downloaded to S3
     * It also displays an error, when there is problem to upload and download file to S3.
     * @param transferObserver
     */
    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                isItemDownloaded = false;
                Log.e(TAG, " [onStateChanged] : " + state);
                if (state == TransferState.COMPLETED) {
                    isItemDownloaded = true;
//                    playMP3FileWithAsyncTask();
//                    AudioApplication.getInstance().getServiceInterface().togglePlay();
                    prepare();

                }
                Log.e(TAG, " [onStateChanged] isItemDownloaded : " + isItemDownloaded);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                if (bytesTotal != 0)  {
                    int percentage = (int) (bytesCurrent/bytesTotal * 100);
                    Log.e(TAG, " [onProgressChanged] : " + percentage);
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, " [onError] : " + ex.toString());
            }

        });
    }

    /**
     * [AWS S3]
     */
    public void credentialsProvider(){
        //ap-northeast-2:59ff7d2b-82c2-43ff-b4e8-0c6580fbe3e8
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:59ff7d2b-82c2-43ff-b4e8-0c6580fbe3e8", // Identity Pool ID
                Regions.AP_NORTHEAST_2 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    /**
     * [AWS S3]
     *  Create a AmazonS3Client constructor and pass the credentialsProvider.
     * @param credentialsProvider
     */
    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);
        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
    }

    /**
     * [AWS S3]
     */
    public void setTransferUtility(){
        transferUtility = new TransferUtility(s3, getApplicationContext());
    }


    private boolean getPreferencesForPlayerFirstLoad(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getBoolean("playerFirstLoad", false); //key, value(defaults)

    }

    private void savePreferencesForPlayerFirstLoad(boolean temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("playerFirstLoad", temp);
        editor.commit();
    }




    //데이터 가져오기 isOnShuffle
    private boolean getPreferencesForShuffle(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getBoolean("isOnShuffle", false); //key, value(defaults)

    }

    //데이터 저장하기 isOnShuffle
    private void savePreferencesForShuffle(boolean temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isOnShuffle", temp);
        editor.commit();
    }

    //데이터 가져오기 REPEAT_MODE
    private String getPreferencesForRepeat(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getString("REPEAT_MODE", "NOR"); //key, value(defaults)

    }

    //데이터 저장하기 REPEAT_MODE
    private void savePreferencesForRepeat(String temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("REPEAT_MODE", temp);
        editor.commit();
    }

    //데이터 가져오기 lastPlayedTrackNumber
    private int getPreferencesForLastPlayedTrackNumber(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getInt("lastPlayedTrackNumber", 0); //key, value(defaults)

    }

    //데이터 저장하기
    private void savePreferencesForLastPlayedTrackNumber(int position){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("lastPlayedTrackNumber", position);
        editor.commit();
    }

    //데이터 가져오기 lastPlayedTrackId
    private String getPreferencesForLastPlayedTrackId(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getString("lastPlayedTrackId", ""); //key, value(defaults)

    }

    //데이터 저장하기
    private void savePreferencesForLastPlayedTrackId(String id){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lastPlayedTrackId", id);
        editor.commit();
    }

    //데이터 삭제
    private void removePreferencesForLastPlayedTrackNumber(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("lastPlayedTrackNumber");
        editor.commit();
    }

    //모든 데이터 삭제
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * USE WHEN GLOBAL VALUES FOR LIST.
     * FOR STORE : setStringArrayPref
     * FOR RETRIEVE : getStringArrayPref
     // store preference
     * @Example
     * ArrayList<String> list = new ArrayList<String>(Arrays.asList(urls));
     * setStringArrayPref(this, "urls", list);
     */
    private void setStringArrayPref(Context context,String key,ArrayList<String> values){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray a = new JSONArray();
        for (int i=0;i<values.size();i++) {
            a.put(values.get(i));
        }

        if(!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key,null);
        }
        editor.commit();
    }

    /**
     * // retrieve preference
     * @Example
     * list = getStringArrayPref(this, "urls");
     * urls = (String[]) list.toArray();
     */
    private ArrayList<String> getStringArrayPref(Context context,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key,null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i=0;i<a.length();i++) {
                    String url = a.optString(i);
                    urls.add(url);;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

}
