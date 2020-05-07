package com.strawberryswing.upmusic.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.databinding.DialogMenuBinding;
import com.strawberryswing.upmusic.util.E777SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DialogMenu extends BaseDialog {

    private DialogMenuBinding binding;
    private Context mCtx;
    private int type;
    private E777SharedPreferences pref;
    private String cnt;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DialogMenu(Context mCtx, String cnt) {
        super(mCtx);
        this.mCtx = mCtx;
        this.cnt = cnt;
        pref = E777SharedPreferences.getInstance(mCtx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(mCtx), R.layout.dialog_menu, null, false);
        setContentView(binding.getRoot());

        String type = "UTF-8";
        String userId = "";
        try {
            userId = URLDecoder.decode(pref.getUserId(), type);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        byte[] decodedBytes = Base64.decode(userId, 0);
        String usereIdDecodedString = new String(decodedBytes);
        binding.menuName.setText(TextUtils.isEmpty(usereIdDecodedString) ? "비회원상태입니다." : usereIdDecodedString);

        if (cnt.equals("0")) {
            binding.summaryTxt.setVisibility(View.GONE);
        } else {
            binding.summaryTxt.setVisibility(View.VISIBLE);
            binding.summaryTxt.setText(cnt);
        }

        binding.setDialog(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu01:
                setType(1);
                break;

            case R.id.menu02:
                setType(2);
                break;

            case R.id.menu03:
                setType(3);
                break;

            case R.id.menu04:
                setType(4);
                break;

            case R.id.menu05:
                setType(5);
                break;

            case R.id.menu06:
                setType(6);
                break;

            case R.id.menu_close:
                setType(3);
                break;
        }
        dismiss();
        setCancel(true);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return false;
    }
}