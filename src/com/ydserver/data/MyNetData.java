package com.ydserver.data;

public class MyNetData {
	public final static String yunduoGroupAddress = "yunduoxmpp.ouarea.cc";
	public final static String yunduoGroupConference = "conference-yunduo.server.nightbar.cc";
	public static int serverPort = 5000;
	public static String loginIp = "ufun.cc";
	public static int loginPort = 5000;
	// public static String connectIp = "127.0.0.1";
	// public static int connectPort = 5000;
	public static String httpAddress;
	public static int httpPort;

	public static String getDownLoadAddress(String pic_url) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(httpAddress);
		sb.append(":");
		sb.append(httpPort);
		sb.append("/");
		sb.append(pic_url);
		return sb.toString();
	}

	// --------------------------------------------------------------
	public static final String ROOT_URL = "http://serviceapi.51obo.com/assets/upload/";
	public static final String[] ip = { "serviceapi.51obo.com", "192.168.1.229", "118.123.240.188", "192.168.1.110", "192.168.1.101", "118.123.240.187" };
	public static final String port = "80";
	public static final String urlRootPath = "";// CityLive
	public static final String urlFilePath = "index.php?";// test.php
	public static final int multiThreadNum = 3;
	public static final int socketTimeOut = 30 * 1000;
	public static final int connectTimeOut = 60 * 1000;
	public static final int defaultValue = -1; // 璁剧疆榛???硷??逛究?哄?
	public static String session = ""; // session??

	public static void setSession(String new_session) {
		if (new_session != null && new_session.length() > 5) {
			session = new_session;
		}
	}
}
