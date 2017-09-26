package com.fanfull.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.fff.R;
import com.fanfull.utils.LogsUtil;
import com.fanfull.view.ActivityHeadItemView;

public abstract class LinkActivity extends BaseActivity {

	protected final int MSG_SCAN1_SUCCESS = 1;
	protected final int MSG_SCAN1_FAILED = 2;
	protected final int MSG_SCAN2_SUCCESS = 3;
	protected final int MSG_SCAN2_FAILED = 4;
	protected final int MSG_DO_SOMTING_SUCCESS = 5;
	protected final int MSG_DO_SOMTING_FAILED = 6;

	protected com.fanfull.view.ActivityHeadItemView vTitle;

	protected View mFlInfo1;
	protected TextView tvScan1;
	protected View llShow1;
	protected TextView tvWhich1;
	protected TextView tvBagType1;
	protected TextView tvClearingType1;

	protected View mFlInfo2;
	protected TextView tvScan2;
	protected View llShow2;
	protected TextView tvWhich2;
	protected TextView tvBagType2;
	protected TextView tvClearingType2;

	protected Button btnOk;
	
	
//	protected boolean haveTaskRunning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// LogsUtil.d(TAG, "onClick(): " + v.getId());
		switch (v.getId()) {
		case R.id.fl_info_1:
			onClickScan1(v);
			break;
		case R.id.fl_info_2:
			onClickScan2(v);
			break;
		case R.id.btn_info_ok:
			onClickOk(v);
			break;
		default:
			break;
		}
	}

	public class HandlerCallBack implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_SCAN1_SUCCESS:
			case MSG_SCAN1_FAILED:
				if (msg.obj instanceof byte[]) {
					onScan1Result((byte[]) msg.obj);
				} else {
					onScan1Result(null);
				}
				break;
			case MSG_SCAN2_SUCCESS:
			case MSG_SCAN2_FAILED:
				if (msg.obj instanceof byte[]) {
					onScan2Result((byte[]) msg.obj);
				} else {
					onScan2Result(null);
				}
				break;
			case MSG_DO_SOMTING_SUCCESS:
			case MSG_DO_SOMTING_FAILED:
				onClickResult();
				break;
			default:
				break;
			}
			return false;
		}
	
	}

	@Override
	protected void initView() {
		super.initView();
		
		setContentView(R.layout.new_0424_activity_bag_link);

		vTitle = (ActivityHeadItemView) findViewById(R.id.v_title);

		// 第一个扫描框
		mFlInfo1 = findViewById(R.id.fl_info_1);
		tvScan1 = (TextView) mFlInfo1.findViewById(R.id.tv_info_scan);
		llShow1 = mFlInfo1.findViewById(R.id.ll_info_show);
		tvBagType1 = (TextView) mFlInfo1.findViewById(R.id.tv_info_money_type);
		tvClearingType1 = (TextView) mFlInfo1
				.findViewById(R.id.tv_info_integrity);
		tvWhich1 = (TextView) mFlInfo1.findViewById(R.id.tv_info_which);

		// 第二个扫描框
		mFlInfo2 = findViewById(R.id.fl_info_2);
		tvScan2 = (TextView) mFlInfo2.findViewById(R.id.tv_info_scan);
		llShow2 = mFlInfo2.findViewById(R.id.ll_info_show);
		tvBagType2 = (TextView) mFlInfo2.findViewById(R.id.tv_info_money_type);
		tvClearingType2 = (TextView) mFlInfo2
				.findViewById(R.id.tv_info_integrity);
		tvWhich2 = (TextView) mFlInfo2.findViewById(R.id.tv_info_which);

		btnOk = (Button) findViewById(R.id.btn_info_ok);
	}

	@Override
	protected void initEvent() {
		LogsUtil.d(TAG, "initEvent()");
		mFlInfo1.setOnClickListener(this);
		mFlInfo2.setOnClickListener(this);
		btnOk.setOnClickListener(this);
	}

	/**
	 * @param which
	 *            1=第一个扫描框, 其他=第二个扫描框
	 * @param isShow
	 *            true:显示扫描提示, false:显示信息
	 */
	protected void setScanShow(int which, boolean isShow) {
		if (1 == which) {
			if (isShow) {
				tvScan1.setVisibility(View.VISIBLE);
				llShow1.setVisibility(View.GONE);
			} else {
				tvScan1.setVisibility(View.GONE);
				llShow1.setVisibility(View.VISIBLE);
			}
		} else {
			if (isShow) {
				tvScan2.setVisibility(View.VISIBLE);
				llShow2.setVisibility(View.GONE);
			} else {
				tvScan2.setVisibility(View.GONE);
				llShow2.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * @param which
	 *            1=第一个扫描框, 其他=第二个扫描框
	 * @return 显示扫描提示 返回 true
	 */
	protected boolean isScanShow(int which) {
		if (1 == which) {
			return tvScan1.getVisibility() == View.VISIBLE;
		} else {
			return tvScan2.getVisibility() == View.VISIBLE;
		}
	}

	/**
	 * @param which
	 *            1=第一个扫描框, 其他=第二个扫描框
	 * @param bagTypeText
	 * @param clearingTypeText
	 */
	protected void setInfo(int which, String bagTypeText,
			String clearingTypeText) {
		if (1 == which) {
			tvBagType1.setText(bagTypeText);
			tvClearingType1.setText(clearingTypeText);
		} else {
			tvBagType2.setText(bagTypeText);
			tvClearingType2.setText(clearingTypeText);
		}
	}

	protected abstract void onClickScan1(View v);
	protected abstract void onScan1Result(byte[] bs);

	protected abstract void onClickScan2(View v);
	protected abstract void onScan2Result(byte[] bs);

	protected abstract void onClickOk(View v);
	protected abstract void onClickResult();

}
