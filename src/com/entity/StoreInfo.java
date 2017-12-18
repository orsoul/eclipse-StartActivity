package com.entity;

public class StoreInfo {
	private int storeID; // 库房ID
	private String storeCode;// 库房编号
	private String storeName;// 库房名
	private int used;// 启用状态
	public int getStoreID() {
		return storeID;
	}
	public void setStoreID(int storeID) {
		this.storeID = storeID;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public int getUsed() {
		return used;
	}
	public void setUsed(int used) {
		this.used = used;
	}
	
//	"storeID":32,        库房ID
//	            "storeCode":"B",        库房编号
//	            "storeName":"残损库",      库房名
//	            "address":"金库一楼",      所在地址
//	            "startDate":"2014-03-22",      建立时间
//	            "remark":"",
//	            "OrganCode":"002701001",    机构号
//	            "creatID":"002701001000",    创建人
//	            "creatTime":"2017-03-22 15:28:57",  创建时间
//	            "used":"1"        启用状态
//	        }

}
