package com.fanfull.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.fanfull.activity.scan_lot.LotMainActivity;
import com.fanfull.activity.setting.SettingCenterActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.mvp.center.CenterActivity;

public class MainActivity extends BaseActivity {
	
	private Button mGeneralScan;
	private Button mLotScan;
	private Button mSeach;
	private Button mSetting;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);

		mGeneralScan = (Button) findViewById(R.id.btn_main_general_scan);
		mGeneralScan.setOnClickListener(this);
		
		mLotScan = (Button) findViewById(R.id.btn_main_lot_scan);
		mLotScan.setOnClickListener(this);
		
		mSeach = (Button) findViewById(R.id.btn_main_check);
		mSeach.setOnClickListener(this);
		
		mSetting = (Button) findViewById(R.id.btn_main_setting);
		mSetting.setOnClickListener(this);
		
		ViewUtil.requestFocus(mGeneralScan);
		
	}
	@Override
	protected void onDestroy() {
		if (SocketConnet.getInstance().isConnect()) {
			if (null != StaticString.userIdcheck) {
				SocketConnet.getInstance().communication(SendTask.CODE_LOGOUT_CHECKE);
			}
			SocketConnet.getInstance().communication(SendTask.CODE_LOGOUT);
			StaticString.userId = null;
		}
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_main_general_scan:// 一般扫描
			intent = new Intent(this, GeneralActivity.class);// General.class
			startActivity(intent);
		//	finish();
			// 一般扫描
			break;
		case R.id.btn_main_lot_scan:
			// 批量扫描
			intent = new Intent(this, LotMainActivity.class);// LotScanActivity,LotScan
			startActivity(intent);
		//	finish();
			break;
		case R.id.btn_main_check:
//			intent = new Intent(this, NfcEmptyBagManagerActivity.class);// EnableBagActivity,LotScan
//			startActivity(intent);
			
			intent = new Intent(this, CenterActivity.class);
			startActivity(intent);
			//ToastUtil.showToastInCenter("功能测试中，敬请期待");
			break;
		case R.id.btn_main_setting:
			intent = new Intent(this, SettingCenterActivity.class);// 设置页面
			startActivity(intent);
//			finish();
			break;
		
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_2: // up
			if (mGeneralScan.isFocused()) {
				ViewUtil.requestFocus(mSetting);
			} else {
				ViewUtil.requestFocus(mGeneralScan);
			}
			return true;
		case KeyEvent.KEYCODE_8: // down
			if (mSetting.isFocused()) {
				ViewUtil.requestFocus(mGeneralScan);
			} else {
				ViewUtil.requestFocus(mSetting);
			}
			return true;
		case KeyEvent.KEYCODE_4: // left
			if (mSeach.isFocused()) {
				ViewUtil.requestFocus(mLotScan);
			} else {
				ViewUtil.requestFocus(mSeach);
			}
			return true;
		case KeyEvent.KEYCODE_6: // right
			if (mLotScan.isFocused()) {
				ViewUtil.requestFocus(mSeach);
			} else {
				ViewUtil.requestFocus(mLotScan);
			}
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
