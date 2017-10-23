package com.fanfull.activity.scan_general;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.op.BarcodeOperation;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.hardware.Hardware;
import com.orsoul.view.MyProgressBar;
import com.orsoul.view.NumberInputer;

public class ScanBunchActivity extends BaseActivity {

	private final static String TEXT_PAUSE_SCAN = "暂停扫捆";
	private final static String TEXT_START_SCAN = "开始扫捆";

	/** 捆封签数量设置对话框 */
	private final int CHANGE_NUMBER_DIALOG = 5;

	/** 扫描到捆封签 */
	private final int MSG_SCAN_RESULT = 1;
	
	/** 扫描捆封签完毕 */
	private final int MSG_SCAN_FINISH = 2;
	
	/** 长时间未扫到 捆封签 */
	private final int MSG_SCAN_TIMEOUT = 3;
	
	/** 前置检查捆封签 回复结果 */
	private final int MSG_CHECK_RESULT = 4;
	
	/** 前置 回复结果 超时 */
	private final int MSG_CHECK_TIMEOUT = 5;

	private TextView mTvPlan;
	private TextView mTvReal;
	private MyProgressBar mProgreeBar;
	private Button mBtnStart;

	private int mPlanScanNum;

	private ScanTask mScanTask;

	private byte[] barcodeBuf;

	private List<String> barcodeList;

	private String barcodeType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mDiaUtil = new DialogUtil(this);
		mDiaUtil.showProgressDialog();
		if (BarcodeOperation.getInstance().open(false) < 0) {
			mDiaUtil.dismissProgressDialog();
			mDiaUtil.showDialogFinishActivity(getResources().getString(
					R.string.text_init_barcode_failed));
			return;
		}
		mDiaUtil.dismissProgressDialog();

		mHandler = new Handler(new MyHandlerCallback());

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		barcodeList = new ArrayList<String>();

		barcodeBuf = new byte[26];
		mScanTask = new ScanTask();
		mScanTask.setRunTime(8 * 1000);
		mScanTask.setReadTimes(-1);
		SocketConnet.getInstance().setRecieveListener(
				new ScanBundleRecieveListener());

	}

	@Override
	protected void initView() {
		mPlanScanNum = SPUtils.getInt(MyContexts.KEY_BUNCH_NUM, 20);

		setContentView(R.layout.activity_scan_bunch_content);

		mBtnStart = (Button) findViewById(R.id.scan_ok);
		mBtnStart.setOnClickListener(this);
		mBtnStart.setEnabled(true);
		ViewUtil.requestFocus(mBtnStart);

		mTvPlan = (TextView) findViewById(R.id.scan_planamount);// 计划数量
		mTvPlan.setText(mPlanScanNum + "");
		mTvPlan.setOnClickListener(this);

		mTvReal = (TextView) findViewById(R.id.real_amount);// 实际数量
		mProgreeBar = (MyProgressBar) findViewById(R.id.pb_scanbunch_activity);
		mProgreeBar.setMax(mPlanScanNum);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.scan_ok:

			if (mScanTask.isRunning()) {
				mScanTask.stop();
				mProgreeBar.setText("扫捆已停止!");
				mBtnStart.setText(TEXT_START_SCAN);
				return;
			}

			// 显示完读卡操作结果后 开启读头扫条码
			mProgreeBar.setText("请继续扫捆...");
			mBtnStart.setText(TEXT_PAUSE_SCAN);
			ThreadPoolFactory.getNormalPool().execute(mScanTask);
			break;
		case R.id.im_item_activity_back:
			onBackPressed();
			break;
		case R.id.scan_planamount:
			if (!mScanTask.isRunning()) {
				showDialog(CHANGE_NUMBER_DIALOG);
			}
			break;

		}
	}

	private void incrementProgress() {
		mProgreeBar.incrementProgressBy(1);
		mTvReal.setText(mProgreeBar.getProgress() + "");
	}

	private void resetProgress() {
		mTvReal.setText("-");
		mProgreeBar.setProgress(0);
		mBtnStart.setText(TEXT_START_SCAN);
		barcodeList.clear();
		barcodeType = null;
	}

	private String checkBarcode(String barcode) {

		if (true) {
			return null;// TODO
		}

		if (barcode == null) {
			return "";
		}

		// 1, 判断是否已扫描
		if (barcodeList.contains(barcode)) {
			return "该捆已扫描";
		}

		// 2, 判断 格式
		if (!barcode.matches("^[A-Za-z0-9]{5}[12][ABCD][A-Za-z0-9]{18}$")) {
			return "捆封签格式错误";
		}

		// 3, 判断 当前捆封签 的类型 是否 与之前的一致
		if (null == barcodeType) {
			// 记录第一个 捆封签的类型： 机构、券别、面值
			barcodeType = barcode.substring(0, 7);
		} else if (!barcode.startsWith(barcodeType)) {
			return "捆封签类型不一致";
		}

		return null;
	}

	/**
	 * 开启 下一个Activity, 并关闭当前 Activity 和 资源
	 */
	private void startNextActivity() {
		Intent intent = new Intent(ScanBunchActivity.this,
				CoverNfcNewBagActivity.class);
		intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.COVER_BAG);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (mScanTask.isRunning()) {
			mScanTask.stop();
			mProgreeBar.setText("扫捆已停止!");
			mBtnStart.setText(TEXT_START_SCAN);
			return;
		}
		
		mDiaUtil.showDialog2Button("当前扫描进度将会丢失，是否确定离开？", "离开", "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}, null);

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		mScanTask.stop();
		barcodeList.clear();
		barcodeList = null;
		BarcodeOperation.getInstance().close();
		super.onDestroy();
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new Builder(this);
		switch (id) {
		case CHANGE_NUMBER_DIALOG:
			// 设置 对话框 的样式
			View view = View.inflate(getApplicationContext(),
					R.layout.dialog_scan_bunch_plan_num, null);
			builder.setView(view);

			// 为 对话框 上的按钮设置监听器
			final NumberInputer vNumInputer = (NumberInputer) view
					.findViewById(R.id.v_item_nubmer_input);
			vNumInputer.setNumber(mPlanScanNum);
			OnClickListener myDialogListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.btn_item_dialog_set_one:
						vNumInputer.setNumber(5);
						break;
					case R.id.btn_item_dialog_set_ten:
						vNumInputer.setNumber(15);
						break;
					case R.id.btn_item_dialog_set_twinty:
						vNumInputer.setNumber(28);
						break;
					}
				}
			};
			view.findViewById(R.id.btn_item_dialog_set_one).setOnClickListener(
					myDialogListener);
			view.findViewById(R.id.btn_item_dialog_set_ten).setOnClickListener(
					myDialogListener);
			view.findViewById(R.id.btn_item_dialog_set_twinty)
					.setOnClickListener(myDialogListener);

			builder.setTitle("修改扫捆数量")
					.setPositiveButton("保存",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									int num = vNumInputer.getNumber();
									if (30 < num) {
										num = 30;
									} else if (num <= barcodeList.size()) {
										num = barcodeList.size() + 1;
									}
									mTvPlan.setText(String.valueOf(num));
									mPlanScanNum = num;
									mProgreeBar.setMax(mPlanScanNum);
									mProgreeBar.setProgress(barcodeList.size());
									SPUtils.putInt(getApplicationContext(),
											MyContexts.KEY_BUNCH_NUM,
											mPlanScanNum);
								}
							}).setNegativeButton("取消", null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	private class MyHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO
			switch (msg.what) {
			case MSG_SCAN_RESULT:
				if (null != msg.obj) {
					ToastUtil.showToastInCenter(msg.obj);
					break;
				}

				incrementProgress();
				SoundUtils.playNumber(mProgreeBar.getProgress());

				break;
			case MSG_SCAN_FINISH:
				incrementProgress();
				SoundUtils.playScanFinishSound();

				StringBuilder sb = new StringBuilder();
				int num = barcodeList.size();
				for (int i = 0; i < num; i++) {
					sb.append(barcodeList.get(i)).append(',');
				}
				sb.setLength(sb.length() - 1);

				mDiaUtil.showProgressDialog("正在校验捆封签...");
				SocketConnet.getInstance().communication(
						SendTask.CODE_UPLODE_PACKETS, String.valueOf(num),
						sb.toString());
				break;
			case MSG_SCAN_TIMEOUT:
				ToastUtil.showToastInCenter("长时间未扫描到数据");
				mBtnStart.setText(TEXT_START_SCAN);
				break;
			case MSG_CHECK_RESULT:
				mDiaUtil.dismissProgressDialog();
				if (!(msg.obj instanceof String)) {
					SoundUtils.playFailedSound();
					mDiaUtil.showDialog("服务器回复异常");
					barcodeList.clear();
					break;
				}
				String[] split = msg.obj.toString().split(" ");
				if ("*77".equals(split[0]) && "0".equals(split[1])) {
					SocketConnet.getInstance().setRecieveListener(null);
					SoundUtils.playDropSound();
					startNextActivity();
				} else {
					SoundUtils.playFailedSound();
					mDiaUtil.showDialog("校验失败");
					resetProgress();
				}
				break;
			case MSG_CHECK_TIMEOUT:
				mDiaUtil.dismissProgressDialog();
				SoundUtils.playFailedSound();
				mDiaUtil.showDialog("回复超时");
				break;
			}
			return true;
		}

	}

	private class ScanTask implements Runnable {

		private BarcodeOperation op = BarcodeOperation.getInstance();
		/** 未扫描到数据时 扫描线程 持续时间 运行时间 */
		private long runTime = 5000;
		/** 读取数据 次数 */
		private int readTimes = 1;
		private boolean isRunning;

		public boolean isRunning() {
			return isRunning;
		}

		public void stop() {
			this.isRunning = false;
		}

		public void setRunTime(long runTime) {
			this.runTime = runTime;
		}

		public void setReadTimes(int readTimes) {
			this.readTimes = readTimes;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			isRunning = true;

			op.clearBuffer();

			int len = -1;
			long stopTime = runTime + System.currentTimeMillis();
			while (isRunning && 0 != readTimes
					&& (System.currentTimeMillis() < stopTime)) {
				LogsUtil.d(TAG, readTimes + "  " + System.currentTimeMillis());

				op.light(true);

				int select = Hardware.getInstance().select(op.getFd(), 0,
						200 * 1000);

				// 扫到 捆封签 控制读头停止扫描
				op.light(false);
				SystemClock.sleep(50);

				if (select != 1) {
					LogsUtil.d(TAG, "select failed");
					continue;
				}

				len = Hardware.getInstance().read(op.getFd(), barcodeBuf,
						barcodeBuf.length);
				LogsUtil.d(TAG, "read() len:" + len);

				if (0 < len) {// 扫描到数据，检查数据 并 通知ui线程

					Message msg = mHandler.obtainMessage();
					msg.what = MSG_SCAN_RESULT;

					String barcode = new String(barcodeBuf, 0, len).trim();

					LogsUtil.d(TAG, "scan data:" + barcode);

					msg.obj = checkBarcode(barcode);
					if (null == msg.obj) {
						barcodeList.add(barcode);
						if (mPlanScanNum <= barcodeList.size()) {
							msg.what = MSG_SCAN_FINISH;
							stop();
						}
					}
					mHandler.sendMessage(msg);

					// 扫描到数据，重置停止时间
					stopTime = runTime + System.currentTimeMillis();

				} // end if(1 < len)

				readTimes--;
				SystemClock.sleep(20);
			} // end while

			if (isRunning) {
				// 长时间未 扫描到 数据
				mHandler.sendEmptyMessage(MSG_SCAN_TIMEOUT);
				// Message msg = mHandler.obtainMessage();
				// msg.what = MSG_SCAN_TIMEOUT;
				// mHandler.sendMessage(msg);
			}
			isRunning = false;
		}
	}

	private class ScanBundleRecieveListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			// TODO Auto-generated method stub
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_CHECK_RESULT;
			msg.obj = recString;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onTimeout() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(MSG_CHECK_TIMEOUT);
		}

	}

}
