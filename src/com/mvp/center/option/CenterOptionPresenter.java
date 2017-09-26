package com.mvp.center.option;

import android.os.Handler;


import com.mvp.center.option.CenterOptionContract.Presenter;
import com.mvp.center.option.CenterOptionContract.View;
import com.net.Net;

public class CenterOptionPresenter implements Presenter {
	private Net net;
	private View mCenterOptionView;
	private Handler mHandler = new Handler();
	public CenterOptionPresenter(View v) {
		mCenterOptionView = v;
		net = Net.getInstance();
	}
}
