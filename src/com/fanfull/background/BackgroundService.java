package com.fanfull.background;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemClock;

import com.fanfull.activity.CheckUserInfoActivity;
import com.fanfull.activity.LoginActivity;
import com.fanfull.base.BaseApplication;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.WiFiUtil;

/**
 * 后台服务，开机后一直运行。监听 灭屏亮屏、退出APP、自动关机的广播
 * 
 * @author orsoul
 * 
 */
public class BackgroundService extends Service {
	private final String TAG = BackgroundService.class.getSimpleName();
	private final int REQUESTCODE = 10240;
	private PendingIntent pi;
	private IntentFilter filter;
	private MyBroadcastReceiver myReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		LogsUtil.w(TAG, TAG + " onCreate()");

		filter = new IntentFilter();

		// filter.addAction(Intent.ACTION_USER_PRESENT); // 屏幕解锁广播
		// filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

		filter.addAction(MyContexts.ACTION_EXIT_APP); // 退出 app 广播

		filter.addAction(MyContexts.ACTION_SHUT_DOWN); // 关闭 手持 广播

		filter.addAction(Intent.ACTION_SCREEN_OFF); // 灭屏广播

		filter.addAction(Intent.ACTION_SCREEN_ON); // 亮屏广播

		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 网络变化

		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // wifi 启用/停用

//		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); // wifi网络变化

		myReceiver = new MyBroadcastReceiver();
		registerReceiver(myReceiver, filter);

		// setScreenOff(false, 0);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogsUtil.w(TAG, "onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogsUtil.w(TAG, "onDestroy");
		unregisterReceiver(myReceiver);
		super.onDestroy();
		Intent servIceintent = new Intent(getApplicationContext(),
				BackgroundService.class);
		// servIceintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(servIceintent);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			LogsUtil.d(TAG, "action:" + action);

			if (Intent.ACTION_SCREEN_ON.equals(action)) {

				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				boolean requestRouteToHost = connectivityManager
						.requestRouteToHost(ConnectivityManager.TYPE_WIFI,
								WiFiUtil.StringIP2Int("192.168.11.174"));
				LogsUtil.d(TAG, "requestRouteToHost:" + requestRouteToHost);

				NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				LogsUtil.d(
						TAG,
						"wifiNetworkInfo:"
								+ networkInfo);

				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				LogsUtil.d(TAG, "SCREEN_ON: " + wifiInfo);
				
//				List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
//				LogsUtil.d(TAG, "WifiConfiguration:" + configuredNetworks);
//				WifiConfiguration wifiConfiguration = configuredNetworks.get(0);
				
				LogsUtil.d(TAG, "isWifiEnabled:" + wifiManager.isWifiEnabled());
				if (wifiManager.isWifiEnabled()) {
					if (null == networkInfo || !networkInfo.isConnectedOrConnecting()) {
						wifiManager.reconnect();
					}
				} else {
//					wifiManager.setWifiEnabled(true);
				}

				/* 收到 亮屏 广播，自动关机 停止计时 */
				stopAlarm();
				// 尝试 进行 复核登录
				startCheckActivity();
				// setScreenOff(true, 5000);
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				/* 收到 灭屏 广播，自动关机 开始计时 */
				startAlarm();
				OLEDOperation.getInstance().close();
				// WiFiUtil.disconnect(context);
				// setScreenOff(false, 5000);
			} else if (MyContexts.ACTION_EXIT_APP.equals(action)) {
				/* 收到 退出APP 广播 */
				exitApp();
			} else if (MyContexts.ACTION_SHUT_DOWN.equals(action)) {
				/* 收到 关闭手持 广播 */
				exitApp();
				BaseApplication.shutdown();
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

				/* 网络变化 */
				boolean no_connectivity = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				LogsUtil.d(TAG, "EXTRA_NO_CONNECTIVITY:" + no_connectivity);

				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				boolean requestRouteToHost = connectivityManager
						.requestRouteToHost(ConnectivityManager.TYPE_WIFI,
								WiFiUtil.StringIP2Int("192.168.11.174"));
				LogsUtil.d(TAG, "requestRouteToHost:" + requestRouteToHost);

				NetworkInfo networkInfo = connectivityManager
						.getActiveNetworkInfo();
				LogsUtil.d(TAG, "ActiveNetworkInfo: " + networkInfo);
				if (null == networkInfo) {
					// LogsUtil.d(TAG, "ActiveNetworkInfo == null");
				} else {
					// LogsUtil.d(TAG, "getTypeName:" +
					// networkInfo.getTypeName());
				}

				// NetworkInfo[] allNetworkInfo = connectivityManager
				// .getAllNetworkInfo();
				// for (int i = 0; i < allNetworkInfo.length; i++) {
				// LogsUtil.d(TAG, i + ") allNetworkInfo: " + networkInfo);
				// }

			} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				/* WiFi 停用/启用 变化 */
				int wifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE, -1);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_DISABLING:
					// LogsUtil.d(TAG, "正在关闭 WiFi " + wifiState);
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					LogsUtil.d(TAG, "已经关闭 WiFi " + wifiState);
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					// LogsUtil.d(TAG, "正在打开 WiFi " + wifiState);
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					LogsUtil.d(TAG, "已经打开 WiFi " + wifiState);
					break;
				case WifiManager.WIFI_STATE_UNKNOWN:
					LogsUtil.d(TAG, "WiFi未知状态 " + wifiState);
					break;
				default:
					LogsUtil.d(TAG, "获取WiFi状态失败");
					break;
				}
			} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				/* WiFi 连接变化 */
				Parcelable parcelable = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				LogsUtil.d(TAG, "WiFi NetworkInfo:" + parcelable);
				if (parcelable instanceof NetworkInfo) {
				}
			}
		}
	}

	/**
	 * 
	 * 打开 复核界面；需要符合如下条件： 1，已登录过；2，复核界面未在前台显示。
	 */
	private void startCheckActivity() {

		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);

		if (list != null && list.size() > 0) {

			ComponentName cpn = list.get(0).topActivity;

			LogsUtil.d(TAG, "topActivity:" + cpn.getClassName());
			LogsUtil.d(TAG, "numActivities:" + list.get(0).numActivities);
			LogsUtil.d(TAG, "numRunning:" + list.get(0).numRunning);

			String topActName = cpn.getClassName();

			// 复核登录
			if (null != StaticString.userId
					&& !topActName
							.equals(CheckUserInfoActivity.class.getName())
					&& !topActName.equals(LoginActivity.class.getName())
					&& 1 < list.get(0).numActivities) {

				Intent intent = new Intent(getApplicationContext(),
						CheckUserInfoActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(MyContexts.KEY_CHECK_LOGIN,
						CheckUserInfoActivity.LOGIN_AGAIN);
				startActivity(intent);
			}
		}
	}

	/*
	 * ❑ ELAPSED_REALTIME 在指定的延时过后，发送广播，但不唤醒设备。 ❑ ELAPSED_REALTIME_WAKEUP
	 * 在指定的演示后，发送广播，并唤醒设备 延时是要把系统启动的时间SystemClock.elapsedRealtime()算进去的，具体用法看代码。
	 * ❑ RTC 在指定的时刻，发送广播，但不唤醒设备 ❑ RTC_WAKEUP 在指定的时刻，发送广播，并唤醒设备
	 */
	/**
	 * 自动关机 计时开始；之前的计时会被取消
	 */
	private void startAlarm() {

		int minute = SPUtils.getInt(BaseApplication.getContext(),
				MyContexts.KEY_SHUTDOWN_DELAY, 30);
		if (0 == minute) {
			LogsUtil.w(TAG, "自动关机 未启用");
			return;
		}

		if (null == pi) {
			Intent intent = new Intent(MyContexts.ACTION_SHUT_DOWN);
			pi = PendingIntent.getBroadcast(BaseApplication.getContext(),
					REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}

		AlarmManager am = (AlarmManager) BaseApplication.getContext()
				.getSystemService(Context.ALARM_SERVICE);

		am.cancel(pi);//

		long triggerAtTime = SystemClock.uptimeMillis() + minute * 60 * 1000; // uptimeMillis()从启动到当前的时间

		am.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pi); // 设定的一次性闹钟，这里决定是否使用绝对时间

		LogsUtil.w(TAG, "关机计时开始（分钟）：" + minute);
	}

	/**
	 * 取消 自动关机 的计时
	 */
	private void stopAlarm() {
		// if (null == pi) {
		// Intent intent = new Intent(MyContexts.ACTION_EXIT_APP);
		// pi = PendingIntent.getBroadcast(BaseApplication.getContext(),
		// REQUESTCODE , intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// }
		/*
		 * ❑ ELAPSED_REALTIME 在指定的延时过后，发送广播，但不唤醒设备。 ❑ ELAPSED_REALTIME_WAKEUP
		 * 在指定的演示后，发送广播，并唤醒设备
		 * 延时是要把系统启动的时间SystemClock.elapsedRealtime()算进去的，具体用法看代码。 ❑ RTC
		 * 在指定的时刻，发送广播，但不唤醒设备 ❑ RTC_WAKEUP 在指定的时刻，发送广播，并唤醒设备
		 */
		AlarmManager am = (AlarmManager) BaseApplication.getContext()
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

	/**
	 * 退出 app
	 */
	private void exitApp() {
		ActivityUtil.getInstance().closeAllActivity();
	}

	/* 暂未使用 */
	@SuppressWarnings("unused")
	private void setScreenOff(final boolean isSetOff, long delayMillis) {
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		// PowerManager.WakeLock wakeLock = pm.newWakeLock(
		// PowerManager.ACQUIRE_CAUSES_WAKEUP
		// | PowerManager.SCREEN_DIM_WAKE_LOCK, "ScreenOnOff");

		// PowerManager.WakeLock wakeLock = pm.newWakeLock(
		// android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// "ScreenOnOff");

		if (isSetOff) {
			// pm.goToSleep(SystemClock.uptimeMillis() + delay);
			// pm.goToSleep(SystemClock.elapsedRealtime() + delay);
			// pm.goToSleep(SystemClock.currentThreadTimeMillis() +
			// delayMillis);
		} else {
			// pm.wakeUp(SystemClock.uptimeMillis() + delay);
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// PowerManager.WakeLock wakeLock = pm
				// .newWakeLock(
				// android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				// "ScreenOnOff");
				PowerManager.WakeLock wakeLock = pm.newWakeLock(
						PowerManager.ACQUIRE_CAUSES_WAKEUP
								| PowerManager.SCREEN_DIM_WAKE_LOCK,
						"ScreenOnOff");
				if (isSetOff) {
					// wakeLock.acquire();
					// wakeLock.release();
					// pm.goToSleep(System.currentTimeMillis());
				} else {
					wakeLock.acquire();
				}

			}
		}, delayMillis);

	}

}
