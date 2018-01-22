package com.fanfull.socket;

import java.io.DataOutputStream;

import com.fanfull.contexts.Constants;
import com.fanfull.contexts.StaticString;
import com.fanfull.utils.LogsUtil;

/**
 * 
 * @ClassName: SentThread
 * @Description: 向服务器发送信息, 并开启 一个线程 接收服务器的回复信息
 * @author
 * @date 2014-9-12 下午04:26:17
 * 
 */
public class SendTask implements Runnable {
	private final static String TAG = SendTask.class.getSimpleName();
	/** 收到的网络信息的 头标识 */
	public final static char INFO_HEAD = '*';
	/** 发送的网络信息的 头标识 */
	public final static char INFO_HEAD_SEND = '$';
	/** 网络信息的 尾标识 */
	public final static char INFO_TAIL = '#';
	/** 网络信息 信息的分隔符 */
	public final static String INFO_SEPARATOR = " ";

	/** 查询天线柜类别：半自动，全自动 ; $1000 01 06*/
	public final static int CODE_DOOR_READY = 100006;
	/**
	 * 手持 预扫描 上传袋id 1003
	 */
	public final static int CODE_PRE_SCAN_UPLOAD_BAGID = 100002;
	public final static int CODE_DOOR_SCAN_UPLOAD_BAGID = 100003;
	/** 德州演示 模式切换. 00正常； 01特殊模式 */
	public final static int CODE_DZ_SWICH = 100004;
	public final static int CODE_SET_SALVER_NUM = 100005;
	/** 袋流转查询：$41 userID 01 封签事件码 包号# */
	public final static int CODE_BAG_CIRCULATION = 100007;
	/** 手持 预扫描 获取袋id列表 ：$1000 01 57 " */
	public final static int CODE_PRE_SCAN_GET_BAGID_LIST = 100057;
	/** 登陆 1 $01 userID password 000000 00000000000000000 taskcount# */
	public final static int CODE_LOGIN = 1;
	/** 复核登陆 0 $00 02 checkID password 000000 00000000000000000 taskcount# */
	public final static int CODE_LOGIN_CHECK = 0;
	/** 登出 30 */
	public final static int CODE_LOGOUT = 30;
	/** 复核登出 996 */
	public final static int CODE_LOGOUT_CHECKE = 996;

	/** 上传多个捆封签: $77 userId num barcode1,barcode2 pinumber taskcount# */
	public final static int CODE_UPLODE_PACKETS = 2001;
	/** 上传1个捆封签: $77 userId barcode 序号  pinumber taskcount# */
	public final static int CODE_UPLODE_PACKET = 20;
	/** 封袋上传袋信息: $22 userID 21 bagid tid bagType checkID pinumber taskcount# */
	public final static int CODE_COVER_UPLOAD_BAG_INFO = 881;

	/** 新袋选择入库操作: $07 userId 82 00000000000000000 taskcount# */
	public final static int CODE_IN_STORE_NEW = -4;
	/** 选择入库操作: $07 userId 02 00000000000000000 taskcount# */
	public final static int CODE_IN_STORE = 4;
	/**
	 * 入库上传袋信息(逐个): $22 userID 22 bagid tid eventCode checkID pinumber
	 * taskcount#
	 */
	public final static int CODE_IN_STORE_UPLOAD_BAG_INFO = 885;
	/**
	 * 入库上传袋信息(逐个): $22 userID 23 bagid tid eventCode checkID pinumber
	 * taskcount#
	 */
	public final static int CODE_OUT_STORE_UPLOAD_BAG_INFO = 886;
	/**
	 * 验封上传袋信息(逐个): $22 userID 24 bagid tid eventCode checkID pinumber
	 * taskcount#
	 */
	public final static int CODE_CHECK_COVER_UPLOAD_BAG_INFO = 887;
	/** 入库上传袋信息(批量): $32 userID 02 pinumber taskcount# */
	public final static int CODE_IN_STORE_LOT_SCAN_UPLOAD_BAGID = 71;

	/** 选择出库操作（新袋）: $07 userId 83 00000000000000000 taskcount# */
	public final static int CODE_OUT_STORE_NEW = -31;
	/** 选择出库操作（老袋）: $07 userId 03 00000000000000000 taskcount# */
	public final static int CODE_OUT_STORE = 31;
	/**
	 * 出库上传袋信息(逐个): $22 userID 23 bagid tid eventCode checkID pinumber
	 * taskcount#
	 */
	public final static int CODE_OUT_STORE_LOT_SCAN_UPLOAD_BAGID = 72;

	/**
	 * 选择 换袋 操作 62
	 */
	public final static int CODE_CHANGE_BAG = 62;
	/** 锁定批 7 */
	public final static int CODE_LOCK_PI = 7;

	/**
	 * 选择封袋操作 21
	 */
	public final static int CODE_LOCK_BAG = 21;

	/**
	 * 指纹操作，注册，更新等
	 */
	public final static int FINGER_OPERATION = 14;

	/**
	 * 获取银行代号列表 80 $08
	 */
	public final static int CODE_BANK_CODE_LIST = 80;
	/** 手持 批量入库 获取袋数： $37 userID count# */
	public final static int CODE_LOT_INSTORE_NUM = 370;
	/**
	 * 手持 批量入库 获取袋id列表 570： "$57 userid"
	 */
	public final static int CODE_LOT_INSTORE_GET_BAGID_LIST = 570;
	/**
	 * 入库时间查询 100
	 */
	public final static int CODE_QUERY_INSTORE_TIME = 100;
	/**
	 * 打开预扫描(入库) 160
	 */

	public final static int CODE_PRE_OPEN_IN_IS_READY = 160;
	/**
	 * 打开预扫描(出库) 161
	 */
	public final static int CODE_PRE_OPEN_OUT = 161;
	/**
	 * 开始预扫描(入库) 170
	 */
	public final static int CODE_PRE_START_IN = 170;
	/**
	 * 开始预扫描(出库) 171
	 */
	public final static int CODE_PRE_START_OUT = 171;
	/**
	 * 停止预扫描(入库) 180
	 */
	public final static int CODE_PRE_PAUSE_IN = 180;
	/**
	 * 停止预扫描(出库) 181
	 */
	public final static int CODE_PRE_STOP_OUT = 181;
	/**
	 * 关闭预扫描(入库) 190
	 */
	public final static int CODE_PRE_CLOSE_IN = 190;
	/**
	 * 关闭预扫描(出库) 191
	 */
	public final static int CODE_PRE_CLOSE_OUT = 191;
	/**
	 * 查询 托盘信息 109
	 */
	public final static int CODE_QUERY_SALVER_INFO = 109;
	/**
	 * 确认进行托盘 110
	 */
	public final static int CODE_MINGLE_SALVER = 110;
	/**
	 * 选择 入库 任务 17
	 */
	public final static int CODE_SELECT_INSTORE_TASK = 17;
	/**
	 * 遥控 启动(入库) 200
	 */
	public final static int CODE_YK_START_IN = 200;
	/**
	 * 遥控 启动(出库) 201
	 */
	public final static int CODE_YK_START_OUT = 201;
	/**
	 * 遥控 停止/复位(入库) 220 : $107 userId xx pinumber taskcount#
	 */
	public final static int CODE_YK_RESET_IN = 220;
	/**
	 * 遥控 停止/复位(出库) 221 : $107 userId xx pinumber taskcount#
	 */
	public final static int CODE_YK_RESET_OUT = 221;
	/**
	 * 遥控 摆动(出库) 211 : $nt userId xx pinumber taskcount#
	 */
	public final static int CODE_YK_SWING_OUT = 211;
	/**
	 * 遥控 摆动(入库) 210 : $nt userId xx pinumber taskcount#
	 */
	public final static int CODE_YK_A2_IN = 210;
	/**
	 * 遥控 摆动(入库) 210 : $nt userId xx pinumber taskcount#
	 */
	public final static int CODE_YK_SWING_IN = CODE_YK_A2_IN;
	/**
	 * 遥控 A3 点(入库) 230
	 */
	public final static int CODE_YK_A3_IN = 230;
	/**
	 * 遥控 A3 点 复位(出库) 231
	 */
	public final static int CODE_YK_A3_OUT = 231;
	/**
	 * 出库任务单 230
	 */
	public final static int CODE_OUT_STORE_TASK = 240;
	/**
	 * 完成一托盘 入库 480
	 */
	public final static int CODE_FINISH_TP_IN = 480;
	/**
	 * 完成一托盘 出库 481
	 */
	public final static int CODE_FINISH_TP_OUT = 481;

	public DataOutputStream dout;
	private String msg = null;

	public SendTask(DataOutputStream dout) {
		this.dout = dout;
	}
	public static String getInfo(int taskID, String taskcount, String... info) {
		String msg = null;
		switch (taskID) {
		case 500:// 上传袋id
			msg = "$500 " + info[0] + " " + taskcount + "#";
			break;
		case CODE_BAG_CIRCULATION:// 上传袋id
			msg = "$41 " + StaticString.userId + " 01 " + info[0] + " "
					+ taskcount + "#";
			break;
		case CODE_DZ_SWICH:// 德州演示 模式切换
			msg = "$1000 01 04 " + info[0] + " " + StaticString.userId + " "
					+ taskcount + "#";
			break;
		case CODE_PRE_SCAN_UPLOAD_BAGID:// 预扫描 补扫
			msg = "$1000 01 02 " + StaticString.userId + " " + info[0] + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_DOOR_SCAN_UPLOAD_BAGID:// 交接扫描 补扫
			msg = "$1000 01 03 " + StaticString.userId + " " + info[0] + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_SET_SALVER_NUM:// 设置 托盘袋数
			msg = "$1000 01 05 " + info[0] + " " + StaticString.userId + " "
					+ taskcount + "#";
			break;
		case 8000:// 查询袋流转情况
			msg = "$41 " + StaticString.userId + " " + info[0] + " "
					+ taskcount + "#";
			break;
		case 888:// 本地写封签信息失败，通知服务器删除
			msg = "$22 " + StaticString.userId + " "
					+ Constants.OP_TYPE.DEL_BAG + " " + info[0] + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case 997:// 指纹登录
			msg = "$14 03 " + StaticString.orgId + StaticString.userLast3Id
					+ " " + taskcount + "#";
			break;
		case 999:// 上传指纹信息 用户编号后两位
			msg = "$14 01 " + StaticString.userLast3Id + " " + info[0] + " "
					+ info[1] + " " + info[2] + " " + taskcount + "#";
			break;
		case 998:// 查询指纹版本
			msg = "$14 02 " + StaticString.userLast3Id + " " + taskcount + "#";
			break;

		case 1000:// 发送版本信息，当服务器的数字大于info[0]时，需要进行更新操作
			msg = "$14 00 " + info[0] + " " + taskcount + "#";
			break;
		case 1001:// 下载指纹
			msg = "$14 04 " + info[0] + " " + info[1] + " " + info[2] + " "
					+ taskcount + "#";// json:[{"087101001002","4"}]
			break;
		case 1002:// 删除
			msg = "$14 05 " + StaticString.orgId + StaticString.userLast3Id
					+ " " + info[0] + " " + info[1] + " " + taskcount + "#";
			break;
		case CODE_QUERY_SALVER_INFO:// 查询 托盘信息; info[0]:袋Id
			msg = "$" + CODE_QUERY_SALVER_INFO + " " + info[0] + " "
					+ taskcount + "#";
			break;
		case CODE_MINGLE_SALVER:// 确定 混托盘; info[0]:托盘Id
			msg = "$" + CODE_MINGLE_SALVER + " " + info[0] + " " + taskcount
					+ "#";
			break;
		case CODE_SELECT_INSTORE_TASK:// 选择入库任务 info[0]:任务或批, info[1]:taskNum
			msg = "$" + CODE_SELECT_INSTORE_TASK + " " + StaticString.userId
					+ " 02 " + info[0] + " " + info[1] + " 000000000000000000 "
					+ taskcount + "#";
			break;

		case CODE_DOOR_READY:// 查询天线柜类别：半自动，全自动
			msg = "$1000 01 06 " + StaticString.userId + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_PRE_SCAN_GET_BAGID_LIST:
			msg = "$1000 01 57 " + taskcount + "#";
			break;
		case -1:// 指纹复核
			msg = "$00 01 " + StaticString.orgId + StaticString.userLast3Id
					+ " " + taskcount + "#";
			break;
		case CODE_LOGIN_CHECK:// 复核
			msg = "$00 02 " + StaticString.userIdcheck + " "
					+ StaticString.password + " 000000 00000000000000000 "
					+ taskcount + "#";
			break;
		case CODE_LOGIN:// 登陆 新：666666； 老：000000
			msg = "$01 " + StaticString.userId + " " + StaticString.password
					+ " 666666 00000000000000000 " + taskcount + "#";
			break;

		case -3:// 新建一个批
			msg = "$07 " + StaticString.userId + " -1 00000000000000000 "
					+ taskcount + "#";
			break;
		case -2:// 查询所有批
			msg = "$17 " + StaticString.userId + " -1 " + "000000000000000000 "
					+ taskcount + "#";
			break;
		case 3:// 封袋
			msg = "$02 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.barcode + " 01 "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_IN_STORE_NEW:// 查询入库任务 新版

			msg = "$07 " + StaticString.userId + " 82 00000000000000000 "
					+ taskcount + "#";
			break;
		case CODE_IN_STORE:// 查询入库任务

			msg = "$07 " + StaticString.userId + " 02 00000000000000000 "
					+ taskcount + "#";
			break;
		case 5:// 选择入库任务
			msg = "$17 " + StaticString.userId + " 02 "
					+ StaticString.tasknumber + " 000000000000000000 "
					+ taskcount + "#";
			break;
		case 6:// 入库 上传批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.bagbarcode
					+ " " + StaticString.barcode + " 02 " + StaticString.bagid
					+ " " + StaticString.pinumber + " " + taskcount + "#";
			break;
		case 2301:// 老系统 上传封袋批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.barcode + " 01 "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case 601:// 老袋入库 上传批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.barcode + " 02 "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case 602:// 老袋出库 上传批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.barcode + " 03 "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_LOCK_PI:// 锁定批
			msg = "$27 " + StaticString.userId + " 00 0 "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_LOGOUT:// 登出
			if (StaticString.pinumber == null) {
				msg = "$03 " + StaticString.userId + " " + "0"
						+ " 00000000000000000 " + taskcount + "#";
			} else {
				msg = "$03 " + StaticString.userId + " 0 "
						+ StaticString.pinumber + " " + taskcount + "#";
			}
			break;
		case CODE_LOGOUT_CHECKE: // 复核 登出
			msg = "$03 " + StaticString.userIdcheck + " " + "0"
					+ " 00000000000000000 " + taskcount + "#";
			break;
		case CODE_UPLODE_PACKET:// 上传1个捆封签: 
			msg = "$77 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.SCAN_OK + " " + StaticString.pinumber
					+ " " + taskcount + "#";
			break;
		case CODE_UPLODE_PACKETS:// 上传多个捆封签
			msg = "$77 " + StaticString.userId + " " + info[0]
			+ " " + info[1] + " " + StaticString.pinumber
			+ " " + taskcount + "#";
			break;
		case CODE_LOCK_BAG:// 封袋批查询
			msg = "$07 " + StaticString.userId + " 01 00000000000000000 "
					+ taskcount + "#";
			break;
		case 22:// 选择封袋任务
			msg = "$17 " + StaticString.userId + " 01 "
					+ StaticString.tasknumber + " 000000000000000000 "
					+ taskcount + "#";
			break;
		case 23:// 上传封袋批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.barcode + " 01 " + StaticString.bagid
					+ " " + StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_OUT_STORE_NEW:// 查询出库任务 新版 //0918
			msg = "$07 " + StaticString.userId + " 83 00000000000000000 "
					+ taskcount + "#";
			break;
		case CODE_OUT_STORE:// 查询出库任务
			msg = "$07 " + StaticString.userId + " 03 00000000000000000 "
					+ taskcount + "#";
			break;
		case 32:// 选择出库任务
			msg = "$17 " + StaticString.userId + " 03 "
					+ StaticString.tasknumber + " 000000000000000000 "
					+ taskcount + "#";
			break;
		case 33:// 上传批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.bagbarcode
					+ " " + StaticString.barcode + " 03 " + StaticString.bagid
					+ " " + StaticString.pinumber + " " + taskcount + "#";
			break;
		case 34:// 上传出库开袋批编号
			msg = "$40 " + StaticString.userId + " 000000 " + " 0 "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case 41:// 验封批查询
			msg = "$07 " + StaticString.userId + " 04 00000000000000000 "
					+ taskcount + "#";
			break;
		case 42:// 选择验封任务
			msg = "$17 " + StaticString.userId + " 04 "
					+ StaticString.tasknumber + " 000000000000000000 "
					+ taskcount + "#";
			break;
		case 43:// 上传验封批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.bagbarcode
					+ " " + StaticString.barcode + " 04 " + " "
					+ StaticString.bagid + " " + StaticString.pinumber + " "
					+ taskcount + "#";
			break;
		case 51:// 开袋批查询
			msg = "$07 " + StaticString.userId + " 06 00000000000000000 "
					+ taskcount + "#";
			break;
		case 52:// 选择开袋任务
			msg = "$17 " + StaticString.userId + " 06 "
					+ StaticString.tasknumber + " 000000000000000000 "
					+ taskcount + "#";
			break;
		case 53:// 上传开袋批编号
			msg = "$02 " + StaticString.userId + " " + StaticString.bagbarcode
					+ " " + StaticString.barcode + " 06 " + " "
					+ StaticString.bagid + " " + StaticString.pinumber + " "
					+ taskcount + "#";
			break;

		case 61:// 上传旧条码
			msg = "$06 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.reason + " 000000000000000000 "
					+ taskcount + "#";
			break;
		case CODE_CHANGE_BAG:// 启动换袋
			msg = "$16 " + "00" + " 000000000000000000 " + taskcount + "#";
			break;
		case 63:// 请求换袋
			msg = "$26 " + "01" + " " + StaticString.barcode + " "
					+ StaticString.reason + " " + "000000000000000000" + " "
					+ taskcount + "#";
			break;
		case 64:// 重新封袋
			msg = "$36 " + StaticString.userId + " " + StaticString.barcode
					+ " " + StaticString.barcode + " " + StaticString.reason
					+ " " + StaticString.bagid + " " + StaticString.pinumber
					+ " " + taskcount + "#";
			break;
		case CODE_IN_STORE_LOT_SCAN_UPLOAD_BAGID:// 手持批量入库 上传袋id
			msg = "$32 " + StaticString.userId + " 02 " + StaticString.bagid
					+ " " + StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_OUT_STORE_LOT_SCAN_UPLOAD_BAGID: // 手持批量出库 上传袋id
			msg = "$32 " + StaticString.userId + " 03 " + StaticString.bagid
					+ " " + StaticString.pinumber + " " + taskcount + "#";
			break;
		case 8: // / 取消入库任务
			msg = ("$17 " + StaticString.userId + " 02 01 00" + " " + taskcount + "#");
			break;
		case 9: // / 取消出库任务
			msg = ("$17 " + StaticString.userId + " 03 01 00" + " " + taskcount + "#");
			break;
		case CODE_LOT_INSTORE_NUM:// 手持 批量入库 获取袋数
			msg = "$37 " + StaticString.userId + " " + taskcount + "#";
			break;
		case CODE_LOT_INSTORE_GET_BAGID_LIST:// 手持 批量入库 获取袋id列表
			msg = "$57 " + StaticString.userId + " " + taskcount + "#";
			break;

		case 11:// 遥控设定 批次数
			msg = "$58 " + StaticString.YK_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#";
			break;
		case CODE_YK_START_IN:// 开启遥控 入库
			msg = ("$106 " + StaticString.userId + " 02 "
					+ StaticString.YK_BAGID + " " + StaticString.pinumber + " "
					+ taskcount + "#");
			break;
		case CODE_YK_START_OUT:// 开启遥控 出库
			msg = ("$106 " + StaticString.userId + " 03 "
					+ StaticString.YK_BAGID + " " + StaticString.pinumber + " "
					+ taskcount + "#");
			break;
		case CODE_YK_A2_IN:
			msg = ("$nt " + StaticString.userId + " 02" + " "
					+ StaticString.pinumber + " " + taskcount + "#");
			break;
		case CODE_YK_SWING_OUT:
			msg = ("$nt " + StaticString.userId + " 03" + " "
					+ StaticString.pinumber + " " + taskcount + "#");
			break;
		case CODE_YK_RESET_IN:
			msg = ("$107 " + StaticString.userId + " 02" + " "
					+ StaticString.pinumber + " " + taskcount + "#");
			break;
		case CODE_YK_RESET_OUT:
			msg = ("$107 " + StaticString.userId + " 03" + " "
					+ StaticString.pinumber + " " + taskcount + "#");
			break;
		case CODE_YK_A3_IN:
			msg = ("$A3 " + StaticString.userId + " 02" + " "
					+ StaticString.pinumber + " " + taskcount + "#");
			break;
		case CODE_YK_A3_OUT:
			msg = ("$A3 " + StaticString.userId + " 03" + " "
					+ StaticString.pinumber + " " + taskcount + "#");
			break;
		case 950:// 手持 预扫描
			msg = ("$95 " + StaticString.userId + " 0 " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case 951:// 天线 预扫描
			msg = ("$95 " + StaticString.userId + " 1 " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		/* 天线预扫描 遥控指令 */
		case CODE_PRE_OPEN_IN_IS_READY:// 启动 预扫描
			msg = ("$108 " + StaticString.userId + " 01 " + taskcount + "#");
			break;
		case CODE_PRE_OPEN_OUT:
			msg = ("$perOpen " + StaticString.userId + " 03" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_PRE_START_IN:// 开始 预扫描
			msg = ("$103 " + StaticString.userId + " 02" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_PRE_START_OUT:
			msg = ("$103 " + StaticString.userId + " 03" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_PRE_PAUSE_IN:// 停止 预扫描
			msg = ("$104 " + StaticString.userId + " 02" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_PRE_STOP_OUT:
			msg = ("$104 " + StaticString.userId + " 03" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_PRE_CLOSE_IN:// 结束 预扫描
			msg = ("$105 " + StaticString.userId + " 02" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_PRE_CLOSE_OUT:
			msg = ("$105 " + StaticString.userId + " 03" + " "
					+ StaticString.PRE_SCAN_NUM + " " + StaticString.pinumber
					+ " " + taskcount + "#");
			break;
		case CODE_OUT_STORE_TASK: // 出库 表单 $230
			msg = ("$230 " + StaticString.userId + " "
					+ StaticString.OUT_STORE_TASK + " " + taskcount + "#");
			break;
		case CODE_FINISH_TP_IN:// 完成一托盘 入库
			msg = ("$48 " + StaticString.userId + " 02 " + taskcount + "#");
			break;
		case CODE_FINISH_TP_OUT:// 完成一托盘 出库
			msg = ("$48 " + StaticString.userId + " 03 " + taskcount + "#");
			break;
		case CODE_BANK_CODE_LIST:// 获取银行列表
			msg = ("$08 " + StaticString.userId + " 00 " + taskcount + "#");
			break;
		case CODE_QUERY_INSTORE_TIME:// 入库时间查询
			msg = ("$10 " + StaticString.userId + " " + StaticString.bagid
					+ " " + taskcount + "#");
			break;

		/** 05版新增的命令 */
		case 797: // 服务器查询该袋目前的流转状态
			msg = "$20 " + StaticString.userId + " " + StaticString.bagid + " "
					+ taskcount + "#";
			break;
		case 798:// 初始化
			msg = "$ib " + StaticString.bagid + " " + taskcount + "#";
			break;
		case 799:// 上传开袋批编号
			msg = "$22 " + StaticString.userId + " " + "26" + " "
					+ StaticString.bagid + " " + StaticString.tid + " "
					+ StaticString.eventCode + " " + StaticString.userIdcheck
					+ " " + StaticString.pinumber + " " + taskcount + "#";
			break;
		case 880:// 22 userIC 21 袋ID + 封签ID（TID）+ 批号+ 包号# 查询当前袋的状态。
			msg = "$22 " + StaticString.userId + " " + "20" + " "
					+ StaticString.bagid + " " + StaticString.tid + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_COVER_UPLOAD_BAG_INFO:// 22 userIC 21 袋ID + 封签ID（TID）+券别信息
										// 批号+包号# 21
			msg = "$22 "
					+ StaticString.userId
					+ " "
					+ Constants.OP_TYPE.COVER_BAG
					+ " "
					+ StaticString.bagid
					+ " "
					+ StaticString.tid
					+ " "
					+ (StaticString.bagtype = StaticString.bagtype == null ? "11"
							: StaticString.bagtype) + " "
					+ StaticString.userIdcheck + " " + StaticString.pinumber
					+ " " + taskcount + "#";
			LogsUtil.d(TAG, msg);
			break;
		case 882:// 22 userIC 20 袋ID + 包号# //启用
			msg = "$22 " + StaticString.userId + " " + "30" + " "
					+ StaticString.bagid + " " + taskcount + "#";
			break;
		case 883:// 22 userIC 21 袋ID + 包号# //注销
			msg = "$22 " + StaticString.userId + " " + "31" + " "
					+ StaticString.bagid + " " + taskcount + "#";
			break;

		case 884:// 22 userIC 21 袋ID + 包号# //查询有没启用
			msg = "$22 " + StaticString.userId + " " + "32" + " "
					+ StaticString.bagid + " " + taskcount + "#";
			break;
		case CODE_IN_STORE_UPLOAD_BAG_INFO:// 上传入库袋信息
			msg = "$22 " + StaticString.userId + " "
					+ Constants.OP_TYPE.IN_STORE + " " + StaticString.bagid
					+ " " + StaticString.tid + " " + StaticString.eventCode
					+ " " + StaticString.userIdcheck + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_OUT_STORE_UPLOAD_BAG_INFO:// 上传出库袋信息
			msg = "$22 " + StaticString.userId + " "
					+ Constants.OP_TYPE.OUT_STORE + " " + StaticString.bagid
					+ " " + StaticString.tid + " " + StaticString.eventCode
					+ " " + StaticString.userIdcheck + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		case CODE_CHECK_COVER_UPLOAD_BAG_INFO:// 上传验封袋信息
			msg = "$22 " + StaticString.userId + " "
					+ Constants.OP_TYPE.CHECK_COVER + " " + StaticString.bagid
					+ " " + StaticString.tid + " " + StaticString.eventCode
					+ " " + StaticString.userIdcheck + " "
					+ StaticString.pinumber + " " + taskcount + "#";
			break;
		}
		LogsUtil.e(TAG, taskID + " send:" + msg);
		return msg;
	}
	
	@Override
	public void run() {
		LogsUtil.e("sentTask " + msg);
		if (null == msg) {
			return;
		}
		try {
			dout.writeBytes(msg);
			dout.flush();
		} catch (Exception e) {
			LogsUtil.e(TAG + " Exception");
			e.printStackTrace();
			SocketConnet.getInstance().connect(0);
		}
	}

	public DataOutputStream getDout() {
		return dout;
	}

	public void setDout(DataOutputStream dout) {
		this.dout = dout;
	}

}
