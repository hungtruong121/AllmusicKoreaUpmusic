package com.strawberryswing.upmusic.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by Brian on 19/08/2015.
 */
public class SharedPreferenceBase {

    private static final String SHARED_PREF_NAME = "card";

    private static Context context;

    public static SharedPreferenceBase init(Context context) {
        setContext(context);
        return new SharedPreferenceBase();
    }

    public static void setContext(Context context) {
        SharedPreferenceBase.context = context;
    }

    protected static void setString(final String key, final String value) {
        getPrefs().edit().putString(key, value).commit();
    }

    protected static SharedPreferences getPrefs() {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }
//
//    public static HashSet<String> getCookies(Context context) {
//        SharedPreferences mcpPreferences = getSKSharedPreferences(context);
//        return (HashSet<String>) mcpPreferences.getStringSet("cookies", new HashSet<String>());
//    }
//
//    public static boolean setCookies(Context context, HashSet<String> cookies) {
//        SharedPreferences mcpPreferences = getSKSharedPreferences(context);
//        SharedPreferences.Editor editor = mcpPreferences.edit();
//        return editor.putStringSet("cookies", cookies).commit();
//    }


}