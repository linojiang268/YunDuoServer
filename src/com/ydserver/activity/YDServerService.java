package com.ydserver.activity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPException;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ydserver.data.MyNetData;
import com.ydserver.db.DeviceService;
import com.ydserver.tool.JavaMailTool;
import com.ydserver.tool.SMSMsgTool;
import com.ydserver.util.Client;
import com.ydserver.util.MsgUtil;
import com.ydserver.xmpp.MyXMPP;

import frame.ydserver.socket.ReadBody;

public class YDServerService extends SerialPortService {
	// public class DCServerService extends Service {
	private long lastTime = 0;
	private Timer mTimer;
	private TimerTask mTimerTask;
	public static boolean hasStart, isExit;
	private ServerSocket mServerSocket;
	public static ArrayList<Long> mMarkList;
	public static HashMap<Long, Client> mClientMap;
	// =================================需要定义的变量=========================================
	// 报警相关的
	// protected final int maxSmsSendNum = 3;// 最多三次
	// protected final long spaceSendSmsMsgTime = 300000;// 五分钟
	public static final int maxSmsSendNum = 3;// 最多三次
	public static final long spaceSendSmsMsgTime = 300000;// 五分钟(之后如果再收到上传数据，才报警)
	public static int sHasSendSmsMsgNum = 0;
	public static long sLastSendSmsMsgTime = 0;

	// =========================================================================

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lastTime = System.currentTimeMillis();
	}

	@Override
	public void onDestroy() {
		// VPNTool.disconnect(YDServerService.this);
		serverHandler.sendEmptyMessage(2);
		exit();
		super.onDestroy();
	}

	protected void exit() {
		isExit = true;
		hasStart = false;
		MyXMPP.closeConnection();
		// 关闭定时重连
		timeCancel();
		if (null != mServerSocket) {
			try {
				mServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mServerSocket = null;
		while (null != mMarkList && mMarkList.size() > 0) {
			long tmpMark = mMarkList.remove(0);
			Client tmpClient = mClientMap.remove(tmpMark);
			if (null != tmpClient) {
				tmpClient.close();
			}
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (null != mTimer) {
			// 服务已经开了
			return;
		}
		isExit = false;
		hasStart = false;
		// isLogining = false;
		// 服务器开启成功
		serverHandler.sendEmptyMessage(1);
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				// MyXMPP.setValue(YDServerService.this,
				// MyNetData.yunduoAddress, "yunduoCeshi", "123456",
				// MyNetData.yunduoGroupAddress);
				if ((!MyXMPP.isConnect()) && MyXMPP.isSet(YDServerService.this)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								MyXMPP.initXmpp(YDServerService.this, serverHandler);
								Log.i("YDServerService", "远程服务初始化成功");
								serverHandler.sendEmptyMessage(65);
							} catch (XMPPException e) {
								// e.printStackTrace();
								MyXMPP.closeConnection();
								Log.e("YDServerService", "远程服务初始化失败");
								// showToast("远程服务初始化失败");
								serverHandler.sendEmptyMessage(67);
							}
						}
					}).start();
				}
				if (!isExit && !hasStart) {
					hasStart = true;
					Log.i("DCServerServie", "DaChuangServer running " + (System.currentTimeMillis() - lastTime) / 60000 + " minute.");
					init();
				}
			}
		};
		mTimer.schedule(mTimerTask, 20, 120000);
	}

	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mServerSocket = new ServerSocket(6666);
					mMarkList = new ArrayList<Long>();
					mClientMap = new HashMap<Long, Client>();
					Log.i("DCClientService", "服务器创建成功");
					serverHandler.sendEmptyMessage(0);
					while (hasStart && !isExit) {
						Socket socket = mServerSocket.accept();
						long tmpMark = System.currentTimeMillis();
						// 客户端
						Client tmpClient = new Client(YDServerService.this, tmpMark, socket, serverHandler);
						mClientMap.put(tmpMark, tmpClient);
						mMarkList.add(tmpMark);
						tmpClient.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					mServerSocket = null;
					serverHandler.sendEmptyMessage(-1);
				}
			}
		}).start();
	}

	private Handler serverHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				hasStart = false;
				if (!isExit) {
					// 服务器重建
					Log.e("DCClientService", "服务器重建");
					showToast("服务器重建");
				}
				break;
			case 0:
				showToast("服务器创建成功");
				// Log.i("DCClientService", "连接服务器成功");
				break;
			case 1:
				Log.i("DCClientService", "服务开启成功");
				showToast("服务开启成功");
				// try {
				// VPNTool.connect(YDServerService.this);
				// } catch (Exception e) {
				// e.printStackTrace();
				// showToast("远程服务创建失败");
				// }
				break;
			case 2:
				Log.i("DCClientService", "服务关闭成功");
				showToast("服务关闭成功");
				break;
			case 3:
				Log.i("DCClientService", "客户端接入");
				showToast("客户端接入");
				break;
			case 4:
				Log.i("DCClientService", "客户端登录成功");
				showToast("客户端登录成功");
				break;
			case 5:
				Log.i("DCClientService", "客户端下线");
				showToast("客户端下线");
				break;
			case 6:
				// showToast((String) msg.obj);
				break;
			case 8:
				Log.i("DCClientService", "客户端连接关闭");
				Long mark = (Long) msg.obj;
				mMarkList.remove(mark);
				Client tmpClient = mClientMap.remove(mark);
				if (null != tmpClient) {
					tmpClient.close();
				}
				break;
			case 65:
				showToast("远程服务初始化成功");
				JavaMailTool.send("云朵提醒：" + MyXMPP.getAdminName(YDServerService.this) + "上线", "恭喜您，'" + MyXMPP.getAdminName(YDServerService.this) + "设备'连接'派克远程中转CentOs服务器'成功！ " + new Date().toLocaleString(), "mywork@ouarea.cc");
				break;
			case 66:
				ReadBody readBody = (ReadBody) msg.obj;
				try {
					if (6 == readBody.mark && 6 == readBody.type) {
						MyXMPP.sendMulMsg(readBody.mark, readBody.type, readBody.msg);
					}
					// MyXMPP.sendMulMsg("服务器返回" +
					// bd.getString("from").substring(bd.getString("from").indexOf("/"))
					// + "的消息：" + bd.getString("body"));
				} catch (Exception e) {
					// e.printStackTrace();
					MyXMPP.closeConnection();
				}
				break;
			case 67:
				showToast("远程服务初始化失败");
				// JavaMailTool.send("云朵提醒：" +
				// MyXMPP.getAdminName(YDServerService.this) + "下线", "警告，'" +
				// MyXMPP.getAdminName(YDServerService.this) +
				// "设备'连接'派克远程中转CentOs服务器'失败！ " + new Date().toLocaleString(),
				// "mywork@ouarea.cc");
				break;
			default:
				break;
			}
		}

	};

	public void timeCancel() {
		if (null != mTimer) {
			mTimer.cancel();
		}
		if (null != mTimerTask) {
			mTimerTask.cancel();
		}
		mTimer = null;
		mTimerTask = null;
	}

	private void showToast(String str) {
		Toast.makeText(YDServerService.this, str, Toast.LENGTH_SHORT).show();
	}

	// ===================================================================================
	// ======================================底层对接===================================
	// ===================================================================================
	// 读取到的信息
	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		if (null == MsgUtil.sDeviceService) {
			// 初始化设备的数据库表操作对象
			MsgUtil.sDeviceService = new DeviceService(YDServerService.this);
		}
		if (null != MainTest.mainTest) {
			MainTest.mainTest.onDataReceived(buffer, size);
		}
	}

	int n = 0;
	byte[] Databuffer = new byte[6000];
}
