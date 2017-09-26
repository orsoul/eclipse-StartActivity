package com.mvp.baglink1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.greenrobot.greendao.query.QueryBuilder;

import android.os.Handler;
import android.util.Log;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.RefreshScreen;
import com.entity.Response;
import com.entity.TrayInfo;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.FileUtils;
import com.google.gson.Gson;
import com.mvp.BaseListener;
import com.mvp.baglink.usercase.ScanBagTask;
import com.mvp.baglink1.BagLinkContract.Presenter;
import com.mvp.baglink1.BagLinkContract.View;
import com.mvp.baglink1.usecase.ParseDataTask;
import com.mvp.baglink1.usecase.ReadScreenInfoTask;
import com.mvp.refreshscreen.usecase.LightTask;
import com.mvp.refreshscreen.usecase.WriteScreenTask;
import com.mvp.screenlink.usecase.ScanScreenTask;
import com.net.MessageListener;
import com.net.Net;

/**
 * Presenter层
 * 
 * @author root
 * 
 */
public class BagLinkPresenter implements Presenter {
	private Net mNet;
	private BagInfoDao bagDao;
	private TrayInfoDao trayDao;
	private View view;
	private Handler mHandler = new Handler();

	public BagLinkPresenter(View view) {
		mNet = Net.getInstance();
		bagDao = DBService.getService().getBagInfoDao();
		trayDao = DBService.getService().getTrayInfoDao();
		this.view = view;
	}

	@Override
	public void download() {
		mNet.get(Net.BAGS_DOWNLOAD, "cardID", "5AED84D4",
				new MessageListener() {

					@Override
					public void onSuccess(String msg) {
						Gson gson = new Gson();
						Response response = gson.fromJson(msg, Response.class);
						if (response.isSuccess()) {
							ParseDataTask task = new ParseDataTask(response
									.getResult().toString());
							ThreadPoolFactory.getNormalPool().execute(task);
						}
					}

					@Override
					public void onFailure() {
						// TODO Auto-generated method stub

					}
				});
	}

	/**
	 * 托盘中的总金额
	 */
	private int trayMoneyNum;
	private int trayBagNum;
	private String trayID;

	@Override
	public void scanTrayBag() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				final String bagID = (String) o;
				QueryBuilder<BagInfo> bagInfoQuery = bagDao.queryBuilder()
						.where(BagInfoDao.Properties.BagID.eq(bagID));
				List<BagInfo> list = bagInfoQuery.list();
				if (list != null && list.size() != 0) {
					BagInfo bagInfo = list.get(0);
					if (!bagInfo.getPileID().equals("null")) {
						postError("已关联堆");
					} else {
						QueryBuilder<TrayInfo> trayInfoQuery = trayDao
								.queryBuilder().where(
										TrayInfoDao.Properties.TrayID
												.eq(bagInfo.getTrayID()));
						List<TrayInfo> trayList = trayInfoQuery.list();
						if (trayList != null && trayList.size() != 0) {
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									view.scanTrayBagSuccess(bagID);
								}
							});

							TrayInfo trayInfo = trayList.get(0);
							trayMoneyNum = (int) Double.parseDouble(trayInfo
									.getTotalAmount());

							trayBagNum = trayInfo.getBagNum();
							trayID = trayInfo.getTrayID();
						} else {
							postError("未同步数据");
						}
					}
				} else {
					postError("该袋数据未同步");
				}
			}

			@Override
			public void failure(Object o) {
				postError("扫描袋失败");
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private String screenID;
	private RefreshScreen value = new RefreshScreen();

	@Override
	public void scanScreen() {
		ReadScreenInfoTask mScantask = new ReadScreenInfoTask(
				new BaseListener() {

					@Override
					public void result(Object o) {
						UHFOperation mUhfOperation = UHFOperation.getInstance();
						screenID = ArrayUtils
								.bytes2HexString(mUhfOperation.mEPC);

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
						//System.out.println("读取墨水屏数据："+value.toString());
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								view.scanScreenSuccess(screenID);
							}
						});

					}

					@Override
					public void failure(Object o) {
						postError((String) o);
					}
				});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private String pileBagID;

	@Override
	public void scanPileBag() {
		ScanBagTask mScantask = new ScanBagTask(new BaseListener() {

			@Override
			public void result(Object o) {
				final String bagID = (String) o;
				QueryBuilder<BagInfo> bagInfoQuery = bagDao.queryBuilder()
						.where(BagInfoDao.Properties.BagID.eq(bagID));
				List<BagInfo> list = bagInfoQuery.list();

				if (list != null && list.size() != 0) {
					if (!list.get(0).getPileID().equals("null")) {
						// 保存该值，做为出库后的比对数据
						pileBagID = bagID;
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								view.scanPileBagSuccess(bagID);
							}
						});

					} else {
						postError("该袋未关联堆，请扫描在堆中的袋");
					}
				} else {
					// 当数据库中没有该袋的数据时，保存该值，做为出库后的比对数据
					pileBagID = bagID;
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							view.scanPileBagSuccess(bagID);
						}
					});
				}
			}

			@Override
			public void failure(Object o) {
				postError("扫描袋失败");
			}
		});
		ThreadPoolFactory.getNormalPool().execute(mScantask);
	}

	private int moneyNum;
	private int bagNum;
	private int refreshNum;

	@Override
	public void count() {
		moneyNum = value.getMoneyNum() + trayMoneyNum;
		bagNum = value.getBagNumber() + trayBagNum;
		refreshNum = value.getRefreshNumber() + 1;
	}

	@Override
	public void refreshScreen() {
		if (value.getBagNum() != null) {
			count();
			WriteScreenTask task = new WriteScreenTask(view, value, bagNum,
					refreshNum, String.valueOf(moneyNum), screenID);
			ThreadPoolFactory.getNormalPool().execute(task);
		}
	}

	@Override
	public void saveInfo() {
		File file = FileUtils.createFile("commitFile");
		String msg = "pileBagID:" + pileBagID + " trayID:" + trayID;
		msg += " moneyTotal:" + moneyNum + " bagNum:" + bagNum + " refreshNum:"
				+ refreshNum + "\n";
		System.out.println("写入文件内容：" + msg);
		if (FileUtils.writeFileFromString(file, msg, true)) {
			System.out.println("写入成功");
		} else {
			System.out.println("写入失败");
		}
	}

	private void postError(final String error) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				view.scanFailure(error);
			}
		});
	}

	@Override
	public void light() {
		saveInfo();
		ThreadPoolFactory.getNormalPool().execute(new LightTask(screenID));
	}

	@Override
	public void upload() {
		File file = FileUtils.createFile("commitFile");
		BufferedReader reader = null;
		String temp = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			int line = 1;
			String content = "";
			while ((temp = reader.readLine()) != null) {
				// 显示行号
				content += temp + " \n";
			}

			reader.close();
			if (content != null) {
				System.out.println(content);
				mNet.upload(Net.UPLOAD2, content, new MessageListener() {

					@Override
					public void onSuccess(String msg) {
						System.out.println("接收数据：" + msg);
					}

					@Override
					public void onFailure() {
					}
				});
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

}
