package com.ydserver.model;

import java.util.List;

import com.ydserver.db.AirParamInfo;
import com.ydserver.db.DeviceInfo;

/**
 * 空调设备新增、修改
 * 
 * @author ouArea
 * 
 */
public class AirUpdateModel {
	public DeviceInfo deviceInfo;
	public List<AirParamInfo> airParamInfos;
}
