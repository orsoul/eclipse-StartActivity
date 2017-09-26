package com.mvp.screenManage;

import android.os.Handler;

import com.db.DBService;
import com.entity.Response;
import com.entity.RootInfo;
import com.fanfull.factory.ThreadPoolFactory;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.screenManage.ScreenManageContract.View;
import com.mvp.screenlink.usecase.ScanScreenTask;
import com.net.MessageListener;
import com.net.Net;

public class ScreenManagePresenter implements ScreenManageContract.Presenter{
	private View mScreenManageView;
	private Net mNet;
	private DBService mDb;
	private Handler mHandler = new Handler();
	
	public ScreenManagePresenter(View view){
		mScreenManageView = view;
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}
	private String screenID;
	@Override
	public void scanScreen() {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			ScanScreenTask mTask = new ScanScreenTask(new BaseListener() {
				
				@Override
				public void result(Object o) {
					// TODO Auto-generated method stub
					screenID = String.valueOf(o);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanScreenSuccess(screenID);
						}
					});
				}
				
				@Override
				public void failure(Object o) {
					// TODO Auto-generated method stub
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanScreenFailure();
						}
					});
				}
			});
			ThreadPoolFactory.getNormalPool().execute(mTask);
		}else{
				mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mScreenManageView.onFailure("网络未连接");
				}
			});
		}
	}
	@Override
	public void scanStart(String screenID,String cardID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = {"screenID","cardID"}; 
			String[] values = {screenID,cardID};
			mNet.get(Net.PATH_SCAN_START, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					final Response rInfo = new Gson()
					.fromJson(msg, Response.class);	
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess(rInfo.getMsg());
						}
					});
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess("墨水瓶启用失败");
						}
					});
				}
			});
		}else{
				mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mScreenManageView.onFailure("网络未连接");
				}
			});
		}
	}
	@Override
	public void scanStop(String screenID,String cardID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = {"screenID","cardID"}; 
			String[] values = {screenID,cardID};
			mNet.get(Net.PATH_SCAN_STOP, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					final Response rInfo = new Gson()
					.fromJson(msg, Response.class);	
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess(rInfo.getMsg());
						}
					});
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess("墨水瓶停用失败");
						}
					});
				}
			});
		}else{
				mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mScreenManageView.onFailure("网络未连接");
				}
			});
		}
	}
	@Override
	public void scanRegister(String screenID,String cardID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = {"screenID","cardID"}; 
			String[] values = {screenID,cardID};
			mNet.get(Net.PATH_SCAN_REGISTER, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					final Response rInfo = new Gson()
					.fromJson(msg, Response.class);	
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess(rInfo.getMsg());
						}
					});
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess("墨水瓶注册失败");
						}
					});
				}
			});
		}else{
				mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mScreenManageView.onFailure("网络未连接");
				}
			});
		}
	}
	@Override
	public void scanCancle(String screenID,String cardID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = {"screenID","cardID"}; 
			String[] values = {screenID,cardID};
			mNet.get(Net.PATH_SCAN_CANCLE, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					final Response rInfo = new Gson()
					.fromJson(msg, Response.class);	
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess(rInfo.getMsg());
						}
					});
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mScreenManageView.scanSuccess("墨水瓶注销失败");
						}
					});
				}
			});
		}else{
				mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mScreenManageView.onFailure("网络未连接");
				}
			});
		}
	}
}
