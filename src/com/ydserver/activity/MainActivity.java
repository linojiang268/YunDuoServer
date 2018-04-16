package com.ydserver.activity;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 服务端
 * 
 * @author ouArea
 * 
 */
public class MainActivity extends Activity {
	private TextView mtv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Toast.makeText(MainActivity.this, "欢迎进入云朵智能家居管理系统", Toast.LENGTH_LONG).show();
		this.mtv = (TextView) findViewById(R.id.tv);
		mtv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// startActivity(new Intent(MainActivity.this, MainTest.class));
				// testDeviceDb();
				// testCommandDb();
			}
		});
		startService(new Intent(MainActivity.this, YDServerService.class));
		startActivity(new Intent(MainActivity.this, MainTest.class));
		// 发短信
		// SMSMsgTool.send("158", "neirong", null);
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

	@Override
	protected void onDestroy() {
		stopService(new Intent(MainActivity.this, YDServerService.class));
		super.onDestroy();
	}
	// ------------------数据库---------------------
	// private void testDeviceDb() {
	// DeviceService deviceService = new DeviceService(MainActivity.this);
	// deviceService.insertDevice(new DeviceInfo(null, 101, "abcefghijklmn1",
	// "客厅1"));
	// deviceService.insertDevice(new DeviceInfo(null, 102, "abcefghijklmn2",
	// "客厅2"));
	// deviceService.insertDevice(new DeviceInfo(null, 103, "abcefghijklmn3",
	// "客厅3"));
	// DeviceInfo deviceInfo = deviceService.findDevice("abcefghijklmn2");
	// ArrayList<DeviceInfo> deviceInfos = (ArrayList<DeviceInfo>)
	// deviceService.getDeviceList(-1, -1, 101, 103);
	// long num = deviceService.getCountWithType(101, 103);
	// deviceService.delete(1, 3, 4, 6, 9);
	// deviceInfos = (ArrayList<DeviceInfo>) deviceService.getDeviceList(-1, -1,
	// 101, 103);
	// }

	// private void testCommandDb() {
	// CommandService commandService = new CommandService(MainActivity.this);
	// commandService.insertDevice(new CommandInfo(103, 10001, "abcdefghijklmn",
	// "开电视"));
	// commandService.insertDevice(new CommandInfo(103, 10002, "abcdefghijklmn",
	// "开电视"));
	// commandService.insertDevice(new CommandInfo(103, 10003, "abcdefghijklmn",
	// "开电视"));
	// commandService.insertDevice(new CommandInfo(104, 10001, "abcdefghijklmn",
	// "开电视"));
	// commandService.insertDevice(new CommandInfo(105, 10003, "abcdefghijklmn",
	// "开电视"));
	// commandService.insertDevice(new CommandInfo(106, 10003, "abcdefghijklmn",
	// "开电视"));
	// CommandInfo commandInfo = commandService.findCommand(103, 10002);
	// boolean a = commandService.delete(103, 105);
	// boolean b = commandService.deleteCommand(104, 10001);
	//
	// CommandInfo commandInfo2 = commandService.findCommand(103, 10002);
	// CommandInfo commandInfo3 = commandService.findCommand(104, 10001);
	// CommandInfo commandInfo4 = commandService.findCommand(105, 10003);
	// CommandInfo commandInfo5 = commandService.findCommand(106, 10003);
	// }
}
