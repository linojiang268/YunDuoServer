package Wireless;

import java.io.IOException;

import android_serialport_api.SerialPort;

public class CodingLearn {

	// public byte hostMac[] = new byte[]{0x00,0x00,0x00,0x09,0x09,0x09};
	// 红外学习
	public static void IRlearning() throws IOException {
		byte cmd[] = new byte[] { (byte) 0xAA, 0x11, 0x00, 0x00, 0x00, 0x00, 0x55 };
		// 发送学习指令
		// mOutputStream.writeB('\n');
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 433M无线学习
	public static void RF433learning() throws IOException {
		byte cmd[] = new byte[] { (byte) 0xAA, 0x12, 0x00, 0x00, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 315M无线学习
	public static void RF315learning() throws IOException {
		byte cmd[] = new byte[] { (byte) 0xAA, 0x13, 0x00, 0x00, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 无线学习
	public static void RFlearning() throws IOException {
		// byte cmd[] = new byte[] { (byte) 0xAA, 0x14, 0x00, 0x00, 0x00, 0x00,
		// 0x55 };
		// 433M
		byte cmd[] = new byte[] { (byte) 0xAA, 0x12, 0x00, 0x00, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 退出学习
	public static void EscLearning() throws IOException {
		byte cmd[] = new byte[] { (byte) 0xAA, 0x15, 0x00, 0x00, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}
}
