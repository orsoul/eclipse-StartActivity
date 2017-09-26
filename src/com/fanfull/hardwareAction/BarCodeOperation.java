package com.fanfull.hardwareAction;

import android.os.SystemClock;
import android.util.Log;

import com.fanfull.utils.LogsUtil;
import com.hardware.Hardware;

/**
 * 
 * @ClassName: BarCodeOperation
 * @Description: 扫条码
 * @author Keung
 * @date 2014-9-5 上午11:01:50
 * 
 */
public class BarCodeOperation {
	private final static String TAG = BarCodeOperation.class.getSimpleName();
	
	public static int fd = 0;
	private final static String choosed_serial = "/dev/s3c2410_serial3";// 设置串口号
	private final static int choosed_buad = 9600;// 设置波特率
	
	byte[] buf = new byte[100];
	int count = 0;
	public Hardware hardware;// GPIO控制
	private static  BarCodeOperation mBarCodeOperation = null;

	public BarCodeOperation() {
		hardware = Hardware.getInstance();
	}
	public static BarCodeOperation getInstance() {
		if(null == mBarCodeOperation) {
			mBarCodeOperation = new BarCodeOperation();
		}
		return mBarCodeOperation;
	}

	public static BarCodeOperation getCloseInstance() {
		if(null == mBarCodeOperation) {
			mBarCodeOperation = new BarCodeOperation();
		}
		return mBarCodeOperation;
	}
	/**
	 * @Description: 初始化读头
	 */
	public boolean connection() {
		hardware.openGPIO();
        hardware.setGPIO(0, 0);//通过设置GPIO控制模块电源
        hardware.setGPIO(0, 2);//读取数据控制
        
        SystemClock.sleep(500);//上电延迟
		fd = hardware.openSerialPort(choosed_serial, choosed_buad, 8, 1);// 打开串口
		// 使用前 清空缓存
//		hardware.read(fd, buf, buf.length);
		LogsUtil.d(TAG, "串口打开"+choosed_serial +": " + (fd != -1)+"   fd="+fd);
		if (0 < fd) {
			return true;
		} else {
			hardware.setGPIO(1, 0);// 通过设置GPIO控制模块电源
			hardware.setGPIO(1, 2);// 关闭电源
			hardware.closeGPIO();
			return false;
		}
		
	}
	/**
	 * @return 是否已经打开串口
	 */
	public boolean isOpen() {
		return -1 != fd;
	}

	/**
	 * @Description: 点亮读头
	 */
	public int  scan() {
		return hardware.setGPIO(0, 1);
	}
	/**
	 * @Description: 熄灭读头
	 */
	public void stopScan() {
		hardware.setGPIO(1, 1);
	}

	/**
	 * 
	 * @Title: close
	 * @Description:关闭
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	/**
	 * @Description:关闭模块
	 * hardware.setGPIO(1, 1);
	 * hardware.setGPIO(1, 0);
	 * hardware.setGPIO(1, 2);
	 * 
	 */
	public void close() {
		Log.d(TAG, "--close--读头");
		hardware.setGPIO(1, 1);// 熄灭读头
		hardware.setGPIO(1, 0);// 通过设置GPIO控制模块电源
		hardware.setGPIO(1, 2);// 关闭电源
		hardware.closeGPIO();
		
		hardware.close(fd);// 关闭模块
		fd = -1;
	}

}
