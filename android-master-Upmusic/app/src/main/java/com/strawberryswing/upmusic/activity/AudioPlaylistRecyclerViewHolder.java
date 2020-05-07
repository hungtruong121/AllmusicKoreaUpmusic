package com.strawberryswing.upmusic.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.strawberryswing.upmusic.R;

public class AudioPlaylistRecyclerViewHolder extends RecyclerView.ViewHolder {

    int Holderid;

//    public GoodealHeader header;
//    public Footer footer;

    public RelativeLayout container;
    public ImageView imageView01;
    public TextView textView01;
    public TextView textView02;
    public Button btn01;
    public OnListItemClickListener mListener;

    public AudioPlaylistRecyclerViewHolder(View itemView) {
        super(itemView);

        //row_audio_playlist
        container = (RelativeLayout) itemView.findViewById(R.id.container);
        imageView01 = (ImageView) itemView.findViewById(R.id.imageView01);
        textView01 = (TextView) itemView.findViewById(R.id.textView01);
        textView02 = (TextView) itemView.findViewById(R.id.textView02);
        btn01 = (Button) itemView.findViewById(R.id.btnMore);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListItemClick(getAdapterPosition());
            }
        });
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mListener = onListItemClickListener;
    }

    public interface OnListItemClickListener {
        public void onListItemClick(int position);
    }

}
