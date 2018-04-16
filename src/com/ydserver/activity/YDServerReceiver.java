package com.ydserver.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class YDServerReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent) {
			return;
		}
		// if
		// (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
		// }
		// Intent intent_has = new Intent(context, YDServerReceiver.class);
		// intent_has.putExtra("background", O"");
		// context.getApplicationContext().startService(intent_has);
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Toast.makeText(context, "3秒后自启动云朵智能家居管理系统", Toast.LENGTH_LONG).show();
			final Context mContext = context;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			}, 3000);
		}
		// context.getApplicationContext().startActivity(new Intent(context,
		// MainActivity.class));
	}

	// /**
	// * 检测是否符合后台登录条件
	// *
	// * @Title: checkBackGroundLogin
	// * @Description: TODO
	// * @param context
	// * @return
	// * @author: ouArea
	// * @return boolean
	// * @throws
	// */
	// private boolean checkBackGroundLogin(Context context) {
	// SharedPreferences sharedPreferences =
	// context.getSharedPreferences("user", Context.MODE_PRIVATE);
	// if (!sharedPreferences.getBoolean("BackGroundLogin", true)) {
	// return false;
	// }
	// if (!sharedPreferences.getBoolean("ThisLoginSuccess", false)) {
	// return false;
	// }
	// return true;
	// }
}
