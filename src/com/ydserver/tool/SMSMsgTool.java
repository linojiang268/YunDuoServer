package com.ydserver.tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.os.Message;

public class SMSMsgTool {
	private static String getUrl(String mobile, String context) {
		// 短信通网址：www.ss106.cn
		// 账号：dc
		// 密码：123
		// 短信ID 5
		String smsID = "5";
		String passwd = "123";
		String LoginName = "dc";
		// String mobile = "13922455188";
		// String contexts = "小明同学，2013第二学期期中考成绩:语文89数学104英语97总分290";
		// String mobile = metPhone.getText().toString();
		// String contexts = metMsg.getText().toString();
		String urls = null;
		try {
			urls = "http://www.ss106.cn/smsComputer/smsComputersend.asp?dxlbid=" + smsID + "&zh=" + LoginName + "&mm=" + passwd + "&hm=" + mobile + "&nr=" + URLEncoder.encode(context, "GB2312") + "";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return urls;
	}

	public static void send(String mobile, String context, final SMSMsgCallBack smsMsgCallBack) {
		final String urlStr = getUrl(mobile, context);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = "";
				try {
					URL url = new URL(urlStr);
					System.out.println(urlStr);
					URLConnection connection = url.openConnection();
					connection.connect();
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}
					in.close();
					if (null != smsMsgCallBack) {
						Message msg = new Message();
						msg.what = 0;
						msg.obj = result;
						smsMsgCallBack.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					result = "产生异常" + e;
					if (null != smsMsgCallBack) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = result;
						smsMsgCallBack.sendMessage(msg);
					}
				}
			}
		}).start();
		// return result;
	}
}
