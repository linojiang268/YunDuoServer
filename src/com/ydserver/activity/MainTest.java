package com.ydserver.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Wireless.CodingLearn;
import Wireless.Htool;
import Wireless.NodeParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android_serialport_api.SerialPort;

import com.baidu.mobstat.StatService;
import com.ydserver.db.SceneInfo;
import com.ydserver.db.SceneService;
import com.ydserver.db.UserInfo;
import com.ydserver.db.UserService;
import com.ydserver.tool.SMSMsgTool;
import com.ydserver.type.TypeDevice;
import com.ydserver.util.MsgUtil;

import frame.ydserver.socket.ReadBody;

public class MainTest extends Activity {
	public static MainTest mainTest;
	private EditText metRec, metSend;
	private Button mbtSend, mbtSend1, mbtSend2, mbtSend3, mbtSend4, mbtSend5, mbtSend6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test);
		this.findViews();
		MainTest.mainTest = this;
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(this);
		super.onPause();
	}

	private void findViews() {
		this.metRec = (EditText) findViewById(R.id.etRec);
		this.metSend = (EditText) findViewById(R.id.etSend);
		this.mbtSend = (Button) findViewById(R.id.btSend);
		this.mbtSend1 = (Button) findViewById(R.id.btSend1);
		this.mbtSend2 = (Button) findViewById(R.id.btSend2);
		this.mbtSend3 = (Button) findViewById(R.id.btSend3);
		this.mbtSend4 = (Button) findViewById(R.id.btSend4);
		this.mbtSend5 = (Button) findViewById(R.id.btSend5);
		this.mbtSend6 = (Button) findViewById(R.id.btSend6);
		mbtSend.setOnClickListener(clickListener);
		mbtSend1.setOnClickListener(clickListener);
		mbtSend2.setOnClickListener(clickListener);
		mbtSend3.setOnClickListener(clickListener);
		mbtSend4.setOnClickListener(clickListener);
		mbtSend5.setOnClickListener(clickListener);
		mbtSend6.setOnClickListener(clickListener);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.btSend:
				try {
					// 下编辑框发送出消息
					// mOutputStream.write(metSend.getText().toString().getBytes());
					byte[] cmd = new byte[] { (byte) 0xAA, 0x11, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x55 };
					SerialPort.sp.getOutputStream().write(cmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case R.id.btSend1:
				showToast("学习红外");
				try {
					CodingLearn.IRlearning();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case R.id.btSend2:
				showToast("退出学习");
				try {
					CodingLearn.EscLearning();
					// SearchAdd.call(new String("callMAC"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case R.id.btSend3:
				showToast("控灯-关");
				try {
					NodeParams.set((byte) 200, "12344");
					// SearchAdd.setNumber(new String("NumberMAC"), 120);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case R.id.btSend4:
				showToast("控灯3");
				// try {
				// SearchAdd.led0n();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				break;
			case R.id.btSend5:
				showToast("按钮5");
				// try {
				// SearchAdd.ledoff();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				break;
			case R.id.btSend6:
				showToast("按钮6");
				// try {
				// SendCode.SendCodes("Code:ok!");
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				SMSMsgTool.send("13693434985", "您的家里烟雾报警！", null);
				break;
			default:
				break;
			}

		}
	};

	public static String bytes2HexString(byte[] b, int size) {
		String ret = "";
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase() + ' ';
		}
		return ret;
	}

	// public static learn(long mark) {
	//
	// }
	// public static do(long mark,String hex) {
	//
	// }
	int n = 0;
	byte[] Databuffer = new byte[6000];
	long TimeData = Calendar.getInstance().getTimeInMillis();
	private int disPlayNum = 0;

	public void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				// 超时判断，若两次进入相隔50ms就将n 置0

				// if (Calendar.getInstance().getTimeInMillis() > TimeData + 50)
				// {
				// n = 0; //超时后就重新接收
				// }
				//
				// TimeData = Calendar.getInstance().getTimeInMillis();
				// ===========================================测试========================================
				if (disPlayNum++ > 20) {
					metRec.setText("");
					disPlayNum = 0;
				}
				if (metRec != null) {
					// 显示到上编辑框
					// metRec.append(new String(buffer, 0,
					// size));
					metRec.append(bytes2HexString(buffer, size));
				}

				// ======================================================================================
				for (int i = 0; i < size; i++) {
					switch (n) {
					case 0:
						if ((buffer[i] == (byte) 0xAA) || (buffer[i] == (byte) 0xDD)) {
							Databuffer[n++] = buffer[i];
						} else {
							n = 0;
						}
						break;
					case 1:
						if (((buffer[i] >= (byte) 0xC0) && (buffer[i] <= (byte) 0xC9)) || ((buffer[i] >= (byte) 0x80) && (buffer[i] <= (byte) 0x89))) {
							Databuffer[n++] = buffer[i];
						} else {
							n = 0;
						}
						break;
					default:
						if (n < 4) {
							Databuffer[n++] = buffer[i];
						} else {
							// 消息长度不能超过1000
							if ((n < ((int) ((Databuffer[2] & 0xFF) << 8 | (Databuffer[3] & 0xFF)) + 7)) && (1000 > ((int) ((Databuffer[2] & 0xFF) << 8 | (Databuffer[3] & 0xFF)) + 7))) {
								Databuffer[n++] = buffer[i];

								// if ((Calendar.getInstance().getTimeInMillis()
								// > TimeData + 50)
								// && (Databuffer[1] != (byte)0xC0)) {
								// n = 0; //超时后就重新接收,红外或者无线学习不用超时
								// }
								// TimeData =
								// Calendar.getInstance().getTimeInMillis();

							} else {
								n = 0;
							}
						}

						// if ((n == ((int) ((Databuffer[2] & 0xFF) << 8 |
						// (Databuffer[3] & 0xFF)) + 7))
						// || ((Databuffer[n - 1] == (byte) 0x55) &&
						// (Databuffer[2] == (byte) 0xFF) && (Databuffer[3] ==
						// (byte) 0x11)
						// && (n == ((int) ((Databuffer[n - 3] & 0xFF) << 8 |
						// (Databuffer[n - 2] & 0xFF)) + 7)))) {
						if ((n == ((int) ((Databuffer[2] & 0xFF) << 8 | (Databuffer[3] & 0xFF)) + 7))) {
							if (Databuffer[n - 1] == (byte) 0x55) {

								// 红外学习时报文的数据长度被填充到CRC校验位上了，下面语句是进行交换还原
								if ((Databuffer[1] == (byte) 0xC0) && (Databuffer[4] == (byte) 0xE5)) {
									Databuffer[3] = (byte) (Databuffer[3] + 0x08);// ???????
									// Databuffer[3] = Databuffer[n - 2];
								}

								// 有效报文输出
								// 取出有效的byte数组
								byte[] needBytes = new byte[n];
								System.arraycopy(Databuffer, 0, needBytes, 0, n);

								// ============================================================================
								if (metRec != null) {
									// 显示到上编辑框
									// metRec.append(new String(buffer, 0,
									// size));
									metRec.append("收到：" + bytes2HexString(needBytes, needBytes.length) + Calendar.getInstance().getTimeInMillis() + "\n");
								}
								// 数据处理
								switch (needBytes[1] & 0xFF) {
								case 0xC0:
									switch (needBytes[4] & 0xFF) {
									case 0xE5:
									case 0xEA:
										// 无线、红外学习返回
										needBytes[1] = 0x16;
										String needStr = Htool.bytesToHexString(needBytes);
										MsgUtil.sendServerToAllClient(6001, "{\"result\":0,\"msg\":\"学习完成\",\"command\":\"" + needStr + "\"}");
										try {
											CodingLearn.EscLearning();
										} catch (IOException e) {
											e.printStackTrace();
										}
										break;
									case 0xED:
										String needStrC0;
										// 把报警的MAC地址传递出去
										// needBytes[18] = 0x00;
										needStrC0 = Htool.bytesToHexString(needBytes);
										needStrC0 = needStrC0.substring(6 * 2, 9 * 2);
										MsgUtil.warning(MainTest.this, needStrC0);
										// 触发场景
										SceneService sceneService = new SceneService(MainTest.this);
										SceneInfo sceneInfo = sceneService.findSceneMac(needStrC0);
										if (null != sceneInfo) {
											MsgUtil msgUtil = new MsgUtil(MainTest.this, new ReadBody((int) (System.currentTimeMillis() / 1000), 6510, "{\"_id\":" + sceneInfo.get_id() + "}"));
											try {
												msgUtil.parse(null);
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
										MsgUtil.sendServerToAllClient(60111, "{\"result\":0,\"msg\":\"设备学习成功\"" + ",\"command\":\"" + needStrC0 + "\"}");
										// ============================================================================
										// if (metRec != null) {
										// metRec.append("MAC：" + needStrC0 +
										// "  T=" +
										// Calendar.getInstance().getTimeInMillis()
										// + "\n");
										// }
										break;
									default:
										break;
									}

									break;
								case 0xC1:
									MsgUtil.sendServerToAllClient(6001, "{\"result\":0,\"msg\":\"学习失败\"}");
									try {
										CodingLearn.EscLearning();
									} catch (IOException e) {
										e.printStackTrace();
									}
									break;
								// 灯光、窗帘操作成功
								case 0xC2:
									MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"操作成功\"}");
									try {
										if ((needBytes[4] & 0xFF) == 0xEC) {
											CodingLearn.EscLearning();
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
									break;
								// 灯光、窗帘操作失败
								case 0xC3:
									try {
										CodingLearn.EscLearning();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"操作失败\"}");
									break;
								// 内部通讯
								case 0x80:
									String needStr80;
									// 回复接收成功
									try {
										NodeParams.ReceiveSuccess(needBytes);
									} catch (IOException e) {
										e.printStackTrace();
									}
									// =================
									ArrayList<String> macList;
									int num;
									switch (needBytes[4] & 0xFF) {
									case 0x66:
										// 搜索出灯光
										switch (needBytes[18] & 0xFF) {
										case 0x01:
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.LIGHT_FB, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
											break;
										case 0x02:
											macList = new ArrayList<String>();
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											needBytes[18] = 0x01;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											num = MsgUtil.saveSearchDevice(TypeDevice.LIGHT_FB, macList);
											if (num > 0) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"成功搜索" + num + "个设备\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
											break;
										case 0x03:
											macList = new ArrayList<String>();
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											needBytes[18] = 0x01;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											needBytes[18] = 0x02;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											num = MsgUtil.saveSearchDevice(TypeDevice.LIGHT_FB, macList);
											if (num > 0) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"成功搜索" + num + "个设备\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
											break;
										case 0x04:
											macList = new ArrayList<String>();
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											needBytes[18] = 0x01;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											needBytes[18] = 0x02;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											needBytes[18] = 0x03;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											macList.add(needStr80);
											// MsgUtil.sendServerToAllClient(6001,
											// "{\"result\":0,\"msg\":\"搜索返回\",\"command\":\""
											// + needStr80 + "\"}");
											num = MsgUtil.saveSearchDevice(TypeDevice.LIGHT_FB, macList);
											if (num > 0) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"成功搜索" + num + "个设备\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
											break;
										default:
											break;
										}
										break;
									case 0x67:
										// 搜索出窗帘
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.BLIND_FB, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									case 0x60:
										// 搜索出红外发射设备
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.INFRARED_FB, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									case 0x61:
										// 搜索出报警 幕帘
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.SECURITY_FB_BLIND, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									case 0x62:
										// 搜索出报警 煤气
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.SECURITY_FB_GAS, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									case 0x63:
										// 搜索出报警 烟雾
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.SECURITY_FB_SMOKE, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									case 0x64:
										// 搜索出报警 温度
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.SECURITY_FB_TEMPERATURE, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									case 0x65:
										// 搜索出报警 门磁
										if (0x01 == (needBytes[18] & 0xFF)) {
											needBytes[18] = 0x00;
											needStr80 = Htool.bytesToHexString(needBytes);
											needStr80 = needStr80.substring(12 * 2, 19 * 2);
											if (MsgUtil.saveSearchDevice(TypeDevice.SECURITY_FB_DOOR, needStr80)) {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索成功\"}");
											} else {
												MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
											}
										} else {
											MsgUtil.sendServerToAllClient(6003, "{\"result\":0,\"msg\":\"搜索失败\"}");
										}
										break;
									default:
										break;
									}
									break;
								case 0x82:
									MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"呼叫返回\"}");
									break;
								case 0x83:
									MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"参数返回\"}");
									break;
								case 0x85:
									MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"接收成功\"}");
									break;
								case 0x86:
									MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"接收失败\"}");
									break;
								case 0x87:
									switch (needBytes[4] & 0xFF) {
									case 0x66:
										MsgUtil.sendServerToAllClient(6103, "{\"result\":0,\"msg\":\"灯光操作成功\"}");
										break;
									case 0x67:
										MsgUtil.sendServerToAllClient(6203, "{\"result\":0,\"msg\":\"窗帘操作成功\"}");
										break;
									case 0x60:
										// 没区别电视、空调
										MsgUtil.sendServerToAllClient(6005, "{\"result\":0,\"msg\":\"操作成功\"}");
										break;
									case 0x63:
										MsgUtil.sendServerToAllClient(6603, "{\"result\":0,\"msg\":\"监控操作成功\"}");
										break;
									default:
										break;
									}
									break;
								case 0x89:
									switch (needBytes[4] & 0xFF) {
									case 0x66:
										MsgUtil.sendServerToAllClient(6104, "{\"result\":0,\"msg\":\"灯光操作失败\"}");
										break;
									case 0x67:
										MsgUtil.sendServerToAllClient(6103, "{\"result\":0,\"msg\":\"窗帘操作失败\"}");
										break;
									case 0x63:
										MsgUtil.sendServerToAllClient(6103, "{\"result\":0,\"msg\":\"监控操作失败\"}");
										break;
									default:
										break;
									}
									break;
								case 0x88:
									try {
										NodeParams.ReceiveSuccess(needBytes);
									} catch (IOException e) {
										e.printStackTrace();
									}
									MsgUtil.warning(MainTest.this, null);
									break;
								default:
									break;
								}
							}
							n = 0;
						}
						break;
					}
				}
			}
		});
	}

	public void name() {

	}

	private void showToast(String str) {
		Toast.makeText(MainTest.this, str, Toast.LENGTH_SHORT).show();
	}

	public void disPlayStr(final byte[] needBytes) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				metRec.append("发出：" + bytes2HexString(needBytes, needBytes.length) + "\n");
			}
		});
	}
}
