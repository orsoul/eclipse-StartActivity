package com.mvp.pileCreate1;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.db.DBService;
import com.entity.Response;
import com.entity.RootInfo;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.LogsUtil;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.baglink.usercase.ScanBagTask;
import com.mvp.pileCreate1.PileCreateOneContract.Presenter;
import com.mvp.pileCreate1.PileCreateOneContract.View;
import com.net.MessageListener;
import com.net.Net;

public class PileCreateOnePresenter implements Presenter {
	private View mPileCreateOneView;
	private Net mNet;
	private DBService mDb;
	private Handler mHandler = new Handler();
	protected String bagId;
	protected String result;

	public PileCreateOnePresenter(View v) {
		this.mPileCreateOneView = v;
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}

	@Override
	public void getPilePermission(String userID) {
		if (mNet.isWifiConnected()) {
			// TODO Auto-generated method stub
			String[] keys = { "cardID" };
			String[] values = { userID };
			mNet.get(Net.PATH_GET_PILEPERMISSION, keys, values,
					new MessageListener() {

						@Override
						public void onSuccess(String jsonStr) {
							// TODO Auto-generated method stub
							final RootInfo rInfo = new Gson().fromJson(jsonStr,
									RootInfo.class);
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mPileCreateOneView.createPileSuccess(rInfo);
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
									// mPileCreateOneView.createPileFailure();
									mPileCreateOneView.onFailure("检查权限失败");
								}
							});
						}
					});

		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mPileCreateOneView.onFailure("网络未连接");
				}
			});
		}
	}

	@Override
	public void getPileStore(String userID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = { "cardID" };
			String[] values = { userID };
			mNet.get(Net.PATH_GET_STROENUM_LIST, keys, values,
					new MessageListener() {

						@Override
						public void onSuccess(String msg) {
							// TODO Auto-generated method stub
							final Map<String, Integer> storeMap;
							Response response = new Gson().fromJson(msg,
									Response.class);
							if (response.isSuccess()) {
								try {
									JSONObject root = new JSONObject(msg);
									Object object = root.getJSONArray("result");
									JSONArray jsonArray = null;
									storeMap = new HashMap<String, Integer>();
									jsonArray = (JSONArray) object;
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject jsonObject = jsonArray
												.getJSONObject(i);
										String key = jsonObject
												.getString("storeCode")
												+ "-"
												+ jsonObject
														.getString("storeName");
										int value = jsonObject
												.getInt("storeID");
										storeMap.put(key, value);
									}
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											mPileCreateOneView
													.onGetStoreNumResult(storeMap);
										}
									});
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onFailure() {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mPileCreateOneView.onFailure("获取库房失败");
								}
							});

						}
					});

		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mPileCreateOneView.onFailure("网络未连接");
				}
			});
		}
	}

	@Override
	public void applyPermission(String cardID, String moneyType,
			String moneyModel, String bagModel, String storeID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = { "cardID", "moneyType", "moneyModel", "bagModel",
					"storeID" };
			String[] values = { cardID, moneyType, moneyModel, bagModel,
					storeID };
			mNet.get(Net.PATH_APPLY_PILEPERMISSION, keys, values,
					new MessageListener() {

						@Override
						public void onSuccess(String msg) {
							// TODO Auto-generated method stub
							final RootInfo rInfo = new Gson().fromJson(msg,
									RootInfo.class);
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											mPileCreateOneView
													.applyPermissionSuccess(rInfo
															.getMsg());
										}
									});

								}
							});
						}

						@Override
						public void onFailure() {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mPileCreateOneView.onFailure("申请权限失败");
								}
							});

						}
					});
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mPileCreateOneView.onFailure("网络未连接");
				}
			});
		}
	}

	@Override
	public void getBagID() {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			ScanBagTask mTask = new ScanBagTask(new BaseListener() {

				@Override
				public void result(Object o) {
					// TODO Auto-generated method stub
					bagId = String.valueOf(o);
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mPileCreateOneView.getBigIDSuccess(bagId);
								}
							});

						}
					});
				}

				@Override
				public void failure(Object o) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mPileCreateOneView.getBigIDError("扫描失败");
						}
					});

				}
			});
			ThreadPoolFactory.getNormalPool().execute(mTask);
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mPileCreateOneView.onFailure("网络未连接");
				}
			});
		}
	}

	@Override
	public void applyPermissionByBagID(String bagID, String cardID,
			String storeID) {
		// TODO Auto-generated method stub
		if (mNet.isWifiConnected()) {
			String[] keys = { "bagID", "cardID", "storeID" };
			String[] values = { bagID, cardID, storeID };
			mNet.get(Net.PATH_APPLY_PREMISSION_BAGID, keys, values,
					new MessageListener() {

						@Override
						public void onSuccess(String msg) {
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
									String bagModel = object
											.getString("bagModel");
									String storeID = String.valueOf(object
											.get("storeID"));
									String storeName = object
											.getString("storeName");
									result = bagModel + "--" + moneyType + "--"
											+ moneyModel + "--" + storeName
											+ "--" + storeID;

								} catch (JSONException e) {
									e.printStackTrace();
								}
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										mPileCreateOneView
												.applyPermissionSuccessByBigID(result);
									}
								});
							} else {
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										mPileCreateOneView
												.applyPermissionErrorByBigID();
									}
								});
							}
						}

						@Override
						public void onFailure() {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mPileCreateOneView.onFailure("权限申请失败");
								}
							});

						}
					});
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mPileCreateOneView.onFailure("网络未连接");
				}
			});
		}
	}

}
