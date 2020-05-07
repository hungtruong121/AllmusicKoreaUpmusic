package com.strawberryswing.upmusic.activity.video;

import android.view.View;
import android.widget.AdapterView;

import com.strawberryswing.upmusic.model.MusicTrack;
import com.strawberryswing.upmusic.model.Video;

public interface VideoPlaylistInterface {

    public void setItemSelected();

    void onItemClick(AdapterView<?> adapterView, View view, int i, long l);

    public void  playVideo(Video param, int position);
}
