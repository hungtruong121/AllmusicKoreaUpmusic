package com.strawberryswing.upmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.activity.MainActivity;
import com.strawberryswing.upmusic.model.Collection;
import com.strawberryswing.upmusic.model.MusicTrack;

import java.util.ArrayList;

public class CustomDiaglogListViewAdapter extends BaseAdapter {
 
    private ArrayList<Collection> list;
    private Activity activity;
     
    // 생성할 클래스
    CustomDiaglogListViewAdapter(Activity activity){
        this.activity = activity;
        list = new ArrayList<Collection>();
    }
     
    // 리스트에 값을 추가할 메소드
    public void addCollection(Collection collection)
    {
        Log.e("[TEST]", collection.getSubject() + "is added.");
        list.add(0, collection);
        notifyDataSetChanged();
    }

    public void setCollectionlist(ArrayList<Collection> namelist) {
        list = namelist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // 리스트뷰 갯수 리턴
        return list.size();
    }
 
    @Override
    public Object getItem(int position) {
        // 리스트 값 리턴
        return list.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder    holder  = null;
        final int pos = position;
        TextView name;
         
        // 최초 뷰 생성
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             
            convertView = inflater.inflate(R.layout.custom_dialog_cell, parent, false);
            name    = (TextView) convertView.findViewById(R.id.name_text);
             
            holder = new ListViewHolder();
            holder.name = name;
             
            // list values save
            convertView.setTag(holder);
            // 텍스트 보이기
            name.setVisibility(View.VISIBLE);
        }
        else
        {
            // list values get
            holder = (ListViewHolder) convertView.getTag();
            name = holder.name;
        }

        final Collection item = list.get(position);
         
        // 리스트 이름 보이기
        name.setText(item.getSubject());

        // 리스트 아이템을 터치 했을 때 이벤트 발생
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(activity.getApplicationContext(), "선택한 이름:" + list.get(pos).getSubject(), Toast.LENGTH_SHORT).show();

                Log.e("[TEST]",  "선택한 이름:" + item.getSubject());

                ((MainActivity)MainActivity.mContext).requestCollectionAddTrackByID(item.getId());


            }
        });
         
        // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
 
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(activity.getApplicationContext(), list.get(pos) + " 삭제합니다.", Toast.LENGTH_SHORT).show();
                // list choice remove
//                list.remove(pos);
                // listview update
//                MainActivity.listview.clearChoices();
//                MainActivity.listviewadapter.notifyDataSetChanged();
                return false;
            }
        });
         
        return convertView;
    }
 
    // list values class
    private class ListViewHolder {
        TextView name;
    }
}
