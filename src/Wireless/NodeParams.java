package Wireless;

import java.io.IOException;

import android_serialport_api.SerialPort;

public class NodeParams {
	// 读取参数
	public void read() {

	}

	// 设置参数
	public static void set(int type, String Mac) throws IOException {
		byte cmd[] = new byte[] { (byte) 0xDD, 0x34, 0x00, 0x0B, 0x66, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, (byte) 0xB4, 0x01, (byte) 200, 0x01, 0x02, 0x55 };
		byte data = 0;
		switch (type) {
		// 灯光开
		case 6103:
			cmd[4] = 0x66;
			data = 0x0F;
			break;
		// 灯光关
		case 6104:
			cmd[4] = 0x66;
			data = (byte) 0xF0;
			break;
		// 窗帘开
		case 6203:
			cmd[4] = 0x67;
			data = (byte) 0x0F;
			break;
		// 窗帘关
		case 6204:
			cmd[4] = 0x67;
			data = (byte) 0xF0;
			break;
		// 窗帘停
		case 6206:
			cmd[4] = 0x67;
			data = (byte) 0xFF;
			break;
		// 监控开
		case 6603:
			cmd[4] = 0x61;
			data = (byte) 0x0F;
			break;
		// 监控关
		case 6604:
			cmd[4] = 0x61;
			data = (byte) 0xF0;
			break;
		default:
			break;
		}
		byte[] Bytes = Htool.hexStringToBytes(Mac);
		for (int i = 0; i < 6; i++) {
			cmd[6 + i] = Bytes[i];
		}
		cmd[13] = Bytes[6];
		cmd[14] = data;

		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 设置参数
	public static void set(int type, String Mac, String strCode) throws IOException {
		// Mac = "010203040501";
		//byte data = 0;
		String str1 = new String("DD34");
		String str2 = new String("6000");
		String str3 = new String("BD");

		String str = str1 + strCode.substring(4, 8) + str2 + Mac.substring(0, 12) + str3 + strCode.substring(10);

		byte cmd[] = Htool.hexStringToBytes(str);

		// byte[] Bytes = Htool.hexStringToBytes(Mac);
		// byte cmd[] = new byte[] { (byte) 0xDD, 0x34, 0x00, 0x0B, 0x66, 0x00,
		// 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, (byte) 0xB4, 0x01, (byte) 200,
		// 0x01, 0x02, 0x55 };
		// for (int i = 0; i < 6; i++) {
		// cmd[6 + i] = Bytes[i];
		// }
		// cmd[13] = Bytes[6];
		// cmd[14] = data;

		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 接收成功
	public static void ReceiveSuccess(byte[] Bytes) throws IOException {
		byte cmd[] = new byte[] { (byte) 0xDD, 0x35, 0x00, 0x08, 0x66, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x01, 0x02, 0x55 };
		for (int i = 0; i < 6; i++) {
			cmd[6 + i] = Bytes[12 + i];
		}
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 接收失败
	public static void ReceiveError(byte[] Bytes) throws IOException {
		byte cmd[] = new byte[] { (byte) 0xDD, 0x36, 0x00, 0x08, 0x66, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x01, 0x02, 0x55 };
		for (int i = 0; i < 6; i++) {
			cmd[6 + i] = Bytes[12 + i];
		}
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}
}
