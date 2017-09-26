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
import com.fanfull.utils.IOUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;

/**
 * @author Administrator 启动页面
 * 
 */
public class StartActivity extends Activity {
	private final String TAG = StartActivity.class.getSimpleName();

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

		// 打开wifi
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		LogsUtil.d(TAG, "WifiState:" + wifiManager.getWifiState());
		// wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING // 0
		// || wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED // 1
		// || wifiManager.getWifiState() == WifiManager.WIFI_STATE_UNKNOWN // 4
		if (!wifiManager.isWifiEnabled()) { // 3
			wifiManager.setWifiEnabled(true);
		}
		SPUtils.putBoolean(MyContexts.KEY_CHECK_LOGIN, true);// 默认 需要 复核

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
//				servIceintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startService(servIceintent);

				Intent intent = new Intent(StartActivity.this,
						LoginActivity.class);
				// intent = new
				// Intent(StartActivity.this,ChangeBagActivity.class);
				startActivity(intent);
				finish();
			}
		});
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
