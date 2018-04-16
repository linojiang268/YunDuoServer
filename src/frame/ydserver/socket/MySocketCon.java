package frame.ydserver.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Socket连接类
 * 
 * @author ouArea
 * 
 */
public class MySocketCon {
	@SuppressWarnings("unused")
	private final String TAG = "MySocketCon";

	public long mark;
	public Socket con = null;
	public DataOutputStream outStream;
	public DataInputStream inStream;
	// private byte[] readBytes;

	// private final int multiThreadNum = 3;
	// private ExecutorService pool;
	private Handler mHandler;
	{
		// charset = Charset.forName("UTF-8");
		java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
		java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
	}

	/**
	 * 初始化配置(服务端)
	 * 
	 * @param context
	 * @param handler
	 * @param address
	 * @param port
	 */
	public MySocketCon(long mark, Socket socket) {
		super();
		this.mark = mark;
		this.con = socket;
		try {
			this.inStream = new DataInputStream(socket.getInputStream());
			this.outStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			closeWithException();
		}
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	/**
	 * 发送
	 * 
	 * @Title: sendZlib
	 * @Description: TODO
	 * @param type
	 * @param msg
	 * @author: ouArea
	 * @return void
	 * @throws
	 */
	public void sendZlib(int mark, int type, String msg) {
		if (null == sendThread || !sendThread.isAlive()) {
			sendThread = new SendThread();
			// getPool().execute(sendThread);
			// getPool().shutdown();
			sendThread.start();
			while (null == sendThread || null == sendThread.mHandler) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("mark", mark);
			jsonObject.put("type", type);
			jsonObject.put("msg", msg);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		// Bundle bundle = new Bundle();
		// bundle.putInt("mark", mark);
		// bundle.putInt("type", type);
		// bundle.putString("msg", msg);
		// Message message = new Message();
		// message.setData(bundle);
		// sendThread.mHandler.sendMessage(message);
		Message message = new Message();
		message.obj = jsonObject.toString();
		sendThread.mHandler.sendMessage(message);
	}

	protected SendThread sendThread;

	// 发送线程队列
	class SendThread extends Thread {
		public Handler mHandler;

		public void run() {
			Looper.prepare();
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					// Bundle bundle = msg.getData();
					// sendZlibSynchronized(bundle.getInt("mark"),
					// bundle.getInt("type"), bundle.getString("msg"));
					sendZlibSynchronized((String) msg.obj);
				}
			};
			Looper.loop();
		}
	}

	/**
	 * 同步发送
	 * 
	 * @Title: sendZlibSynchronizedvoid
	 * @Description: TODO
	 * @param type
	 * @param msg
	 * @author: ouArea
	 * @return void
	 * @throws
	 */
	// public void sendZlibSynchronized(int mark, int type, String msg) {
	// try {
	// // 压缩后的长度和压缩后的消息体
	// byte[] msgBodyAll = msg.getBytes("UTF-8");
	// byte[] msgBodyCompress = ByteTool.compress(msgBodyAll);
	// outStream.writeInt(mark);
	// outStream.writeInt(type);
	// outStream.writeInt(msgBodyCompress.length);
	// outStream.write(msgBodyCompress, 0, msgBodyCompress.length);
	// outStream.flush();
	// Log.i("send", type + "," + msg);
	// } catch (IOException e) {
	// e.printStackTrace();
	// closeWithException();
	// // throw new OutLineException("IOE");
	// } catch (NullPointerException e) {
	// e.printStackTrace();
	// closeWithException();
	// // throw new OutLineException("NullPinter");
	// }
	// }
	public void sendZlibSynchronized(String msg) {
		try {
			outStream.writeUTF(msg);
			outStream.flush();
			Log.i("send", msg);
		} catch (IOException e) {
			e.printStackTrace();
			closeWithException();
			// throw new OutLineException("IOE");
		} catch (NullPointerException e) {
			e.printStackTrace();
			closeWithException();
			// throw new OutLineException("NullPinter");
		}
	}

	/**
	 * @throws OutLineException
	 *             读取
	 * 
	 * @Title: readZlib
	 * @Description: TODO
	 * @param readBody
	 * @return
	 * @author: ouArea
	 * @return boolean
	 * @throws
	 */
	// public boolean readZlib(ReadBody readBody) {
	// try {
	// readBody.mark = inStream.readInt();
	// if (readBody.mark < 0) {
	// return true;
	// }
	// readBody.type = inStream.readInt();
	// int length = inStream.readInt();
	// byte[] msgBodyBytes = new byte[length];
	// for (int i = 0; i < msgBodyBytes.length; i++) {
	// msgBodyBytes[i] = inStream.readByte();
	// }
	// byte[] decodeBytes = ByteTool.decompress(msgBodyBytes);
	// readBody.msg = new String(decodeBytes, "UTF-8");
	// Log.i("read", readBody.type + "," + readBody.msg);
	// return true;
	// } catch (IOException e) {
	// e.printStackTrace();
	// closeWithException();
	// return false;
	// } catch (NullPointerException e) {
	// e.printStackTrace();
	// closeWithException();
	// return false;
	// } catch (Exception e) {
	// e.printStackTrace();
	// closeWithException();
	// return false;
	// }
	// }
	public boolean readZlib(ReadBody readBody) throws OutLineException {
		try {
			if (readBody.parse(inStream.readUTF())) {
				if (readBody.mark > 0) {
					Log.i("read", readBody.type + "," + readBody.msg);
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			closeWithException();
			throw new OutLineException(null);
		}
	}

	// private ExecutorService getPool() {
	// if (null == pool || pool.isShutdown()) {
	// pool = Executors.newFixedThreadPool(multiThreadNum);
	// }
	// return pool;
	// }

	// private void destoryPool() {
	// if (null != pool) {
	// try {
	// pool.shutdownNow();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// pool = null;
	// }
	// }

	public boolean isClose = false;

	/**
	 * 关闭
	 */
	public void close() {
		if (isClose) {
			return;
		}
		isClose = true;
		if (null != sendThread) {
			if (null != sendThread.mHandler) {
				sendThread.mHandler.getLooper().quit();
			}
			sendThread.mHandler = null;
		}
		sendThread = null;
		if (null != inStream) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inStream = null;
		}
		if (null != outStream) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outStream = null;
		}
		if (null != con) {
			try {
				con.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			con = null;
		}
		// destoryPool();
	}

	private void closeWithException() {
		close();
		if (null != mHandler) {
			Message msg = new Message();
			msg.obj = mark;
			msg.what = 8;
			mHandler.sendMessage(msg);
			mHandler = null;
		}
	}
}
