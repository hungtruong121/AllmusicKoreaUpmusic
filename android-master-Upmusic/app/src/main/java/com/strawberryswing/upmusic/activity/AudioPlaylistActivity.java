package com.strawberryswing.upmusic.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.model.MusicTrack;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AudioPlaylistActivity extends AppCompatActivity {


    private ListView listView;
    List<MusicTrack> itemsList;
    ArrayList<MusicTrack> itemsArrayList;
    AudioPlaylistBaseAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [BASIC] setContentView
        setContentView(R.layout.activity_navigation_drawer);
        // [BASIC] findViewById
        connectWithViews();

        // [SET] Get Items For listview.
        getAllTrackItems();


    }

    private void connectWithViews() {

        listView = (ListView) findViewById(R.id.listView);
    }

    private void getAllTrackItems() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UpmuicAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpmuicAPI api =  retrofit.create(UpmuicAPI.class);

        Call<List<MusicTrack>> call = api.getAllTracks();
        call.enqueue(new Callback<List<MusicTrack>>() {
            @Override
            public void onResponse(Call<List<MusicTrack>> call, Response<List<MusicTrack>> response) {
                itemsList = response.body();

                String[] itemNames = new String[itemsList.size()];

                for (int i = 0; i<itemsList.size();i++) {
                    itemNames[i] = itemsList.get(i).getSubject();
                }

//                listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, itemNames));

                itemsArrayList.addAll(itemsList);
                // Interface Processing...
//                AudioPlaylistAdapter = new AudioPlaylistAdapter(getApplicationContext(), itemsArrayList, MainActivity);
                listView.setAdapter(playlistAdapter);
            }

            @Override
            public void onFailure(Call<List<MusicTrack>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
            }

        });
    }

}