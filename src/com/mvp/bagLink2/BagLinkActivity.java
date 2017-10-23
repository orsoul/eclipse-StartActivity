package com.mvp.bagLink2;

import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.entity.PileInfo;
import com.entity.TrayInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.bagLink2.BagLinkContract.Presenter;
import com.mvp.bagLink2.BagLinkContract.View;

public class BagLinkActivity extends BaseActivity implements View {
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
			mPresenter.scanTrayBag();
			break;
		default:
			break;
		}
	}

	@Override
	public void scanTrayBagSuccess(TrayInfo trayInfo) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		
		mDiaUtil.showProgressDialog("扫描堆袋锁1");
		SoundUtils.playScanPileBag();
		mPresenter.scanPileBag();
		scanPile2 = true;
	}

	@Override
	public void scanTrayBagFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}
	private boolean scanPile2 = false;
	@Override
	public void scanPileBagSuccess(PileInfo pileInfo) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		if(scanPile2){
			mDiaUtil.showProgressDialog("扫描堆袋锁2");
			SoundUtils.playScanPileBag();
			mPresenter.scanPileBagTwo();	
			scanPile2 = false;
		}else{
			mDiaUtil.showProgressDialog("正在关联");
			mPresenter.link();	
		}
	}

	@Override
	public void scanPileBagFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}

	@Override
	public void linkSuccess(PileInfo pileInfo) {
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
