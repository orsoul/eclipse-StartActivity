package com.mvp.pilecreate2;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.db.DBService;
import com.db.PileInfoDao;
import com.entity.PileInfo;
import com.entity.Response;
import com.fanfull.utils.DateUtils;
import com.google.gson.Gson;
import com.mvp.pilecreate2.PileCreateTwoContract.View;
import com.net.MessageListener;
import com.net.Net;

public class PileCreateTwoPresenter implements PileCreateTwoContract.Presenter{
	private View mPileCreateTwoView;
	private Net mNet;
	private DBService mDb;
	private PileInfoDao pileInfoDao;
	private Handler mHandler = new Handler();
	public PileCreateTwoPresenter(View view){
		this.mPileCreateTwoView = view;
		mNet = Net.getInstance();
		mDb = DBService.getService();
		pileInfoDao = DBService.getService().getPileInfoDao();
	}
	@Override
	public void createStore(final String moneyType, final String moneyModel,
			final String bagModel, String cardID, String storeID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			if(storeID.contains(".")){
				storeID = storeID.substring(0,storeID.indexOf("."));
			}
			String[] keys = {"moneyType","moneyModel","bagModel","cardID","storeID"}; 
			String[] values = {moneyType,moneyModel,bagModel,cardID,storeID};
			mNet.get(Net.CREATE_PILE, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					Response response = new Gson()
					.fromJson(msg, Response.class);
					if (response.isSuccess()) {
				
						PileInfo pileInfo = new Gson().fromJson(response.getResult().toString(), PileInfo.class);
						pileInfoDao.insert(pileInfo);
						mHandler.post(new Runnable() {	
							@Override
							public void run() {
								mPileCreateTwoView.onCreateSuccess();
							}
						});
				
					} else {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								mPileCreateTwoView.onCreateFailure();
							}
						});
					}
						}
						
						@Override
				public void onFailure() {
					// TODO Auto-generated method stub
					mPileCreateTwoView.onCreateFailure();
				}
			});	
		}else{
				mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mPileCreateTwoView.onConnectionError("网络未连接");
				}
			});
		}
	}
}
