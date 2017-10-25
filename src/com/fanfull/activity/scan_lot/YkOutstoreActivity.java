package com.fanfull.activity.scan_lot;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;

public class YkOutstoreActivity extends BaseActivity {

	public static final String TEXT_OUT_STORE_0 = "完整券出库";
	public static final String TEXT_OUT_STORE_1 = "残损券出库";

	private ActivityHeadItemView mVTitle;
	private Spinner mSp;
	private CheckBox mCbWz;
	private CheckBox mCbQf;
	private EditText mEt;
	private Button mBtnOk;
	private Button mBtnCancel;

	private com.fanfull.view.OutStoreTaskInputerView mV100;
	private com.fanfull.view.OutStoreTaskInputerView mV50;
	private com.fanfull.view.OutStoreTaskInputerView mV20;
	private com.fanfull.view.OutStoreTaskInputerView mV10;
	private com.fanfull.view.OutStoreTaskInputerView mV5;
	private com.fanfull.view.OutStoreTaskInputerView mV1;
	private AlertDialog diaNumberIputer;

	private boolean haveTaskRunning;
	private Handler mActHandler;
	private DialogUtil mDiaUtil;
	private JSONObject mJsonObj;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_yk_out_store);

		mVTitle = (ActivityHeadItemView) findViewById(R.id.v_title);
		mSp = (Spinner) findViewById(R.id.sp_out_store_bank);
		mCbWz = (CheckBox) findViewById(R.id.cb_out_store_wz);
		mCbQf = (CheckBox) findViewById(R.id.cb_out_store_qf);

		mEt = (EditText) findViewById(R.id.et_out_store_total);
		mEt.setOnClickListener(this);

		mBtnOk = (Button) findViewById(R.id.btn_out_store_ok);
		mBtnOk.setOnClickListener(this);

		mBtnCancel = (Button) findViewById(R.id.btn_out_store_cancel);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		String jsonStr = this.getIntent().getStringExtra(
				MyContexts.KEY_BANK_LIST);
		ArrayList<String> bankNames = null;
		try {
			bankNames = new ArrayList<String>();
			mJsonObj = new JSONObject(jsonStr);
			Iterator<String> keys = mJsonObj.keys();
			while (keys.hasNext()) {
				bankNames.add(keys.next());
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// Map<String, ?> map = SPUtils.getBankMap();
		// Collection<String> values = (Collection<String>) map.values();
		// String[] bankNames = values.toArray(new String[] {});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, bankNames);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSp.setAdapter(adapter);

		if (1 == SocketConnet.getInstance().getConnectedDoorNum()) {
			// 完整券 出库
			mVTitle.setText(TEXT_OUT_STORE_0);
			mCbWz.setChecked(true);
		} else if (2 == SocketConnet.getInstance().getConnectedDoorNum()) {
			// 残损券 出库
			mVTitle.setText(TEXT_OUT_STORE_1);
			mCbWz.setChecked(false);
		}
	}
	@Override
	protected void initEvent() {
		mEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

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
	private int mStepPointer = STEP_OUT_START_1;
	public static final int MSG_REPLY_TIMEOUT = 16;
	public static final int MSG_OUTSTORE_TASK_REPLY = 27;
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
			case MSG_REPLY_TIMEOUT:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				ToastUtil.showToastInCenter("服务器回复超时!");
				break;
			case MSG_OUTSTORE_TASK_REPLY:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				String[] outtask = StaticString.information.split(" ");
				if ("00".equals(outtask[1])) {
					ToastUtil.showToastInCenter("执行成功!");
					if (mStepPointer == STEP_OUT_START_1) {
						mStepPointer = STEP_OUT_STORE_GOING;
						mEt.setText(null);
						ViewUtil.requestFocus(mBtnCancel);
					} else if (mStepPointer == STEP_OUT_STORE_GOING) {
						mStepPointer = STEP_OUT_START_1;
						mBtnCancel.clearFocus();
					} else {
						
					}
				} else if ("01".equals(outtask[1])) {
					ToastUtil.showToastInCenter("执行失败!");
				} else {
					ToastUtil.showToastInCenter("发送失败!");
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
			tmp = MSG_OUTSTORE_TASK_REPLY;
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
				tmp = MSG_OUTSTORE_TASK_REPLY;
			} else {
				onBackPressed();
			}
			break;
		}
		final int msg = tmp;
		if (-1 != msg) {
			ThreadPoolFactory.getNormalPool().execute(new Runnable() {
				@Override
				public void run() {
					if (ReplyParser.waitReply()) {
						mHandler.sendEmptyMessage(msg);
					} else {
						mHandler.sendEmptyMessage(MSG_REPLY_TIMEOUT);
					}
				}
			});
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
									int totalBag = getTotalBag();
									if (0 != totalBag) {
										mEt.setText("合计袋数: "
												+ String.valueOf(totalBag));
									}
								}
							}).setNegativeButton("取消", null);
			diaNumberIputer = builder.create();
		}

		if (!diaNumberIputer.isShowing()) {
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
	
	
	
	
	

}
