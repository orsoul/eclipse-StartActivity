package com.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 堆的信息实体
 * 
 * @author andy
 *
 */
@Entity
public class PileInfo {

	/**
	 * 堆ID
	 */
	@Id
	private String pileID;

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
	 * 总金额
	 */
	private String totalAmount;

	/**
	 * 机构ID
	 */
	private String organID;

	/**
	 * 流水号，用来创建下一个堆ID
	 */
	private String serialNum;

	/**
	 * 与堆相关联的屏的个数
	 */
	private int screenNum;

	/**
	 * 与堆相关联的袋的个数
	 */
	private int bagNum;

	/**
	 * 堆刷新的次数，作为屏显示的是否是最新数据的依据
	 */
	private int refresh_flag;

	private String update_time;

	private String pileName;
	/**
	 * 第几套人民币
	 */
	private String series;

	/**
	 * 最早存放日期
	 */
	private String time;

	/** * 清分/复点 类型 */
	private String clearingType;

	/** * 库房id */
	private String storeId;

	@Generated(hash = 1043801831)
	public PileInfo(String pileID, String bagType, String moneyType,
									String moneyModel, String totalAmount, String organID, String serialNum,
									int screenNum, int bagNum, int refresh_flag, String update_time,
									String pileName, String series, String time, String clearingType,
									String storeId) {
					this.pileID = pileID;
					this.bagType = bagType;
					this.moneyType = moneyType;
					this.moneyModel = moneyModel;
					this.totalAmount = totalAmount;
					this.organID = organID;
					this.serialNum = serialNum;
					this.screenNum = screenNum;
					this.bagNum = bagNum;
					this.refresh_flag = refresh_flag;
					this.update_time = update_time;
					this.pileName = pileName;
					this.series = series;
					this.time = time;
					this.clearingType = clearingType;
					this.storeId = storeId;
	}

	@Generated(hash = 1006751723)
	public PileInfo() {
	}

	@Override
	public String toString() {
		String string = "PileInfo:";
		string += "堆ID：" + pileID;
		string += " 袋类型：" + bagType;
		string += " 券别：" + moneyType;
		string += " 总金额：" + totalAmount;
		string += " 机构ID：" + organID;
		string += " 流水号：" + serialNum;
		string += " 屏数：" + screenNum;
		string += " 袋数：" + bagNum;
		string += " 刷新次数：" + refresh_flag;
		return string;
	}

	public String getPileID() {
		return pileID;
	}

	public void setPileID(String pileID) {
		this.pileID = pileID;
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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getOrganID() {
		return organID;
	}

	public void setOrganID(String organID) {
		this.organID = organID;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public int getScreenNum() {
		return screenNum;
	}

	public void setScreenNum(int screenNum) {
		this.screenNum = screenNum;
	}

	public int getBagNum() {
		return bagNum;
	}

	public void setBagNum(int bagNum) {
		this.bagNum = bagNum;
	}

	public int getRefresh_flag() {
		return refresh_flag;
	}

	public void setRefresh_flag(int refresh_flag) {
		this.refresh_flag = refresh_flag;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getPileName() {
					return this.pileName;
	}

	public void setPileName(String pileName) {
					this.pileName = pileName;
	}

	public String getSeries() {
					return this.series;
	}

	public void setSeries(String series) {
					this.series = series;
	}

	public String getTime() {
					return this.time;
	}

	public void setTime(String time) {
					this.time = time;
	}

	public String getClearingType() {
					return this.clearingType;
	}

	public void setClearingType(String clearingType) {
					this.clearingType = clearingType;
	}

	public String getStoreId() {
					return this.storeId;
	}

	public void setStoreId(String storeId) {
					this.storeId = storeId;
	}

	public String getMoneyModel() {
		return moneyModel;
	}

	public void setMoneyModel(String moneyModel) {
		this.moneyModel = moneyModel;
	}
}
