package com.fanfull.activity.scan_lot;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.activity.scan_general.CoverNfcNewBagActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.operation.BagOperation;
import com.fanfull.socket.RecieveListener;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.ClickUtil;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;

/**
 * @author Administrator
 * @description 手持遥控入库,控制青岛人行库房 3道门式天线 入库
 */
public class YkInstoreActivity extends BaseActivity {
	private static final int DIALOG_SET_YK_NUM = 1;
	private static final int DIALOG_SET_PART_NUM = 2;

	private static final int DIALOG_OVER_LOCK = 3;
	private static final int DIALOG_PALLET_NUM = 4;

	public static final int MSG_DOOR_READY = 4;
	public static final int MSG_DOOR_READY_AUTO = 5;
	public static final int MSG_PRE_REPLY = 6;
	public static final int MSG_YK_REPLY = 7;

	public static final int MSG_REPLY_TIMEOUT = 8;
	public static final int MSG_PRE_START_NOT_READY = 9;
	/**
	 * 完成一托盘 回复处理
	 */
	public static final int MSG_FINISH_TP = 10;
	private static final int MSG_SHOW_LOCK_RESULT_DIALOG = 11;
	/**
	 * 查询 托盘 信息
	 */
	private static final int MSG_QUERY_SALVER_REPLY = 12;
	/**
	 * 混托盘, 获取袋id 成功
	 */
	private static final int MSG_SCAN_BAGID_SUCCESS = 13;
	/**
	 * 混托盘, 获取袋id 失败
	 */
	private static final int MSG_SCAN_BAGID_FAILED = 14;
	/**
	 * 确定进行混托盘
	 */
	private static final int MSG_MINGLE_SALVER_REPLY = 15;

	/**
	 * 预扫描
	 */
	public static final int STEP_IN_START_0 = 0;
	/**
	 * 交接 扫描
	 */
	public static final int STEP_IN_START_1 = 1;
	/**
	 * 完整券入库 一楼
	 */
	public static final int STEP_IN_START_2 = 2;
	/**
	 * 残损券入库 二楼
	 */
	public static final int STEP_IN_START_3 = 3;

	private int mStepPointer;

	private ActivityHeadItemView mVTitle;
	/**
	 * 混托盘
	 */
	private View mVmingle;
	private TextView mTvLock;
	private TextView mTvFinishTp;
	// private CheckBox mCp;

	public Button mBtnStart;
	public Button mBtnStop;
	public Button mBtnHandScan;

	private boolean haveTaskRunning;

	public static final String TEXT_IN_STORE_0 = "预 扫 描";
	public static final String TEXT_IN_STORE_1 = "交 接";
	public static final String TEXT_IN_STORE_2 = "完整券入库";
	public static final String TEXT_IN_STORE_3 = "残损券入库";
	private static final String TEXT_OTHER = "未知操作";

	private String[] text_title = new String[] { "预 扫 描", "交 接" };
	private String[] text_start = new String[] { "开始", "开始" };
	private String[] text_swing = new String[] { "停止", "停止" };
	private String[] text_reset = new String[] { "补扫", "补扫" };

	private int mOptype;
	private int scanModle;

	boolean sacnningPallet;
	private RecieveListener mRecvLis;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		mDiaUtil = new DialogUtil(this);
		mHandler = new Handler(new MyHandlerCallback());

		mRecvLis = new JjRecvListener();
		SocketConnet.getInstance().setRecieveListener(mRecvLis);

		mOptype = getIntent().getIntExtra(TYPE_OP.KEY_TYPE,
				TYPE_OP.IN_STORE_DOOR);

		/* 入库 */
		if (0 == SocketConnet.getInstance().getConnectedDoorNum()) {
			// if (TYPE_OP.IN_STORE_DOOR_PRE == mOptype) {
			// switchViewName(0);
			// } else {
			// switchViewName(1);
			// }
			// ViewUtil.clearFocus(mBtnStart);
			// mBtnStart.setEnabled(false);
			// 等待天线柜和前置准备完毕，才启动预扫描
			// SocketConnet.getInstance().communication(
			// SendTask.CODE_PRE_OPEN_IN_IS_READY);
			SocketConnet.getInstance().communication(SendTask.CODE_DOOR_READY);
			mDiaUtil.showProgressDialog();

			// 等待线程, 超时未得到回复 退出
//			ThreadPoolFactory.getNormalPool().execute(new Runnable() {
//				@Override
//				public void run() {
//					SystemClock.sleep(5000);
//					if (mDiaUtil.progressDialogIsShowing()) {
//						YkInstoreActivity.this.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								mDiaUtil.dismissProgressDialog();
//								ToastUtil.showToastInCenter("天线柜未准备好");
//								finish();
//							}
//						});
//					}// end if
//				}
//			});
		} else if (1 == SocketConnet.getInstance().getConnectedDoorNum()) {
			findView();
			mVTitle.setText(TEXT_IN_STORE_2);
			mStepPointer = STEP_IN_START_2;
			OLEDOperation.getInstance().showTextOnOled("按确认键开始", "完整券入库");
		} else if (2 == SocketConnet.getInstance().getConnectedDoorNum()) {
			findView();
			mVTitle.setText(TEXT_IN_STORE_3);
			mStepPointer = STEP_IN_START_3;
			OLEDOperation.getInstance().showTextOnOled("按确认键开始", "残损券入库");
		} else {
			findView();
			mVTitle.setText("连接中断");
		}

	}

	private void findView() {
		setContentView(R.layout.activity_yk_instore);

		mVTitle = (ActivityHeadItemView) findViewById(R.id.v_title);

		mVmingle = findViewById(R.id.iv_yk_instore_mingle);
		mVmingle.setOnClickListener(this);

		mBtnStart = ((Button) findViewById(R.id.btn_yk_go));
		mBtnStart.setOnClickListener(this);

		mBtnStop = ((Button) findViewById(R.id.btn_yk_stop));
		mBtnStop.setOnClickListener(this);

		mBtnHandScan = ((Button) findViewById(R.id.btn_yk_hand_scan));
		mBtnHandScan.setOnClickListener(this);

		mTvLock = ((TextView) findViewById(R.id.tv_yk_lock));
		mTvLock.setOnClickListener(this);

		mTvFinishTp = ((TextView) findViewById(R.id.tv_yk_finish_tp));
		mTvFinishTp.setOnClickListener(this);

		// mCp = (CheckBox) findViewById(R.id.cb_yk_dcrk);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		switch (v.getId()) {
		case R.id.btn_yk_go:// 开启
			if (haveTaskRunning) {
				return;
			}
			if (mStepPointer == STEP_IN_START_0) {
				SocketConnet.getInstance().communication(
						SendTask.CODE_PRE_START_IN);
				waitReply(v.getId(), MSG_PRE_REPLY);

			} else if (mStepPointer == STEP_IN_START_1) {

				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_START_IN);
				waitReply(v.getId(), MSG_YK_REPLY);

			} else if (STEP_IN_START_2 == mStepPointer
					|| STEP_IN_START_3 == mStepPointer) {
				// mDiaUtil.showProgressDialog();
				// scanSalver();
				showDialog(DIALOG_PALLET_NUM);
			}
			break;
		case R.id.btn_yk_stop:// 停止
			if (haveTaskRunning) {
				return;
			}
			if (STEP_IN_START_0 == mStepPointer) {
				/* 预扫描 */
				if (true) {
					// 第2次 按 结束 按钮
					SocketConnet.getInstance().communication(
							SendTask.CODE_PRE_CLOSE_IN);
					waitReply(v.getId(), MSG_PRE_REPLY);
				} else {
					// 第1次 按 停止 按钮
					SocketConnet.getInstance().communication(
							SendTask.CODE_PRE_PAUSE_IN);
					waitReply(v.getId(), MSG_PRE_REPLY);
				}
			} else if (STEP_IN_START_1 == mStepPointer) {
				/* 交接扫描 */
				if (true) {
					// 第1次 按 停止 按钮
					SocketConnet.getInstance().communication(
							SendTask.CODE_YK_RESET_IN);
					waitReply(v.getId(), MSG_YK_REPLY);
				} else {
					// 第2次 按 结束 按钮
					finish();
				}

			} else {
				// 完整券 或 残损券 入库
				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_RESET_IN);
				waitReply(v.getId(), MSG_YK_REPLY);
			}

			break;
		case R.id.btn_yk_hand_scan:// 手持补扫
			if (haveTaskRunning) {
				return;
			}
			if (STEP_IN_START_0 == mStepPointer) {
				// 预扫描
				Intent intent = new Intent(YkInstoreActivity.this,
						LotScanActivity.class);
				intent.putExtra(MyContexts.KEY_OPERATION_TYPE,
						LotScanActivity.TYPE_PRE_SCAN);
				// startActivity(intent);
				startActivityForResult(intent, LotScanActivity.TYPE_PRE_SCAN);
			} else if (STEP_IN_START_1 == mStepPointer) {
				Intent intent = new Intent(YkInstoreActivity.this,
						LotScanActivity.class);
				// intent.putExtra(MyContexts.KEY_OPERATION_TYPE,
				// LotInBankActivity.TYPE_MEND_SCAN);
				// startActivity(intent);
				intent.putExtra(TYPE_OP.KEY_TYPE, mOptype);
				intent.putExtra(MyContexts.KEY_DOOR_MODLE, scanModle);
				startActivityForResult(intent, LotScanActivity.TYPE_MEND_SCAN);
			} else {
				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_RESET_IN);
				waitReply(v.getId(), MSG_YK_REPLY);
			}

			// if (STEP_IN_START_0 == mStepPointer) {
			// // 预扫描 复位
			// SocketConnet.getInstance().communication(
			// SendTask.CODE_PRE_CLOSE_IN);
			// waitReply(v.getId(), MSG_PRE_REPLY);
			// } else {
			// // 交接扫描 复位
			// SocketConnet.getInstance().communication(
			// SendTask.CODE_YK_RESET_IN);
			// waitReply(v.getId(), MSG_YK_REPLY);
			// }
			break;
		case R.id.tv_yk_lock:// 锁定批
			if (haveTaskRunning || STEP_IN_START_1 != mStepPointer) {
				LogsUtil.d(TAG, "锁定批未执行 haveTaskRunning:" + haveTaskRunning
						+ " || STEP_IN_START: " + mStepPointer);
				return;
			}
			showDialog(DIALOG_OVER_LOCK);
			break;
		case R.id.tv_yk_finish_tp:// 完成一托盘
			if (haveTaskRunning || STEP_IN_START_1 != mStepPointer) {
				return;
			}

			SocketConnet.getInstance()
					.communication(SendTask.CODE_FINISH_TP_IN);
			waitReply(R.id.tv_yk_finish_tp, MSG_FINISH_TP);

			break;
		case R.id.iv_yk_instore_mingle: // 混托盘
			querySalver();
			break;
		default:
			break;
		}// TODO
	}

	@Override
	public void onBackPressed() {
		if (haveTaskRunning) {
			// do nothing
		} else if (ClickUtil.isFastDoubleClick(2500)) {
			finish();
		} else {
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_click_again_leave));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getRepeatCount() != 0) {
			return true;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_2:
			break;
		case KeyEvent.KEYCODE_8:
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		LogsUtil.d(TAG, "resultCode: " + resultCode);
		// 从 补扫界面 回来 继续 监听
		SocketConnet.getInstance().setRecieveListener(mRecvLis);

		switch (resultCode) {
		case LotScanActivity.RESULT_CODE_PRE_FINISH:// 预扫描 完成
			switchViewName(1);
			ViewUtil.requestFocus(mBtnStart);
			OLEDOperation.getInstance().showTextOnOled("按确认键开", "始交接扫描");
			break;
		case LotScanActivity.RESULT_CODE_PRE_UNFINISH:// 预扫描 未完成

			break;
		case LotScanActivity.RESULT_CODE_MEND_FINISH_TP:// 交接完成一托盘
			sendPreOpen();
			break;
		case LotScanActivity.RESULT_CODE_MEND_FINISH_ALL:// 交接完成全部
			mTvLock.performClick();
			break;
		case LotScanActivity.RESULT_CODE_MEND_UNFINISH:// 交接扫描 未完成

			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		OLEDOperation.getInstance().close();
		super.onDestroy();
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				YkInstoreActivity.this);
		switch (id) {
		case DIALOG_OVER_LOCK:
			builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
					MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mDiaUtil.showProgressDialog();
							SocketConnet.getInstance().communication(
									SendTask.CODE_LOCK_PI);// 锁定批
							ThreadPoolFactory.getNormalPool().execute(
									new Runnable() {
										@Override
										public void run() {
											// 在子线程中 检查 StaticString.information
											// 是否为空
											if (ReplyParser.waitReply()) {
												mHandler.sendEmptyMessage(MSG_SHOW_LOCK_RESULT_DIALOG);
											} else {
												mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
											}
										}
									});
						}
					});
			builder.setNegativeButton("取消", null);
			return builder.create();
		case DIALOG_PALLET_NUM:
			builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage("选择入库模式");
			builder.setPositiveButton("手持确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mDiaUtil.showProgressDialog();
							mDiaUtil.setProgressDialogCancelListener(new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									sacnningPallet = false;
								}
							});
							scanSalver();
						}
					});
			builder.setNegativeButton("智能识别",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDiaUtil.showProgressDialog();
							StaticString.YK_BAGID = "0";
							SocketConnet.getInstance().communication(
									SendTask.CODE_YK_START_IN);
							waitReply(R.id.btn_yk_go, MSG_YK_REPLY);
						}
					});
			return builder.create();
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		default:
			break;
		}
	}

	/**
	 * @param viewId
	 *            所点击的 View
	 * @param msgWhat
	 *            回复成功后 要发送的 msg
	 */
	private void waitReply(final int viewId, final int msgWhat) {
		mDiaUtil.showProgressDialog();
		haveTaskRunning = true;
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				haveTaskRunning = true;
				if (ReplyParser.waitReply()) {
					Message msg = Message.obtain();
					msg.what = msgWhat;
					msg.obj = viewId;
					mHandler.sendMessage(msg);
				} else {
					mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
				}
				haveTaskRunning = false;
			}
		});
	}

	private void preCmdSessucc(int viewId) {
		switch (viewId) {
		case 0: // 打开预扫描
			OLEDOperation.getInstance().showTextOnOled("预扫描就绪", "请按确认键",
					"开始预扫描");
			ViewUtil.requestFocus(mBtnStart);
			if (TYPE_OP.IN_STORE_DOOR_PRE == mOptype) {
				switchViewName(0);
			} else {
				switchViewName(1);
			}
			// mBtnNext.performClick();
			// 监听服务器回复
			break;
		case R.id.btn_yk_go:
			// mBtnStop.setText("停止"); // 没有 暂停 功能
			// isFinish = false;
			mBtnStop.setText("结束");
			ViewUtil.requestFocus(mBtnStop);
			OLEDOperation.getInstance().showTextOnOled("预扫描中");
			break;
		case R.id.btn_yk_stop:
			// ViewUtil.requestFocus(mBtnReset);
			if (true) {
				switchViewName(1);
				ViewUtil.requestFocus(mBtnStart);
				OLEDOperation.getInstance().showTextOnOled("按确认键开", "始交接扫描");
			} else {
				mBtnStop.setText("结束");
				ViewUtil.requestFocus(mBtnStart);
				OLEDOperation.getInstance().showTextOnOled("按确认键", "结束预扫描",
						"进入交接扫描");
			}
			break;
		case R.id.btn_yk_hand_scan:
			// switchViewName(1);
			// ViewUtil.requestFocus(mBtnStart);
			// OLEDOperation.getInstance().showTextOnOled("按确认键开", "始交接扫描");
			break;
		}
	}

	/**
	 * @param viewId
	 *            所点击的 viewId
	 * @des 交接扫描 后台回复成功
	 */
	private void jjCmdSessucc(int viewId) {
		switch (viewId) {
		case R.id.btn_yk_go:
			// mBtnStop.setText("停止"); // 没有 暂停 功能
			// isFinish = false;
			mBtnStop.setText("结束");
			// isFinish = true;
			ViewUtil.requestFocus(mBtnStop);
			if (STEP_IN_START_1 == mStepPointer) {
				OLEDOperation.getInstance().showTextOnOled("交接扫描中", "按确认键进行",
						"停止扫描");
			} else {
				OLEDOperation.getInstance().showTextOnOled("入库扫描中", "按确认键进行",
						"停止扫描");
			}
			break;
		case R.id.btn_yk_stop:
			if (true) {
				// mBtnStop.setText("结束");
				ViewUtil.requestFocus(mBtnStart);
				OLEDOperation.getInstance().showTextOnOled("交接扫描停止");
			} else {
				finish();
			}

			// ViewUtil.requestFocus(mBtnReset);
			// OLEDOperation.getInstance().showTextOnOled("扫描中", "按确认键",
			// "停止扫描");
			break;
		case R.id.btn_yk_hand_scan:
			// ViewUtil.requestFocus(mBtnStart);
			// OLEDOperation.getInstance().showTextOnOled("交接扫描停止");
			break;

		default:
			break;
		}
	}

	/**
	 * @param n
	 *            0 为 预扫描, 其他 为 交接扫描
	 * @description 预扫描 与 交接扫描 间切换, 切换 3个 遥控按钮显示的 名称
	 */
	private void switchViewName(int n) {
		if (0 != n) {
			n = 1;
			mVmingle.setVisibility(View.GONE);
			mTvFinishTp.setVisibility(View.VISIBLE);
			mTvLock.setVisibility(View.VISIBLE);
			mStepPointer = STEP_IN_START_1;
		} else {
			// mVmingle.setVisibility(View.VISIBLE);
			mTvFinishTp.setVisibility(View.GONE);
			mTvLock.setVisibility(View.GONE);
			mStepPointer = STEP_IN_START_0;
		}
		LogsUtil.d(TAG, "switchViewName StepPointer:" + mStepPointer);
		mVTitle.setText(text_title[n]);
		mBtnStart.setText(text_start[n]);
		mBtnStop.setText(text_swing[n]);
		mBtnHandScan.setText(text_reset[n]);
	}

	private boolean isQuery;

	/**
	 * 查询 托盘信息
	 */
	private void querySalver() {
		mDiaUtil.setProgressDialogCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				isQuery = false;
			}
		});
		mDiaUtil.showProgressDialog("正在扫描任一袋锁...");
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				haveTaskRunning = true;
				int what = MSG_SCAN_BAGID_FAILED;
				isQuery = true;
				for (int i = 0; i < 50 && isQuery; i++) {
					byte[] mUid = BagOperation.getInstance().getUid(); // 寻NFC卡

					if (null != mUid && mUid.length == 7) {
						int r = BagOperation.getInstance().getNfcBagOperation()
								.readBagID(); // 读 袋Id

						if (r == CoverNfcNewBagActivity.BAG_HAD_INIT) {
							what = MSG_SCAN_BAGID_SUCCESS;
							break;
						}
					}
				}

				mHandler.sendEmptyMessage(what);
				// mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
			}
		});

	}

	/**
	 * 确认 混托盘
	 * 
	 * @param salverId
	 *            托盘号
	 */
	private void mingleSalver(String salverId) {
		SocketConnet.getInstance().communication(SendTask.CODE_MINGLE_SALVER,
				new String[] { salverId }); // 查询 混托盘 信息
		waitReply(R.id.iv_yk_instore_mingle, MSG_MINGLE_SALVER_REPLY);
	}

	/**
	 * 扫描入库托盘
	 */
	private void scanSalver() {
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				haveTaskRunning = true;
				UHFOperation uhfOperation = UHFOperation.getInstance();
				sacnningPallet = true;
				for (int i = 0; i < 60 && sacnningPallet; i++) {
					if (uhfOperation.findOne()) {
						StaticString.YK_BAGID = ArrayUtils
								.bytes2HexString(uhfOperation.mEPC);
						SocketConnet.getInstance().communication(
								SendTask.CODE_YK_START_IN);
						waitReply(R.id.btn_yk_go, MSG_YK_REPLY);
						break;
					}
					SystemClock.sleep(50);
				}
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
			}
		});
	}

	/**
	 * 增加一个车方向控制
	 */
	private void buildCarDirctionControl() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
				"预扫描已关闭，交接车辆是否为倒车？默认3秒进入正向操作。");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				SocketConnet.getInstance().communication(200,
						new String[] { "BACKCAR" });
				arg0.dismiss();
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				LogsUtil.d(TAG, "707:对话框消失");
				mBtnHandScan.performClick();
			}
		});
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}, 3000);
	}

	/**
	 * 通知 服务器 进入 预扫描
	 */
	private void sendPreOpen() {
		SocketConnet.getInstance().communication(
				SendTask.CODE_PRE_OPEN_IN_IS_READY);
		waitReply(0, MSG_PRE_REPLY);
	}

	private class MyHandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			mDiaUtil.dismissProgressDialog();
			switch (msg.what) {
			case MSG_DOOR_READY:// 半自动 天线， 留在当前遥控界面
				findView();
				mBtnStart.setEnabled(true);
				mBtnHandScan.setVisibility(View.VISIBLE);
				ViewUtil.requestFocus(mBtnStart);
				if (TYPE_OP.IN_STORE_DOOR_PRE == mOptype) {
					switchViewName(0);
				} else {
					switchViewName(1);
				}
				String[] split = msg.obj.toString().split(" ");
				scanModle = -1;// 天线柜的扫描模式
				if ("00".equals(split[4])) {// 锁模式（正常）
					scanModle = 0;
				} else if ("01".equals(split[4]) && "01".equals(split[5])) {// 锁或袋模式，并且天线柜已统计完成
					scanModle = 1;
				} else if ("02".equals(split[4]) && "01".equals(split[5])) {// 锁和袋模式，并且天线柜已统计完成
					scanModle = 2;
				} else {
					mDiaUtil.showDialog("天线柜未统计完毕");
					finish();
					return true;
				}
				break;
			case MSG_DOOR_READY_AUTO:// 全自动 天线， 进入补扫界面
				// *1000 02 06 xx yy zz 手持IP 批号 包数#
				String[] split2 = msg.obj.toString().split(" ");
				scanModle = -1;// 天线柜的扫描模式
				if ("00".equals(split2[4])) {// 锁模式（正常）
					scanModle = 0;
				} else if ("01".equals(split2[4]) && "01".equals(split2[5])) {// 锁或袋模式，并且天线柜已统计完成
					scanModle = 1;
				} else if ("02".equals(split2[4]) && "01".equals(split2[5])) {// 锁和袋模式，并且天线柜已统计完成
					scanModle = 2;
				} else {
					mDiaUtil.showDialog("天线柜未统计完毕");
					finish();
					return true;
				}

				Intent intent = new Intent(YkInstoreActivity.this,
						LotScanActivity.class);
				intent.putExtra(TYPE_OP.KEY_TYPE, mOptype);
				intent.putExtra(MyContexts.KEY_DOOR_MODLE, scanModle);
				startActivity(intent);
				finish();
				return true;
			case MSG_REPLY_TIMEOUT:// 回复超时
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				if (null == mVTitle) {
					// 天线柜未准备好
					ToastUtil.showToastInCenter("天线柜未准备好");
					finish();
				} else {
					mDiaUtil.showReplyDialog();
				}
				return true;
			case MSG_PRE_REPLY:// 预扫描指令 回复 结果
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				String[] preReply = StaticString.information.split(" ");
				if ("00".equals(preReply[1])) {
					ToastUtil.showToastInCenter("执行成功!");
					preCmdSessucc((Integer) msg.obj);
				} else if ("01".equals(preReply[1])) {
					ToastUtil.showToastInCenter("执行失败!");
				} else {
					ToastUtil.showToastInCenter("发送失败!");
				}
				break;

			case MSG_YK_REPLY:// 遥控指令 回复 结果
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				String[] outtask = StaticString.information.split(" ");
				if ("00".equals(outtask[1])) {
					ToastUtil.showToastInCenter("执行成功!");
					jjCmdSessucc((Integer) msg.obj);
				} else if ("01".equals(outtask[1])) {
					if (2 < outtask.length) {
						ToastUtil.showToastInCenter("执行失败! " + outtask[2]);
					} else {
						ToastUtil.showToastInCenter("执行失败!");
					}
				} else {
					ToastUtil.showToastInCenter("发送失败!");
				}
				break;
			case MSG_FINISH_TP:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				String[] tp = StaticString.information.split(" ");
				if ("00".equals(tp[1])) {
					// SocketConnet.getInstance().communication(
					// SendTask.CODE_PRE_OPEN_IN_IS_READY);
					// waitReply(0, MSG_PRE_REPLY);
					sendPreOpen();
				} else if ("01".equals(tp[1])) {
					ToastUtil.showToastInCenter("执行失败!");
				} else {
					ToastUtil.showToastInCenter("服务器回复异常");
				}
				break;
			case MSG_SHOW_LOCK_RESULT_DIALOG:
				// dismissWaittingDialog();
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				final String info = ReplyParser
						.parseReply(StaticString.information);
				if ("锁定批成功".equals(info)) {
					YkInstoreActivity.this.finish();
				}
				break;
			case MSG_SCAN_BAGID_SUCCESS:
				LogsUtil.d(TAG, "MSG_SCAN_BAGID_SUCCESS");
				mDiaUtil.dismissProgressDialog();
				SocketConnet.getInstance().communication(
						SendTask.CODE_QUERY_SALVER_INFO,
						new String[] { StaticString.bagid }); // 查询 混托盘 信息
				waitReply(R.id.iv_yk_instore_mingle, MSG_QUERY_SALVER_REPLY);
				break;
			case MSG_SCAN_BAGID_FAILED:
				LogsUtil.d(TAG, "MSG_SCAN_BAGID_FAILED");
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showDialog("扫描袋锁失败,请重试");
				break;
			case MSG_QUERY_SALVER_REPLY:
				LogsUtil.d(TAG, "MSG_QUERY_SALVER_REPLY");
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				final String[] salverInfo = StaticString.information.split(" ");
				if ("00".equals(salverInfo[1]) && 4 < salverInfo.length) {
					// 查询托盘成功
					mDiaUtil.showSalverInfoDialog(salverInfo[2], salverInfo[3],
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mingleSalver(salverInfo[4]);
								}
							});
				} else {
					// 查询 托盘失败
					mDiaUtil.showDialog(salverInfo[2]);
					// mDiaUtil.showDialog("查询托盘信息失败");
				}
				break;
			case MSG_MINGLE_SALVER_REPLY:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				String[] mingle = StaticString.information.split(" ");
				if ("00".equals(mingle[1])) {
					// 混托盘成功
					mDiaUtil.showDialog("混托盘成功");
				} else {
					// 混 托盘失败
					mDiaUtil.showDialog(mingle[2]);
				}
				break;
			// TODO
			default:
				break;
			}
			return false;
		}

	}

	/**
	 * @author orsoul
	 * @des 预扫描 与 交接扫描 自动切换 监听
	 */
	private class JjRecvListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			LogsUtil.d(TAG, "onRecieve:" + recString);
			if (TextUtils.isEmpty(recString)) {
				return;
			}

			if (recString.startsWith("*100 ")) { // perStop
				// 预扫描 完成一个托盘
				YkInstoreActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						preCmdSessucc(R.id.btn_yk_stop);
						// mBtnStop.performClick();
					}

				});
				LogsUtil.d("自动切换", "预扫描 完成");
			} else if (recString.startsWith("*101 ")) {
				// 交接扫描 完成一个托盘
				YkInstoreActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						sendPreOpen();
					}

				});
				LogsUtil.d("自动切换", "交接扫描 完成一个托盘");
			} else if (recString.startsWith("*102 ")
					|| recString.startsWith("*112 ")) {
				// 交接扫描 完成
				YkInstoreActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mTvLock.performClick();
					}
				});
				LogsUtil.d("自动切换", "交接扫描 完成");
			} else if (recString.startsWith("*108 00 ")) {
				// 前置和天线柜已经准备就绪
				mHandler.sendEmptyMessage(MSG_DOOR_READY);
				YkInstoreActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mDiaUtil.dismissProgressDialog();
						OLEDOperation.getInstance().showTextOnOled("预扫描就绪",
								"请按确认键", "开始预扫描");
						mBtnStart.setEnabled(true);
						mBtnHandScan.setVisibility(View.VISIBLE);
						ViewUtil.requestFocus(mBtnStart);
						if (TYPE_OP.IN_STORE_DOOR_PRE == mOptype) {
							switchViewName(0);
						} else {
							switchViewName(1);
						}
					}
				});
				LogsUtil.e(TAG, "天线柜，准备完毕");
			} else if ("*108 01".equals(recString)) {// 尚未准备好
				LogsUtil.e(TAG, "天线柜，尚未准备好");
				// mHandler.sendEmptyMessage(MSG_PRE_START_NOT_READY);
			} else if (recString.startsWith("*1000 02 06 01")) {
				// 半自动 天线
				Message msg = mHandler.obtainMessage();
				msg.obj = recString;
				msg.what = MSG_DOOR_READY;
				mHandler.sendMessage(msg);
			} else if (recString.startsWith("*1000 02 06 02 ")) {
				// 全自动 天线
				Message msg = mHandler.obtainMessage();
				msg.obj = recString;
//				msg.what = MSG_DOOR_READY_AUTO;
				msg.what = MSG_DOOR_READY;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
		}
	}
}