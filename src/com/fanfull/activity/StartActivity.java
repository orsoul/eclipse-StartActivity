package com.fanfull.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfull.activity.setting.SettingPowerActivity;
import com.fanfull.background.BackgroundService;
import com.fanfull.base.BaseApplication;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.FingerPrint;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.op.RFIDOperation;
import com.fanfull.op.SerialPortOperation;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.IOUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;

/**
 * @author Administrator 启动页面
 * 
 */
public class StartActivity extends Activity {
	private final String TAG = StartActivity.class.getSimpleName();
	private boolean checkIsFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_start);
		TextView tvVersionName = (TextView) findViewById(R.id.tv_start_activity_versionname);
		tvVersionName.setText(BaseApplication.getVersionName());

		// 多道天线
		SPUtils.putBoolean(MyContexts.KEY_LOT_DOOR, false);
		// wifi 是否启用
		boolean wifiEnable = SPUtils.getBoolean(MyContexts.KEY_WIFI_ENABLE,
				true);
		SocketConnet.setEnable(wifiEnable);
		LogsUtil.d(TAG, "wifi enable : " + wifiEnable);
		if (wifiEnable) {
			// 打开wifi
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			LogsUtil.d(TAG, "WifiState:" + wifiManager.getWifiState());
			// wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING //
			// 0
			// || wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED
			// // 1
			// || wifiManager.getWifiState() == WifiManager.WIFI_STATE_UNKNOWN
			// // 4
			if (!wifiManager.isWifiEnabled()) { // 3
				wifiManager.setWifiEnabled(true);
			}
		}

		// 串口 是否启用
		boolean serialEnable = SPUtils.getBoolean(MyContexts.KEY_SERIAL_ENABLE,
				false);
		SerialPortOperation.setEnable(serialEnable);
		int baudrate = SPUtils.getInt(MyContexts.KEY_BAUDRATE_VALUE, 19200);
		SerialPortOperation.setBaudrate(baudrate);
		LogsUtil.d(TAG, "串口 enable : " + serialEnable);
		LogsUtil.d(TAG, "串口 baudrate : " + baudrate);

		// 默认 需要 复核
		SPUtils.putBoolean(MyContexts.KEY_CHECK_LOGIN, true);
		// SPUtils.putBoolean(MyContexts.KEY_SCAN_BUNCH, true);

		// 封袋 超高频功率
		int coverRead = SPUtils.getInt(MyContexts.KEY_POWER_READ_COVER, -1);
		if (-1 == coverRead) {
			SPUtils.putInt(MyContexts.KEY_POWER_READ_COVER,
					SettingPowerActivity.POWER_READ_COVER);
		}
		int coverWrite = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_COVER, -1);
		if (-1 == coverWrite) {
			SPUtils.putInt(MyContexts.KEY_POWER_WRITE_COVER,
					SettingPowerActivity.POWER_WRITE_COVER);
		}
		// 批量入库 超高频功率
		int instoreRead = SPUtils.getInt(MyContexts.KEY_POWER_READ_INSTORE, -1);
		if (-1 == instoreRead) {
			SPUtils.putInt(MyContexts.KEY_POWER_READ_INSTORE,
					SettingPowerActivity.POWER_READ_INSTORE);
		}
		int instoreWrite = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_INSTORE,
				-1);
		if (-1 == instoreWrite) {
			SPUtils.putInt(MyContexts.KEY_POWER_WRITE_INSTORE,
					SettingPowerActivity.POWER_WRITE_INSTORE);
		}

		// oled屏是否启用
		OLEDOperation.enable = SPUtils.getBoolean(
				MyContexts.KEY_SMALL_SRCEEN_ENABLE, false);
		LogsUtil.d(TAG, "oled enable : " + OLEDOperation.enable);
		// 指纹模块 是否启用
		FingerPrint.enable = SPUtils.getBoolean(
				MyContexts.KEY_FINGER_PRINT_ENABLE, false);
		LogsUtil.d(TAG, "finger enable : " + FingerPrint.enable);

		// 加载 声音文件, 供以后使用
		SoundUtils.loadSounds(getApplicationContext());
		// 将 assets 目录下的 ip_config.json 配置信息存到 SharedPerferences
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				String ip = null;
				ip = SPUtils.getString(MyContexts.KEY_IP0);
				if (null == ip) {
					// copyIP2SharedPreferences();
					copyIpConfig2SharedPreferences();
				}

				StaticString.IP0 = SPUtils.getString(MyContexts.KEY_IP0);
				StaticString.PORT0 = SPUtils.getInt(MyContexts.KEY_PORT0);

				StaticString.IP1 = SPUtils.getString(MyContexts.KEY_IP1);
				StaticString.PORT1 = SPUtils.getInt(MyContexts.KEY_PORT1);

				StaticString.IP2 = SPUtils.getString(MyContexts.KEY_IP2);
				StaticString.PORT2 = SPUtils.getInt(MyContexts.KEY_PORT2);

				/* 载入 oled屏 字库 */
				loadWordLibLogin();// 先加载登录需要文字
				loadWordLib();

				/* 打开高频模块 */
				int i;
				for (i = 0; i < 3; i++) {
					if (RFIDOperation.getInstance().openAndWakeup()) {
						break;
					}
				}
				if (3 <= i) {
					ToastUtil.showToastOnUiThreadInCenter(getResources().getString(
							R.string.text_init_rfid_failed), StartActivity.this);
					finish();
					return;
				}

				if (SerialPortOperation.isEnable()) {
					if (!SerialPortOperation.open(false)) {
						ToastUtil.showToastOnUiThreadInCenter(getResources().getString(
								R.string.text_init_serial_failed), StartActivity.this);
					}
				}

				startNext();

			}
		});

		// 启动 动画
		ImageView mImageView = (ImageView) findViewById(R.id.image_logo);
		AlphaAnimation mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		mAlphaAnimation.setDuration(1000);// 3000
		mImageView.setAnimation(mAlphaAnimation);
		mAlphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 启动屏幕亮灭监听
				Intent servIceintent = new Intent(StartActivity.this,
						BackgroundService.class);
				// servIceintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startService(servIceintent);

				startNext();

			}
		});
	}

	/**
	 * 决定是否进入 登录界面。动画播放完毕 和 模块初始化完毕
	 */
	private void startNext() {
		if (checkIsFinish) {
			// if (!RFIDOperation.getInstance().openAndWakeup()) {
			// ToastUtil.showToastInCenter(getResources().getString(
			// R.string.text_init_rfid_failed));
			// finish();
			// return;
			// }
			// if (SerialPortOperation.isEnable() &&
			// !SerialPortOperation.isOpen()) {
			// ToastUtil.showToastInCenter(getResources().getString(
			// R.string.text_init_serial_failed));
			//
			// if (!SocketConnet.isEnable()) {
			// // 串口打开失败 同时 网络传输未启用
			// SPUtils.putBoolean(MyContexts.KEY_SERIAL_ENABLE, false);
			// SPUtils.putBoolean(MyContexts.KEY_WIFI_ENABLE, true);
			// }
			// }
			Intent intent = new Intent(StartActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			checkIsFinish = true;
		}
	}

	/**
	 * 将 assets 目录下的 ip_config.json 配置信息存到 SharedPerferences
	 */
	private void copyIpConfig2SharedPreferences() {
		InputStream is = null;
		try {
			is = getAssets().open("ip_config.json");
			String jsonStr = IOUtil.convertInputStream2String(is);
			JSONObject root = new JSONObject(jsonStr);

			@SuppressWarnings("unchecked")
			Iterator<String> keys = root.keys();

			while (keys.hasNext()) {
				String key = keys.next();

				if (key.startsWith("KEY_IP")) {
					SPUtils.putString(key, root.getString(key));
				} else {
					SPUtils.putInt(key, root.getInt(key));
				}
				LogsUtil.d("key : " + key);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 载入 oled屏 字库
	 */
	private void loadWordLibLogin() {
		LineNumberReader lineReader = null;
		try {
			InputStream is = getAssets().open("login_wordLib.txt");
			lineReader = new LineNumberReader(
					new InputStreamReader(is, "utf-8"));

			String line = null;
			String key = null;
			String value = null;

			HashMap<String, String> map = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			while (null != (line = lineReader.readLine())) {
				// System.out.println(lineNum++);
				if (line.startsWith("/*")) {
					if (sb.length() != 0) {
						value = sb.toString().replaceAll("0x|,", "");// 去掉字符串中
																		// 0x 和
																		// ,
						map.put(key, value);
						sb.setLength(0);
					}
					key = line.substring(3, 4);
				} else {
					sb.append(line);
				}
			}
			// value = sb.toString().replaceAll("0x", "").replaceAll(",", "");
			value = sb.toString().replaceAll("0x|,", "");
			map.put(key, value);

			OLEDOperation.mLoginWordLib = map;

			// Set<Entry<String,String>> entrySet = map.entrySet();
			// for (Entry<String, String> entry : entrySet) {
			// LogsUtil.d(TAG, entry.toString());
			// }
		} catch (IOException e) {
			LogsUtil.e(TAG, "IOException 字库文件载入异常");
			e.printStackTrace();
		} finally {
			if (lineReader != null) {
				try {
					lineReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 载入 oled屏 字库
	 */
	private void loadWordLib() {
		LineNumberReader lineReader = null;
		try {
			InputStream is = getAssets().open("wordLib.txt");
			lineReader = new LineNumberReader(
					new InputStreamReader(is, "utf-8"));

			String line = null;
			String key = null;
			String value = null;

			HashMap<String, String> map = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			while (null != (line = lineReader.readLine())) {
				if (line.startsWith("/*")) {
					if (sb.length() != 0) {
						value = sb.toString().replaceAll("0x|,", "");// 去掉0x和,
						map.put(key, value);
						sb.setLength(0);
					}
					key = line.substring(3, 4);
				} else {
					sb.append(line);
				}
			}
			value = sb.toString().replaceAll("0x|,", "");
			map.put(key, value);
			OLEDOperation.mWordLib = map;
		} catch (IOException e) {
			LogsUtil.e(TAG, "IOException 字库文件载入异常");
			e.printStackTrace();
		} finally {
			if (lineReader != null) {
				try {
					lineReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
