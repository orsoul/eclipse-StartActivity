package com.fanfull.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.fanfull.activity.setting.SettingCenterActivity;
import com.fanfull.background.ActivityUtil;
import com.fanfull.contexts.MyContexts;
import com.fanfull.fff.R;
import com.fanfull.socket.RecieveListener;
import com.fanfull.utils.ClickUtil;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.WiFiUtil;

public class BaseActivity extends Activity implements View.OnClickListener {
	protected final String TAG = this.getClass().getSimpleName();

	protected BaseBroadcastReceiver mBroadcastReceiver;
	protected IntentFilter filter;

	protected Handler mHandler;
	protected DialogUtil mDiaUtil;
	protected RecieveListener mRecieveListener;

	// protected WiFiUtil mWiFiUtil = new WiFiUtil(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/* 添加 广播 */
//		mBroadcastReceiver = new BaseBroadcastReceiver();
//		filter = new IntentFilter();
//		filter.addAction(Intent.ACTION_BATTERY_CHANGED); // 电量改变
//		filter.addAction(Intent.ACTION_BATTERY_LOW); // 电量达到下限
//		filter.addAction(Intent.ACTION_BATTERY_OKAY); // 电量从低变高时
//		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 网络变化
		// filter.addAction(MyContexts.ACTION_EXIT_APP);

		initView();

		initData();

		initEvent();

		ActivityUtil.getInstance().addActivityToList(this);
	}

	protected void initView() {
		// TODO Auto-generated method stub
	}

	protected void initData() {
		// TODO Auto-generated method stub

	}

	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	private static long runTime = 0;
	private static int oldLevel = 0;

	/**
	 * 电量变化 回调此方法。子类根据需要覆盖此方法。
	 * 
	 * @param intent
	 */
	protected void onBatteryChanged(Intent intent) {
		int level = intent.getIntExtra("level", -1); // 获得当前电量
//		int scale = intent.getIntExtra("scale", -1); // 获得总电量
		// LogsUtil.d(TAG, "当前电量:" + level + " 总电量:" + scale + " 之前电量:" +
		// oldLevel);

		/* 第一次 接受 电量改变广播 */
		if (0 == runTime) {

			if (20 < level) {
				ToastUtil.showToastInCenter("当前电量为 " + level + " %");
			} else {
				ToastUtil.showToastInCenter("当前电量为 " + level + " %, 请及时充电");
			}
			runTime = System.currentTimeMillis();
			oldLevel = level;
			return;
		}

		/* 电量降低了 1% */
		if (level < oldLevel) {
			// 计算 1% 电量 续航时间
			LogsUtil.d(TAG, "1% 电量运行了: "
					+ (System.currentTimeMillis() - runTime) / 1000 + " 秒");
			oldLevel = level;
			runTime = System.currentTimeMillis();

			// 根据电量 给出提示
			if (30 < level) {
				// nothing
			} else if (30 == level) {
				ToastUtil.showToastInCenter("当前电量为 " + level + " %");
			} else if (20 == level) {
				ToastUtil.showToastInCenter("当前电量为 " + level + " %, 请及时充电");
			} else if (0 == level % 5) {// 15 10 5 进行提示
				ToastUtil.showToastInCenter("电量低于 " + level + " %, 请及时充电");
			}
		}
	}

	/**
	 * WiFi状态变化 回调此方法。子类根据需要覆盖此方法。
	 * 
	 * @param intent
	 */
	protected void onWiFiStatChanged() {
		if (!WiFiUtil.isConnected()) {
			AlertDialog.Builder ab = new AlertDialog.Builder(BaseActivity.this);
			ab.setMessage("WiFi断开，请检查网络!");
			ab.setPositiveButton("设置WiFi",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							WiFiUtil.openWiFiSetting();
						}
					});
			ab.setNegativeButton("取消", null);
			ab.create().show();
		}
	}

	/**
	 * 点击2次 退出activity
	 */
	protected void clickTwiceFinish() {
		if (ClickUtil.isFastDoubleClick(2500)) {
			finish();
//			onBackPressed();
		} else {
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_click_again_leave));
		}
	}

	/**
	 * com.fanfull.view.ActivityHeadItemView 的 back图标
	 * 
	 * @param v
	 */
	public void onClickBack(View v) {
		onBackPressed();
	}

	@Override
	public void onClick(View v) {
		// 点击声音
		SoundUtils.play(SoundUtils.DROP_SOUND);
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		// setAlarm();
	}

	@Override
	protected void onStart() {
		if (null != mBroadcastReceiver && null != filter) {
			registerReceiver(mBroadcastReceiver, filter);
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (null != mBroadcastReceiver) {
			unregisterReceiver(mBroadcastReceiver);
		}
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		if (null != mDiaUtil) {
			mDiaUtil.destroy();
		}
		ActivityUtil.getInstance().removeActivityFromList(this);
		super.onDestroy();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // 设置 返回键 事件
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// // 按键音效
	// SoundUtils.play(SoundUtils.DROP_SOUND);
	// StaticString.information = null;
	//
	// Intent intent = new Intent();
	// intent.putExtra("falg", false);
	// setResult(2, intent);
	// finish();
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		// 设置页面
		if (KeyEvent.KEYCODE_MENU == keyCode
				|| KeyEvent.KEYCODE_SHIFT_LEFT == keyCode) {
			LogsUtil.d(TAG, "setting");
			// 设置界面 是前台 activity
			if (null != SettingCenterActivity.mSca
					&& SettingCenterActivity.mSca.isOnForeground) {
				LogsUtil.d(TAG, "SettingCenterActivity finish");
				SettingCenterActivity.mSca.finish();
				SettingCenterActivity.mSca = null;
			} else {
				Intent intent = new Intent(this, SettingCenterActivity.class);
				startActivity(intent);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected class BaseBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			LogsUtil.d(TAG, "action:" + action);

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				// 电量改变
				onBatteryChanged(intent);
			} else if (MyContexts.ACTION_EXIT_APP.equals(action)) {
				// 退出APP
				LogsUtil.w(TAG, context.getClass().getSimpleName() + "  exit");
				finish();
				// if (context != null) {
				// if (context instanceof Activity) {
				// ((Activity) context).finish();
				// } else if (context instanceof FragmentActivity) {
				// ((FragmentActivity) context).finish();
				// } else if (context instanceof Service) {
				// ((Service) context).stopSelf();
				// }
				// }
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				// 网络 改变
				onWiFiStatChanged();
			}

		}
	}

}
