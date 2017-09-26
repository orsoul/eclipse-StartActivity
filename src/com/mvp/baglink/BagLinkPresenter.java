package com.mvp.baglink;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import android.os.Handler;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.PileInfoDao;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.Response;
import com.entity.TrayInfo;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mvp.BaseListener;
import com.mvp.baglink.BagLinkContract.Presenter;
import com.mvp.baglink.BagLinkContract.View;
import com.mvp.baglink.usercase.ScanBagTask;
import com.net.MessageListener;
import com.net.Net;

public class BagLinkPresenter implements Presenter {
	private View mBagLinkView;
	private Net mNet;
	private DBService mDb;
	private Handler mHandler = new Handler();

	public BagLinkPresenter(View view) {
		this.mBagLinkView = view;
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}

	/**
	 * 扫描袋
	 */
	@Override
	public void scanBag() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				String bagID = (String) o;
				String bagType = "";
				String moneyType = "";
				final BagInfo bagInfo = new BagInfo();
				bagInfo.setBagID(bagID);
				// 检测网络连接情况
				if (mNet.isWifiConnected()) {
					mNet.get(Net.BAG_LINK, "bagID", bagID,
							new MessageListener() {

								@Override
								public void onSuccess(String msg) {
									Gson gson = new Gson();
									Response response = gson.fromJson(msg,
											Response.class);
									System.out.println(response.getResult()
											.toString());
									if (response.isSuccess()) {
										Type type = new TypeToken<ArrayList<PileInfo>>() {
										}.getType();

										final List<PileInfo> list = gson
												.fromJson(response.getResult()
														.toString(), type);
										mHandler.post(new Runnable() {

											@Override
											public void run() {
												mBagLinkView.scanBagSuccess(
														list, bagInfo);
											}
										});

									} else {
										final String msg1 = response.getMsg();
										if (msg1.equals("没有相关堆数据")) {
											mHandler.post(new Runnable() {

												@Override
												public void run() {
													mBagLinkView
															.scanBagSuccess(
																	null,
																	bagInfo);
												}
											});

										} else {
											mHandler.post(new Runnable() {

												@Override
												public void run() {
													mBagLinkView
															.scanBagFailure(msg1);
												}
											});

										}
									}
								}

								@Override
								public void onFailure() {
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											mBagLinkView.scanBagFailure("网络连接失败");
										}
									});
									
								}
							});
				} else {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mBagLinkView.scanBagFailure("网络未连接");
						}
					});
					
				}
			}

			@Override
			public void failure(Object o) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mBagLinkView.scanBagFailure("扫描袋锁失败");
					}
				});
				
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	@Override
	public void linkBagAndPile(BagInfo bagInfo, final PileInfo pileInfo) {
		final String bagID = bagInfo.getBagID();
		final String pileID = pileInfo.getPileID();
		if (mNet.isWifiConnected()) {
			String[] keys = { "bagID", "pileID" };
			String[] values = { bagID, pileID };
			mNet.get(Net.BAG_LINK_PILE, keys, values, new MessageListener() {

				@Override
				public void onSuccess(String msg) {
					Gson gson = new Gson();
					final Response response = gson.fromJson(msg, Response.class);
					if (response.isSuccess()) {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mBagLinkView.linkBagAndPileSuccess();
							}
						});
						
						localUpdate(bagID, pileID, pileInfo);
					} else {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mBagLinkView.linkBagAndPileFailure(response.getMsg());
							}
						});
						
					}
				}

				@Override
				public void onFailure() {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mBagLinkView.linkBagAndPileFailure("网络错误");
						}
					});
					
				}
			});
		} else {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mBagLinkView.linkBagAndPileFailure("网络未连接");
				}
			});
			
		}

	}

	private void localUpdate(String bagID, String pileID, PileInfo pileInfo) {
		QueryBuilder<BagInfo> bagInfoQuery = mDb.getBagInfoDao().queryBuilder()
				.where(BagInfoDao.Properties.BagID.eq(bagID));
		List<BagInfo> bagInfoList = bagInfoQuery.list();
		if (bagInfoList == null || bagInfoList.size() == 0) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					mBagLinkView.linkBagAndPileFailure("本地数据中没有查询到该袋，请在联网的情况下同步数据");
				}
			});
			
		} else {
			BagInfo bag = bagInfoList.get(0);
			if (!bag.getPileID().equals("null")) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mBagLinkView.linkBagAndPileFailure("该袋已关联堆");
					}
				});
			} else {
				String trayID = bag.getTrayID();
				// 将所有袋与堆关联
				bagInfoQuery = mDb.getBagInfoDao().queryBuilder()
						.where(BagInfoDao.Properties.TrayID.eq(trayID));
				bagInfoList = bagInfoQuery.list();
				for (int i = 0; i < bagInfoList.size(); i++) {
					bagInfoList.get(i).setPileID(pileID);
				}
				mDb.getBagInfoDao().updateInTx(bagInfoList);
				// 查询托盘数据用来更新堆数据
				QueryBuilder<TrayInfo> trayInfoQuery = mDb.getTrayInfoDao()
						.queryBuilder()
						.where(TrayInfoDao.Properties.TrayID.eq(trayID));
				List<TrayInfo> trayInfoList = trayInfoQuery.list();
				TrayInfo tray = trayInfoList.get(0);
				int bagNum = tray.getBagNum();
				String totalAmount = tray.getTotalAmount();
				// 更新堆数据
				bagNum += pileInfo.getBagNum();
				int num = (int) (Double.parseDouble(totalAmount) + Double
						.parseDouble(pileInfo.getTotalAmount()));
				pileInfo.setBagNum(bagNum);
				pileInfo.setTotalAmount(String.valueOf(num));
				pileInfo.setUpdate_time(DateUtils.getFormatDate());
				mDb.getPileInfoDao().update(pileInfo);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mBagLinkView.linkBagAndPileSuccess();
					}
				});
				
			}
		}
	}
}
