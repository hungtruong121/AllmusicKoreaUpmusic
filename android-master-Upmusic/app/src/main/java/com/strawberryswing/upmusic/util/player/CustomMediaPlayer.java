package com.strawberryswing.upmusic.util.player;

import android.media.MediaPlayer;
import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer
{
    String dataSource;

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
    {
        // TODO Auto-generated method stub
        super.setDataSource(path);
        dataSource = path;
    }

    public String getDataSource()
    {
        return dataSource;
    }
}