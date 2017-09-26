package com.mvp.bagLink3;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.bagLink3.BagLinkContract.Presenter;
import com.mvp.bagLink3.BagLinkContract.View;

import android.os.Bundle;
import android.widget.Button;

public class BagLinkActivity extends BaseActivity implements View{
	private Button scanTray;
	private Presenter mPresenter;
	private DialogUtil mDiaUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bag_link2);
		findView();

		mPresenter = new BagLinkPresenter(this);
		mDiaUtil = new DialogUtil(this);
	}
	
	private void findView() {
		scanTray = (Button) findViewById(R.id.scanTrayBag);

		scanTray.setOnClickListener(this);
	}

	@Override
	public void onClick(android.view.View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.scanTrayBag:
			mDiaUtil.showProgressDialog("扫描托盘袋锁");
			SoundUtils.playScanBag();
			mPresenter.scanBag();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void scanBagSuccess() {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		
		mDiaUtil.showProgressDialog("关联中");
		mPresenter.linkNewPile();
	}

	@Override
	public void scanBagFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}

	@Override
	public void linkSuccess() {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		ToastUtil.showToastInCenter("关联成功");
	}

	@Override
	public void linkFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}
	
	
}
