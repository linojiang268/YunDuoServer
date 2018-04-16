package com.ydserver.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Wireless.CodingLearn;
import Wireless.NodeParams;
import Wireless.SearchAdd;
import Wireless.SendCode;
import android.content.Context;

import com.google.gson.Gson;
import com.ydserver.activity.YDServerService;
import com.ydserver.data.MyNetData;
import com.ydserver.db.AirParamInfo;
import com.ydserver.db.AirParamService;
import com.ydserver.db.CommandInfo;
import com.ydserver.db.CommandService;
import com.ydserver.db.DeviceInfo;
import com.ydserver.db.DeviceService;
import com.ydserver.db.SceneCommandMsg;
import com.ydserver.db.SceneCommandMsgService;
import com.ydserver.db.SceneInfo;
import com.ydserver.db.SceneService;
import com.ydserver.db.UserInfo;
import com.ydserver.db.UserService;
import com.ydserver.model.AirUpdateModel;
import com.ydserver.model.CommandMsgItemsModel;
import com.ydserver.model.DeviceItemsModel;
import com.ydserver.model.DeviceUpdateModel;
import com.ydserver.model.SceneItemsModel;
import com.ydserver.model.SceneUpdateModel;
import com.ydserver.tool.SMSMsgTool;
import com.ydserver.type.TypeDevice;
import com.ydserver.xmpp.MyXMPP;

import frame.ydserver.socket.ReadBody;

/**
 * 消息处理类
 * 
 * @author ouArea
 * 
 */
public class MsgUtil {
	private Context context;
	private ReadBody readBody;
	private UserService userService;
	private DeviceService deviceService;
	private CommandService commandService;
	private AirParamService airParamService;
	private SceneService sceneService;
	private SceneCommandMsgService sceneCommandMsgService;
	private Gson gson;

	// private UserInfo userInfo;

	public MsgUtil(Context context, ReadBody readBody) {
		super();
		this.context = context;
		this.readBody = readBody;
		this.userService = new UserService(context);
		// this.userInfo = new UserInfo();
		// this.userInfo = userService.find(1);
		this.deviceService = new DeviceService(context);
		this.commandService = new CommandService(context);
		this.airParamService = new AirParamService(context);
		this.sceneService = new SceneService(context);
		this.sceneCommandMsgService = new SceneCommandMsgService(context);
		this.gson = new Gson();
	}

	/**
	 * 向所有客户端发送消息
	 * 
	 * @param type
	 * @param msg
	 * @return
	 */
	public static boolean sendServerToAllClient(int type, String msg) {
		int mark = (int) (System.currentTimeMillis() / 1000);
		try {
			if (null != YDServerService.mMarkList && YDServerService.mMarkList.size() > 0) {
				for (int i = 0; i < YDServerService.mMarkList.size(); i++) {
					Client sClient = YDServerService.mClientMap.get(YDServerService.mMarkList.get(i));
					sClient.send(mark, type, msg);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (MyXMPP.isConnect()) {
					MyXMPP.sendMulMsg(mark, type, msg);
				}
			} catch (Exception e) {
			}
		}
		// MsgUtil.sendServerToClient(mark, 10001, "");
	}

	/**
	 * 向指定客户端发送消息
	 * 
	 * @param client_mark
	 * @param type
	 * @param msg
	 * @return
	 */
	// public static boolean sendServerToClient(long client_mark, int type,
	// String msg) {
	// try {
	// Client sClient = YDServerService.mClientMap.get(client_mark);
	// sClient.send((int) (System.currentTimeMillis() / 1000), type, msg);
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// }

	public static DeviceService sDeviceService;

	/**
	 * 添加搜索到的设备，返回新增的设备数目
	 * 
	 * @param type
	 * @param markList
	 * @return
	 */
	public static int saveSearchDevice(int type, ArrayList<String> markList) {
		int num = 0;
		for (String mark : markList) {
			DeviceInfo deviceInfo = sDeviceService.findDevice(type, mark);
			if (null == deviceInfo) {
				if (sDeviceService.insertDevice(new DeviceInfo(null, type, mark, "设备", "0"))) {
					num++;
				}
			}
		}
		return num;
	}

	/**
	 * 添加单个搜索到的设备，返回新增的设备数目
	 * 
	 * @param type
	 * @param markList
	 * @return
	 */
	public static boolean saveSearchDevice(int type, String mark) {
		DeviceInfo deviceInfo = sDeviceService.findDevice(type, mark);
		if (null == deviceInfo) {
			return sDeviceService.insertDevice(new DeviceInfo(null, type, mark, "设备", "0"));
		} else {
			return false;
		}
	}

	/**
	 * 报警
	 * 
	 * @param mac
	 * @return
	 */
	public static void warning(Context context, String mac) {
		if (null == mac) {
			MsgUtil.sendServerToAllClient(6605, "{\"result\":0,\"msg\":\"上传报警数据\"}");
		} else {
			DeviceInfo deviceInfo = sDeviceService.findDevice(TypeDevice.SECURITY_UN_FB, mac);
			if (null != deviceInfo && null != deviceInfo.getContent() && deviceInfo.getContent().trim().equals("0")) {
				MsgUtil.sendServerToAllClient(6605, "{\"result\":0,\"msg\":\"上传报警数据\"}");
			} else {
				return;
			}
		}
		if (YDServerService.sHasSendSmsMsgNum < YDServerService.maxSmsSendNum && System.currentTimeMillis() - YDServerService.sLastSendSmsMsgTime >= YDServerService.spaceSendSmsMsgTime) {
			UserService userService = new UserService(context);
			UserInfo userInfo = userService.find(1);
			if (null != userInfo) {
				try {
					JSONArray jsonArray = new JSONArray(userInfo.security_phone_numbers);
					if (null != jsonArray && jsonArray.length() > 0) {
						for (int j = 0; j < jsonArray.length(); j++) {
							SMSMsgTool.send(jsonArray.getString(j), "云朵技术提醒:有报警数据上传！请您尽快处理！  " + Calendar.getInstance().getTime().toLocaleString(), null);
							YDServerService.sHasSendSmsMsgNum++;
							YDServerService.sLastSendSmsMsgTime = System.currentTimeMillis();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public UserInfo getUserInfo() {
		return userService.find(1);
	}

	public String parse(ReadBody readBody) throws JSONException {
		if (null == readBody) {
			readBody = this.readBody;
		}
		switch (readBody.type) {
		case 5001:
			// 登录
			String r_password = new JSONObject(readBody.msg).getString("password");
			if (null != r_password && r_password.trim().length() > 0 && r_password.trim().equals(getUserInfo().password.trim())) {
				return null;
			} else {
				return "";
			}
		case 5002:
			// 设置密码
			JSONObject jsonObject5002 = new JSONObject(readBody.msg);
			String r_oldpassword = jsonObject5002.getString("oldpassword");
			String r_newpassword = jsonObject5002.getString("newpassword");
			UserInfo userInfo5002 = getUserInfo();
			// String tmp_password = userInfo.password;
			if (null != r_oldpassword && r_oldpassword.trim().length() > 0 && null != r_newpassword && r_newpassword.trim().length() >= 6 && userInfo5002.password.equals(r_oldpassword)) {
				userInfo5002.password = r_newpassword;
				if (userService.update(userInfo5002)) {
					return "{\"result\":0,\"msg\":\"密码修改成功\"}";
				}
			}
			// userInfo.password = tmp_password;
			return "{\"result\":1,\"msg\":\"密码修改失败\"}";
		case 5003:
			// 获取报警电话
			UserInfo userInfo5003 = getUserInfo();
			if (null == userInfo5003.security_phone_numbers) {
				return "{\"result\":1,\"msg\":\"报警电话未设置\"}";
			} else {
				return "{\"result\":0,\"msg\":{\"security_phone_numbers\":" + userInfo5003.security_phone_numbers + "}}";
			}
		case 5004:
			// 设置报警电话
			JSONObject jsonObject5004 = new JSONObject(readBody.msg);
			JSONArray jsonArray5004 = jsonObject5004.getJSONArray("security_phone_numbers");
			// String tmp_security_phone_numbers =
			// userInfo.security_phone_numbers;
			UserInfo userInfo5004 = getUserInfo();
			userInfo5004.security_phone_numbers = jsonArray5004.toString();
			if (userService.update(userInfo5004)) {
				return "{\"result\":0,\"msg\":\"报警电话修改成功\"}";
			}
			// userInfo.security_phone_numbers = tmp_security_phone_numbers;
			return "{\"result\":1,\"msg\":\"报警电话修改失败\"}";
		case 5005:
			// 获取远程访问参数
			if (MyXMPP.isSet(context)) {
				return "{\"result\":0,\"msg\":{\"groupName\":\"" + MyXMPP.getAdminName(context) + "\",\"groupPass\":\"" + MyXMPP.getAdminPassword(context) + "\"}}";
			} else {
				return "{\"result\":1,\"msg\":\"远程参数未设置\"}";
			}
		case 5006:
			// 设置远程访问
			JSONObject jsonObject5006 = new JSONObject(readBody.msg);
			String groupName5006 = jsonObject5006.getString("groupName").trim();
			String groupPass5006 = jsonObject5006.getString("groupPass").trim();
			if (groupName5006.length() >= 6 && groupPass5006.length() >= 6) {
				MyXMPP.setValue(context, MyNetData.yunduoGroupAddress, groupName5006, groupPass5006, MyNetData.yunduoGroupConference);
				return "{\"result\":0,\"msg\":\"远程参数设置成功\"}";
			} else {
				return "{\"result\":1,\"msg\":\"远程参数设置失败\"}";
			}
			// --------------------------学习-----------------------------
		case 6001:
			try {
				CodingLearn.RFlearning();
			} catch (IOException e) {
				e.printStackTrace();
				return "{\"result\":1,\"msg\":\"学习指令发送失败\"}";
			}
			// return "{\"result\":0,\"msg\":\"请求\"}";
			break;
		case 6011:
			try {
				CodingLearn.IRlearning();
			} catch (IOException e) {
				e.printStackTrace();
				return "{\"result\":1,\"msg\":\"红外学习指令发送失败\"}";
			}
			break;
		// --------------------------学习后新增、修改设备-----------------------------
		case 6002:
			String resultStr6002;
			DeviceUpdateModel deviceUpdateModel = gson.fromJson(readBody.msg, DeviceUpdateModel.class);
			DeviceInfo deviceInfo = deviceUpdateModel.deviceInfo;
			if (null == deviceInfo.get_id()) {
				deviceService.insertDevice(deviceInfo);
				resultStr6002 = "设备添加成功";
				deviceInfo = deviceService.findLastDevice();
			} else {
				deviceService.updateDevice(deviceInfo);
				resultStr6002 = "设备修改成功";
			}
			if (null != deviceUpdateModel.commandInfos) {
				for (CommandInfo commandInfo : deviceUpdateModel.commandInfos) {
					commandInfo.set_id(deviceInfo.get_id());
					if (null == commandService.findCommand(commandInfo._id, commandInfo.type)) {
						commandService.insertCommand(commandInfo);
					} else {
						commandService.updateCommand(commandInfo);
					}
				}
			}
			return "{\"result\":0,\"msg\":\"" + resultStr6002 + "\"}";
			// --------------------------搜索-----------------------------
		case 6003:
			switch (new JSONObject(readBody.msg).getInt("type")) {
			case TypeDevice.LIGHT_FB:
				try {
					SearchAdd.searchLED();
				} catch (IOException e1) {
					e1.printStackTrace();
					return "{\"result\":1,\"msg\":\"搜索指令发送失败\"}";
				}
				break;
			case TypeDevice.BLIND_FB:
				try {
					SearchAdd.searchCurtain();
				} catch (IOException e1) {
					e1.printStackTrace();
					return "{\"result\":1,\"msg\":\"搜索指令发送失败\"}";
				}
				break;
			case TypeDevice.INFRARED_FB:
				try {
					SearchAdd.searchIR();
				} catch (IOException e1) {
					e1.printStackTrace();
					return "{\"result\":1,\"msg\":\"搜索指令发送失败\"}";
				}
				break;
			case TypeDevice.SECURITY_FB:
				try {
					// 幕帘、煤气、烟雾、温度
					SearchAdd.searchWarning();
				} catch (IOException e1) {
					e1.printStackTrace();
					return "{\"result\":1,\"msg\":\"搜索指令发送失败\"}";
				}
				break;
			default:
				break;
			}
			break;
		// return "{\"result\":0,\"msg\":\"请求\"}";
		// --------------------------删除-----------------------------
		case 6004:
			int _id6004 = new JSONObject(readBody.msg).getInt("_id");
			DeviceInfo deviceInfo6004 = deviceService.findDevice(_id6004);
			if (null == deviceInfo6004) {
				return "{\"result\":1,\"msg\":\"设备不存在\"}";
			}
			if (TypeDevice.hasFeedBack(deviceInfo6004.type)) {
				deviceService.delete(_id6004);
			} else {
				commandService.delete(_id6004);
				deviceService.delete(_id6004);
			}
			return "{\"result\":0,\"msg\":\"设备删除成功\"}";
			// --------------------------操作-----------------------------
		case 6102:
			DeviceItemsModel deviceItemsModelL = new DeviceItemsModel();
			deviceItemsModelL.devices = deviceService.getDeviceList(-1, -1, TypeDevice.LIGHT_FB, TypeDevice.LIGHT_UN_FB);
			deviceItemsModelL.result = 0;
			deviceItemsModelL.msg = "获取成功";
			return gson.toJson(deviceItemsModelL);
		case 6202:
			DeviceItemsModel deviceItemsModelB = new DeviceItemsModel();
			deviceItemsModelB.devices = deviceService.getDeviceList(-1, -1, TypeDevice.BLIND_FB, TypeDevice.BLIND_UN_FB);
			deviceItemsModelB.result = 0;
			deviceItemsModelB.msg = "获取成功";
			return gson.toJson(deviceItemsModelB);
		case 6302:
			DeviceItemsModel deviceItemsModelT = new DeviceItemsModel();
			deviceItemsModelT.devices = deviceService.getDeviceList(-1, -1, TypeDevice.TV_FB, TypeDevice.TV_UN_FB);
			deviceItemsModelT.result = 0;
			deviceItemsModelT.msg = "获取成功";
			return gson.toJson(deviceItemsModelT);
		case 6402:
			DeviceItemsModel deviceItemsModelA = new DeviceItemsModel();
			deviceItemsModelA.devices = deviceService.getDeviceList(-1, -1, TypeDevice.AIR_FB, TypeDevice.AIR_UN_FB);
			deviceItemsModelA.result = 0;
			deviceItemsModelA.msg = "获取成功";
			return gson.toJson(deviceItemsModelA);
		case 603402:
			DeviceItemsModel deviceItemsModelTA_F = new DeviceItemsModel();
			deviceItemsModelTA_F.devices = deviceService.getDeviceList(-1, -1, TypeDevice.INFRARED_FB);
			deviceItemsModelTA_F.result = 0;
			deviceItemsModelTA_F.msg = "获取成功";
			return gson.toJson(deviceItemsModelTA_F);
		case 6602:
			DeviceItemsModel deviceItemsModelS = new DeviceItemsModel();
			deviceItemsModelS.devices = deviceService.getDeviceList(-1, -1, TypeDevice.SECURITY_FB, TypeDevice.SECURITY_UN_FB, TypeDevice.SECURITY_FB_BLIND, TypeDevice.SECURITY_FB_GAS, TypeDevice.SECURITY_FB_SMOKE, TypeDevice.SECURITY_FB_TEMPERATURE, TypeDevice.SECURITY_FB_DOOR);
			deviceItemsModelS.result = 0;
			deviceItemsModelS.msg = "获取成功";
			return gson.toJson(deviceItemsModelS);
		case 6103:
		case 6104:
		case 6203:
		case 6204:
		case 6206:
		case 6603:
		case 6604:
			if (6603 == readBody.type || 6604 == readBody.type) {
				YDServerService.sHasSendSmsMsgNum = 0;
				YDServerService.sLastSendSmsMsgTime = 0;
			}
			DeviceInfo deviceInfo6103_4 = deviceService.findDevice(new JSONObject(readBody.msg).getInt("_id"));
			if (null == deviceInfo6103_4) {
				return "{\"result\":1,\"msg\":\"设备不存在\"}";
			}
			if (TypeDevice.hasFeedBack(deviceInfo6103_4.type)) {
				try {
					NodeParams.set(readBody.type, deviceInfo6103_4.getMark());
				} catch (IOException e) {
					e.printStackTrace();
					return "{\"result\":1,\"msg\":\"操作失败\"}";
				}
			} else {
				CommandInfo commandInfo6103_4 = commandService.findCommand(deviceInfo6103_4._id, readBody.type);
				if (null == commandInfo6103_4) {
					return "{\"result\":1,\"msg\":\"未学习该操作\"}";
				} else {
					try {
						SendCode.SendCodes(commandInfo6103_4.getCommand());
					} catch (IOException e) {
						e.printStackTrace();
						return "{\"result\":1,\"msg\":\"服务器出错\"}";
					}
				}
			}
			break;
		case 6303:
		case 6306:
		case 6307:
		case 6308:
		case 6309:
		case 6310:
		case 6311:
		case 6312:
		case 6313:
		case 6314:
		case 6315:
		case 6316:
		case 6317:
			DeviceInfo deviceInfo6303_4 = deviceService.findDevice(new JSONObject(readBody.msg).getInt("_id"));
			if (null == deviceInfo6303_4) {
				return "{\"result\":1,\"msg\":\"该设备不存在\"}";
			}
			CommandInfo commandInfo6303_4 = commandService.findCommand(deviceInfo6303_4._id, readBody.type);
			if (null == commandInfo6303_4) {
				return "{\"result\":1,\"msg\":\"未学习该操作\"}";
			} else {
				try {
					// SendCode.SendCodes(commandInfo6303_4.getCommand());
					NodeParams.set(readBody.type, deviceInfo6303_4.getMark(), commandInfo6303_4.getCommand());
				} catch (IOException e) {
					e.printStackTrace();
					return "{\"result\":1,\"msg\":\"服务器出错\"}";
				}
			}
			break;
		// 空调
		case 6403:
			String resultStr6403;
			AirUpdateModel airUpdateModel = gson.fromJson(readBody.msg, AirUpdateModel.class);
			DeviceInfo deviceInfo6403 = airUpdateModel.deviceInfo;
			if (null == deviceInfo6403.get_id()) {
				deviceService.insertDevice(deviceInfo6403);
				resultStr6403 = "设备添加成功";
				deviceInfo6403 = deviceService.findLastDevice();
			} else {
				deviceService.updateDevice(deviceInfo6403);
				resultStr6403 = "设备修改成功";
			}
			if (null != airUpdateModel.airParamInfos) {
				for (AirParamInfo airParamInfo : airUpdateModel.airParamInfos) {
					airParamInfo.setDevice_id(deviceInfo6403.get_id());
					AirParamInfo airParamInfoNew = airParamService.find(airParamInfo.getDevice_id(), airParamInfo.getPower(), airParamInfo.getModel(), airParamInfo.getWind_speed(), airParamInfo.getWind_direction(), airParamInfo.getTemperature());
					if (null == airParamInfoNew) {
						airParamService.insert(airParamInfo);
					} else {
						airParamInfo.set_id(airParamInfoNew.get_id());
						airParamService.update(airParamInfo);
					}
				}
			}
			return "{\"result\":0,\"msg\":\"" + resultStr6403 + "\"}";
		case 6404:
			JSONObject jsonObject6404 = new JSONObject(readBody.msg);
			// int _id6404 = jsonObject6404.getInt("_id");
			int device_id6404 = jsonObject6404.getInt("device_id");
			int power6404 = jsonObject6404.getInt("power");
			int model6404 = jsonObject6404.getInt("model");
			int wind_speed6404 = jsonObject6404.getInt("wind_speed");
			int wind_direction6404 = jsonObject6404.getInt("wind_direction");
			int temperature6404 = jsonObject6404.getInt("temperature");
			DeviceInfo deviceInfo6404 = deviceService.findDevice(device_id6404);
			if (null == deviceInfo6404) {
				return "{\"result\":1,\"msg\":\"该设备不存在\"}";
			}
			AirParamInfo airParamInfo6404 = airParamService.find(device_id6404, power6404, model6404, wind_speed6404, wind_direction6404, temperature6404);
			if (null == airParamInfo6404) {
				return "{\"result\":1,\"msg\":\"未学习该操作\"}";
			} else {
				try {
					// SendCode.SendCodes(commandInfo6303_4.getCommand());
					NodeParams.set(readBody.type, deviceInfo6404.getMark(), airParamInfo6404.getCommand());
				} catch (IOException e) {
					e.printStackTrace();
					return "{\"result\":1,\"msg\":\"服务器出错\"}";
				}
			}
			break;
		case 6105:
		case 6405:
		case 6606:
			JSONObject jsonObject61405 = new JSONObject(readBody.msg);
			int _id61405 = jsonObject61405.getInt("_id");
			String content = jsonObject61405.getString("content");
			DeviceInfo deviceInfo61405 = deviceService.findDevice(_id61405);
			if (null == deviceInfo61405) {
				return "{\"result\":1,\"msg\":\"设备状态保存失败\"}";
			} else {
				if (deviceService.updateDevice(_id61405, content)) {
					return "{\"result\":0,\"msg\":\"设备状态保存成功\"}";
				} else {
					return "{\"result\":1,\"msg\":\"设备状态保存失败\"}";
				}
			}
		case 6608:
			YDServerService.sHasSendSmsMsgNum = 0;
			YDServerService.sLastSendSmsMsgTime = 0;
			JSONObject jsonObject6608 = new JSONObject(readBody.msg);
			int _id6608 = jsonObject6608.getInt("_id");
			DeviceInfo deviceInfo6608 = deviceService.findDevice(_id6608);
			if (null == deviceInfo6608) {
				return "{\"result\":1,\"msg\":\"设备不存在\"}";
			} else {
				if (deviceService.updateDevice(_id6608, "0")) {
					return "{\"result\":0,\"msg\":\"开启成功\"}";
				} else {
					return "{\"result\":1,\"msg\":\"开启失败\"}";
				}
			}
		case 6609:
			YDServerService.sHasSendSmsMsgNum = 0;
			YDServerService.sLastSendSmsMsgTime = 0;
			JSONObject jsonObject6609 = new JSONObject(readBody.msg);
			int _id6609 = jsonObject6609.getInt("_id");
			DeviceInfo deviceInfo6609 = deviceService.findDevice(_id6609);
			if (null == deviceInfo6609) {
				return "{\"result\":1,\"msg\":\"设备不存在\"}";
			} else {
				if (deviceService.updateDevice(_id6609, "1")) {
					return "{\"result\":0,\"msg\":\"关闭成功\"}";
				} else {
					return "{\"result\":1,\"msg\":\"关闭失败\"}";
				}
			}
			// 灯光
			// 安防监控
			// ------------------------场景----------------------------
		case 6501:
			String resultStr6501;
			SceneUpdateModel sceneUpdateModel = gson.fromJson(readBody.msg, SceneUpdateModel.class);
			SceneInfo sceneInfo = sceneUpdateModel.sceneInfo;
			if (null == sceneInfo.get_id()) {
				sceneInfo.isUsed = 0;
				sceneService.insertSceneInfo(sceneInfo);
				resultStr6501 = "场景添加成功";
				sceneInfo = sceneService.findLastScene();
			} else {
				sceneService.updateScence(sceneInfo);
				resultStr6501 = "场景修改成功";
			}
			if (null != sceneUpdateModel.commandMsgs && sceneUpdateModel.commandMsgs.size() > 0) {
				// 先删除多余的
				sceneCommandMsgService.deleteCommandMsgWithOut(sceneInfo.get_id(), sceneUpdateModel.commandMsgs.toArray(new SceneCommandMsg[sceneUpdateModel.commandMsgs.size()]));
				for (SceneCommandMsg commandMsg : sceneUpdateModel.commandMsgs) {
					commandMsg.set_id(sceneInfo.get_id());
					if (null == sceneCommandMsgService.findCommandMsg(commandMsg.get_id(), commandMsg.getD_id())) {
						sceneCommandMsgService.insertCommandMsg(commandMsg);
					} else {
						sceneCommandMsgService.updateCommandMsg(commandMsg);
					}
				}
			} else {
				// 删除所有的命令
				sceneCommandMsgService.delete(sceneInfo.get_id());
			}
			return "{\"result\":0,\"msg\":\"" + resultStr6501 + "\"}";
		case 6502:
			SceneItemsModel sceneItemsModel = new SceneItemsModel();
			sceneItemsModel.scenes = sceneService.getSceneList(-1, -1, 0);
			sceneItemsModel.result = 0;
			sceneItemsModel.msg = "获取成功";
			return gson.toJson(sceneItemsModel);
		case 6503:
			SceneItemsModel sceneItemsModel3 = new SceneItemsModel();
			sceneItemsModel3.scenes = sceneService.getSceneList(-1, -1, 1);
			sceneItemsModel3.result = 0;
			sceneItemsModel3.msg = "获取成功";
			return gson.toJson(sceneItemsModel3);
		case 6504:
			JSONObject json6504 = new JSONObject(readBody.msg);
			int d_id6504,
			_id6504;
			if (json6504.has("d_id")) {
				d_id6504 = json6504.getInt("d_id");
				_id6504 = json6504.getInt("_id");
				sceneCommandMsgService.deleteCommandMsg(_id6504, d_id6504);
			} else {
				_id6504 = json6504.getInt("_id");
				sceneCommandMsgService.delete(_id6504);
				sceneService.delete(_id6504);
			}
			return "{\"result\":0,\"msg\":\"删除成功\"}";
		case 6505:
			int _id6505 = new JSONObject(readBody.msg).getInt("_id");
			CommandMsgItemsModel commandMsgItemsModel = new CommandMsgItemsModel();
			commandMsgItemsModel.commandMsgs = sceneCommandMsgService.getSceneCommandMsgList(-1, -1, _id6505);
			commandMsgItemsModel.result = 0;
			commandMsgItemsModel.msg = "获取成功";
			return gson.toJson(commandMsgItemsModel);
		case 6506:
			JSONObject json6506 = new JSONObject(readBody.msg);
			int _id6506 = json6506.getInt("_id");
			int isUsed = json6506.getInt("isUsed");
			SceneInfo sceneInfo6506 = sceneService.findScene(_id6506);
			if (null == sceneInfo6506) {
				return "{\"result\":1,\"msg\":\"场景不存在\"}";
			} else {
				if (0 == isUsed) {
					sceneInfo6506.isUsed = 0;
					sceneService.updateScence(sceneInfo6506);
					return "{\"result\":0,\"msg\":\"场景设置成功\"}";
				} else {
					sceneInfo6506.isUsed = 1;
					if (sceneService.getCount(1) < 6) {
						sceneService.updateScence(sceneInfo6506);
						return "{\"result\":0,\"msg\":\"场景设置成功\"}";
					} else {
						return "{\"result\":0,\"msg\":\"常用场景已满\"}";
					}
				}
			}
		case 6510:
			// 执行某个场景
			int _id6510 = new JSONObject(readBody.msg).getInt("_id");
			List<SceneCommandMsg> commandMsgs = sceneCommandMsgService.getSceneCommandMsgList(-1, -1, _id6510);
			if (null != commandMsgs) {
				ReadBody tmpReadBody = new ReadBody();
				tmpReadBody.mark = readBody.mark;
				for (int i = 0; i < commandMsgs.size(); i++) {
					tmpReadBody.type = commandMsgs.get(i).c_type;
					switch (tmpReadBody.type) {
					case 6404:
						// 空调控制
						tmpReadBody.msg = commandMsgs.get(i).c_msg;
						break;
					default:
						tmpReadBody.msg = "{\"_id\":" + commandMsgs.get(i).d_id + "}";
						break;
					}
					this.parse(tmpReadBody);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			return "{\"result\":0,\"msg\":\"场景设置成功\"}";
		default:
			break;
		}
		return null;
	}
}
