package com.ydserver.util;

import java.net.Socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import frame.ydserver.socket.MySocketCon;
import frame.ydserver.socket.ReadBody;

/**
 * 客户端
 * 
 * @author ouArea
 * 
 */
public class Client {
	private Context context;
	public long mark;
	public ReadThread readThread;
	public Handler mHandler;

	public Client(Context context, long mark, Socket socket, Handler handler) {
		super();
		this.context = context;
		this.mark = mark;
		this.readThread = new ReadThread(mark, socket);
		this.mHandler = handler;
	}

	public void start() {
		readThread.start();
	}

	public void close() {
		if (null != readThread) {
			readThread.closeRead();
		}
		readThread = null;
	}

	public void send(int mark, int type, String msg) {
		readThread.mCon.sendZlib(mark, type, msg);
	}

	/**
	 * 读取线程
	 * 
	 * @author ouArea
	 * 
	 */
	class ReadThread extends Thread {
		public String userName;
		// -------------------------------
		private ReadBody readBody;
		private MsgUtil msgUtil;
		private boolean isRun;
		private MySocketCon mCon;

		public ReadThread(long mark, Socket socket) {
			this.mCon = new MySocketCon(mark, socket);
			this.readBody = new ReadBody();
			this.msgUtil = new MsgUtil(context, readBody);
		}

		@Override
		public void run() {
			super.run();
			isRun = true;
			try {
				mHandler.sendEmptyMessage(3);
				Log.i("ReadThread", "begin接收客户端:" + mCon.mark);
				// Log.i("ReadThread", "begin接收:" +
				// mCon.con.getInetAddress().getHostName() + "," +
				// mCon.con.getPort());
				// mCon.sendZlib(0, 0, "0");
				mCon.outStream.writeUTF("1");
				mCon.outStream.flush();
				if (!mCon.inStream.readUTF().equals("1")) {
					isRun = false;
				}
			} catch (Exception e) {
				isRun = false;
				// e.printStackTrace();
			}
			try {
				while (isRun && mCon.readZlib(readBody)) {
					if (readBody.mark < 0) {
						continue;
					}
					Message message = new Message();
					message.what = 6;
					message.obj = "收到：\nmark:" + readBody.mark + "\ntype:" + readBody.type + "\nmsg:" + readBody.msg;
					mHandler.sendMessage(message);
					if (5001 == readBody.type && null == msgUtil.parse(null)) {
						mCon.sendZlib(readBody.mark, readBody.type, "{\"result\":0,\"msg\":\"登录成功\"}");
					} else {
						mCon.sendZlib(readBody.mark, readBody.type, "{\"result\":1,\"msg\":\"登录失败\"}");
						Thread.sleep(20);
						isRun = false;
						message = new Message();
						message.what = 8;
						message.obj = mark;
						mHandler.sendMessage(message);
					}
					break;
				}
			} catch (Exception e) {
				isRun = false;
				// e.printStackTrace();
			}
			boolean isFirst = true;
			try {
				while (true) {
					if (isRun && mCon.readZlib(readBody)) {
						if (readBody.mark <= 0) {
							continue;
						}
						try {
							if (isFirst) {
								isFirst = false;
								mHandler.sendEmptyMessage(4);
							}
							// 记录
							Message message = new Message();
							message.what = 6;
							message.obj = "收到：\nmark:" + readBody.mark + "\ntype:" + readBody.type + "\nmsg:" + readBody.msg;
							mHandler.sendMessage(message);
							// 测试
							// mCon.sendZlib(readBody.mark, readBody.type,
							// "发送成功：" +
							// readBody.msg);
							// mCon.sendZlib(0, 6000,
							// "{\"result\":0,\"msg\":\"登录成功\"}");
							// 处理
							String reStr = null;
							try {
								reStr = msgUtil.parse(null);
							} catch (Exception e) {
								e.printStackTrace();
								mCon.sendZlib(readBody.mark, readBody.type, "{\"result\":1,\"msg\":\"参数错误，操作失败\"}");
							}
							if (null != reStr) {
								mCon.sendZlib(readBody.mark, readBody.type, reStr);
							}
							sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
			} catch (Exception e) {
				isRun = false;
				// e.printStackTrace()
			}

			Log.i("ReadThread", "ReadThread close.");
			mHandler.sendEmptyMessage(5);
		}

		public void closeRead() {
			isRun = false;
			if (null != mCon) {
				mCon.close();
			}
			mCon = null;
		}

	}
}
