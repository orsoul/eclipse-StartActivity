package com.fanfull.contexts;

/**
 * 操作 类型
 * 
 * @author orsoul
 */
public interface TYPE_OP {
	String KEY_TYPE = "KEY_TYPE";
	
	/**
	 * 封袋
	 */
	int COVER_BAG = 11;
	/**
	 * 封袋，老袋
	 */
	int COVER_BAG_OLD_BAG = 12;
	
	/**
	 * 天线柜 入库，无预扫描
	 */
	int IN_STORE_DOOR = 21;
	/**
	 * 天线柜 入库，有预扫描
	 */
	int IN_STORE_DOOR_PRE = 22;
	/**
	 * 手持批量入库
	 */
	int IN_STORE_HAND_LOT = 23;
	/**
	 * 手持逐个入库
	 */
	int IN_STORE_HAND = 24;
	/**
	 * 手持 老袋入库
	 */
	int IN_STORE_OLD_BAG = 25;
	
	/**
	 * 天线柜出库
	 */
	int OUT_STORE_DOOR = 31;
	/**
	 * 手持批量出库
	 */
	int OUT_STORE_HAND_LOT = 32;
	/**
	 * 手持逐个出库
	 */
	int OUT_STORE_HAND = 33;
	/**
	 * 手持出库，老袋
	 */
	int OUT_STORE_OLD_BAG = 34;
	
	/**
	 * 开袋
	 */
	int OPEN_BAG = 41;
	
	/**
	 * 换袋
	 */
	int CHANGE_BAG = 51;
	/**
	 * 查询袋
	 */
	int QUERY_BAG = 61;
}
