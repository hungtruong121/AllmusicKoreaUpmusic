package com.strawberryswing.upmusic.util;

import android.app.Activity;
import android.widget.Toast;

import com.strawberryswing.upmusic.R;


public class BackPressCloseHandler {
	private long backKeyPressedTime = 0;
	private Toast toast;

	private Activity activity;

	public BackPressCloseHandler(Activity context) {
		this.activity = context;
	}

	public void onBackPressed() {
		if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
			backKeyPressedTime = System.currentTimeMillis();
			showGuide();
			return;
		}
		if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
			activity.finish();
			toast.cancel();
		}
	}

	public void onAllBackPressed() {
		if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
			backKeyPressedTime = System.currentTimeMillis();
			showGuide();
			return;
		}
		if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
			activity.moveTaskToBack(true);
			activity.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			toast.cancel();
		}
	}


	private void showGuide() {
		Utils.toastShowing(activity, activity.getString(R.string.app_finish));
	}
}
