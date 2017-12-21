package com.mvp.center;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import com.google.gson.reflect.TypeToken;
import com.net.MessageListener;
import com.net.Net;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

public class NetReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("网络监听器", "网络变化");
		if (getActiveNetwork(context) != null) {
			upload();
			Log.i("网络监听器", "开始上传数据");
		}
	}

	public static NetworkInfo getActiveNetwork(Context context) {
		if (context == null)
			return null;
		ConnectivityManager mConnMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnMgr == null)
			return null;
		NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
		return aActiveInfo;
	}

	private Net mNet = Net.getInstance();
	//private AlertDialog dialog;

	public void upload() {
		/*AlertDialog.Builder builder = new AlertDialog.Builder(
				BaseApplication.getContext());
		dialog = builder.setMessage("正在上传数据...")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				}).create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setCanceledOnTouchOutside(false);// 点击屏幕不消失
		if (!dialog.isShowing()) {// 此时提示框未显示
			dialog.show();
		}*/

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("数据上传检测开始");
				upload1();
				SystemClock.sleep(1000 * 2);
				upload2();
			}
		});
		thread.start();
	}

	List<Map<String, Object>> lastInfo = new ArrayList<Map<String, Object>>();

	private void upload1() {
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
						response = gson.fromJson(msg, Response.class);
						if (response.isSuccess()) {
							FileUtils.deleteFile("commitFile");
						}
						Map<String, Object> s = gson.fromJson(response
								.getResult().toString(),
								new TypeToken<Map<String, Object>>() {
								}.getType());
						List<Map<String, Object>> info = gson.fromJson(
								s.get("lastInfo").toString(),
								new TypeToken<List<Map<String, Object>>>() {
								}.getType());
						if (info != null && info.size() != 0) {
							lastInfo.addAll(info);
							update();
						} else {
							//dialog.dismiss();
						}
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

	private void upload2() {
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
					Gson gson = new Gson();
					Response response = gson.fromJson(msg, Response.class);
					Map<String, Object> s = gson.fromJson(response.getResult()
							.toString(), new TypeToken<Map<String, Object>>() {
					}.getType());
					List<Map<String, Object>> info = gson.fromJson(
							s.get("lastInfo").toString(),
							new TypeToken<List<Map<String, Object>>>() {
							}.getType());
					if (info != null && info.size() != 0) {
						lastInfo.addAll(info);
						update();
					} else {
						//dialog.dismiss();
					}
				}

				@Override
				public void onFailure() {
					//dialog.dismiss();
					// System.out.println("暂无网络");
				}
			});
		} else {
			//dialog.dismiss();
		}
	}

	private void update() {
		
		if (lastInfo.size() != 0) {
			Gson gson = new Gson();

			mNet.upload(Net.DATA_DOWNLOAD, gson.toJson(lastInfo),
					new MessageListener() {

						@Override
						public void onSuccess(String msg) {
						//	dialog.dismiss();
							Gson gson = new Gson();
							Response response = gson.fromJson(msg,
									Response.class);

							Map<String, Object> map = gson.fromJson(response
									.getResult().toString(),
									new TypeToken<Map<String, Object>>() {
									}.getType());
							
							List<BagInfo> bagInfoList = gson.fromJson(map.get("bag").toString(),
									new TypeToken<List<BagInfo>>() {
									}.getType());
							
							List<ScreenInfo> screens = gson.fromJson(map.get("screen").toString(),
									new TypeToken<List<ScreenInfo>>() {
									}.getType());
							
							List<PileInfo> pile = gson.fromJson(map.get("pile").toString(),
									new TypeToken<List<PileInfo>>() {
									}.getType());
							
							savaData(bagInfoList);
							savaScreen(screens);
							savePile(pile);
							String msg1 = "";
							/*for(PileInfo pileName:pile){
								msg1+= pileName.getPileName()+" ";
							}*/
							//dialog.setMessage("请刷新屏:"+msg1);
							//dialog.show();
						}

						@Override
						public void onFailure() {

						}
					});
		} else {
			//dialog.dismiss();
		}
	}

	private void savaData(List<BagInfo> bagInfoList) {
		BagInfoDao mBagInfoDao = DBService.getService().getBagInfoDao();
		mBagInfoDao.insertOrReplaceInTx(bagInfoList);
	}
	
	private void savaScreen(List<ScreenInfo> screenInfoList){
		ScreenInfoDao mScreenInfoDao = DBService.getService().getScreenInfoDao();
		mScreenInfoDao.insertOrReplaceInTx(screenInfoList);
	}
	
	private void savePile(List<PileInfo> pileInfoList){
		PileInfoDao mPileInfoDao = DBService.getService().getPileInfoDao();
		mPileInfoDao.insertOrReplaceInTx(pileInfoList);
	}
}
