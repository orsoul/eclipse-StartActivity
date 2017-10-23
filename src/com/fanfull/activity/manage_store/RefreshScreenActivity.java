package com.fanfull.activity.manage_store;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
		OnClickListener {
	private Button scan_screen, scan_bag, refresh;

	private DialogUtil mDiaUtil = new DialogUtil(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_refresh_screen);

		scan_screen = (Button) findViewById(R.id.scan_screen);
		scan_bag = (Button) findViewById(R.id.scan_bag);
		refresh = (Button) findViewById(R.id.refresh);
	}
	@Override
	protected void initEvent() {
		scan_screen.setOnClickListener(this);
		scan_bag.setOnClickListener(this);
		refresh.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_screen:
			mDiaUtil.showProgressDialog();
			break;

		case R.id.scan_bag:
			mDiaUtil.showProgressDialog();
			break;
		case R.id.refresh:
			mDiaUtil.showProgressDialog();
		default:
			break;
		}
	}
	private void onSuccess() {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
	}
	private void onFailed(Object failedInfo) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(failedInfo);
	}

	@Override
	protected void onDestroy() {
		if (null != mDiaUtil) {
			mDiaUtil.destroy();
		}
		super.onDestroy();
	}

}
