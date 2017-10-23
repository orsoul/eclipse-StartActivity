package com.mvp.refreshscreen.usecase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.os.Handler;

import com.entity.RefreshScreen;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.SoundUtils;
import com.mvp.refreshscreen.RefreshScreenContract.View;

public class WriteScreenTask implements Runnable {

	private String moneyType;
	private String model;
	private String moneyTotal;
	private String date;
	private String bagNum;
	private String refreshNum;
	private String userID = "0000000000000"; // 工号

	private int screenType;
	/**
	 * 显示样式
	 */
	private String display_style = "01";

	private String[] moneyDisplay;
	String mSreenEPC;

	private String pileName;
	/**
	 * 第几套人民币
	 */
	private String series;

	/**
	 * 最早存放日期
	 */
	private String time;

	private View mRefreshScreenView;
	private com.mvp.baglink1.BagLinkContract.View mBagLinkView;
	/**
	 * 作为刷新方式的区分
	 */
	int style;

	private Handler mHandler = new Handler();

	/**
	 * 
	 * @param moneyType
	 *            券别：0.1、0.5、1、5、10、20、50、100
	 * @param model
	 *            类型：已清分、未清分、已复点、未复点
	 * @param moneyTotal
	 *            总金额
	 * @param bagNum
	 *            袋数
	 * @param refreshNum
	 *            刷新次数
	 * @param screenType
	 *            屏类型、黑底白字0/白底黑字1
	 */
	public WriteScreenTask(View mRefreshScreenView, String moneyType,
			String model, String moneyTotal, int bagNum, int refreshNum,
			int screenType, String mSreenEPC, String pileName, String series,
			String time) {
		this.mRefreshScreenView = mRefreshScreenView;
		this.moneyType = moneyType;
		this.model = model;
		this.moneyTotal = moneyTotal;
		this.bagNum = String.valueOf(bagNum);
		/*if (refreshNum % 100 < 10) {
			this.refreshNum = "0" + (refreshNum % 100);
		} else {
			this.refreshNum = "" + (refreshNum % 100);
		}*/
		if (refreshNum % 100 < 16) {
			this.refreshNum = "0" + (Long.toHexString(refreshNum % 100));
		} else {
			this.refreshNum = Long.toHexString(refreshNum % 100);
		}
		this.screenType = screenType;
		this.mSreenEPC = mSreenEPC;
		this.pileName = pileName;
		this.series = "0" + series;
		this.time = time;
		style = 0;
		init();
	}

	public WriteScreenTask(com.mvp.baglink1.BagLinkContract.View view,
			RefreshScreen refreshScreen, int bagNum, int refreshNum,
			String moneyTotal, String mSreenEPC) {
		this.mBagLinkView = view;
		this.moneyType = refreshScreen.getMoneyType();
		this.model = refreshScreen.getModel();
		this.moneyTotal = moneyTotal;
		this.bagNum = String.valueOf(bagNum);
/*		if (refreshNum % 100 < 10) {
			this.refreshNum = "0" + (refreshNum % 100);
		} else {
			this.refreshNum = "" + (refreshNum % 100);
		}*/
		if (refreshNum % 100 < 16) {
			this.refreshNum = "0" + (Long.toHexString(refreshNum % 100));
		} else {
			this.refreshNum = Long.toHexString(refreshNum % 100);
		}
		this.display_style = refreshScreen.getDisplay_style();
		this.mSreenEPC = mSreenEPC;
		this.pileName = refreshScreen.getPileName();
		this.series = refreshScreen.getSeries();
		
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		date = dataFormat.format(new Date());
		date = getTimeOrNumberString(date);
		this.time = refreshScreen.getTime();
		userID = refreshScreen.getUserID();
		if(moneyTotal.contains(".0")){
			moneyDisplay = getMoneyString(moneyTotal.replace(".0", ""));	
		}else{
			moneyDisplay = getMoneyString(moneyTotal);
		}
		
		this.bagNum = getNum(this.bagNum);
		style = 1;
	}

	private void init() {
		System.out.println("测试" + model);
		if (model.equals("残损|已复点")) {
			model = "01";
		} else if (model.equals("残损|未复点")) {
			model = "02";
		} else if (model.equals("残损|已清分")) {
			model = "03";
		} else if (model.equals("残损|未清分")) {
			model = "04";
		} else if (model.equals("完整|已复点")) {
			model = "05";
		} else if (model.equals("完整|未复点")) {
			model = "06";
		} else if (model.equals("完整|已清分")) {
			model = "07";
		} else if (model.equals("完整|未清分")) {
			model = "08";
		}

		if (moneyType.contains("1角")) {
			moneyType = "01";
		} else if (moneyType.contains("2角")) {
			moneyType = "02";
		} else if (moneyType.contains("5角")) {
			moneyType = "03";
		} else if (moneyType.contains("1元")) {
			moneyType = "04";
		} else if (moneyType.contains("2元")) {
			moneyType = "05";
		} else if (moneyType.contains("5元")) {
			moneyType = "06";
		} else if (moneyType.contains("10元")) {
			moneyType = "07";
		} else if (moneyType.contains("20元")) {
			moneyType = "08";
		} else if (moneyType.contains("50元")) {
			moneyType = "09";
		} else if (moneyType.contains("100元")) {
			moneyType = "0A";
		} else {
			moneyType = "0A";
		}

		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		date = dataFormat.format(new Date());
		date = getTimeOrNumberString(date);
		time = getTimeOrNumberString(time);
		userID = getTimeOrNumberString(userID);

		if(moneyTotal.contains(".0")){
			moneyDisplay = getMoneyString(moneyTotal.replace(".0", ""));	
		}else{
			moneyDisplay = getMoneyString(moneyTotal);
		}
		bagNum = getNum(bagNum);
		pileName = getPileNameString(pileName);

	}

	@Override
	public void run() {
		UHFOperation mUhfOperation = UHFOperation.getInstance();
		mUhfOperation.setPower(20, 20, 1, 0, 0);
		mUhfOperation.open();
		int count = 0;
		while (20 > count++) {
			System.out.println("刷新" + count + "次");
			if (mUhfOperation.findOne()) {
				/** 目的是确保读到的是之前的那个超高频 */
				// LogsUtil.d("write id:"+ArrayUtils.bytesToHexString(mUhfOperation.mEPC).equals(mSreenEPC)+""+"----"+mSreenEPC);
				/*
				 * if(null == mSreenEPC ||
				 * !ArrayUtils.bytesToHexString(mUhfOperation
				 * .mEPC).equals(mSreenEPC)){ continue; }else {
				 */
				String wString = moneyType + model + moneyDisplay[0]
						+ moneyDisplay[1] + display_style + date + userID
						+ bagNum + series + "00" + pileName + refreshNum + time
						+ "00";
				System.out.println(wString);
				byte[] wByte = ArrayUtils.hexString2Bytes(wString);
				byte jSum = 0x0;
				for (int i = 0; i < wByte.length; i++) {
					jSum += wByte[i];
					System.out.println(i + ":" + wByte[i]);
				}
				byte[] wByte2 = { wByte[wByte.length - 2],
						wByte[wByte.length - 1], jSum, 0x55 };
				for (int i = 1; i < wByte2.length; i++) {
					System.out.println(i + ":" + wByte2[i]);
				}
				System.out.println("开始刷新,数据长度：" + wByte.length);
				if (mUhfOperation.writeUHF(mUhfOperation.mEPC,
						Arrays.copyOfRange(wByte, 0, 30), 1, 0x20, 3, 0x0)) {
					if (mUhfOperation.writeUHF(mUhfOperation.mEPC,
							Arrays.copyOfRange(wByte, 30, 60), 1, 0x20, 3, 0xF)) {
						if (mUhfOperation.writeUHF(mUhfOperation.mEPC, wByte2,
								1, 0x20, 3, 0x1E)) {

							// mHandler.sendEmptyMessage(WRITE_TID_SUCCESS);
							if (style == 0) {
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										mRefreshScreenView.writeScreenSuccess();
									}
								});
							} else {
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										mBagLinkView.writeScreenSuccess();
									}
								});
							}

							System.out.println("刷新成功");
							SoundUtils.playInitSuccessSound();
							return;
						} else {
							System.out.println("刷新失败3");
							continue;
						}
					} else {
						System.out.println("刷新失败2");
						continue;
					}
				} else {
					System.out.println("刷新失败1");
					continue;
				}
			}
		}
		if (style == 0) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mRefreshScreenView.error("刷新屏失败");
				}
			});
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mBagLinkView.writeScreenFailure();
				}
			});
		}
	}

	/**
	 * 得到总金额传到墨水屏的数据
	 * 
	 * @param str
	 * @return
	 */
	private String[] getMoneyString(String str) {
		StringBuilder strBuilder = new StringBuilder();
		StringBuilder strBuilder2 = new StringBuilder();
		String str1 = "11";
		String str2 = "12";
		if (null == str) {
			return new String[] { "00002710", "00000000" };
		}

		if (str.length() > 8) {
			str1 = str.substring(str.length() - 8, str.length());
			str2 = str.substring(0, str.length() - 8);
			strBuilder.append(Long.toHexString(Long.parseLong(str1)));
			strBuilder2.append(Long.toHexString(Long.parseLong(str2)));
			if (strBuilder.toString().length() < 8) {
				String tmp = "";
				for (int i = 0; i < 8 - strBuilder.toString().length(); i++) {
					tmp = tmp + "0";
				}
				strBuilder.insert(0, tmp);
			}

			if (strBuilder2.toString().length() < 8) {
				String tmp = "";
				for (int i = 0; i < 8 - strBuilder2.toString().length(); i++) {
					tmp = tmp + "0";
				}
				strBuilder2.insert(0, tmp);
			}

			if (Long.parseLong(str2) > 999) {
				strBuilder.replace(0, strBuilder.length(), "05F5E0FF");
				strBuilder2.replace(0, strBuilder2.length(), "000003E7");
			}
		} else {
			strBuilder.append(Long.toHexString(Long.parseLong(str)));
			String tmp = "";
			for (int i = 0; i < 8 - strBuilder.toString().length(); i++) {
				tmp = tmp + "0";
			}
			strBuilder.insert(0, tmp);
			strBuilder2.append("00000000");
		}
		System.out.println("1:" + strBuilder.toString());
		System.out.println("2:" + strBuilder2.toString());
		return new String[] { strBuilder2.toString(), strBuilder.toString() };
	}

	/**
	 * 得到时间和工号传递给墨水屏的数据
	 * 
	 * @param str
	 * @return
	 */

	private String getTimeOrNumberString(String str) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			strBuilder.append("3" + str.charAt(i));
		}
		return strBuilder.toString();
	}

	/**
	 * 补充袋数和刷新次数
	 * 
	 * @param str
	 * @return
	 */
	private String getNum(String str) {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(Long.toHexString(Long.parseLong(str)));
		if (strBuilder.toString().length() < 6) {
			String tmp = "";
			for (int i = 0; i < 6 - strBuilder.toString().length(); i++) {
				tmp = tmp + "0";
			}
			strBuilder.insert(0, tmp);
			str = strBuilder.toString();
		} else {
			str = "01869F";
		}
		return str;
	}

	private String getPileNameString(String s) {
		int one = getposition(s.charAt(0));
		int two = getposition(s.charAt(1));
		String one1;
		if (one < 10) {
			one1 = "0" + one;
		} else {
			one1 = "" + one;
		}

		String two1;
		if (two < 10) {
			two1 = "0" + two;
		} else {
			two1 = "" + two;
		}
		return one1 + two1 + s.substring(2);
	}

	private int getposition(char c) {
		String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		return s.indexOf(c);
	}
}
