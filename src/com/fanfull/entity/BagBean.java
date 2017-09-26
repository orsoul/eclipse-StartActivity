package com.fanfull.entity;

public class BagBean {
	private static final String[] BAG_TYPE_NAME = new String[] {"100元", "5元",
			"50元", "20元", "10元", "1元", "2元", "5角", "2角", "1角" };
	private static final String[] BAG_CLEARING_NAME = new String[] { "未复点",
			"已复点", "未清分", "已清分" };

	/**
	 * 袋id
	 */
	private String bagId;
	public String getBagId() {
		return bagId;
	}

	public void setBagId(String bagId) {
		this.bagId = bagId;
	}

	/**
	 * 袋型号 : 04 , 05
	 */
	private int bagVersion;
	/**
	 * 机构号
	 */
	private String orgId;
	/**
	 * 是否为完整券
	 */
	private boolean isIntegrity;
	/**
	 * 袋型号 : 01=100元, 02=1元, 03=50元, 04=20元, 05=10元, 06=1元, 07=2元, 08= 0.5元,
	 * 09=0.2元, 10=0.1元
	 */
	private int bagType;
	/**
	 * 清分/复点类型 : 0=未复点, 1=已复点, 2=未清分, 3=已清分
	 */
	private int clearingType = -1;
	/**
	 * nfc的uid
	 */
	private String uid;
	/**
	 * 袋型号 名称: 100元
	 */
	private String bagTypeName;
	/**
	 * 清分/复点名称: 未复点
	 */
	private String clearingName;

	/**
	 * @return 袋型号 : 04 , 05
	 */
	public int getBagVersion() {
		return bagVersion;
	}

	public void setBagVersion(int bagVersion) {
		this.bagVersion = bagVersion;
	}

	/**
	 * @return 机构号
	 */
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	/**
	 * @return 是完整券 返回true
	 */
	public boolean isIntegrity() {
		return isIntegrity;
	}

	public void setIntegrity(boolean isIntegrity) {
		this.isIntegrity = isIntegrity;
	}

	/**
	 * 01=100元, 02=1元, 03=50元, 04=20元, 05=10元, 06=1元, 07=2元, 08= 0.5元, 09=0.2元,
	 * 10=0.1元
	 * 
	 * @return 袋型号 :
	 */
	public int getBagType() {
		return bagType;
	}

	/**
	 * 01=100元, 02=1元, 03=50元, 04=20元, 05=10元, 06=1元, 07=2元, 08= 0.5元, 09=0.2元,
	 * 10=0.1元
	 * @param bagType
	 */
	public void setBagType(int bagType) {
		this.bagType = bagType;
	}

	/**
	 * @return 清分/复点名称: 未复点
	 */
	public int getClearingType() {
		return clearingType;
	}
	/**
	 * 0=未复点, 1=已复点, 2=未清分, 3=已清分
	 * @param clearingType
	 */
	public void setClearingType(int clearingType) {
		this.clearingType = clearingType;
	}


	public String getUid() {
		return uid;
	}


	/**
	 * @return 袋型号 名称: 100元
	 */
	public String getBagTypeName() {
		if (BAG_TYPE_NAME.length < bagType || bagType < 1) {
			return null;
		}
		return BAG_TYPE_NAME[bagType];
	}

	/**
	 * @return 袋型号 名称: 100元
	 */
	public String getClearingName() {
		if (BAG_CLEARING_NAME.length < clearingType || clearingType < 0) {
			return null;
		}
		return BAG_CLEARING_NAME[clearingType];
	}

}
