package com.mvp.screenlink;

import java.util.List;

import org.greenrobot.greendao.query.QueryBuilder;

import android.os.Handler;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.ScreenInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.Response;
import com.entity.ScreenInfo;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.DateUtils;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.baglink.usercase.ScanBagTask;
import com.mvp.screenlink.ScreenLinkContract.View;
import com.mvp.screenlink.usecase.ScanScreenTask;
import com.net.MessageListener;
import com.net.Net;

public class ScreenLinkPresenter implements ScreenLinkContract.Presenter{
	private View mScreenLinkView;
	private Net mNet;
	private DBService mDb;
	private Handler mHandler = new Handler();
	
	public ScreenLinkPresenter(View view){
		this.mScreenLinkView = view;
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}
	private String bagID;
	/**
	 * 扫描袋
	 */
	@Override
	public void scanBag() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {
			
			@Override
			public void result(Object o) {
				bagID = (String) o;
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mScreenLinkView.scanBagSuccess(bagID);
					}
				});
			}
			
			@Override
			public void failure(Object o) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mScreenLinkView.scanBagFailure("扫描袋失败");
					}
				});
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private String screenID;
	/**
	 * 扫描屏
	 */
	@Override
	public void scanScreen() {
		ScanScreenTask mScanTask = new ScanScreenTask(new BaseListener() {
			@Override
			public void result(Object o) {
				screenID = (String) o;
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mScreenLinkView.scanScreenSuccess(screenID);
					}
				});
			}
			
			@Override
			public void failure(Object o) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mScreenLinkView.scanScreenFailure();
					}
				});
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScanTask);
	}

	/**
	 * 屏关联
	 */
	@Override
	public void linkScreenAndPile() {
		if(mNet.isWifiConnected()){
			String[] keys = {"bagID","screenID","cardID"};
			String[] values = {bagID,screenID,StaticString.userId};
			mNet.get(Net.LINK_SCREEN, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					Gson gson = new Gson();
					Response response = gson.fromJson(msg, Response.class);
					if(response.isSuccess()){
						mScreenLinkView.linkScreenAndPileSuccess();
					}else{
						mScreenLinkView.linkScreenAndPileFailure("关联失败");
					}
				}
				
				@Override
				public void onFailure() {
					mScreenLinkView.linkScreenAndPileFailure("网络错误");
				}
			});
		}else{
			try{
				QueryBuilder<BagInfo> bagInfoQuery = mDb.getBagInfoDao()
						.queryBuilder()
						.where(BagInfoDao.Properties.BagID.eq(bagID));
				List<BagInfo> bagInfoList = bagInfoQuery.list();
				String pileID = bagInfoList.get(0).getPileID();
				
				QueryBuilder<ScreenInfo> screenInfoQuery = mDb.getScreenInfoDao()
						.queryBuilder()
						.where(ScreenInfoDao.Properties.ScreenID.eq(screenID));
				List<ScreenInfo> screenInfoList = screenInfoQuery.list();
				ScreenInfo screenInfo;
				if(screenInfoList!=null && screenInfoList.size()!=0){
					screenInfo = screenInfoList.get(0);
					screenInfo.setPileID(pileID);
					screenInfo.setUpdate_time(DateUtils.getFormatDate());
					mDb.getScreenInfoDao().update(screenInfo);
				}else{
					screenInfo = new ScreenInfo();
					screenInfo.setScreenID(screenID);
					screenInfo.setPileID(pileID);
					screenInfo.setInit(true);
					screenInfo.setIsUse(true);
					screenInfo.setRefresh_flag(0);
					screenInfo.setSerialNum("null");
					screenInfo.setUpdate_time(DateUtils.getFormatDate());
					mDb.getScreenInfoDao().insert(screenInfo);
				}
				
				mScreenLinkView.linkScreenAndPileSuccess();
			}catch(Exception e){
				e.printStackTrace();
				mScreenLinkView.linkScreenAndPileFailure("关联失败");
			}
			
		}
	}

}
