package com.mvp.hand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.db.DBService;
import com.db.PileInfoDao;
import com.db.ScreenInfoDao;
import com.entity.PileInfo;
import com.entity.RefreshScreen;
import com.entity.Response;
import com.entity.ScreenInfo;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DateUtils;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.baglink1.usecase.ReadScreenInfoTask;
import com.mvp.hand.HandContract.Presenter;
import com.mvp.refreshscreen.RefreshScreenContract.View;
import com.mvp.refreshscreen.usecase.LightTask;
import com.mvp.refreshscreen.usecase.WriteScreenTask;
import com.net.MessageListener;
import com.net.Net;

public class HandPresenter implements Presenter {
	private View mRefreshScreenView;
	private com.mvp.baglink1.BagLinkContract.View view;
	private Net mNet;
	private com.mvp.hand.HandContract.View mHandView;

	private Handler mHandler = new Handler();

	public HandPresenter(View mRefreshScreenView,
			com.mvp.hand.HandContract.View view) {
		this.mRefreshScreenView = mRefreshScreenView;
		mNet = Net.getInstance();
		mHandView = view;
	}

	public HandPresenter(com.mvp.baglink1.BagLinkContract.View view) {
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
						System.out.println("读取墨水屏数据：" + screenInfo);

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

	WriteScreenTask task = null;
	String moneyTotal;
	int bagNum;

	@Override
	public void writeScreen(String moneyTotal, int bagNum) {
		System.out.println(moneyTotal + " " + bagNum);
		task = new WriteScreenTask(view, value, bagNum,
				value.getRefreshNumber(), moneyTotal, "");
		this.moneyTotal = moneyTotal;
		this.bagNum = bagNum;
		ThreadPoolFactory.getNormalPool().execute(task);
	}

	@Override
	public void initScreen(String bagType, String moneyType, int version,
			String series) {
		System.out.println("test line" + bagType + " " + moneyType + " "
				+ version + " " + series);
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String date = dataFormat.format(new Date());
		task = new WriteScreenTask(mRefreshScreenView, bagType, moneyType, "0",
				0, version, 1, "", pileName, series, time);
		ThreadPoolFactory.getNormalPool().execute(task);
	}

	@Override
	public void light() {
		if (task != null && task.mTid != null) {
			ThreadPoolFactory.getNormalPool().execute(new LightTask(task.mTid));
		} else {
			ThreadPoolFactory.getNormalPool().execute(new LightTask(""));
		}

	}

	@Override
	public void create(final String bagModel, final String moneyType,
			final String moneyModel, String storeNum) {
		this.bagModel = bagModel;
		this.moneyModel = moneyModel;
		this.moneyType = moneyType;
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

							System.out.println(object1.get("result").toString());
							JSONObject object = new JSONObject(object1.get(
									"result").toString());
							pileID = "" + (Integer) object.get("pileID");
							pileName = (String) object.get("pileName");
							time = (String) object.get("time");
							series = (String) object.get("series");

							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mHandView.createPileSuccess();
								}
							});

						} else {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mHandView.createPileFailure();
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
							mHandView.createPileFailure();
						}
					});
				}
			});
		} else {
			postError("网络未连接");
		}
	}

	String pileID, pileName, time, moneyType, moneyModel, bagModel, series;

	public void insert() {
		try {
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
			pileInfo.setSeries(series);
			pileInfo.setUpdate_time(DateUtils.getFormatDate());
			pileInfo.setTime(time);
			pileInfo.setSerialNum("null");
			PileInfoDao dao = DBService.getService().getPileInfoDao();
			dao.insert(pileInfo);

			ScreenInfo screenInfo = new ScreenInfo();
			screenInfo.setInit(true);
			screenInfo.setIsUse(true);
			screenInfo.setPileID(pileID);
			screenInfo.setRefresh_flag(0);
			screenInfo.setUpdate_time(DateUtils.getFormatDate());
			screenInfo.setScreenID(task.mTid);
			screenInfo.setSerialNum("null");
			Log.d("", screenInfo.toString());
			ScreenInfoDao screenDao = DBService.getService().getScreenInfoDao();
			screenDao.insertOrReplace(screenInfo);
		} catch (Exception e) {
			e.printStackTrace();
			postError("操作错误,请重新操作");
		}
	}

	public void update() {
		try {
			ScreenInfoDao screenDao = DBService.getService().getScreenInfoDao();
			QueryBuilder<ScreenInfo> screenInfoQuery = screenDao.queryBuilder()
					.where(ScreenInfoDao.Properties.ScreenID.eq(task.mTid));
			List<ScreenInfo> screenInfoList = screenInfoQuery.list();
			String pileID = null;
			if (screenInfoList != null && screenInfoList.size() != 0) {
				ScreenInfo screenInfo = screenInfoList.get(0);
				pileID = screenInfo.getPileID();
				screenInfo.setRefresh_flag(value.getRefreshNumber());
				screenInfo.setUpdate_time(DateUtils.getFormatDate());
				screenDao.update(screenInfo);
			}
			PileInfoDao dao = DBService.getService().getPileInfoDao();
			QueryBuilder<PileInfo> pileInfoQuery = dao.queryBuilder().where(
					PileInfoDao.Properties.PileID.eq(pileID));
			List<PileInfo> pileInfoList = pileInfoQuery.list();
			if (pileInfoList != null && pileInfoList.size() != 0) {
				PileInfo pileInfo = pileInfoList.get(0);
				pileInfo.setBagNum(this.bagNum);
				pileInfo.setTotalAmount(this.moneyTotal);
				pileInfo.setUpdate_time(DateUtils.getFormatDate());
				dao.update(pileInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			postError("操作错误,请重新操作");
		}
	}

	private void postError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mHandView.onFailure(error);
			}
		});
	}
}
