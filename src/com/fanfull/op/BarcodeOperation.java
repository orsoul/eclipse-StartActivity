package com.fanfull.op;

import android.os.SystemClock;
import android.util.Log;

import com.fanfull.utils.LogsUtil;

/**
 * 
 */
public class BarcodeOperation extends BaseOperation {
	/** 串口 */
	private final String SERIAL_PORT = "/dev/s3c2410_serial3";
	/** 波特率 9600 */
	private final int BAUDRATE = 9600;
	
	private boolean isScanning;

	private static BarcodeOperation instance = null;

	public static BarcodeOperation getInstance() {
		if (null == instance) {
			instance = new BarcodeOperation();
		}
		return instance;
	}

	public static void sClose() {
		if (null != instance) {
			instance.close();
		}
	}

	/**
	 * 解密原始锁片数据
	 * 
	 * @param data
	 *            锁片上的二维码
	 */
	public static void DecodeBarcode(byte[] data) {
		HARDWARE.DecodeBarcode(data);
	}

	/**
	 * @param isTurnOn true 点亮读头；
	 * @return
	 */
	public int light(boolean isTurnOn) {
		if (isTurnOn) {
			return HARDWARE.setGPIO(0, 1);
		} else {
			return HARDWARE.setGPIO(1, 1);
		}
	}

	@Override
	public int open(boolean isReinit) {

		if (isOpen() && !isReinit) {
			LogsUtil.d(TAG, "open() : openned");
			return 0;
		}

		HARDWARE.openGPIO();
		HARDWARE.setGPIO(0, 0);// 通过设置GPIO控制模块电源
		HARDWARE.setGPIO(0, 2);// 读取数据控制

		SystemClock.sleep(500);// 上电延迟

		fd = HARDWARE.openSerialPort(SERIAL_PORT, BAUDRATE, 8, 1);// 打开串口
		LogsUtil.d(TAG, "open : fd = " + fd);

		if (fd < 1) {
			HARDWARE.setGPIO(1, 0);// 通过设置GPIO控制模块电源
			HARDWARE.setGPIO(1, 2);// 关闭电源
			HARDWARE.closeGPIO();
		}

		return fd;
	}

	/**
	 * @return 是否已经打开串口
	 */
	@Override
	public boolean isOpen() {
		return -1 != fd;
	}
	public int getFd() {
		return fd;
	}
	@Override
	public void clearBuffer() {
		byte[] buf = new byte[8];
		while (0 < HARDWARE.read(fd, buf, buf.length)) {
			// empty
		}
//		super.clearBuffer();
	};

	/**
	 * 
	 * @Title: close
	 * @Description:关闭
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	/**
	 * @Description:关闭模块 hardware.setGPIO(1, 1); hardware.setGPIO(1, 0);
	 *                   hardware.setGPIO(1, 2);
	 * 
	 */
	@Override
	public void close() {
		HARDWARE.setGPIO(1, 1);// 熄灭读头
		HARDWARE.setGPIO(1, 0);// 通过设置GPIO控制模块电源
		HARDWARE.setGPIO(1, 2);// 关闭电源
		HARDWARE.closeGPIO();

		HARDWARE.close(fd);// 关闭模块
		fd = -1;
		instance = null;
		Log.d(TAG, "--close barcode--");
	}
	public void stopScan() {
		isScanning = false;
	}
	public int scanData(byte[] buf, int readTimes, long runTime,
			BarcodeReadTask callBack) {
		isScanning = true;

		if (fd < 1) {
			LogsUtil.d(TAG, "runCmd() fd: " + fd);
			return -3;
		}

		clearBuffer();

		int len = -1;
		// long start = System.currentTimeMillis();
		long stopTime = runTime + System.currentTimeMillis();
		while (isScanning && 0 != readTimes && (System.currentTimeMillis() < stopTime)) {
			LogsUtil.d(TAG, readTimes + "  " + System.currentTimeMillis());

			light(true);
			
			int select = HARDWARE.select(fd, 0, 200 * 1000);

			// 扫到 捆封签 控制读头停止扫描
			light(false);
			SystemClock.sleep(50);
			
			if (select != 1) {
				LogsUtil.d(TAG, "select failed");
				continue;
			}


			len = HARDWARE.read(fd, buf, buf.length);

			LogsUtil.d(TAG, len);

			if (callBack != null) {
				callBack.onReadResult(buf, len, callBack.getMsgWhat());
			} else {
				break;
			}

			readTimes--;
			stopTime = runTime + System.currentTimeMillis();
			SystemClock.sleep(50);
		} // end while
		
		isScanning = false;
		return len;
	}

}
