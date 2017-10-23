package com.mvp.center;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.entity.Response;
import com.entity.RootInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.mvp.bagLinkCenter.BagLinkCenterActivity;
import com.mvp.baglink.BagLinkActivity;
import com.mvp.center.option.CenterOptionActivity;
import com.mvp.hand.HandOperActivity;
import com.mvp.pileCreate1.PileCreateOneActivity;
import com.mvp.pilecreate.PileCreateActivity;
import com.mvp.refreshscreen.RefreshScreenActivity;
import com.mvp.screenManage.ScreenManageActivity;
import com.mvp.screenlink.ScreenLinkActivity;

public class CenterActivity extends BaseActivity implements OnClickListener,
		com.mvp.center.CenterContract.View {
	private Button initScreen, linkBag, screenManage, refreshScreen, createpile,upload,handOper;
	
	private ImageView back,sync;

	private CenterPresenter mPresenter;
	/**
	 * 数据更新的标志，只有当该值为true时点击更新标志不能再更新 
	 */
	private boolean refresh_flag = false;
	private DialogUtil mDiaUtil = new DialogUtil(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center);
		findView();

		mPresenter = new CenterPresenter(this);
		//mPresenter.getPermission();
	}
	private boolean isPause = false;
	
	
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	protected void onStart() {
		if(isPause){
			mPresenter.checkSyncStatus(this);	
			isPause = false;
		}
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		isPause = true;
		super.onPause();
	}
	
	private void findView() {
		//initScreen = (Button) findViewById(R.id.initScreen);
		linkBag = (Button) findViewById(R.id.linkBag);
		screenManage = (Button) findViewById(R.id.screen_manage);
		refreshScreen = (Button) findViewById(R.id.refreshScreen);
		createpile = (Button) findViewById(R.id.createpile);
		sync = (ImageView) findViewById(R.id.sync);
		back = (ImageView) findViewById(R.id.back);
		upload = (Button) findViewById(R.id.upload);
		handOper = (Button) findViewById(R.id.hand_oper);

		//initScreen.setOnClickListener(this);
		linkBag.setOnClickListener(this);
		screenManage.setOnClickListener(this);
		refreshScreen.setOnClickListener(this);
		createpile.setOnClickListener(this);
		sync.setOnClickListener(this);
		upload.setOnClickListener(this);
		back.setOnClickListener(this);
		handOper.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.linkBag:
			intent = new Intent(this, BagLinkCenterActivity.class);
			startActivity(intent);
			break;
		case R.id.screen_manage:
			intent = new Intent(this, ScreenManageActivity.class);
			startActivity(intent);
			break;
		case R.id.refreshScreen:
			intent = new Intent(this, RefreshScreenActivity.class);
			startActivity(intent);
			break;
		case R.id.createpile:
			intent = new Intent(this, PileCreateOneActivity.class);
			startActivity(intent);
			break;
		case R.id.sync:
			if(!refresh_flag){
				mDiaUtil.showProgressDialog("更新数据中");
				mPresenter.download();	
			}
			break;
		case R.id.upload:
			mPresenter.upload();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.hand_oper:
			intent = new Intent(this,HandOperActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void syncStatus(Response response) {
		mDiaUtil.dismissProgressDialog();
		if (response.isSuccess()) {
			refresh_flag = false;
			sync.setImageResource(R.drawable.coverbag_checked_failed);
			mDiaUtil.showProgressDialog("更新数据中");
			mPresenter.download();	
		} else {
			sync.setImageResource(R.drawable.coverbag_checked);
			refresh_flag = true;
		}
	}

	@Override
	public void syncSuccess() {
		mDiaUtil.dismissProgressDialog();
		sync.setImageResource(R.drawable.coverbag_checked);
		refresh_flag = true;
	}

	@Override
	public void error(String error) {
		sync.setImageResource(R.drawable.coverbag_checked_failed);
		refresh_flag = false;
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}

	@Override
	public void userSuccess(String msg) {
		// TODO Auto-generated method stub
		LogsUtil.e("123456",msg);
		SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, msg);
		mPresenter.checkSyncStatus(this);
	}

	@Override
	public void userError() {
		// TODO Auto-generated method stub
		SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, null);
		mPresenter.checkSyncStatus(this);
	}
	
	
}
