package com.mvp.center;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.greenrobot.greendao.query.QueryBuilder;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.PileInfoDao;
import com.db.ScreenInfoDao;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.Response;
import com.entity.ScreenInfo;
import com.entity.TrayInfo;
import com.fanfull.base.BaseApplication;
import com.fanfull.contexts.MyContexts;
import com.fanfull.utils.FileUtils;
import com.fanfull.utils.SPUtils;
import com.google.gson.Gson;
import com.net.MessageListener;
import com.net.Net;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

public class UploadService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.i("UploadService", "上传服务开启");
		super.onCreate();
	}

	private Net mNet = Net.getInstance();
	//private AlertDialog dialog;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("数据上传检测开始");
				if (mNet.isWifiConnected()) {
					/*AlertDialog.Builder builder = new AlertDialog.Builder(
							getApplicationContext());
					dialog = builder
							.setMessage("正在上传数据")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
										}
									}).create();
					dialog.getWindow().setType(
							WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					dialog.setCanceledOnTouchOutside(false);// 点击屏幕不消失
					if (!dialog.isShowing()) {// 此时提示框未显示
						dialog.show();
					}*/
					upload1();
					SystemClock.sleep(1000 * 2);
					upload2();
				}
			}
		};
		Timer timer = new Timer();
		long delay = 1 * 1000;
		long intevalPeriod = 60 * 1000;
		// schedules the task to be run in an interval
		timer.scheduleAtFixedRate(task, delay, intevalPeriod);
		return super.onStartCommand(intent, flags, startId);
	}

	public void upload1() {
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
			if (content != null && !content.equals("")) {
				System.out.println(content);
				mNet.upload(Net.UPLOAD2, content, new MessageListener() {

					@Override
					public void onSuccess(String msg) {
						System.out.println("接收数据：" + msg);
						Gson gson = new Gson();
						Response response = new Response();
						//if (response.isSuccess()) {
							FileUtils.deleteFile("commitFile");
						//}
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

	public void upload2() {
		final BagInfoDao mBagInfoDao = DBService.getService().getBagInfoDao();
		final PileInfoDao mPileInfoDao = DBService.getService()
				.getPileInfoDao();
		final ScreenInfoDao mScreenInfoDao = DBService.getService()
				.getScreenInfoDao();
		final TrayInfoDao mTrayInfoDao = DBService.getService()
				.getTrayInfoDao();
		final boolean inkScreen = SPUtils.getBoolean(
				BaseApplication.getContext(), MyContexts.INK_SCREEN_DOWNLOAD,
				false);

		String update_time;
		if (inkScreen) {
			SharedPreferences preferences = BaseApplication.getContext()
					.getSharedPreferences("user", Context.MODE_PRIVATE);
			update_time = preferences.getString("update_time", "0");
		} else {
			update_time = "-1";
		}

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
		//TODO:Debug
		for(PileInfo i:pileInfoList){
			i.setTime("1");
		}
		
		List<ScreenInfo> screenInfoList = screenInfoQuery.list();
		List<TrayInfo> trayInfoList = trayInfoQuery.list();
		if (bagInfoList.size() != 0 || pileInfoList.size() != 0
				|| screenInfoList.size() != 0 || trayInfoList.size() != 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bagInfo", bagInfoList);
			map.put("pileInfo", pileInfoList);
			map.put("screenInfo", screenInfoList);
			map.put("trayInfo", trayInfoList);
			Gson gson = new Gson();
			String msg = gson.toJson(map);
			System.out.println(msg);
			mNet.upload(Net.UPLOAD, msg, new MessageListener() {

				@Override
				public void onSuccess(String msg) {
					//dialog.dismiss();
					Gson gson = new Gson();
					Response response = gson.fromJson(msg, Response.class);
					if (response.isSuccess()) {
						if (!inkScreen) {
							/*mBagInfoDao.deleteAll();
							mPileInfoDao.deleteAll();
							mScreenInfoDao.deleteAll();
							mTrayInfoDao.deleteAll();*/
						}
					}
				}

				@Override
				public void onFailure() {
					//dialog.dismiss();
					// System.out.println("暂无网络");
				}
			});
		}
	}
}
