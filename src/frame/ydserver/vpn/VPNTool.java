package frame.ydserver.vpn;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class VPNTool {
	/** Called when the activity is first created. */

	private static final String FILE_PROFILES = "profiles";
	public static final int VPN_ERROR_CONNECTION_FAILED = 0x01;
	static Context sContext;
	static PptpProfile profile;

	static String TAG = "VPNTool";

	private static VpnManager vpnMgr;
	private static VpnService vpnSrv;

	static PptpProfile p;

	public static void connect(Context context) throws Exception {
		sContext = context.getApplicationContext();
		profile = new PptpProfile(sContext);
		// profile.setName("yunduoserver");
		// profile.setServerName("198.100.113.18");
		// profile.setUsername("zouyou_yunduo");
		// profile.setPassword("123456");
		// profile.setState(VpnState.IDLE);
		// profile.setId("7776");
		// profile.setEncryptionEnabled(false);
		// -----------------------------------
		profile.setName("yunduoserver");
		profile.setServerName("198.100.113.18");
		profile.setDomainSuffices("8.8.8.8");
		profile.setUsername("zouyou_yunduo");
		profile.setPassword("123456");
		profile.setEncryptionEnabled(true);
		saveProfiles(profile);
		p = (PptpProfile) loadProfiles();
		connect(p);
	}

	public static void disconnect(Context context) {
		sContext = context.getApplicationContext();
		disconnect();
	}

	/**
	 * 保存VPN信息
	 * 
	 * @param p
	 * @throws IOException
	 */
	private static void saveProfiles(VpnProfile p) throws IOException {
		ObjectOutputStream os = null;

		try {
			os = new ObjectOutputStream(openPrivateFileOutput(FILE_PROFILES));
			p.write(os);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	private static FileOutputStream openPrivateFileOutput(final String fileName) throws FileNotFoundException {
		return sContext.openFileOutput(fileName, Context.MODE_PRIVATE);
	}

	/**
	 * 加载现有VPN信息
	 * 
	 * @return
	 * @throws Exception
	 */
	private static VpnProfile loadProfiles() throws Exception {
		ObjectInputStream is = null;

		try {
			is = new ObjectInputStream(sContext.openFileInput(FILE_PROFILES));
			VpnProfile p = loadProfilesFrom(is);
			return p;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	private static VpnProfile loadProfilesFrom(final ObjectInputStream is) throws Exception {
		Object obj = null;

		try {
			while (true) {
				VpnType type = (VpnType) is.readObject();
				obj = is.readObject();
				VpnProfile p = loadProfileObject(type, obj, is);
				return p;
			}
		} catch (EOFException eof) {
			Log.d("testVPN", "reach the end of profiles file");
			return null;
		}
	}

	private static VpnProfile loadProfileObject(final VpnType type, final Object obj, final ObjectInputStream is) throws Exception {
		if (obj == null)
			return null;

		VpnProfile p = VpnProfile.newInstance(type, sContext);
		if (p.isCompatible(obj)) {
			p.read(obj, is);
			return p;
		} else {
			Log.e("testVPN", "saved profile '" + obj + "' is NOT compatible with " + type);
			return null;
		}
	}

	/**
	 * 连接一个VPN
	 * 
	 * @param p
	 */
	public static void connect(final VpnProfile p) {
		Log.i(TAG, "connect to: " + p);
		p.preConnect();
		// connect using a clone, so the secret key can be replace
		final VpnProfile cp = p.dulicateToConnect();
		// final VpnProfile cp = p;

		getVpnMgr().startVpnService();
		ServiceConnection c = new ServiceConnection() {
			public void onServiceConnected(final ComponentName className, final IBinder service) {
				try {
					boolean flag = getVpnSrv().connect(service, cp);
					Log.i(TAG, "connect() falg:" + flag);
					if (flag) {
						Toast.makeText(sContext, "远程服务创建成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(sContext, "远程服务创建失败", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(sContext, "远程服务创建失败", Toast.LENGTH_SHORT).show();
				} finally {
					sContext.unbindService(this);
				}
			}

			public void onServiceDisconnected(final ComponentName className) {
				Log.e(TAG, "onServiceDisconnected:" + className.toString());
			}
		};

		if (!getVpnMgr().bindVpnService(c)) {
			Log.e(TAG, "bind service failed");
		}
	}

	private static VpnManager getVpnMgr() {
		if (vpnMgr == null) {
			vpnMgr = new VpnManager(sContext);
		}
		return vpnMgr;
	}

	private static VpnService getVpnSrv() {
		if (vpnSrv == null) {
			vpnSrv = new VpnService(sContext);
		}
		return vpnSrv;
	}

	private static void disconnect() {
		Log.i(TAG, "disconnect active vpn");
		ServiceConnection c = new ServiceConnection() {
			public void onServiceConnected(final ComponentName className, final IBinder service) {
				try {
					getVpnSrv().disconnect(service);
				} catch (Exception e) {
					Log.e(TAG, "disconnect()", e);
				} finally {
					sContext.unbindService(this);
				}
			}

			public void onServiceDisconnected(final ComponentName className) {
				Log.e(TAG, "onServiceDisconnected");
			}
		};
		if (!getVpnMgr().bindVpnService(c)) {
			Log.e(TAG, "bind service failed");
		}
	}

}