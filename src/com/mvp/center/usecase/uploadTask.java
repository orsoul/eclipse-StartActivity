package com.mvp.center.usecase;

import org.greenrobot.greendao.query.QueryBuilder;

import android.content.Context;
import android.content.SharedPreferences;

import com.db.DBService;
import com.db.PileInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.fanfull.base.BaseApplication;
import com.net.Net;

/**
 * 数据上传
 * 
 * @author root
 *
 */
public class uploadTask implements Runnable{
	private DBService mDb;
	private Net mNet;

	public uploadTask(){
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}
	
	@Override
	public void run() {
		SharedPreferences preferences=BaseApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
		String update_time =preferences.getString("update_time", "0");
		QueryBuilder<PileInfo> pileInfoQuery=mDb.getPileInfoDao().queryBuilder().where(PileInfoDao.Properties.Update_time.gt(update_time));
	}

}
