package com.fanfull.activity.scan_general;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfull.activity.scan_general_oldbag.CIOActivity4oldBag;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.entity.OperationBean;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.fff.R.color;
import com.fanfull.operation.BaseBarcodeHelper;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ViewUtil;
import com.orsoul.view.MyProgressBar;
import com.orsoul.view.NumberInputer;

public class ScanBunchActivity extends BaseActivity{

	private final static int SHOW_LOCK_RESULT = 10;

	private final static String SCANNING = "暂停扫捆";
	private final static String START_SCAN = "开始扫捆";

	private boolean haveTaskRunning = false;// 扫捆线程是否在进行
	private final int WRONG_SCAN_GAS = 1000;// 扫描失败后 开启下次扫描的时间隔
	

	private final int QUIT_DIALOG = 0;
	private final int SAME_BARCODE_DIALOG = 1;
	private final int WRONG_BARCODE_DIALOG = 3;
	private final int OVER_LOCK_DIALOG = 4;
	private final int CHANGE_NUMBER_DIALOG = 5;
	

	private LinearLayout scanContent;
	private TextView mTvPlan;
	private TextView mTvReal;
	private MyProgressBar mProgreeBar;
	private Button mBtnStart;

	private int type = 0;// 当前activity的类型

	private int mPlanScanNum;
	private TextView mLockTextView;

	private BaseBarcodeHelper mBarcodeHelper;

	private List<String> barcodeList = new ArrayList<String>();// 保存条码数据
	private String reguler;

	private String pinumber;

	private OperationBean BaseInf;// 当前bean
	private boolean firstWrongBarcade = true;
	
	private BarcodeHelper.ReadBarcodeTask mReadBarcodeTask;
//	private List<Map<String, String>> mList = new ArrayList<Map<String,String>>();
	
	
	private boolean isFirstScan = false;//开始扫描语音只报一次
	private DialogUtil mDiaUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		StaticString.SCAN_OK = getInt(1);
		
		findViews();
		mBarcodeHelper = new BarcodeHelper(mHandler);
//		mBarcodeHelper.mBarCodeOp.connection();
		
		mReadBarcodeTask = mBarcodeHelper.getReadbBarcodeTask();

		try {
			if (StaticString.information.substring(1, 3).equals("18")) {
				pinumber = StaticString.information.substring(4, 22); // 批号
				StaticString.pinumber = pinumber;
				LogsUtil.s("批号pinumber:" + pinumber);
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			Toast.makeText(this, "没有收到后台消息", Toast.LENGTH_LONG).show();
		}
	}

	private void findViews() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_scan_bunch);
		type = getIntent().getIntExtra(MyContexts.KEY_OPERATION_TYPE, 0);
		BaseInf = new OperationBean(type);

		mBtnStart = (Button) findViewById(R.id.scan_ok);
		mBtnStart.setOnClickListener(this);
		mBtnStart.setEnabled(false);
//		mBtnStart.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if(!ScanBunchActivity.this.isFinishing()){
//					mBtnStart.setEnabled(true);
//					ViewUtil.requestFocus(mBtnStart);
//				}
//			}
//		}, 2000);
		mBtnStart.setEnabled(true);
		ViewUtil.requestFocus(mBtnStart);
		
		mPlanScanNum = SPUtils.getInt(ScanBunchActivity.this,
				MyContexts.KEY_BUNCH_NUM, 20);
		mLockTextView = (TextView) findViewById(R.id.tv_scanbunch_activity_lock);
		mLockTextView.setOnClickListener(this);
		
		mTvPlan = (TextView) findViewById(R.id.scan_planamount);// 计划数量
		mTvPlan.setText(mPlanScanNum + "");
		mTvPlan.setOnClickListener(this);

		mTvReal = (TextView) findViewById(R.id.real_amount);// 实际数量
		mProgreeBar = (MyProgressBar) findViewById(R.id.pb_scanbunch_activity);
		mProgreeBar.setMax(mPlanScanNum);

		scanContent = (LinearLayout) findViewById(R.id.scan_content);

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.scan_ok:
			if(isFirstScan){
				SoundUtils.playScanStartSound();
				isFirstScan = false;
			}
			mHandler.sendEmptyMessage(BaseBarcodeHelper.READ_BARCODE_START);
			break;
		case R.id.im_item_activity_back:
			closeAndFinsh();
			break;
		case R.id.tv_scanbunch_activity_lock:
			if (!haveTaskRunning) {
				showDialog(OVER_LOCK_DIALOG);
			}
			break;
		case R.id.scan_planamount:
			if (!haveTaskRunning) {
				showDialog(CHANGE_NUMBER_DIALOG);
			}
			break;

		}
	}

	/**
	 * @author lenovo
	 * @date 28/7-2015 14:09
	 * @describe 处理线程反馈的信息来显示相应的UI
	 */

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_LOCK_RESULT:

				final String info = ReplyParser.parseReply(StaticString.information);
				if(null!=info && info.equals("锁定批成功")){
					Drawable nav_up=getResources().getDrawable(R.drawable.lock);
					nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
					mLockTextView.setCompoundDrawables(null, nav_up,null , null);
					mLockTextView.setEnabled(false);
				}
				new TipDialog().createDialog(ScanBunchActivity.this, 0);
				break;
			default:
				mBarcodeHelper.handleResult(msg.what);
				break;
			}
		}

	};

	/**
	 * 记录 服务器 回复超时的次数
	 */
	private int timeoutCount;

	class BarcodeHelper extends BaseBarcodeHelper {

		public BarcodeHelper(Handler handler) {
			super(handler);
		}

		@Override
		public void startScan() {
			LogsUtil.s("READ_BARCODE_START");
			if (haveTaskRunning) {
				LogsUtil.s("isScanning");
				stopScanTask();
				return;
			}

			// 显示完读卡操作结果后 开启读头扫条码
			mProgreeBar.setText("请继续扫捆...");
			// mTvReal.setText("0");
			mBtnStart.setText(SCANNING);
			// delayScan(100);
			// mBarCodeOp.scan(); // 打开摄像头开关
			this.mBarCodeOp.connection();// 初始化摄像头
			haveTaskRunning = true;
			ThreadPoolFactory.getNormalPool().execute(mReadBarcodeTask);
		}

		@Override
		public void handleOK() {
			LogsUtil.s("ok");
			mScannedNum++;
			// 判断 扫捆 是否完成
			if (mScannedNum == mPlanScanNum) {
				mReadBarcodeTask.stop();
				SoundUtils.playNumber(mScannedNum);
				update(); // 更新相关控件
//				mHandler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						SoundUtils.playScanFinishSound();
//					}
//				}, 700);
				haveTaskRunning = false;
				mProgreeBar.setText("扫捆已完成!");
				mBtnStart.setText("已完成");
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						startNextActivity();
					}
				}, 2000);
				// startNextActivity();
			} else if (mScannedNum < mPlanScanNum) {
				SoundUtils.playNumber(mScannedNum);
				update(); // 更新相关控件
				StaticString.SCAN_OK = getInt(mScannedNum + 1);
				haveTaskRunning = false;
				mHandler.sendEmptyMessage(BaseBarcodeHelper.READ_BARCODE_START);
			} else {
				LogsUtil.e("超出计划数量");
			}
		}

		@Override
		public void handleBad() {
			SoundUtils.playFailedSound();
			LogsUtil.s("READ_BARCODE_BAD");
			changFocusToTrue();
			TipDialog tip1 = new TipDialog();
			tip1.createDialog(ScanBunchActivity.this, 0);
			stopScanTask();
		}

		@Override
		public void handleWrong() {
			firstWrongBarcade = false;
			LogsUtil.s("handle wrong barcode");
			if (!firstWrongBarcade) {
				SoundUtils.playFailedSound();
				showDialog(WRONG_BARCODE_DIALOG);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						dismissDialog(WRONG_BARCODE_DIALOG);
						// delayScan(0);
						// mReadBarcodeTask.notify();
					}
				}, WRONG_SCAN_GAS);
				mBtnStart.setText("继续扫描");
				haveTaskRunning = false;

			} else {
				// 第一打开扫描,会读到一个错误的barcode,忽略这个错误barcode不向用户提示
				mReadBarcodeTask.stop();
				haveTaskRunning = false;
				firstWrongBarcade = false;
				mHandler.sendEmptyMessageDelayed(READ_BARCODE_START, 1000);
				LogsUtil.s("firstWrongBarcade");
			}
		}

		@Override
		public void handleTimeout() {
			SoundUtils.playFailedSound();
			Toast.makeText(getApplicationContext(), "服务器回复超时!",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void handleSameBarcode() {
			haveTaskRunning = false;
			mBtnStart.setText("继续扫捆");
			SoundUtils.playFailedSound();
			showDialog(SAME_BARCODE_DIALOG);
			mBtnStart.performClick();
		}

		@Override
		public void handleSameBarcodeFinish() {
			mBtnStart.setText("继续扫捆");
			dismissDialog(SAME_BARCODE_DIALOG);
			
		}

		@Override
		protected boolean remoteCheckCorrect() {
			return pasrePiNumber();
		}

		@Override
		protected boolean localCheckCorrect() {
			return judgedata(StaticString.barcode);
		}
		@Override
		protected boolean netCheckCorrect() {
			// TODO Auto-generated method stub
			if(SocketConnet.getInstance().isConnect()){
				return true;
			}else {
				return false;
			}
			
		}
	}

	/**
	 * 记录 已扫描的 捆数
	 */
	int mScannedNum = 0; // 记录 已扫描的 捆数
	int z = 1;

	/**
	 * @Title: pasrePiNumber
	 * @Description:解析批信息
	 * @param 设定文件
	 * @return booelan : ture,数据正确，下一步;false,数据异常
	 */
	public Boolean pasrePiNumber() {
		// 回复信息: *77 00 034#
		// 77 00 == 正常通过
		// 77 01 == 重复捆编号
		// 77 03 == 后台服务器保存数据异常
		// int info = Integer.valueOf(StaticString.information.substring(4, 6));
		if ("00".equals(StaticString.information.substring(4, 6))) {
			barcodeList.add(new String(StaticString.barcode));
			return true;
		}
		return false;
	}

	private void update() {
		mTvReal.setText(mScannedNum + "");
		mProgreeBar.incrementProgressBy(1);
	}

	/**
	 * @Description: 判断是否当前bar编号 是否已经扫描过, 以及格式是否正确
	 * @param byte[] epc // 303230343030303135413231
	 * @return 返回 true 表示 barcode 未被扫描过
	 */
	public boolean judgedata(String epc) { // 303230343030303135413231
		if (null == epc || barcodeList.contains(epc)) {
			return false;
		}

		// 截取 券别以及券种 代号
		String tmp = epc.substring(8, 11);
		if (0 == barcodeList.size()) {
			// 本袋中的第一个barcode, 记录此barcode的券别和券种
			reguler = tmp;
		} else {
			// 券别或券种不匹配
			if (!tmp.equals(reguler)) {
				return false;
			}
		}

		return true;
	}
 
	/**
	 * 设置 确定按钮字体 为黑色 和 可点击
	 */
	public void changFocusToTrue() {
		mBtnStart.setTextColor(getResources().getColor(color.black));
		mBtnStart.setClickable(true);
	}

	/**
	 * @param number
	 * @return 返回 numberb 的字符串形式, number不足两位的在前面 补 '0'
	 */
	public static String getInt(int number) {
		if (number < 10) {
			return "0" + number;
		} else {
			return Integer.toString(number);
		}
	}


	/**
	 * 
	 * @Title: pasrePiNumber
	 * @Description:解析任务信息
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void pasreTaskNumber(int arg2) {
		Log.v("TestBagOperation", StaticString.information);
//		pinumber = StaticString.information.substring(4, 26);
//		StaticString.pinumber = pinumber;
//		String planString = mList.get(arg2).get("task");
//		String sum = planString.substring(planString.length() - 6,
//				planString.length() - 3);
		// plantv.setText(String.valueOf(Integer.valueOf(sum)));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mReadBarcodeTask.stop();
		haveTaskRunning = false;
		type = 0;
	}

	/**
	 * 开启 下一个Activity, 并关闭当前 Activity 和 资源
	 */
	private void startNextActivity() {
		mBarcodeHelper.close();
//		Intent intent = new Intent(ScanBunchActivity.this,CoverNewBagActivity.class);
//		Intent intent = new Intent(ScanBunchActivity.this,CoverNewBagActivityTwo.class);
		Intent intent;
		if(SPUtils.getBoolean(ScanBunchActivity.this,
				MyContexts.KEY_USE_OLDBAG_ENABLE, false)){
			LogsUtil.d(TAG,"---Cover old bag---");
			intent = new Intent(ScanBunchActivity.this,CIOActivity4oldBag.class);
		}else {
			intent = new Intent(ScanBunchActivity.this,CoverNfcNewBagActivity.class);
		}
		startActivity(intent);
		closeAndFinsh();
	}

	private void closeAndFinsh() {
		barcodeList.clear();
		finish();
	}

	/**
	 * 延迟一定时间后 打开扫描
	 */
	private void delayScan(int delay) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mBarcodeHelper.scan();
			}
		}, delay);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (haveTaskRunning) {
				stopScanTask();
			} else {
					showDialog(QUIT_DIALOG);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new Builder(this);
		switch (id) {
		case QUIT_DIALOG:
			// AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("警告").setMessage("退出后之前的扫捆记录将被清空,确定要退出?");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							closeAndFinsh();
						}
					});
			builder.setNegativeButton("取消", null);
			return builder.create();
			/* 锁定批的 对话框 */
		case OVER_LOCK_DIALOG:
			// AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
					MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							StaticString.information = null;
							SocketConnet.getInstance().communication(7);// 锁定批

							ThreadPoolFactory.getNormalPool().execute(
									new Runnable() {
										@Override
										public void run() {
											// 在子线程中 检查 StaticString.information
											// 是否为空
											if (ReplyParser.waitReply()) {
												mHandler.sendEmptyMessage(SHOW_LOCK_RESULT);
											}
										}
									});
						}
					});
			builder.setNegativeButton("取消", null);
			return builder.create();
		case SAME_BARCODE_DIALOG:
			// builder.setTitle(MyContexts.DIALOG_TITLE).setMessage(MyContexts.MESSAGE_SCANED);
			// return builder.create();
		case WRONG_BARCODE_DIALOG:
			builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage("");
			return builder.create();
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
						vNumInputer.setNumber(1);
						break;
					case R.id.btn_item_dialog_set_ten:
						vNumInputer.setNumber(10);
						break;
					case R.id.btn_item_dialog_set_twinty:
						vNumInputer.setNumber(20);
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
									} else if (num <= mScannedNum) {
										num = mScannedNum + 1;
									}
									mTvPlan.setText(String.valueOf(num));
									mPlanScanNum = num;
									mProgreeBar.setMax(mPlanScanNum);
									mProgreeBar.setProgress(mScannedNum);
									SPUtils.putInt(getApplicationContext(),
											MyContexts.KEY_BUNCH_NUM,
											mPlanScanNum);
								}
							}).setNegativeButton("取消", null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case SAME_BARCODE_DIALOG:
			((AlertDialog) dialog).setMessage(MyContexts.DIALOG_MESSAGE_SCANED);
			break;
		case WRONG_BARCODE_DIALOG:
			((AlertDialog) dialog)
					.setMessage(MyContexts.DIALOG_MESSAGE_BARCODE_WRONG);
			break;

		default:
			break;
		}
	}

	private void showSameBarcodeDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showDialog(SAME_BARCODE_DIALOG);
			}
		});
	}

	private void dismissSameBarcodeDialog() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dismissDialog(SAME_BARCODE_DIALOG);
			}
		});
	}
	/**
	 * 停止扫捆线程,以及更新相关的 控件
	 */
	private void stopScanTask() {
		mBarcodeHelper.stopScan();
		mReadBarcodeTask.stop();
		haveTaskRunning = false;
		mProgreeBar.setText("扫捆已停止!");
		mBtnStart.setText(START_SCAN);
	}
	
}
