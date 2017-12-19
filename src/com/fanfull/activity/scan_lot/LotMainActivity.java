package com.fanfull.activity.scan_lot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import com.fanfull.activity.CheckUserInfoActivity;
import com.fanfull.activity.InStorePickTask;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;

/**
 * @date 16/6-2015 14:15
 * @author lenovo
 * @describe 批量扫描主页面
 * @describe 包含3个页面 ，批量入库，批量出库，批量清点
 */
public class LotMainActivity extends BaseActivity {

	private Button mBtnInDoor;
	private Button mBtnInHand;
	private Button mBtnOutDoor;
	private Button mBtnOutHand;

	private String info = null;

	private static final int MSG_REPLY = 0;
	private static final int MSG_NO_TASK = 1;

	private static final int MSG_HAVE_TASK_OUT = 3;
	private static final int MSG_NO_TASK_OUT = 4;
	private static final int MSG_TIMEOUT_TASK_OUT = 5;

	private final int CHECK_LOGIN_REQUEST_CODE = 9999;

	private int mInStoreType;

	private View mClickView;
	// private boolean isChecked;
	private MyReciever mReciever;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler(new HandlerCallback());
		mDiaUtil = new DialogUtil(this);

		mReciever = new MyReciever();
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_lot_scan);

		mBtnInDoor = (Button) findViewById(R.id.btn_lot_scan_in_door);
		mBtnInDoor.setOnClickListener(this);

		mBtnInHand = (Button) findViewById(R.id.btn_lot_scan_in_hand);
		mBtnInHand.setOnClickListener(this);

		mBtnOutDoor = (Button) findViewById(R.id.btn_lot_scan_out_door);
		mBtnOutDoor.setOnClickListener(this);

		mBtnOutHand = (Button) findViewById(R.id.btn_lot_scan_out_hand);
		mBtnOutHand.setOnClickListener(this);

		OnLongClickListener listener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				switch (v.getId()) {
				case R.id.btn_lot_scan_in_door:// 锁 模式
					SocketConnet.getInstance().communication(
							SendTask.CODE_DZ_SWICH, new String[] { "00" });
					break;
				case R.id.btn_lot_scan_in_hand:// 锁或袋 模式
					SocketConnet.getInstance().communication(
							SendTask.CODE_DZ_SWICH, new String[] { "01" });
					break;
				case R.id.btn_lot_scan_out_hand:// 锁与袋 模式
					SocketConnet.getInstance().communication(
							SendTask.CODE_DZ_SWICH, new String[] { "02" });
					break;
				default:
					break;
				}
				mDiaUtil.showProgressDialog();

				return true;
			}
		};
		mBtnInDoor.setOnLongClickListener(listener);
		mBtnInHand.setOnLongClickListener(listener);
		mBtnOutHand.setOnLongClickListener(listener);

		ViewUtil.requestFocus(mBtnInDoor);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		// !isChecked &&
		if (null == StaticString.userIdcheck
				&& SPUtils.getBoolean(MyContexts.KEY_CHECK_LOGIN, true)) {
			mClickView = v;
			intent = new Intent(getApplicationContext(),
					CheckUserInfoActivity.class);
			// startActivity(intent);
			startActivityForResult(intent, CHECK_LOGIN_REQUEST_CODE);
			return;
		}

		SocketConnet.getInstance().setRecieveListener(mReciever);

		switch (v.getId()) {
		case R.id.btn_lot_scan_in_door:// 天线柜入库

			if (SPUtils.getBoolean(MyContexts.KEY_PRESCAN_ENABLE, false)) {
				mInStoreType = TYPE_OP.IN_STORE_DOOR_PRE;// 启用 预扫描
			} else {
				mInStoreType = TYPE_OP.IN_STORE_DOOR;
			}

			if (SPUtils.getBoolean(MyContexts.KEY_LOT_DOOR, false)) {
				// 启用了多道天线柜, 选择天线柜
				intent = new Intent(this, PickDoorActivity.class);
				intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.IN_STORE_DOOR);
				startActivityForResult(intent, TYPE_OP.IN_STORE_DOOR);
				return;
			}

			mDiaUtil.showProgressDialog();
			SocketConnet.getInstance()
					.communication(SendTask.CODE_IN_STORE_NEW);
			break;
		case R.id.btn_lot_scan_in_hand:// 手持批量入库
			mInStoreType = TYPE_OP.IN_STORE_HAND_LOT;
			// getInstoreTaskData();
			mDiaUtil.showProgressDialog();
			SocketConnet.getInstance()
					.communication(SendTask.CODE_IN_STORE_NEW);
			break;
		case R.id.btn_lot_scan_out_door:// 天线柜出库

			//ToastUtil.showToastInCenter("功能完善中，敬请期待");

			mInStoreType = TYPE_OP.OUT_STORE_DOOR;
			if (SPUtils.getBoolean(MyContexts.KEY_LOT_DOOR, false)) {
				// 启用了多道天线柜， 选择天线柜
				intent = new Intent(this, PickDoorActivity.class);
				intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.OUT_STORE_DOOR);
				startActivityForResult(intent, TYPE_OP.OUT_STORE_DOOR);
				return;
			}
			break;
		case R.id.btn_lot_scan_out_hand:// 手持批量出库
			mInStoreType = TYPE_OP.OUT_STORE_HAND_LOT;
			SocketConnet.getInstance().communication(
					SendTask.CODE_OUT_STORE_NEW);
			mDiaUtil.showProgressDialog();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		SocketConnet.getInstance().setRecieveListener(mReciever);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// SocketConnet.getInstance().setRecieveListener(null);
		// isChecked = false;
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogsUtil.d(TAG, "resultCode:" + resultCode);

		// 1. 从复核登录 回来
		if (CHECK_LOGIN_REQUEST_CODE == requestCode) {
			if (CheckUserInfoActivity.CHECK_SUCCESS_CODE == resultCode) {
				// isChecked = true;
				onClick(mClickView);
			}
			return;
		}

		// 2. 从选择 天线门回来
		// isChecked = true;
		final int operationType;
		final int doorNum;
		if (TYPE_OP.IN_STORE_DOOR == requestCode) {
			operationType = SendTask.CODE_IN_STORE_NEW;
			doorNum = resultCode;
		} else if (TYPE_OP.OUT_STORE_DOOR == requestCode) {
			operationType = SendTask.CODE_OUT_STORE;
			doorNum = resultCode + 1;
		} else {
			doorNum = -1;
			operationType = -1;
		}

		switch (doorNum) {
		case 0: // 第一道天线
		case 1: // 第二道天线
		case 2: // 第三道天线
			SocketConnet.getInstance().setRecieveListener(mReciever);
			int connectedDoorNum = SocketConnet.getInstance()
					.getConnectedDoorNum();
			LogsUtil.d(TAG, connectedDoorNum + "  doornum:" + doorNum);

			// 如果 已经与 所选择的天线连接, 直接通讯
			if (doorNum == SocketConnet.getInstance().getConnectedDoorNum()) {
				SocketConnet.getInstance().communication(operationType);
				return;
			}

			// 切换天线 并 通讯
			mDiaUtil.showProgressDialog();
			ThreadPoolFactory.getNormalPool().execute(new Runnable() {
				@Override
				public void run() {
					// 切换天线
					if (SocketConnet.getInstance().connect(doorNum)) {
						// 切换天线 成功, 进行通讯
						SocketConnet.getInstance().communication(operationType);
					} else {
						// 切换天线 失败, 进行提示
						mDiaUtil.dismissProgressDialog();
						ToastUtil.showToastOnUiThreadInCenter("连接第 "
								+ (doorNum + 1) + " 道天线失败,请检测网络!",
								LotMainActivity.this);
					}
				}
			});
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}

	}

	// 键盘上下左右功能
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_2: // up
			if (mBtnInDoor.isFocused()) {
				ViewUtil.requestFocus(mBtnOutHand);
			} else {
				ViewUtil.requestFocus(mBtnInDoor);
			}
			return true;
		case KeyEvent.KEYCODE_8: // down
			if (mBtnOutHand.isFocused()) {
				ViewUtil.requestFocus(mBtnInDoor);
			} else {
				ViewUtil.requestFocus(mBtnOutHand);
			}
			return true;
		case KeyEvent.KEYCODE_4: // left
			if (mBtnInHand.isFocused()) {
				ViewUtil.requestFocus(mBtnOutDoor);
			} else {
				ViewUtil.requestFocus(mBtnInHand);
			}
			return true;
		case KeyEvent.KEYCODE_6: // right
			if (mBtnOutDoor.isFocused()) {
				ViewUtil.requestFocus(mBtnInHand);
			} else {
				ViewUtil.requestFocus(mBtnOutDoor);
			}
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class HandlerCallback implements Handler.Callback {
		public boolean handleMessage(Message msg) {
			mBtnInHand.setEnabled(true);
			mBtnOutDoor.setEnabled(true);

			String recievStr = null;
			if (msg.obj instanceof String) {
				recievStr = (String) msg.obj;
			}

			Intent intent;
			switch (msg.what) {
			case MSG_REPLY:
				mDiaUtil.dismissProgressDialog();
				if (null == recievStr) {
					mDiaUtil.showDialog(getResources().getString(
							R.string.text_recieve_err));
					SoundUtils.playFailedSound();
					return true;
				}

				String[] split = recievStr.split(" ");
				if (("*08".equals(split[0]) || "*28".equals(split[0]))
						&& (0 == SocketConnet.getInstance()
								.getConnectedDoorNum())) { // 有任务或批: 08任务 28批
					intent = new Intent(getApplicationContext(),
							InStorePickTask.class);
					intent.putExtra(MyContexts.KEY_BANK_LIST,
							StaticString.information);
					if ("*08".equals(split[0])) {
						intent.putExtra(MyContexts.KEY_TASK_TYPE, "01");
					} else {
						intent.putExtra(MyContexts.KEY_TASK_TYPE, "02");
					}

				} else if ("*18".equals(split[0])) { // 无任务

					if (TYPE_OP.OUT_STORE_HAND_LOT == mInStoreType) {
						// 手持批量出库
						intent = new Intent(getApplicationContext(),
								LotScanActivity.class);
					} else if (TYPE_OP.IN_STORE_DOOR == mInStoreType) {
						// 门式入库
						intent = new Intent(getApplicationContext(),
								YkInstoreActivity.class);
					} else {
						// 手持批量入库
						intent = new Intent(getApplicationContext(),
								LotScanActivity.class);
					}
				} else if ("*1000".equals(split[0]) && "02".equals(split[1])
						&& "04".equals(split[2])) {
					// *1000 02 04 01
					if ("00".equals(split[3])) {
						ToastUtil.showToastInCenter("进入“锁”模式");
					} else if ("01".equals(split[3])) {
						ToastUtil.showToastInCenter("进入“锁或袋”模式");
					} else if ("02".equals(split[3])) {
						ToastUtil.showToastInCenter("进入“锁和袋”模式");
					} else {
						mDiaUtil.showDialog("切换模式失败");
					}
					return true;
				} else {
					return true;
				}
				StaticString.pinumber = split[1];
				intent.putExtra(TYPE_OP.KEY_TYPE, mInStoreType);
				startActivity(intent);

				break;
			case MSG_NO_TASK:
				intent = new Intent(getApplicationContext(),
						LotScanActivity.class);
				// intent.putExtra("KEY_PLAN_SCAN", 28);
				startActivity(intent);
				break;
			case MSG_HAVE_TASK_OUT:
				intent = new Intent(LotMainActivity.this, InStorePickTask.class);// 有任务进入任务列表
				intent.putExtra("flag", true);// 判断是否存在任务
				intent.putExtra(MyContexts.KEY_OPERATION_TYPE, 2);// 1表示批量入库
																	// 2表示批量出库
				startActivity(intent);
				break;
			case MSG_TIMEOUT_TASK_OUT:
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showDialog(getResources().getString(
						R.string.text_recieve_timeout));
				SoundUtils.playFailedSound();
				break;
			default:
				break;
			}
			return true;
		}
	}

	private class MyReciever extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			Message msg = mHandler.obtainMessage();
			msg.obj = recString;
			msg.what = MSG_REPLY;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_TIMEOUT_TASK_OUT);
		}
	}
}
