package com.mvp.pilecreate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.DatabaseUtils;
import android.os.Handler;
import android.util.Log;

import com.db.DBService;
import com.db.PileInfoDao;
import com.entity.PileInfo;
import com.entity.Response;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.hardwareAction.NfcOperation;
import com.fanfull.utils.DateUtils;
import com.fanfull.utils.LogsUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mvp.pilecreate.PileCreateContract.Presenter;
import com.mvp.pilecreate.PileCreateContract.View;
import com.net.MessageListener;
import com.net.Net;

public class PileCreatePresenter implements Presenter {
	private View mPileCreateView;
	private Net mNet;
	private DBService mDb;
	private Handler mHandler = new Handler();

	public PileCreatePresenter(View v) {
		this.mPileCreateView = v;
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}

	@Override
	public void create(final String bagModel, final String moneyType,
			final String moneyModel, String storeNum) {
		if (mNet.isWifiConnected()) {
			// 有网情况下的处理
			String[] keys = { "moneyType", "moneyModel", "bagModel", "cardID",
					"storeID" };
			String[] values = { moneyType, moneyModel, bagModel,
					StaticString.userId, storeNum };
			mNet.get(Net.CREATE_PILE, keys, values, new MessageListener() {

				@Override
				public void onSuccess(String msg) {
					try {
						JSONObject object1 = new JSONObject(msg);
						boolean isSuccess = (Boolean) object1.get("success");
						if (isSuccess) {

							JSONObject object = new JSONObject(object1.get(
									"result").toString());
							int pileID = (Integer) object.get("pileID");
							String pileName = (String) object.get("pileName");
							String time = (String) object.get("time");
							String serias = (String) object.get("series");
							insert("" + pileID, pileName, "" + time, moneyType,
									moneyModel, bagModel, serias);
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mPileCreateView.createPileSuccess();
								}
							});

						} else {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mPileCreateView.createPileFailure();
								}
							});
						}
					} catch (JSONException e) {
						Log.e("presenter", "数据解析失败");
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mPileCreateView.createPileFailure();
						}
					});
				}
			});
		} else {
			postError("网络未连接");
		}
	}

	@Override
	public void getStoreNum(String userId) {
		mNet.get(Net.PATH_GET_STROENUM_LIST, "cardID", userId,
				new MessageListener() {
					@Override
					public void onSuccess(String msg) {
						final Map<String, Integer> storeMap;
						try {
							JSONObject root = new JSONObject(msg);
							String success = root.getString("success");
							if ("true".equalsIgnoreCase(success)) {
								Object object = root.getJSONArray("result");
								if (!(object instanceof JSONArray)) {
									postError("回复数据异常");
								}
								JSONArray jsonArray = null;
								storeMap = new HashMap<String, Integer>();
								try {
									// jsonArray = new
									// JSONArray(msg.obj.toString());
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
											mPileCreateView
													.onGetStoreNumResult(storeMap);
										}
									});
								} catch (JSONException e1) {
									e1.printStackTrace();
									Log.e("presenter", "数据解析异常");
									postError("回复数据异常");
								}
							} else {
								postError("回复数据异常");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mPileCreateView.onGetStoreNumResult(null);
							}
						});
					}
				});
	}

	@Override
	public void scan() {
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {

				byte[] bagId = null;

				long startTime = System.currentTimeMillis();

				while ((System.currentTimeMillis() - startTime) < 3000) {
					bagId = NfcOperation.getInstance().reaAddrToByte(0x04);
					if (null != bagId) {
						break;
					}
					startTime = System.currentTimeMillis();
				}
				// mPileCreateView.onScanResult(bagId);
			}
		});
	}

	private void insert(String pileID, String pileName, String time,
			String moneyType, String moneyModel, String bagModel, String serias) {
		PileInfo pileInfo = new PileInfo();
		pileInfo.setBagNum(0);
		if (pileID.contains(".0")) {
			int s = (int) Double.parseDouble(pileID);
			pileID = String.valueOf(s);
		}
		pileInfo.setPileID(pileID);
		pileInfo.setPileName(pileName);
		if (moneyModel.equals("清分")) {
			moneyModel = "已清分";
		} else if (moneyModel.equals("复点")) {
			moneyModel = "已复点";
		}
		pileInfo.setMoneyType(moneyType + "|" + moneyModel);
		pileInfo.setBagType(bagModel);
		pileInfo.setRefresh_flag(0);
		pileInfo.setScreenNum(0);
		pileInfo.setTotalAmount("0");
		pileInfo.setSeries(serias);
		pileInfo.setUpdate_time(DateUtils.getFormatDate());
		pileInfo.setTime(time);
		pileInfo.setSerialNum("null");
		PileInfoDao dao = DBService.getService().getPileInfoDao();
		dao.insert(pileInfo);
	}

	private void postError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mPileCreateView.onFailure(error);
			}
		});
	}
}
