package com.fanfull.hardwareAction;

import java.util.Arrays;

import android.os.SystemClock;

import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.hardware.Hardware;

public class UHFOperation {
	private final static String TAG = UHFOperation.class.getSimpleName();

	private static int fd = -1;
	private final static String CHOOSED_SERIAL = "/dev/s3c2410_serial2";
	private final static int CHOOSED_BUAD = 115200;

	/**
	 * 获取当前设备发射功率
	 */
	private final static byte[] CMD_GET_POWER = new byte[] { (byte) 0xA5,
			(byte) 0x5A, 0x00, 0x08, (byte) 0x12, 0x1A, 0x0d, 0x0A };
	/**
	 * 单次寻标签 命令; [5] = 帧类型 = 0x80 [6,7] timeOut, 时间到或寻到标签 回传应答帧
	 */
	private final static byte[] CMD_READ_ONE = new byte[] { (byte) 0xa5,
			(byte) 0x5a, 0x00, 0x0a, (byte) 0x80, 0x00, 0x64, (byte) 0xee,
			0x0d, 0x0a };
	/**
	 * 快速读取 TID
	 */
	private final static byte[] CMD_FAST_READ_TID = new byte[] { (byte) 0xA5,
			(byte) 0x5A, 0x00, 0x0C, (byte) 0x8E, 0x00, 0x00, 0x00, 0x12, 0x00,
			0x0D, 0x0A };
	// a55a000c8e00000012000d0a
	// A55A000A800064EE0D0A
	// byte[] read_one = new
	// byte[]{(byte)0xa5,(byte)0x5a,0x00,0x0a,(byte)0x80,0x00,0x64,(byte)0xee,0x0d,0x0a};
	public byte[] read_more = new byte[] { (byte) 0xa5, (byte) 0x5a, 0x00,
			0x0a, (byte) 0x82, 0x00, 0x00, (byte) 0x88, 0x0d, 0x0a };
	// a55a00088c840d0a
	public byte[] stop_read_more = new byte[] { (byte) 0xa5, (byte) 0x5a, 0x00,
			0x08, (byte) 0x8c, (byte) 0x84, 0x0d, 0x0a };
	public byte[] read_more_time = new byte[] { (byte) 0xa5, (byte) 0x5a, 0x00,
			0x0a, (byte) 0x3c, 0x00, 0x00, 0x36, 0x0d, 0x0a };

	public byte[] buf = new byte[70];
	public byte[] newbuf = new byte[25];
	public final byte[] mEPC = new byte[12];
	public byte[] read_data = new byte[100];

	public byte[] mWriteData = new byte[46];
	public byte[] readTID = new byte[34];

	private int len;
	public int count = 0;// 记录读卡数
	public int stop_more_flag = 0;// 停止连续寻卡标志

	public Hardware hardware;
	private static UHFOperation mUhf = null;

	public boolean isFisrt = true;

	public UHFOperation() {
		// 启动模块
		hardware = Hardware.getInstance();
	}

	public static UHFOperation getInstance() {
		if (null == mUhf) {
			mUhf = new UHFOperation();
		}
		mUhf.open();
		return mUhf;
	}

	public static UHFOperation getCloseInstance() {
		if (null == mUhf) {
			mUhf = new UHFOperation();
		}
		return mUhf;
	}

	/**
	 * 
	 * @Title: stopMore
	 * @Description: 停止多次扫描
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void stopMore() {
		while ((len = hardware.read(fd, buf, 25)) > 0)
			;
		hardware.write(fd, stop_read_more);
		stop_more_flag = 1;
		LogsUtil.d(TAG, "stopMore() count" + count);
	}

	/**
	 * 
	 * @Title: findMore
	 * @Description: 多次扫描
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public boolean findMore() {
		boolean flag = false;
		while ((len = hardware.read(fd, buf, 25)) > 0)
			;// clear buf
		hardware.write(fd, read_more_time);// 设置连续寻卡等待时间
		if (hardware.select(fd, 3, 0) == 1) {
			len = hardware.read(fd, buf, 25);
			if (len < 1) {
				LogsUtil.d("read error");
			} else {
				LogsUtil.d(TAG, ArrayUtils.bytes2HexString(buf));
				flag = true;
			}
		}
		return flag;

	}

	/**
	 * 
	 * @Title: findOne
	 * @Description: 一次扫描EPC
	 * @param @return 设定文件
	 * @return Boolean TRUE 扫描成功；FALSE没有扫描
	 * @throws
	 */
	public boolean findOne() {
		boolean reVal = false;
		Arrays.fill(mEPC, (byte) 0x0);
		byte[] buf = new byte[32];
		
		while (hardware.read(fd, buf, buf.length) > 0) {
			;// clear buf
		}
		
		int len = hardware.write(fd, CMD_READ_ONE);
		if (len < 1) {
			LogsUtil.d(TAG, "write findoneCMD error len < 1 fd=" + fd);
			if (isFisrt) {
				mUhf.open();
				isFisrt = true;
			}
			return false;
		}
		SystemClock.sleep(50);
		len = 0;
		if (hardware.select(fd, 1, 0) == 1) {
			len = hardware.read(fd, buf, buf.length);
		}
		if (len != 25) {
			LogsUtil.d(TAG, "findOne read len != 25 fd=" + fd);
			return false;
		}

		if (buf[4] == (byte) 0x81) {
			for (int i = 0; i < mEPC.length; i++) {
				mEPC[i] = buf[i + 7];
			}

			String debug = ArrayUtils.bytes2HexString(mEPC);
			LogsUtil.d(TAG,
					"findOne() EPC: " + debug + "  length: " + debug.length());
			reVal = true;
		}

		return reVal;
	}

	public boolean writeEpc(byte data[]) {
		byte[] write_EPC = new byte[] { (byte) 0xa5, (byte) 0x5a, 0x00, 0x2e,
				(byte) 0x86, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x20, 0x00,
				0x60, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, (byte) 0x01, 0x00, 0x02, 0x00, 0x06, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, (byte) 0x06, 0x0d, 0x0a };
		int i = 0;
		// TODO Auto-generated method stub
		for (i = 0; i < 12; i++) {
			write_EPC[i + 14] = mEPC[i];
		}

		for (i = 31; i < 43; i++) {
			write_EPC[i] = data[i - 31];
		}
		write_EPC[43] = 0x00;
		for (i = 2; i < 43; i++) {
			write_EPC[43] ^= write_EPC[i];
		}
		hardware.write(fd, write_EPC);
		SystemClock.sleep(50);
		if (hardware.select(fd, 1, 0) == 1) {
			len = hardware.read(fd, buf, 25);
			if (len < 1) {
				System.out.println("read error");
			}
			// A55A000A8700018C0D0A498EB10009CB0D0A00000000000000000
			System.out.println(ArrayUtils.bytes2HexString(buf));
			if (buf[4] == (byte) 0x87 && buf[5] == 0x01) {
				LogsUtil.d("写入EPC成功！\n");
				return true;
			} else {
				LogsUtil.d("写入EPC失败！\n");
				return false;
			}
		} else {
			LogsUtil.d("写入EPC失败！\n");
			return false;
		}
	}

	/**
	 * @description 快速获取 TID区 数据, TID区总大小 : 24字节
	 * @param sa
	 *            获取数据 起始地址, 单位 字
	 * @param dl
	 *            获取数据 长度 , 单位 字节
	 * @return
	 */
	public byte[] fastReadTID(int sa, int dl) {
		byte[] reVal = null;

		// 起始地址, 单位为 字 // 0x02
		if (0xFF < sa) {
			CMD_FAST_READ_TID[6] = (byte) (sa >> 8);
		} else {
			CMD_FAST_READ_TID[6] = (byte) 0x00;
		}
		CMD_FAST_READ_TID[5] = (byte) sa;

		// 获取数据长度, 单位为 字
		int wordLen = dl / 2;
		if (0xFF < wordLen) {
			CMD_FAST_READ_TID[7] = (byte) (wordLen >> 8);
		} else {
			CMD_FAST_READ_TID[7] = (byte) 0x00;
		}
		CMD_FAST_READ_TID[8] = (byte) wordLen;

		// 校验位
		CMD_FAST_READ_TID[9] = 0;
		for (int i = 2; i < CMD_FAST_READ_TID.length - 3; i++) {
			CMD_FAST_READ_TID[9] ^= CMD_FAST_READ_TID[i];
		}
		LogsUtil.d(ArrayUtils.bytes2HexString(CMD_FAST_READ_TID));

		while ((hardware.read(fd, buf, 25)) > 0) {
			;// clear buf
		}
		hardware.write(fd, CMD_FAST_READ_TID);
		while ((hardware.read(fd, buf, 25)) > 0) {
			;// clear buf
		}
		if (hardware.select(fd, 3, 0) == 1) {
			len = hardware.read(fd, buf, 25);
		}
		if (len < 1) {
			LogsUtil.d(TAG, "error len < 1");
			return reVal;
		} else {

			if (buf[4] == (byte) 0x8F && buf[5] == 0x01) {
				for (int i = 0; i < len; i++) {
					newbuf[i] = buf[i];
				}
				reVal = new byte[dl];
				if (len >= 13) {
					for (int i = 0; i < dl; i++) {
						reVal[i] = buf[i + 9];
					}
					String debug = ArrayUtils.bytes2HexString(reVal);
					LogsUtil.d(TAG, "fastReadTID: " + debug + " length:"
							+ debug.length());
				}
			}
			LogsUtil.d(TAG, "buf[4] = " + buf[4] + "  buf[5] = " + buf[5]);
		}
		return reVal;
	}

	/**
	 * 
	 * @Title: readTID
	 * @Description: 读取厂家码
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public byte[] readTID() {
		hardware.setGPIO(0, 2);
		// 帧头
		readTID[0] = (byte) 0xa5;
		readTID[1] = (byte) 0x5a;
		// 帧长度
		readTID[2] = (byte) 0x00;
		readTID[3] = (byte) 0x1F;
		// 帧类型
		readTID[4] = (byte) 0x84;
		// 密码
		readTID[5] = (byte) 0x00;
		readTID[6] = (byte) 0x00;
		readTID[7] = (byte) 0x00;
		readTID[8] = (byte) 0x00;
		// PC
		readTID[9] = 0x30;
		readTID[10] = 0x00;
		// 要读的EPC号
		for (int i = 0; i < mEPC.length; i++) {
			readTID[11 + i] = mEPC[i];
		}
		// MB
		readTID[23] = 0x02;
		// 写入起始位置
		readTID[24] = 0x00;
		readTID[25] = 0x02;
		// 数据大小
		readTID[26] = 0x00;
		readTID[27] = 0x04;
		// 校验
		readTID[readTID[2] * 256 + readTID[3] - 3] = 0x00;
		for (int i = 2; i < readTID[2] * 256 + readTID[3] - 3; i++) {
			readTID[readTID[2] * 256 + readTID[3] - 3] ^= readTID[i];
		}
		// 帧尾
		readTID[29] = 0x0D;
		readTID[30] = 0x0A;
		// 写入数据
		LogsUtil.d(ArrayUtils.bytes2HexString(readTID));
		hardware.write(fd, readTID);
		if (hardware.select(fd, 1, 0) == 1) {
			len = hardware.read(fd, buf, 25);
			if (len < 1) {
				LogsUtil.d("read error");
			}
			LogsUtil.d(ArrayUtils.bytes2HexString(buf));
			if (buf[4] == (byte) 0x85 && buf[5] == 0x01) {
				LogsUtil.d("readTID success\n");
				hardware.setGPIO(1, 2);
				byte[] tid = new byte[8];
				for (int i = 0; i < tid.length; i++) {
					tid[i] = buf[9 + i];
				}
				return tid;
			} else {
				LogsUtil.d("readTID error\n");
				hardware.setGPIO(1, 2);
				return null;
			}
		}
		hardware.setGPIO(1, 2);
		return null;
	}

	/**
	 * @param EPC
	 *            过滤 数据,如果不需要过滤,请传入 长度为0的数组
	 * @param data
	 *            需要写入的 数据
	 * @param mmb
	 *            为启动过滤操作的 bank号， 0x01 表示 EPC， 0x02 表示 TID， 0x03 表示USR，其他值为非法值
	 *            //01
	 * @param msa
	 *            MSA启动过滤操作的起始地址, 单位为 bit // 0x20
	 * @param mb
	 *            memory bank，用户需要写入的数据的 bank号 // 代号与 mmb 一致
	 * @param sa
	 *            写入起始位置, 单位为 字 // 0x02
	 * @return
	 */
	// if (mUHFOp.writeUHF(mUHFOp.mEPC, data, 1, 0x20, 3, 0x02)) {
	public boolean writeUHF(byte[] EPC, byte[] data, int mmb, int msa, int mb,
			int sa) {

		LogsUtil.d(
				TAG,
				"writeUHF() " + mb + " guoLv: "
						+ ArrayUtils.bytes2HexString(EPC));
		// 总长度 = 22其他信息 + 过滤数据 + 50写入数据
		int totalLen = 22 + EPC.length + data.length;

		byte[] writeData = new byte[totalLen];

		// hardware.setGPIO(0, 2);
		// 帧头
		writeData[0] = (byte) 0xa5;
		writeData[1] = (byte) 0x5a;

		// 帧长度
		if (0xff < totalLen) {
			writeData[2] = (byte) (totalLen >> 8);
		} else {
			writeData[2] = (byte) 0x00;
		}
		writeData[3] = (byte) totalLen;

		// 帧类型
		writeData[4] = (byte) 0x86;
		// 密码ap
		writeData[5] = (byte) 0x00;
		writeData[6] = (byte) 0x00;
		writeData[7] = (byte) 0x00;
		writeData[8] = (byte) 0x00;
		// MMB 为启动过滤操作的 bank 号， 0x01 表示 EPC， 0x02 表示 TID， 0x03 表示
		// USR，其他值为非法值；//01
		writeData[9] = (byte) mmb; // 01

		// MSA启动过滤操作的起始地址, 单位为 bit, // 0x20
		if (0xff < msa) {
			writeData[10] = (byte) (msa >> 8);
		} else {
			writeData[10] = (byte) 0x00;
		}
		writeData[11] = (byte) msa;

		// MDL过滤的数据长度, 单位为 bit
		int bitLen = EPC.length * 8;
		if (0xff < bitLen) {
			writeData[12] = (byte) (bitLen >> 8);
		} else {
			writeData[12] = (byte) 0x00;
		}
		writeData[13] = (byte) bitLen;
		// A55A00488600000000010020000003000000193038373130313030313030303030363037303131353037303731363230323931303030313030E28011052000409D000000FF850D0A
		// 要写的EPC号
		int epcLen = EPC.length;
		for (int i = 0; i < epcLen; i++) {// 12
			writeData[14 + i] = EPC[i];
		}

		// MB:memory bank，用户需要写入的数据的 bank 号；
		writeData[14 + epcLen] = (byte) mb; // 01

		// 写入起始位置, 单位为 字 // 0x02
		if (0xff < sa) {
			writeData[15 + epcLen] = (byte) (sa >> 8);
		} else {
			writeData[15 + epcLen] = (byte) 0x00;
		}
		writeData[16 + epcLen] = (byte) sa;

		// 数据大小, 单位为 字
		int wordLen = data.length / 2;
		if (0xff < totalLen) {
			writeData[17 + epcLen] = (byte) (wordLen >> 8);
		} else {
			writeData[17 + epcLen] = (byte) 0x00;
		}
		writeData[18 + epcLen] = (byte) wordLen;

		// 写入的数据 12位
		int dataLen = data.length;
		for (int i = 0; i < dataLen; i++) {// data.length
			writeData[19 + epcLen + i] = data[i];
		}
		// 校验,将除了 帧头 和 帧尾 之外的所有数据进行异或
		int checkIndex = 19 + epcLen + dataLen; // 校验位
		// LogsUtil.d(checkIndex);
		writeData[checkIndex] = 0x00;
		for (int i = 2; i < checkIndex; i++) {
			writeData[checkIndex] ^= writeData[i];
		}

		// 帧尾
		writeData[checkIndex + 1] = 0x0D;
		writeData[checkIndex + 2] = 0x0A;

		while (1 < (len = hardware.read(fd, buf, buf.length))) {
			;// clear buff
		}
		boolean reVal = false;
		hardware.write(fd, writeData);
		if (hardware.select(fd, 1, 0) == 1) {
			SystemClock.sleep(50);// 此行代码 9月30日 添加，测试是否可提高EPC写入成功率
			len = hardware.read(fd, buf, buf.length);
			// uhf write
			// buf:A55A000A8700018C0D0A801105200044890F0D0A2101360D0A0000000
			if (len < 1) {
				reVal = false;
				LogsUtil.s("writeEPC false len < 1");// NewEPCId:048711006068B5B88A
			} else if (buf[4] == (byte) 0x87 && buf[5] == 0x01) {// 0x87 == -121
				LogsUtil.d(TAG, "写入成功");// 写入失败 : buf[4] == -121 buf[5] == 0
				reVal = true;
			} else {
				LogsUtil.s("写入失败 : buf[4] == " + buf[4] + " buf[5] == "
						+ buf[5]);
			}
		} else {
			LogsUtil.s("hardware.select");
		}

		return reVal;
	}

	/**
	 * @param EPC
	 *            过滤数据
	 * @param mmb
	 *            过滤的区
	 * @param msa
	 *            过滤的起始地址
	 * @param mb
	 *            读取数据 的 区,0x01 表示 EPC， 0x02 表示 TID， 0x03 表示user
	 * @param sa
	 *            读取 数据的 起始地址, 单位 字
	 * @param readDataLen
	 *            读取 数据 的长度, 单位 字节
	 * @return
	 */
	// if (mUHFOp.writeUHF(mUHFOp.mEPC, data, 1, 0x20, 3, 0x02)) {
	// public boolean writeUHF(byte[] EPC, byte[] data, int mmb, int msa, int
	// mb,
	// int sa) {
	public byte[] readUHF(byte[] EPC, int mmb, int msa, int mb, int sa,
			int readDataLen) {
		if (null == EPC) {
			EPC = new byte[0];
		}
		LogsUtil.d(TAG, "readUHF 过滤:" + ArrayUtils.bytes2HexString(EPC));
		int totalLen = 22 + EPC.length;

		byte[] writeData = new byte[totalLen];

		// 帧头
		writeData[0] = (byte) 0xA5;
		writeData[1] = (byte) 0x5A;
		// 帧长度
		if (0xff < totalLen) {
			writeData[2] = (byte) (totalLen >> 8);
		} else {
			writeData[2] = (byte) 0x00;
		}
		writeData[3] = (byte) totalLen;
		// 帧类型
		writeData[4] = (byte) 0x84;
		// 密码
		writeData[5] = (byte) 0x00;
		writeData[6] = (byte) 0x00;
		writeData[7] = (byte) 0x00;
		writeData[8] = (byte) 0x00;

		// MMB 为启动过滤操作的 bank 号， 0x01 表示 EPC， 0x02 表示 TID， 0x03 表示
		// USR，其他值为非法值；//01
		writeData[9] = (byte) mmb;

		// MSA启动过滤操作的起始地址 // 20
		if (0xff < msa) {
			writeData[10] = (byte) (msa >> 8);
		} else {
			writeData[10] = (byte) 0x00;
		}
		writeData[11] = (byte) msa;

		// MDL过滤的数据长度, 单位为 bit // 60
		int bitLen = EPC.length * 8;
		if (0xff < totalLen) {
			writeData[12] = (byte) (bitLen >> 8);
		} else {
			writeData[12] = (byte) 0x00;
		}
		writeData[13] = (byte) bitLen;

		// 要读的EPC号
		int epcLen = EPC.length;
		for (int i = 0; i < epcLen; i++) {// 12
			writeData[14 + i] = EPC[i];
		}

		// MB // 0x02
		writeData[epcLen + 14] = (byte) mb;

		// 写入起始位置
		if (0xff < sa) {
			writeData[15 + epcLen] = (byte) (sa >> 8);
		} else {
			writeData[15 + epcLen] = (byte) 0x00;
		}
		writeData[16 + epcLen] = (byte) sa;

		// 读取数据的长度,单位 字
		readDataLen >>= 1;// 相当于 readDataLen / 2, 把 字节长度 转为 字长度
		if (0xff < readDataLen) {
			writeData[17 + epcLen] = (byte) (readDataLen >> 8);
		} else {
			writeData[17 + epcLen] = (byte) 0x00;
		}
		writeData[18 + epcLen] = (byte) readDataLen;

		// writeData[epcLen + 17] = 0x00;
		// writeData[epcLen + 18] = 0x06;

		writeData[epcLen + 19] = (byte) 0x00; // 校验位
		for (int i = 2; i < totalLen - 3; i++) {
			writeData[epcLen + 19] ^= writeData[i];
		}
		// 帧尾
		writeData[epcLen + 20] = 0x0D;
		writeData[epcLen + 21] = 0x0A;

		while ((len = hardware.read(fd, buf, 25)) > 0) {
			;// clear buf
		}
		hardware.write(fd, writeData);
		SystemClock.sleep(40);

		if (hardware.select(fd, 1, 0) == 1) {
			len = hardware.read(fd, buf, buf.length);
			if (len < 1) {
				LogsUtil.s("readUHF error : len < 1");
			}

			if (buf[4] == (byte) 0x85 && buf[5] == 0x01) {

				// hardware.setGPIO(1, 2);
				byte[] data = new byte[readDataLen << 1];
				for (int i = 0; i < data.length; i++) {
					data[i] = buf[9 + i];
				}
				return data;
			} else {
				LogsUtil.s("readUHF error");
				LogsUtil.s(" buf[4]:" + buf[4] + " buf[5]:" + buf[5]);
				// hardware.setGPIO(1, 2);
				return null;
			}
		}
		return null;
	}
	
	public byte[] readUHF1(byte[] EPC, int mmb, int msa, int mb, int sa,
			int readDataLen) {
		if (null == EPC) {
			EPC = new byte[0];
		}
		LogsUtil.d(TAG, "readUHF 过滤:" + ArrayUtils.bytes2HexString(EPC));
		int totalLen = 22 + EPC.length;

		byte[] writeData = new byte[totalLen];

		// 帧头
		writeData[0] = (byte) 0xA5;
		writeData[1] = (byte) 0x5A;
		// 帧长度
		if (0xff < totalLen) {
			writeData[2] = (byte) (totalLen >> 8);
		} else {
			writeData[2] = (byte) 0x00;
		}
		writeData[3] = (byte) totalLen;
		// 帧类型
		writeData[4] = (byte) 0x84;
		// 密码
		writeData[5] = (byte) 0x00;
		writeData[6] = (byte) 0x00;
		writeData[7] = (byte) 0x00;
		writeData[8] = (byte) 0x00;

		// MMB 为启动过滤操作的 bank 号， 0x01 表示 EPC， 0x02 表示 TID， 0x03 表示
		// USR，其他值为非法值；//01
		writeData[9] = (byte) mmb;

		// MSA启动过滤操作的起始地址, 单位为 bit // 20
		int bitMsa = msa << 4;
		if (0xff < bitMsa) {
			writeData[10] = (byte) (bitMsa >> 8);
		} else {
			writeData[10] = (byte) 0x00;
		}
		writeData[11] = (byte) bitMsa;

		// MDL过滤的数据长度, 单位为 bit // 60
		int bitLen = EPC.length << 3;
		if (0xff < bitLen) {
			writeData[12] = (byte) (bitLen >> 8);
		} else {
			writeData[12] = (byte) 0x00;
		}
		writeData[13] = (byte) bitLen;

		// 要读的EPC号
		int epcLen = EPC.length;
		for (int i = 0; i < epcLen; i++) {// 12
			writeData[14 + i] = EPC[i];
		}

		// MB // 0x02
		writeData[epcLen + 14] = (byte) mb;

		// 写入起始位置
		if (0xff < sa) {
			writeData[15 + epcLen] = (byte) (sa >> 8);
		} else {
			writeData[15 + epcLen] = (byte) 0x00;
		}
		writeData[16 + epcLen] = (byte) sa;

		// 读取数据的长度,单位 字
		readDataLen >>= 1;// 相当于 readDataLen / 2, 把 字节长度 转为 字长度
		if (0xff < readDataLen) {
			writeData[17 + epcLen] = (byte) (readDataLen >> 8);
		} else {
			writeData[17 + epcLen] = (byte) 0x00;
		}
		writeData[18 + epcLen] = (byte) readDataLen;

		// writeData[epcLen + 17] = 0x00;
		// writeData[epcLen + 18] = 0x06;

		writeData[epcLen + 19] = (byte) 0x00; // 校验位
		for (int i = 2; i < totalLen - 3; i++) {
			writeData[epcLen + 19] ^= writeData[i];
		}
		// 帧尾
		writeData[epcLen + 20] = 0x0D;
		writeData[epcLen + 21] = 0x0A;

		while ((len = hardware.read(fd, buf, 25)) > 0) {
			;// clear buf
		}
		hardware.write(fd, writeData);
		SystemClock.sleep(40);

		if (hardware.select(fd, 1, 0) == 1) {
			len = hardware.read(fd, buf, buf.length);
			if (len < 1) {
				LogsUtil.s("readUHF error : len < 1");
			}

			if (buf[4] == (byte) 0x85 && buf[5] == 0x01) {

				// hardware.setGPIO(1, 2);
				byte[] data = new byte[readDataLen << 1];
				for (int i = 0; i < data.length; i++) {
					data[i] = buf[9 + i];
				}
				return data;
			} else {
				LogsUtil.s("readUHF error");
				LogsUtil.s(" buf[4]:" + buf[4] + " buf[5]:" + buf[5]);
				// hardware.setGPIO(1, 2);
				return null;
			}
		}

		return null;
	}


	public byte[] readTIDNogl(byte[] data) {
		// byte[] tid = readUHF(new byte[0], 1, 0x20, 2, 0x00, 12);
		// return readUHF(new byte[0], 1, 0x20, 2, 0, 12);
		return readUHF(data, 1, 0x20, 2, 0x03, 6);
	}
	
	public byte[] readTIDNogl() {
		//	byte[] tid = readUHF(new byte[0], 1, 0x20, 2, 0x00, 12);
			// return readUHF(new byte[0], 1, 0x20, 2, 0, 12);
		return readUHF(new byte[0], 1, 0x20, 2, 0x03, 6);
	}

	/**
	 * 读封签事件码信息
	 */
	public byte[] readCoverInfo() {
		return readUHF(new byte[0], 1, 0x20, 3, 0x00, 30);
	}

	/**
	 * 读目录索引
	 */
	public byte[] readIndex() {
		return readUHF(new byte[0], 1, 0x20, 3, 0x21, 2);
	}

	/**
	 * 读交接信息
	 */
	public byte[] readExageInfo() {
		return readUHF(new byte[0], 1, 0x20, 3, 0x21, 12);
	}

	/**
	 * 
	 * @Title: readTID
	 * @Description: 指向性读取厂家码 // 得到UHF的厂家码8个字节
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	/**
	 * @param EPC
	 * @return byte[34]
	 * @Description: 指向性读取厂家码 得到UHF的厂家码 12 个字节
	 */
	public byte[] readTID(byte[] EPC) {
		return readUHF(EPC, 1, 0x20, 2, 0x03, 6);
	}

	/**
	 * @param readPower
	 *            读 功率.范围:5-30
	 * @param writePower
	 *            写 功率.范围:5-30
	 * @param id
	 *            天线号
	 * @param save
	 *            本次设置是否保存 , 0 = 不保存
	 * @param open
	 *            是否闭环, 非0 = 闭环, 默认0
	 * @return 设置成功 返回 true
	 */
	public boolean setPower(int readPower, int writePower, int id, int save,
			int open) {

		if (readPower < 0 || writePower < 0 || id < 0) {
			return false;
		}

		byte[] data = new byte[14]; // 设置功率命令 14个 字节
		/* 固定格式 */
		data[0] = (byte) 0xA5; // 帧头 2字节
		data[1] = (byte) 0x5A;
		data[2] = (byte) 0x00; // 帧长度 2字节
		data[3] = (byte) 0x0E;
		data[4] = (byte) 0x10; // 帧类型
		data[12] = (byte) 0x0D; // 帧尾 2字节
		data[13] = (byte) 0x0A;

		/* 参数设置 */
		// 状态:bit7-bit2 保留，
		// bit0 为 0 表示开环状态（默认使用的状态），bit0 为 1 表示闭环状态
		// bit1 为 0 表示当前设置在断电后会丢失， bit1 为 1 表示当前设置在掉电后会保存，下次上电默认功率值为该设置值。
		if (0 != save) {
			save = 1;
		}
		if (0 != open) {
			open = 1;
		}
		data[5] = (byte) ((save << 1) + open);
		data[6] = (byte) id; // 天线号

		// 限制 读功率范围在 [5,30]内
		if (30 < readPower) {
			readPower = 30;
		} else if (readPower < 5) {
			readPower = 5;
		}
		// 读功率,2字节
		readPower *= 100; // 功率要先乘以100,协议如此规定
		data[7] = (byte) (readPower >> 8);
		data[8] = (byte) readPower;

		// 限制 写功率范围在 [5,30]内
		if (30 < writePower) {
			writePower = 30;
		} else if (writePower < 5) {
			writePower = 5;
		}
		// 写功率,2字节
		writePower *= 100; // 功率要先乘以100,协议如此规定
		data[9] = (byte) (writePower >> 8);
		data[10] = (byte) writePower;

		// 校验位
		data[11] = (byte) 0x00;
		for (int i = 2; i < 11; i++) {
			data[11] ^= data[i];
		}

		int len = 0;
		while ((len = hardware.read(fd, buf, 25)) > 0) {
			;// clear buf
		}
		len = hardware.write(fd, data);

		while ((len = hardware.read(fd, buf, 25)) > 0) {
			;// clear buf
		}

		if (hardware.select(fd, 3, 0) == 1) {
			SystemClock.sleep(100);

			len = hardware.read(fd, buf, 9);
			LogsUtil.d(TAG, "setPower() len:" + len);
			if (len < 1) {
				LogsUtil.s("setpower read error");
			} else if (buf[5] == 0x01 && buf[4] == (byte) 0x11) {
				LogsUtil.d(TAG, "功率设置成功: readPower = " + readPower / 100
						+ "writePower = " + writePower / 100 + ", buf[5] = "
						+ buf[5] + ",buf[4] = " + buf[4]);
				return true;
			} else {
				LogsUtil.d(TAG, "功率设置失败: readPower = " + readPower / 100
						+ "writePower = " + writePower / 100 + " buf[5] = "
						+ buf[5] + ",buf[4] = " + buf[4]);
			}
		}
		return false;
	}

	/**
	 * @param readPower
	 *            读 功率.范围:5-30
	 * @param writePower
	 *            写 功率.范围:5-30
	 * @return 设置成功 返回 true
	 */
	public boolean setPower(int readPower, int writePower) {
		return setPower(readPower, writePower, 1, 1, 0);
	}

	/**
	 * @param dwPower
	 *            第0个元素存 读功率, 第1个元素存 写功率
	 * @description 获取 超高频 读写 功率, 保存在 dw_power 中;
	 * 
	 */
	public boolean getPower(int[] dwPower) {
		boolean reVal = false;
		if (null == dwPower || dwPower.length < 2) {
			return reVal;
		}

		int len;
		while (0 < (len = hardware.read(fd, buf, 25))) {
			;// clear buf
		}

		hardware.write(fd, CMD_GET_POWER);

		// 帧类型 buf[4] 0x13
		// 开闭环 buf[5] 0 = 开环, 1 = 闭环

		// 天线号 buf[6]
		// 读功率 buf[7,8]
		// 写功率 buf[9,10]

		// 天线号 buf[11]
		// 读功率 buf[12,13]
		// 写功率 buf[14,15]

		if (hardware.select(fd, 1, 0) == 1) {
			buf[5] = 0x00;
			len = hardware.read(fd, buf, 20);
			if (14 <= len && buf[4] == (byte) 0x13) {
				int h = 0;
				int l = 0;

				// 读 功率
				h = (buf[7] & 0xFF) << 8;
				l = buf[8] & 0xFF;
				dwPower[0] = (h | l) / 100;

				// 写 功率
				h = (buf[9] & 0xFF) << 8;
				l = buf[10] & 0xFF;
				dwPower[1] = (h | l) / 100;

				reVal = true;
				// for (int i = 0; i < len; i++) {
				// LogsUtil.d(buf[i] + ",");
				// }
			}
		}
		return reVal;
	}

	/**
	 * 
	 * @Title: close
	 * @Description: 退出
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void close() {
		hardware.close(fd);
		hardware.setGPIO(1, 3);
		hardware.setGPIO(1, 2);
		hardware.closeGPIO();
		fd = -1;
		LogsUtil.d(TAG, "UHF已关闭");
	}

	public void open() {
		if (isOpen()) {
			return;
		}
		int n = 0;
		hardware.openGPIO();
		hardware.setGPIO(0, 2);
		hardware.setGPIO(0, 3);
		SystemClock.sleep(500);
		while (n < 10 && fd == -1) {
			fd = hardware.openSerialPort(CHOOSED_SERIAL, CHOOSED_BUAD, 8, 1);
			LogsUtil.d(TAG, "uhf fd= " + fd);
		}

	}
	public boolean isOpen() {
		return fd != -1;
	}
	
	public void readUHFInThread(final byte[] EPC, final int mmb, final int msa, final int mb, final int sa,
			final int readDataLen, final long runTime, final UHFReadDataListener listener) {
		if (null == listener) {
			return;
		}
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();
				while ((System.currentTimeMillis() - time) < runTime) {
					byte[] bs = readUHF(EPC, mmb, msa, mb, sa, readDataLen);
					if (null != bs) {
						if (null != listener) {
							listener.onReadSuccess(bs);
						}
						return;
					}
				}// end while
				if (null != listener) {
					listener.onReadFailure(0);
				}
				
			}
		});
		
	}
	interface UHFReadDataListener {
		void onReadSuccess(byte[] uhfData);
		void onReadFailure(int cusec);
	}
}
