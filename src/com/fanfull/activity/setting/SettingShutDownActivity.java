package com.fanfull.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.fff.R;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.ToastUtil;

public class SettingShutDownActivity extends BaseActivity {

	private TextView tvMinus;
	private SeekBar sbMinus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int minus = SPUtils.getInt(MyContexts.KEY_SHUTDOWN_DELAY, 30);
		sbMinus.setProgress(minus / 5);
	}
	
	@Override
	protected void initView() {
		setContentView(R.layout.activity_setting_shut_down);
		
		findViewById(R.id.tv_setting_item_screen).setOnClickListener(this);
		findViewById(R.id.btn_activity_shutdown_save).setOnClickListener(this);
		findViewById(R.id.btn_activity_shutdown_cancel).setOnClickListener(this);
		
		tvMinus = (TextView) findViewById(R.id.tv_setting_shut_down_1);
		tvMinus.setOnClickListener(this);
		
		sbMinus = (SeekBar) findViewById(R.id.sb_setting_shut_down_1);
		sbMinus.setMax(30);
		sbMinus.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (0 == progress) {
					tvMinus.setText("从不");
				} else {
					tvMinus.setText(String.valueOf(progress * 5));
				}
			}
		});
	}
	
	private final String PACKAGENAME = "com.android.settings";
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_setting_item_screen:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName(PACKAGENAME, PACKAGENAME + ".DisplaySettings");
			startActivity(intent);
			break;
		case R.id.btn_activity_shutdown_save:
			int minus = sbMinus.getProgress() * 5;
			SPUtils.putInt(MyContexts.KEY_SHUTDOWN_DELAY, minus);
			if (0 == minus) {
				ToastUtil.showToastInCenter("自动关机已取消");
			} else {
				ToastUtil.showToastInCenter("灭屏" + minus + "分钟后自动关机");
			}
			finish();
			break;
		case R.id.btn_activity_shutdown_cancel:
			finish();
			break;
		default:
			break;
		}
	}
}
