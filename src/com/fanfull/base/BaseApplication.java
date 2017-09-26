package com.fanfull.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

import com.fanfull.background.CrashHandler;
import com.fanfull.contexts.MyContexts;
import com.fanfull.db.DaoMaster;
import com.fanfull.db.DaoMaster.OpenHelper;
import com.fanfull.db.DaoSession;

public class BaseApplication extends Application {
	private static Context context;
//	private static BaseApplication mInstance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	@Override
	public void onCreate() {
		super.onCreate();
//		mInstance = this;
		context = getApplicationContext();

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(context);
	}

	public static Context getContext() {
		return context;
	}

	/**
	 * 获取 版本名 
	 * @return manifest tag's versionName attribute
	 */
	public static String getVersionName() {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 手持 关机
	 * {@link http://blog.csdn.net/wulei202/article/details/9137509}
	 * @des 需要系统APP： android:sharedUserId="android.uid.system"
	 * @des 需要权限：uses-permission android:name="android.permission.SHUTDOWN" />
	 */
	public static void shutdown() {
		Intent newIntent = new Intent(
				"android.intent.action.ACTION_REQUEST_SHUTDOWN");
		newIntent.putExtra("android.intent.extra.KEY_CONFIRM", false);
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newIntent);
	}

	/**
	 * @param id
	 *            string.xml中字符串id
	 * @return 资源文件中 配置的字符串
	 */
	public static String getResString(int id) {
		return context.getResources().getString(id);
	}

	/**
	 * 取得DaoMaster
	 * 
	 * @param context
	 * @return
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context,
					MyContexts.DB_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * 取得DaoSession
	 * 
	 * @param context
	 * @return
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}
}
