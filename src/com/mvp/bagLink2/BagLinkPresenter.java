package com.mvp.bagLink2;

import java.io.File;
import java.util.List;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import android.os.Handler;
import android.util.Log;

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
import com.fanfull.utils.FileUtils;
import com.fanfull.utils.WiFiUtil;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.bagLink2.BagLinkContract.Presenter;
import com.mvp.bagLink2.BagLinkContract.View;
import com.mvp.baglink.usercase.ScanBagTask;
import com.net.MessageListener;
import com.net.Net;

public class BagLinkPresenter implements Presenter {
	private View mBagLinkView;
	private DBService mDb;
	private Handler mHandler = new Handler();
	private Net mNet;
	private boolean isOffline = false; // 是否采用纯离线方式，该方式只有当扫描堆袋时出现堆袋没有与堆关联时使用

	public BagLinkPresenter(View view) {
		this.mBagLinkView = view;
		mNet = Net.getInstance();
		mDb = DBService.getService();
	}

	private String trayBagID;
	private String pileID;
	private String trayID;
	private TrayInfo trayInfo;
	private String pileBagID1;
	private String pileBagID2;

	@Override
	public void scanTrayBag() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				trayBagID = (String) o;
				// 查询袋数据中是否存在托盘袋数据，并判断袋是否已经关联
				Query<BagInfo> query = mDb.getBagInfoDao().queryBuilder()
						.where(BagInfoDao.Properties.BagID.eq(trayBagID))
						.build();
				List<BagInfo> list = query.list();
				if (list != null && list.size() != 0) {
					if ((list.get(0).getPileID() == null)
							|| (list.get(0).getPileID().equals("null"))) { // 袋未关联
						trayID = list.get(0).getTrayID();
						Query<TrayInfo> query1 = mDb
								.getTrayInfoDao()
								.queryBuilder()
								.where(TrayInfoDao.Properties.TrayID.eq(trayID))
								.build();
						final List<TrayInfo> list1 = query1.list();
						if (list1 != null && list1.size() != 0) {
							trayInfo = list1.get(0);
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mBagLinkView.scanTrayBagSuccess(list1
											.get(0));
								}
							});
						} else {
							postTrayError("数据错误,请重新同步数据");
						}
					} else {
						postTrayError("该袋已关联");
					}

				} else {
					postTrayError("数据错误,请重新同步数据");
				}
			}

			@Override
			public void failure(Object o) {
				postTrayError("扫描失败");
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private void postTrayError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mBagLinkView.scanTrayBagFailure(error);
			}
		});
	}

	private PileInfo pileInfo1;
	private PileInfo pileInfo2;

	@Override
	public void scanPileBag() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				// 判断扫描的袋是否是堆袋
				String pileBagID = (String) o;
				pileBagID1 = pileBagID;
				Query<BagInfo> query = mDb.getBagInfoDao().queryBuilder()
						.where(BagInfoDao.Properties.BagID.eq(pileBagID))
						.build();
				List<BagInfo> list = query.list();
				if (list != null && list.size() != 0) {

					if ((list.get(0).getPileID() == null)
							|| (list.get(0).getPileID().equals("null"))) {
						isOffline = true;
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								mBagLinkView.scanPileBagSuccess(null);
							}
						});
						
						return;
					}

					// 判断托盘袋是否能够放到该堆中
					pileID = list.get(0).getPileID();
					Query<PileInfo> query1 = mDb.getPileInfoDao()
							.queryBuilder()
							.where(PileInfoDao.Properties.PileID.eq(pileID))
							.build();
					final List<PileInfo> list1 = query1.list();
					if (list1 != null && list1.size() != 0) {
						pileInfo1 = list1.get(0);
						if (pileInfo1.getMoneyModel().equals(
								trayInfo.getMoneyModel())
								&& pileInfo1.getBagType().equals(
										trayInfo.getBagType())
								&& pileInfo1.getMoneyType().equals(
										trayInfo.getMoneyType())) {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mBagLinkView.scanPileBagSuccess(list1
											.get(0));
								}
							});
						} else {
							postPileError("该托盘袋与堆类型不一致");
						}
					} else {
						postPileError("数据错误,请重新同步数据");
					}
				} else {
					postPileError("未同步数据或者托盘袋不能放置到该堆中");
				}
			}

			@Override
			public void failure(Object o) {
				postPileError("扫描失败");
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private void postPileError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mBagLinkView.scanPileBagFailure(error);
			}
		});
	}

	@Override
	public void link() {
		if (isOffline) {
			isOffline = false;
			saveInfo();
		} else {
			localLink();
		}
	}

	private void localLink() {
		Query<BagInfo> bagInfoQuery = mDb.getBagInfoDao().queryBuilder()
				.where(BagInfoDao.Properties.TrayID.eq(trayID)).build();
		List<BagInfo> bagInfoList = bagInfoQuery.list();
		for (int i = 0; i < bagInfoList.size(); i++) {
			bagInfoList.get(i).setPileID(pileID);
		}
		mDb.getBagInfoDao().updateInTx(bagInfoList); // 查询托盘数据用来更新堆数据
		/*QueryBuilder<TrayInfo> trayInfoQuery = mDb.getTrayInfoDao()
				.queryBuilder().where(TrayInfoDao.Properties.TrayID.eq(trayID));
		List<TrayInfo> trayInfoList = trayInfoQuery.list();
		TrayInfo tray = trayInfoList.get(0);*/
		int bagNum = trayInfo.getBagNum();
		String totalAmount = trayInfo.getTotalAmount();
		// 更新堆数据
		bagNum += pileInfo1.getBagNum();
		int num = (int) Double.parseDouble(totalAmount)
				+ (int) Double.parseDouble(pileInfo1.getTotalAmount());
		pileInfo1.setBagNum(bagNum);
		pileInfo1.setTotalAmount(String.valueOf(num));
		pileInfo1.setUpdate_time(DateUtils.getFormatDate());
		mDb.getPileInfoDao().update(pileInfo1);
		mBagLinkView.linkSuccess(new PileInfo());
	}

	@Override
	public void scanPileBagTwo() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				// 判断扫描的袋是否是堆袋
				String pileBagID = (String) o;
				pileBagID2 = pileBagID;
				if (!isOffline) {
					Query<BagInfo> query = mDb.getBagInfoDao().queryBuilder()
							.where(BagInfoDao.Properties.BagID.eq(pileBagID))
							.build();
					List<BagInfo> list = query.list();
					if (list != null && list.size() != 0) {

						if ((list.get(0).getPileID() == null)
								|| (list.get(0).getPileID().equals("null"))) {
							isOffline = true;
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									mBagLinkView.scanPileBagSuccess(null);
								}
							});
							return;
						}

						// 判断托盘袋是否能够放到该堆中
						pileID = list.get(0).getPileID();
						Query<PileInfo> query1 = mDb
								.getPileInfoDao()
								.queryBuilder()
								.where(PileInfoDao.Properties.PileID.eq(pileID))
								.build();
						final List<PileInfo> list1 = query1.list();
						if (list1 != null && list1.size() != 0) {
							pileInfo2 = list1.get(0);
							if (pileInfo1.getMoneyModel().equals(
									pileInfo2.getMoneyModel())
									&& pileInfo1.getBagType().equals(
											pileInfo2.getBagType())
									&& pileInfo1.getMoneyType().equals(
											pileInfo2.getMoneyType())) {
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										mBagLinkView.scanPileBagSuccess(list1
												.get(0));
									}
								});
							} else {
								postPileError("该托盘袋与堆类型不一致");
							}
						} else {
							postPileError("数据错误,请重新同步数据");
						}
					} else {
						postPileError("数据错误,请重新同步数据");
					}
				} else {
					isOffline = true;
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							mBagLinkView.scanPileBagSuccess(null);
						}
					});
				}
			}

			@Override
			public void failure(Object o) {
				postPileError("扫描失败");
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private void saveInfo() {
		File file = FileUtils.createFile("commitFile");
		String msg = "pileBagID1:" + pileBagID1 + " pileBagID2:" + pileBagID2
				+ " trayBagID:" + trayBagID + "\n";
		System.out.println("写入文件内容：" + msg);
		FileUtils.writeFileFromString(file, msg, true);
		mBagLinkView.linkSuccess(null);
	}
}
