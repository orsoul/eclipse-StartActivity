package com.fanfull.activity.scan_general;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.fanfull.activity.scan_lot.LotScanActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.fff.R;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.ToastUtil;

/**
 * 
 * 
 */
public class ChangeBagMainActivity extends BaseActivity {
	private ViewGroup ll1;
	private ViewGroup ll2;
	private int mReasonCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void initView() {
		setContentView(R.layout.activity_change_bag);
		
		ll1 = (ViewGroup) findViewById(R.id.change_bag_main);
		
		ll1.findViewById(R.id.btn_changebag_go_findout).setOnClickListener(this);
		ll1.findViewById(R.id.btn_changebag_go_open).setOnClickListener(this);
		ll1.findViewById(R.id.btn_changebag_go_change).setOnClickListener(this);
		
		ll2 = (ViewGroup) findViewById(R.id.change_bag_reason);
		MyOnCheckedChangeListener listener = new MyOnCheckedChangeListener();
		((CheckBox) ll2.findViewById(R.id.checkBox1)).setOnCheckedChangeListener(listener);
		((CheckBox) ll2.findViewById(R.id.checkBox2)).setOnCheckedChangeListener(listener);
		((CheckBox) ll2.findViewById(R.id.checkBox3)).setOnCheckedChangeListener(listener);
		((CheckBox) ll2.findViewById(R.id.checkBox4)).setOnCheckedChangeListener(listener);
		((CheckBox) ll2.findViewById(R.id.checkBox5)).setOnCheckedChangeListener(listener);
		((CheckBox) ll2.findViewById(R.id.checkBox6)).setOnCheckedChangeListener(listener);
		
		ll2.findViewById(R.id.change_bag_reason_btn_quit).setOnClickListener(this);
		ll2.findViewById(R.id.change_bag_reason_btn_ok).setOnClickListener(this);
		
	}
	private void showPanel(int which) {
		if (1== which) {
			ll1.setVisibility(View.VISIBLE);
			ll2.setVisibility(View.GONE);
		} else {
			ll2.setVisibility(View.VISIBLE);
			ll1.setVisibility(View.GONE);
		}
	}
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_changebag_go_findout:
			intent = new Intent(this, LotScanActivity.class);
			intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.FIND_MISSING_BAG);
			startActivity(intent);
			break;
		case R.id.btn_changebag_go_open:
			intent = new Intent(this, OpenNfcNewBagActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_changebag_go_change:
			showPanel(0);
			break;
		case R.id.change_bag_reason_btn_ok:
			if (0 != mReasonCode) {
				intent = new Intent(this, ChangeBagActivity.class);
				intent.putExtra(MyContexts.KEY_CHANGE_BAG_REASON, mReasonCode);
				startActivity(intent);
			} else {
				ToastUtil.showToastInCenter("请选择开袋原因");
			}
			break;
		case R.id.change_bag_reason_btn_quit:
			showPanel(1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	private class MyOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.checkBox1:
				if (isChecked) {
					mReasonCode |= 0x01;
				} else {
					mReasonCode &= 0xFE;
				}
				StaticString.reason = "00";

				break;
			case R.id.checkBox2:
				if (isChecked) {
					mReasonCode |= 0x02;
				} else {
					mReasonCode &= 0xFD;
				}
				StaticString.reason = "01";
				break;
			case R.id.checkBox3:
				if (isChecked) {
					mReasonCode |= 0x04;
				} else {
					mReasonCode &= 0xFB;
				}
				StaticString.reason = "02";
				break;
			case R.id.checkBox4:
				if (isChecked) {
					mReasonCode |= 0x08;
				} else {
					mReasonCode &= 0xF7;
				}
				StaticString.reason = "03";
				break;
			case R.id.checkBox5:
				if (isChecked) {
					mReasonCode |= 0x10;
				} else {
					mReasonCode &= 0xEF;
				}
				StaticString.reason = "04";
				break;
			case R.id.checkBox6:
				if (isChecked) {
					mReasonCode |= 0x20;
				} else {
					mReasonCode &= 0xDF;
				}
				StaticString.reason = "05";
				break;
			}
			LogsUtil.d(TAG, "mReason : " + Integer.toBinaryString(mReasonCode));
		}
		
	}
}
