package com.entity;

public class PermissionInfo {
	private String bagModel;

	private String moneyType;

	private String moneyModel;

	public void setBagModel(String bagModel){
	this.bagModel = bagModel;
	}
	public String getBagModel(){
	return this.bagModel;
	}
	public void setMoneyType(String moneyType){
	this.moneyType = moneyType;
	}
	public String getMoneyType(){
	return this.moneyType;
	}
	public void setMoneyModel(String moneyModel){
	this.moneyModel = moneyModel;
	}
	public String getMoneyModel(){
	return this.moneyModel;
	}
}
