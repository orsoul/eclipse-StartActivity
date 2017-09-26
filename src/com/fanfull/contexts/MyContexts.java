package com.fanfull.contexts;

/**
 * @author Administrator
 * 
 * app 中常用的 常量
 */
public interface MyContexts {
	
	public static final String DB_NAME = "fanfull_db";
	public static final byte KEY_OLDBAGVERSON = 37;
	public static final String KEY_BANK_CODE_JSON = "KEY_BANK_CODE_JSON";
	/**
	 * 保存银行列表的 sharedpreference配置文件
	 */
	String BANKNAME_PREFERENCE = "BANKNAME_PREFERENCE";
	
	/**
	 * 墨水屏的下载模式，true为全离线，false为半离线
	 */
	String INK_SCREEN_DOWNLOAD = "int_screen_downLoad";
	/**
	 * sharedpreference配置文件名
	 */
	String PREFERENCE_NAME = "Cniao_Pref_Common"; // sharedpreference配置文件名
	/**
	 * 用户建堆权限
	 */
	String KEY_CREATE_PRESENTER = "KEY_CREATE_PRESENTER";
	/**
	 * ip 配置文件的 文件名
	 */
	String IP_CONFIG = "config.xml"; // ip 配置文件的 文件名
	/**
	 * 银行列表文件的 文件名
	 */
	String BANK_LIST = "bankList.json"; // ip 配置文件的 文件名
	/**
	 * ip
	 */

	/**
	 * 操作类型
	 */
	String KEY_OPERATION_TYPE = "KEY_OPERATION_TYPE";
	
	String KEY_FINGER_UPDATE= "FINGER_UPDATE";
	String KEY_IP0 = "KEY_IP0";
	/**
	 * 端口
	 */
	String KEY_PORT0 = "KEY_PORT0"; //
	/**
	 * ip
	 */
	String KEY_IP1 = "KEY_IP1";
	/**
	 * 端口
	 */
	String KEY_PORT1 = "KEY_PORT1"; //
	/**
	 * ip
	 */
	String KEY_IP2 = "KEY_IP2";
	/**
	 * 端口
	 */
	String KEY_PORT2 = "KEY_PORT2"; //
	
	/**
	 * 多道门天线
	 */
	String KEY_LOT_DOOR = "KEY_LOT_DOOR";
	/**
	 * 登录复核
	 */
	String KEY_CHECK_LOGIN = "KEY_CHECK_LOGIN";
	
	/**
	 * 勾选扫捆 (在 设置界面/扫捆设置 中配置)
	 */
	String KEY_SCAN_BUNCH = "KEY_SCAN_BUNCH";
	/**
	 * 扫捆 数量 (在 设置界面/扫捆设置 中配置)
	 */
	String KEY_BUNCH_NUM = "KEY_BUNCH_NUM";
	/**
	 * 超高频卡 封袋 读功率
	 */
	String KEY_POWER_READ_COVER = "KEY_POWER_READ_COVER";
	/**
	 * 超高频卡 封袋  写功率
	 */
	String KEY_POWER_WRITE_COVER = "KEY_POWER_WRITE_COVER";
	/**
	 * 超高频卡 入库 读功率
	 */
	String KEY_POWER_READ_INSTORE = "KEY_POWER_READ_INSTORE";
	/**
	 * 超高频卡 入库 写功率
	 */
	String KEY_POWER_WRITE_INSTORE = "KEY_POWER_WRITE_INSTORE";
	/**
	 * 是否启用预扫描
	 */
	String KEY_PRESCAN_ENABLE = "KEY_YK_ENABLE";
	/**
	 * 启用小屏显示
	 */
	String KEY_SMALL_SRCEEN_ENABLE = "KEY_SMALL_SRCEEN_ENABLE";
	
	/**
	 * 旧锁扫描显示
	 */
	String KEY_USE_OLDBAG_ENABLE = "KEY_OLDBAG_ENABLE";
	
	/**
	 * 银行 表单
	 */
	String KEY_BANK_LIST = "KEY_BANK_LIST";
	/**
	 * 任务类型, 任务或者批
	 */
	String KEY_TASK_TYPE = "KEY_TASK_TYPE";
	
	/**
	 * 指纹开关
	 * 
	 */
	String KEY_FINGER_PRINT_ENABLE = "KEY_FINGERPRINT_ENABLE";
	
	String KEY_FINGER_UPDATE_VERSION = "KEY_FINGER_UPDATE_VERSION";
	String KEY_CHANGE_BAG_REASON = "KEY_CHANGE_BAG_REASON";
	
	String IS_LAST_PI_NUMBER = "IS_LAST_PI_NUMBER";
	String IS_LAST_TYPE_NUMBER = "IS_LAST_TYPE_NUMBER";
	/**
	 * 手持批量入库 扫描模式
	 */
	String KEY_SCAN_MODE = "KEY_SCAN_MODE";
	/**
	 * 天线柜 工作模式: 0锁模式， 1锁或标牌，2锁与标牌
	 */
	String KEY_DOOR_MODLE = "KEY_DOOR_MODLE";
	/**
	 * 灭屏后，手持自动关机 的等待时间，单位 ： 分钟
	 */
	String KEY_SHUTDOWN_DELAY = "KEY_SHUTDOWN_DELAY";
	
	/*对话框 提示信息*/
	/**
	 * "提示"
	 */
	String TEXT_DIALOG_TITLE = "提示"; // 
	String DIALOG_MESSAGE_SCANED = "该捆已被扫描过,或券别有误!"; // 
	String DIALOG_MESSAGE_BARCODE_WRONG = "封签数据错误!"; // 
	String DIALOG_MESSAGE_QUIT = "是否退出当前页面?"; // 
	String DIALOG_MESSAGE_EPC_NO_MATCH = "锁片还未插上，请插上锁片后再操作！"; // 
	String DIALOG_MESSAGE_LOCK_BUNCH = "您确定要锁定批?"; // 

	/**
	 * 自定义 action，后台服务收到此广播 退出app
	 */
	String ACTION_EXIT_APP = "ACTION_EXIT_APP";
	/**
	 * 自定义 action，后台服务收到此广播 关闭手持
	 */
	String ACTION_SHUT_DOWN = "ACTION_SHUT_DOWN";
	/**
	 * 确认
	 */
	String TEXT_OK = "确认";
	/**
	 * 取消
	 */
	String TEXT_CANCEL = "取消";
	
	String LOCK_PI_TIMEOUT= "锁定批超时";
	String BAG_LOACK_NOT_INIT = "袋锁尚未初始化";
	
}
