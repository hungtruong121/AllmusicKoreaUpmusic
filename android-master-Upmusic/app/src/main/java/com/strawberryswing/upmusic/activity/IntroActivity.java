package com.strawberryswing.upmusic.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.gun0912.tedpermission.PermissionListener;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.databinding.ActivityIntroBinding;

import java.util.ArrayList;


public class IntroActivity extends BaseActivity {

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        PermissionListener qrcodePermission = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveActivity();
                    }
                }, 1000);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage(getString(R.string.alert_msg_permission_denied_text))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
        };

        String[] qrcodePermissionList = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        permissonCheck(qrcodePermission, qrcodePermissionList);
    }

    private void moveActivity() {
//        Bundle extras = getIntent().getExtras();
////        Intent intent = new Intent(mCtx, MainActivityMam.class);
//
//        if (extras != null && extras.containsKey("url") && !TextUtils.isEmpty(extras.getString("url"))) {
//            intent.putExtra("url", extras.getString("url"));
//        }
//
//        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
//            Uri uri = getIntent().getData();
//            intent.putExtra("scheme_url", uri.toString());
//        }
//
//        startActivity(intent);
//        finish();
    }
}
