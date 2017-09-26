package com.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 托盘的信息实体
 * 
 * @author andy
 *
 */
@Entity
public class TrayInfo {
	/**
	 * 托盘ID
	 */
	@Id
	private String trayID;

	/**
	 * 总金额
	 */
	private String totalAmount;

	/**
	 * 袋类型
	 */
	private String bagType;

	/**
	 * 券别
	 */
	private String moneyType;

	private String moneyModel;

	/**
	 * 袋数量
	 */
	private int bagNum;

	private String update_time;
	@Generated(hash = 2084712056)
	public TrayInfo(String trayID, String totalAmount, String bagType,
									String moneyType, String moneyModel, int bagNum, String update_time) {
					this.trayID = trayID;
					this.totalAmount = totalAmount;
					this.bagType = bagType;
					this.moneyType = moneyType;
					this.moneyModel = moneyModel;
					this.bagNum = bagNum;
					this.update_time = update_time;
	}

	@Generated(hash = 426239576)
	public TrayInfo() {
	}

	@Override
	public String toString() {
		String string = "TrayInfo:";
		string += "托盘ID：" + trayID;
		string += " 总金额：" + totalAmount;
		string += " 袋数量：" + bagNum;
		return string;
	}

	public String getTrayID() {
		return trayID;
	}

	public void setTrayID(String trayID) {
		this.trayID = trayID;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getBagNum() {
		return bagNum;
	}

	public void setBagNum(int bagNum) {
		this.bagNum = bagNum;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getBagType() {
		return bagType;
	}

	public void setBagType(String bagType) {
		this.bagType = bagType;
	}

	public String getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	public String getMoneyModel() {
		return moneyModel;
	}

	public void setMoneyModel(String moneyModel) {
		this.moneyModel = moneyModel;
	}
}
