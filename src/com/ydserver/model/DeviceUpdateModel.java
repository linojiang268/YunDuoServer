package com.ydserver.model;

import java.util.List;

import com.ydserver.db.CommandInfo;
import com.ydserver.db.DeviceInfo;

/**
 * 设备新增、修改
 * 
 * @author laurencetsang
 * 
 */
public class DeviceUpdateModel {
	public DeviceInfo deviceInfo;
	public List<CommandInfo> commandInfos;
}
