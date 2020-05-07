package com.strawberryswing.upmusic.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefHelper {
	public SharedPreferences prefs;

	protected SharedPrefHelper() {
	}

	public boolean getSharedPreferences(String key, boolean defaultVal) {

		return prefs.getBoolean(key, defaultVal);
	}

	public String getSharedPreferences(String key, String defaultVal) {

		return prefs.getString(key, defaultVal);
	}

	public int getSharedPreferences(String key, int defaultVal) {

		return prefs.getInt(key, defaultVal);
	}

	public void setSharedPreferences(String key, boolean val) {

		Editor edit = prefs.edit();
		edit.putBoolean(key, val);
		edit.commit();
	}

	public void setSharedPreferences(String key, int val) {

		Editor edit = prefs.edit();
		edit.putInt(key, val);
		edit.commit();
	}

	public void setSharedPreferences(String key, String val) {

		Editor edit = prefs.edit();
		edit.putString(key, val);
		edit.commit();
	}

	public void setSharedFravoritePreferences(String key, int div) {

		Editor edit = prefs.edit();

		int cnt = getSharedPreferences(key, 0);

		if (div > 0) {
			++cnt;
		} else {
			--cnt;
			if (cnt < 0)
				cnt = 0;
		}

		edit.putInt(key, cnt);
		edit.commit();
	}
}
