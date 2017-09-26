package com.entity;

import java.math.BigInteger;

/**
 * 墨水屏刷新的数据实体
 * 
 * @author root
 *
 */
public class RefreshScreen {
	public String moneyType;	//一角 01
	public String model;	//已清分 01
	public String moneyDisplay1;	//8位
	public String moneyDisplay2;
	public String display_style;	//00白底黑字 01黑底白字
	public String date;		//28位
	public String userID;	//26位
	public String bagNum;	//6位
	public String series;	//2位
	public String moneyModel;	//币种
	public String pileName;	//6位
	public String refreshNum;	//2位
	public String time;	//28位
	public String police;	//2位
	
	public RefreshScreen(){}

	public String getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMoneyDisplay1() {
		return moneyDisplay1;
	}

	public void setMoneyDisplay1(String moneyDisplay1) {
		this.moneyDisplay1 = moneyDisplay1;
	}

	public String getMoneyDisplay2() {
		return moneyDisplay2;
	}

	public void setMoneyDisplay2(String moneyDisplay2) {
		this.moneyDisplay2 = moneyDisplay2;
	}

	public String getDisplay_style() {
		return display_style;
	}

	public void setDisplay_style(String display_style) {
		this.display_style = display_style;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getBagNum() {
		return bagNum;
	}

	public void setBagNum(String bagNum) {
		this.bagNum = bagNum;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getMoneyModel() {
		return moneyModel;
	}

	public void setMoneyModel(String moneyModel) {
		this.moneyModel = moneyModel;
	}

	public String getPileName() {
		return pileName;
	}

	public void setPileName(String pileName) {
		this.pileName = pileName;
	}

	public String getRefreshNum() {
		return refreshNum;
	}

	public void setRefreshNum(String refreshNum) {
		this.refreshNum = refreshNum;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPolice() {
		return police;
	}

	public void setPolice(String police) {
		this.police = police;
	}
	
	
	public int getMoneyNum(){
		BigInteger s1 = new BigInteger(moneyDisplay1, 16);
		BigInteger s2 = new BigInteger(moneyDisplay2, 16);
		
		String s = s1.toString()+s2.toString();
		
		int r = Integer.parseInt(s);
		System.out.println("读取金额数据："+r);
		return r;
	}
	
	public int getBagNumber(){
		BigInteger s = new BigInteger(bagNum, 16);
		return Integer.parseInt(s.toString());
	}
	
	public int getRefreshNumber(){
		if(refreshNum.substring(0, 1).equals("0")){
			return Integer.parseInt(refreshNum.substring(1, 2),16);
		}else{
			return Integer.parseInt(refreshNum,16);
		}
	}
}
