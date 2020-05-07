package com.strawberryswing.upmusic.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.strawberryswing.upmusic.R;

public class CustomDeleteConfirmDialog extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.custom_dialog_confirm_or_not;

    private Context context;
    private Button btn1, btn2;

    private String name;

    public CustomDeleteConfirmDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomDeleteConfirmDialog(Context context, String name){
        super(context);
        this.context = context;
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);


        btn1 = (Button) findViewById(R.id.input_bt1);
        btn2 = (Button) findViewById(R.id.input_bt2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.input_bt1:
                // 확인 (삭제)
                ((MainActivity) MainActivity.mContext).
                        deleteTrack(null, 0 , null);

                this.dismiss();
                break;
            case R.id.input_bt2:
                // 취소
                this.dismiss();
                break;
        }
    }
}
