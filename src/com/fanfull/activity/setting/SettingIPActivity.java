package com.fanfull.activity.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.ToastUtil;
import com.orsoul.view.IPEditText;

public class SettingIPActivity extends BaseActivity {

	private IPEditText mVSetIP1;
	private IPEditText mVSetIP2;
	private IPEditText mVSetIP3;
	private Button mBtnChangeIP;
	private Button mBtnQuit;

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		setContentView(R.layout.activity_setting_ip);

		mVSetIP1 = (IPEditText) findViewById(R.id.v_setting_ip_set_ip1);
//		mVSetIP1.setInputType(InputType.TYPE_NULL);

		mVSetIP2 = (IPEditText) findViewById(R.id.v_setting_ip_set_ip2);
//		mVSetIP2.setInputType(InputType.TYPE_NULL);
		
		mVSetIP3 = (IPEditText) findViewById(R.id.v_setting_ip_set_ip3);
//		mVSetIP3.setInputType(InputType.TYPE_NULL);
		
		if (SPUtils.getBoolean(MyContexts.KEY_LOT_DOOR, false)) {
			mVSetIP2.setVisibility(View.VISIBLE);
			mVSetIP3.setVisibility(View.VISIBLE);
			findViewById(R.id.tv_setting_ip_set_ip2).setVisibility(View.VISIBLE);
			findViewById(R.id.tv_setting_ip_set_ip3).setVisibility(View.VISIBLE);
		} else {
			mVSetIP2.setVisibility(View.GONE);
			mVSetIP3.setVisibility(View.GONE);
			findViewById(R.id.tv_setting_ip_set_ip2).setVisibility(View.GONE);
			findViewById(R.id.tv_setting_ip_set_ip3).setVisibility(View.GONE);
		}

		mBtnChangeIP = (Button) findViewById(R.id.btn_setting_ip_change);
		mBtnQuit = (Button) findViewById(R.id.btn_setting_ip_quit);
		

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();

		if (!TextUtils.isEmpty(StaticString.IP0)) {
			mVSetIP1.setIp(StaticString.IP0);
		}
		
		if (!TextUtils.isEmpty(StaticString.IP1)) {
			mVSetIP2.setIp(StaticString.IP1);
		}
		
		if (!TextUtils.isEmpty(StaticString.IP2)) {
			mVSetIP3.setIp(StaticString.IP2);
		}
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub
		super.initEvent();

		mBtnChangeIP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String ip0 = mVSetIP1.getText();
				if (null == ip0) {
					ToastUtil.showToastInCenter("请输入完整的IP1!");
					return;
				}
				SPUtils.putString(MyContexts.KEY_IP0, ip0);
				StaticString.IP0 = ip0;
				
				String ip1 = mVSetIP2.getText();
				if (null == ip1) {
					ToastUtil.showToastInCenter("请输入完整的IP2!");
					return;
				}
				SPUtils.putString(MyContexts.KEY_IP1, ip1);
				StaticString.IP1 = ip1;
				
				String ip2 = mVSetIP3.getText();
				if (null == ip2) {
					ToastUtil.showToastInCenter("请输入完整的IP3!");
					return;
				}
				SPUtils.putString(MyContexts.KEY_IP2, ip2);
				StaticString.IP2 = ip2;
				
				ToastUtil.showToastInCenter("IP修改成功");
				finish();
			}
		});

		mBtnQuit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
