package com.strawberryswing.upmusic.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.strawberryswing.upmusic.R;
import com.strawberryswing.upmusic.util.cookieUsage.DefaultRestClient;
import com.strawberryswing.upmusic.util.BackPressCloseHandler;
import com.strawberryswing.upmusic.util.E777SharedPreferences;
import com.strawberryswing.upmusic.util.cookieUsage.AddCookiesInterceptor;
import com.strawberryswing.upmusic.util.cookieUsage.ReceivedCookiesInterceptor;


public class BaseActivity extends AppCompatActivity implements OnClickListener {

    protected AlertDialog dialog;
    protected Context mCtx;

    public static  Context context;

    protected E777SharedPreferences pref;
    CookieManager cookieManager;
    protected BackPressCloseHandler backPressCloseHandler;

    protected String token;

    private ViewDataBinding binding;

    public DefaultRestClient<UpmuicAPI> defaultRestClient;
    public UpmuicAPI retrofitService;
    public ReceivedCookiesInterceptor receivedCookiesInterceptor;
    public AddCookiesInterceptor addCookiesInterceptor;


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    // ###############################################################
    // ##                                                           ##
    // ##                       Override                            ##
    // ##                                                           ##
    // ###############################################################
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base);

        mCtx = this;
        context = this;

        FirebaseApp.initializeApp(context);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
//                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 아이콘 색상.
                getWindow().setStatusBarColor(Color.parseColor("#181e33"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.parseColor("#181e33"));
        }



        pref = E777SharedPreferences.getInstance(mCtx);
        backPressCloseHandler = new BackPressCloseHandler(this);
        receivedCookiesInterceptor = new ReceivedCookiesInterceptor(this);
        addCookiesInterceptor = new AddCookiesInterceptor(this);

        // cookie
        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();

        // retrofit set
        defaultRestClient = new DefaultRestClient<>();
        retrofitService = defaultRestClient.getClient(UpmuicAPI.class);

        // FCM token
        FirebaseMessaging.getInstance().subscribeToTopic("1041644148359");
        token = FirebaseInstanceId.getInstance().getToken();
//        System.out.println("sjw token01 : " + token);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.createInstance(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().startSync();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //noinspection deprecation
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }


    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }


    @Override
    public void onClick(View v) {
    }

    public void permissonCheck(PermissionListener permissionListener, String[] permissionList) {
        new TedPermission(mCtx)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(null)
                .setDeniedMessage(getString(R.string.alert_msg_permission_text))
                .setPermissions(permissionList)
                .check();
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}