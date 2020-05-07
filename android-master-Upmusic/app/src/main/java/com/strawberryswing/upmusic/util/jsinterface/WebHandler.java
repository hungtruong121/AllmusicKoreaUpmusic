package com.strawberryswing.upmusic.util.jsinterface;

import android.app.Activity;

public class WebHandler {

    private static final String TAG = WebHandler.class.getSimpleName();

    Activity mContext;
    ObservableWebView mWebView;
    WebTrackListener mWebTrackLister;

    public WebHandler(Activity context, ObservableWebView webView, WebTrackListener webTrackLister) {
        mContext = context;
        mWebView = webView;
        mWebTrackLister = webTrackLister;
    }

    @android.webkit.JavascriptInterface
    public void callbackFromJS(final String jsonData) {
        mWebTrackLister.callbackFromJS(jsonData);
    }

    public void callWeb(String msg) {
        final String webUrl = "javascript:callWeb('" + msg + "')";
        if (!mContext.isFinishing()) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(webUrl);
                }
            });
        }
    }

}
