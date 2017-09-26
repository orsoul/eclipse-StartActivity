package com.fanfull.contexts;

public class StaticString {
	/**
	 * ip0
	 */
	public static String IP0 = "192.168.11.179";
	/**
	 * 端口
	 */
	public static int PORT0 = 10001; //
	/**
	 * 青岛第二道门 ip
	 */
	public static String IP1 = "192.168.18.100";
	/**
	 * 青岛第二道门 端口
	 */
	public static int PORT1 = 10001; //
	/**
	 * 青岛第三道门 ip
	 */
	public static String IP2 = "192.168.18.111";
	/**
	 * 青岛第三道门 端口
	 */
	public static int PORT2 = 10001; //

	/**
	 * 保存 从服务器 返回的 信息
	 */
	public static String information;// 返回信息

	public static String userId = null;// "794AC50C"; //卡号StaticString.userId
	/** 复核的userID；==null说明还未复核过；!=null说明已经复核过，后续将不再复核； 每次登陆后将其置为null； */
	public static String userIdcheck = null; // 复核人的userID
	/** 用户权限 */
	public static String permission = null;
	public static String password = null;// 密码
	public static String orgId = null;// 机构号
	public static String userLast3Id = "000";// 登录用户最后三位 这是服务器返回的用户序号编号，不是刷卡的后两位
	
	public static String tasknumber = null;// 任务编号
	public static String pinumber = "0000000000000000000000";// 批编号
	public static String barcode = null;// 条码
	public static String bagbarcode = null;// 袋中的条码
	public static String reason = null;// 换袋原因
	public static String bagid = null;// 袋子ID
	public static String tid = null;// 封签码
	/** 封签事件码 60字符 */
	public static String bagtidcode = null;
	public static String barcode_old = null;// 旧条码
	public static String bagtype = null;// 袋类型，记录封签的时候选择的完整，清分，复点等信息
	public static boolean againPass = false;// 无复核人

	public static String SCAN_OK = null;// 扫捆成功

	public static int YK_TOTAL_NUM = 0;
	public static int YK_PART_NUM = 0;
	public static int PRE_SCAN_NUM = 0;

	/**
	 * 青岛 第2道天线 出库任务单
	 */
	public static String OUT_STORE_TASK;
	/**
	 * 青岛 第2道天线 入库数量
	 */
	public static String YK_NUM = "0";
	/**
	 * 青岛 第2道天线 入库扫描袋id,后台据此知道托盘编号
	 */
	public static String YK_BAGID = "0";

}
