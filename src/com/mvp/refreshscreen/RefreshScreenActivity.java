package com.mvp.refreshscreen;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.entity.PileInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;

/**
 * 刷新屏
 * 
 * @author root
 */
public class RefreshScreenActivity extends BaseActivity implements
		OnClickListener, com.mvp.refreshscreen.RefreshScreenContract.View {
	private Button btnScanBag;
	private Button btnScanScreen;
	private Button btnRefresh;

	private RefreshScreenPresenter mPresenter;

	private DialogUtil mDiaUtil = new DialogUtil(this);
	private boolean scanBag = false, scanScreen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findView();
		setView();

		mPresenter = new RefreshScreenPresenter(this);
	}

	private void findView() {
		setContentView(R.layout.activity_refresh_screen);

		btnScanScreen = (Button) findViewById(R.id.scan_screen);
		btnScanBag = (Button) findViewById(R.id.scan_bag);
		btnRefresh = (Button) findViewById(R.id.refresh);
	}

	private void setView() {
		btnScanScreen.setOnClickListener(this);
		btnScanBag.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		btnScanBag.setFocusable(true);
		btnScanBag.setFocusableInTouchMode(true);
		btnScanBag.requestFocus();
		btnScanBag.requestFocusFromTouch();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_bag:
			/*
			 * btnScanBag.setBackgroundDrawable(getResources().getDrawable(
			 * R.drawable.button));
			 */
			// btnScanBag.setBackground(background)
			mDiaUtil.showProgressDialog("扫描袋锁");
			mPresenter.scanBag();
			break;
		case R.id.scan_screen:
			mDiaUtil.showProgressDialog("扫描墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.refresh:
			if (!scanBag) {
				Toast.makeText(this, "请扫描袋", Toast.LENGTH_LONG).show();
				return;
			} else if (!scanScreen) {
				Toast.makeText(this, "请扫描屏", Toast.LENGTH_LONG).show();
				return;
			}
			mDiaUtil.showProgressDialog("正在获取屏幕刷新信息");
			mPresenter.getPileInfo();
		default:
			break;
		}
	}

	private void onSuccess() {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
	}

	private void onFailed(Object failedInfo) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		if (failedInfo != null) {
			Toast.makeText(this, failedInfo.toString(), Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void scanBagSuccess(String bagID) {
		btnScanScreen.setFocusable(true);
		btnScanScreen.setFocusableInTouchMode(true);
		btnScanScreen.requestFocus();
		btnScanScreen.requestFocusFromTouch();
		scanBag = true;
		onSuccess();
		// mDiaUtil.showProgressDialog("扫描墨水屏");
		// SystemClock.sleep(1000);
		// mPresenter.scanScreen();
	}

	@Override
	public void scanBagFailure(String error) {
		onFailed(error);
		scanBag = false;
	}

	@Override
	public void scanScreenSuccess(String screenID) {
		onSuccess();
		scanScreen = true;
		btnRefresh.setFocusable(true);
		btnRefresh.setFocusableInTouchMode(true);
		btnRefresh.requestFocus();
		btnRefresh.requestFocusFromTouch();
	}

	@Override
	public void scanScreenFailure(String failedInfo) {
		onFailed(failedInfo);
		scanScreen = false;
	}

	@Override
	public void refreshData(PileInfo info) {
		mDiaUtil.dismissProgressDialog();
		if (info != null) {
			
			mDiaUtil.showProgressDialog("正在更新屏幕信息");
			mPresenter.writeScreen(info);
		} else {
			Toast.makeText(this, "数据异常，袋与屏可能不匹配", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void error(String error) {
		onFailed(error);
	}

	@Override
	public void writeScreenSuccess() {
		mDiaUtil.dismissProgressDialog();
		mPresenter.light();
		scanBag = false;
		scanScreen = false;
	}

	@Override
	protected void onDestroy() {
		if (null != mDiaUtil) {
			mDiaUtil.destroy();
		}
		super.onDestroy();
	}

}
