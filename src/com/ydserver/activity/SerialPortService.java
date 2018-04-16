/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.ydserver.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.app.Service;
import android.util.Log;
import android.widget.Toast;
import android_serialport_api.SerialPort;

public abstract class SerialPortService extends Service {

	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;

	public static String strs;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = (Application) getApplication();
		try {
			// 连接建立辅助类
			mSerialPort = mApplication.getSerialPort();
			// 得到输出流
			mOutputStream = mSerialPort.getOutputStream();
			// 得到输入流
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
			SerialPort.sp = mSerialPort;
		} catch (SecurityException e) {
			Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
			Log.e("error", getString(R.string.error_security));
			// DisplayError(R.string.error_security);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
			Log.e("error", getString(R.string.error_unknown));
			// DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			Toast.makeText(getApplicationContext(), "设置失败", Toast.LENGTH_SHORT).show();
			Log.e("error", getString(R.string.error_configuration));
			// DisplayError(R.string.error_configuration);
		}
	}

	@Override
	public void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		mApplication.closeSerialPort();
		mSerialPort = null;
		super.onDestroy();
	}

	// -----------------------------------------------------------------------------------
	protected abstract void onDataReceived(final byte[] buffer, final int size);

	/**
	 * w 读取线程
	 * 
	 * @author 宋奇明
	 * 
	 */
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];

					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);

					if (size > 0) {
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		@SuppressWarnings("unused")
		private void swich(int j) {
		}
	}
	// -----------------------------------------------------------------------------------
	// @SuppressWarnings("unused")
	// private String CodeAnalysis(byte[] cmd) {
	// String str;
	// // CRC校验
	// // return null;
	//
	// switch (cmd[1] & 0xFF) {
	// case 0xC0:
	// String needStr = Htool.bytesToHexString(cmd);
	// str = "Code:" + needStr;
	// return str;
	// case 0xC1:
	// str = new String("learnOK");
	// return str;
	// case 0xC2:
	// str = new String("performOK");
	// return str;
	// case 0xC3:
	// str = new String("performError");
	// return str;
	// default:
	// break;
	// }
	//
	// return null;
	// }
}
