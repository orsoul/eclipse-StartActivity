package com.fanfull.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.FingerPrint;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ViewSettingItem;

/**
 * @describe 设置页面
 * @author lenovo
 * @date 22/7-2015 14:02
 */
public class SettingCenterActivity extends BaseActivity {
	public static SettingCenterActivity mSca;
	/**
	 * 记录 主设置界面 是否为前台activity
	 */
	public boolean isOnForeground;

	public SettingCenterActivity() {
		super();
		LogsUtil.d(TAG, "new SettingCenterActivity()");
		mSca = this;
	}

	private ViewSettingItem mVOpenOldBag;
	private ViewSettingItem mVPrescan;
	private ViewSettingItem mVCheckBunch;
	private ViewSettingItem mVCloseFingerSwitch;
	private ViewSettingItem mVCheckSmallSrceen;
	private ViewSettingItem mVDoor;
	private ViewSettingItem mVCheckLogin;

	private TextView mIpSettingtv;
	private TextView mPowerSettingtv;
	private TextView mInitSettingtv;
	private TextView mFingerSettingtv;
	private TextView mShutdownTv;
	private TextView mReadLogView;

	private int mCurrentFocus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView() {

		setContentView(R.layout.activity_setting);
		ItemCheckedChangeListener listener = new ItemCheckedChangeListener();

		// 开启旧锁
		boolean openOldBag = SPUtils.getBoolean(
				MyContexts.KEY_USE_OLDBAG_ENABLE, false);
		mVOpenOldBag = (ViewSettingItem) findViewById(R.id.v_setting_check_oldbag);
		mVOpenOldBag.setCheckedChangedListener(listener);
		mVOpenOldBag.setChecked(openOldBag);
		// mVOpenOldBag.setVisibility(View.GONE);
		// 是否启用预扫描
		boolean ykEnable = SPUtils.getBoolean(MyContexts.KEY_PRESCAN_ENABLE,
				false);
		mVPrescan = (ViewSettingItem) findViewById(R.id.v_setting_prescan_enable);
		mVPrescan.setCheckedChangedListener(listener);
		mVPrescan.setChecked(ykEnable);

		// 扫捆
		boolean openBunch = SPUtils
				.getBoolean(MyContexts.KEY_SCAN_BUNCH, false);
		mVCheckBunch = (ViewSettingItem) findViewById(R.id.v_setting_check_bunch);
		mVCheckBunch.setCheckedChangedListener(listener);
		mVCheckBunch.setChecked(openBunch);

		// 多道天线
		boolean openLotDoor = SPUtils
				.getBoolean(MyContexts.KEY_LOT_DOOR, false);
		mVDoor = (ViewSettingItem) findViewById(R.id.v_setting_check_lot_door);
		mVDoor.setCheckedChangedListener(listener);
		mVDoor.setChecked(openLotDoor);
		// 登录复核
		boolean checkLogin = SPUtils.getBoolean(MyContexts.KEY_CHECK_LOGIN,
				true);
		mVCheckLogin = (ViewSettingItem) findViewById(R.id.v_setting_check_login);
		mVCheckLogin.setCheckedChangedListener(listener);
		mVCheckLogin.setChecked(checkLogin);

		// 辅助屏幕
		boolean smallSrceen = SPUtils.getBoolean(
				MyContexts.KEY_SMALL_SRCEEN_ENABLE, false);
		mVCheckSmallSrceen = (ViewSettingItem) findViewById(R.id.v_setting_item_small);
		mVCheckSmallSrceen.setCheckedChangedListener(listener);
		mVCheckSmallSrceen.setChecked(smallSrceen);

		// 启用指纹
		boolean openFringer = SPUtils.getBoolean(
				MyContexts.KEY_FINGER_PRINT_ENABLE, false);
		mVCloseFingerSwitch = (ViewSettingItem) findViewById(R.id.v_setting_fingerPrint);
		mVCloseFingerSwitch.setCheckedChangedListener(listener);
		mVCloseFingerSwitch.setChecked(openFringer);

		mIpSettingtv = (TextView) findViewById(R.id.tv_setting_item_ip);
		mPowerSettingtv = (TextView) findViewById(R.id.tv_setting_item_set_power);
		mInitSettingtv = (TextView) findViewById(R.id.tv_setting_item_init_bag);
		mFingerSettingtv = (TextView) findViewById(R.id.tv_setting_item_manager_friger);
		mShutdownTv = (TextView) findViewById(R.id.tv_setting_item_shutdown);
		mReadLogView = (TextView) findViewById(R.id.tv_setting_item_manager_log);

		mReadLogView.setOnClickListener(this);
		mIpSettingtv.setOnClickListener(this);
		mPowerSettingtv.setOnClickListener(this);
		mInitSettingtv.setOnClickListener(this);
		mShutdownTv.setOnClickListener(this);
		mFingerSettingtv.setOnClickListener(this);

		// 隐藏功能 复制EPC
		mReadLogView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (LogsUtil.LEVEL_TEST == LogsUtil.getDebugLevel()) {
					LogsUtil.setDebugLevel(LogsUtil.LEVEL_ALL);
					ToastUtil.showToastInCenter("封袋回到【标准模式】");
				} else {
					LogsUtil.setDebugLevel(LogsUtil.LEVEL_TEST);
					ToastUtil.showToastInCenter("封袋进入【测试模式】");
				}
//				Intent intent = new Intent(SettingCenterActivity.this,
//						CopyEpcActivity.class);
//				startActivity(intent);
				return true;// 事件拦截
			}
		});

	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.tv_setting_item_ip:// 更改IP
			intent = new Intent(SettingCenterActivity.this,
					SettingIPActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_setting_item_set_power:// 超高频 读写 功率 设置
			intent = new Intent(SettingCenterActivity.this,
					SettingPowerActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_setting_item_init_bag:// 初始化
			// showListDia();
			// intent = new Intent(SettingCenterActivity.this,
			// InitBag4ezActivity.class);
			// startActivity(intent);
			// intent = new Intent(SettingCenterActivity.this,
			// InitNewBagActivity.class);
			// startActivity(intent);
			// intent = new Intent(SettingCenterActivity.this,
			// InitNewBagActivityTwo.class);
			// startActivity(intent);
			// intent = new Intent(SettingCenterActivity.this,
			// InitNfcBagActivity.class);
			// startActivity(intent);

			break;
		case R.id.tv_setting_item_manager_friger:
			// if(null != StaticString.orgId ){//机构号不等于空，说明已经登录了
			if (null != StaticString.userId) {// 机构号不等于空，说明已经登录了
				intent = new Intent(SettingCenterActivity.this,
						SettingFingerPrintActivity.class);
				startActivity(intent);
			} else {
				ToastUtil.showToastInCenter("该功能需要先登录！");
			}

			break;
		case R.id.tv_setting_item_shutdown:
			intent = new Intent(SettingCenterActivity.this,
					SettingShutDownActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_setting_item_manager_log:
			intent = new Intent(SettingCenterActivity.this,
					SettingLogActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onResume() {
		isOnForeground = true;
		LogsUtil.d(TAG, "onResume isOnForeground " + isOnForeground);
		super.onResume();
	}

	@Override
	protected void onStop() {
		isOnForeground = false;
		LogsUtil.d(TAG, "onResume onStop " + isOnForeground);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mSca = null;
		super.onDestroy();
	}

	// 获取键值，手动让控件获得焦点。
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_2:
			if (mCurrentFocus > 0) {
				mCurrentFocus--;
				if (mCurrentFocus == 0) {
					mVOpenOldBag.obtainFocus();
				} else if (mCurrentFocus == 1) {
					mVPrescan.obtainFocus();
				} else if (mCurrentFocus == 2) {
					mVDoor.obtainFocus();
				} else if (mCurrentFocus == 3) {
					mVCheckBunch.obtainFocus();
				} else if (mCurrentFocus == 4) {
					mVCheckSmallSrceen.obtainFocus();
				} else if (mCurrentFocus == 5) {
					mVCloseFingerSwitch.obtainFocus();
				} else if (mCurrentFocus == 6) {
					ViewUtil.requestFocus(mIpSettingtv);
				} else if (mCurrentFocus == 7) {
					ViewUtil.requestFocus(mPowerSettingtv);
				}
				// else if (mCurrentFocus ==
				// 8){ViewUtil.requestFocus(mInitSettingtv);}
				else if (mCurrentFocus == 8) {
					ViewUtil.requestFocus(mFingerSettingtv);
				} else if (mCurrentFocus == 9) {
					ViewUtil.requestFocus(mReadLogView);
				}
			}
			break;
		case KeyEvent.KEYCODE_4:
			finish();
			break;
		case KeyEvent.KEYCODE_5:
			if (mCurrentFocus == 0) {
				mVOpenOldBag.performClick();
			} else if (mCurrentFocus == 1) {
				mVPrescan.performClick();
			} else if (mCurrentFocus == 2) {
				mVDoor.performClick();
			} else if (mCurrentFocus == 3) {
				mVCheckBunch.performClick();
			} else if (mCurrentFocus == 4) {
				mVCheckSmallSrceen.performClick();
			} else if (mCurrentFocus == 5) {
				mVCloseFingerSwitch.performClick();
			} else if (mCurrentFocus == 6) {
				mIpSettingtv.performClick();
			} else if (mCurrentFocus == 7) {
				mPowerSettingtv.performClick();
			}
			// else if (mCurrentFocus == 8){mInitSettingtv.performClick();}
			else if (mCurrentFocus == 8) {
				mFingerSettingtv.performClick();
			} else if (mCurrentFocus == 9) {
				mReadLogView.performClick();
			}
			break;
		case KeyEvent.KEYCODE_6:
			if (mCurrentFocus == 6) {
				mIpSettingtv.performClick();
			} else if (mCurrentFocus == 7) {
				mPowerSettingtv.performClick();
			}
			// else if (mCurrentFocus == 8){mInitSettingtv.performClick();}
			else if (mCurrentFocus == 8) {
				mFingerSettingtv.performClick();
			} else if (mCurrentFocus == 9) {
				mReadLogView.performClick();
			}
			break;
		case KeyEvent.KEYCODE_8:
			if (mCurrentFocus < 9) {
				mCurrentFocus++;
				if (mCurrentFocus == 1) {
					mVPrescan.obtainFocus();
				} else if (mCurrentFocus == 2) {
					mVDoor.obtainFocus();
				} else if (mCurrentFocus == 3) {
					mVCheckBunch.obtainFocus();
				} else if (mCurrentFocus == 4) {
					mVCheckSmallSrceen.obtainFocus();
				} else if (mCurrentFocus == 5) {
					mVCloseFingerSwitch.obtainFocus();
				} else if (mCurrentFocus == 6) {
					ViewUtil.requestFocus(mIpSettingtv);
				} else if (mCurrentFocus == 7) {
					ViewUtil.requestFocus(mPowerSettingtv);
				}
				// else if (mCurrentFocus ==
				// 8){ViewUtil.requestFocus(mInitSettingtv);}
				else if (mCurrentFocus == 8) {
					ViewUtil.requestFocus(mFingerSettingtv);
				} else if (mCurrentFocus == 9) {
					ViewUtil.requestFocus(mReadLogView);
				}
			}
			break;
		default:
			break;
		}
		/*
		 * 修改 back键的默认事件
		 */
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class ItemCheckedChangeListener implements
			ViewSettingItem.ItemCheckedChangeListener {

		@Override
		public void onCheckedChanged(ViewSettingItem view, boolean isChecked) {
			switch (view.getId()) {
			case R.id.v_setting_check_oldbag:// 旧锁
				SPUtils.putBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE, isChecked);
				break;
			case R.id.v_setting_prescan_enable:// 预扫描
				SPUtils.putBoolean(MyContexts.KEY_PRESCAN_ENABLE, isChecked);
				break;
			case R.id.v_setting_check_lot_door: // 多天线
				SPUtils.putBoolean(MyContexts.KEY_LOT_DOOR, isChecked);
				break;
			case R.id.v_setting_item_small:// 小屏
				SPUtils.putBoolean(MyContexts.KEY_SMALL_SRCEEN_ENABLE,
						isChecked);
				OLEDOperation.setEnable(isChecked);
				if (isChecked) {
					OLEDOperation.getInstance().open();
				}
				break;
			case R.id.v_setting_fingerPrint:// 指纹
				SPUtils.putBoolean(MyContexts.KEY_FINGER_PRINT_ENABLE,
						isChecked);
				FingerPrint.enable = isChecked;
				/** 表示关闭指纹，复位初始化 */
				if (!isChecked) {
					FingerPrint.getInstance().setInit(false);
				}
				break;
			case R.id.v_setting_check_login: // 登录复核
				SPUtils.putBoolean(MyContexts.KEY_CHECK_LOGIN, isChecked);
				if (isChecked) {
					StaticString.userIdcheck = null;
				} else {
					StaticString.userIdcheck = StaticString.userId;
				}
				break;
			case R.id.v_setting_check_bunch:// 扫捆
				SPUtils.putBoolean(MyContexts.KEY_SCAN_BUNCH, isChecked);
				break;
			}
		}

	}
}
