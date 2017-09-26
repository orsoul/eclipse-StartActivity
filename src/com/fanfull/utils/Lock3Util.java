package com.fanfull.utils;

import com.fanfull.activity.setting.SettingPowerActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.op.RFIDOperation;
import com.fanfull.op.UHFOperation;

/**
 * 封袋、出入库、开袋 会用到的一些方法。
 */
public class Lock3Util {
	/**
	 * 十套代表F1-->F4的标志 和单片机A版本对接的，请勿修改
	 */
	public static final byte[] FLAG_DATA = new byte[] { (byte) 0x23,
			(byte) 0x5f, (byte) 0x8e, (byte) 0x41, (byte) 0x4d, (byte) 0x8c,
			(byte) 0x3d, (byte) 0x6a, (byte) 0x23, (byte) 0x9c, (byte) 0x95,
			(byte) 0x3c, (byte) 0x4b, (byte) 0x11, (byte) 0x73, (byte) 0x1c,
			(byte) 0x3e, (byte) 0x22, (byte) 0x49, (byte) 0x83, (byte) 0x36,
			(byte) 0x47, (byte) 0x88, (byte) 0x26, (byte) 0x32, (byte) 0x28,
			(byte) 0x3d, (byte) 0x6f, (byte) 0x78, (byte) 0x7a, (byte) 0x99,
			(byte) 0x2d, (byte) 0x6c, (byte) 0x24, (byte) 0xa3, (byte) 0x8c,
			(byte) 0x7f, (byte) 0x9d, (byte) 0x36, (byte) 0xb2, (byte) 0x25,
			(byte) 0x39, (byte) 0x48, (byte) 0x76, (byte) 0xea, (byte) 0x2c,
			(byte) 0x36, (byte) 0x47, (byte) 0x79, (byte) 0x29, };
	/** 启用码 4byte：未启用 00000000 */
	public static final byte[] ENABLE_CODE_DISABLE = new byte[4];
	/** 启用码 4byte：已启用 FFDDFFEE */
	public static final byte[] ENABLE_CODE_ENABLE = new byte[] { (byte) 0xFF,
			(byte) 0xDD, (byte) 0xFF, (byte) 0xEE, };
	/** 启用码 4byte：已注销 EEEEEEEE注销 */
	public static final byte[] ENABLE_CODE_DISENABLE = new byte[] {
			(byte) 0xEE, (byte) 0xEE, (byte) 0xEE, (byte) 0xEE, };

	/**
	 * @param isCover
	 *            true 按出封袋的功率进行设置； false 按入库的功率进行设置
	 * @return 设置成功 返回 大于0的int
	 */
	public static int setCoverPower(boolean isCover) {
		int rPower;
		int wPower;
		if (isCover) {
			// 封袋功率
			rPower = SPUtils.getInt(MyContexts.KEY_POWER_READ_COVER,
					SettingPowerActivity.POWER_READ_COVER);
			wPower = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_COVER,
					SettingPowerActivity.POWER_WRITE_COVER);
		} else {
			// 其他业务功率
			rPower = SPUtils.getInt(MyContexts.KEY_POWER_READ_INSTORE,
					SettingPowerActivity.POWER_READ_INSTORE);
			wPower = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_INSTORE,
					SettingPowerActivity.POWER_WRITE_INSTORE);
		}
		return UHFOperation.getInstance().setPower(rPower, wPower, 1, 0, 0);
	}

	/**
	 * 获取明文标志位。1~5：对应标志位F1~F5
	 * 
	 * @param flag
	 *            原始密文 标志位
	 * @param miyueNum
	 *            原始 密钥 编号 A0~A9
	 * @param uid
	 *            锁内NFC的UID
	 * @param isGetPlainFlag
	 *            true：根据加密标志位解出明文标志位；false: 根据 明文标志位 获得 密文标志位
	 * @return -1：uid错误； -2：密钥编号错误； -3：解出的标志位错误； 明文1~5：对应标志位F1~F5；
	 *         ！isPlainFlag： 加密成功返回
	 */
	public static int getFlag(int flag, int miyueNum, byte[] uid,
			boolean isGetPlainFlag) {
		if (null == uid || uid.length != 7) {
			return -1;
		}

		byte key = (byte) 0x0;// 解密密钥

		// int n = miyueNum & 0x0F;// 密钥编号 0~9
		switch (miyueNum & 0x0F) {// 密钥编号 : A0~A9 --> 0~9
		case 0:
			key = (byte) (uid[3] + uid[4] + uid[1]);
			break;
		case 1:
			key = (byte) (key ^ uid[1]);
			key = (byte) (key ^ uid[3]);
			key = (byte) (key ^ uid[5]);
			break;
		case 2:
			key = (byte) (key ^ uid[4]);
			key = (byte) (key ^ uid[5]);
			key = (byte) (key ^ uid[6]);
			break;
		case 3:
			key = (byte) (uid[3] + uid[2] + uid[1]);
			break;
		case 4:
			key = (byte) (uid[6] + 1);
			break;
		case 5:
			key = (byte) (uid[1] + uid[4] + uid[6]);
			break;
		case 6:
			key = (byte) (uid[1] ^ uid[3]);
			key = (byte) (key + uid[5]);
			break;
		case 7:
			key = (byte) (uid[4] + uid[6]);
			key = (byte) (key ^ uid[2]);
			break;
		case 8:
			key = (byte) (uid[2] + uid[4]);
			break;
		case 9:
			key = (byte) (uid[3] + uid[5]);
			key = (byte) (key ^ uid[4]);
			break;
		default:
			// 密钥编号 非法
			return -2;
		}

		int t = (uid[1] & 0xFF) % 10;
		if (isGetPlainFlag) {
			// 解出 明文 标志位
			int flagPlain = key ^ flag;
			for (int i = 0; i < 5; i++) {
				if (flagPlain == FLAG_DATA[5 * t + i]) {
					return i + 1;
				}
			}
			return -3;
		} else {
			// 生成 密文 标志位
			byte encodeFlag = FLAG_DATA[5 * t + (flag - 1)];
			return encodeFlag ^ key;
		}
	}

	/**
	 * 写交接信息：1、读交接索引； 2、写交接信息； 3、更新交接索引
	 * 
	 * @param newRFIDOp
	 * @param mHandoverData
	 * @param mUid
	 * @return 写入成功返回true
	 */
	public static boolean writeHandoverData(RFIDOperation newRFIDOp,
			byte[] mHandoverData, byte[] mUid) {

		// 4.1 读出 交接信息 索引
		byte[] indexs = new byte[1];
		if (!newRFIDOp.readNFCInTime(0x20, indexs, 500, mUid)) {
			return false;
		}
		int sa = (indexs[0]) & 0xFF;// 获得 相对位置
		LogsUtil.d("CIOO", "交接信息索引：" + sa);
		sa = 3 * sa + 0x40; // 计算绝对位置

		// 4.2 写入 交接信息
		if (!newRFIDOp.writeNFCInTime(sa, mHandoverData, 1000, mUid)) {
			return false;
		}
		// 4.3 更新 交接信息 索引
		indexs[0]++;
		if (!newRFIDOp.writeNFCInTime(0x20, indexs, 500, mUid)) {
			return false;
		}

		return true;
	}
}
