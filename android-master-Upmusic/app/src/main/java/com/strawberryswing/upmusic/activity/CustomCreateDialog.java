package com.strawberryswing.upmusic.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.strawberryswing.upmusic.R;

public class CustomCreateDialog extends Dialog implements View.OnClickListener{
    private static final int LAYOUT = R.layout.custom_dialog_create;

    private Context context;
    private EditText nameEt;
    private Button btn1;

    private String name;

    public CustomCreateDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomCreateDialog(Context context, String name){
        super(context);
        this.context = context;
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        nameEt = (EditText) findViewById(R.id.listinput);

        btn1 = (Button) findViewById(R.id.input_bt);

        btn1.setOnClickListener(this);

        if(name != null){
            nameEt.setText(name);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.input_bt:
                name = nameEt.getText().toString().trim();
                Log.e("[TEST]" , " > input_bt : " + name);
                ((MainActivity) MainActivity.mContext).requestCollectionCreate(name);
//                this.dismiss();
                break;
        }
    }
}
