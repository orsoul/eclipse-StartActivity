package com.mvp.center.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.PileInfoDao;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.TrayInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mvp.center.TimeListener;

/**
 * 数据解析
 * 
 * @author root
 * 
 */
public class ParseDataTask implements Runnable {

	private String json;
	private TimeListener listener;

	public ParseDataTask(String json,TimeListener listener) {
		this.json = json;
		this.listener = listener;
	}

	@Override
	public void run() {
		try{
			Gson gson = new Gson();
			Map<String,Object> msg = gson.fromJson(json, new TypeToken<Map<String,Object>>() {
			}.getType());
			List<BagInfo> bagInfoList = gson.fromJson(msg.get("bags").toString(),
					new TypeToken<List<BagInfo>>() {
					}.getType());
			
			List<TrayInfo> trays = gson.fromJson(msg.get("trays").toString(),
					new TypeToken<List<TrayInfo>>() {
					}.getType());
			
			List<PileInfo> pile = gson.fromJson(msg.get("pile").toString(),
					new TypeToken<List<PileInfo>>() {
					}.getType());
			savaData(bagInfoList);
			savaTray(trays);
			savePile(pile);
			listener.onFinish(1);	
		}catch(Exception e){
			e.printStackTrace();
			listener.onFailure(1);
		}
		
	}

	private void savaData(List<BagInfo> bagInfoList) {
		Log.i("保存袋数据",""+bagInfoList.size());
		BagInfoDao mBagInfoDao = DBService.getService().getBagInfoDao();
		mBagInfoDao.insertOrReplaceInTx(bagInfoList);
	}
	
	private void savaTray(List<TrayInfo> trayInfoList){
		Log.i("保存托盘数据",""+trayInfoList.size());
		TrayInfoDao mTrayInfoDao = DBService.getService().getTrayInfoDao();
		mTrayInfoDao.insertOrReplaceInTx(trayInfoList);
	}
	
	private void savePile(List<PileInfo> pileInfoList){
		Log.i("保存堆数据",""+pileInfoList.size());
		PileInfoDao mPileInfoDao = DBService.getService().getPileInfoDao();
		mPileInfoDao.insertOrReplaceInTx(pileInfoList);
	}
}
