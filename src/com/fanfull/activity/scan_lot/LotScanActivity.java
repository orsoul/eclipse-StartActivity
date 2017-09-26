package com.fanfull.activity.scan_lot;

import java.util.Collection;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.activity.setting.SettingPowerActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.NfcOperation;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;
import com.orsoul.view.MyProgressBar;
import com.orsoul.view.NumberInputer;

/**
 * 手持批量扫描，包括：入库，出库，补扫
 * 
 * @author orsoul
 * 
 */
public class LotScanActivity extends BaseActivity {
	/**
	 * 手持 批量入库
	 */
	public static final int TYPE_LOT_INSTORE = 0;
	/**
	 * 手持 预扫描补扫
	 */
	public static final int TYPE_PRE_SCAN = 1;
	/**
	 * 手持 入库补扫
	 */
	public static final int TYPE_MEND_SCAN = 2;
	/**
	 * 预扫描 补扫 完成
	 */
	public static final int RESULT_CODE_PRE_FINISH = 1;
	/**
	 * 预扫描 补扫 未完成
	 */
	public static final int RESULT_CODE_PRE_UNFINISH = 2;
	/**
	 * 交接 补扫 完成一托盘
	 */
	public static final int RESULT_CODE_MEND_FINISH_TP = 3;
	/**
	 * 交接 补扫 完成全部
	 */
	public static final int RESULT_CODE_MEND_FINISH_ALL = 4;
	/**
	 * 交接 补扫 未完成
	 */
	public static final int RESULT_CODE_MEND_UNFINISH = 5;

	// TODO
	private static final String[] TEXT_TITLES = { "批量入库", "预扫补扫", "入库补扫",
			"批量出库", };
	private static final String TEXT_START_SCAN = "开始扫描";
	private static final String TEXT_STOP_SCAN = "停止";
	private static final String TEXT_QUIT = "离开";
	private static final String TEXT_TITLE_SET_SALVER = "修改托盘袋数";
	private static final String TEXT_TITLE_SET_OUTSTORE_PLAN_NUM = "预计出库数量";

	private final int MSG_ADD_ONE = 0;
	private final int MSG_GET_INSTORE_INFO_OVER = 2;
	private final int MSG_LONG_TIME_NO_SCAN = 3;
	private final int MSG_LONG_TIME_NO_REPLY = 4;
	private final int MSG_SCAN_FINISH = 5;
	private final int MSG_ADD_ONE_OUT = 6;

	private final int MSG_SHOW_LOCK_RESULT = 11;
	private final int MSG_SET_SALVER_NUM = 12;

	private ActivityHeadItemView mVtitle;
	private View mImgSwich;
	private TextView mTvPlanNum;
	private TextView mTvScannedNum;
	private MyProgressBar mProgreeBar;
	private Button mBtnOk;
	private TextView mTvLock;

	private boolean haveTaskRunning;
	private boolean haveDone; // 当前补扫是否完成
	private int mType;
	/** 天线柜工作模式，手持批量扫描模式与其一致：: 0锁模式， 1锁或标牌，2锁与标牌 */
	private int mDoorWorkMode;

	private ScanTask mScanTask;
	/** 保存袋id。1、入库时，保存任务列表；2、出库时，保存已出库袋id */
	private Collection<String> mBagIdList;
	/** 保存 扫描到的 待入库的标牌id（8开头的id），袋和锁模式 中使用 */
	private Collection<String> mScannedLableList;
	/** 保存 扫描到的 待入库的袋id，袋和锁模式 中使用 */
	private Collection<String> mScannedBagList;

	private UHFOperation mUhfOp;
	private boolean isUHFMode = true;
	/**
	 * 已扫描的数量
	 */
	private int mScannedNum;
	/**
	 * 计划数量
	 */
	private int mPlanNum;

	private int commCode;
	private int mSalverNum = 28;
	private LotInRecieveListener mRecieveListener;

	// private SparseBooleanArray mActive = new SparseBooleanArray();
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fanfull.base.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO
		mType = getIntent().getIntExtra(TYPE_OP.KEY_TYPE,
				TYPE_OP.IN_STORE_HAND_LOT);
		mDoorWorkMode = getIntent().getIntExtra(MyContexts.KEY_DOOR_MODLE, 0);
		LogsUtil.d(TAG, "mType:" + mType);
		LogsUtil.d(TAG, "mDoorWorkMode:" + mDoorWorkMode);
		if (2 == mDoorWorkMode) {
			mScannedLableList = new HashSet<String>();
			mScannedBagList = new HashSet<String>();
		}

		isUHFMode = SPUtils.getBoolean(MyContexts.KEY_SCAN_MODE, true);
		mDiaUtil = new DialogUtil(this);
		mHandler = new Handler(new MyHandlerCallback());
		mRecieveListener = new LotInRecieveListener();

		SocketConnet.getInstance().setRecieveListener(mRecieveListener);

		if (TYPE_OP.OUT_STORE_HAND_LOT == mType) {
			// 出库
			mUhfOp = UHFOperation.getInstance();
			setLotScanPower(true);
			mScanTask = new ScanTask();
			showInputNum(TEXT_TITLE_SET_OUTSTORE_PLAN_NUM);
			mBagIdList = new HashSet<String>();
			findView();
		} else if (TYPE_OP.IN_STORE_HAND_LOT == mType) {
			// 手持批量入库 // 从 服务端 获取任务信息
			SocketConnet.getInstance().communication(
					SendTask.CODE_LOT_INSTORE_NUM);
			waitReply(SendTask.CODE_LOT_INSTORE_NUM, MSG_GET_INSTORE_INFO_OVER);
			mDiaUtil.showProgressDialog();
		} else {
			// 预扫描、交接 补扫 // 从 服务端 获取任务信息
			SocketConnet.getInstance().communication(
					SendTask.CODE_PRE_SCAN_GET_BAGID_LIST);
			waitReply(SendTask.CODE_PRE_SCAN_GET_BAGID_LIST,
					MSG_GET_INSTORE_INFO_OVER);
			mDiaUtil.showProgressDialog();
		}
	}

	private void findView() {

		setContentView(R.layout.activity_pre_scan);

		findViewById(R.id.iv_pre_scan_redo).setOnClickListener(this);

		mVtitle = (ActivityHeadItemView) findViewById(R.id.v_title);
		mTvLock = (TextView) findViewById(R.id.tv_scanbunch_activity_lock);

		mTvLock.setVisibility(View.VISIBLE);
		mTvLock.setOnClickListener(this);
		// 隐藏功能， 切换工作模式
		mTvLock.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (!haveTaskRunning) {
					mDoorWorkMode = (mDoorWorkMode + 1) % 3;
					if (0 == mDoorWorkMode) {
						ToastUtil.showToastInCenter("已进行【锁】模式");
					} else if (1 == mDoorWorkMode) {
						ToastUtil.showToastInCenter("已进行【锁或袋】模式");
					} else {
						ToastUtil.showToastInCenter("已进行【锁和袋】模式");
						if (null == mScannedLableList) {
							mScannedLableList = new HashSet<String>();
							mScannedBagList = new HashSet<String>();
						}
					}
				}
				return true;
			}
		});
		if (TYPE_OP.IN_STORE_DOOR_PRE == mType) {
			// 预扫描 补扫
			mVtitle.setText(TEXT_TITLES[1]);
			mTvLock.setVisibility(View.GONE);// 预扫描 不显示[锁定批]按钮
		} else if (TYPE_OP.IN_STORE_DOOR == mType) {
			// 交接 补扫
			mVtitle.setText(TEXT_TITLES[2]);
		} else if (TYPE_OP.IN_STORE_HAND_LOT == mType) {
			// 手持批量 入库
			mVtitle.setText(TEXT_TITLES[0]);
		} else {
			// 手持批量 出库
			mVtitle.setText(TEXT_TITLES[3]);
		}

		mImgSwich = findViewById(R.id.iv_pre_scan_redo);
		if (isUHFMode) {
			mImgSwich.setBackgroundResource(R.drawable.mode_far_64);
		} else {
			mImgSwich.setBackgroundResource(R.drawable.mode_near_64);
		}

		// mPlanNum = getIntent().getIntExtra(KEY_PLAN_SCAN, -1);
		mTvPlanNum = (TextView) findViewById(R.id.tv_pre_scan_plan_num);
		mTvPlanNum.setText("" + mPlanNum);
		mTvPlanNum.setOnClickListener(this);

		mTvScannedNum = (TextView) findViewById(R.id.tv_pre_scan_scanned_num);
		mTvScannedNum.setText("" + mScannedNum);
		mTvScannedNum.setOnClickListener(this);

		mBtnOk = (Button) findViewById(R.id.btn_pre_scan_ok);
		mBtnOk.setOnClickListener(this);

		mProgreeBar = (MyProgressBar) findViewById(R.id.pb_pre_scan_scanned_progress);
		mProgreeBar.setMax(mPlanNum);
		mProgreeBar.setProgress(mScannedNum);
		mProgreeBar.setText("未开始入库");

		ViewUtil.requestFocus(mBtnOk);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
		// TODO
		switch (v.getId()) {
		case R.id.btn_pre_scan_ok:
			if (haveTaskRunning) {
				haveTaskRunning = false;
				mScanTask.stop();
				mBtnOk.setText(TEXT_START_SCAN);
				mProgreeBar.setText("入库已停止!");
			} else {
				if (haveDone) {
					onBackPressed();
					return;
				}
				haveTaskRunning = true;
				mProgreeBar.setText("请继续入库...");
				ThreadPoolFactory.getNormalPool().execute(mScanTask);
				mBtnOk.setText(TEXT_STOP_SCAN);
			}
			break;
		case R.id.tv_scanbunch_activity_lock:
			if (!haveTaskRunning) {
				buildLockPi();
			}
			break;
		case R.id.iv_pre_scan_redo:
			if (!haveTaskRunning) {
				swichScanModel();
			}
			break;
		case R.id.tv_pre_scan_plan_num:
			if (haveTaskRunning) {
				return;
			}
			if (TYPE_OP.OUT_STORE_HAND_LOT == mType) {
				showInputNum(TEXT_TITLE_SET_OUTSTORE_PLAN_NUM);
			} else {
				showInputNum(TEXT_TITLE_SET_SALVER);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		int resultCode = 0;
		if (mType == TYPE_OP.IN_STORE_DOOR_PRE) {// 回到 预扫描 遥控界面
			if (haveDone) {
				resultCode = RESULT_CODE_PRE_FINISH;
			} else {
				resultCode = RESULT_CODE_PRE_UNFINISH;
			}
		} else if (mType == TYPE_OP.IN_STORE_DOOR) {// 回到交接 遥控界面
			if (haveDone) {
				boolean isLastPallet = (null == mBagIdList ? false
						: 0 == mBagIdList.size());
				LogsUtil.d(TAG, "最后一托盘？ " + isLastPallet);
				if (isLastPallet) {
					resultCode = RESULT_CODE_MEND_FINISH_ALL;
				} else {
					resultCode = RESULT_CODE_MEND_FINISH_TP;
				}
			} else {
				resultCode = RESULT_CODE_MEND_UNFINISH;
			}
		}
		LogsUtil.d(TAG, "onBackPressed resultCode:" + resultCode);
		setResult(resultCode);
		SocketConnet.getInstance().setRecieveListener(null);
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		SocketConnet.getInstance().setRecieveListener(mRecieveListener);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (null != mBagIdList) {
			mBagIdList.clear();
			mBagIdList = null;
		}
		if (null != mUhfOp) {
			setLotScanPower(false);
			mUhfOp.close();
			mUhfOp = null;
		}
		if (null != mScanTask) {
			mScanTask.stop();
		}
		super.onDestroy();

	}

	/**
	 * 设置超高频功率
	 * 
	 * @param isLotScan
	 *            true:批量扫描的 功率; false:设置默认功率
	 */
	private void setLotScanPower(boolean isLotScan) {
		int rPower;
		int wPower;
		if (isLotScan) {
			// 设置为 批量扫描的 功率
			rPower = SPUtils.getInt(MyContexts.KEY_POWER_READ_INSTORE,
					SettingPowerActivity.POWER_READ_INSTORE);
			wPower = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_INSTORE,
					SettingPowerActivity.POWER_WRITE_INSTORE);
		} else {
			// 回复默认功率
			rPower = SPUtils.getInt(MyContexts.KEY_POWER_READ_COVER, 10);
			wPower = SPUtils.getInt(MyContexts.KEY_POWER_WRITE_COVER, 20);
		}
		mUhfOp.setPower(rPower, wPower, 1, 0, 0);
	}

	private void buildLockPi() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
				MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
		builder.setPositiveButton(MyContexts.TEXT_OK,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						SocketConnet.getInstance().communication(
								SendTask.CODE_LOCK_PI);// 锁定批
						waitReply(SendTask.CODE_LOCK_PI, MSG_SHOW_LOCK_RESULT);
					}
				});
		builder.setNegativeButton(MyContexts.TEXT_CANCEL, null);
		builder.show();
	}

	private void swichScanModel() {
		isUHFMode = !isUHFMode;
		SPUtils.putBoolean(MyContexts.KEY_SCAN_MODE, isUHFMode);
		String msg = null;
		if (isUHFMode) {
			msg = "已切换到远距离扫描";
			mImgSwich.setBackgroundResource(R.drawable.mode_far_64);
		} else {
			msg = "已切换到近距离扫描";
			mImgSwich.setBackgroundResource(R.drawable.mode_near_64);
		}
		mDiaUtil.showDialog(msg);
	}

	private void waitReply(final int code, final int msg) {
		setCommunicationCode(code);
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				LogsUtil.w(TAG, "waitReply: " + code);
				SystemClock.sleep(5000);
				LogsUtil.w(TAG, "waitReply end: " + commCode);
				if (code == commCode
						|| (SendTask.CODE_LOT_INSTORE_NUM == commCode || SendTask.CODE_PRE_SCAN_GET_BAGID_LIST == commCode)) {
					setCommunicationCode(-1);
					mHandler.sendEmptyMessage(msg);
				}
			}
		});
	}

	private void setCommunicationCode(int code) {
		synchronized (ACCESSIBILITY_SERVICE) {
			commCode = code;
		}
	}

	private boolean commCodeEquals(int code) {
		boolean reVal = false;
		synchronized (ACCESSIBILITY_SERVICE) {
			reVal = code == commCode;
		}
		LogsUtil.d(TAG, "real  Code:" + commCode);
		LogsUtil.d(TAG, "check Code:" + code);
		return reVal;
	}

	/**
	 * 修改数量 对话框
	 * 
	 * @param title
	 *            对话框标题内容
	 */
	private void showInputNum(String title) {
		AlertDialog.Builder builder = new Builder(this);
		// 设置 对话框 的样式
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_scan_bunch_plan_num, null);
		builder.setView(view);

		// 为 对话框 上的按钮设置监听器
		final NumberInputer vNumInputer = (NumberInputer) view
				.findViewById(R.id.v_item_nubmer_input);
		vNumInputer.setNumber(mSalverNum);
		OnClickListener myDialogListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_item_dialog_set_one:
					mSalverNum = 5;
					break;
				case R.id.btn_item_dialog_set_ten:
					mSalverNum = 15;
					break;
				case R.id.btn_item_dialog_set_twinty:
					mSalverNum = 28;
					break;
				}
				vNumInputer.setNumber(mSalverNum);
			}
		};
		view.findViewById(R.id.btn_item_dialog_set_one).setOnClickListener(
				myDialogListener);
		view.findViewById(R.id.btn_item_dialog_set_ten).setOnClickListener(
				myDialogListener);
		view.findViewById(R.id.btn_item_dialog_set_twinty).setOnClickListener(
				myDialogListener);
		builder.setTitle(title)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSalverNum = vNumInputer.getNumber();
						if (TYPE_OP.OUT_STORE_HAND_LOT == mType) {
							mPlanNum = mSalverNum;
							mProgreeBar.setMax(mPlanNum);
							mTvPlanNum.setText(String.valueOf(mPlanNum));
						} else {
							SocketConnet.getInstance().communication(
									SendTask.CODE_SET_SALVER_NUM,
									String.valueOf(mSalverNum));
							mDiaUtil.showProgressDialog();
						}
					}
				}).setNegativeButton("取消", null);
		builder.show();
	}

	private class MyHandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_INSTORE_INFO_OVER:
				mDiaUtil.dismissProgressDialog();
				// 未能获取 任务 信息
				if (null == mBagIdList) {
					ToastUtil.showToastInCenter("无法获取任务信息");
					onBackPressed();
					break;
				}
				mUhfOp = UHFOperation.getInstance();
				setLotScanPower(true);
				mScanTask = new ScanTask();
				findView();
				break;
			case MSG_SCAN_FINISH:
				LogsUtil.d(TAG, "MSG_SCAN_FINISH");
				mScanTask.stop();
				mProgreeBar.setText("入库完成");
				mBtnOk.setText(TEXT_QUIT);
				haveTaskRunning = false;
				haveDone = true;
			case MSG_ADD_ONE:
				LogsUtil.d(TAG, "MSG_ADD_ONE");

				SoundUtils.playNumber(mScannedNum);
				mTvScannedNum.setText(String.valueOf(mScannedNum));
				mProgreeBar.incrementProgressBy(1);
				if (haveDone) {
					if (TYPE_OP.IN_STORE_HAND_LOT == mType) {
//						mTvLock.performClick();
						mDiaUtil.showDialogFinishActivity("任务完成");
					} else {
						mDiaUtil.showDialogFinishActivity("扫描结束");
					}
				}
				break;
			case MSG_ADD_ONE_OUT:
				LogsUtil.d(TAG, "MSG_ADD_ONE_OUT");
				SoundUtils.playNumber(mScannedNum);
				mProgreeBar.incrementProgressBy(1);
				mTvScannedNum.setText(String.valueOf(mScannedNum));
				if (mPlanNum <= mScannedNum) {
					mBtnOk.setText(TEXT_QUIT);
					haveTaskRunning = false;
					haveDone = true;
					mTvLock.performClick();
				}
				break;
			case MSG_SHOW_LOCK_RESULT:// 锁定批
				final String info = ReplyParser.parseReply(msg.obj);

				if ("锁定批成功".equals(info)) {
					AlertDialog.Builder builder = new Builder(
							LotScanActivity.this);
					builder.setTitle(MyContexts.TEXT_DIALOG_TITLE);
					builder.setMessage(info);
					builder.setPositiveButton(MyContexts.TEXT_OK,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									LotScanActivity.this.finish();
								}
							});
					builder.show();
				} else {
					mDiaUtil.showDialog(info);
				}
				break;
			case MSG_LONG_TIME_NO_SCAN:
				LogsUtil.d(TAG, "ScanTask send : MSG_LONG_TIME_NO_SCAN");
				ToastUtil.showToastInCenter("长时间未扫描到基金袋");
				mBtnOk.setText(TEXT_START_SCAN);
				haveTaskRunning = false;
				break;
			case MSG_LONG_TIME_NO_REPLY:
				mDiaUtil.dismissProgressDialog();
				ToastUtil.showToastInCenter(getResources().getString(
						R.string.text_recieve_timeout));
				haveTaskRunning = false;
				if (null == mBtnOk) {
					finish();
					return true;
				}
				mBtnOk.setText(TEXT_START_SCAN);
				break;
			case MSG_SET_SALVER_NUM:
				mDiaUtil.dismissProgressDialog();
				mSalverNum = msg.arg1;
				if (0 < msg.arg1) {
					ToastUtil.showToastInCenter("设置托盘袋数为：" + msg.arg1);
				} else {
					SoundUtils.playFailedSound();
					mDiaUtil.showDialog("设置托盘袋数失败");
				}
				break;
			default:
				break;
			}

			return false;
		}
	}

	/**
	 * 4步: 1,扫描超高频; 2,判断袋id; 3,上发服务器; 4,处理回复
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScanTask implements Runnable {
		private final int COUNT_SCAN = 100;
		private final long WAIT_TIME = 5000;
		private boolean stopped;
		/**
		 * 记录 未扫到 超高频卡 的次数
		 */
		private int scanCount;

		public void stop() {
			stopped = true;
			scanCount = 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			LogsUtil.e(TAG, ScanTask.class.getSimpleName() + " run");
			stopped = false;
			scanCount = 0;
			StaticString.information = null;
			while (!stopped) {

				SystemClock.sleep(50);

				byte[] bagIdBuf = null;
				boolean readSuccess = false;
				if (isUHFMode) {
					readSuccess = mUhfOp.findOne();
					if (readSuccess) {
						bagIdBuf = mUhfOp.mEPC;
					}
				} else {
					bagIdBuf = new byte[12];
					readSuccess = NfcOperation.getInstance().readData(0x04,
							bagIdBuf.length, bagIdBuf, 0, null);
				}

				// 1, 未扫到epc
				if (!readSuccess) {
					scanCount++;
					if (COUNT_SCAN <= scanCount) {
						mHandler.sendEmptyMessage(MSG_LONG_TIME_NO_SCAN);
						break;
					} else {
						LogsUtil.d(TAG, "findOne() nothing " + scanCount);
						continue;
					}
				}

				String lableId = null;
				String bagId = ArrayUtils.bytes2HexString(bagIdBuf);
				LogsUtil.d("bagID=" + bagId);

				// 2, 判断 扫到的 袋id； if(null == mBagIdList) mean is out store
				if (TYPE_OP.OUT_STORE_HAND_LOT == mType) {
					// 出库， 只处理 05开头且 未出库 的袋id
					if (null == bagId || !bagId.startsWith("05")
							|| mBagIdList.contains(bagId)) {
						LogsUtil.d(TAG, bagId + " -> wrong bagID");
						continue;
					}
				} else {
					/**
					 * 入库： 锁模式： 只会执行 第2步； 锁或袋： 执行 第1、第2； 锁和袋： 执行 1、2、3
					 */
					// 1, 锁或袋、锁和袋 2种模式中，扫描到标牌，根据标牌得到袋id
					if (0 < mDoorWorkMode && bagId.startsWith("85")) {
						lableId = bagId;
						bagId = lableId.replaceFirst("8", "0");
					}

					// 2, 判断扫描到的东西是否在 任务列表中
					if (!mBagIdList.contains(bagId)) {
						LogsUtil.d(TAG, bagId + " -> no contain");
						continue;
					}

					// 3, 锁和袋模式还需 进一步判断
					if (2 == mDoorWorkMode) {

						if (null == lableId) {
							// 扫描到 袋id
							mScannedBagList.add(bagId);
							lableId = bagId.replaceFirst("0", "8");
						} else {
							// 扫描到 标牌id
							mScannedLableList.add(lableId);
						}

						if (!mScannedBagList.contains(bagId)
								|| !mScannedLableList.contains(lableId)) {
							// 只扫描到了 袋id 或 标牌id， 继续扫描
							continue;
						}
					}
				}

				StaticString.bagid = bagId;
				if (TYPE_OP.OUT_STORE_HAND_LOT == mType) {
					// 批量出库
					SocketConnet.getInstance().communication(
							SendTask.CODE_OUT_STORE_LOT_SCAN_UPLOAD_BAGID);
					setCommunicationCode(SendTask.CODE_OUT_STORE_LOT_SCAN_UPLOAD_BAGID);
				} else if (TYPE_OP.IN_STORE_DOOR_PRE == mType) {
					// 预扫描
					SocketConnet.getInstance().communication(
							SendTask.CODE_PRE_SCAN_UPLOAD_BAGID,
							new String[] { bagId });
					setCommunicationCode(SendTask.CODE_PRE_SCAN_UPLOAD_BAGID);
					// waitReply(SendTask.CODE_PRE_SCAN_UPLOAD_BAGID,
					// MSG_LONG_TIME_NO_REPLY);
				} else {
					// 批量入库
					SocketConnet.getInstance().communication(
							SendTask.CODE_IN_STORE_LOT_SCAN_UPLOAD_BAGID);
					setCommunicationCode(SendTask.CODE_IN_STORE_LOT_SCAN_UPLOAD_BAGID);
					// waitReply(SendTask.CODE_INSTORE_SCAN_UPLOAD_BAGID,
					// MSG_LONG_TIME_NO_REPLY);
				}

				long waitTime = 0;
				try {
					// 等待服务器回复
					LogsUtil.w(TAG, "ScanTask waitting");
					synchronized (mScanTask) {
						waitTime = System.currentTimeMillis();
						mScanTask.wait(WAIT_TIME);
						waitTime = System.currentTimeMillis() - waitTime;
					}
					LogsUtil.w(TAG, "ScanTask waited " + waitTime);
				} catch (InterruptedException e) {
				}
				if (WAIT_TIME <= waitTime) {
					setCommunicationCode(-1);
					mHandler.sendEmptyMessage(MSG_LONG_TIME_NO_REPLY);
					break;
				}
			}// end while
			stop(); // 线程终止前 恢复初值
			haveTaskRunning = false;
			LogsUtil.e(TAG, ScanTask.class.getSimpleName() + " end");
		} // end run

	}

	private class LotInRecieveListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			LogsUtil.w(TAG, "onRecieve " + recString);
			if (null == recString) {
				return;
			}

			String[] split = recString.split(" ");
			if (commCodeEquals(SendTask.CODE_LOT_INSTORE_NUM)
					&& "*37".equals(split[0])
					&& "01".equalsIgnoreCase(split[1])) {// 获取 批次 信息
				setCommunicationCode(-1);
				// 获取已扫数量
				mScannedNum = Integer.parseInt(split[3]);
				// 获取 总数
				mPlanNum = Integer.parseInt(split[2]);
				// 获取待扫描袋id 列表
				SocketConnet.getInstance().communication(
						SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST);
				setCommunicationCode(SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST);
				// if (TYPE_PRE_SCAN == mType) {
				// // 预扫描阶段 从服务器 获取 袋id 列表
				// SocketConnet.getInstance().communication(
				// SendTask.CODE_PRE_SCAN_GET_BAGID_LIST);
				// setCommunicationCode(SendTask.CODE_PRE_SCAN_GET_BAGID_LIST);
				// } else {
				// // 非预扫描阶段 从服务器 获取 袋id 列表
				// SocketConnet.getInstance().communication(
				// SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST);
				// setCommunicationCode(SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST);
				// }
			} else if (commCodeEquals(SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST)
					&& "*57".equals(split[0])) {// 交接扫描 待扫描 列表
				setCommunicationCode(-1);
				/*
				 * 交接阶段 获取 袋id列表 *57 12
				 * 055311010445C9321E448026,05531101043EC9321E44805D,#
				 */
				String[] bagIds = split[2].split(",");
				// mBagIdList = new ArrayList<String>();
				mBagIdList = new HashSet<String>();
				for (int i = 0; i < bagIds.length; i++) {
					mBagIdList.add(bagIds[i]);
				}
				LogsUtil.d(TAG, "mBagIdList.size(): " + mBagIdList.size());
				mHandler.sendEmptyMessage(MSG_GET_INSTORE_INFO_OVER);
			} else if (commCodeEquals(SendTask.CODE_PRE_SCAN_GET_BAGID_LIST)
					&& "*1000".equals(split[0]) && "57".equals(split[2])) {// 预扫描
																			// 待扫描
																			// 列表
				/*
				 * 预扫描阶段 获取 袋id列表 *1000 02 57 02 2 0 9 2
				 * 
				 * 055311010445C9321E448026,05531101043EC9321E44805D,#
				 */
				setCommunicationCode(-1);

				// 托盘 数量
				mPlanNum = Integer.parseInt(split[4]);
				// 托盘 已扫数量
				mScannedNum = Integer.parseInt(split[5]);
				int waitScan = Integer.parseInt(split[7]); // 未扫描的袋数量
				// isLastPallet = waitScan == (mPlanNum - mScannedNum);
				// LogsUtil.d(TAG, "最后一托盘？ " + isLastPallet);
				// 待扫列表
				String[] bagIds = split[8].split(",");
				mBagIdList = new HashSet<String>();
				for (int i = 0; i < bagIds.length; i++) {
					mBagIdList.add(bagIds[i]);
				}
				mHandler.sendEmptyMessage(MSG_GET_INSTORE_INFO_OVER);
			} else if (commCodeEquals(SendTask.CODE_PRE_SCAN_UPLOAD_BAGID)
					&& recString.startsWith("*1000 02 02 00")) {
				// 预扫描上传袋id
				setCommunicationCode(-1);
				mBagIdList.remove(StaticString.bagid);
				if ("03".equals(split[3]) || mBagIdList.size() <= 0) {
					// 完成任务
					mHandler.sendEmptyMessage(MSG_SCAN_FINISH);
				} else if ("00".equals(split[3])) {
					// 成功 上一个 袋id
					mHandler.sendEmptyMessage(MSG_ADD_ONE);
				} else {
					// 袋id已上传过
					// empty
				}
				notifyScanThread();
			} else if (commCodeEquals(SendTask.CODE_IN_STORE_LOT_SCAN_UPLOAD_BAGID)
					&& "*02".equals(split[0])) {
				// 交接上传袋id
				setCommunicationCode(-1);
				mBagIdList.remove(StaticString.bagid);
				boolean scanFinish = false;
				if (split[1].startsWith("08")) {
					// 成功 上一个 袋id
					mScannedNum++;
					if (mScannedNum < mPlanNum && 0 < mBagIdList.size()) {
						mHandler.sendEmptyMessage(MSG_ADD_ONE);
					} else {
						mHandler.sendEmptyMessage(MSG_SCAN_FINISH);
						scanFinish = true;
					}
				}
				if (!scanFinish) {
					notifyScanThread();
				}
			} else if (commCodeEquals(SendTask.CODE_OUT_STORE_LOT_SCAN_UPLOAD_BAGID)
					&& "*02".equals(split[0])) {
				// 出库 上传袋id
				setCommunicationCode(-1);
				mBagIdList.add(StaticString.bagid);// 袋id无论上传是否成功，加入排除列表
				if (split[1].startsWith("08")) {
					// 成功处理一个袋id
					mScannedNum++;
					mHandler.sendEmptyMessage(MSG_ADD_ONE_OUT);
				}
				if (mScannedNum < mPlanNum) {
					notifyScanThread();
				}
			} else if ("*1000".equals(split[0])
					&& ("12".equals(split[2]) || "13".equals(split[2]))) {// 进度同步
				boolean haveBagId = mBagIdList.remove(split[5]);
				if (haveBagId) {
					if ("03".equals(split[3]) || mBagIdList.size() <= 0) {
						mHandler.sendEmptyMessage(MSG_SCAN_FINISH);
					} else {
						mHandler.sendEmptyMessage(MSG_ADD_ONE);
					}
				}
			} else if (commCodeEquals(SendTask.CODE_LOCK_PI)
					&& "*27".equals(split[0])) {// 锁定批
				setCommunicationCode(-1);
				Message msg = Message.obtain();
				msg.what = MSG_SHOW_LOCK_RESULT;
				msg.obj = recString;
				mHandler.sendMessage(msg);
				// mHandler.sendEmptyMessage(MSG_SHOW_LOCK_RESULT);
			} else if (4 < split.length && recString.startsWith("*1000 02 05 ")) {
				Message msg = mHandler.obtainMessage();
				int salverNum = Integer.parseInt(split[3]);
				msg.what = MSG_SET_SALVER_NUM;
				msg.arg1 = salverNum;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_LONG_TIME_NO_REPLY);
		}

		/**
		 * 唤醒 扫描 线程
		 */
		private void notifyScanThread() {
			synchronized (mScanTask) {
				LogsUtil.w(TAG, "notify");
				mScanTask.notify();
			}
		}
	}

}
