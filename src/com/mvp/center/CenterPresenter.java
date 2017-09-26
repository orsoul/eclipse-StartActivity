package com.mvp.center;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.PileInfoDao;
import com.db.ScreenInfoDao;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.Response;
import com.entity.RootInfo;
import com.entity.ScreenInfo;
import com.entity.TrayInfo;
import com.fanfull.base.BaseApplication;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.SPUtils;
import com.google.gson.Gson;
import com.mvp.center.CenterContract.Presenter;
import com.mvp.center.CenterContract.View;
import com.mvp.center.usecase.ParseDataTask;
import com.mvp.center.usecase.ParseFileTask;
import com.net.MessageListener;
import com.net.Net;

public class CenterPresenter implements Presenter {
	private Net net;
	private View mCenterView;
	private Handler mHandler = new Handler();

	public CenterPresenter(View v) {
		mCenterView = v;
		net = Net.getInstance();
	}

	/**
	 * 检查更新状态
	 */
	@Override
	public void checkSyncStatus(Context context) {
		SharedPreferences preferences = BaseApplication.getContext()
				.getSharedPreferences("user", Context.MODE_PRIVATE);
		String update_time = preferences.getString("update_time", "0");
		String[] key = { "update_time", "cardID" };
		String[] values = { update_time, StaticString.userId };
		net.get(Net.CHECK_STATUS, key, values, new MessageListener() {
			Response response;
			Gson gson = new Gson();

			@Override
			public void onSuccess(String msg) {
				System.out.print(msg);
				response = gson.fromJson(msg, Response.class);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mCenterView.syncStatus(response);
					}
				});
			}

			@Override
			public void onFailure() {
				postError("网络错误");
			}
		});
	}

	@Override
	public void download() {
		boolean inkScreen = SPUtils.getBoolean(BaseApplication.getContext(),
				MyContexts.INK_SCREEN_DOWNLOAD, false);
		if(inkScreen){
			Log.i("下载", "全离线");
			SharedPreferences preferences = BaseApplication.getContext()
					.getSharedPreferences("user", Context.MODE_PRIVATE);
			String update_time = preferences.getString("update_time", "0");
			String[] keys = { "update_time", "cardID" };
			String[] values = { update_time, StaticString.userId };
			net.getFile(Net.DOWNLOAD, keys, values, new MessageListener() {

				@Override
				public void onSuccess(String msg) {
					ParseFileTask task = new ParseFileTask(msg, new TimeListener() {

						@Override
						public void onFinish(long time) {
							System.out.println("花费时间：" + time);
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mCenterView.syncSuccess();
								}
							});
						}

						@Override
						public void onFailure(int flag) {
							postError("文件解析错误"+flag);
						}
					});
					ThreadPoolFactory.getNormalPool().execute(task);
				}

				@Override
				public void onFailure() {
					postError("网络错误");
				}
			});
		}else{
			Log.i("下载", "半离线");
			net.get(Net.BAGS_DOWNLOAD, "cardID", StaticString.userId,
					new MessageListener() {

						@Override
						public void onSuccess(String msg) {
							Gson gson = new Gson();
							Response response = gson.fromJson(msg, Response.class);
							if (response.isSuccess()) {
								ParseDataTask task = new ParseDataTask(response
										.getResult().toString(),new TimeListener() {

									@Override
									public void onFinish(long time) {
										mHandler.post(new Runnable() {

											@Override
											public void run() {
												mCenterView.syncSuccess();
											}
										});
									}

									@Override
									public void onFailure(int flag) {
										postError("文件解析错误"+flag);
									}
								});
								ThreadPoolFactory.getNormalPool().execute(task);
							}else{
								mCenterView.syncSuccess();
							}
						}

						@Override
						public void onFailure() {
							postError("网络错误");
						}
					});
		}
		
	}

	private void postError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mCenterView.error(error);
			}
		});
	}

	@Override
	public void upload() {
		BagInfoDao mBagInfoDao = DBService.getService().getBagInfoDao();
		PileInfoDao mPileInfoDao = DBService.getService().getPileInfoDao();
		ScreenInfoDao mScreenInfoDao = DBService.getService()
				.getScreenInfoDao();
		TrayInfoDao mTrayInfoDao = DBService.getService().getTrayInfoDao();

		SharedPreferences preferences = BaseApplication.getContext()
				.getSharedPreferences("user", Context.MODE_PRIVATE);
		String update_time = preferences.getString("update_time", "0");
		// String update_time = "-1";
		QueryBuilder<BagInfo> bagInfoQuery = mBagInfoDao.queryBuilder().where(
				BagInfoDao.Properties.Update_time.gt(update_time));
		QueryBuilder<PileInfo> pileInfoQuery = mPileInfoDao.queryBuilder()
				.where(PileInfoDao.Properties.Update_time.gt(update_time));
		QueryBuilder<ScreenInfo> screenInfoQuery = mScreenInfoDao
				.queryBuilder().where(
						ScreenInfoDao.Properties.Update_time.gt(update_time));
		QueryBuilder<TrayInfo> trayInfoQuery = mTrayInfoDao.queryBuilder()
				.where(TrayInfoDao.Properties.Update_time.gt(update_time));

		List<BagInfo> bagInfoList = bagInfoQuery.list();
		List<PileInfo> pileInfoList = pileInfoQuery.list();
		List<ScreenInfo> screenInfoList = screenInfoQuery.list();
		List<TrayInfo> trayInfoList = trayInfoQuery.list();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bagInfo", bagInfoList);
		map.put("pileInfo", pileInfoList);
		map.put("screenInfo", screenInfoList);
		map.put("trayInfo", trayInfoList);
		Gson gson = new Gson();
		String msg = gson.toJson(map);
		System.out.println(msg);
		net.upload(Net.UPLOAD, msg, new MessageListener() {

			@Override
			public void onSuccess(String msg) {
				System.out.println(msg);
			}

			@Override
			public void onFailure() {
				System.out.println("网络错误");
			}
		});
	}

	@Override
	public void getPermission() {
		String[] keys = { "cardID" };
		String[] values = { StaticString.userId };
		net.get(Net.PATH_GET_USER_PRLEPERMISSION, keys, values,
				new MessageListener() {
					String result;

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						final Response response = new Gson().fromJson(msg,
								Response.class);
						if (response.isSuccess()) {
							try {
								JSONObject object = new JSONObject(response
										.getResult().toString());
								String moneyType = object
										.getString("moneyType");
								String moneyModel = object
										.getString("moneyModel");
								String bagModel = object.getString("bagModel");
								String storeID = String.valueOf(object
										.get("storeID"));
								String storeName = object
										.getString("storeName");
								result = bagModel + "--" + moneyType + "--"
										+ moneyModel + "--" + storeName + "--"
										+ storeID;

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									mCenterView.userSuccess(result);
								}
							});
						} else {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mCenterView.userError();
								}
							});
						}
					}

					@Override
					public void onFailure() {
						// TODO Auto-generated method stub
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mCenterView.userError();
							}
						});
					}
				});
	}

}
