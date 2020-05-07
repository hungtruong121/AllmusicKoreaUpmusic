package com.strawberryswing.upmusic.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.auth.KakaoSDK;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.ArrayList;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import androidx.multidex.MultiDex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;


public class GlobalApplication extends Application {

    public static final String CHANNEL_ID = "UpmusicApplication";
    private static final String TAG = "" + "[ AudioApplication ]";

    private static volatile Activity currentActivity = null;

    public static Context sAppContext;
    private static GlobalApplication mInstance;
    private AudioServiceInterface mInterface;

    //for jks : upmusicEnjoy!@#

    @Override
    public void onCreate() {
        super.onCreate();

        sAppContext = this;
        mInstance = this;
        mInterface = new AudioServiceInterface(getApplicationContext());



        KakaoSDK.init(new KakaoSDKAdapter());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
//        logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));

        // enable cookies
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /**
     * singleton 애플리케이션 객체를 얻는다.
     *
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if (mInstance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return mInstance;
    }



    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        mInstance = null;
    }


    /**
     *   Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
     *   현재 사용되는(되었던) Activity 저장을 위해.
     */
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    public static GlobalApplication getInstance() {
        return mInstance;
    }

    public AudioServiceInterface getServiceInterface() {
        return mInterface;
    }








    public String getPreferencesForToken() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String temp = pref.getString("token", "");
        return temp;
    }

    public void setPreferencesForToken(String token) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
    }




    //데이터 가져오기 isOnShuffle
    public boolean getPreferencesForShuffle(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getBoolean("isOnShuffle", false); //key, value(defaults)

    }

    //데이터 저장하기 isOnShuffle
    public void savePreferencesForShuffle(boolean temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isOnShuffle", temp);
        editor.commit();
    }

    //데이터 가져오기 REPEAT_MODE
    public String getPreferencesForRepeat(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getString("REPEAT_MODE", ""); //key, value(defaults)

    }

    //데이터 저장하기 REPEAT_MODE
    public void savePreferencesForRepeat(String temp){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("REPEAT_MODE", temp);
        editor.commit();
    }




    //데이터 가져오기 lastPlayedTrackNumber
    public int getPreferencesForLastPlayedTrackNumber(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("PREF_STRNAME", ""); //key, value(defaults)
        return pref.getInt("lastPlayedTrackNumber", 0); //key, value(defaults)

    }

    //데이터 저장하기
    public void savePreferencesForLastPlayedTrackNumber(int position){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("lastPlayedTrackNumber", position);
        editor.commit();
    }
    //데이터 삭제
    public void removePreferencesForLastPlayedTrackNumber(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("lastPlayedTrackNumber");
        editor.commit();
    }

    //모든 데이터 삭제
    public void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * USE WHEN GLOBAL VALUES FOR LIST.
     * FOR STORE : setStringArrayPref
     * FOR RETRIEVE : getStringArrayPref
     // store preference
     * @Example
     * ArrayList<String> list = new ArrayList<String>(Arrays.asList(urls));
     * setStringArrayPref(this, "urls", list);
     */

    public void setStringArrayPref(Context context,String key,ArrayList<String> values){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray a = new JSONArray();
        for (int i=0;i<values.size();i++) {
            a.put(values.get(i));
        }

        if(!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key,null);
        }
        editor.commit();
    }

    /**
     * // retrieve preference
     * @Example
     * list = getStringArrayPref(this, "urls");
     * urls = (String[]) list.toArray();
     */
    public ArrayList<String> getStringArrayPref(Context context,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key,null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i=0;i<a.length();i++) {
                    String url = a.optString(i);
                    urls.add(url);;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

}
