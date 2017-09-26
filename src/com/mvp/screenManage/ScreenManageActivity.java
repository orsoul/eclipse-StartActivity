package com.mvp.screenManage;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.screenManage.ScreenManageContract.Presenter;
import com.mvp.screenlink.ScreenLinkActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ScreenManageActivity extends BaseActivity implements ScreenManageContract.View, OnClickListener{
	private Button btn_register,btn_cancle,btn_start,btn_stop,btn_linkScreen;
	private ImageView tv_back;
	private DialogUtil mDiaUtil;
	private Presenter mPresenter;
	private boolean screenFlag = false;
	private int typeID = 0;
	private String screenID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_manage);
		findView();
		mPresenter = new ScreenManagePresenter(this);
		mDiaUtil = new DialogUtil(this);
	}
	public void findView(){
		btn_register = (Button) findViewById(R.id.screen_register);
		btn_cancle = (Button) findViewById(R.id.screen_cancle);
		btn_start = (Button) findViewById(R.id.screen_start);
		btn_stop = (Button) findViewById(R.id.screen_stop);
		btn_linkScreen = (Button) findViewById(R.id.linkScreen);
		tv_back =  (ImageView) findViewById(R.id.screen_manage_back);
		btn_register.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		btn_stop.setOnClickListener(this);
		btn_linkScreen.setOnClickListener(this);
		tv_back.setOnClickListener(this);
	}

	@Override
	public void scanScreenFailure() {
		// TODO Auto-generated method stub
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog("扫描屏失败");
		screenFlag = false;
	}
	@Override
	public void scanScreenSuccess(String screenID) {
		// TODO Auto-generated method stub
		
		this.screenID = screenID;
		switch (typeID) {
		case 1:
			
			mPresenter.scanRegister(screenID, StaticString.userId);
			break;
		case 2:
			
			mPresenter.scanCancle(screenID, StaticString.userId);
			break;
		case 3:
			
			mPresenter.scanStart(screenID, StaticString.userId);
			break;
		case 4:
			
			mPresenter.scanStop(screenID, StaticString.userId);
			break;
		default:
			break;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.screen_register:
			typeID = 1;
			mDiaUtil.showProgressDialog("注册墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.screen_cancle:
			typeID = 2;
			mDiaUtil.showProgressDialog("注销墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.screen_start:
			typeID = 3;
			mDiaUtil.showProgressDialog("启用墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.screen_stop:
			typeID = 4;
			mDiaUtil.showProgressDialog("停用墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.linkScreen:
			intent = new Intent(ScreenManageActivity.this, ScreenLinkActivity.class);
			startActivity(intent);
			break;
		case R.id.screen_manage_back:
			finish();
		default:
			break;
		}
	}
	@Override
	public void scanSuccess(String msg) {
		// TODO Auto-generated method stub
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(msg);
	}
	@Override
	public void scanFailure(String msg) {
		// TODO Auto-generated method stub
		mDiaUtil.showDialog(msg);
	}
	@Override
	public void onFailure(String msg) {
		// TODO Auto-generated method stub
		mDiaUtil.showDialogFinishActivity(msg);
	}
}
