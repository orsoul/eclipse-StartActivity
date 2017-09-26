package com.mvp.screenlink;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.screenlink.ScreenLinkContract.Presenter;

public class ScreenLinkActivity extends BaseActivity implements
		OnClickListener, com.mvp.screenlink.ScreenLinkContract.View {

	private Button scanBag, scanScreen, sure;

	private Presenter mPresenter;
	private DialogUtil mDiaUtil = new DialogUtil(this);
	boolean bagFlag = false, screenFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_screen_link);
		findView();
		setView();

		mPresenter = new ScreenLinkPresenter(this);
	}

	/**
	 * 视图的初始化
	 */
	private void setView() {
		scanBag.setOnClickListener(this);
		scanScreen.setOnClickListener(this);
		sure.setOnClickListener(this);
	}

	private void findView() {
		scanBag = (Button) findViewById(R.id.scanBag);
		scanScreen = (Button) findViewById(R.id.scanScreen);
		sure = (Button) findViewById(R.id.sure);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scanBag:
			scanBag.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button));
			// btnScanBag.setBackground(background)
			mDiaUtil.showProgressDialog("扫描堆袋锁");
			mPresenter.scanBag();
			break;
		case R.id.scanScreen:
			scanScreen.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.button));
			mDiaUtil.showProgressDialog("扫描墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.sure:
			if (bagFlag && screenFlag) {
				mDiaUtil.showProgressDialog("正在关联");
				mPresenter.linkScreenAndPile();
			}else{
				if(!bagFlag){
					mDiaUtil.showDialog("请扫描堆袋锁");
				}else if(!screenFlag){
					mDiaUtil.showDialog("请扫描墨水屏");
				}
			}
		default:
			break;
		}
	}

	@Override
	public void scanBagSuccess(String bagID) {
		
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		bagFlag = true;
	}

	@Override
	public void scanBagFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
		bagFlag = false;
	}

	@Override
	public void scanScreenSuccess(String screenID) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		screenFlag = true;
	}

	@Override
	public void scanScreenFailure() {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog("扫描屏失败");
		screenFlag = false;
	}

	@Override
	public void linkScreenAndPileSuccess() {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		ToastUtil.showToastInCenter("关联成功");
		screenFlag = false;
		bagFlag = false;
	}

	@Override
	public void linkScreenAndPileFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}
}
