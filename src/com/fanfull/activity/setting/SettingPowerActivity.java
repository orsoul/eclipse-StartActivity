package com.fanfull.activity.setting;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.fff.R;
import com.fanfull.op.UHFOperation;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.ToastUtil;

public class SettingPowerActivity extends BaseActivity {

	/**
	 * 手持批量入库 和 手持补扫时 推荐使用的读功率
	 */
	public static final int POWER_READ_INSTORE = 20;
	/**
	 * 手持批量入库 和 手持补扫时 推荐使用的写功率
	 */
	public static final int POWER_WRITE_INSTORE = 20;
	/**
	 * 封袋 推荐使用的读功率
	 */
	public static final int POWER_READ_COVER = 10;
	/**
	 * 封袋 推荐使用的写功率
	 */
	public static final int POWER_WRITE_COVER = 20;

	private final int SET_POWER_SUCCESS = 1;
	private final int SET_POWER_FAILED = 2;

	private TextView tvCoverRead;
	private TextView tvCoverWrite;
	private TextView tvInstoreRead;
	private TextView tvInstoreWrite;

	private SeekBar sbCoverRead;
	private SeekBar sbCoverWrite;
	private SeekBar sbInstoreRead;
	private SeekBar sbInstoreWrite;

	private Button mBtnSave;
	private Button mBtnCancel;
	private Button mBtnSetNormal;

	private UHFOperation mUHFOp = null;
	/**
	 * 若等于0，说明超高频在别处被打开，在结束本activity时不关闭超高频
	 */
	private int openCode;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_setting_power);

		tvCoverRead = (TextView) findViewById(R.id.tv_setting_power_normal_read_power);
		tvCoverWrite = (TextView) findViewById(R.id.tv_setting_power_normal_write_power);
		tvInstoreRead = (TextView) findViewById(R.id.tv_setting_power_dynamic_read_power);
		tvInstoreWrite = (TextView) findViewById(R.id.tv_setting_power_dynamic_write_power);

		sbCoverRead = (SeekBar) findViewById(R.id.sb_setting_power_read_cover);
		sbCoverRead.setMax(25);
		sbCoverWrite = (SeekBar) findViewById(R.id.sb_setting_power_write_cover);
		sbCoverWrite.setMax(25);
		sbInstoreRead = (SeekBar) findViewById(R.id.sb_setting_power_read_instore);
		sbInstoreRead.setMax(25);
		sbInstoreWrite = (SeekBar) findViewById(R.id.sb_setting_power_write_instore);
		sbInstoreWrite.setMax(25);

		mBtnSave = (Button) findViewById(R.id.btn_activity_setting_power_save);
		mBtnCancel = (Button) findViewById(R.id.btn_activity_setting_power_cancel);
		mBtnSetNormal = (Button) findViewById(R.id.btn_activity_setting_power_set_normal);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();

		mUHFOp = UHFOperation.getInstance();
		openCode = mUHFOp.open(false);
		if (openCode < 0) {
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_init_uhf_failed));
			onBackPressed();
			return;
		}
		SystemClock.sleep(50); // 注意,主线程中休眠! 获取 UHFOperacation 实例后 要失眠一段时间才能 获取
								// 功率

		/* 获取 当前功率 */
		int currRead = 0;//
		int currWrite = 0;
		int[] rwPower = new int[2];
		if (0 < mUHFOp.getPower(rwPower)) {
			currRead = rwPower[0];
			currWrite = rwPower[1];
			// SPUtils.putInt(MyContexts.KEY_NORMAL_READ_POWER, currRead);
			// SPUtils.putInt(MyContexts.KEY_NORMAL_WRITE_POWER, currWrite);
			sbCoverRead.setSecondaryProgress(currRead - 5);
			sbCoverWrite.setSecondaryProgress(currWrite - 5);
		} else {
			ToastUtil.showToastInCenter("获取当前功率失败");
		}

		/* 获取 配置 */
		// 封袋 功率
		int coverRead = SPUtils.getInt(MyContexts.KEY_POWER_READ_COVER, -1);
		int coverWrite = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_COVER, -1);
		tvCoverRead.setText(String.valueOf(coverRead));
		tvCoverWrite.setText(String.valueOf(coverWrite));
		sbCoverRead.setProgress(coverRead - 5);
		sbCoverWrite.setProgress(coverWrite - 5);

		// 批量入库 功率
		int instoreRead = SPUtils.getInt(MyContexts.KEY_POWER_READ_INSTORE, -1);
		int instoreWrite = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_INSTORE,
				-1);
		tvInstoreRead.setText(String.valueOf(instoreRead));
		tvInstoreWrite.setText(String.valueOf(instoreWrite));
		sbInstoreRead.setProgress(instoreRead - 5);
		sbInstoreWrite.setProgress(instoreWrite - 5);

		// etNormalReadSeekBar.setOnSeekBarChangeListener(seekListener1);
		sbCoverRead.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Log.i(TAG,"onProgressChanged");
				String s = Integer.toString(progress / 4 + 5);
				tvCoverRead.setText(s);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});
		sbCoverWrite.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Log.i(TAG,"onStopTrackingTouch");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Log.i(TAG,"onStartTrackingTouch");
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Log.i(TAG,"onProgressChanged");

				String s = Integer.toString(progress / 4 + 5);

				tvCoverWrite.setText(s);
			}

		});

		sbInstoreRead.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Log.i(TAG,"onStopTrackingTouch");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Log.i(TAG,"onStartTrackingTouch");
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Log.i(TAG,"onProgressChanged");

				String s = Integer.toString(progress / 4 + 5);

				tvInstoreRead.setText(s);

			}
		});

		sbInstoreWrite
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// Log.i(TAG,"onStopTrackingTouch");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// Log.i(TAG,"onStartTrackingTouch");
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// Log.i(TAG,"onProgressChanged");

						String s = Integer.toString(progress / 4 + 5);

						tvInstoreWrite.setText(s);

					}
				});

	}

	@Override
	protected void initEvent() {
		super.initEvent();

		MyOnSeekBarChangeListener onSeekBarChangeListener = new MyOnSeekBarChangeListener();
		sbCoverRead.setOnSeekBarChangeListener(onSeekBarChangeListener);
		sbCoverWrite.setOnSeekBarChangeListener(onSeekBarChangeListener);
		sbInstoreRead.setOnSeekBarChangeListener(onSeekBarChangeListener);
		sbInstoreWrite.setOnSeekBarChangeListener(onSeekBarChangeListener);

		mBtnSave.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnSetNormal.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_activity_setting_power_save:
			try {
				final int coverRead = Integer.parseInt(tvCoverRead.getText()
						.toString());
				final int coverWrite = Integer.parseInt(tvCoverWrite.getText()
						.toString());
				final int instoreRead = Integer.parseInt(tvInstoreRead
						.getText().toString());
				final int instoreWrite = Integer.parseInt(tvInstoreWrite
						.getText().toString());
				// 判断 功率数值 是否 合法
				if ((30 < coverRead || coverRead < 5)
						|| (30 < coverWrite || coverWrite < 5)
						|| (30 < instoreRead || instoreRead < 5)
						|| (30 < instoreWrite || instoreWrite < 5)) {
					ToastUtil.showToastInCenter(getResources().getString(
							R.string.text_set_power_illegal));
					return;
				}

				if (0 < mUHFOp.setPower(coverRead, coverWrite)) {
					SPUtils.putInt(MyContexts.KEY_POWER_READ_COVER, coverRead);
					SPUtils.putInt(MyContexts.KEY_POWER_WRITE_COVER, coverWrite);
					SPUtils.putInt(MyContexts.KEY_POWER_READ_INSTORE,
							instoreRead);
					SPUtils.putInt(MyContexts.KEY_POWER_WRITE_INSTORE,
							instoreWrite);
					ToastUtil.showToastInCenter(getResources().getString(
							R.string.text_set_power_success));
					finish();
				} else {
					ToastUtil.showToastInCenter(getResources().getString(
							R.string.text_set_power_failed));
				}

				// ThreadPoolFactory.getNormalPool().execute(new Runnable() {
				// @Override
				// public void run() {
				// if (0 < mUHFOp.setPower(coverRead, coverWrite)) {
				// SPUtils.putInt(MyContexts.KEY_POWER_READ_COVER,
				// coverRead);
				// SPUtils.putInt(MyContexts.KEY_POWER_WRITE_COVER,
				// coverWrite);
				// SPUtils.putInt(MyContexts.KEY_POWER_READ_INSTORE,
				// instoreRead);
				// SPUtils.putInt(MyContexts.KEY_POWER_WRITE_INSTORE,
				// instoreWrite);
				// mHandler.sendEmptyMessage(SET_POWER_SUCCESS);
				// } else {
				// mHandler.sendEmptyMessage(SET_POWER_FAILED);
				// }
				// }
				// });
			} catch (NumberFormatException e) {
				LogsUtil.e("格式转换异常!");
				return;
			}

			break;
		case R.id.btn_activity_setting_power_cancel:
			finish();
			break;
		case R.id.btn_activity_setting_power_set_normal:
			tvCoverRead.setText(String.valueOf(POWER_READ_COVER));
			sbCoverRead.setProgress(POWER_READ_COVER - 5);

			tvCoverWrite.setText(String.valueOf(POWER_WRITE_COVER));
			sbCoverWrite.setProgress(POWER_WRITE_COVER - 5);

			tvInstoreRead.setText(String.valueOf(POWER_READ_INSTORE));
			sbInstoreRead.setProgress(POWER_READ_INSTORE - 5);

			tvInstoreWrite.setText(String.valueOf(POWER_WRITE_INSTORE));
			sbInstoreWrite.setProgress(POWER_WRITE_INSTORE - 5);
			break;

		default:
			break;
		}

	};

	// @Override
	// public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	//
	// }
	// return false;
	// };

	@Override
	protected void onDestroy() {
		if (null != mUHFOp && openCode != 0) {
			mUHFOp.close();
			mUHFOp = null;
		}
		super.onDestroy();
	};

	// private TextWatcher textWatcher1 = new TextWatcher() {
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before,
	// int count) {
	// //
	// // System.out.println("-1-onTextChanged-->"
	// // + edText.getText().toString() + "<--");
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	// //
	// // System.out.println("-2-beforeTextChanged-->"
	// // + edText.getText().toString() + "<--");
	//
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	// //
	// // System.out.println("-3-afterTextChanged-->"
	// // + edText.getText().toString() + "<--");
	// int value = Integer.valueOf(tvNormalRead.getText().toString());
	// etNormalReadSeekBar.setProgress(value*4);
	// }
	// };

	class MyOnSeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			switch (seekBar.getId()) {
			case R.id.sb_setting_power_read_cover:
				tvCoverRead.setText(String.valueOf(progress + 5));
				break;
			case R.id.sb_setting_power_write_cover:
				tvCoverWrite.setText(String.valueOf(progress + 5));
				break;
			case R.id.sb_setting_power_read_instore:
				tvInstoreRead.setText(String.valueOf(progress + 5));
				break;
			case R.id.sb_setting_power_write_instore:
				tvInstoreWrite.setText(String.valueOf(progress + 5));
				break;

			default:
				break;
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}

}
