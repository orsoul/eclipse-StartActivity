package com.fanfull.hardwareAction;

import java.util.HashMap;
import java.util.List;

import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.spi.SPIThread;
import com.fanfull.spi.SPIgetPoint;
import com.fanfull.spi.SPIpoint;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.hardware.Hardware;

/**
 * @author orsoul
 * @description oled屏, 6个字 * 4行
 * 
 */
public class OLEDOperation {

	private static int fd = -1;
	private static Hardware mHardware;

	public static HashMap<String, String> mWordLib = new HashMap<String, String>();
	public static HashMap<String, String> mLoginWordLib = new HashMap<String, String>();
	/**
	 * oled 模块是否启用
	 */
	public static boolean enable;

	private final int TEXT_WITDH = 24;
	private final int TEXT_HEIGTH = 24;
	private final int SCREEN_WITDH = 159;
	private final int SCREEN_HEIGTH = 127;

	/**
	 * 背景颜色, 格式:RGB, 默认 黑色 0X000001()
	 */
	private long mBackColor = 0X000000;
	/**
	 * 文字颜色, 格式:RGB, 默认白色 0XFFFFFF
	 */
	private long mTextColor = 0XFFFFFF;

	public long getmBackColor() {
		return mBackColor;
	}

	public void setmBackColor(long mBackColor) {
		this.mBackColor = mBackColor;
	}

	public long getmTextColor() {
		return mTextColor;
	}

	public void setmTextColor(long mTextColor) {
		this.mTextColor = mTextColor;
	}

	private static OLEDOperation mOledOp;

	// 打开OLED屏
	private OLEDOperation() {
		mHardware = Hardware.getInstance();
	}

	public static OLEDOperation getInstance() {
		if (null == mOledOp) {
			mOledOp = new OLEDOperation();
		}
		return mOledOp;
	}

	/**
	 * @param enable
	 * @description 设置 oled 是否启用,若不启用,关闭oled模块
	 */
	public static void setEnable(boolean enable) {
		OLEDOperation.enable = enable;
		if (!OLEDOperation.enable && null != mOledOp) {
			mOledOp.destroy();
		}
	}

	public void open() {
		if (enable) {
			fd = mHardware.openSPI();
			LogsUtil.d("openSPI : " + fd);
		}
	}

	/**
	 * @return oled模块已启用 且 已打开
	 */
	public static boolean isOpen() {
		return enable && (-1 != fd);
	}

	/**
	 * @return
	 * @description 清屏, 用 黑色 铺满屏幕
	 */
	public int clearScreen() {
		int reVal = -1;
		if (isOpen()) {
			reVal = mHardware.fillScreen(fd, (char) 0, (char) SCREEN_WITDH,
					(char) 0, (char) SCREEN_HEIGTH, mBackColor);
		}
		return reVal;
	}

	public int transferString(char XSpos, char YSpos, byte[] buffer) {
		int reVal = -1;
		if (isOpen()) {
			reVal = mHardware.transferString(fd, (char) buffer.length, XSpos,
					YSpos, (char) TEXT_WITDH, (char) TEXT_HEIGTH, mTextColor,
					mBackColor, buffer);
		}
		return reVal;
	}

	/**
	 * @param text
	 * @return
	 * @description 将字符串 转成 字节数组, 供oled屏使用
	 */
	public static byte[] convertString2Buf(String text) {
		if (null == mWordLib) {
			LogsUtil.sf("mWordLib == null");
			return null;
		}

		String tmp = "";
		for (int i = 0; i < text.length(); i++) {
			char charAt = text.charAt(i);

			String v = mWordLib.get(charAt + "");
			if (null != v) {
				tmp += v;
//				LogsUtil.s(v);
			}
		}
		return ArrayUtils.hexString2Bytes(tmp);
	}

	/**
	 * @param text
	 * @return
	 * @description 将字符串 转成 字节数组, 供oled屏使用
	 */
	public static byte[] convertString2BufLogin(String text) {
		String tmp = "";
		for (int i = 0; i < text.length(); i++) {
			char charAt = text.charAt(i);
			String v = mLoginWordLib.get(charAt + "");
			if (null != v) {
				tmp += v;
//				LogsUtil.s(v);
			}
		}
		return ArrayUtils.hexString2Bytes(tmp);
	}

	
	/**
	 * @param textLines
	 * @description 支持 1-4 个参数, 对应oled屏 的1-4行
	 */
	public void showTextOnOled(final byte[]... textLines) {
		if (false == enable) {
			return;
		}

		if (null == textLines || 0 == textLines.length || 4 < textLines.length) {
			return;
		}
		

		if (-1 == fd) {
			open();
		}

		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				// 清屏
				int clearScreen = clearScreen();

				int textLen = 0;
				for (int i = 0; i < textLines.length; i++) {
					textLen += (textLines[i].length)
							/ (SPIThread.oneTextNumberLength);
				}

				List<SPIpoint> list = new SPIgetPoint().getSpIpoint(textLen);
				for (int i = 0; i < list.size(); i++) {
					transferString((char) list.get(i).getxPoint(), (char) list
							.get(i).getyPoint(), textLines[i]);
				}
			}
		});
	}
	
	public void showTextOnOled(String...textLines) {
		if (null == textLines || 0 == textLines.length || null == mWordLib) {
			return;
		}
		byte[][] buffs = new byte[textLines.length][];
		for (int i = 0; i < textLines.length; i++) {
			buffs[i] = convertString2Buf(textLines[i]);
		}
		showTextOnOled(buffs);
	}

	public void showTextOnLoginOled(String...textLines) {
		if (null == textLines || 0 == textLines.length) {
			return;
		}
		byte[][] buffs = new byte[textLines.length][];
		for (int i = 0; i < textLines.length; i++) {
			buffs[i] = convertString2BufLogin(textLines[i]);
		}
		showTextOnOled(buffs);
	}
	
	/**
	 * 关闭 OLED 串口
	 */
	public void close() {
		mHardware.sleep(fd, 0);
		mHardware.closeSPI();
		fd = -1;
	}

	/**
	 * 关闭 OLED 串口, 销毁对象
	 */
	public void destroy() {
		close();
		mOledOp = null;
	}

	/**
	 * 
	 * @Title: uclose
	 * @Description: 异常关闭
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void uclose() {
		mHardware.closeSPI();
		fd = -1;
		mOledOp = null;
	}

}
