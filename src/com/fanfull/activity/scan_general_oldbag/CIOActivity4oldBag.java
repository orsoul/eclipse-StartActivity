package com.fanfull.activity.scan_general_oldbag;

import java.util.Arrays;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.BarCodeOperation;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.op.RFIDOperation;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.ClickUtil;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;
import com.fanfull.view.CoverBagItemView;

/**
 * @Description: 封袋,入库,出库 三合一
 */

public class CIOActivity4oldBag extends BaseActivity {

	private final String TEXT_IN_STORE = "入 库 (老袋)";
	private final String TEXT_OUT_STORE = "出 库 (老袋)";
	/**
	 * 38
	 */
	private final int BARCODE_BUF_LEN = 38;
	/**
	 * 23
	 */
	private final int BARCODE_READ_LEN = 23;

	// TODO
	private final int MSG_INIT_BARCODE_FAILED = 1;
	private final int MSG_INIT_BARCODE_SUCCESS = 2;

	// read barcode
	private final int MSG_READ_BARCODE_START = 11;
	private final int MSG_READ_BARCODE_DATA_WRONG = 12;
	private final int MSG_READ_BARCODE_SUCCESS = 13;

	// read or Write M1
	private final int MSG_RW_LOCK_START = 21;
	private final int MSG_NO_M1 = 22;
	private final int MSG_BAG_NO_LOCK = 23;
	private final int MSG_BARCODE_CHECK_FAILED = 24;
	private final int MSG_RW_LOCK_FAILED = 25;
	private final int MSG_RW_LOCK_SUCCESS = 26;

	// net check
	private final int MSG_NET_CHECK_START = 31;
	private final int MSG_NET_CHECK_FAILED = 32;
	private final int MSG_NET_CHECK_SUCCESS = 33;
	private final int MSG_TIME_OUT = 34;

	// finish task
	private final int MSG_SHOW_LOCK_RESULT = 41;

	// 业务进度
	private final int SETP_READ_BARCODE = 1;
	private final int STEP_RW_LOCK = 2;
	private final int STEP_NET_CHECK = 3;
	private final int STEP_OVER = 4;
	private int mStep = SETP_READ_BARCODE;

	// Views
	private TextView mTvPlanAmount;
	private TextView mTvTotalAmount;
	private TextView mTvPersonAmount;

	private CoverBagItemView mVshow1;
	private CoverBagItemView mVshow2;
	private CoverBagItemView mVshow3;

	private Button mBtnConfirm;
	private Button mBtnCancel;
	private TextView mTvLock;

	private BarCodeOperation mBarcodeOP;
	private ReadWriteLockTask mReadWriteLockTask;
	private ReadBarcodeTask mReadBarcodeTask;

	private int mTotalAmount;// 总完成数量
	private int mPersonAmount;// 个人完成数量
	private int mPlanAmount;// 计划完成数量

	private int mTypeOperation;
	private byte[] mBarcodeBuf;
	private boolean haveTaskRunning;
	private RecieveListenerAbs mRecieveListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO

		mDiaUtil = new DialogUtil(this);
		mHandler = new Handler(new HandlerCallBack());
		mRecieveListener = new RecieveListener();
		SocketConnet.getInstance().setRecieveListener(mRecieveListener);

		mDiaUtil.showProgressDialog();

		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				while (false == BarCodeOperation.getInstance().connection()) {
					LogsUtil.d(TAG,
							getResources()
									.getString(R.string.connet_rfid_error));
					if (5 < ++count) {
						mHandler.sendEmptyMessage(MSG_INIT_BARCODE_FAILED);
						return;
					}
					SystemClock.sleep(200);
				}
				mHandler.sendEmptyMessage(MSG_INIT_BARCODE_SUCCESS);
			}
		});
	}

	private void findView() {
		setContentView(R.layout.activity_cover_bag_old_bag);

		// mTypeOperation =
		// getIntent().getIntExtra(MyContexts.KEY_OPERATION_TYPE,
		// GeneralActivity.OPERATION_LOCK_BAG);
		mTypeOperation = getIntent().getIntExtra(TYPE_OP.KEY_TYPE,
				TYPE_OP.COVER_BAG_OLD_BAG);

		ActivityHeadItemView title = (ActivityHeadItemView) findViewById(R.id.v_coverbagactivity_title);
		if (TYPE_OP.IN_STORE_OLD_BAG == mTypeOperation) {
			// 入库,改标题
			title.setText(TEXT_IN_STORE);
		} else if (TYPE_OP.OUT_STORE_OLD_BAG == mTypeOperation) {
			// 出库,改标题
			title.setText(TEXT_OUT_STORE);
		} else {
			// 封袋,默认标题
		}

		mVshow1 = (CoverBagItemView) findViewById(R.id.v_cover_oldbag_show1);
		mVshow2 = (CoverBagItemView) findViewById(R.id.v_cover_oldbag_show2);
		mVshow3 = (CoverBagItemView) findViewById(R.id.v_cover_oldbag_show3);
		// mVshow3.setVisibility(View.GONE);

		mTvPlanAmount = (TextView) findViewById(R.id.plan_amount);
		mTvTotalAmount = (TextView) findViewById(R.id.finish_amount);// 完成总数量
		mTvPersonAmount = (TextView) findViewById(R.id.person_scan_amount);// 个人扫描总数量

		mBtnConfirm = (Button) findViewById(R.id.btn_ok);// 确认按钮
		mBtnConfirm.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);// 取消按钮
		mBtnCancel.setOnClickListener(this);
		mTvLock = (TextView) findViewById(R.id.tv_check_cover_bag_ez_lock); // 结束批
		mTvLock.setOnClickListener(this);
		// mTvLock.setEnabled(false);
		ViewUtil.requestFocus(mBtnConfirm);
	}

	private void initShowView() {
		mVshow1.setChecked(false);
		mVshow2.setChecked(false);
		mVshow3.setChecked(false);
		mBtnConfirm.setText("扫描");
	}

	private void checkReply(String replyInfo) {
		Message msg = Message.obtain();
		if (TextUtils.isEmpty(replyInfo)) {
			msg.what = MSG_NET_CHECK_FAILED;
			msg.obj = ReplyParser.parseReply(replyInfo);
			mHandler.sendMessage(msg);
			return;
		}
		if (replyInfo.length() < 69) {
			msg.what = MSG_NET_CHECK_FAILED;
			msg.obj = ReplyParser.parseReply(replyInfo);
			mHandler.sendMessage(msg);
			return;
		}

		// 走到这里 认为 回复成功
		try {
			mPlanAmount = Integer.parseInt(replyInfo.substring(52, 56));// 计划成数量
			mTotalAmount = Integer.parseInt(replyInfo.substring(44, 48));// 总完成数量
			mPersonAmount = Integer.parseInt(replyInfo.substring(60, 64));// 个人完成数量
		} catch (Exception e) {
			msg.what = MSG_NET_CHECK_FAILED;
			msg.obj = "完成数量解析错误";
			mHandler.sendMessage(msg);
			return;
		}

		LogsUtil.d(TAG, "mPersonFinish: " + mPersonAmount);
		LogsUtil.d(TAG, "mTotalFinish: " + mTotalAmount);
		LogsUtil.d(TAG, "mPlanNumber: " + mPlanAmount);

		msg.what = MSG_NET_CHECK_SUCCESS;
		mHandler.sendMessage(msg);
	}

	private void buildLockPi() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
				MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				SocketConnet.getInstance().communication(SendTask.CODE_LOCK_PI);
				mDiaUtil.showProgressDialog();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// 重置back键功能
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_4:
			ViewUtil.requestFocus(mBtnConfirm);
			break;
		case KeyEvent.KEYCODE_6:
			ViewUtil.requestFocus(mBtnCancel);
			break;
		case KeyEvent.KEYCODE_HOME:
			onBackPressed();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!haveTaskRunning) {
			if (SETP_READ_BARCODE != mStep) {
				mStep = SETP_READ_BARCODE;
				initShowView();
				return;
			}
			if (ClickUtil.isFastDoubleClick(2500)) {
				finish();
			} else {
				ToastUtil.showToastInCenter("再次点击退出");
			}
		} else if (mStep == SETP_READ_BARCODE) {
			mReadBarcodeTask.stop();
			mVshow1.setDoing(false);
			mBtnConfirm.setClickable(true);
			haveTaskRunning = false;
		} else if (mStep == STEP_RW_LOCK) {
			mReadWriteLockTask.stop();
			mVshow2.setDoing(false);
			mBtnConfirm.setClickable(true);
			haveTaskRunning = false;
		} else if (mStep == STEP_NET_CHECK) {
			ToastUtil.showToastInCenter("正在进行网络通讯,请稍候");
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		BarCodeOperation.getInstance().close();
		mStep = STEP_OVER;
		if (SPUtils.getBoolean(MyContexts.KEY_SMALL_SRCEEN_ENABLE, false)) {
			OLEDOperation.getInstance().close();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_ok:// 扫描操作按键
			mBtnConfirm.setText("扫描");
			if (mStep == SETP_READ_BARCODE) {
				mHandler.sendEmptyMessage(MSG_READ_BARCODE_START);
			} else if (mStep == STEP_RW_LOCK) {
				mHandler.sendEmptyMessage(MSG_READ_BARCODE_START);
				// mHandler.sendEmptyMessage(MSG_RW_LOCK_START);
			} else if (mStep == STEP_NET_CHECK) {
				mHandler.sendEmptyMessage(MSG_NET_CHECK_START);
			} else if (mStep == STEP_OVER) {
				// initUi();
				// mVshow1.setDoing(true);
				// mBtnConfirm.setClickable(false);
				// ThreadPoolFactory.getNormalPool().execute(mReadLockTask);
				mHandler.sendEmptyMessage(MSG_READ_BARCODE_START);
			}
			break;
		case R.id.btn_cancel:// 取消按键
			onBackPressed();
			break;
		case R.id.tv_check_cover_bag_ez_lock:// 锁定批
			if (!haveTaskRunning) {
				buildLockPi();
			}
			break;
		default:
			break;
		}
	}

	class HandlerCallBack implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			String text = null;
			switch (msg.what) {
			case MSG_INIT_BARCODE_FAILED:
				SoundUtils.playFailedSound();
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showDialogFinishActivity(getResources().getString(
						R.string.text_init_barcode_failed));
				break;
			case MSG_INIT_BARCODE_SUCCESS:
				mDiaUtil.dismissProgressDialog();
				findView();
				mBarcodeOP = BarCodeOperation.getInstance();
				mReadWriteLockTask = new ReadWriteLockTask();
				mReadBarcodeTask = new ReadBarcodeTask();
				break;
			case MSG_READ_BARCODE_START:// 第一步读条码操作 1
				// mBarcodeOP.connection();// 初始化摄像头
				// mBarcodeOP.scan(); // 打开摄像头开关
				initShowView();
				mBtnConfirm.setClickable(false);
				mVshow1.setDoing(true);
				mStep = SETP_READ_BARCODE;
				ThreadPoolFactory.getNormalPool().execute(mReadBarcodeTask);
				break;
			case MSG_READ_BARCODE_DATA_WRONG:
				SoundUtils.playFailedSound();
				mDiaUtil.showDialog(MyContexts.DIALOG_MESSAGE_BARCODE_WRONG);
				mVshow1.setDoing(false);
				mBtnConfirm.setClickable(true);
				break;
			case MSG_READ_BARCODE_SUCCESS:
				LogsUtil.d(TAG, "MSG_READ_BARCODE_SUCCESS");
				mVshow1.setChecked(true);
				mHandler.sendEmptyMessageDelayed(MSG_RW_LOCK_START, 500);
				break;
			case MSG_RW_LOCK_START:
				mVshow2.setDoing(true);
				mBtnConfirm.setClickable(false);
				mStep = STEP_RW_LOCK;
				ThreadPoolFactory.getNormalPool().execute(mReadWriteLockTask);
				break;
			case MSG_RW_LOCK_SUCCESS:
				mVshow2.setChecked(true);
				mHandler.sendEmptyMessageDelayed(MSG_NET_CHECK_START, 500);
				break;
			case MSG_NO_M1:
				if (null == text) {
					text = "袋锁类型错误";
				}
			case MSG_BAG_NO_LOCK:
				if (null == text) {
					text = "未插入锁片或锁片未插好";
				}
			case MSG_BARCODE_CHECK_FAILED:
				if (null == text) {
					text = "标签校验失败";
				}
			case MSG_RW_LOCK_FAILED:
				if (null != text) {
					mDiaUtil.showDialog(text);
					mBtnConfirm.setText("重新扫描");
					mStep = STEP_OVER;
				}
				SoundUtils.playFailedSound();
				mVshow2.setDoing(false);
				mBtnConfirm.setClickable(true);
				break;
			case MSG_NET_CHECK_START:
				mVshow3.setDoing(true);
				mBtnConfirm.setClickable(false);
				mStep = STEP_NET_CHECK;
				// ThreadPoolFactory.getNormalPool().execute(mNetCheckTask);
				if (TYPE_OP.COVER_BAG_OLD_BAG == mTypeOperation) {
					// 2301:老系统封袋；23：新系统封袋
					SocketConnet.getInstance().communication(2301);
				} else if (TYPE_OP.IN_STORE_OLD_BAG == mTypeOperation) {
					// 601:老系统入库；6：新系统入库
					SocketConnet.getInstance().communication(601);
				} else {
					// 602:老系统出库；
					SocketConnet.getInstance().communication(602);
				}
				break;
			// case EPC_NO_MATCH:
			// showDialog(DIALOG_EPC_NO_MATCH);
			case MSG_NET_CHECK_FAILED:
				mDiaUtil.dismissProgressDialog();
				SoundUtils.playFailedSound();
				mDiaUtil.showDialog(msg.obj);
				mVshow3.setDoing(false);
				mBtnConfirm.setClickable(true);
				mBtnConfirm.setText("重新扫描");
				mStep = STEP_OVER;
				break;
			case MSG_NET_CHECK_SUCCESS:
				SoundUtils.playNumber(mPersonAmount);
				mVshow3.setChecked(true);
				mBtnConfirm.setClickable(true);
				mStep = STEP_OVER;

				if (9999 == mPlanAmount) {
					mTvPlanAmount.setText("-");
				} else {
					mTvPlanAmount.setText(String.valueOf(mPlanAmount));
				}
				mTvTotalAmount.setText(String.valueOf(mTotalAmount));
				mTvPersonAmount.setText(String.valueOf(mPersonAmount));

				if (mPlanAmount != mTotalAmount) {
					mBtnConfirm.setText("继续扫描");
				} else {
					if (TYPE_OP.IN_STORE_OLD_BAG == mTypeOperation) {
						mBtnConfirm.setText("入库完成");
					} else {
						mBtnConfirm.setText("出库完成");
					}
					mTvLock.performClick();
				}

				break;
			case MSG_TIME_OUT:
				SoundUtils.playFailedSound();
				mDiaUtil.showReplyDialog();
				break;
			case MSG_SHOW_LOCK_RESULT:
				mDiaUtil.dismissProgressDialog();
				final String info = ReplyParser.parseReply(msg.obj);
				// LogsUtil.d(TAG, "parseReply：" + info);
				if ("锁定批成功".equals(info)) {
					Drawable nav_up = getResources().getDrawable(
							R.drawable.lock);
					nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
							nav_up.getMinimumHeight());
					mTvLock.setCompoundDrawables(null, nav_up, null, null);
					mTvLock.setText("已锁定");
					mTvLock.setEnabled(false);
					mDiaUtil.showDialogFinishActivity(info);
				} else {
					SoundUtils.playFailedSound();
					mDiaUtil.showDialog(info);
				}
				break;
			}// end swich
			return false;
		}

	}

	/**
	 * 开启线程监听串口 取出条码读头数据
	 */
	class ReadBarcodeTask implements Runnable {
		private boolean stoped;

		public void stop() {
			stoped = true;
		}

		public void run() {
			LogsUtil.w(TAG, ReadBarcodeTask.class.getSimpleName() + " run()");
			mBarcodeOP.connection();// 重新打开串口，GPIO上电
			stoped = false;
			haveTaskRunning = true;
			byte buf[] = new byte[32];

			// SystemClock.sleep(200);
			while (mBarcodeOP.hardware.read(mBarcodeOP.fd, buf, buf.length) > 0)
				;// clear buf
			mBarcodeOP.scan();
			int msg = -1;
			while (!stoped) {
				msg = -1;
				if (mBarcodeOP.hardware.select(mBarcodeOP.fd, 1, 0 * 1000) == 1) {// 当串口存在数据，异步读取数据，修改UI
					mBarcodeOP.stopScan();
					SystemClock.sleep(100);
					mBarcodeBuf = new byte[BARCODE_BUF_LEN];
					int len = mBarcodeOP.hardware.read(mBarcodeOP.fd,
							mBarcodeBuf, mBarcodeBuf.length);

					if (BARCODE_READ_LEN == len) {// 读到正确的数据
						LogsUtil.s("barcode解码前hex:"
								+ ArrayUtils.bytes2HexString(mBarcodeBuf, 0,
										BARCODE_READ_LEN));
						mBarcodeOP.hardware.DecodeBarcode(mBarcodeBuf);// 解码
						LogsUtil.s("barcode解码后hex:"
								+ ArrayUtils.bytes2HexString(mBarcodeBuf));

						StaticString.barcode = new String(mBarcodeBuf).trim();
						LogsUtil.s("StaticString.barcode:"
								+ StaticString.barcode);
						msg = MSG_READ_BARCODE_SUCCESS;
					} else if (38 == len) {// 读到未加密的数据
						StaticString.barcode = new String(mBarcodeBuf).trim();
						LogsUtil.d(TAG, "未加密的条码:" + StaticString.barcode);
						msg = MSG_READ_BARCODE_SUCCESS;
					} else {// 读到异常的数据
						LogsUtil.d(
								TAG,
								"读到异常的数据 len = "
										+ len
										+ " data: "
										+ ArrayUtils.bytes2HexString(
												mBarcodeBuf, 0, len));
						msg = MSG_READ_BARCODE_DATA_WRONG;
					}
					mHandler.sendEmptyMessage(msg);
					break;// 当有数据时，跳出循环
				} else {
					SystemClock.sleep(50);
					Log.v(TAG, "---select() failed---");
				}
				SystemClock.sleep(50);
			}// end while()
			mBarcodeOP.stopScan();
			haveTaskRunning = false;
			LogsUtil.w(TAG, ReadBarcodeTask.class.getSimpleName()
					+ " run() end");
		}
	}

	class ReadWriteLockTask implements Runnable {
		private final long RUNTIME = 8000;
		private final int WRITE_TIMES = 3;
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			LogsUtil.w(TAG, ReadWriteLockTask.class.getSimpleName() + " run");
			haveTaskRunning = true;
			stoped = false;

			long startTime = System.currentTimeMillis();
			while (!stoped) {
				// 5秒内 读M1卡失败
				if (RUNTIME < (System.currentTimeMillis() - startTime)) {
					mHandler.sendEmptyMessage(MSG_RW_LOCK_FAILED);
					break;
				}
				/* 寻卡，并判断是否 为 M1卡 */
				byte[] uid = RFIDOperation.getInstance().findCard();
				if (null == uid) {
					SystemClock.sleep(50);
					continue;
				}
				if (4 != uid.length) { // m1卡 uid长度为 4个byte
					mHandler.sendEmptyMessage(MSG_NO_M1);
					break;
				}

				/* 读M1卡 第9块区 判断是否上锁 */
				byte[] block9 = new byte[16];
				if (!RFIDOperation.getInstance().readM1(9, block9, 0, uid)) {
					SystemClock.sleep(50);
					continue;
				}
				// 块区9 的16字节 全部 为 39 说明已插入锁片
				LogsUtil.d(TAG, "block9:" + ArrayUtils.bytes2HexString(block9));
				LogsUtil.d(TAG, "block9[0] = " + block9[0]);
				if (0x39 != block9[0]) {
					mHandler.sendEmptyMessage(MSG_BAG_NO_LOCK);
					break;
				}

				/* 读M1卡 第1块区 获取袋id 并 判断 袋版本 */
				// byte[] block1 =
				// RFIDOperation.getInstance().readBlockToByte(1);
				// if (null == block1) {
				// SystemClock.sleep(50);
				// continue;
				// }
				// LogsUtil.d(TAG, "block1:" +
				// ArrayUtils.bytesToHexString(block1));
				// LogsUtil.d(TAG, "block1[0] = " + block1[0]);
				// // if (MyContexts.KEY_OLDBAGVERSON != block1[0]) {
				// // mHandler.sendEmptyMessage(MSG_BAG_VERSON_WRONG);
				// // break;
				// // }
				// mBagIdBuf = Arrays.copyOf(block1, 12);
				// StaticString.bagid = ArrayUtils.bytesToHexString(mBagIdBuf);
				// LogsUtil.d(TAG, "StaticString.bagid:" + StaticString.bagid);

				int res = 0;
				if (TYPE_OP.COVER_BAG_OLD_BAG == mTypeOperation) {
					// 封袋, 将barcodebuf写入4,5,6块区
					res = getWriteResult(mBarcodeBuf);
				} else if (TYPE_OP.IN_STORE_OLD_BAG == mTypeOperation) {
					// 入库, 从4,5,6块区读出 barcodeBuf,并进行检验
					res = readDatasFrom456BlockAndCheck(mBarcodeBuf);
				} else if (TYPE_OP.OUT_STORE_OLD_BAG == mTypeOperation) {
					// 出库, 从4,5,6块区读出 barcodeBuf,并进行检验
					res = readDatasFrom456BlockAndCheck(mBarcodeBuf);
				}

				if (1 == res) {
					// 执行成功, 退出while()
					mHandler.sendEmptyMessage(MSG_RW_LOCK_SUCCESS);
					break;
				} else if (-1 == res) {
					// 入库检验 失败, 退出while()
					mHandler.sendEmptyMessage(MSG_BARCODE_CHECK_FAILED);
					break;
				} else {
					// 读写失败,继续尝试读写
				}

			} // end while()

			LogsUtil.w(TAG, ReadWriteLockTask.class.getSimpleName() + " end");
			haveTaskRunning = false;
			stoped = true;
		}// end run()

		/**
		 * 将barcode写入 4,5,6三个区块
		 * 
		 * @param barcodeBuf
		 *            38byte的 barcode
		 * @return 1:写入成功; 0:写入失败;
		 */
		private int getWriteResult(byte[] barcodeBuf) {
			/*  */
			byte[][] datas = ArrayUtils.get3Data(barcodeBuf);
			if (null == datas || 3 != datas.length) {
				return 0;
			}
			boolean[] wasWrite = new boolean[3];
			boolean allWrite = false;
			int count = 0;
			while (!allWrite) {
				if (WRITE_TIMES < ++count) {
					break;
				}
				if (!wasWrite[0]) {
					wasWrite[0] = RFIDOperation.getInstance().writeM1(4,
							datas[0], 500, null);
				}
				if (!wasWrite[1]) {
					wasWrite[1] = RFIDOperation.getInstance().writeM1(5,
							datas[0], 500, RFIDOperation.sLastUid);
				}
				if (!wasWrite[2]) {
					wasWrite[2] = RFIDOperation.getInstance().writeM1(6,
							datas[0], 500, RFIDOperation.sLastUid);
				}
				allWrite = wasWrite[0] && wasWrite[1] && wasWrite[2];
			}

			if (allWrite) {
				// 成功写入数据
				return 1;
			}
			return 0;// 写入数据失败
		}

		/**
		 * 读取4,5,6块区数据,并与barcodeBuf比较
		 * 
		 * @param barcodeBuf
		 *            38byte的 barcode
		 * @return 1:检验通过; 0:读块区失败; -1:检验失败
		 */
		private int readDatasFrom456BlockAndCheck(byte[] barcodeBuf) {

			byte[][] datas = new byte[3][16];
			boolean allRead = false;
			allRead = RFIDOperation.getInstance()
					.readM1(4, datas[0], 500, null)
					&& RFIDOperation.getInstance().readM1(5, datas[1], 500,
							RFIDOperation.sLastUid)
					&& RFIDOperation.getInstance().readM1(6, datas[2], 500,
							RFIDOperation.sLastUid);
//			int count = 0;
//			while (!allRead) {
//				if (WRITE_TIMES < ++count) {
//					break;
//				}
//				if (null == datas[0]) {
//					datas[0] = RFIDOperation.getInstance().readBlockToByte(4);
//				}
//				if (null == datas[1]) {
//					datas[1] = RFIDOperation.getInstance().readBlockToByte(5);
//				}
//				if (null == datas[2]) {
//					datas[2] = RFIDOperation.getInstance().readBlockToByte(6);
//				}
//				allRead = (null != datas[0]) && (null != datas[1])
//						&& (null != datas[2]);
//			}
			if (!allRead) {
				// 未能完全读取数据
				return 0;
			}
			// 合并3个数组
			byte[] threeInOne = new byte[barcodeBuf.length];
			System.arraycopy(datas[0], 0, threeInOne, 0, datas[0].length);
			System.arraycopy(datas[1], 0, threeInOne, 16, datas[1].length);
			System.arraycopy(datas[2], 0, threeInOne, 32,
					threeInOne.length - 32);

			if (Arrays.equals(threeInOne, barcodeBuf)) {
				// 检验通过
				return 1;
			}

			return -1;// 检验失败
		}
	}

	private class RecieveListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			if (null == recString) {
				return;
			} else if (recString.startsWith("*27 ")) {
				// 锁定批
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_SHOW_LOCK_RESULT;
				msg.obj = recString;
				mHandler.sendMessage(msg);
			} else {
				checkReply(recString);
			}

		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_TIME_OUT);
		}
	}
}
