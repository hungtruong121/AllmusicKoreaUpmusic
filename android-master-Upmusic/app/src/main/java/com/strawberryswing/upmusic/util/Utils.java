package com.strawberryswing.upmusic.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Utils {

    /**
     * 디바이스 아이디 가지고 오기
     *
     * @return String : 휴대폰 아아디
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceID(Context mCtx) {
        String DeviceID = "";
        try {
            DeviceID = ((TelephonyManager) mCtx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
        }
        return DeviceID;
    }

    /**
     * 휴대폰 번호 가지고 오기
     *
     * @return String : 휴대폰 전화
     */

    public static String getPhoneNumber(Context mCtx) {
        TelephonyManager mgr = (TelephonyManager) mCtx.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = "";
        try {
            phoneNumber = mgr.getLine1Number().replace("+82", "0");
        } catch (Exception e) {
        }
        return phoneNumber;
    }

    private static Toast toast;

    public static void toastShowing(Context mCtx, String msg) {
        if (toast == null) {
            toast = Toast.makeText(mCtx, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
