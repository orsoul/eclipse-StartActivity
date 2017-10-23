package com.mvp.bagLink3;

import java.io.File;
import java.util.List;

import org.greenrobot.greendao.query.Query;

import android.database.DatabaseUtils;
import android.os.Handler;
import android.util.Log;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.Response;
import com.entity.TrayInfo;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.FileUtils;
import com.fanfull.utils.SPUtils;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.bagLink3.BagLinkContract.Presenter;
import com.mvp.bagLink3.BagLinkContract.View;
import com.mvp.baglink.usercase.ScanBagTask;
import com.net.MessageListener;
import com.net.Net;

public class BagLinkPresenter implements Presenter {
	private View mBagLinkView;

	/**
	 * 扫描到的袋信息
	 */
	private BagInfo bagInfo;
	/**
	 * 托盘信息
	 */
	private TrayInfo trayInfo;

	private Handler mHandler;
	private DBService mDb;
	private Net mNet;

	public BagLinkPresenter(View view) {
		this.mBagLinkView = view;
		mDb = DBService.getService();
		mHandler = new Handler();
		mNet = Net.getInstance();
	}

	@Override
	public void scanBag() {
		ScanBagTask task = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				String bagID = (String) o;
				// 检查本地数据库中是否存在扫描到的袋
				Query<BagInfo> query = mDb.getBagInfoDao().queryBuilder()
						.where(BagInfoDao.Properties.BagID.eq(bagID)).build();
				List<BagInfo> list = query.list();
				if (list == null || list.size() == 0) {
					postScanBagError("请下载最新数据");
					return;
				}
				bagInfo = list.get(0);
				// 查询托盘数据
				Query<TrayInfo> query1 = mDb
						.getTrayInfoDao()
						.queryBuilder()
						.where(TrayInfoDao.Properties.TrayID.eq(bagInfo
								.getTrayID())).build();
				List<TrayInfo> list1 = query1.list();
				if (list1 == null || list1.size() == 0) {
					postScanBagError("请下载最新数据");
					return;
				}
				trayInfo = list1.get(0);

				String result = SPUtils
						.getString(MyContexts.KEY_CREATE_PRESENTER);
				if (!"KEY_CREATE_PRESENTER".equals(result)
						&& !"".equals(result) && result != null) {
					Log.i("用户建堆权限", result);
					String[] permission = result.split("--");
					if (!(permission[0].equals(trayInfo.getBagType())
							&& permission[1].equals(trayInfo.getMoneyType()) && permission[2]
							.equals(trayInfo.getMoneyModel()))) {
						postScanBagError("用户没有申请建新堆权限，不能与新堆关联");
						return;
					}
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mBagLinkView.scanBagSuccess();
					}
				});
			}

			@Override
			public void failure(final Object o) {
				postScanBagError((String) o);
			}
		});
		ThreadPoolFactory.getNormalPool().execute(task);
	}

	@Override
	public void linkNewPile() {
		if(mNet.isWifiConnected()){
			String[] keys = {"cardID","bagID"};
			String[] values = {StaticString.userId, bagInfo.getBagID()};
			mNet.get(Net.NEW_PILE_LINK, keys, values, new MessageListener() {
				
				@Override
				public void onSuccess(String msg) {
					Gson gson = new Gson();
					Response response = gson.fromJson(msg, Response.class);
					if(response.isSuccess()){
						SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, null);
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								mBagLinkView.linkSuccess();	
							}
						});
					}else{
						postLinkError(response.getMsg());
					}
					
				}
				
				@Override
				public void onFailure() {
					postLinkError("网络请求失败");
				}
			});
		}else{
			//新建堆关联，离线方式处理
			File file = FileUtils.createFile("commitFile");
			String msg = "pileBagID1:root pileBagID2:root trayBagID:" + bagInfo.getBagID() + "\n";
			System.out.println("写入文件内容：" + msg);
			FileUtils.writeFileFromString(file, msg, true);
			SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, null);
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					mBagLinkView.linkSuccess();	
				}
			});
		}
	}

	private void postScanBagError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mBagLinkView.scanBagFailure(error);
			}
		});
	}
	
	private void postLinkError(final String error){
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mBagLinkView.linkFailure(error);
			}
		});
	}

}
