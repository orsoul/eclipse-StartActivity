package com.fanfull.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.activity.scan_general.CoverNfcNewBagActivity;
import com.fanfull.activity.scan_general_oldbag.CIOActivity4oldBag;
import com.fanfull.activity.scan_lot.LotScanActivity;
import com.fanfull.activity.scan_lot.YkInstoreActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.entity.BankInfoBean;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.view.ActivityHeadItemView;

/**
 * 入库任务 选择界面
 * 
 * @author orsoul
 *
 */
public class InStorePickTask extends BaseActivity { // 0918

	// private static final String OPEN_BAG = "开袋任务选择";
	// private static final String COVER_BAG = "封袋任务选择";
	// private static final String IN_STORE = "入库任务选择";
	// private static final String OUT_STORE = "出库任务选择";
	// private static final String CHECK_COVER = "验封任务选择";
	//
	// private static final String PRE_IN_STORE = "入库交接任务";
	// private static final String PRE_OUT_STORE = "出库交接任务";
	// public static final String KEY_IN_STORE = "KEY_IN_STORE";
	// public static final String KEY_IS_EZ = "KEY_IS_EZ";

	private final int HANDLE_REPLY_OK = 0;
	private final int HANDLE_REPLY_ERR = 1;
	// private final int MSG_HANDSET_SCAN_OK = 3;
	// private final int MSG_HANDSET_SCAN_ERR = 4;
	private final int MSG_GET_BANKLIST = 5;

	// private int mOperationType;
	private com.fanfull.view.ActivityHeadItemView mVTitle;
	// private OperationBean mOperationBean;
	private ListView mListview;
	private List<String> mList = new ArrayList<String>();

	private int mCurrent = 0;
	private CommonAdapter<BankInfoBean> mAdapter;
	private JSONObject mBankCodeJson;

	private boolean isPiDisplay = false;// 判断是批下发还是任务
	private Button mCanclBtn;

	List<BankInfoBean> mBankInfoBeanList = new ArrayList<BankInfoBean>();
	private String bankList;
	private MyRecieveListener mRecieveListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mHandler = new Handler(new HandlerCallback());
		mDiaUtil = new DialogUtil(this);
		mDiaUtil.showProgressDialog();

		bankList = getIntent().getStringExtra(MyContexts.KEY_BANK_LIST);

		mRecieveListener = new MyRecieveListener();
		SocketConnet.getInstance().setRecieveListener(mRecieveListener);
		// 从 服务端 获取银行列表
		SocketConnet.getInstance().communication(SendTask.CODE_BANK_CODE_LIST);

		super.onCreate(savedInstanceState);
		
		// 定时线程
		// ThreadPoolFactory.getNormalPool().execute(new Runnable() {
		// @Override
		// public void run() {
		// SystemClock.sleep(8000);
		// if (mDiaUtil.progressDialogIsShowing()) {
		// SocketConnet.getInstance().setRecieveListener(null);
		// mHandler.sendEmptyMessage(MSG_GET_BANKLIST);
		// }
		// }
		// });

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_in_out_store_task);
		mVTitle = (ActivityHeadItemView) findViewById(R.id.v_title);
		mCanclBtn = (Button) findViewById(R.id.btn_cancel);

		if ("*28".equals(getIntent().getStringExtra(MyContexts.KEY_BANK_LIST)
				.split(" ")[0])) {
			isPiDisplay = true;
			LogsUtil.d(TAG, "isPiDisplay=" + isPiDisplay);
			mVTitle.setText("入库批选择");
			mCanclBtn.setText("新建批");
		} else {// 08 任务
			isPiDisplay = false;
			LogsUtil.d(TAG, "isPiDisplay=" + isPiDisplay);
			mCanclBtn.setText("显示批");
		}

		mCanclBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPiDisplay) {// 新建一个批
					ThreadPoolFactory.getNormalPool().execute(new Runnable() {
						public void run() {
							SocketConnet.getInstance().communication(-3);
							if (!ReplyParser.waitReply()) {
								// 回复超时
								mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
								return;
							}

							if (StaticString.information.startsWith("*18")) {
								// 正常返回
								StaticString.pinumber = StaticString.information
										.substring(4, 22);
								mHandler.sendEmptyMessage(HANDLE_REPLY_OK);
							} else {
								// 02 异常处理 返回上层操作
								mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
							}
						}
					});
				} else {// 从任务列表进入批列表
					mVTitle.setText("入库批选择");
					mCanclBtn.setText("新建批");
					SocketConnet.getInstance().communication(8);// 查询所有批，待确定
					ThreadPoolFactory.getNormalPool().execute(new Runnable() {
						public void run() {
							if (!ReplyParser.waitReply()) {
								// 回复超时
								mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
								return;
							}

							if (StaticString.information.startsWith("*28")) {
								// 从 服务端 获取银行列表
								SocketConnet.getInstance().communication(
										SendTask.CODE_BANK_CODE_LIST);
							} else if (StaticString.information
									.startsWith("*18")) {

							} else {

								// 02 异常处理 返回上层操作
								mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
							}
						}
					});
				}
			}
		});

		mListview = (ListView) findViewById(R.id.lv_in_out_store_task);
		mListview.setDivider(null);
		mAdapter = new CommonAdapter<BankInfoBean>(getApplicationContext(),
				mBankInfoBeanList, R.layout.item_in_store_task_list) {

			@Override
			public void convert(ViewHolder helper, BankInfoBean item,
					boolean isSelect) {

				helper.setText(R.id.tv_item_in_store_tasklist_num,
						item.getTvNum());
				helper.setText(R.id.tv_item_in_store_bank_name,
						item.getTvBankName());
				helper.setText(R.id.tv_item_in_store_info, item.getTvInfo());
				if (isSelect) {
					helper.getConvertView().setBackgroundColor(
							getApplicationContext().getResources().getColor(
									R.color.orangered));
				} else {
					helper.getConvertView().setBackgroundColor(
							getApplicationContext().getResources().getColor(
									R.color.transparent));
				}

			}
		};
		mListview.setAdapter(mAdapter);

		mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SocketConnet.getInstance().setRecieveListener(mRecieveListener);
				mCurrent = position;
				// boolean b = parent.getId() == R.id.lv_in_out_store_task;
				// System.out.println(b);
				// System.out.println("position:" + position);
				// System.out.println("id:" + id);

				// StaticString.tasknumber = -1 即选择批
				StaticString.tasknumber = String.valueOf(position + 1);
				// SocketConnet.getInstance().communication(5); // 选择入库任务
				String tastType = getIntent().getStringExtra(
						MyContexts.KEY_TASK_TYPE);
				SocketConnet.getInstance().communication(
						SendTask.CODE_SELECT_INSTORE_TASK,
						new String[] { tastType, StaticString.tasknumber }); // 选择入库任务
				mDiaUtil.showProgressDialog();

				// ThreadPoolFactory.getNormalPool().execute(new Runnable() {
				// @Override
				// public void run() {
				//
				// if (!ReplyParser.waitReply()) {
				// // 回复超时
				// mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
				// return;
				// }
				// if (StaticString.information.startsWith("*18")) {
				// // 正常返回
				// StaticString.pinumber = StaticString.information
				// .substring(4, 22);
				// mHandler.sendEmptyMessage(HANDLE_REPLY_OK);
				// } else {
				// // 02 异常处理 返回上层操作
				// mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
				// }
				// } // end run()
				// });

			}
		});

	}

	private void initdata() {
		mList.clear();
		mBankInfoBeanList.clear();
		mList.addAll(ReplyParser.parse2List(bankList, mBankCodeJson));
		for (int i = 0; i < mList.size(); i++) {
			BankInfoBean bankInfoBean = new BankInfoBean();
			String[] strs = mList.get(i).split(ReplyParser.SPLIT_FLAG);
			bankInfoBean.setTvNum(String.valueOf(i + 1));
			bankInfoBean.setTvBankName(strs[0]);
			bankInfoBean.setTvInfo(strs[1]);
			mBankInfoBeanList.add(bankInfoBean);
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onResume() {
		SocketConnet.getInstance().setRecieveListener(mRecieveListener);
		super.onResume();
	}
	@Override
	protected void onPause() {
		SocketConnet.getInstance().setRecieveListener(null);
		super.onPause();
	}

	// 重置back键功能
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int d = 1;
		switch (keyCode) {
		case KeyEvent.KEYCODE_2:
			d = -1;
		case KeyEvent.KEYCODE_8:
			int itemCount = mListview.getCount();
			mCurrent = (mCurrent + d + itemCount) % itemCount;

			setListViewItmeFocus(mCurrent);
			LogsUtil.d("mCurrent:" + (mCurrent));
			break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_5:
			View selectedView = mListview.getSelectedView();
			if (null != selectedView) {
				mListview.performItemClick(selectedView, mCurrent,
						mListview.getItemIdAtPosition(mCurrent));
				return true;
			}
			break;
		case KeyEvent.KEYCODE_4:
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		case KeyEvent.KEYCODE_DEL:
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			SocketConnet.getInstance().communication(-2);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setListViewItmeFocus(int pos) {
		if (null == mListview || null == mAdapter || pos < 0) {
			return;
		}
		mListview.setSelection(pos);
		mAdapter.setSection(pos);
		mAdapter.notifyDataSetChanged();
	}

	private class HandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_BANKLIST:
				mDiaUtil.dismissProgressDialog();
				initdata();
				break;
			case HANDLE_REPLY_OK:
				mDiaUtil.dismissProgressDialog();
				Intent intent = null;
				int instoreType = getIntent().getIntExtra(TYPE_OP.KEY_TYPE, -1);
				switch (instoreType) {
				case TYPE_OP.IN_STORE_DOOR:
				case TYPE_OP.IN_STORE_DOOR_PRE:
					/* 天线柜入库 */
					intent = new Intent(getApplicationContext(),
							YkInstoreActivity.class);
					break;
				case TYPE_OP.IN_STORE_HAND_LOT:
					// 手持批量入库
					intent = new Intent(getApplicationContext(),
							LotScanActivity.class);
					break;
				case TYPE_OP.IN_STORE_HAND:
					// 手持 逐个入库
					intent = new Intent(getApplicationContext(),
							CoverNfcNewBagActivity.class);
					break;
				case TYPE_OP.IN_STORE_OLD_BAG:
					// 使用第一代基金系统
					intent = new Intent(getApplicationContext(),
							CIOActivity4oldBag.class);
					break;
				default:
					break;
				}

				intent.putExtra(TYPE_OP.KEY_TYPE, instoreType);
				startActivity(intent);
				finish();
				break;
			case HANDLE_REPLY_ERR:
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showReplyDialog();
				break;
			default:
				break;
			}

			return false;
		}
	}

	private class MyRecieveListener extends RecieveListenerAbs {

		@Override
		public void onRecieve(String recString) {
			if (null == recString) {
				return;
			}
			if (recString.startsWith("*18")) {
				// 选择任务成功
				StaticString.pinumber = recString.substring(4, 22);
				mHandler.sendEmptyMessage(HANDLE_REPLY_OK);
			}
			if (recString.startsWith("*02")) {
				// 选择任务失败
				mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
			} else if (recString.startsWith("*{")) {
				// 获取银行列表
				String jsonStr = null;
				try {
					String[] split = recString.split(" ");
					jsonStr = split[0].substring(1);

					mBankCodeJson = new JSONObject(jsonStr);

					mHandler.sendEmptyMessage(MSG_GET_BANKLIST);
				} catch (Exception e) {
					LogsUtil.d("Exception", jsonStr);
					mHandler.sendEmptyMessage(MSG_GET_BANKLIST);
				}
			}
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(HANDLE_REPLY_ERR);
		}
	}
}
