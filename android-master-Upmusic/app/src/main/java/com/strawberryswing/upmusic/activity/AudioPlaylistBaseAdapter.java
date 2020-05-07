package com.strawberryswing.upmusic.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.model.MusicTrack;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * 1. where WE INFLATE OUR MODEL LAYOUT INTO VIEW ITEM
 * 2. THEN BIND DATA
 */
public class AudioPlaylistBaseAdapter extends BaseAdapter {
    Context c;
    ArrayList<MusicTrack> items;
    LayoutInflater inflater;
    AudioPlaylistInterface pInterface;

    private static class ViewHolder {
        public RelativeLayout container;
        public ImageView imageView01;
        public TextView textView01;
        public TextView textView02;
        public Button btn01;

    }

    public AudioPlaylistBaseAdapter(Context c, ArrayList<MusicTrack> items, AudioPlaylistInterface pInterface) {
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
            convertView= LayoutInflater.from(c).inflate(R.layout.row_audio_playlist,parent,false);
            holder = new ViewHolder();
            holder.container = (RelativeLayout) convertView.findViewById(R.id.container);
            holder.imageView01 = (ImageView) convertView.findViewById(R.id.imageView01);
            holder.textView01 = (TextView) convertView.findViewById(R.id.textView01);
            holder.textView02 = (TextView) convertView.findViewById(R.id.textView02);
            holder.btn01 = (Button) convertView.findViewById(R.id.btnMore);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MusicTrack model= (MusicTrack) this.getItem(position);

        Picasso.with(c)
                .load(model.getCoverImageUrl())
                .into(holder.imageView01);

        holder.textView01.setText("" + model.getSubject());
        holder.textView02.setText("" + model.getArtistNick());


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(c,model.getId(), Toast.LENGTH_SHORT).show();
//                goViewMore(view, model);
                pInterface.setItemSelected();

                if (pInterface.getPreferencesForToken().equals("")) {
                    return;
                }

                if (pInterface.getPreferencesForToken().equals(null)) {
                    return;
                }

                pInterface.playTrack(model, position, false, false);


            }
        });

        return convertView;
    }



}

