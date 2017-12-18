package com.fanfull.spi;

import java.util.List;

import android.util.Log;

import com.fanfull.hardwareAction.OLEDOperation;
import com.hardware.Hardware;

public class SPIThread extends Thread {
	private final String TAG = SPIThread.class.getSimpleName();
	public static int oneTextNumberLength = 72;
	private int fd_spi;
	private Hardware hardware;
	private byte[] buffer1;
	private byte[] buffer2;
	private byte[] buffer3;
	private byte[] buffer4;
	private long backColor = (long) 0xffffff;
	private long textColor =  (long) 0x000001;
	private int mTextNumber = 0;

	private final int textWitdh = 24;
	private final int textHeigth = 24;
	private final int screenWitdh = 159;
	private final int screenHeigth = 127;
	
	public SPIThread() {

	}

//	public SPIThread(int fd_spi, Hardware hardware, byte[] buffer) {
//		this.fd_spi = fd_spi;
//		this.hardware = hardware;
//		this.buffer1 = buffer;
//	}
	public SPIThread(int fd_spi, Hardware hardware,long textColor, long  backColor,byte[] buffer1) {
		this.fd_spi = fd_spi;
		this.hardware = hardware;
		this.buffer1 = buffer1;
		if(0 != textColor){
			this.textColor = textColor;
		} 
		if(0 != backColor){
			this.backColor = backColor;
		}
		if(null != buffer1) mTextNumber =  ( buffer1.length ) / ( SPIThread.oneTextNumberLength );
	}
	public SPIThread(int fd_spi, Hardware hardware,long textColor , long backColor ,byte[] buffer1, byte[] buffer2) {
		this.fd_spi = fd_spi;
		this.hardware = hardware;
		this.buffer1 = buffer1;
		this.buffer2 = buffer2;
		if(0 != textColor){
			this.textColor = textColor;
		} 
		if(0 != backColor){
			this.backColor = backColor;
		}
		if(null != buffer1) mTextNumber =  ( buffer1.length ) / ( SPIThread.oneTextNumberLength );
		if(null != buffer2) mTextNumber +=  ( buffer2.length ) / ( SPIThread.oneTextNumberLength );
	}
	
	
	/**
	 * 
	 * @param fd_spi  文件描述符
	 * @param hardware  链接动态库
	 * @param backColor 背景颜色
	 * @param textColor 文字显示颜色
	 * @param buffer1   存放第一行显示的文字
	 * @param buffer2  存放第二行显示的文字
	 * @param buffer3  存放第三行显示的文字
	 */
	public SPIThread(int fd_spi, Hardware hardware,long textColor, long backColor ,byte[] buffer1, byte[] buffer2, byte[] buffer3) {
		this.fd_spi = fd_spi;
		this.hardware = hardware;
		this.buffer1 = buffer1;
		this.buffer2 = buffer2;
		this.buffer3 = buffer3;
		if(0 != textColor){
			this.textColor = textColor;
		} 
		if(0 != backColor){
			this.backColor = backColor;
		}
		if(null != buffer1) mTextNumber =  ( buffer1.length ) / ( SPIThread.oneTextNumberLength );
		if(null != buffer2) mTextNumber +=  ( buffer2.length ) / ( SPIThread.oneTextNumberLength );
		if(null != buffer3) mTextNumber +=  ( buffer3.length ) / ( SPIThread.oneTextNumberLength );
	}
	
	public SPIThread(int fd_spi, Hardware hardware,long textColor, long backColor  ,byte[] buffer1
			, byte[] buffer2, byte[] buffer3, byte[] buffer4) {
		this.fd_spi = fd_spi;
		this.hardware = hardware;
		this.buffer1 = buffer1;
		this.buffer2 = buffer2;
		this.buffer3 = buffer3;
		this.buffer4 = buffer4;
		if(0 != textColor){
			this.textColor = textColor;
		} 
		if(0 != backColor){
			this.backColor = backColor;
		}
		if(null != buffer1) mTextNumber =  ( buffer1.length ) / ( SPIThread.oneTextNumberLength );
		if(null != buffer2) mTextNumber +=  ( buffer2.length ) / ( SPIThread.oneTextNumberLength );
		if(null != buffer3) mTextNumber +=  ( buffer3.length ) / ( SPIThread.oneTextNumberLength );
		if(null != buffer4) mTextNumber +=  ( buffer4.length ) / ( SPIThread.oneTextNumberLength );
	}
	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		if (Integer.valueOf(OLEDOperation.fd_spi) != null) {
//			try {
//				// 清屏
//				Hardware.getInstance().fillScreen(OLEDOperation.fd_spi,
//						(char) 0, (char) screenWitdh, (char) 0, (char) screenHeigth,
//						textColor);
//				// 写字操作
//				SPIgetPoint spIgetPoint = new SPIgetPoint();
//				Log.d(TAG, "mTextNumber="+mTextNumber);
//				List<SPIpoint> list = spIgetPoint.getSpIpoint(mTextNumber);
//				for (int i = 0; i < list.size(); i++) {
//					if(i== 0) {
//						Hardware.getInstance().transferString(
//								OLEDOperation.fd_spi, (char) buffer1.length,
//								(char) list.get(i).getxPoint(), (char) list.get(i).getyPoint(), (char) textWitdh, (char) textHeigth,
//								backColor,// 0xf800,0x003f,0x69B1BD,0x02ef
//								textColor, buffer1);
//					}else if (i == 1){
//						Hardware.getInstance().transferString(
//								OLEDOperation.fd_spi, (char) buffer2.length,
//								(char) list.get(i).getxPoint(), (char) list.get(i).getyPoint(), (char) textWitdh, (char) textHeigth,
//								backColor,// 0xf800,0x003f,0x69B1BD,0x02ef
//								textColor, buffer2);
//					}else if (i == 2){
//						Hardware.getInstance().transferString(
//								OLEDOperation.fd_spi, (char) buffer3.length,
//								(char) list.get(i).getxPoint(), (char) list.get(i).getyPoint(), (char) textWitdh, (char) textHeigth,
//								backColor,// 0xf800,0x003f,0x69B1BD,0x02ef
//								textColor, buffer3);
//					}else {
//						Hardware.getInstance().transferString(
//								OLEDOperation.fd_spi, (char) buffer4.length,
//								(char) list.get(i).getxPoint(), (char) list.get(i).getyPoint(), (char) textWitdh, (char) textHeigth,
//								backColor,// 0xf800,0x003f,0x69B1BD,0x02ef
//								textColor, buffer4);
//					}
//				}
//			} catch (NullPointerException ex) {
//				ex.printStackTrace();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
	}
}
