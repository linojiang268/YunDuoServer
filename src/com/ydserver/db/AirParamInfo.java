package com.ydserver.db;

/**
 * 
 * @author ouArea
 * 
 */
public class AirParamInfo {
	/**
	 * 数据库id
	 */
	public Integer _id = null;
	/**
	 * 空调设备id
	 */
	public Integer device_id;
	/**
	 * 电源(开0、关1)
	 */
	public Integer power;
	/**
	 * 模式(自动0、冷气1、除湿2、送风3、暖气4)
	 */
	public Integer model;
	/**
	 * 风速(小0、中1、大2)
	 */
	public Integer wind_speed;
	/**
	 * 风向(停0、动1)
	 */
	public Integer wind_direction;
	/**
	 * 温度(17-32)
	 */
	public Integer temperature;
	/**
	 * 定时
	 */
	public Long timing = 0l;
	public String command;
	public String name;

	public AirParamInfo() {
		super();
	}

	public AirParamInfo(Integer _id, Integer device_id, Integer power, Integer model, Integer wind_speed, Integer wind_direction, Integer temperature, Long timing, String command, String name) {
		super();
		this._id = _id;
		this.device_id = device_id;
		this.power = power;
		this.model = model;
		this.wind_speed = wind_speed;
		this.wind_direction = wind_direction;
		this.temperature = temperature;
		this.timing = timing;
		this.command = command;
		this.name = name;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public Integer getPower() {
		return power;
	}

	public void setPower(Integer power) {
		this.power = power;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public Integer getWind_speed() {
		return wind_speed;
	}

	public void setWind_speed(Integer wind_speed) {
		this.wind_speed = wind_speed;
	}

	public Integer getWind_direction() {
		return wind_direction;
	}

	public void setWind_direction(Integer wind_direction) {
		this.wind_direction = wind_direction;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public Long getTiming() {
		return timing;
	}

	public void setTiming(Long timing) {
		this.timing = timing;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
