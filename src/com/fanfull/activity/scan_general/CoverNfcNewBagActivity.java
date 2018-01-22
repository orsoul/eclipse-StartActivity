package com.fanfull.activity.scan_general;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fanfull.activity.setting.SettingPowerActivity;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.op.RFIDOperation;
import com.fanfull.op.UHFOperation;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.AESCoder;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.ClickUtil;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.Lock3Util;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;
import com.fanfull.view.CoverBagItemView;

/**
 * 锁3封袋，分5大步骤：
 * 
 * @step1 读取袋锁NFC中数据，并检查；由内部类CheckBagTask完成。
 * @step2 读取锁片TID中数据；由内部类ReadTIDTask完成。
 * @step3 上传袋信息，包括从NFC中读到的袋id、从锁片读到的TID等,从服务器获得封袋事件码、交接信息、封袋流水号，对封袋事件码和交接信息进行加密；
 *        由内部类NetCoverTask完成。
 * @step4 将袋id写入锁片EPC区，加密后的封签事件码写入USE区；由内部类WriteUHFTask完成。
 * @step5 将锁片TID、封袋流水号、以及加密后的封签事件码和交接信息 写入袋锁NFC；由内部类WriteRFIDTask完成。
 * 
 */
public class CoverNfcNewBagActivity extends BaseActivity {

	/** 未使用锁片EPC区第1个字节:0xEF */
	private static final byte PIECE_NOT_USE_FLAG = (byte) 0xEF;
	
	private static final String TEXT_COVER = "封 袋";
	private static final String TEXT_IN_STORE = "入 库";
	private static final String TEXT_OUT_STORE = "出 库";
	private static final String TEXT_OPEN = "开 袋";

	private TextView mTvPlanAmount;
	private TextView mTvTotalAmount;
	private TextView mTvScanAmount;
	private TextView mTvLock;

	private CoverBagItemView mVReadBagLock;
	private CoverBagItemView mVNetCheck;
	private CoverBagItemView mVWriteNFC;

	private Button mBtnConfirm;
	private Button mBtnCancel;

	public static final int BAG_HAD_INIT = 5;

	private final int SHOW_LOCK_RESULT = 11;
	private final int SHOW_LOCK_FAILED = 12;

	private final static int MSG_GET_INSTORE_INFO_OVER = 0x1003;// 开始读袋锁
	private final static int MSG_CHECK_BAG_START = 0x1002;// 开始读袋锁
	private final int MSG_CHECK_BAG_FAILED = 0x1000;
	private final static int MSG_CHECK_BAG_SUCCESS = 0x1001;

	private final static int MSG_NET_CHECK_START = 0x1005;// 开始服务器校验
	private final static int MSG_COVER_NET_SUCCESS = 0x1009;
	private final static int MSG_COVER_NET_FAILED = 0x1010;
	private final static int MSG_COVER_NET_TIMEOUT = 0x1011;

	private final static int MSG_WRITE_NFC_FAILED = 0x1013;

	private final static int MSG_WRITE_NFC_SUCCESS = 0x1014;

	// 任务进度
	private final int STEP_SELECT_TYPE = 0;
	private final int STEP_CHECK_BAG = 1;
	private final int STEP_NET_CHECK = 3;
	private final int STEP_UPDATE_BAG = 5;

	private final int STEP_COVER_OVER = 6;

	private int mStep = STEP_CHECK_BAG;

	private boolean haveTaskRunning;

	private UHFOperation mUHFOp = null;

	private CheckBagTask mCheckBagTask;
	private WriteRFIDTask mWriteNFCTask;
	private CoverBagRecieveListener mRecieveListener;

	private String mTotalFinish;// 总完成数量
	private String mPersonFinish;// 个人完成数量
	private String mPlanNumber;// 个人完成数量

	/** 启用码 4byte */
	private byte[] mEnableCode = null;

	private boolean isFirstScan = true;
	// 用于记录选择了什么券别，清分，复点信息。
	private String mStr, str1 = "", str2 = "", str3 = "";

	private byte mUid[] = null;
	/** 封签 信息 30byte */
	private byte[] mCoverEventData;
	/** 交接 信息 12byte */
	private byte[] mHandoverData;
	/** 袋ID 12byte */
	private byte[] mBagIdBuf;
	/** 锁片TID 6byte */
	private byte[] mPieceTid;
	/** 流水号 11byte */
	// private byte[] mSerialData;

	/** 交接信息 24char */
	// private String mHandoverInfo;
	/** 流水号 22char */
	private String mSerialNumber;

	/** 密钥编号 */
	private int miyueNum = -1;
	/** 交接信息索引 */
	private byte mHandoverIndex;

	private long startTime;
	private RFIDOperation newRFIDOp;

	/** 业务类型：封袋、出入库、开袋 */
	private int mTypeOp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mUHFOp = UHFOperation.getInstance();
		if (mUHFOp.open(false) < 0) {
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_init_uhf_failed));
			return;
		}
		newRFIDOp = RFIDOperation.getInstance();
		// newRFIDOp.openTemp(false);

		mHandler = new Handler(new MyHandlerCallBack());
		mDiaUtil = new DialogUtil(this);

		super.onCreate(savedInstanceState);

		mRecieveListener = new CoverBagRecieveListener();
		SocketConnet.getInstance().setRecieveListener(mRecieveListener);

		findView();

		mBagIdBuf = new byte[12];
		mPieceTid = new byte[6];

		/** 第1 读袋码，以及验证是否启用 */
		mCheckBagTask = new CheckBagTask();
		/** 第3 更新袋锁，写标志位以及索引信息 */
		mWriteNFCTask = new WriteRFIDTask();

		/* 根据业务类型 设置 超高频 功率 */
		SystemClock.sleep(100); // 打开模块后，休眠一段时间再使用
		boolean isCover = TYPE_OP.COVER_BAG == mTypeOp;
		if (Lock3Util.setCoverPower(isCover) < 1) {
			ToastUtil.showToastInCenter("功率设置失败，请手动设置");
		}

	}

	private int mNetCode;

	/**
	 * 
	 * @Title: initView
	 * @Description: 初始化界面
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	protected void findView() {

		setContentView(R.layout.activity_cover_newbag);
		ActivityHeadItemView mTitle = (ActivityHeadItemView) findViewById(R.id.v_coverbagactivity_title);

		mTypeOp = getIntent().getIntExtra(TYPE_OP.KEY_TYPE, TYPE_OP.COVER_BAG);
		LogsUtil.d(TAG, "mTypeOp : " + mTypeOp);

		switch (mTypeOp) {
		case TYPE_OP.COVER_BAG:
			mTitle.setText(TEXT_COVER);
			mNetCode = SendTask.CODE_COVER_UPLOAD_BAG_INFO;

			/** 获取批编号，判断和上一次是否有改变，如果改变则需要弹出券别选择框 */
			String lastNumber = SPUtils.getString(MyContexts.IS_LAST_PI_NUMBER,
					"1");
			if (!lastNumber.equals(StaticString.pinumber)) {
				SPUtils.putString(MyContexts.IS_LAST_PI_NUMBER,
						StaticString.pinumber);
				mStep = STEP_SELECT_TYPE;
			} else {
				StaticString.bagtype = SPUtils.getString(
						MyContexts.IS_LAST_TYPE_NUMBER, "11");
			}
			break;
		case TYPE_OP.IN_STORE_HAND:
			mTitle.setText(TEXT_IN_STORE);
			mNetCode = SendTask.CODE_IN_STORE_UPLOAD_BAG_INFO;
			// 手持入库, 从 服务端 获取任务信息
			setCommunicationCode(SendTask.CODE_LOT_INSTORE_NUM);
			SocketConnet.getInstance().communication(
					SendTask.CODE_LOT_INSTORE_NUM);
			break;
		case TYPE_OP.OUT_STORE_HAND:
			mTitle.setText(TEXT_OUT_STORE);
			mNetCode = SendTask.CODE_OUT_STORE_UPLOAD_BAG_INFO;
			break;
		case TYPE_OP.OPEN_BAG:
			mTitle.setText(TEXT_OPEN);
			mNetCode = 799;
			break;
		}

		mVReadBagLock = (CoverBagItemView) findViewById(R.id.v_show1);
		mVNetCheck = (CoverBagItemView) findViewById(R.id.v_show2);
		mVWriteNFC = (CoverBagItemView) findViewById(R.id.v_show3);

		// // 完成总数量 个人扫描总数量
		mTvPlanAmount = (TextView) findViewById(R.id.plan_amount);
		mTvTotalAmount = (TextView) findViewById(R.id.finish_amount);
		mTvScanAmount = (TextView) findViewById(R.id.person_scan_amount);
		// 结束批
		mTvLock = (TextView) findViewById(R.id.over);
		mTvLock.setOnClickListener(this);
		// 确认按钮
		mBtnConfirm = (Button) findViewById(R.id.btn_ok);
		mBtnConfirm.setOnClickListener(this);
		// 取消按钮
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(this);
		// mBtnCancel.setOnLongClickListener(new OnLongClickListener() {
		// @Override
		// public boolean onLongClick(View v) {
		// mStep = STEP_READ_BAG_LOCK;
		// haveTaskRunning = false;
		// initUi();
		// return true;// 事件拦截
		// }
		// });

		ViewUtil.requestFocus(mBtnConfirm);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 控制 oled屏 显示
		if (OLEDOperation.enable) {
			OLEDOperation.getInstance().open();
			OLEDOperation.getInstance().showTextOnOled("请将手持", "靠近袋锁");
		}
	}

	/**
	 * 锁定批
	 */
	public void buildLockPi() {
		mDiaUtil.showDialog2Button(MyContexts.DIALOG_MESSAGE_LOCK_BUNCH, "确定",
				"取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setCommunicationCode(SendTask.CODE_LOCK_PI);
						SocketConnet.getInstance().communication(
								SendTask.CODE_LOCK_PI);//
					}
				}, null);
		// mHandler.sendEmptyMessage(SHOW_LOCK_RESULT);

		// AlertDialog.Builder builder = new Builder(this);
		// builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
		// MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
		// builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		// {
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// setCommunicationCode(SendTask.CODE_LOCK_PI);
		// SocketConnet.getInstance().communication(SendTask.CODE_LOCK_PI);//
		// 锁定批
		// }
		// });
		// builder.setNegativeButton("取消", null);
		// create = builder.create();
		// create.show();
		// LogsUtil.d(TAG, "buildLockPi: " + create);
	}

	// 22943587 82284256
	// 重置back键功能
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_4:
			ViewUtil.requestFocus(mBtnConfirm);
			return true;
		case KeyEvent.KEYCODE_6:
			ViewUtil.requestFocus(mBtnCancel);
			return true;
		case KeyEvent.KEYCODE_HOME:
			onBackPressed();
			return true;
		case KeyEvent.KEYCODE_DEL:
			if (!haveTaskRunning) {
				choiceBiaoqianType();
			}
			return true;
		case KeyEvent.KEYCODE_SHIFT_LEFT: // 0918
			if (!haveTaskRunning) {
				Intent intent = new Intent(CoverNfcNewBagActivity.this,
						SettingPowerActivity.class);
				intent.putExtra("setOne", true);
				startActivity(intent);
			}
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {

		// mTimer.cancel();
		// mProcessView.stopLoad();// 停止动画扩散
		SystemClock.sleep(150);

		if (OLEDOperation.enable) {
			OLEDOperation.getInstance().close();
		}

		if (null != mUHFOp) {
			mUHFOp.close();
			mUHFOp = null;
		}
		super.onDestroy();
	}

	private void choiceBiaoqianType() {
		// 需弹出选择劵别，清分选择框
		// 11 完整已清分
		// 12 完整未清分
		// 23残损已复点
		// 24 残损未复点
		mBtnConfirm.setFocusable(false);

		final RadioButton wz;
		final RadioButton cs;
		final RadioButton radio3;
		final RadioButton radio4;
		final RadioButton radio5;
		final RadioButton radio6;
		final Button okDialog;
		final AlertDialog.Builder builder = new Builder(
				CoverNfcNewBagActivity.this);
		str1 = str2 = str3 = "";
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.cover_dialog_type, null);
		builder.setView(view);
		builder.setIcon(getResources().getDrawable(R.drawable.login_logo));
		builder.setTitle("选择改批袋类型");

		wz = (RadioButton) view.findViewById(R.id.radioButton1);
		cs = (RadioButton) view.findViewById(R.id.radioButton2);
		radio3 = (RadioButton) view.findViewById(R.id.radioButton3);
		radio4 = (RadioButton) view.findViewById(R.id.radioButton4);
		radio5 = (RadioButton) view.findViewById(R.id.radioButton5);
		radio6 = (RadioButton) view.findViewById(R.id.radioButton6);

		okDialog = (Button) view.findViewById(R.id.dialog_confirm);
		ViewUtil.requestFocus(okDialog);

		final AlertDialog dialog = builder.create();
		okDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (wz.isChecked())
					str1 = "1";
				if (cs.isChecked())
					str1 = "2";
				if (radio3.isChecked()) { // 已清分
					str2 = "1";
				}
				if (radio4.isChecked()) { // 未清分
					str2 = "2";
				}
				if (radio5.isChecked()) { // 已复点
					str3 = "3";
				}
				if (radio6.isChecked()) {
					str3 = "4";
				}

				if ("".equals(str1)) {
					ToastUtil.showToastInCenter("请选择完整残损信息");
				} else {
					if ("".equals(str3) && "".equals(str2)) {
						ToastUtil.showToastInCenter("请选择清分复点信息");
					} else {
						mStr = str1 + str2 + str3;
						dialog.dismiss();
					}
				}

				if (!"".equals(str1)) {
					StaticString.bagtype = mStr;
					SPUtils.putString(MyContexts.IS_LAST_TYPE_NUMBER,
							StaticString.bagtype);
				} else {
					StaticString.bagtype = SPUtils.getString(
							getApplicationContext(),
							MyContexts.IS_LAST_TYPE_NUMBER, "11");
				}
				LogsUtil.d(TAG, "StaticString.bagtype=" + StaticString.bagtype);

				okDialog.setFocusable(false);
				ViewUtil.requestFocus(mBtnConfirm);
				mStep = STEP_CHECK_BAG;
				mBtnConfirm.performClick();// 直接开始扫描
			}

		});
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		dialog.show();

	}

	/**
	 * 界面过程显示恢复到初始状态
	 */
	private void initUi() {
		mStep = STEP_CHECK_BAG;

		mVReadBagLock.setChecked(false);
		mVNetCheck.setChecked(false);
		mVWriteNFC.setChecked(false);

		mBtnConfirm.setText("扫描");
	}

	/**
	 * 解析 封袋回复
	 * 
	 * @return
	 */
	private boolean pasremPiNumber(String recInfo) {
		boolean retVal = false;
		if (null == recInfo) {// 82484256
			return retVal;
		}
		String[] splits = recInfo.split(" ");
		if (splits.length < 2 || splits[1].length() < 14) {
			return retVal;
		}

		try {
			// *22 22000100019999
			// 055310040491C9321E4480F620292D6E000B160726165835 160726165835
			// 2016072608710100100014 004#

			// mPersonFinish = StaticString.information.substring(6, 10);//
			// 个人完成数量
			// mTotalFinish = StaticString.information.substring(10, 14);//
			// 总完成数量
			// mPlanNumber = StaticString.information.substring(14, 18);// 计划数量

			mPersonFinish = splits[1].substring(2, 6);// 个人完成数量
			mTotalFinish = splits[1].substring(6, 10);// 总完成数量
			mPlanNumber = splits[1].substring(10, 14);// 计划数量

			if ("9999".equals(mPlanNumber)) {
				mPlanNumber = "-";
			}

			LogsUtil.d(TAG, "个人完成: " + mPersonFinish);
			LogsUtil.d(TAG, "总完成数: " + mTotalFinish);
			LogsUtil.d(TAG, "计划数量: " + mPlanNumber);
			// LogsUtil.d(TAG, "封签事件: " + splits[2]);
			// LogsUtil.d(TAG, "交接信息: " + splits[3]);
			// LogsUtil.d(TAG, "流水号: " + splits[4]);

			if (mTypeOp == TYPE_OP.COVER_BAG) {
				mHandoverData = ArrayUtils.hexString2Bytes(splits[3]);
				mSerialNumber = splits[4];
				/* 在封签事件中嵌入 清分信息 */
				// 1. 截取袋型号 01~10
				int bagType = Integer.parseInt(splits[2].substring(6, 8));
				// 2. 截取末尾2位 清分/复点信息, 1:完整, 2:残损; 1清分,2未清分,3复点,4未复点
				String chs = splits[2].substring(splits[2].length() - 2,
						splits[2].length());
				int clearType = Integer.parseInt(chs);
				if (20 <= clearType) {
					clearType -= 20;
				}
				// 3. 清分信息 与 袋型号 合并
				mCoverEventData = ArrayUtils.hexString2Bytes(splits[2]
						.substring(0, splits[2].length() - 2)); // 去掉末尾2个字符
				mCoverEventData[3] = (byte) (bagType | (clearType << 4));// 袋型号低4位，清分信息高4位
			} else {
				mHandoverData = ArrayUtils.hexString2Bytes(splits[2]);
				mSerialNumber = splits[3];
			}
			retVal = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * 
	 * @成功恢复 *22 300105534101045D5B0AE2548028 004#
	 * @return booelan : ture, 数据正确，下一步;false,数据异常
	 */
	private boolean pasreStartUseInfo() {
		String str = StaticString.information.substring(6, 8);
		if (!"02".equals(StaticString.information.substring(1, 3))
				&& !"00".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 返回
	 */
	@Override
	public void onBackPressed() {
		if (!haveTaskRunning) {
			if (STEP_CHECK_BAG == mStep || STEP_COVER_OVER == mStep) {
				if (ClickUtil.isFastDoubleClick(2500)) {
					super.onBackPressed();
				} else {
					ToastUtil.showToastInCenter("再次点击退出");
				}
			} else {
				initUi();
			}
			// 读袋锁阶段，初始化信息，启用码
		} else if (mStep == STEP_CHECK_BAG) {
			mCheckBagTask.stop();
			mVReadBagLock.setDoing(false);
			mBtnConfirm.setEnabled(true);
			// haveTaskRunning = false;
		} else if (mStep == STEP_NET_CHECK) { // 将条码等数据发送到服务器验证，是否已经封签过
			// 网络通信中，不让中断
		} else if (mStep == STEP_UPDATE_BAG) { // 更新袋锁，索引，条码，封签信息，标志位
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_ok:// 扫描操作按键
			// mBtnConfirm.setText("扫描");
			LogsUtil.d(TAG, "onClick() step:" + mStep);
			// 第一次按确定键, 读袋锁
			switch (mStep) {
			case STEP_SELECT_TYPE:// 选择 清分/复点
				choiceBiaoqianType();
				break;
			case STEP_CHECK_BAG: // 第1步， 读袋锁、检查
			case STEP_NET_CHECK: // 第2步 上传袋id 失败， 回到第1步重新 读袋锁、检查
			case STEP_UPDATE_BAG: // 第3步 写NFC 失败， 回到第1步重新 读袋锁、检查
				initUi();
				mHandler.sendEmptyMessage(MSG_CHECK_BAG_START);
				break;
			default:
				break;
			}
			break;
		case R.id.btn_cancel:// 取消按键
			onBackPressed();
			break;
		case R.id.over:// 锁定批
			if (!haveTaskRunning) {
				buildLockPi();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 处理数据
	 */
	private class MyHandlerCallBack implements Handler.Callback {
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_INSTORE_INFO_OVER: // 入库 获取列表 成功
				mDiaUtil.dismissProgressDialog();

				if (null == mBagIdList || mBagIdList.size() == 0) {
					mDiaUtil.showDialogFinishActivity("获取入库列表失败，请确定有入库任务");
					return true;
				}

				mTvScanAmount.setText(mPersonFinish.replaceFirst("^0+", ""));// 去掉
				// 数字前面的
				// '0'
				mTvTotalAmount.setText(mTotalFinish.replaceFirst("^0+", ""));
				mTvPlanAmount.setText(mPlanNumber.replaceFirst("^0+", ""));

				if ("".equals(mTvTotalAmount.getText().toString())) {
					mTvTotalAmount.setText("0");
				}
				if ("".equals(mTvPlanAmount.getText().toString())) {
					mTvPlanAmount.setText("0");
				}
				break;
			case MSG_CHECK_BAG_START: // 1. 检查袋锁
				mVReadBagLock.setDoing(true);
				mBtnConfirm.setEnabled(false);
				haveTaskRunning = true;
				ThreadPoolFactory.getNormalPool().execute(mCheckBagTask);
				break;
			case MSG_CHECK_BAG_FAILED:
				haveTaskRunning = false;
				if (msg.obj != null) {
					ToastUtil.showToastInCenter(msg.obj);
				}
				SoundUtils.playFailedSound();
				mCheckBagTask.stop();
				mVReadBagLock.setDoing(false);
				mBtnConfirm.setEnabled(true);
				RFIDOperation.getInstance().closeRF();
				break;
			case MSG_CHECK_BAG_SUCCESS:
				mVReadBagLock.setChecked(true);
			case MSG_NET_CHECK_START: // 2. 网络数据检验
				mVNetCheck.setDoing(true);
				mBtnConfirm.setEnabled(false);
				mBtnCancel.setEnabled(false);
				mStep = STEP_NET_CHECK;
				haveTaskRunning = true;
				setCommunicationCode(mNetCode);
				if (!SocketConnet.getInstance().communication(mNetCode)) {
					Message s = mHandler.obtainMessage();
					msg.what = MSG_COVER_NET_FAILED;
					msg.obj = "数据发送失败";
					mHandler.sendMessage(s);
				}
				// ThreadPoolFactory.getNormalPool().execute(mNetCoverTask);
				break;
			case MSG_COVER_NET_FAILED:
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				mVNetCheck.setDoing(false);
				mBtnConfirm.setEnabled(true);
				mBtnCancel.setEnabled(true);
				mBtnConfirm.setText("重新扫描");
				if (msg.obj == null) {
					mDiaUtil.showReplyDialog();
				} else {
					mDiaUtil.showDialog(msg.obj);
				}
				RFIDOperation.getInstance().closeRF();
				break;
			case MSG_COVER_NET_TIMEOUT:
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				mVNetCheck.setDoing(false);
				mBtnConfirm.setEnabled(true);
				mBtnConfirm.setText("重新上传");
				mDiaUtil.showDialog2Button("回复超时", "重发", "取消",
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mHandler.sendEmptyMessage(MSG_NET_CHECK_START);
							}
						}, null);
				RFIDOperation.getInstance().closeRF();
				break;
			case MSG_COVER_NET_SUCCESS:
				mVNetCheck.setChecked(true);
				mVWriteNFC.setDoing(true);
				mBtnConfirm.setEnabled(false);
				mBtnCancel.setEnabled(false);
				mStep = STEP_UPDATE_BAG;
				haveTaskRunning = true;
				ThreadPoolFactory.getNormalPool().execute(mWriteNFCTask);
				break;
			case MSG_WRITE_NFC_FAILED:
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				SocketConnet.getInstance().communication(false, 888,
						new String[] { mSerialNumber });
				mVWriteNFC.setDoing(false);
				mBtnConfirm.setEnabled(true);
				mBtnCancel.setEnabled(true);
				mBtnConfirm.setText("重新扫描");
				RFIDOperation.getInstance().closeRF();
				break;
			case MSG_WRITE_NFC_SUCCESS:
				haveTaskRunning = false;
				LogsUtil.d(TAG, "本袋用时:"
						+ (System.currentTimeMillis() - startTime) + "ms");
				ToastUtil.showToastInCenter("本袋用时:"
						+ (System.currentTimeMillis() - startTime) + "ms");
				mVWriteNFC.setChecked(true);
				mBtnConfirm.setEnabled(true);
				mBtnCancel.setEnabled(true);
				SoundUtils.playNumber(mPersonFinish);
				mTvScanAmount.setText(mPersonFinish.replaceFirst("^0+", ""));// 去掉
				// 数字前面的
				// '0'
				mTvTotalAmount.setText(mTotalFinish.replaceFirst("^0+", ""));
				mTvPlanAmount.setText(mPlanNumber.replaceFirst("^0+", ""));

				if ("".equals(mTvTotalAmount.getText().toString())) {
					mTvTotalAmount.setText("0");
				}
				if ("".equals(mTvPlanAmount.getText().toString())) {
					mTvPlanAmount.setText("0");
				}

				if (!mTvTotalAmount.toString().equals(mPlanNumber.toString())) {
					mBtnConfirm.setText("继续");
					mStep = STEP_CHECK_BAG;// 重新开始
				} else {
					mBtnConfirm.setText("封袋完成");
					mStep = STEP_COVER_OVER;
					mBtnCancel.setText("退出");

					// 小屏提示锁定批
					if (OLEDOperation.enable) {
						OLEDOperation.getInstance().open();
						OLEDOperation.getInstance().showTextOnOled("请锁定该批");
					}
				}

				// 关闭屏幕
				if (isFirstScan) {
					if (OLEDOperation.enable) {
						OLEDOperation.getInstance().close();
					}
					isFirstScan = false;
				}
				RFIDOperation.getInstance().closeRF();
				break;
			case SHOW_LOCK_RESULT:// 111

				final String info = ReplyParser
						.parseReply(StaticString.information);
				if ("锁定批成功".equals(info)) {
					Drawable nav_up = getResources().getDrawable(
							R.drawable.lock);
					nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
							nav_up.getMinimumHeight());
					mTvLock.setCompoundDrawables(null, nav_up, null, null);
					mTvLock.setText("已锁定");

					mDiaUtil.showDialogFinishActivity(info);
				} else {
					// mDiaUtil.showDialogFinishActivity(info);
					mDiaUtil.showDialog(info);
				}

				break;
			case SHOW_LOCK_FAILED:
				mDiaUtil.showReplyDialog();
				break;
			}
			return true;
		}

	};

	/**
	 * 第一步，读袋锁NFC、读锁片EPC，检验袋锁状态
	 * 
	 * @ClassName: readLockBag
	 * @Description: 读基金袋,验证袋版本是否 初始化(读取起始为0x04的位置) 以及是否已经启用。
	 */
	private class CheckBagTask implements Runnable {
		private final long INTIME = 500;
		private final long TIME_GAP = 5000;
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		/**
		 * 在给定时间内读取 超高频epc，读到非85开头的epc返回 true。
		 * 
		 * @param time
		 *            给定时间
		 * @return
		 */
		private boolean readEPCInTime(long time) {
			boolean readEPCSuccess = false;
			/*  */
			long startTime = System.currentTimeMillis();

			int t = 0;
			int t85 = 0;
			while ((System.currentTimeMillis() - startTime) < time) {
				if (mUHFOp.fastReadTID(0x06, mPieceTid)) {
					LogsUtil.d(TAG, "TID:" + ArrayUtils.bytes2HexString(mPieceTid));
//				if (mUHFOp.readUHFInTime(UHFOperation.MB_TID, 0x03, mPieceTid,
//						time)) {
					if (mUHFOp.readUHFInTime(UHFOperation.MB_EPC, 0x02,
							UHFOperation.sEPC, 0, mPieceTid,
							UHFOperation.MB_TID, 0x03)) {
						// 85开头的EPC是标牌；不允许使用05开头的锁片
						if ((UHFOperation.sEPC[0] & 0xFF) != 0x85
								&& (LogsUtil.getDebugLevel() == LogsUtil.LEVEL_TEST
										|| (mTypeOp != TYPE_OP.COVER_BAG) || (UHFOperation.sEPC[0] & 0xFF) != 0x05)) {
							readEPCSuccess = true;
							break;
						}
						t85++;
					}
				}// end read tid
				
				// if (mUHFOp.fastReadEpc(UHFOperation.sEPC)) {
				// t++;
				// LogsUtil.d(TAG,
				// ArrayUtils.bytes2HexString(UHFOperation.sEPC));
				// if ((UHFOperation.sEPC[0] & 0xFF) != 0x85
				// && (LogsUtil.getDebugLevel() == LogsUtil.LEVEL_TEST
				// || (mTypeOp != TYPE_OP.COVER_BAG) || (UHFOperation.sEPC[0] &
				// 0xFF) != 0x05)) { // 85开头的EPC是标牌
				// readEPCSuccess = true;
				// break;
				// }
				// t85++;
				// }
				// SystemClock.sleep(50);
			}
			LogsUtil.d(TAG, "total:" + t + " 85:" + t85);
			return readEPCSuccess;
		}

		public void run() {
			haveTaskRunning = true;
			startTime = System.currentTimeMillis();
			stoped = false;

			Message msg = mHandler.obtainMessage();
			msg.what = MSG_CHECK_BAG_FAILED;
			msg.obj = "读袋锁失败";
			// long startTime = System.currentTimeMillis();
			// long endTime = 0;
			while (!stoped) {
				if (TIME_GAP < System.currentTimeMillis() - startTime) {
					mHandler.sendMessage(msg);
					stoped = true;
					haveTaskRunning = false;
					return;
				}

				/* 1，寻卡NFC后，500ms内 读EPC */
				mUid = newRFIDOp.findCard();
				if (null != mUid && mUid.length == 7) {
					if (!readEPCInTime(INTIME)) {
						// 读锁片EPC失败
						msg.obj = "读锁片失败";
						// SystemClock.sleep(50);
						continue;
					}
				} else {
					// 读NFC失败
					continue;
				}
				LogsUtil.d(TAG, "check1 uid:");

				if (!newRFIDOp.readNFCInTime(0x04, mBagIdBuf, 1000, mUid)) {
					// 获取 袋id失败

					msg.obj = "获取袋ID失败";
					mHandler.sendMessage(msg);
					return;
				} else if ((mBagIdBuf[0] & 0xFF) == 0x04
						|| (mBagIdBuf[0] & 0xFF) == 0x05) {
					StaticString.bagid = ArrayUtils.bytes2HexString(mBagIdBuf);
				} else {
					// 未初始化
					stoped = true;
					msg.obj = "该基金袋尚未初始化，请更换基金袋";
					mHandler.sendMessage(msg);
					return;
				}
				LogsUtil.d(TAG, "check2 bagId:" + StaticString.bagid);

				/* 3, 检查 启用码0x11， 并检查 */
				byte[] flagSrc = new byte[20];// 20个byte=5个地址
				if (!newRFIDOp.readNFCInTime(0x10, flagSrc, 1000, mUid)) {
					// 读取 0x10 5个地址内容, 失败
					msg.obj = "获取标志位失败";
					mHandler.sendMessage(msg);
					return;
				}
				// 解密 启用码
				mEnableCode = Arrays.copyOfRange(flagSrc, 4, 8);
				mEnableCode = ArrayUtils.deciphering(mEnableCode, mUid);
				LogsUtil.d(
						TAG,
						"enable Code:"
								+ ArrayUtils.bytes2HexString(mEnableCode));
				if (!Arrays.equals(mEnableCode, Lock3Util.ENABLE_CODE_ENABLE)) {
					// 基金袋 未启用， 向服务端 查询 是否 可启用
					setCommunicationCode(882);
					SocketConnet.getInstance().communication(882);
					if (ReplyParser.waitReply()) {
						if (pasreStartUseInfo()) {
							// 启用基金袋失败
							// mEnableCode = Arrays.copyOf(
							// Lock3Util.ENABLE_CODE_ENABLE,
							// Lock3Util.ENABLE_CODE_ENABLE.length);
							mEnableCode = ArrayUtils.encryption(
									Lock3Util.ENABLE_CODE_ENABLE, mUid);
							if (!newRFIDOp.writeNFCInTime(0x11, mEnableCode,
									1000, mUid)) {
								msg.obj = "启用基金袋失败，请重新封袋";
								mHandler.sendMessage(msg);
								return;
							}
						} else {
							// 基金袋 不允许 使用
							msg.obj = "该基金袋不允许使用，请更换基金袋或联系公司客服";
							mHandler.sendMessage(msg);
							return;
						}
					} else {
						// 基金袋 启用 超时
						msg.obj = "启用基金袋超时，请重新封袋";
						mHandler.sendMessage(msg);
						return;
					}
				}
				LogsUtil.d(TAG, "check3 enable");

				mHandoverIndex = flagSrc[1]; //
				miyueNum = flagSrc[16] & 0x0F;
				int plainFlag = Lock3Util.getFlag(flagSrc[0], miyueNum, mUid,
						true);
				if (-3 == plainFlag) {
					LogsUtil.d(TAG, "src miyueNum wrong:" + miyueNum);
					for (miyueNum = 0; miyueNum < 10; miyueNum++) {
						plainFlag = Lock3Util.getFlag(flagSrc[0], miyueNum,
								mUid, true);
						if (0 < plainFlag) {
							// 得到正确的密钥编号， 进行改写
							LogsUtil.d(TAG, "更正 密钥编号为：" + miyueNum);
							if (!newRFIDOp.writeNFCInTime(0x14,
									new byte[] { (byte) (0xA0 | miyueNum) },
									1000, mUid)) {
								msg.obj = "该袋标志位异常，且修正失败，请重新封袋";
								mHandler.sendMessage(msg);
								return;
							}
							break;
						} else {
							LogsUtil.d(TAG, "miyueNum wrong:" + miyueNum);
						}
					}
				}
				LogsUtil.d(TAG, "check4 flag: F" + plainFlag + " miyueNum:"
						+ miyueNum);

				if (mTypeOp != TYPE_OP.COVER_BAG) {
					if (3 == plainFlag) {
						plainFlag = 2; // 已正常封袋
					} else {
						plainFlag = -1; // 未正常封袋
					}
				}
				switch (plainFlag) {
				case -1: // 出入库、开袋 标志位错误
					msg.obj = "该袋标志位异常，可能未正常封袋";
					mHandler.sendMessage(msg);
					return;
				case 1: // F1
					int bagVersion = UHFOperation.sEPC[0] & 0xFF;// 锁片中epc一般不会以04、05开头
					LogsUtil.d(TAG, "标志位为F1，bagVersion： " + bagVersion);
					if (4 == bagVersion || 5 == bagVersion) {
						if (LogsUtil.getDebugLevel() == LogsUtil.LEVEL_TEST) {
							ToastUtil.showToastOnUiThreadInCenter("测试 F1",
									CoverNfcNewBagActivity.this);
						} else {
							msg.obj = "锁片未插好";
							mHandler.sendMessage(msg);
							// mHandler.sendEmptyMessage(BAG_NO_LOCK);
							return;
						}
					}
					break;
				case 2: // F2 正常情况
					break;
				case 3: // F3
					if (LogsUtil.getDebugLevel() == LogsUtil.LEVEL_TEST) {
						ToastUtil.showToastOnUiThreadInCenter("测试 F3",
								CoverNfcNewBagActivity.this);
						break;
					} else {
						msg.obj = "该袋可能已被封签";
						mHandler.sendMessage(msg);
						// mHandler.sendEmptyMessage(BAG_HAD_HAD_COVER_BAG);
						return;
					}
				case 4: // F4
					int bagVersion2 = UHFOperation.sEPC[0] & 0xFF;// 锁片中epc一般不会以04、05开头
					LogsUtil.d(TAG, "标志位为F4，bagVersion： " + bagVersion2);
					if (4 == bagVersion2 || 5 == bagVersion2) {
						if (LogsUtil.getDebugLevel() == LogsUtil.LEVEL_TEST) {
							ToastUtil.showToastOnUiThreadInCenter("测试 F4",
									CoverNfcNewBagActivity.this);
						} else {
							msg.obj = "该袋处于开袋状态";
							mHandler.sendMessage(msg);
							// mHandler.sendEmptyMessage(BAG_HAD_ERROR_OPEN_BAG);
							return;
						}
					}
					break;
				case 5:
				default:
					miyueNum = -1;

					msg.obj = "该袋标志位异常，请勿继续使用";
					mHandler.sendMessage(msg);
					return;
				}

				/** 读电压值 */
				byte[] vBuf = new byte[12]; // 0x17 ~ 0x19
				if (!newRFIDOp.readNFCInTime(0x17, vBuf, 1000, mUid)) {
					msg.obj = "获取电压值失败";
					mHandler.sendMessage(msg);
					return;
				}
				double v1 = 2.5 * (vBuf[0 + 3] & 0xFF) / 128; // 0x17.3
				double v2 = 2.5 * (vBuf[8 + 1] & 0xFF) / 128; // 0x19.1
				v1 = Math.round(v1 * 100) / 100.0; //	 保存小数点后 2位，四舍五入
				v2 = Math.round(v2 * 100) / 100.0;
				int witch = vBuf[8 + 0] & 0xFF; // 0x19.0
				LogsUtil.d(TAG, "当前使用：电池" + witch);
				LogsUtil.d(TAG, "电压记录:v1=" + v1);
				LogsUtil.d(TAG, "电压记录:v2=" + v2);
				if ((v1 < Lock3Util.LOWEST_V) && (v2 < Lock3Util.LOWEST_V)) {
					if (mTypeOp == TYPE_OP.COVER_BAG) {
						if (LogsUtil.getDebugLevel() == LogsUtil.LEVEL_TEST) {
							ToastUtil.showToastOnUiThreadInCenter("测试 :电压不足 "
									+ v1 + " , " + v2, CoverNfcNewBagActivity.this);
						} else {
							msg.obj = "袋锁电压不足，请更换基金袋";
							mHandler.sendMessage(msg);
							return;
						}
					} else {
						ToastUtil.showToastOnUiThreadInCenter("袋锁电压不足",
								CoverNfcNewBagActivity.this);
					}
				}

				if (mTypeOp == TYPE_OP.COVER_BAG) {
					/* 封袋，读取并写入 TID */
					// if (!mUHFOp.readUHFInTime(UHFOperation.MB_TID, 0x03,
					// mTid,
					// 1000, UHFOperation.sEPC, UHFOperation.MB_EPC, 0x02)) {
					// msg.obj = "获取TID失败，请检查锁片是否插好";
					// mHandler.sendMessage(msg);
					// return;
					// }
					if (!newRFIDOp.writeNFCInTime(0x07, mPieceTid, 1000, mUid)) {
						msg.obj = "写入TID失败";
						mHandler.sendMessage(msg);
						return;
					}
					StaticString.tid = ArrayUtils.bytes2HexString(mPieceTid);
					LogsUtil.d(TAG, "check6 TID:" + StaticString.tid);
				} else {
					/** 读取袋中存放的TID信息 */
					if (!RFIDOperation.getInstance().readNFCInTime(0x07,
							mPieceTid, 500, null)
							|| !mUHFOp.readUHFInTime(UHFOperation.MB_TID, 0x03,
									mPieceTid, 500, mPieceTid,
									UHFOperation.MB_TID, 0x03)) {
						msg.obj = "锁片与袋锁信息不匹配";
						mHandler.sendMessage(msg);
						return;
					}
					byte[] eventBuf = new byte[30];
					if (!RFIDOperation.getInstance().readNFCInTime(0x30,
							eventBuf, 1000, null)) {
						msg.obj = "获取事件码失败";
						mHandler.sendMessage(msg);
						return;
					}
					LogsUtil.d(TAG,
							"解密前：" + ArrayUtils.bytes2HexString(eventBuf));
					boolean encrypt = AESCoder.myEncrypt(eventBuf, mPieceTid,
							false);
					LogsUtil.d(TAG, "解密EventCode成功？" + encrypt);

					// 封袋的时候在封签事件码嵌入清分信息。 去掉清分信息，还原封签事件码
					eventBuf[3] = (byte) (eventBuf[3] & 0x0F);
					StaticString.eventCode = ArrayUtils
							.bytes2HexString(eventBuf);

					LogsUtil.d(TAG, "解密后：" + StaticString.eventCode);

					StaticString.tid = ArrayUtils.bytes2HexString(mPieceTid);
				}
				mHandler.sendEmptyMessage(MSG_CHECK_BAG_SUCCESS);
				stoped = true;
				return;

			}// end while
			haveTaskRunning = false;
		}// end run()

	}

	/** 保存袋id。1、入库时，保存任务列表；2、出库时，保存已出库袋id */
	private Collection<String> mBagIdList;
	private int commCode;

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

	private class CoverBagRecieveListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			// TODO Auto-generated method stub

			if (commCodeEquals(-1)) {
				return;
			}

			String[] split = recString.split(" ");
			if (commCodeEquals(SendTask.CODE_LOT_INSTORE_NUM)
					&& "*37".equals(split[0])
					&& "01".equalsIgnoreCase(split[1])) {// 获取 批次 信息
				setCommunicationCode(-1);
				if (split[2] == null || !split[2].matches("^[1-9]+")) {
					mHandler.sendEmptyMessage(MSG_GET_INSTORE_INFO_OVER);
					return;
				}
				// 获取已扫数量
				mPersonFinish = split[3];
				mTotalFinish = split[3];
				// 获取 总数
				mPlanNumber = split[2];
				// 获取待扫描袋id 列表
				setCommunicationCode(SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST);
				SocketConnet.getInstance().communication(
						SendTask.CODE_LOT_INSTORE_GET_BAGID_LIST);
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
			} else if (commCodeEquals(882)) {
				setCommunicationCode(-1);
				// do nothing, 启用码
			} else if (commCodeEquals(SendTask.CODE_LOCK_PI)) {
				setCommunicationCode(-1);
				mHandler.sendEmptyMessage(SHOW_LOCK_RESULT);
			} else if (pasremPiNumber(StaticString.information)) {
				setCommunicationCode(-1);
				mHandler.sendEmptyMessage(MSG_COVER_NET_SUCCESS);
			} else {
				setCommunicationCode(-1);
				mHandler.sendEmptyMessage(MSG_COVER_NET_FAILED);
			}

		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_COVER_NET_TIMEOUT);
		}
	}

	/**
	 * 第5步，写标志位和封签事件码信息 对于封签事件码，写入NFC卡中时，用uid进行加密处理 写入超高频时，用tid进行加密
	 * 
	 * @author WriteRFIDTask
	 * 
	 */
	private class WriteRFIDTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			LogsUtil.w(TAG, WriteRFIDTask.class.getSimpleName() + " run");
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_WRITE_NFC_FAILED;
			msg.obj = "读袋锁失败";
			stoped = false;
			int count = 0;
			final int TIMES = 3;// 写条码信息的最大次数，超时算写入失败
			count = 0;
			while (!stoped) {
				if (TIMES < ++count) {
					stoped = true;
					mHandler.sendMessage(msg);
					haveTaskRunning = false;
					return;
				}

				/** 寻卡操作 */
				byte[] uid2 = null;
				// uid2 = mBagOp.getUid();
				uid2 = newRFIDOp.findCard();
				if (null == uid2) {
					continue;
				}
				if (!Arrays.equals(mUid, uid2)) {
					// 与第1次的读到的高频卡 不一致
					LogsUtil.d(TAG, "与第1次的读到的高频卡 不一致");
					continue;
				}

				if (mTypeOp == TYPE_OP.COVER_BAG) {
					/* 加密 封签事件 */
					boolean encryptEventCode = AESCoder.myEncrypt(
							mCoverEventData, mPieceTid, true);
					LogsUtil.d(TAG, "加密事件码成功？ " + encryptEventCode);
					LogsUtil.d(
							TAG,
							"加密后："
									+ ArrayUtils
											.bytes2HexString(mCoverEventData));
					/* 2, 写封签事件码到高频卡中 */
					if (!newRFIDOp.writeNFCInTime(0x30, mCoverEventData, 1500,
							mUid)) {
						msg.obj = "写入封签事件码失败";
						mHandler.sendMessage(msg);
						return;
					}

					/* 3, 写 流水号 到高频卡中 */
					if (!newRFIDOp.writeNFCInTime(0x90,
							ArrayUtils.hexString2Bytes(mSerialNumber), 1000,
							mUid)) {
						msg.obj = "写入 流水号 失败";
						mHandler.sendMessage(msg);
						return;
					}

					/* 向锁片USE写入 封签事件码 */
					int tcount = 0;
					while (tcount++ < 2) {
						if (mUHFOp.writeUHFInTime(mCoverEventData,
								UHFOperation.MB_USE, 0x00, 0, mPieceTid,
								UHFOperation.MB_TID, 0x03)) {
							LogsUtil.d(TAG, "封签事件码写入use 成功 -- " + tcount);
							tcount = Integer.MAX_VALUE;
						} else {
							LogsUtil.d(TAG, "封签事件码写入use failed -- " + tcount);
						}
					}
				}// end if coverBag

				/* 4、写入交接信息 */
				// 加密 交接信息
				boolean encryptHandover = AESCoder.myEncrypt(mHandoverData,
						mPieceTid, true);
				LogsUtil.d(TAG, "加密交接信息成功？ " + encryptHandover);
				LogsUtil.d(TAG,
						"加密后：" + ArrayUtils.bytes2HexString(mHandoverData));

				int sa = mHandoverIndex & 0xFF;// 获得 相对位置
				LogsUtil.d(TAG, "交接信息索引：" + sa);
				sa = 3 * sa + 0x40; // 计算绝对位置
				// 写入 交接信息
				if (!newRFIDOp.writeNFCInTime(sa, mHandoverData, 1000, mUid)) {
					msg.obj = "写入 交接信息 失败";
					mHandler.sendMessage(msg);
					return;
				}

				if (mTypeOp == TYPE_OP.COVER_BAG) {
					/* 向锁片EPC区写入 袋ID */
					if (!mUHFOp.writeUHFInTime(mBagIdBuf, UHFOperation.MB_EPC,
							0x02, 1000, mPieceTid, UHFOperation.MB_TID, 0x03)) {
						LogsUtil.d(TAG, "写袋id到 EPC区  失败！");
						msg.obj = "写入 袋ID 失败";
						mHandler.sendMessage(msg);
						return;
					}
				}

				/* 更新标志位 以及 交接信息 */
				int f = 3;
				if (mTypeOp == TYPE_OP.OPEN_BAG) {
					f = 4;
				}
				byte flag = (byte) Lock3Util.getFlag(f, miyueNum, mUid, false);
				mHandoverIndex++;
				byte[] bs = new byte[] { flag, mHandoverIndex };// 更新标志位 以及
																// 交接信息索引
				if (!newRFIDOp.writeNFCInTime(0x10, bs, 1000, mUid)) {
					/* 是封袋操作 且 非debug模式，修改锁片袋ID */
					if (mTypeOp == TYPE_OP.COVER_BAG
							&& LogsUtil.getDebugLevel() != LogsUtil.LEVEL_DEBUG) {
						/* 封袋失败后，将锁片epc区第1个字节05改为其他数值，否则无法再次封袋 */
						if (!mUHFOp.writeUHFInTime(new byte[] { PIECE_NOT_USE_FLAG },
								UHFOperation.MB_EPC, 0x02, 500, mPieceTid,
								UHFOperation.MB_TID, 0x03)) {
							LogsUtil.d(TAG, "恢复锁片袋ID失败");
							ToastUtil.showToastOnUiThreadInCenter("恢复锁片袋ID失败",
									CoverNfcNewBagActivity.this);
						}
					}
					msg.obj = "更新 标志位 失败";
					mHandler.sendMessage(msg);
					return;
				}
				mHandler.sendEmptyMessage(MSG_WRITE_NFC_SUCCESS);
				return;
			} // end while()
			haveTaskRunning = false;
			LogsUtil.w(TAG, WriteRFIDTask.class.getSimpleName() + " finish");
		}// end run()
	}
}