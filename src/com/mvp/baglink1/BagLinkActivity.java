package com.mvp.baglink1;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.baglink1.BagLinkContract.Presenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BagLinkActivity extends BaseActivity implements OnClickListener,
		com.mvp.baglink1.BagLinkContract.View {

	private Button button1, button2, button3, button4, button5, button6;
	private Presenter mPresenter;
	private DialogUtil mDiaUtil = new DialogUtil(this);
	private boolean trayBagFlag = false, pileBagFlag = false, screenFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bag_link1);
		findView();
		mPresenter = new BagLinkPresenter(this);
	}

	private void findView() {
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button) findViewById(R.id.button6);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);
		button6.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			mPresenter.download();
			break;
		case R.id.button2:
			mDiaUtil.showProgressDialog("请扫描托盘袋锁");
			mPresenter.scanTrayBag();
			break;
		case R.id.button3:
			mDiaUtil.showProgressDialog("请扫描墨水屏");
			mPresenter.scanScreen();
			break;
		case R.id.button4:
			mDiaUtil.showProgressDialog("请扫描堆袋锁");
			mPresenter.scanPileBag();
			break;
		case R.id.button5:
			if(pileBagFlag&&trayBagFlag&&screenFlag){
				mDiaUtil.showProgressDialog("写入数据");
				mPresenter.refreshScreen();	
			}else{
				if(!trayBagFlag){
					mDiaUtil.showDialog("请扫描托盘袋");
				}else if(!screenFlag){
					mDiaUtil.showDialog("请扫描屏");
				}else if(!pileBagFlag){
					mDiaUtil.showDialog("请扫描堆袋");
				}
			}
			break;
		case R.id.button6:
			mPresenter.upload();
			break;
		default:
			break;
		}
	}

	@Override
	public void scanTrayBagSuccess(String bagID) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		ToastUtil.showToastInCenter("扫描托盘袋成功");
		trayBagFlag = true;
	}

	@Override
	public void scanFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}

	@Override
	public void scanPileBagSuccess(String bagID) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		ToastUtil.showToastInCenter("扫描堆袋成功");
		pileBagFlag = true;
	}

	@Override
	public void scanScreenSuccess(String screenID) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		ToastUtil.showToastInCenter("扫描屏成功");
		screenFlag = true;
	}

	@Override
	public void writeScreenSuccess() {
		mPresenter.light();
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog("请保持光照刷新墨水屏");
		trayBagFlag = false;
		pileBagFlag = false;
		screenFlag = false;
	}

	@Override
	public void writeScreenFailure() {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog("刷新屏失败");
	}
}
