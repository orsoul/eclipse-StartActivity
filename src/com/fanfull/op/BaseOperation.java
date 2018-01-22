package com.fanfull.op;

import android.os.SystemClock;

import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.hardware.Hardware;

abstract class BaseOperation {
	private static final long GAP = 30;
	
	protected static final Hardware HARDWARE = Hardware.getInstance();
	protected final String TAG = this.getClass().getSimpleName();
	protected int fd = -1;

	/**
	 * @param fd
	 * @description 清空 指定 fd 数据
	 */
	protected void clearBuffer() {
		byte[] buf = new byte[8];
		while (0 < HARDWARE.read(fd, buf, buf.length)) {
			// empty
		}
	}

	/**
	 * @param fd
	 * @param cmd
	 * @param buf
	 * @param r
	 * @param timeGap
	 *            写入命令 与 读取串口 的时间隔
	 * @return 0<:命令执行成功, 0:串口未检测到数据, -1:执行命令失败, -2获取串口数据的长度小于1, -3:fd<1
	 */
	protected int runCmd(byte[] cmd, byte[] buf, long timeGap) {
		if (fd < 1) {
			LogsUtil.d(TAG, "runCmd() fd: " + fd);
			return -3;
		}

		int write = HARDWARE.write(fd, cmd);
		if (write < 1) {
			LogsUtil.d(TAG, "write() failed len: " + write);
			return -1;
		}
		clearBuffer();

		SystemClock.sleep(timeGap);

		int reVal = 0;
		if (1 != HARDWARE.select(fd, 1, 0)) {
			LogsUtil.d(TAG, "select() failed");
			return reVal;
		}

		SystemClock.sleep(timeGap);

		reVal = HARDWARE.read(fd, buf, buf.length);
		if (reVal < 1) {
			LogsUtil.d(TAG, "read() failed len: " + reVal);
			return -2;
		}
		// reVal = 1;

		return reVal;
	}

	/**
	 * @param fd
	 * @param cmd
	 * @param buf
	 * @param r
	 * @return 0<:命令执行成功, 0:串口未检测到数据, -1:执行命令失败, -2获取串口数据的长度小于1, -3:fd<1
	 */
	protected int runCmd(byte[] cmd, byte[] buf) {
		return runCmd(cmd, buf, GAP);
	}
	protected String bytes2HexString(byte[] bArray, int start, int end) {
		return ArrayUtils.bytes2HexString(bArray, start, end);
	}

	protected String bytes2HexString(byte[] bArray) {
		return ArrayUtils.bytes2HexString(bArray);
	}

	protected static byte[] hexString2Bytes(String hexString) {
		return ArrayUtils.hexString2Bytes(hexString);
	}
	/**
	 * 初始化模块
	 * @param isReinit 是否重新初始化。若为true，无论当前模块是否已经初始化，都会再次进行初始化。
	 * @return 已经初始化返回0；初始化成功返回大于0；初始化失败 返回小于0
	 */
	abstract int open(boolean isReinit);

	/**
	 * 判断模块是否打开
	 * @return
	 */
	abstract boolean isOpen();

	/**
	 * 关闭模块
	 */
	abstract void close();
}
