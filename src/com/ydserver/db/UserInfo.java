package com.ydserver.db;

public class UserInfo {
	/**
	 * 数据库id
	 */
	public Integer id = null;
	public String password;
	public String security_phone_numbers;

	public UserInfo() {
		super();
	}

	public UserInfo(Integer id, String password, String security_phone_numbers) {
		super();
		this.id = id;
		this.password = password;
		this.security_phone_numbers = security_phone_numbers;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecurity_phone_numbers() {
		return security_phone_numbers;
	}

	public void setSecurity_phone_numbers(String security_phone_numbers) {
		this.security_phone_numbers = security_phone_numbers;
	}

}
