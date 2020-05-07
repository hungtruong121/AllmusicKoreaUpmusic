package com.strawberryswing.upmusic.util.jsinterface;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.strawberryswing.upmusic.R;

import org.json.JSONObject;


public class WebActivity extends AppCompatActivity {

    private static final String TAG = WebActivity.class.getSimpleName();
    private static final String DEFAULT_URL = "http://up-music.ap-northeast-2.elasticbeanstalk.com"; // "http://10.0.2.2:8080";
    private static final String USER_AGENT = "UPMusicAndroid";
    private static final String HANDLER_NAME = "AndroidHandler";

    // 웹뷰로부터 전달받는 요청 종류
    private static final String REQUEST_LOGIN_DEFAULT = "REQUEST_LOGIN_DEFAULT";
    private static final String REQUEST_LOGIN_FACEBOOK = "REQUEST_LOGIN_FACEBOOK";
    private static final String REQUEST_LOGIN_GOOGLE = "REQUEST_LOGIN_GOOGLE";
    private static final String REQUEST_LOGIN_KAKAO = "REQUEST_LOGIN_KAKAO";
    private static final String REQUEST_LOGIN_NAVER = "REQUEST_LOGIN_NAVER";
    private static final String REQUEST_PASSWORD_CHANGE = "REQUEST_PASSWORD_CHANGE";
    private static final String REQUEST_PLAYLIST_UPDATE = "REQUEST_PLAYLIST_UPDATE";
    private static final String REQUEST_PLAY_VIDEO = "REQUEST_PLAY_VIDEO";

    private ObservableWebView mWebView;
    private ProgressBar mProgressBar;
    private WebHandler mWebHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);

        // 진행바 - 필요시
        mProgressBar = (ProgressBar) findViewById(R.id.articleview_progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(mWebView.VISIBLE);

        // 웹뷰 정의 및 설정
        mWebView = (ObservableWebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.invalidate();
            }
        });
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setAppCacheEnabled(false);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setUserAgentString(USER_AGENT);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(false);
        mWebView.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int l, int t) {
                InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mWebView.getWindowToken(), 0);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);

                if (newProgress == 100) {
                    mProgressBar.setVisibility(view.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(view.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        // 업뮤직 페이지 불러오기. 리스너를 추가하여 이벤트 출력
        mWebView.loadUrl(DEFAULT_URL);
        mWebHandler = new WebHandler(this, mWebView, new WebTrackListener() {
            @Override
            public void callbackFromJS(String jsonData) {
                Log.d(TAG, "callbackFromJS : jsonData - " + jsonData);
                try {
                    JSONObject jsonObj = new JSONObject(jsonData);
                    String requestType = jsonObj.getString("request_type");
                    Log.d(TAG, "callbackFromJS : requestType - " + requestType);
                    switch (requestType) {
                        case REQUEST_LOGIN_DEFAULT: // 일반 로그인 요청
                            String email = jsonObj.getString("email");
                            String password = jsonObj.getString("password");
                            // TODO 로그인 처리
                            break;
                        case REQUEST_LOGIN_FACEBOOK: // 페이스북 로그인 요청
                            // TODO 로그인 처리
                            break;
                        case REQUEST_LOGIN_GOOGLE: // 구글 로그인 요청
                            // TODO 로그인 처리
                            break;
                        case REQUEST_LOGIN_KAKAO: // 카카오 로그인 요청
                            // TODO 로그인 처리
                            break;
                        case REQUEST_LOGIN_NAVER: // 네이버 로그인 요청
                            // TODO 로그인 처리
                            break;
                        case REQUEST_PASSWORD_CHANGE: // 비밀번호 변경 요청
                            String newPassword = jsonObj.getString("new_password");
                            // TODO 비밀번호 변경
                        case REQUEST_PLAYLIST_UPDATE: // 재생목록 업데이트 요청
                            // TODO 서버에서 재생목록 불러오기
                            break;
                        case REQUEST_PLAY_VIDEO: // 영상 재생 요청
                            JSONObject jsonVideo = jsonObj.getJSONObject("video");
                            Log.d(TAG, "callbackFromJS : video id - " + jsonVideo.getString("id"));
                            Log.d(TAG, "callbackFromJS : video subject - " + jsonVideo.getString("subject"));
                            Log.d(TAG, "callbackFromJS : video thumbnail - " + jsonVideo.getString("thumbnail"));
                            Log.d(TAG, "callbackFromJS : video duration - " + jsonVideo.getString("duration"));
                            Log.d(TAG, "callbackFromJS : video filename - " + jsonVideo.getString("filename"));
                            Log.d(TAG, "callbackFromJS : video filenameUrl - " + jsonVideo.getString("filenameUrl"));
                            // TODO 영상 재생
                            break;
                    }

                } catch (Exception ex) {

                }
            }
        });
        mWebView.addJavascriptInterface(mWebHandler, HANDLER_NAME);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
