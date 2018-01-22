package com.fanfull.activity.scan_lot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.fff.R;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;
import com.fanfull.view.OutStoreTaskInputerView;

public class YkOutstoreActivity extends BaseActivity {

	public static final String TEXT_OUT_STORE_2 = "门 式 出 库";
	public static final String TEXT_OUT_STORE_0 = "完整券出库";
	public static final String TEXT_OUT_STORE_1 = "残损券出库";
	
	public static final int MSG_REPLY_TIMEOUT = 16;
	public static final int MSG_YK_REPLY = 15;
	public static final int MSG_DOOR_READY = 17;
	public static final int MSG_SCAN_FINISH = 18;
	private int mStepPointer = STEP_OUT_START_1;

	private ActivityHeadItemView mVTitle;
	private ImageView mImSwich;

	private LinearLayout mLlInput;
	private Spinner mSp;
	private CheckBox mCbWz;
	private CheckBox mCbQf;
	private EditText mEt;
	private TextView mTv;
	private Button mBtnOk;
	private Button mBtnCancel;

	private LinearLayout mLlCtrl;
	private Button mBtnStart;
	private Button mBtnStop;
	private Button mBtnHandScan;

	private OutStoreTaskInputerView mV100;
	private OutStoreTaskInputerView mV50;
	private OutStoreTaskInputerView mV20;
	private OutStoreTaskInputerView mV10;
	private OutStoreTaskInputerView mV5;
	private OutStoreTaskInputerView mV1;
	private OutStoreTaskInputerView[] V_ARRAY;
	/** 保存 已选择 的组件 */
	private List<com.fanfull.view.OutStoreTaskInputerView> checkedViewList;
	private AlertDialog diaNumberIputer;

	private boolean haveTaskRunning;
	private DialogUtil mDiaUtil;
	private JSONObject mJsonObj;
	private int mOptype;
	private JjRecvListener mRecvLis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		mOptype = getIntent().getIntExtra(TYPE_OP.KEY_TYPE,
				TYPE_OP.OUT_STORE_DOOR);
		LogsUtil.d(TAG, "OpType: " + mOptype);

		super.onCreate(savedInstanceState);

		mDiaUtil = new DialogUtil(this);

		mRecvLis = new JjRecvListener();
		SocketConnet.getInstance().setRecieveListener(mRecvLis);
		if ((mOptype != TYPE_OP.OUT_STORE_DOOR_POST)
				&& (mOptype != TYPE_OP.IN_STORE_DOOR_POST)) {
			// 单道门天线出库，查询天线柜状态
			SocketConnet.getInstance().communication(SendTask.CODE_DOOR_READY);
			mDiaUtil.showProgressDialog();
		} else {
			setResult(LotMainActivity.REQUEST_CODE_POST_DOOR);
		}

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_yk_out_store);

		mVTitle = (ActivityHeadItemView) findViewById(R.id.v_title);

		mLlCtrl = (LinearLayout) findViewById(R.id.ll_outstore_ctrl);

		mBtnStart = (Button) mLlCtrl.findViewById(R.id.btn_yk_outstore_start);
		mBtnStart.setOnClickListener(this);

		mBtnStop = (Button) mLlCtrl.findViewById(R.id.btn_yk_outstore_stop);
		mBtnStop.setOnClickListener(this);

		mBtnHandScan = (Button) mLlCtrl
				.findViewById(R.id.btn_yk_outstore_handscan);
		mBtnHandScan.setOnClickListener(this);

		if (TYPE_OP.OUT_STORE_DOOR_POST == mOptype) {
			mLlInput = (LinearLayout) findViewById(R.id.ll_outstore_input);
			mSp = (Spinner) mLlInput.findViewById(R.id.sp_out_store_bank);

			mCbWz = (CheckBox) mLlInput.findViewById(R.id.cb_out_store_wz);
			mCbQf = (CheckBox) mLlInput.findViewById(R.id.cb_out_store_qf);

			mEt = (EditText) mLlInput.findViewById(R.id.et_out_store_total);
			mEt.setOnClickListener(this);
			mTv = (TextView) findViewById(R.id.tv_outstore_list);
			mTv.setText(null);
			mTv.setVisibility(View.GONE);

			mBtnOk = (Button) mLlInput.findViewById(R.id.btn_out_store_ok);
			mBtnOk.setOnClickListener(this);

			mBtnCancel = (Button) mLlInput
					.findViewById(R.id.btn_out_store_cancel);
			mBtnCancel.setOnClickListener(this);

			mImSwich = (ImageView) findViewById(R.id.iv_yk_outstore_swich);
			mImSwich.setOnClickListener(this);
			mImSwich.setVisibility(View.VISIBLE);

			mLlInput.setVisibility(View.VISIBLE);
			mLlCtrl.setVisibility(View.GONE);
			mBtnHandScan.setText("返回");

			initListData();
			initEtEvent();
		}

		if (1 == SocketConnet.getInstance().getConnectedDoorNum()) {
			// 青岛 完整券（1楼） 出库
			mVTitle.setText(TEXT_OUT_STORE_0);
			// mCbWz.setChecked(true);
		} else if (2 == SocketConnet.getInstance().getConnectedDoorNum()) {
			// 青岛 残损券（2楼） 出库
			mVTitle.setText(TEXT_OUT_STORE_1);
			// mCbWz.setChecked(false);
		} else {
			// 其他地区 单门 出库
			mVTitle.setText(TEXT_OUT_STORE_2);
		}
	}

	private void initListData() {
		String jsonStr = this.getIntent().getStringExtra(
				MyContexts.KEY_BANK_LIST);
		ArrayList<String> bankNames = null;
		try {
			bankNames = new ArrayList<String>();

			// JSONObject bankListJson = new JSONObject(jsonStr);
			// Iterator<String> keys = bankListJson.keys();
			// Iterator<String> keys = bankListJson.keys();
			// mJsonObj = new JSONObject();

			mJsonObj = new JSONObject(jsonStr);
			Iterator<String> keys = mJsonObj.keys();
			while (keys.hasNext()) {
				bankNames.add(keys.next());
			}
			// LogsUtil.d(TAG, "old:" + bankListJson);
			LogsUtil.d(TAG, "new:" + mJsonObj);
		} catch (JSONException e) {
			e.printStackTrace();
			LogsUtil.d(TAG, "非后置天线 出库，无银行列表数据");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, bankNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp.setAdapter(adapter);
	}

	private void initEtEvent() {
		if (TYPE_OP.OUT_STORE_DOOR_POST != mOptype) {
			return;
		}

		mEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (0 == s.length()) {
					mBtnOk.setEnabled(false);
				} else {
					mBtnOk.setEnabled(true);
					ViewUtil.requestFocus(mBtnOk);
				}
			}

		});
	}

	
	/**
	 * 一楼出库
	 */
	public static final int STEP_OUT_START_1 = 100;
	/**
	 * 二楼出库
	 */
	public static final int STEP_OUT_START_2 = 101;

	public static final int STEP_OUT_STORE_GOING = 102;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_DOOR_READY:
				if (1 == msg.arg1) {
					// 半自动
					ToastUtil.showToastInCenter("半自动门式天线");
				} else if (2 == msg.arg1) {
					// 全自动
					ToastUtil.showToastInCenter("全自动门式天线");
					Intent intent = new Intent(YkOutstoreActivity.this,
							LotScanActivity.class);
					intent.putExtra(TYPE_OP.KEY_TYPE,
							TYPE_OP.OUT_STORE_HAND_LOT);
					startActivity(intent);
					finish();
				} else if (3 == msg.arg1) {
					// 后置天线

				} else {
					ToastUtil.showToastInCenter("天线柜未准备好");
				}
				break;
			case MSG_YK_REPLY:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				if (230 == msg.arg1) {
					swichPanel();
					mEt.setText(null);
					mTv.setText(null);
					mTv.setVisibility(View.GONE);
				} else {
					ToastUtil.showToastInCenter(msg.obj);
				}
				break;
			case MSG_SCAN_FINISH:
				swichPanel();
				break;
			case MSG_REPLY_TIMEOUT:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				ToastUtil.showToastInCenter("服务器回复超时!");
				if (null == mBtnOk) {
					SocketConnet.getInstance().setRecieveListener(null);
					finish();
				}
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		int tmp = -1;
		switch (v.getId()) {
		case R.id.et_out_store_total:
			LogsUtil.d("edit click");
			showNumberIputer();
			break;
		case R.id.btn_out_store_ok:
			if (haveTaskRunning) {
				return;
			}
			StaticString.OUT_STORE_TASK = getCmd();
			mDiaUtil.showProgressDialog();
			haveTaskRunning = true;
			SocketConnet.getInstance().communication(
					SendTask.CODE_OUT_STORE_TASK);
			break;
		case R.id.btn_out_store_cancel:
			if (haveTaskRunning) {
				return;
			}
			if (STEP_OUT_STORE_GOING == mStepPointer) {
				mDiaUtil.showProgressDialog();
				haveTaskRunning = true;
				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_RESET_OUT);
			} else {
				onBackPressed();
			}
			break;
		case R.id.iv_yk_outstore_swich:
			if (!haveTaskRunning) {
				swichPanel();
			}
			break;
		case R.id.btn_yk_outstore_start:// 开始
			if (!haveTaskRunning) {
				mDiaUtil.showProgressDialog();
				haveTaskRunning = true;
				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_START_OUT);
			}
			break;
		case R.id.btn_yk_outstore_stop:// 停止
			if (!haveTaskRunning) {
				mDiaUtil.showProgressDialog();
				haveTaskRunning = true;
				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_RESET_OUT);
			}
			break;
		case R.id.btn_yk_outstore_handscan:// 摆动/返回
			if (haveTaskRunning) {
				// do nothing
			} else if (TYPE_OP.OUT_STORE_DOOR_POST == mOptype) {
				swichPanel();
			} else {
				mDiaUtil.showProgressDialog();
				haveTaskRunning = true;
				SocketConnet.getInstance().communication(
						SendTask.CODE_YK_SWING_OUT);
			}
			break;
		}
		// final int msg = tmp;
		// if (-1 != msg) {
		// ThreadPoolFactory.getNormalPool().execute(new Runnable() {
		// @Override
		// public void run() {
		// if (ReplyParser.waitReply()) {
		// mHandler.sendEmptyMessage(msg);
		// } else {
		// mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
		// }
		// }
		// });
		// }
	}

	private void swichPanel() {
		if (mLlInput.getVisibility() == View.GONE) {
			mLlInput.setVisibility(View.VISIBLE);
			mLlCtrl.setVisibility(View.GONE);
		} else {
			mLlInput.setVisibility(View.GONE);
			mLlCtrl.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示 币种/袋数 输入
	 */
	private void showNumberIputer() {
		if (null == diaNumberIputer) {
			LayoutInflater factory = this.getLayoutInflater();
			final View v = factory.inflate(
					R.layout.dialog_out_store_number_input, null);
			mV100 = (com.fanfull.view.OutStoreTaskInputerView) v
					.findViewById(R.id.v_out_store_100);
			mV50 = (com.fanfull.view.OutStoreTaskInputerView) v
					.findViewById(R.id.v_out_store_50);
			mV20 = (com.fanfull.view.OutStoreTaskInputerView) v
					.findViewById(R.id.v_out_store_20);
			mV10 = (com.fanfull.view.OutStoreTaskInputerView) v
					.findViewById(R.id.v_out_store_10);
			mV5 = (com.fanfull.view.OutStoreTaskInputerView) v
					.findViewById(R.id.v_out_store_5);
			mV1 = (com.fanfull.view.OutStoreTaskInputerView) v
					.findViewById(R.id.v_out_store_1);
			mV100.setChecked(true);
			mV100.setNumber(28);
			V_ARRAY = new OutStoreTaskInputerView[]{mV100, mV50, mV20, mV10, mV5, mV1, };
			checkedViewList = new ArrayList<OutStoreTaskInputerView>();
			checkedViewList.add(mV100);
			// 控制 不使用 软键盘
			mV100.setInputType(InputType.TYPE_NULL);
			mV50.setInputType(InputType.TYPE_NULL);
			mV20.setInputType(InputType.TYPE_NULL);
			mV10.setInputType(InputType.TYPE_NULL);
			mV5.setInputType(InputType.TYPE_NULL);
			mV1.setInputType(InputType.TYPE_NULL);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v)
					.setTitle("设置币种/袋数")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									checkedViewList.clear();
									int totalBag = 0;
									StringBuilder sb = new StringBuilder();
									for (int i=0; i< V_ARRAY.length; i++) {
										if (V_ARRAY[i].isChecked()) {
											checkedViewList.add(V_ARRAY[i]);
											totalBag += V_ARRAY[i].getNumber();
											sb.append(V_ARRAY[i].getText()).append(":").append(V_ARRAY[i].getNumber()).append("\n");
										}
									}
									if (0 != totalBag) {
										mEt.setText("合计袋数: "
												+ String.valueOf(totalBag));
										sb.setLength(sb.length() - 1);
										mTv.setText(sb.toString());
										mTv.setVisibility(View.VISIBLE);
									}
								}
							}).setNegativeButton("取消", null);
			diaNumberIputer = builder.create();
		}

		if (!diaNumberIputer.isShowing()) {
			for (int i=0; i< V_ARRAY.length; i++) {
				V_ARRAY[i].setChecked(checkedViewList.contains(V_ARRAY[i]));
			}
			diaNumberIputer.show();
		}
	}

	/**
	 * @return 统计袋数
	 */
	private int getTotalBag() {
		try {
			return mV100.getNumber() + mV50.getNumber() + mV20.getNumber()
					+ mV10.getNumber() + mV5.getNumber() + mV1.getNumber();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
	 * @return 统计袋数
	 */
	private String getCmd() {
		String bankCode = mSp.getSelectedItem().toString();

		// 获取 银行代号
		// Map<String, String> map = (Map<String, String>) SPUtils.getBankMap();
		// Set<Map.Entry<String, String>> entrySet = map.entrySet();
		// for (Map.Entry<String, String> entry : entrySet) {
		// if (bankCode.equals(entry.getValue())) {
		// bankCode = entry.getKey();
		// break;
		// }
		// }

		try {
			bankCode = mJsonObj.getString(bankCode);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		String wz = null;
		String qf = null;
		if (mCbWz.isChecked()) {
			wz = "1";
		} else {
			wz = "0";
		}
		if (mCbQf.isChecked()) {
			qf = "1";
		} else {
			qf = "0";
		}

		try {
			return bankCode + mV100.getStringNumber() + mV50.getStringNumber()
					+ mV20.getStringNumber() + mV10.getStringNumber()
					+ mV5.getStringNumber() + mV1.getStringNumber() + wz + qf;
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public void onBackPressed() {
		clickTwiceFinish();
//		super.onBackPressed();
	}

	private class JjRecvListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			LogsUtil.d(TAG, "onRecieve:" + recString);
			if (TextUtils.isEmpty(recString)) {
				return;
			}

			Message msg = mHandler.obtainMessage();

			String[] split = recString.split(SocketConnet.CH_SPLIT);

			if ("*1000".equals(split[0]) && "02".equals(split[1])
					&& "06".equals(split[2])) {

				msg.what = MSG_DOOR_READY;

				if ("01".equals(split[3])) {
					// 半自动 天线
					msg.arg1 = 1;
				} else if ("02".equals(split[3])) {
					// 全自动 天线
					msg.arg1 = 2;
				} else if ("03".equals(split[3]) && split[4].startsWith("{")) {
					// 后置 天线
					msg.arg1 = 3;
					msg.obj = split[4];
				}
				mHandler.sendMessage(msg);
			} else if ("*38".equals(split[0])) {
				msg.obj = split[1];
				msg.what = MSG_DOOR_READY;
				mHandler.sendMessage(msg);
			} else if ("*108".equals(split[0])) {
				if ("00".equals(split[1])) {
					// 天线柜 完成扫描
					msg.what = MSG_SCAN_FINISH;
				} else {
					// 天线柜 出现漏扫，暂不处理
					msg.what = MSG_SCAN_FINISH;
				}
				mHandler.sendMessage(msg);
			} else {
				msg.what = MSG_YK_REPLY;
				if ("00".equals(split[1])) {
					msg.obj = "执行成功!";
					if ("*230".equals(split[0])) {
						msg.arg1 = 230;
					}
				} else if ("01".equals(split[1])) {
					if (2 < split.length) {
						msg.obj = "执行失败! " + split[2];
					} else {
						msg.obj = "执行失败!";
					}
				} else {
					msg.obj = "回复异常!";
				}
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
		}
	}

}
