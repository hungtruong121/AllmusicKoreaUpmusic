package com.strawberryswing.upmusic.activity;

import com.strawberryswing.upmusic.model.MusicTrack;

public interface AudioPlaylistInterface {

    public void setItemSelected();

    public void playTrack(MusicTrack track, int position, boolean noPlay, boolean isFirstLoad);

    public void collectTrack(MusicTrack track, int position);

    public void deleteTrack(MusicTrack track, int position, Long[] ids);

    public void retrieveTracks(MusicTrack track, int position, Long[] ids);

    public void moreTrack(MusicTrack track, int position);

    public String getPreferencesForToken();

    public void setPreferencesForToken(String token);

}
