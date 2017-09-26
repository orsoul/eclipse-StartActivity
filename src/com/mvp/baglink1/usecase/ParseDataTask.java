package com.mvp.baglink1.usecase;

import java.util.ArrayList;
import java.util.List;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.TrayInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 数据解析
 * 
 * @author root
 * 
 */
public class ParseDataTask implements Runnable {

	private String json;

	public ParseDataTask(String json) {
		this.json = json;
	}

	@Override
	public void run() {
		Gson gson = new Gson();
		List<Object> msg = gson.fromJson(json, new TypeToken<List<Object>>() {
		}.getType());
		List<BagInfo> bagInfoList = new ArrayList<BagInfo>();

		List<Data> data = gson.fromJson(msg.get(0).toString(),
				new TypeToken<List<Data>>() {
				}.getType());
		List<TrayInfo> trays = gson.fromJson(msg.get(1).toString(),
				new TypeToken<List<TrayInfo>>() {
				}.getType());
		for (Data d : data) {
			BagInfo bagInfo = new BagInfo();
			bagInfo.setBagID(d.bagID);
			bagInfo.setTrayID(d.trayID);
			bagInfoList.add(bagInfo);
		}
		savaData(bagInfoList);
		savaTray(trays);
	}

	private void savaData(List<BagInfo> bagInfoList) {
		BagInfoDao mBagInfoDao = DBService.getService().getBagInfoDao();
		mBagInfoDao.insertOrReplaceInTx(bagInfoList);
	}
	
	private void savaTray(List<TrayInfo> trayInfoList){
		TrayInfoDao mTrayInfoDao = DBService.getService().getTrayInfoDao();
		mTrayInfoDao.insertOrReplaceInTx(trayInfoList);
	}

	class Data {
		String bagID;
		String trayID;

		public String getBagID() {
			return bagID;
		}

		public void setBagID(String bagID) {
			this.bagID = bagID;
		}

		public String getTrayID() {
			return trayID;
		}

		public void setTrayID(String trayID) {
			this.trayID = trayID;
		}
	}

}
