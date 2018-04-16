package Wireless;

import java.io.IOException;

import android_serialport_api.SerialPort;

public class SendCode {
	// 发送编码
	public static void SendCodes(String str) throws IOException {
		// if (str.substring(0, 4).equals("Code:")) {
		byte[] desBytes = Htool.hexStringToBytes(str);
		SerialPort.sp.writeB(desBytes, 0, desBytes.length);
		// }
	}

	// 发送红外编码
	public static void SendIR() throws IOException {
		byte[] cmd = new byte[] { (byte) 0xAA, 0x16, 0x00, 0x00, (byte) 0xE5, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 433M编码发送
	public static void SendRF433() throws IOException {
		byte cmd[] = new byte[] { (byte) 0xAA, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}

	// 315M编码发送
	public static void SendRF315() throws IOException {
		byte cmd[] = new byte[] { (byte) 0xAA, 0x16, 0x00, 0x00, (byte) 0xEB, 0x00, 0x00, 0x55 };
		SerialPort.sp.writeB(cmd, 0, cmd.length);
	}
}
