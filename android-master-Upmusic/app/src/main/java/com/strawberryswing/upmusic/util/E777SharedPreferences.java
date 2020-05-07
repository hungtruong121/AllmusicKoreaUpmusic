package com.strawberryswing.upmusic.util;

import android.app.Activity;
import android.content.Context;

public class E777SharedPreferences extends SharedPrefHelper {
    private static E777SharedPreferences instance;
    private static Context mCtx;

    // Preference
    public static final String PREFERENCE_NAME = "MAMMAFRONT_PREF";

    public static final String PUSHTOKEN = "PUSHTOKEN";
    public static final String USERID = "USERID";
    public static final String APPVERSION = "APPVERSION";

    private E777SharedPreferences() {
        super();
    }

    public static E777SharedPreferences getInstance(Context context) {
        mCtx = context;
        if (instance == null) {
            instance = new E777SharedPreferences();
            instance.prefs = context.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
        }
        return instance;
    }

    // ###############################################################
    // ## ##
    // ## Getter&Setter ##
    // ## ##
    // ###############################################################
    public String getPushToken() {
        return getSharedPreferences(PUSHTOKEN, "");
    }

    public void setPushToken(String token) {
        setSharedPreferences(PUSHTOKEN, token);
    }

    public String getUserId() {
        return getSharedPreferences(USERID, "");
    }

    public void setUserId(String userId) {
        setSharedPreferences(USERID, userId);
    }

    public String getAppVersion() {
        return getSharedPreferences(APPVERSION, "");
    }

    public void setAppVersion(String appVersion) {
        setSharedPreferences(APPVERSION, appVersion);
    }
}