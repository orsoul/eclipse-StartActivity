package com.fanfull.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.activity.other.CheckNfcCoverBagActivity;
import com.fanfull.activity.scan_general.ChangeBagMainActivity;
import com.fanfull.activity.scan_general.CoverNfcNewBagActivity;
import com.fanfull.activity.scan_general.QueryBagCirculationActivity;
import com.fanfull.activity.scan_general.ScanBunchActivity;
import com.fanfull.activity.scan_general_oldbag.CIOActivity4oldBag;
import com.fanfull.activity.scan_general_oldbag.CheckCoverBagActivity;
import com.fanfull.activity.scan_general_oldbag.OpenBag4OldBag;
import com.fanfull.activity.scan_lot.LotScanActivity;
import com.fanfull.activity.scan_lot.YkOutstoreActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.entity.MemberItem;
import com.fanfull.fff.R;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;

public class GeneralActivity extends BaseActivity {
	/** 封袋操作 */
	public static final int OPERATION_LOCK_BAG = 0;
	/** 入库操作 */
	public static final int OPERATION_IN_STORE = 1;
	/** 出库操作 */
	public static final int OPERATION_OUT_STORE = 2;
	/** 开袋操作 */
	public static final int OPERATION_OPEN_BAG = 3;
	/** 查找漏扫 */
	public static final int OPERATION_FIND_MISSING_BAG = 4;
	/** 换袋操作 */
	public static final int OPERATION_CHANGE_BAG = 5;
	/** 袋流转查询 */
	public static final int OPERATION_QUERY_BAG = 6;
	/** 验封操作 */
	public static final int OPERATION_CHECK = 7;

	private final int HANDLE_RESPONSE_ERR = 90;
	private final int MSG_REPLY_TIMEOUT = 91;

	// TODO

	private ArrayList<MemberItem> mMainList = null;

	private CommonAdapter<MemberItem> mMainAdapter = null;

	private ListView mListView = null;

	// private MyHandler mHandler;
	private int mOperationType;// 下个操作类型

	private int mCurrentPos = 0;
	int taskID = -1;
	/**
	 * 记录 点击的位置，复核回来后 根据该位置 进入相应功能
	 */
	private int mLastClickPos = -1;

	private MainOnItemClickListener mMainOnItemListener;
	private boolean isShowMainListView;

	private int mPickOp;

	// public static SocketConnet sConnet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler(new MyHandlerCallback());

		mDiaUtil = new DialogUtil(this);
		mDiaUtil.setProgressDialogCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				ReplyParser.stopWaitReply();
			}
		});

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_general_scan);

		mListView = (ListView) this.findViewById(R.id.list);
		mMainOnItemListener = new MainOnItemClickListener();
		mListView.setOnItemClickListener(mMainOnItemListener);

		showMainListView();
	}

	@Override
	protected void onResume() {
		SocketConnet.getInstance().setRecieveListener(new MyReciever());
		// StaticString.netThread_flag = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		SocketConnet.getInstance().setRecieveListener(null);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		LogsUtil.d("keyCode:" + keyCode);
		int d = 1;
		switch (keyCode) {
		case KeyEvent.KEYCODE_4: // 焦点 上移
			finish();
			break;
		case KeyEvent.KEYCODE_2: // 焦点 上移
			d = -1;
		case KeyEvent.KEYCODE_8: // 焦点 下移
			int itemCount = mListView.getCount();
			mCurrentPos = mListView.getSelectedItemPosition();
			mCurrentPos = (mCurrentPos + d + itemCount) % itemCount;

			setListViewItmeFocus(mCurrentPos);
			LogsUtil.d("mCurrentPos:" + (mCurrentPos));
			break;
		case KeyEvent.KEYCODE_ENTER:
			View selectedView = mListView.getSelectedView();
			if (null != selectedView) {
				mListView.performItemClick(selectedView, mCurrentPos,
						mListView.getItemIdAtPosition(mCurrentPos));
				return true;
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			if (isShowMainListView) {
				finish();
			} else {
				showMainListView();
			}
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// if (checkLogin && (8 <= StaticString.userIdcheck.length())) {
		// SocketConnet.getInstance().communication(996,
		// new String[] { "复核退出" });
		// StaticString.againPass = false;
		// }
		// mLastClickPos = -1;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 1. 从复核登录 回来
		if (CHECK_LOGIN_REQUEST_CODE == requestCode) {
			if (null != StaticString.userIdcheck) {
				mMainOnItemListener.onItemClick(null, null, mLastClickPos, 0);
			}
			return;
		}
	}

	private void dealNoRecv() {
		SoundUtils.play(SoundUtils.DROP_SOUND);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GeneralActivity.this);
		builder.setTitle("提示");
		builder.setMessage("后台没有返回消息，请稍后再试");
		builder.setPositiveButton("确定", null);
		builder.create().show();
	}

	// private void startWaitReplay() {
	// mDiaUtil.showProgressDialog();
	// ThreadPoolFactory.getNormalPool().execute(new Runnable() {
	// @Override
	// public void run() {
	// if (ReplyParser.waitReply()) {
	// mHandler.sendEmptyMessage(mOperationType);
	// } else {
	// mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
	// }
	// }
	// });
	// }

	private void setListViewItmeFocus(int pos) {
		if (null == mListView || null == mMainAdapter || pos < 0) {
			return;
		}
		mListView.setSelection(pos);
		mMainAdapter.setSection(pos);
		mMainAdapter.notifyDataSetChanged();
	}

	/**
	 * 显示 操作选择 ListView
	 */
	private void showMainListView() {
		if (null == mMainList) {
			mMainList = new ArrayList<MemberItem>();
			MemberItem item = null;

			item = new MemberItem();
			item.name = "封袋操作";
			item.nameEnglish = " envelop Operation";
			mMainList.add(item);

			item = new MemberItem();
			item.name = "入库操作";
			item.nameEnglish = "in-stockroom operation";
			mMainList.add(item);

			item = new MemberItem();
			item.name = "出库操作";
			item.nameEnglish = "out-stockroom operation";
			mMainList.add(item);

			item = new MemberItem();
			item.name = "开袋操作";
			item.nameEnglish = "Open operation";
			mMainList.add(item);

			item = new MemberItem();
			item.name = "查找漏袋";
			item.nameEnglish = "Find Missing";
			mMainList.add(item);
			
			item = new MemberItem();
			item.name = "换袋操作";
			item.nameEnglish = "Change operation";
			mMainList.add(item);

			item = new MemberItem();
			item.name = "袋流转查询";
			item.nameEnglish = "Query operation";
			mMainList.add(item);

			// MemberItem item0 = new MemberItem();
			// item0.name = "封袋操作";
			// item0.nameEnglish = " envelop Operation";
			// MemberItem item1 = new MemberItem();
			// item1.name = "入库操作";
			// item1.nameEnglish = " in-stockroom operation";
			// MemberItem item2 = new MemberItem();
			// item2.name = "出库操作";
			// item2.nameEnglish = "Outbound operation";
			// // MemberItem item3 = new MemberItem();
			// // item3.name = "验封操作";
			// // item3.nameEnglish = "check operation";
			// MemberItem item4 = new MemberItem();
			// item4.name = "开袋操作";
			// item4.nameEnglish = "open operation";
			// MemberItem item5 = new MemberItem();
			// item5.name = "换袋操作";
			// item5.nameEnglish = "change operation";
			// MemberItem item6 = new MemberItem();
			// item6.name = "返回上一页";
			// item6.nameEnglish = "go back";
			// this.mMainList.add(item0);
			// this.mMainList.add(item1);
			// this.mMainList.add(item2);
			// // this.mMainList.add(item3);
			// this.mMainList.add(item4);
			// this.mMainList.add(item5);
			// this.mMainList.add(item6);

			mMainAdapter = new CommonAdapter<MemberItem>(
					getApplicationContext(), mMainList,
					R.layout.item_general_activity_member) {

				@Override
				public void convert(ViewHolder helper, MemberItem item,
						boolean isSelect) {
					// TODO Auto-generated method stub
					helper.setText(R.id.name, item.name);
					helper.setText(R.id.nameEnglish, item.nameEnglish);
					helper.setTextColor(R.id.name, Color.BLUE);
					helper.setTextColor(R.id.nameEnglish, Color.BLACK);

					if (isSelect) {
						helper.getConvertView().setBackgroundResource(
								R.drawable.selector_item_general_list_focus);
					} else {
						helper.getConvertView().setBackgroundResource(
								R.drawable.selector_item_general_list);
					}
				}
			};
		}

		mListView.setOnItemClickListener(mMainOnItemListener);
		mListView.setAdapter(mMainAdapter);
		isShowMainListView = true;
		mListView.post(new Runnable() {
			@Override
			public void run() {
				if (StaticString.permission.endsWith("rh")) {
					setListViewItmeFocus(1);
				} else {
					setListViewItmeFocus(0);
				}
			}
		});
	}

	private class MyHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {

			mDiaUtil.dismissProgressDialog();

			String recievStr = null;
			if (msg.obj instanceof String) {
				recievStr = (String) msg.obj;
			}
			Intent intent = null;

			switch (msg.what) {
			case OPERATION_LOCK_BAG:
				// 处理回复异常
				if (null == recievStr || !recievStr.startsWith("*18")) {
					mDiaUtil.showDialog(getResources().getString(
							R.string.text_recieve_err));
					SoundUtils.playFailedSound();
					return true;
				}

				if (SPUtils.getBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE, false)) {
					// 老袋封袋
					intent = new Intent(getApplicationContext(),
							CIOActivity4oldBag.class);
					intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.COVER_BAG_OLD_BAG);
				} else {
					// 新袋封袋
					if (SPUtils.getBoolean(MyContexts.KEY_SCAN_BUNCH, false)) {
						// 冠字号，封袋前先进行扫捆操作
						intent = new Intent(GeneralActivity.this,
								ScanBunchActivity.class);
					} else {
						intent = new Intent(getApplicationContext(),
								CoverNfcNewBagActivity.class);
					}
					intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.COVER_BAG);
				}

				StaticString.pinumber = recievStr.substring(4, 22);
				startActivity(intent);
				break;
			case OPERATION_IN_STORE:
			case OPERATION_OUT_STORE:

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
					intent = new Intent(GeneralActivity.this,
							InStorePickTask.class);
					intent.putExtra(MyContexts.KEY_BANK_LIST,
							StaticString.information);
					if ("*08".equals(split[0])) {
						intent.putExtra(MyContexts.KEY_TASK_TYPE, "01");
					} else {
						intent.putExtra(MyContexts.KEY_TASK_TYPE, "02");
					}

				} else if ("*18".equals(split[0])) { // 无任务

					if (SPUtils.getBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE,
							false)) {
						// 使用第一代基金系统
						intent = new Intent(getApplicationContext(),
								CIOActivity4oldBag.class);
					} else {
						// 未启用遥控
//						intent = new Intent(getApplicationContext(),
//								IOStoreNfcActivity.class);
						intent = new Intent(getApplicationContext(),
								CoverNfcNewBagActivity.class);
					}
				} else if ("*38".equals(split[0])) {// 后置天线出库, 手动输入表单
					intent = new Intent(getApplicationContext(),
							YkOutstoreActivity.class);
					intent.putExtra(MyContexts.KEY_BANK_LIST, split[1]);
				} else {
					mDiaUtil.showDialog(getResources().getString(
							R.string.text_recieve_err));
					SoundUtils.playFailedSound();
					return true;
				}
				StaticString.pinumber = split[1];
				intent.putExtra(MyContexts.KEY_OPERATION_TYPE, mOperationType);
				intent.putExtra(TYPE_OP.KEY_TYPE, mPickOp);
				startActivity(intent);
				break;
			case OPERATION_CHECK:
				mDiaUtil.dismissProgressDialog();
				if (StaticString.information == null) {
					// MyToast.closeWaiting();
					dealNoRecv();
					break;
				}

				recievStr = StaticString.information.substring(1, 3);
				if ("08".equals(recievStr) || "28".equals(recievStr)) { // 有任务
					intent = new Intent(GeneralActivity.this,
							InStorePickTask.class);
				} else if ("18".equals(recievStr)) { // 无任务
					StaticString.pinumber = StaticString.information.substring(
							4, 22);// 没有任务的时候截取收到信息的第4为到26位作为pinumber;//substring(4,
									// 26)
					if (SPUtils.getBoolean(getApplicationContext(),
							MyContexts.KEY_USE_OLDBAG_ENABLE, false)) {
						LogsUtil.d(TAG, "---check old bag----");
						intent = new Intent(getApplicationContext(),
								CheckCoverBagActivity.class);
					} else {
						intent = new Intent(getApplicationContext(),
								CheckNfcCoverBagActivity.class);// 新锁验封
					}
					intent.putExtra(MyContexts.KEY_OPERATION_TYPE,
							mOperationType);
				} else {
					return true;
				}
				intent.putExtra(MyContexts.KEY_OPERATION_TYPE, mOperationType);
				startActivity(intent);
				break;
			case OPERATION_OPEN_BAG: // 开袋操作
				Log.d(TAG, "-----开袋操作-----");
				mDiaUtil.dismissProgressDialog();
				if (StaticString.information == null) {
					// MyToast.closeWaiting();
					dealNoRecv();
					break;
				}
				recievStr = StaticString.information.substring(1, 3);
				if ("08".equals(recievStr) || "28".equals(recievStr)) { // 有任务
					intent = new Intent(GeneralActivity.this,
							InStorePickTask.class);
				} else if ("18".equals(recievStr)) { // 无任务
					if (SPUtils.getBoolean(GeneralActivity.this,
							MyContexts.KEY_USE_OLDBAG_ENABLE, false)) {
						LogsUtil.d(TAG, "---open old bag----");
						intent = new Intent(GeneralActivity.this,
								OpenBag4OldBag.class);
					} else {
//						intent = new Intent(GeneralActivity.this,
//								OpenNfcNewBagActivity.class);
						intent = new Intent(getApplicationContext(),
								CoverNfcNewBagActivity.class);
					}

					intent.putExtra("flag", false);
					StaticString.pinumber = StaticString.information.substring(
							4, 22);
				} else {
					return true;
				}

				intent.putExtra(MyContexts.KEY_OPERATION_TYPE, mOperationType);
				intent.putExtra(TYPE_OP.KEY_TYPE, mPickOp);
				startActivity(intent);
				break;
			case HANDLE_RESPONSE_ERR:
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showReplyDialog();
				break;
			case MSG_REPLY_TIMEOUT:
				SoundUtils.playFailedSound();
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showReplyDialog();
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
			msg.what = mOperationType;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
		}
	}

	private int CHECK_LOGIN_REQUEST_CODE = 1024;

	private class MainOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 判断是否 需要复核
			if (null == StaticString.userIdcheck
					&& SPUtils.getBoolean(MyContexts.KEY_CHECK_LOGIN, true)
					&& !SPUtils.getBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE,
							false)) {
				mLastClickPos = position;
				Intent intent2 = new Intent(GeneralActivity.this,
						CheckUserInfoActivity.class);
				startActivityForResult(intent2, CHECK_LOGIN_REQUEST_CODE);
				// startActivity(intent2);
				return;
			}

			Intent intent = null;
			mOperationType = position;
			taskID = -1;

			if (OPERATION_QUERY_BAG == position) {
				intent = new Intent(GeneralActivity.this,
						QueryBagCirculationActivity.class);
				startActivity(intent);
				return;
			}

			switch (position) {
			case OPERATION_LOCK_BAG:
				taskID = SendTask.CODE_LOCK_BAG; // taskID = 21;
				if (SPUtils.getBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE, false)) {
					mPickOp = TYPE_OP.COVER_BAG_OLD_BAG;
				} else {
					mPickOp = TYPE_OP.COVER_BAG;
				}
				break;
			case OPERATION_IN_STORE:
				if (SPUtils.getBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE, false)) {
					taskID = SendTask.CODE_IN_STORE;
					mPickOp = TYPE_OP.IN_STORE_OLD_BAG;
				} else {
					// taskID = 4;// 入库任务查询 老版协议
					taskID = SendTask.CODE_IN_STORE_NEW;
					mPickOp = TYPE_OP.IN_STORE_HAND;
				}
				break;
			case OPERATION_OUT_STORE:
				if (SPUtils.getBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE, false)) {
					taskID = SendTask.CODE_OUT_STORE;
					mPickOp = TYPE_OP.OUT_STORE_OLD_BAG;
				} else {
					// taskID = 31;// 出库任务 //0918
					taskID = SendTask.CODE_OUT_STORE_NEW;
					mPickOp = TYPE_OP.OUT_STORE_HAND;
				}
				// if (SPUtils.getBoolean(MyContexts.KEY_LOT_DOOR, true)
				// && SPUtils.getBoolean(MyContexts.KEY_YK_ENABLE, true)) {
				// showOutStoreListView();
				// } else {
				// // taskID = 31;// 出库任务 //0918
				// taskID = SendTask.CODE_OUT_STORE_NEW;
				// }
				break;
			case OPERATION_CHECK:
				Log.d("case 3", "id:" + id + "验封操作");
				taskID = 41;// 查询验封任务
				break;
			case OPERATION_OPEN_BAG:
				Log.d("case 4", "id:" + id + "开袋操作");
				taskID = 51;// 查询开袋任务
				mPickOp = TYPE_OP.OPEN_BAG;
				break;
			case OPERATION_CHANGE_BAG:
				intent = new Intent(getApplicationContext(),
						ChangeBagMainActivity.class);
				startActivity(intent);
				return;
			case OPERATION_FIND_MISSING_BAG:
				intent = new Intent(getApplicationContext(),
						LotScanActivity.class);
				intent.putExtra(TYPE_OP.KEY_TYPE, TYPE_OP.FIND_MISSING_BAG);
				startActivity(intent);
				return;
			}

			mDiaUtil.showProgressDialog();
			SocketConnet.getInstance().communication(taskID);

			/** 出现点击封袋，超时的情况处理 */
			// if (-1 != taskID) {
			// mDiaUtil.showProgressDialog();
			// ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			// @Override
			// public void run() {
			// for (int i = 0; i < 2; i++) {
			// SocketConnet.getInstance().communication(taskID);
			// if (ReplyParser.waitReply()) {
			// mHandler.sendEmptyMessage(mOperationType);
			// taskID = -1;
			// return;
			// } else {
			// continue;
			// }
			// }
			// taskID = -1;
			// mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
			// }
			// });
			// }
		}
	}
}