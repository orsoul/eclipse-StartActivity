package com.fanfull.utils;

import static android.content.ContentValues.TAG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import com.fanfull.base.BaseApplication;

public class WiFiUtil {
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;

	// 构造器
	public WiFiUtil(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	// 打开WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前WIFI状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	public void startScan() {
		mWifiManager.startScan();
		// 得到扫描结果
		mWifiList = mWifiManager.getScanResults();
		// 得到配置好的网络连接
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuilder lookUpScan() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++) {
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到IP地址
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b = mWifiManager.enableNetwork(wcgID, true);
		System.out.println("a--" + wcgID);
		System.out.println("b--" + b);
	}

	// 断开指定ID的网络
	public void disconnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	// 然后是一个实际应用方法，只验证过没有密码的情况：

	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
			int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	/**
	 * Reconnect to the currently active access point, if we are currently
	 * disconnected. This may result in the asynchronous delivery of state
	 * change events.
	 * 
	 * @return true if the operation succeeded
	 */
	public static boolean reconnect(Context context) {
		WifiManager wifiManager = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE));
		return wifiManager.reconnect();
	}

	/**
	 * Disassociate from the currently active access point. This may result in
	 * the asynchronous delivery of state change events.
	 * 
	 * @param context
	 * @return Disassociate from the currently active access point. This may
	 *         result in the asynchronous delivery of state change events.
	 */
	public static boolean disconnect(Context context) {
		WifiManager wifiManager = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE));
		return wifiManager.disconnect();
	}

	/**
	 * @return wifi已连上 返回true
	 * @Date
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiNetInfo.isConnected();
	}

	public static boolean isConnected() {
		return isConnected(BaseApplication.getContext());
	}

	private static String PACKAGENAME = "com.android.settings";

	/**
	 * 打开系统WiFi设置
	 * 
	 * @param act
	 */
	public static void openWiFiSetting(Context context) {
		Intent intent = null;
		if (10 < android.os.Build.VERSION.SDK_INT) { // API大于10 就是3.0+
			intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
		} else {
			// intent = new Intent();
			// ComponentName component = new ComponentName(
			// "com.android.settings",
			// "com.android.settings.WirelessSettings");
			// intent.setComponent(component);
			// intent.setAction("android.intent.action.VIEW");
			intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.setClassName(PACKAGENAME, PACKAGENAME + ".wifi.WifiSettings");
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void openWiFiSetting() {
		openWiFiSetting(BaseApplication.getContext());
	}

	public static void setWifiEnabled(Context context, boolean enable) {
		((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.setWifiEnabled(enable);
	}

	public static void setWifiEnabled(boolean enable) {
		setWifiEnabled(BaseApplication.getContext(), enable);
	}

	/**
	 * @param context
	 * @return
	 */
	public static int getWifiState(Context context) {
		return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
				.getWifiState();
	}
	/**
	 * 将整数形式的ip地址 转成 字符串形式
	 * @param intIP 整数形式的 ip地址
	 * @return 字符串形式的 ip地址，例：192.168.1.1
	 */
	public static String intIP2String(int intIP) {
		return (intIP & 0xFF) + "." + (intIP >> 8 & 0xFF) + "." + (intIP >> 16 & 0xFF)
				+ "." + (intIP >> 24 & 0xFF);
	}

	/**
	 * 将字符串形式的ip地址 转成 整数形式
	 * @param StringIP 符串形式的 ip地址，例：192.168.1.1
	 * @return 整数形式的 ip地址
	 */
	public static int StringIP2Int(String StringIP) {
		if (null == StringIP || !StringIP.matches("^\\d+.\\d+.\\d+.\\d+$")) {
			return 0;
		}
		String[] split = StringIP.split("\\.");
		int ipInt = 0;
		for (int i = split.length - 1; 0 <= i; i--) {
			ipInt = (ipInt << 8) | Integer.parseInt(split[i]);
		}
		return ipInt;
	}
	public static boolean ping(String ipString, int pingCount, int s) {
		int status = -1;
		long l = System.currentTimeMillis();
		try {
			String cmd = "/system/bin/ping -c " + pingCount + " -w " + s + " " + ipString;
			Process p = Runtime.getRuntime().exec(cmd);
			status = p.waitFor();
			if (status == 0) {
				System.out.println(cmd + " ok");
			} else {
				System.out.println(cmd + " failed");
			}
			LogsUtil.w(TAG, "status:" + status);
		} catch (Exception e) {
			return status == 0;
		}
		LogsUtil.s("ping time:" + (System.currentTimeMillis() - l) + "\n");
		return status == 0;
	}
	public static boolean ping(String host, int pingCount, StringBuffer stringBuffer) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
//        String command = "ping -c " + pingCount + " -w 5 " + host;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                LogsUtil.e("ping fail:process is null.");
                append(stringBuffer, "ping fail:process is null.");
                return false;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                LogsUtil.i(line);
                append(stringBuffer, line);
            }
            int status = process.waitFor();
            if (status == 0) {
                LogsUtil.i("exec cmd success:" + command);
                append(stringBuffer, "exec cmd success:" + command);
                isSuccess = true;
            } else {
                LogsUtil.e("exec cmd fail.");
                append(stringBuffer, "exec cmd fail.");
                isSuccess = false;
            }
            LogsUtil.i("exec finished.");
            append(stringBuffer, "exec finished.");
        } catch (IOException e) {
            LogsUtil.e(e);
        } catch (InterruptedException e) {
            LogsUtil.e(e);
        } finally {
            LogsUtil.i("ping exit.");
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    LogsUtil.e(e);
                }
            }
        }
        return isSuccess;
    }

    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }
    }
	public static final boolean ping() {

		String result = null;

		try {

			String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~

			Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping3次

			// 读取ping的内容，可不加。

			InputStream input = p.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			StringBuffer stringBuffer = new StringBuffer();

			String content = "";

			while ((content = in.readLine()) != null) {

				stringBuffer.append(content);

			}

			Log.i("TTT", "result content : " + stringBuffer.toString());

			// PING的状态

			int status = p.waitFor();

			if (status == 0) {

				result = "successful~";

				return true;

			} else {

				result = "failed~ cannot reach the IP address";

			}

		} catch (IOException e) {

			result = "failed~ IOException";

		} catch (InterruptedException e) {

			result = "failed~ InterruptedException";

		} finally {

			Log.i("TTT", "result = " + result);

		}

		return false;

	}

}
// 分为三种情况：1没有密码2用wep加密3用wpa加密
