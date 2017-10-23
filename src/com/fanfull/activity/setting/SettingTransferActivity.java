package com.fanfull.activity.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.fff.R;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.ToastUtil;

public class SettingTransferActivity extends BaseActivity {

	private CheckBox mCbSerial;
	private CheckBox mCbWifi;
	private Spinner mSpBaudrate;
	private EditText mEtBaudrate;

	private String[] baudrateArrs;
	private int mBaudratePos;
	private int mBaudrateVal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		baudrateArrs = getResources().getStringArray(R.array.baudrate);

		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting_transfer);

		mCbSerial = (CheckBox) findViewById(R.id.cb_setting_transfer_serial);
		mCbWifi = (CheckBox) findViewById(R.id.cb_setting_transfer_wifi);
		OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (!mCbSerial.isChecked() && !mCbWifi.isChecked()) {
					ToastUtil.showToastInCenter("必须选用一种传输方式");
					buttonView.setChecked(true);
					return;
				}

				if (isChecked) {
					buttonView.setTextColor(Color.GREEN);
				} else {
					buttonView.setTextColor(Color.GRAY);
				}

				mSpBaudrate.setEnabled(mCbSerial.isChecked());
				mEtBaudrate.setEnabled(mCbSerial.isChecked()
						&& (mBaudratePos == baudrateArrs.length - 1));

				// switch (buttonView.getId()) {
				// case R.id.cb_setting_transfer_serial:
				// break;
				// case R.id.cb_setting_transfer_wifi:
				// break;
				// }
			}
		};

		mEtBaudrate = (EditText) findViewById(R.id.et_setting_transfer_baudrate);
		mEtBaudrate.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (0 < count) {
					mBaudrateVal = Integer.parseInt(s.toString());
				} else {
					mBaudrateVal = 0;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mSpBaudrate = (Spinner) findViewById(R.id.sp_setting_transfer_baudrate);
		mSpBaudrate.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				LogsUtil.d(TAG, position + " item:" + baudrateArrs[position]);
				mBaudratePos = position;
				if (position == baudrateArrs.length - 1) {
					// mEtBaudrate.setVisibility(View.VISIBLE);
					mEtBaudrate.setEnabled(mCbSerial.isChecked());
				} else {
					// mEtBaudrate.setVisibility(View.GONE);
					mEtBaudrate.setEnabled(false);
					mEtBaudrate.setText(baudrateArrs[mBaudratePos]);
					mBaudrateVal = Integer.parseInt(baudrateArrs[mBaudratePos]);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		mBaudratePos = SPUtils.getInt(MyContexts.KEY_BAUDRATE_POS,
				baudrateArrs.length - 1);
		if (mBaudratePos == baudrateArrs.length - 1) {
			mBaudrateVal = SPUtils.getInt(MyContexts.KEY_BAUDRATE_VALUE, 19200);
			// mEtBaudrate.setVisibility(View.VISIBLE);
			mEtBaudrate.setEnabled(mCbSerial.isChecked());
			mEtBaudrate.setText(mBaudrateVal + "");
		}

		mSpBaudrate.setSelection(mBaudratePos);

		mCbSerial.setOnCheckedChangeListener(onCheckedChangeListener);
		mCbWifi.setOnCheckedChangeListener(onCheckedChangeListener);
		mCbSerial.setChecked(SPUtils.getBoolean(MyContexts.KEY_SERIAL_ENABLE));
		mCbWifi.setChecked(SPUtils.getBoolean(MyContexts.KEY_WIFI_ENABLE, true));

	}

	@Override
	public void onBackPressed() {
		SPUtils.putInt(MyContexts.KEY_BAUDRATE_POS, mBaudratePos);
		SPUtils.putInt(MyContexts.KEY_BAUDRATE_VALUE, mBaudrateVal);
		SPUtils.putBoolean(MyContexts.KEY_SERIAL_ENABLE, mCbSerial.isChecked());
		SPUtils.putBoolean(MyContexts.KEY_WIFI_ENABLE, mCbWifi.isChecked());
		ToastUtil.showToastInCenter("设置成功，重启程序后生效");
		super.onBackPressed();
	}
}
