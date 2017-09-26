package com.mvp.hand;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;

import com.entity.RefreshScreen;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DateUtils;
import com.mvp.BaseListener;
import com.mvp.baglink1.usecase.ReadScreenInfoTask;
import com.mvp.hand.HandContract.Presenter;
import com.mvp.refreshscreen.RefreshScreenContract.View;
import com.mvp.refreshscreen.usecase.LightTask;
import com.mvp.refreshscreen.usecase.WriteScreenTask;

public class HandPresenter implements Presenter {
	private View mRefreshScreenView;
	private com.mvp.baglink1.BagLinkContract.View view;
	
	private Handler mHandler = new Handler();
	public HandPresenter(View mRefreshScreenView){
		this.mRefreshScreenView = mRefreshScreenView;
	}
	
	public HandPresenter(com.mvp.baglink1.BagLinkContract.View view){
		this.view = view;
	}
	
	private RefreshScreen value = new RefreshScreen();
	@Override
	public void readScreen() {
		ReadScreenInfoTask mScantask = new ReadScreenInfoTask(
				new BaseListener() {

					@Override
					public void result(Object o) {
						UHFOperation mUhfOperation = UHFOperation.getInstance();
						
						String screenInfo = (String) o;
						System.out.println("读取墨水屏数据："+screenInfo);

						value.setMoneyType(screenInfo.substring(0, 2));
						value.setModel(screenInfo.substring(2, 4));
						value.setMoneyDisplay1(screenInfo.substring(4, 12));
						value.setMoneyDisplay2(screenInfo.substring(12, 20));
						value.setDisplay_style(screenInfo.substring(20, 22));
						value.setDate(screenInfo.substring(22, 50));
						value.setUserID(screenInfo.substring(50, 76));
						value.setBagNum(screenInfo.substring(76, 82));
						value.setSeries(screenInfo.substring(82, 84));
						value.setMoneyModel(screenInfo.substring(84, 86));
						value.setPileName(screenInfo.substring(86, 92));
						value.setRefreshNum(screenInfo.substring(92, 94));
						value.setTime(screenInfo.substring(94, 122));
						
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								view.scanScreenSuccess("");	
							}
						});
					}

					@Override
					public void failure(Object o) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								view.scanFailure("扫描屏失败");
								
							}
						});
					}
				});
		ThreadPoolFactory.getNormalPool().execute(mScantask);

	}

	@Override
	public void writeScreen(String moneyTotal,int bagNum) {
		System.out.println(moneyTotal+" "+bagNum);
		WriteScreenTask task = new WriteScreenTask(view, value, bagNum,
				value.getRefreshNumber(), moneyTotal, "");
		ThreadPoolFactory.getNormalPool().execute(task);
	}

	@Override
	public void initScreen(String bagType, String moneyType,int version,String series) {
		System.out.println("test line"+bagType+" "+moneyType+" "+version+" "+series);
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String date = dataFormat.format(new Date());
		WriteScreenTask task = new WriteScreenTask(mRefreshScreenView, bagType,
				moneyType, "0", 0, version, 1, "", "RH01", series, date);
		ThreadPoolFactory.getNormalPool().execute(task);
	}

	@Override
	public void light() {
		ThreadPoolFactory.getNormalPool().execute(new LightTask(""));
	}

	
	
}
