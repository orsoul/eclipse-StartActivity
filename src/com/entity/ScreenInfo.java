package com.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 墨水屏信息实体
 * 
 * @author andy
 *
 */
@Entity
public class ScreenInfo {

	/**
	 * 屏ID
	 */
	@Id
	private String screenID;

	/**
	 * 墨水屏是否使用的标志
	 */
	private boolean isUse;

	/**
	 * 墨水屏是否初始化的标志
	 */
	private boolean isInit;

	/**
	 * 屏关联的堆ID
	 */
	private String pileID;

	/**
	 * 流水号，用来创建屏ID
	 */
	private String serialNum;

	/**
	 * 屏刷新的标志，当与堆的更新次数相同时表示是最新的
	 */
	private int refresh_flag;

	private String update_time;

	@Generated(hash = 1462364918)
	public ScreenInfo(String screenID, boolean isUse, boolean isInit, String pileID,
									String serialNum, int refresh_flag, String update_time) {
					this.screenID = screenID;
					this.isUse = isUse;
					this.isInit = isInit;
					this.pileID = pileID;
					this.serialNum = serialNum;
					this.refresh_flag = refresh_flag;
					this.update_time = update_time;
	}

	@Generated(hash = 1053037331)
	public ScreenInfo() {
	}

	@Override
	public String toString() {
		String string = "ScreenInfo:";
		string += "屏ID：" + screenID;
		string += " 是否使用：" + isUse;
		string += " 是否初始化：" + isInit;
		string += " 堆ID：" + pileID;
		string += " 流水号：" + serialNum;
		string += " 刷新次数：" + refresh_flag;
		return string;
	}

	public String getScreenID() {
		return screenID;
	}

	public void setScreenID(String screenID) {
		this.screenID = screenID;
	}

	public boolean isUse() {
		return isUse;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	public String getPileID() {
		return pileID;
	}

	public void setPileID(String pileID) {
		this.pileID = pileID;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public int getRefresh_flag() {
		return refresh_flag;
	}

	public void setRefresh_flag(int refresh_flag) {
		this.refresh_flag = refresh_flag;
	}

	public boolean getIsUse() {
					return this.isUse;
	}

	public void setIsUse(boolean isUse) {
					this.isUse = isUse;
	}

	public boolean getIsInit() {
					return this.isInit;
	}

	public void setIsInit(boolean isInit) {
					this.isInit = isInit;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
}
