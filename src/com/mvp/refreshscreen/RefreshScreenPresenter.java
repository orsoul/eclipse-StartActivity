package com.mvp.refreshscreen;

import java.util.List;

import org.greenrobot.greendao.query.QueryBuilder;

import android.os.Handler;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.PileInfoDao;
import com.db.ScreenInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.Response;
import com.entity.ScreenInfo;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.SoundUtils;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.baglink.usercase.ScanBagTask;
import com.mvp.refreshscreen.RefreshScreenContract.Presenter;
import com.mvp.refreshscreen.RefreshScreenContract.View;
import com.mvp.refreshscreen.usecase.LightTask;
import com.mvp.refreshscreen.usecase.WriteScreenTask;
import com.mvp.screenlink.usecase.ScanScreenTask;
import com.net.MessageListener;
import com.net.Net;

public class RefreshScreenPresenter implements Presenter {
	private View mRefreshScreenView;
	private Net mNet;
	private DBService mDb;
	private Handler mHandler = new Handler();

	public RefreshScreenPresenter(View view) {
		this.mRefreshScreenView = view;
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
						SoundUtils.playInitSuccessSound();
						mRefreshScreenView.scanBagSuccess(bagID);
					}
				});
				/*if (screenID != null) {
					getPileInfo();
				}*/
			}

			@Override
			public void failure(Object o) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mRefreshScreenView.scanBagFailure("");
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
						SoundUtils.playInitSuccessSound();
						mRefreshScreenView.scanScreenSuccess(screenID);
					}
				});
				if (bagID != null) {
					getPileInfo();
				}
			}

			@Override
			public void failure(Object o) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mRefreshScreenView.scanScreenFailure("扫描屏失败");
					}
				});
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScanTask);
	}

	/**
	 * 写屏信息
	 */
	@Override
	public void writeScreen() {
		if (pileInfo != null) {
			WriteScreenTask task = new WriteScreenTask(mRefreshScreenView,
					pileInfo.getBagType(), pileInfo.getMoneyType(),
					pileInfo.getTotalAmount(), pileInfo.getBagNum(),
					pileInfo.getRefresh_flag(), 1, screenID,
					pileInfo.getPileName(), pileInfo.getSeries(),
					pileInfo.getTime());
			ThreadPoolFactory.getNormalPool().execute(task);
		}
	}

	private PileInfo pileInfo;

	private void getPileInfo() {
		if (mNet.isWifiConnected()) {
			String[] keys = { "bagID", "screenID" };
			String[] values = { bagID, screenID };
			mNet.get(Net.SCREEN_DATA, keys, values, new MessageListener() {

				@Override
				public void onSuccess(String msg) {
					Gson gson = new Gson();
					Response response = gson.fromJson(msg, Response.class);
					if (response.isSuccess()) {
						System.out.println(response.getResult().toString());
						pileInfo = gson.fromJson(response.getResult()
								.toString(), PileInfo.class);
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mRefreshScreenView.refreshData(pileInfo);
							}
						});
						System.out.println(pileInfo.toString());
					} else {
						postError(response.getMsg());
					}
				}

				@Override
				public void onFailure() {
					postError("网络错误");
				}
			});
		} else {
			QueryBuilder<BagInfo> bagInfoQuery = mDb.getBagInfoDao()
					.queryBuilder()
					.where(BagInfoDao.Properties.BagID.eq(bagID));
			List<BagInfo> bagInfoList = bagInfoQuery.list();
			if (bagInfoList.size() != 0) {
				BagInfo bagInfo = bagInfoList.get(0);

				QueryBuilder<ScreenInfo> screenInfoQuery = mDb
						.getScreenInfoDao().queryBuilder()
						.where(ScreenInfoDao.Properties.ScreenID.eq(screenID));
				List<ScreenInfo> screenInfoList = screenInfoQuery.list();
				if (screenInfoList.size() != 0) {
					ScreenInfo screenInfo = screenInfoList.get(0);

					if (!bagInfo.getPileID().equals(screenInfo.getPileID())) {
						postError("屏位置错误");
					} else {
						String pileID = bagInfo.getPileID();
						QueryBuilder<PileInfo> pileInfoQuery = mDb
								.getPileInfoDao()
								.queryBuilder()
								.where(PileInfoDao.Properties.PileID.eq(pileID));
						pileInfo = pileInfoQuery.list().get(0);
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mRefreshScreenView.refreshData(pileInfo);
							}
						});
					}
				} else {
					postError("数据同步异常");
				}
			} else {
				postError("数据同步异常");
			}

		}
	}

	@Override
	public void light() {
		ThreadPoolFactory.getNormalPool().execute(new LightTask(screenID));
	}

	private void postError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mRefreshScreenView.error(error);
			}
		});
	}

}
