package com.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 袋的信息实体
 * 
 * @author andy
 *
 */
@Entity
public class BagInfo {

	/**
	 * 袋ID
	 */
	@Id
	private String bagID;

	/**
	 * 堆ID
	 */
	private String pileID;

	/**
	 * 托盘ID
	 */
	private String trayID;

	private String update_time;

	@Generated(hash = 1765611754)
	public BagInfo(String bagID, String pileID, String trayID, String update_time) {
					this.bagID = bagID;
					this.pileID = pileID;
					this.trayID = trayID;
					this.update_time = update_time;
	}

	@Generated(hash = 674332077)
	public BagInfo() {
	}

	@Override
	public String toString() {
		String string = "BagInfo:";
		string += "袋ID：" + bagID;
		string += " 堆ID：" + pileID;
		string += " 托盘ID：" + trayID;
		return string;
	}

	public String getBagID() {
		return bagID;
	}

	public void setBagID(String bagID) {
		this.bagID = bagID;
	}

	public String getPileID() {
		return pileID;
	}

	public void setPileID(String pileID) {
		this.pileID = pileID;
	}

	public String getTrayID() {
		return trayID;
	}

	public void setTrayID(String trayID) {
		this.trayID = trayID;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
}
