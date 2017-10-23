package com.fanfull.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fanfull.contexts.MyContexts;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.WiFiUtil;

/**
 * 开机广播接收者；启动程序后台服务，打开WiFi
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	// private NotificationManager manager;
	// private WifiManager wifiManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		LogsUtil.w(BootBroadcastReceiver.class.getSimpleName() + " : "
				+ intent.getAction());

		// 开机 启动 后台服务
		context.startService(new Intent(context, BackgroundService.class));

		// 打开WiFi
		// if (!WiFiUtil.isConnected(context)) {
		// WiFiUtil.openWiFiSetting(context);
		// 开机重启 WiFi，避免假连情况
		if (SPUtils.getBoolean(MyContexts.KEY_WIFI_ENABLE)) {
			WiFiUtil.setWifiEnabled(context, false);
			WiFiUtil.setWifiEnabled(context, true);
		}
		// }

		// WifiManager wifiManager = (WifiManager) context
		// .getSystemService(Context.WIFI_SERVICE);
		// wifiManager.setWifiEnabled(false);
		// LogsUtil.d("wifi status=" + wifiManager.getWifiState());
		// wifiManager.setWifiEnabled(true);

		// Intent sendIntent =new Intent(context,StartActivity.class);
		// sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(sendIntent);

	}

}
