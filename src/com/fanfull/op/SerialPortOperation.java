package com.fanfull.op;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

import com.fanfull.utils.LogsUtil;

public class SerialPortOperation {
	private static final String TAG = SerialPortOperation.class.getSimpleName();

	private static final String path = "/dev/s3c2410_serial1";// 指定端口

	private static boolean enable;
	private static int baudrate = 19200;// 波特率：4800, 9600, 14400, 19200, 38400,
										// 56000, 115200

	/**
	 * @return 当前使用的 波特率
	 */
	public static int getBaudrate() {
		return baudrate;
	}

	/**
	 * 设置波特率，常用波特率：4800, 9600, 14400, 19200, 38400, 56000, 115200
	 * 
	 * @param baudrate
	 *            波特率
	 */
	public static void setBaudrate(int baudrate) {
		SerialPortOperation.baudrate = baudrate;
	}
	
	

	public static boolean isEnable() {
		return enable;
	}

	public static void setEnable(boolean enable) {
		SerialPortOperation.enable = enable;
	}



	public static SerialPort sSerialPort = null;

	public static InputStream getInputStream() {
		if (!isOpen()) {
			return null;
		}
		return sSerialPort.getInputStream();
	}

	public static OutputStream getOutputStream() {
		if (!isOpen()) {
			return null;
		}
		return sSerialPort.getOutputStream();
	}

	public static SerialPort getSerialPort() {
		return sSerialPort;
	}

	public static boolean open(boolean reOpen) {

		if (reOpen) {
			close();
		}

		if (!isOpen()) {
			try {
				sSerialPort = new SerialPort(new File(path), baudrate, 0);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isOpen();
	}

	public static boolean isOpen() {
		return null != sSerialPort;
	}

	public static boolean close() {
		if (null != sSerialPort) {
			try {
				FileInputStream fileInputStream = (FileInputStream) sSerialPort
						.getInputStream();
				fileInputStream.close();
				FileOutputStream fileoutputStream = (FileOutputStream) sSerialPort
						.getOutputStream();
				fileoutputStream.close();
				// sSerialPort.getInputStream().close();
				// sSerialPort.getOutputStream().close();
			} catch (Exception e) {
				LogsUtil.e(TAG, "close()");
				e.printStackTrace();
			}
			sSerialPort.close();
			sSerialPort = null;
		}
		LogsUtil.w(TAG, "close");
		return false;
	}

	public static boolean send(byte[] data) {
		if (null == data || !isOpen()) {
			return false;
		}

		try {
			sSerialPort.getOutputStream().write(data);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean send(String text) {
		if (null == text) {
			return false;
		}
		return send(text.getBytes());
	}

	private class ReceiveTask implements Runnable {
		@Override
		public void run() {
			byte recBuf[] = new byte[1024 << 2]; // 每次接受数据的buf
			int recLen = 0;
			while (isOpen()) {
				try {
					recLen = getInputStream().read(recBuf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
