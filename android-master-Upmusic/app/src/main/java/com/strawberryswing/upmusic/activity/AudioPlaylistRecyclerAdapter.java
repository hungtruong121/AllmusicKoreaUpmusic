package com.strawberryswing.upmusic.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.model.MusicTrack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AudioPlaylistRecyclerAdapter extends RecyclerView.Adapter<AudioPlaylistRecyclerViewHolder>
    implements AudioPlaylistRecyclerViewHolder.OnListItemClickListener, ItemTouchHelperAdapter


{

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }

    List<MusicTrack> items = new ArrayList<>();
    List<MusicTrack> willBeRemovedItems = new ArrayList<>();


    Context mCtx;
    Resources res;
    AudioPlaylistInterface pInterface;
    private int selectedPos = RecyclerView.NO_POSITION;

    private int selectedPosition = -1;


    //아이템 클릭시 실행 함수
    private ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view,int position);
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }


    private boolean listItemClickable = true;

    public void setListItemClickable(boolean clickable) {
        this.listItemClickable = clickable;
    }

    public boolean getListItemClickable() {
        return listItemClickable;
    }

    public AudioPlaylistRecyclerAdapter(Context mCtx, AudioPlaylistInterface pInterface) {
        this.mCtx = mCtx;
        this.res = mCtx.getResources();
        this.pInterface = pInterface;

    }

    public void add(MusicTrack data) {
        items.add(data);
        notifyDataSetChanged();
    }

    public void updateList(List<MusicTrack> list) {
        items = list;
        notifyDataSetChanged();
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        if(items.get(position).isHeader)
//            return VIEW_TYPES.Header;
//        else if(items.get(position).isFooter)
//            return VIEW_TYPES.Footer;
//        else
//            return VIEW_TYPES.Normal;
//    }

    @NonNull
    @Override
    public AudioPlaylistRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_audio_playlist, viewGroup, false);
        AudioPlaylistRecyclerViewHolder holder = new AudioPlaylistRecyclerViewHolder(v);
        holder.setOnListItemClickListener(this);



//        View rowView;
//        switch (viewType)
//        {
//            case VIEW_TYPES.Normal:
//                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.normal, viewGroup, false);
//                break;
//            case VIEW_TYPES.Header:
//                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header, viewGroup, false);
//                break;
//            case VIEW_TYPES.Footer:
//                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer, viewGroup, false);
//                break;
//            default:
//                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.normal, viewGroup, false);
//                break;
//        }
//        return new ViewHolder (rowView);



        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AudioPlaylistRecyclerViewHolder holder, final int position) {

        final MusicTrack item = items.get(position);

        holder.itemView.setSelected(selectedPos == position);

        holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));

        if(listItemClickable) {

            item.setSelected(false);

            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//            if(selectedPosition == position) {
//                holder.itemView.setBackgroundColor(Color.parseColor("#ecf0f2"));
//            } else {
//
//                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
//            }
        }


        if (!listItemClickable) {

            if( items.get(position).isSelected()) {

                holder.itemView.setBackgroundColor(Color.parseColor("#ecf0f2"));
            } else {

                holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            }

        }

        Picasso.with(mCtx)
                .load(item.getCoverImageUrl())
                .into(holder.imageView01);

        holder.textView01.setText("" + item.getSubject());
        holder.textView01.setEllipsize(TextUtils.TruncateAt.END);//Ellipsize의 MARQUEE 속성 주기
        holder.textView01.setSingleLine(true); //한줄로 나오게 하기.
        holder.textView01.setSelected(true);

        holder.textView02.setText("" + item.getArtistNick());

        holder.btn01.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if (listItemClickable == true) {

                    selectedPosition = position;

                    pInterface.setItemSelected();

                    if (pInterface.getPreferencesForToken().equals("")) {
                        return;
                    }

                    if (pInterface.getPreferencesForToken().equals(null)) {
                        return;
                    }
                    pInterface.moreTrack(item, position);

                }

                if (listItemClickable == false) {
                    items.get(position).setSelected(!items.get(position).isSelected());
//            selectedPosition = position;
                    notifyDataSetChanged();

                    ArrayList<Long> idArray = new ArrayList<Long>();

                    idArray.clear();


                    for (int i=0;i<items.size();i++) {

                        if(items.get(i).isSelected()){
                            idArray.add(Long.parseLong(items.get(i).getId()));
                        }
                    }

                    Long[] ids = new Long[idArray.size()];

                    idArray.toArray(ids);

                    Log.e("[TEST]", "ids : " + Arrays.toString(ids));

                    pInterface.retrieveTracks(items.get(position), position, ids);
                }

            }
        });

        holder.textView01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listItemClickable == true) {
                    if (getPreferencesForEditMode().equals("true")) {
                        Log.e("[on]", "getPreferencesForEditMode true");
                        return;
                    }

                    pInterface.setItemSelected();

                    if (pInterface.getPreferencesForToken().equals("")) {
                        return;
                    }

                    if (pInterface.getPreferencesForToken().equals(null)) {
                        return;
                    }

//                Toast.makeText(mCtx, "재생 준비 중입니다.", Toast.LENGTH_SHORT).show();
                    pInterface.playTrack(item, position, false, false);
                }


                if (listItemClickable == false) {
                    items.get(position).setSelected(!items.get(position).isSelected());
//            selectedPosition = position;
                    notifyDataSetChanged();

                    ArrayList<Long> idArray = new ArrayList<Long>();

                    idArray.clear();


                    for (int i=0;i<items.size();i++) {

                        if(items.get(i).isSelected()){
                            idArray.add(Long.parseLong(items.get(i).getId()));
                        }
                    }

                    Long[] ids = new Long[idArray.size()];

                    idArray.toArray(ids);

                    Log.e("[TEST]", "ids : " + Arrays.toString(ids));

                    pInterface.retrieveTracks(items.get(position), position, ids);
                }

            }
        });
        holder.textView02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listItemClickable == true) {


                    if (getPreferencesForEditMode().equals("true")) {
                        Log.e("[on]", "getPreferencesForEditMode true");
                        return;
                    }


                    pInterface.setItemSelected();

                    if (pInterface.getPreferencesForToken().equals("")) {
                        return;
                    }

                    if (pInterface.getPreferencesForToken().equals(null)) {
                        return;
                    }

//                Toast.makeText(mCtx, "재생 준비 중입니다.", Toast.LENGTH_SHORT).show();
                    pInterface.playTrack(item, position, false, false);
                }


                if (listItemClickable == false) {
                    items.get(position).setSelected(!items.get(position).isSelected());
//            selectedPosition = position;
                    notifyDataSetChanged();

                    ArrayList<Long> idArray = new ArrayList<Long>();

                    idArray.clear();


                    for (int i=0;i<items.size();i++) {

                        if(items.get(i).isSelected()){
                            idArray.add(Long.parseLong(items.get(i).getId()));
                        }
                    }

                    Long[] ids = new Long[idArray.size()];

                    idArray.toArray(ids);

                    Log.e("[TEST]", "ids : " + Arrays.toString(ids));

                    pInterface.retrieveTracks(items.get(position), position, ids);
                }
            }
        });

        holder.imageView01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (listItemClickable == true) {

                    if (getPreferencesForEditMode().equals("true")) {
                        Log.e("[on]", "getPreferencesForEditMode true");
                        return;
                    }

                    pInterface.setItemSelected();

                    if (pInterface.getPreferencesForToken().equals("")) {
                        return;
                    }

                    if (pInterface.getPreferencesForToken().equals(null)) {
                        return;
                    }
//                Toast.makeText(mCtx, "재생 준비 중입니다.", Toast.LENGTH_SHORT).show();
                    pInterface.playTrack(item, position, false, false);
                }

                if (listItemClickable == false) {
                    items.get(position).setSelected(!items.get(position).isSelected());
//            selectedPosition = position;
                    notifyDataSetChanged();

                    ArrayList<Long> idArray = new ArrayList<Long>();

                    idArray.clear();


                    for (int i=0;i<items.size();i++) {

                        if(items.get(i).isSelected()){
                            idArray.add(Long.parseLong(items.get(i).getId()));
                        }
                    }

                    Long[] ids = new Long[idArray.size()];

                    idArray.toArray(ids);

                    Log.e("[TEST]", "ids : " + Arrays.toString(ids));

                    pInterface.retrieveTracks(items.get(position), position, ids);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onListItemClick(int position) {

        Log.e("[TEST]", "[AudioPlaylistRecyclerAdapter]:::(onListItemClick)::");
        if (listItemClickable) {
            Log.e("[TEST]", " > Clickable");
////        Toast.makeText(mCtx, items.get(position).getId(), Toast.LENGTH_SHORT).show();
//
//            selectedPosition = position;
//            notifyDataSetChanged();
//
//            pInterface.setItemSelected();
//            if (pInterface.getPreferencesForToken().equals("")) {
//                return;
//            }
//            if (pInterface.getPreferencesForToken().equals(null)) {
//                return;
//            }
////            Toast.makeText(mCtx, "재생 준비 중입니다.", Toast.LENGTH_SHORT).show();
//            // TODO.
//            pInterface.playTrack(items.get(position), position, false, false);

        }

        if (!listItemClickable) {

            Log.e("[TEST]", " > not Clickable");

            items.get(position).setSelected(!items.get(position).isSelected());
//            selectedPosition = position;
            notifyDataSetChanged();

            ArrayList<Long> idArray = new ArrayList<Long>();

            idArray.clear();


            for (int i=0;i<items.size();i++) {

                if(items.get(i).isSelected()){
                    idArray.add(Long.parseLong(items.get(i).getId()));
                }
            }

            Long[] ids = new Long[idArray.size()];

            idArray.toArray(ids);

            Log.e("[TEST]", "ids : " + Arrays.toString(ids));

            pInterface.retrieveTracks(items.get(position), position, ids);

        }


    }

    @Override
    public void onItemDismiss(int position) {
//        items.remove(position);

        Log.e("[TEST]", "[AudioPlaylistRecyclerAdapter]:::(onItemDismiss):: 0 : CALLED ");

        willBeRemovedItems = new ArrayList<>();
        willBeRemovedItems.clear();

        for (int i=0;i<items.size();i++) {
            if(items.get(i).isSelected()) {

                Log.e("[TEST]", " > :::(deleted):: + position : " + position );
//                items.remove(i);
                willBeRemovedItems.add(items.get(i));

            }
        }
//
//        Log.e("[TEST]", "[AudioPlaylistRecyclerAdapter]:::(onItemDismiss):: 1 : part ");
//        Log.e("[TEST]", "[AudioPlaylistRecyclerAdapter]:::(onItemDismiss):: willBeRemovedItems : " + Arrays.toString(willBeRemovedItems.toArray()));

        for (int i=0;i<willBeRemovedItems.size();i++) {
            items.remove(willBeRemovedItems.get(i));
        }

        notifyDataSetChanged();
//        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.e("[TEST]", "[AudioPlaylistRecyclerAdapter]:::(onItemMove)::");
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
//        return true;
    }

    public int getSelectedPosition() {
        return this.selectedPosition;
    }


    public String getPreferencesForEditMode() {
        SharedPreferences pref = mCtx.getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("editMode", "");
        return temp;
    }
    //FROM AudioInterFace
    public void setPreferencesForEditMode(String editMode) {
        SharedPreferences pref = mCtx.getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("editMode", editMode);
        editor.commit();
    }

}
