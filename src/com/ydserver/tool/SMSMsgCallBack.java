package com.ydserver.tool;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class SMSMsgCallBack extends Handler {
	public static final int SUCCESS = 0, FAIL = 1;
	private Context mContext;

	public SMSMsgCallBack(Context context) {
		this.mContext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case 0:
			// Toast.makeText(mContext, "发送成功\n" + msg.obj,
			// Toast.LENGTH_SHORT).show();
			back(SUCCESS, (String) msg.obj);
			break;
		case 1:
			// Toast.makeText(mContext, "发送失败\n没有结果！\n" + msg.obj,
			// Toast.LENGTH_SHORT).show();
			back(FAIL, (String) msg.obj);
			break;
		default:
			break;
		}
	}

	public abstract void back(int status, String msg);
}