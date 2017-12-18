package com.fanfull.hardwareAction;

import java.util.Arrays;

import android.os.SystemClock;

import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.hardware.Hardware;

/**
 * 
 * @ClassName: NfcOperation
 * @Description: NFC卡 操作类
 * @author Zyp
 * @date 2016-03-07 上午09：30
 * 
 */
public class NfcOperation {
	private final static String TAG = NfcOperation.class.getSimpleName();
	/** 串口操作对象 */
	private final Hardware hardware;
	/** NFC卡操作对象，定义静态的，单例模式 */
	private static NfcOperation mNfcOperation = null;
	/** 获取到M1卡的实例，主要是调用其中打卡的文件描述符fd的值以及寻卡方法，单例模式 */
	private RFIDOperation mRfidOperation;
	/** 接收回复信息 */
	private byte[] buf = new byte[48];
	/** 读写命令的长度 */
	private int len;
	private byte[] mUid = null;
	private byte[] readcmd = new byte[] { (byte) 0x00, (byte) 0x00,
			(byte) 0xFF, (byte) 0x05, (byte) 0xFB, (byte) 0xD4, (byte) 0x40,
			(byte) 0x01, (byte) 0x30, (byte) 0x06, (byte) 0xB5, (byte) 0x00 };

	byte[] writeData = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0xff,
			(byte) 0x09, (byte) 0xf7, (byte) 0xd4, (byte) 0x40, (byte) 0x01,
			(byte) 0xA2, (byte) 0x07, (byte) 0x44, (byte) 0x33, (byte) 0x22,
			(byte) 0x11, (byte) 0x98, (byte) 0x00 };// 将数据写在07地址，数据为11 22 33 44

	public NfcOperation() {
		hardware = Hardware.getInstance();
		mRfidOperation = RFIDOperation.getInstance();
	}

	public static NfcOperation getInstance() {
		if (null == mNfcOperation) {
			mNfcOperation = new NfcOperation();
		}
		return mNfcOperation;
	}

	/**
	 * 将一个字符串转化成一个字节数组
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 将一个字符转化成字节
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 验证回复的信息的前六位是否准确
	 * 
	 * @param buf
	 *            命令返回的信息
	 * @return false 前六位验证不成功，true 验证通过
	 */
	private boolean checkFirst6Data(byte[] buf) {
		if (null == buf || buf.length < 6) {
			return false;
		}
		if (buf[0] == (byte) 0x00 && buf[1] == (byte) 0x00
				&& buf[2] == (byte) 0xff && buf[3] == (byte) 0x00
				&& buf[4] == (byte) 0xff && buf[5] == (byte) 0x00) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * reaAddr 读取NFC卡指定位置的内容
	 * 
	 * @param addr开始地址
	 *            一个地址存储四个字节
	 * @return 指定位置开始连续四个地址共十六个字节的十六进制转换后的字符串
	 */
	public String reaAddr(byte addr) {
		// if(null == activatecard()){
		// LogsUtil.d(TAG, "寻卡错误");
		// return null;
		// }
		byte buf[] = new byte[32];
		while ((len = hardware.read(mRfidOperation.fd, buf, 48)) > 0)
			;// clear buf

		readcmd[9] = addr;
		readcmd[10] = getCheckNumber(5, readcmd);
		len = hardware.write(mRfidOperation.fd, readcmd);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (hardware.select(mRfidOperation.fd, 1, 0) == 1) {
			len = hardware.read(mRfidOperation.fd, buf, 32);
			if (len < 1) {
				LogsUtil.d("107 read error");
			}
			if (checkFirst6Data(buf) && buf[12] == 0x41 && buf[13] == 0x00) {
				byte[] readbuf;
				readbuf = new byte[16];
				for (int i = 0; i < 16; i++) {
					readbuf[i] = buf[i + 14];
				}
				return ArrayUtils.bytes2HexString(readbuf);
			} else {
				LogsUtil.d(TAG, "read data error....");// 读取数据错误，可能是认证不成功
				return null;
			}
		} else {
			LogsUtil.d(TAG, "no data read");
			return null;
		}
	}

	/**
	 * reaAddr 读取NFC卡指定位置的内容
	 * 
	 * @param addr开始地址
	 *            一个地址存储四个字节
	 * @return 指定位置开始连续四个地址共十六个字节的字节数据
	 */
	public byte[] reaAddrToByte(int addr) {
		if (null == activatecard()) {
			LogsUtil.d(TAG, "寻卡错误");
			return null;
		}
		byte buf[] = new byte[32];
		while ((len = hardware.read(mRfidOperation.fd, buf, 48)) > 0)
			;// clear buf

		readcmd[9] = (byte) addr;
		readcmd[10] = getCheckNumber(5, readcmd);
		len = hardware.write(mRfidOperation.fd, readcmd);
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (hardware.select(mRfidOperation.fd, 1, 0) == 1) {
			len = hardware.read(mRfidOperation.fd, buf, 32);
			if (len < 1) {
				LogsUtil.d("107 read error");
			}
			if (checkFirst6Data(buf) && buf[12] == 0x41 && buf[13] == 0x00) {
				byte[] readbuf = null;
				readbuf = new byte[16];
				for (int i = 0; i < 16; i++) {
					readbuf[i] = buf[i + 14];
				}
				return readbuf;
			} else {
				LogsUtil.d(TAG, "校验错误 buf：" + Arrays.toString(buf));// 读取数据错误，
				return null;
			}
		} else {
			LogsUtil.d(TAG, "no data read");
			return null;
		}
	}

	public boolean readData(int sa, int dataLen, byte[] dataBuf, int dsa,
			byte[] tid) {
		boolean reVal = false;
		if (sa < 0 || 16 < dataLen || dsa < 0 || null == dataBuf
				|| dataBuf.length <= dsa) {
			LogsUtil.d(TAG, "readData() args wrong");
			return reVal;
		}

		if (null == tid || 7 != tid.length) {
			tid = activatecard();
			if (null == tid) {
				LogsUtil.d(TAG, "readData() 寻卡失败");
				return reVal;
			}
		}

		byte buf[] = new byte[32];
		while ((len = hardware.read(mRfidOperation.fd, buf, buf.length)) > 0)
			;// clear buf

		readcmd[9] = (byte) sa;
		readcmd[10] = getCheckNumber(5, readcmd);

		if (hardware.write(mRfidOperation.fd, readcmd) < 1) {
			LogsUtil.d(TAG, "readData() write failed");
			return reVal;
		}
		
		SystemClock.sleep(50);
		
		if (hardware.select(mRfidOperation.fd, 1, 0) == 1) {
			len = hardware.read(mRfidOperation.fd, buf, buf.length);
			LogsUtil.d(TAG, "readData() read len:" + len);

			if (checkFirst6Data(buf) && buf[12] == 0x41 && buf[13] == 0x00) {
				if (dataBuf.length - dsa < dataLen) {
					dataLen = dataBuf.length - dsa;
				}
				for (int i = 0; i < dataLen; i++) {
					dataBuf[dsa + i] = buf[i + 14];
				}
				String str = ArrayUtils.bytes2HexString(dataBuf);
				LogsUtil.d(TAG, "readData() data:" + str);
				reVal = true;
			} else {
				LogsUtil.d(TAG, "readData() 校验错误 buf：" + Arrays.toString(buf));// 读取数据错误
			}
		} else {
			LogsUtil.d(TAG, "readData() select failed");
		}
		return reVal;
	}

	/**
	 * writeAddr
	 * 
	 * @param addr
	 * @return 注：每次只能写一个地址即4字节
	 */
	public boolean writeAddr(byte addr, byte[] data) {
		// if(null == activatecard()){
		// LogsUtil.d(TAG, "寻卡错误");
		// return false;
		// }
		while ((len = hardware.read(mRfidOperation.fd, buf, 48)) > 0)
			;// clear buf
		writeData[9] = (byte) addr;
		for (int i = 0; i < data.length; i++) {
			writeData[10 + i] = data[i];
		}
		writeData[writeData.length - 2] = getCheckNumber(writeData.length - 7,
				writeData);
		len = hardware.write(mRfidOperation.fd, writeData);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (hardware.select(mRfidOperation.fd, 1, 0) == 1) {
			len = hardware.read(mRfidOperation.fd, buf, 36);
			if (len < 1) {
				LogsUtil.e("read error");
			}
			if (checkFirst6Data(buf) && buf[6] == (byte) 0x00
					&& buf[7] == (byte) 0x00 && buf[8] == (byte) 0xff
					&& buf[9] == (byte) 0x03 && buf[10] == (byte) 0xfd
					&& buf[11] == (byte) 0xd5 && buf[12] == (byte) 0x41
					&& buf[13] == (byte) 0x00 && buf[14] == (byte) 0xea
					&& buf[15] == (byte) 0x00) {
				LogsUtil.d(TAG, "写入成功");
				return true;
			} else {
				LogsUtil.d(TAG, "write data error....");// 写入数据错误，可能是认证不成功
				return false;
			}
		} else {
			LogsUtil.e(TAG, "no data read");
			return false;
		}
	}
	
	public boolean writeDatas(int sa, byte[] data) {
		int n = 4;
		int len = (data.length + n - 1) / n;
		
		byte datas[][] = new byte[len][n];
		LogsUtil.d(TAG, "datas[" + len + "][" + n);
		int p = 0;
		for (int i = 0; i < data.length; i++) {
			int x = i / n;
			int y = i % n;
				LogsUtil.d(TAG, "x:" + x + " y:" + y);
				datas[x][y] = data[p++];
		}
		
		for (int i = 0; i < datas.length; i++) {
			if (!writeAddr((byte) (sa + i), datas[i])) {
				return false;
			}
		}
		
		return true;
	}
	public boolean writeDatas(int sa, String data) {
		return writeDatas(sa, ArrayUtils.hexString2Bytes(data));
	}

	/**
	 * 通过传入命令，以及数据位的长度，得到最后的检验和，校验和 和数据位总和异或为0
	 * 
	 * @param n
	 *            命令数据位的长度
	 * @param cmd
	 *            命令
	 * @return 字节数据，得到校验和
	 */
	private byte getCheckNumber(int n, byte cmd[]) {
		byte a = 0x00;
		for (int i = 0; i < n; i++) {
			a += cmd[i + 5];
		}
		String string = Integer.toHexString(~(0xFF & a) + 1);
		if (string != null && string.equals("0")) {
			return (byte) 0x00;
		} else if (string == null) {
			return (byte) 0x00;
		}
		if (string.length() > 2) {
			string = (String) string.subSequence(string.length() - 2,
					string.length());
		}
		byte[] ab = hexStringToBytes(string);
		return (byte) ab[0];
	}

	/**
	 * 
	 * @Title: 读封签码 ,读第地址从0x30开始的数据（在封签操作的时候需要把扫描到的袋ID内容和条码内容合并后
	 *         写入到0x30地址去，这里在入库的时候需要读取该地址的数据）
	 * @Description:读取起始地址为0x30的内容
	 * @return String 返回条码类型 总共56位数据
	 * @throws
	 */
	public String readBagBarCode() {
		while ((len = hardware.read(mRfidOperation.fd, buf, 64)) > 0)
			;// clear buf
		String strData = "";
		String tmpString = "";
		strData = reaAddr((byte) 0x30);
		if (null == strData) {
			return null;
		}
		tmpString = reaAddr((byte) 0x34);
		if (null != tmpString) {
			strData = strData + tmpString.substring(0, 28);// 0x34地址只需要前14个字节
		} else {
			return null;
		}
		// 05531106803A87822651049C8D214A8E4E0009 160401081215
		return strData;
	}

	/**
	 * 读NFC卡目录索引信息。
	 * 
	 * @return
	 */
	public String readIndexInfo() {
		while ((len = hardware.read(mRfidOperation.fd, buf, 48)) > 0)
			;// clear buf
		String strData = "";
		strData = reaAddr((byte) 0x20);
		return strData;
	}

	/**
	 * 读NFC卡目录袋型号信息。0x0f
	 * 
	 * @return
	 */
	public String readBagTypeInfo() {
		while ((len = hardware.read(mRfidOperation.fd, buf, 48)) > 0)
			;// clear buf
		String strData = "";
		strData = reaAddr((byte) 0x0f);
		return strData == null ? null : strData.substring(0, 2);
	}

	public String readExchangeInfo(byte addr) {
		while ((len = hardware.read(mRfidOperation.fd, buf, 48)) > 0)
			;// clear buf
		String strData = "";
		strData = reaAddr(addr);
		if (null != strData)
			return strData.substring(0, 24);
		else {
			return null;
		}
	}

	/**
	 * 暂时没有用了，该方法 写条码，将19个字节的数据从二维数组中取出，对于nfc卡，本不需要分成两段，因M1卡扇区的原因，为了保持统一。
	 * 条码信息，写在开始地址为0x30的位置
	 * 
	 * @param data
	 *            19个字节的条码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeBarCode(byte[][] data) {
		byte[] tmpData = new byte[4];
		// 因写入数据，每次只能写一个地址，故需要循环写入
		for (int i = 0; i < 16; i++) {
			tmpData[i % 4] = data[0][i];
			if (i % 4 == 3) {
				if (!writeBarcardToNFC(0x14 + (i / 4), tmpData)) {
					return false;// 在循环写入的过程中，如果一次写入失败，即视为失败
				}
			}
		}
		for (int i = 0; i < 13; i++) {
			tmpData[i % 4] = data[1][i];
			if (i % 4 == 3 || i == 12) {
				if (!writeBarcardToNFC(0x18 + (i / 4), tmpData)) {
					return false;
				}
			}
		}
		LogsUtil.d(TAG, "条码写入成功");
		return true;
	}

	/**
	 * 封签事件码信息，写在开始地址为0x30的位置
	 * 
	 * @param data
	 *            30个字节的条码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeBagBarCode(byte[] data) {
		byte[] tmpData = new byte[4];
		// 因写入数据，每次只能写一个地址，故需要循环写入
		for (int i = 0; i < data.length; i++) {
			tmpData[i % 4] = data[i];
			if (i % 4 == 3 || i == data.length - 1) {
				if (!writeBarcardToNFC(0x30 + (i / 4), tmpData)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 将一个四字节的数据，写入到指定地址
	 * 
	 * @param addr
	 *            指定地址
	 * @param data
	 *            四个字节的数据
	 * @return false 写入失败， true 写入成功
	 */
	private boolean writeBarcardToNFC(int addr, byte[] data) {
		// TODO Auto-generated method stub
		writeData[9] = (byte) addr;
		byte buf[] = new byte[36];
		byte[] fifth = data;
		for (int i = 0; i < fifth.length; i++) {
			writeData[10 + i] = fifth[i];
		}
		writeData[writeData.length - 2] = getCheckNumber(writeData.length - 7,
				writeData);
		len = hardware.write(mRfidOperation.fd, writeData);
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (hardware.select(mRfidOperation.fd, 1, 0) == 1) {
			len = hardware.read(mRfidOperation.fd, buf, 36);

			if (len < 1) {
				LogsUtil.d("read error");
			}
			if (checkFirst6Data(buf) && buf[6] == (byte) 0x00
					&& buf[7] == (byte) 0x00 && buf[8] == (byte) 0xff
					&& buf[9] == (byte) 0x03 && buf[10] == (byte) 0xfd
					&& buf[11] == (byte) 0xd5 && buf[12] == (byte) 0x41
					&& buf[13] == (byte) 0x00 && buf[14] == (byte) 0xea
					&& buf[15] == (byte) 0x00) {
				return true;
			} else {
				LogsUtil.d(TAG, "write data error....");// 写入数据错误，可能是认证不成功
				return false;
			}
		} else {
			LogsUtil.d(TAG, "265 no data read");
			return false;
		}
	}

	/**
	 * 写袋ID，将13字节数据写入到NFC卡中。 袋ID 信息，写在开始地址为0x05的位置
	 * 
	 * @param data
	 *            13字节的袋码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeBagID(byte[] data) {
		byte[] tmpData = new byte[4];
		// 因写入数据，每次只能写一个地址，故需要循环写入
		for (int i = 0; i < data.length; i++) {
			tmpData[i % 4] = data[i];
			if (i % 4 == 3 || i == (data.length - 1)) {
				if (!writeBarcardToNFC(0x04 + (i / 4), tmpData)) {
					return false;// 在循环写入的过程中，如果一次写入失败，即视为失败
				}
			}
		}
		LogsUtil.d(TAG, "袋ID写入成功");
		return true;
	}

	/**
	 * 写袋目录索引信息，将12字节数据写入到NFC卡中。
	 * 
	 * @param data
	 *            12字节的袋码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeIndexInfo(byte[] data) {
		byte[] tmpData = new byte[4];
		// 因写入数据，每次只能写一个地址，故需要循环写入
		for (int i = 0; i < data.length; i++) {
			tmpData[i % 4] = data[i];
			if (i % 4 == 3 || i == (data.length - 1)) {
				if (!writeBarcardToNFC(0x20 + (i / 4), tmpData)) {
					return false;// 在循环写入的过程中，如果一次写入失败，即视为失败
				}
			}
		}
		return true;
	}

	/**
	 * 写启用码，将4字节数据写入到NFC卡中。 启用码信息，写在开始地址为0x11的位置
	 * 
	 * @param data
	 *            4字节的 启用码信息
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeEnableCode(byte[] data) {
		if (!writeBarcardToNFC(0x11, data)) {
			return false;
		}
		return true;
	}

	/**
	 * 写交接信息，将12字节(前11字节有效)数据写入到NFC卡中。 袋ID 信息，写在开始地址为0x11的位置
	 * 
	 * @param data
	 *            12字节的交接数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeExangeInfo(byte addr, byte[] data) {
		byte[] tmpData = new byte[4];
		// 因写入数据，每次只能写一个地址，故需要循环写入
		for (int i = 0; i < data.length; i++) {
			tmpData[i % 4] = data[i];
			if (i % 4 == 3 || i == (data.length - 1)) {
				// 在循环写入的过程中，如果一次写入失败，即视为失败
				if (!writeBarcardToNFC(addr + (i / 4), tmpData)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 写TID，将6个字节数据写入到NFC卡中。 袋TID 信息，写在开始地址为0x07的位置
	 * 
	 * @param data
	 *            6字节的袋码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeTid(byte[] data) {
		byte[] tmpData = new byte[4];
		// 因写入数据，每次只能写一个地址，故需要循环写入
		for (int i = 0; i < data.length; i++) {
			tmpData[i % 4] = data[i];
			if (i % 4 == 3 || i == data.length - 1) {
				if (!writeBarcardToNFC(0x07 + (i / 4), tmpData)) {
					return false;// 在循环写入的过程中，如果一次写入失败，即视为失败
				}
			}
		}
		return true;
	}

	/**
	 * 袋标志位 信息，写在开始地址为0x10的位置
	 * 
	 * @param data
	 *            2字节的袋码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeFlag(byte[] data) {
		if (!writeBarcardToNFC(0x10, data)) {
			return false;// 在循环写入的过程中，如果一次写入失败，即视为失败
		}
		return true;
	}

	/**
	 * 密钥模式选择 信息，写在开始地址为0x14的位置
	 * 
	 * @param data
	 *            1字节的袋码数据
	 * @return true 写入成功，false 写入失败
	 */
	public boolean writeMode(byte[] data) {
		if (!writeBarcardToNFC(0x14, data)) {
			return false;// 在循环写入的过程中，如果一次写入失败，即视为失败
		}
		return true;
	}

	/**
	 * 寻卡操作，每次写数据时都需要先寻卡
	 * 
	 * @return
	 */
	public byte[] activatecard() {
		if (mRfidOperation == null) {
			mRfidOperation = RFIDOperation.getInstance();
		}
		byte tmp[] = mRfidOperation.activatecard();
		if (tmp == null) {
			return null;
		} else if (tmp.length == 4) {
			LogsUtil.d(TAG, "出现不同锁片");
			return null;// 表示又是M1卡
		} else {
			mUid = tmp;
			return tmp;
		}

	}

	/**
	 * 关闭RF后，再次寻卡也不需要唤醒操作，只有在断电的情况下，关闭后才需要再一次唤醒
	 * 
	 * @return false 正常关闭RF不成功
	 */
	public boolean closeRf() {
		int n = 0;
		while (n++ < 10) {
			if (closeRf()) {// 正常关闭读卡器
				return true;
			}
		}
		return false;
	}

	public void readDataOnThread(final int sa, final NFCReadDataCallBack callBack) {
		if (null == callBack) {
			return;
		}
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				while((System.currentTimeMillis() - startTime) < 5000) {
					byte[] data = reaAddrToByte(sa);
					if (null != data) {
						callBack.onReadSuccess(data);
						return;
					}
				}// end while
				callBack.onReadFailed(0);
			}
		});
	}
	public interface NFCReadDataCallBack {
		void onReadSuccess(byte[] data);
		void onReadFailed(int cause);
	}
}
