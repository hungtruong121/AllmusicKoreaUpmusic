package com.strawberryswing.upmusic.activity.video;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.activity.UpmuicAPI;
import com.strawberryswing.upmusic.model.Video;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoPlaylistActivity extends AppCompatActivity implements VideoPlaylistInterface {


    public static final String KEY_VIDEO_ADMIN_URL = "KEY_VIDEO_ADMIN_URL".toLowerCase();
    public static final String KEY_VIDEO_ARTIST_NICK = "KEY_VIDEO_ARTIST_NICK".toLowerCase();
    public static final String KEY_VIDEO_ARTIST_URL = "KEY_VIDEO_ARTIST_URL".toLowerCase();
    public static final String KEY_VIDEO_CREATED_AT = "KEY_VIDEO_CREATED_AT".toLowerCase();

    public static final String KEY_VIDEO_DESCRIPTION = "KEY_VIDEO_DESCRIPTION".toLowerCase();
    public static final String KEY_VIDEO_DURATION = "KEY_VIDEO_DURATION".toLowerCase();
    public static final String KEY_VIDEO_FILENAME = "KEY_VIDEO_FILENAME".toLowerCase();
    public static final String KEY_VIDEO_FILENAME_URL = "KEY_VIDEO_FILENAME_URL".toLowerCase();
    public static final String KEY_VIDEO_GENRE_NAME = "KEY_VIDEO_GENRE_NAME".toLowerCase();

    public static final String KEY_VIDEO_HEART_COUNT = "KEY_VIDEO_HEART_COUNT".toLowerCase();
    public static final String KEY_VIDEO_HIT_COUNT = "KEY_VIDEO_HIT_COUNT".toLowerCase();
    public static final String KEY_VIDEO_HOT_POINT = "KEY_VIDEO_HOT_POINT".toLowerCase();
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID".toLowerCase();
    public static final String KEY_VIDEO_LIKED = "KEY_VIDEO_LIKED".toLowerCase();
    public static final String KEY_VIDEO_SUBJECT = "KEY_VIDEO_SUBJECT".toLowerCase();
    public static final String KEY_VIDEO_THUMBNAIL = "KEY_VIDEO_THUMBNAIL".toLowerCase();
    public static final String KEY_VIDEO_THUMBNAIL_URL = "KEY_VIDEO_THUMBNAIL_URL".toLowerCase();
    public static final String KEY_VIDEO_TYPE_NAME = "KEY_VIDEO_TYPE_NAME".toLowerCase();
    public static final String KEY_VIDEO_UPDATED_AT = "KEY_VIDEO_UPDATED_AT".toLowerCase();
    public static final String KEY_VIDEO_URL = "KEY_VIDEO_URL".toLowerCase();
    public static final String KEY_VIDEO_VIDEO_TYPE = "KEY_VIDEO_VIDEO_TYPE".toLowerCase();
    // EXCEPT MEMBER VAR...

    private ListView listView;
    List<Video> itemsList;
    ArrayList<Video> itemsArrayList;
    VideoPlaylistAdapter playlistAdapter;
    private boolean isItemSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate","[VideoPlaylistActivity]" );
        // [BASIC] setContentView
        setContentView(R.layout.content_listview);
        // [BASIC] findViewById
        itemsArrayList = new ArrayList<>();
        connectWithViews();

        // [SET] Get Items For listview.
        getAllVideoItems();

//        listView.setOnItemClickListener(this);

    }

    private void connectWithViews() {
        listView = (ListView) findViewById(R.id.listView_in_player);
    }

    private void getAllVideoItems() {


        Log.e("Function"," called [getAllVideoItems]" );
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UpmuicAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        Call<List<Video>> call = api.getAllVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                itemsList = response.body();

                Log.e("Function"," onResponse [getAllVideoItems]" );
                String[] itemNames = new String[itemsList.size()];

                for (int i = 0; i<itemsList.size();i++) {
                    itemNames[i] = itemsList.get(i).getSubject();
                }

//                listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, itemNames));

                itemsArrayList.addAll(itemsList);
                playlistAdapter = new VideoPlaylistAdapter(getApplicationContext(), itemsArrayList, VideoPlaylistActivity.this);
                listView.setAdapter(playlistAdapter);

//                Log.e("Function"," listView [getAllVideoItems]" + itemsArrayList.toString() );
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void setItemSelected() {

        isItemSelected = true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(this, VideoPlayerActivity.class);
//        Video track = itemsList.get(i);
//        intent.putExtra(KEY_VIDEO_ADMIN_URL, track.getAdminUrl());
//        intent.putExtra(KEY_VIDEO_ARTIST_NICK, track.getArtistNick());
//        intent.putExtra(KEY_VIDEO_ARTIST_URL, track.getArtistUrl());
//        intent.putExtra(KEY_VIDEO_CREATED_AT, track.getCreatedAt());
//        intent.putExtra(KEY_VIDEO_DESCRIPTION, track.getDescription());
//        intent.putExtra(KEY_VIDEO_DURATION, track.getDuration());
//        intent.putExtra(KEY_VIDEO_FILENAME, track.getFilename());
//        intent.putExtra(KEY_VIDEO_FILENAME_URL, track.getFilenameUrl());
//        intent.putExtra(KEY_VIDEO_GENRE_NAME, track.getGenreName());
//        intent.putExtra(KEY_VIDEO_HEART_COUNT, track.getHeartCnt());
//        intent.putExtra(KEY_VIDEO_HIT_COUNT, track.getHitCnt());
//        intent.putExtra(KEY_VIDEO_HOT_POINT, track.getHotPoint());
//        intent.putExtra(KEY_VIDEO_ID, track.getId());
//        intent.putExtra(KEY_VIDEO_LIKED, track.getLiked());
//        intent.putExtra(KEY_VIDEO_SUBJECT, track.getSubject());
//        intent.putExtra(KEY_VIDEO_THUMBNAIL, track.getThumbnail());
//        intent.putExtra(KEY_VIDEO_THUMBNAIL_URL, track.getThumbnailUrl());
//        intent.putExtra(KEY_VIDEO_UPDATED_AT, track.getUpdatedAt());
//        intent.putExtra(KEY_VIDEO_URL, track.getUrl());
//        intent.putExtra(KEY_VIDEO_VIDEO_TYPE, track.getVideoType());
//
//        startActivity(intent);
////        startActivity(intent); // IF WANT NEW ACTIVITY.
//    }

    @Override
    public void playVideo(Video param, int position) {

        Intent intent = new Intent(this, VideoPlayerActivity.class);
        Video track = param;
        intent.putExtra(KEY_VIDEO_ADMIN_URL, track.getAdminUrl());
        intent.putExtra(KEY_VIDEO_ARTIST_NICK, track.getArtistNick());
        intent.putExtra(KEY_VIDEO_ARTIST_URL, track.getArtistUrl());
        intent.putExtra(KEY_VIDEO_CREATED_AT, track.getCreatedAt());
        intent.putExtra(KEY_VIDEO_DESCRIPTION, track.getDescription());
        intent.putExtra(KEY_VIDEO_DURATION, track.getDuration());
        intent.putExtra(KEY_VIDEO_FILENAME, track.getFilename());
        intent.putExtra(KEY_VIDEO_FILENAME_URL, track.getFilenameUrl());
        intent.putExtra(KEY_VIDEO_GENRE_NAME, track.getGenreName());
        intent.putExtra(KEY_VIDEO_HEART_COUNT, track.getHeartCnt());
        intent.putExtra(KEY_VIDEO_HIT_COUNT, track.getHitCnt());
        intent.putExtra(KEY_VIDEO_HOT_POINT, track.getHotPoint());
        intent.putExtra(KEY_VIDEO_ID, track.getId());
        intent.putExtra(KEY_VIDEO_LIKED, track.getLiked());
        intent.putExtra(KEY_VIDEO_SUBJECT, track.getSubject());
        intent.putExtra(KEY_VIDEO_THUMBNAIL, track.getThumbnail());
        intent.putExtra(KEY_VIDEO_THUMBNAIL_URL, track.getThumbnailUrl());
        intent.putExtra(KEY_VIDEO_UPDATED_AT, track.getUpdatedAt());
        intent.putExtra(KEY_VIDEO_URL, track.getUrl());
        intent.putExtra(KEY_VIDEO_VIDEO_TYPE, track.getVideoType());

        startActivity(intent);

//        Log.e(TAG, "PLAYITEM : " + AudioApplication.getInstance().getServiceInterface().getAudioItem());
    }

}