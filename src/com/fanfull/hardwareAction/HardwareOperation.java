package com.fanfull.hardwareAction;

import android.os.SystemClock;

import com.fanfull.utils.LogsUtil;
import com.hardware.Hardware;

public class HardwareOperation {

	/**
	 * @param fd
	 * @param buf
	 * @description 清空 指定 fd 数据
	 */
	public static void clearBuffer(int fd, byte[] buf) {
		while (0 < Hardware.getInstance().read(fd, buf, buf.length)) {
			// empty
		}
	}

	/**
	 * @param fd
	 * @param cmd
	 * @param buf
	 * @param r
	 * @return 0<:命令执行成功, 0:串口未检测到数据, -1:执行命令失败, -2获取串口数据的长度小于1, -3:fd<1
	 */
	/**
	 * @param fd
	 * @param cmd
	 * @param buf
	 * @param r
	 * @param timeGap 写入命令 与 读取串口 的时间隔
	 * @return 0<:命令执行成功, 0:串口未检测到数据, -1:执行命令失败, -2获取串口数据的长度小于1, -3:fd<1
	 */
	public static int runCmd(int fd, byte[] cmd, byte[] buf, Runnable r, long timeGap) {
		if (fd < 1) {
			LogsUtil.d("runCmd", "fd:" + fd);
			return -3;
		}
		
		int write = Hardware.getInstance().write(fd, cmd);
		if (write < 1) {
			LogsUtil.d("runCmd", "write() failed len:" + write);
			return -1;
		}
		clearBuffer(fd, buf);

		SystemClock.sleep(timeGap);
		
		int reVal = 0;
		if (1 != Hardware.getInstance().select(fd, 1, 0)) {
			LogsUtil.d("runCmd", "select() failed");
			return reVal;
		}
		
		SystemClock.sleep(timeGap);

		reVal = Hardware.getInstance().read(fd, buf, buf.length);
		if (reVal < 1) {
			LogsUtil.d("runCmd", "read() failed len:" + reVal);
			return -2;
		}
		// reVal = 1;

		if (null != r) {
			r.run();
		}

		return reVal;
	}
	/**
	 * @param fd
	 * @param cmd
	 * @param buf
	 * @param r
	 * @return 0<:命令执行成功, 0:串口未检测到数据, -1:执行命令失败, -2获取串口数据的长度小于1, -3:fd<1
	 */
	public static int runCmd(int fd, byte[] cmd, byte[] buf, Runnable r) {
		return runCmd(fd, cmd, buf, r, 50);
	}
}
