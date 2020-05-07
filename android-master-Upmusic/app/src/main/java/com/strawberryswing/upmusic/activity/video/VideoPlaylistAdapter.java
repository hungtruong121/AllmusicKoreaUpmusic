package com.strawberryswing.upmusic.activity.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.model.Video;

import java.util.ArrayList;


/**
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class VideoPlaylistAdapter extends BaseAdapter {
    Context c;
    ArrayList<Video> items;
    LayoutInflater inflater;
    VideoPlaylistInterface pInterface;

    private static class ViewHolder {
        public RelativeLayout container;
        public ImageView imageView01;
        public TextView textView01;
        public TextView textView02;
        public TextView textView03;
        public TextView textView00;
    }

    public VideoPlaylistAdapter(Context c, ArrayList<Video> items) {
        this.c = c;
        this.items = items;
    }

    public VideoPlaylistAdapter(Context c, ArrayList<Video> items, VideoPlaylistInterface pInterface) {
        this.c = c;
        this.items = items;
        this.pInterface = pInterface;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.row_video_playlist,parent,false);
            holder = new ViewHolder();
            holder.container = (RelativeLayout) convertView.findViewById(R.id.container);
            holder.imageView01 = (ImageView) convertView.findViewById(R.id.imageView01);
            holder.textView01 = (TextView) convertView.findViewById(R.id.textView01);
            holder.textView02 = (TextView) convertView.findViewById(R.id.textView02);
            holder.textView03 = (TextView) convertView.findViewById(R.id.textView03);
            holder.textView00 = (TextView) convertView.findViewById(R.id.textView00);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Video model= (Video) this.getItem(position);

        Picasso.with(c)
                .load(model.getThumbnailUrl())
                .into(holder.imageView01);

        holder.textView01.setText("" + model.getSubject());
        holder.textView02.setText("" + model.getArtistNick());
        holder.textView03.setText("" + model.getUpdatedAt());

        int lengthSec;
        lengthSec = Integer.parseInt(model.getDuration())/1000;
        holder.textView00.setText(String.format("%02d:%02d", lengthSec/60 , lengthSec%60));


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(c,model.getId(), Toast.LENGTH_SHORT).show();
//                goViewMore(view, model);
                pInterface.setItemSelected();
                pInterface.playVideo(model, position);


            }
        });

        return convertView;
    }

}

