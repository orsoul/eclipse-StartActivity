package com.mvp.refreshscreen;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.entity.PileInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;

/**
 * 刷新屏
 * 
 * @author root
 * 
 */
public class RefreshScreenActivity extends BaseActivity implements
		OnClickListener, com.mvp.refreshscreen.RefreshScreenContract.View {
	private Button btnScanBag;
	private Button btnScanScreen;
	private Button btnRefresh;

	private RefreshScreenPresenter mPresenter;

	private DialogUtil mDiaUtil = new DialogUtil(this);

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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_bag:
			btnScanBag.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button));
			// btnScanBag.setBackground(background)
			mDiaUtil.showProgressDialog("扫描袋锁");
			mPresenter.scanBag();
			break;
		case R.id.scan_screen:
			mDiaUtil.showProgressDialog("扫描墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.refresh:
			if(refreshFlag){
				mDiaUtil.showProgressDialog("正在更新屏幕信息");
				mPresenter.writeScreen();	
			}else{
				mDiaUtil.showDialog("请重新扫描");
			}
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
		mDiaUtil.showDialog(failedInfo);
	}

	@Override
	public void scanBagSuccess(String bagID) {
		btnScanBag.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_button_green));// 按钮变绿
		onSuccess();
		mDiaUtil.showProgressDialog("扫描墨水屏");
		SystemClock.sleep(1000);
		mPresenter.scanScreen();
		
	}

	@Override
	public void scanBagFailure(String error) {
		onFailed(error);
	}

	@Override
	public void scanScreenSuccess(String screenID) {
		onSuccess();
	}

	@Override
	public void scanScreenFailure(String failedInfo) {
		onFailed(failedInfo);
	}

	boolean refreshFlag = false;
	@Override
	public void refreshData(PileInfo info) {
		if(info != null){
			refreshFlag = true;
		}
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showProgressDialog("正在更新屏幕信息");
		mPresenter.writeScreen();
	}

	@Override
	public void error(String error) {
		onFailed(error);
	}

	@Override
	public void writeScreenSuccess() {
		mDiaUtil.dismissProgressDialog();
		mPresenter.light();	
		refreshFlag = false;
	}

	@Override
	protected void onDestroy() {
		if (null != mDiaUtil) {
			mDiaUtil.destroy();
		}
		super.onDestroy();
	}

}
