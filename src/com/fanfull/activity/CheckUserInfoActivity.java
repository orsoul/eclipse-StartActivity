package com.fanfull.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.db.Finger;
import com.fanfull.db.FingerDbService;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.FingerManager;
import com.fanfull.hardwareAction.FingerManager.FingerListener;
import com.fanfull.hardwareAction.FingerPrint;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.op.RFIDOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.ClickUtil;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;

/**
 * 
 * @ClassName: CheckUserInfoActivity
 * @Description:检测用户用户信息界面
 * @author daneil
 * @date 2015-5-19 上午10:10:38
 * 
 */
public class CheckUserInfoActivity extends BaseActivity {

	public static final int CHECK_SUCCESS_CODE = 12345;
	/**
	 * 灭屏后 再次 登陆 0；
	 */
	public static final int LOGIN_AGAIN = 0;

	protected static final int OPEN_RFID_SUCCESS = 0;
	protected static final int CONNECT_FAILED = 1;
	protected static final int READ_RFID_SUCCESS = 2;
	protected static final int READING_RFID = 3;
	protected static final int READ_RFID_FAULTED = 4;
	protected static final int LOGIN = 5;
	protected static final int PASSWORD_WRONG = 6;
	protected static final int CONNECT_SUCCESS = 8;
	protected static final int RFID_ERROR = 9;

	protected static final int FRIGER_CONNECT_FAILED = 11;
	protected static final int FRIGER_MATCH_NOT = 12;
	protected static final int FRIGER_MATCH_SUCCESS = 13;

	protected static final int LOAD_WORD_END = 16;

	protected static final int CHECK_USER_INFO = 17;// 判断是否是同一个人

	protected static final int FRIGER_LOGIN_FAILUE = 18;
	protected static final int FRIGER_LOGIN_SUCCESS = 19;
	private SocketConnet mSocketConn;
	// TODO

	private String USER_INFO = "点击扫描IC卡";
	private String[] USER_SCANNING = { "正在扫描.", "正在扫描..", "正在扫描...",
			"正在扫描....", "正在扫描.....", };
	private int pointScanning = 0;
	private int mFingerId = -1;

	private TextView mTvUser;
	private EditText mEtPsw;
	private Button mBtnLogin, mBtnExit;// 登陆，退出
	private Animation mShake;// 密码框抖动动画
	// 指纹操作
	private FingerPrint mFingerPrint;
	private Finger mFinger = null;
	private NetFingerLoginTask mNetFingerLoginTask;

	private ConnectivityManager mConnManager;
	private ProgressDialog loginDialog;

	// Handler 处理类
	private NetTask mNetTask;
	private ReadRFIDTask mReadRFIDTask;

	private boolean mFingerInfo = false;// 指纹和登录

	private final IntentFilter filter = new IntentFilter();

	/**
	 * 扫描RFID的线程正在执行
	 */
	private boolean haveTaskRunning;

	// 2016-04-20 增加指纹操作
	private FingerManager mfManager;
	private boolean isFingerSearching = false;
	/**
	 * mtye 0 为唤醒屏幕 1 为操作复核，封签，出入库等都需要两个人操作
	 */
	private int mType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置网络连接
		mSocketConn = SocketConnet.getInstance();
		mNetTask = new NetTask();
		mReadRFIDTask = new ReadRFIDTask();
		mNetFingerLoginTask = new NetFingerLoginTask();

//		Intent intent = getIntent();
//		if (intent.getIntExtra(MyContexts.KEY_CHECK_LOGIN, 0) == 0) {
//			// 如果为0，表示熄灭屏幕后，唤醒检测用户，唤醒时，前后的用户卡号需一致
//			mType = 0;
//		} else if (intent.getIntExtra(MyContexts.KEY_CHECK_LOGIN, 0) == 1) {
//			// 如果为1，表示复核人 当进行复核时，前后的用户卡号需不一致，
//			// 而且还得检测复核人是否有复核的权限
//			mType = 1;
//			mBtnExit.setText("返回");
//		}
		mType = getIntent().getIntExtra(MyContexts.KEY_CHECK_LOGIN, 1);
		if (mType != LOGIN_AGAIN) {
			mBtnExit.setText("返回");
		}

	}

	protected void initData() {

		mHandler.sendEmptyMessage(OPEN_RFID_SUCCESS);
	}

	/**
	 * 
	 * @Title: initView
	 * @Description:初始化界面
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	protected void initView() {
		// 去除title
		setTheme(R.style.Act_Transparent);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_check_user_info);

		mTvUser = (TextView) findViewById(R.id.tv_check_user);
		mTvUser.setOnClickListener(this);

		mShake = AnimationUtils.loadAnimation(this, R.anim.shake);
		mEtPsw = (EditText) findViewById(R.id.et_check_psw);
		mEtPsw.setOnClickListener(this);

		mBtnLogin = (Button) findViewById(R.id.btn_checkinfo_ok);
		mBtnLogin.setOnClickListener(this);
		mBtnLogin.setEnabled(false);

		mBtnExit = (Button) findViewById(R.id.btn_checkinfo_cancel);
		mBtnExit.setOnClickListener(this);

		mEtPsw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				/* 判断是否是“DONE”键 */
				if (actionId == EditorInfo.IME_ACTION_GO) {
					/* 隐藏软键盘 */
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
					}
					// mHandler.sendEmptyMessage(LOGIN);
					return true;
				}
				return false;
			}
		});
		mEtPsw.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (6 == s.length()) {
					mBtnLogin.setEnabled(true);
					login();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private FingerListener mFingerListener = new FingerListener() {

		@Override
		public void openFingerSerialPortSuccess(boolean flag) {

			LogsUtil.d(TAG, "打开指纹串口:" + flag);
			if (!flag) {
				mHandler.sendEmptyMessage(FRIGER_CONNECT_FAILED);
			} else {
				isFingerSearching = true;
				if (null != mFingerPrint)
					mFingerPrint.startSearchFinger();
				//
			}
		}

		@Override
		public void getLocalFingerSucess(int n) {

			mFingerPrint.stopSearchFinger();
			mFingerId = n;
			mHandler.removeMessages(FRIGER_MATCH_SUCCESS);
			mHandler.sendEmptyMessage(FRIGER_MATCH_SUCCESS);
		}

		@Override
		public void getLocalFingerError() {

		}

		@Override
		public void getLocalFingerNoData() {

			mHandler.removeMessages(FRIGER_MATCH_NOT);
			mHandler.sendEmptyMessage(FRIGER_MATCH_NOT);
		}

		@Override
		public void deleteFingerNmber(boolean flag) {

		}

		@Override
		public void emptyFinger(boolean flag) {

		}

		@Override
		public void addFingerData(int flag, String info) {

		}

		/** 该接口的目的是，防止指纹还在搜索中，就被强制关闭GPIO了，影响模块稳定性 */
		@Override
		public void stopSearchFinger(boolean flag) {

			// if(flag){
			// mFingerPrint.closeFinger();
			// mFingerPrint = null;
			// }
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case READ_RFID_SUCCESS:
				haveTaskRunning = false;
				mTvUser.setText(StaticString.userIdcheck);
				mEtPsw.setFocusableInTouchMode(true);
				mEtPsw.requestFocus();
				mEtPsw.setText("000000");
				break;
			case OPEN_RFID_SUCCESS:
				/** 先打开高频模块，注册指纹模块监听 */
				if (FingerPrint.enable) {
					mfManager = FingerManager.getInstance();
					mfManager.registListener(mFingerListener);
					mFingerPrint = FingerPrint.getInstance();
					if (!mFingerPrint.getIsinit()) {
						ThreadPoolFactory.getNormalPool().execute(
								new Runnable() {
									@Override
									public void run() {

										mFingerPrint.open();
									}
								});
					} else {
						mFingerPrint.startSearchFinger();
					}
				}
				break;
			case READ_RFID_FAULTED:
				haveTaskRunning = false;
				mTvUser.setHint(USER_INFO);
				break;

			case CHECK_USER_INFO:
				haveTaskRunning = false;
				mTvUser.setHint(USER_INFO);
				if (mType == 0) {
					ToastUtil.showToastInCenter("非上一次操作用户");
				} else {
					ToastUtil.showToastInCenter("操作用户和复核用户不能是同一个人");
				}
				break;
			case READING_RFID:
				mTvUser.setHint(USER_SCANNING[pointScanning % 5]);
				pointScanning++;
				break;
			case CONNECT_FAILED:
				mBtnLogin.setClickable(true);
				haveTaskRunning = false;
				dismissProgressDialog();
				SoundUtils.playFailedSound();
				StaticString.userIdcheck = null;
				connetFailue();
				break;
			case CONNECT_SUCCESS: /* socket连接成功 */
				dismissProgressDialog();
				haveTaskRunning = false;
				startCheckInfo();
				break;
			case RFID_ERROR:
				new DialogUtil(CheckUserInfoActivity.this)
						.showPostiveReplyDialog(CheckUserInfoActivity.this,
								"高频卡模块启动失败,请重新打开程序.");
				break;
			case FRIGER_MATCH_SUCCESS:
				mFinger = FingerDbService.getInstance(getApplicationContext())
						.queryFingerByFingerID(mFingerId + "");
				if (mFinger != null) {
					// 检测复核和唤醒
					if (mType == 1
							&& (StaticString.orgId + StaticString.userLast3Id)
									.equals(mFinger.getUser_id())) {// 复核不能同一人
						mHandler.sendEmptyMessage(CHECK_USER_INFO);
						mFingerPrint.startSearchFinger();
						return;
					}
					LogsUtil.d(TAG, "aaa=" + StaticString.orgId
							+ StaticString.userLast3Id);
					LogsUtil.d(TAG, "aab=" + mFinger.getUser_id());
					if (mType == 0
							&& !(StaticString.orgId + StaticString.userLast3Id)
									.equals(mFinger.getUser_id())) {// 唤醒必须同一人
						mHandler.sendEmptyMessage(CHECK_USER_INFO);
						mFingerPrint.startSearchFinger();
						return;
					}
					// 满足条件后
					// 服务器验证指纹合法性
					StaticString.orgId = mFinger.getUser_id().substring(0, 9);
					StaticString.userLast3Id = mFinger.getUser_id().substring(
							9, 12);
					ThreadPoolFactory.getNormalPool().execute(
							mNetFingerLoginTask);
				} else {
					mFingerPrint.startSearchFinger();
					LogsUtil.d("本地有指纹，数据库表中没有");
				}
				break;
			case FRIGER_MATCH_NOT:
				mEtPsw.startAnimation(mShake);
				break;
			case FRIGER_CONNECT_FAILED:
				new DialogUtil(CheckUserInfoActivity.this)
						.showPostiveReplyDialog(CheckUserInfoActivity.this,
								"指纹模块启动失败,请重新打开程序.");
				break;
			case FRIGER_LOGIN_FAILUE:// 服务器指纹验证不成功
				if (null != mFingerPrint)
					mFingerPrint.startSearchFinger();
				break;
			case FRIGER_LOGIN_SUCCESS:// 指纹验证成功
				mTvUser.setText(msg.obj.toString());
				mEtPsw.setText("*****");
				StaticString.information = null;

				StaticString.againPass = true;
				mFingerInfo = true;
				// 指纹验证成功
				finish();

				break;

			case LOAD_WORD_END:
				// 控制 oled屏 显示
				if (OLEDOperation.enable && OLEDOperation.mWordLib.size() > 0) {
					if (FingerPrint.enable) {
						OLEDOperation.getInstance().showTextOnOled("还可以选择",
								"按指纹登录");
					} else {
						OLEDOperation.getInstance().showTextOnOled("扫描您的IC",
								"卡登录");
					}
				}
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO
		switch (v.getId()) {
		case R.id.tv_check_user:
			if (!haveTaskRunning) {
				startScanRFID();
			}
			break;
		case R.id.btn_checkinfo_ok:
			login();
			break;
		case R.id.btn_checkinfo_cancel:
			onBackPressed();
//			if (mType == LOGIN_AGAIN) {
//				mFingerInfo = true;
//				if (!ClickUtil.isFastDoubleClick(2500)) {
//					ToastUtil.showToastInCenter("未进行复核，再次点击退出程序");
//				} else {
//					sendBroadcast(new Intent(MyContexts.ACTION_EXIT_APP));
//					finish();
//				}
//			} else {
//				// 复核 //不用退出整个进程
//				finish();
//			}
			break;
		default:
			break;
		}
	}

	private void startCheckInfo() {
		mBtnLogin.setEnabled(true);
		// 验证 用户密码
		if (null == StaticString.information) {
			mHandler.sendEmptyMessage(CONNECT_FAILED);
			return;
		}
		if (mType == 1 && StaticString.information.startsWith("*00")) {
			// 表示复核
			String[] split = StaticString.information.split(" ");
			if ("01".equals(split[1])) {
				// 复核通过
				StaticString.userIdcheck = split[2];
				StaticString.againPass = true;
				ToastUtil.showToastInCenter("登录验证通过");
				mFingerInfo = true;
				setResult(CHECK_SUCCESS_CODE);
				LogsUtil.d(TAG, "userIdcheck:" + split[2]);
				finish();
				return;
			} else {
				// 复核不通过
				SoundUtils.playFailedSound();
				StaticString.information = null;
				Toast.makeText(CheckUserInfoActivity.this,
						getResources().getString(R.string.login_passwd_error),
						Toast.LENGTH_SHORT).show();
				mEtPsw.startAnimation(mShake);
				return;
			}
		}
		// 同一个人唤醒
		String[] split = StaticString.information.split(" ");
		if ("01".equals(split[1])) {
			ToastUtil.showToastInCenter("验证通过");
			mFingerInfo = true;
			finish();
		} else {
			SoundUtils.playFailedSound();
			StaticString.information = null;
			Toast.makeText(CheckUserInfoActivity.this,
					getResources().getString(R.string.login_passwd_error),
					Toast.LENGTH_SHORT).show();
			mEtPsw.startAnimation(mShake);
		}
	}

	private void login() {
		if (TextUtils.isEmpty(mTvUser.getText())) {
			SoundUtils.playFailedSound();
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.text_please_scan_logincard),
					Toast.LENGTH_SHORT).show();
			// showToast("请扫描您的ID卡");
			return;
		}
		if (TextUtils.isEmpty(mEtPsw.getText())) {
			SoundUtils.playFailedSound();
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.text_please_input_password),
					Toast.LENGTH_SHORT).show();
			// showToast("请输入密码");
			return;
		}

		if (!networkIsAvailable()) {
			SoundUtils.playFailedSound();
			setNetwork();
			return;
		}
		SoundUtils.playDropSound();
		// 在子线程中 进行 网络连接
		mBtnLogin.setEnabled(false);
		showProgressDialog();
		haveTaskRunning = true;
		ThreadPoolFactory.getNormalPool().execute(mNetTask);

	}

	/**
	 * 39 * 检测网络是否连接 40 * @return 41
	 */
	private boolean networkIsAvailable() {

		boolean flag = false;
		// 得到网络连接信息
		mConnManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (mConnManager.getActiveNetworkInfo() != null) {
			flag = mConnManager.getActiveNetworkInfo().isAvailable();
		}
		return flag;
	}

	/**
	 * 网络未连接时，调用设置方法
	 */
	private void setNetwork() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_title_wifi);
		builder.setTitle("网络提示信息");
		builder.setMessage("网络不可用，如果继续，请先设置网络！");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				/**
				 * 判断手机系统的版本！如果API大于10 就是3.0+ 因为3.0以上的版本的设置 和3.0以下的设置不一样，调用的方法不同
				 */
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIFI_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName(
							"com.android.settings",
							"com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void startScanRFID() {
		SoundUtils.play(SoundUtils.DROP_SOUND);
		mTvUser.setText(null);
		mTvUser.setHint(USER_SCANNING[0]);
		pointScanning = 1;

		// 清除密码框的内容和焦点
		mEtPsw.setText(null);
		mEtPsw.clearFocus();
		mEtPsw.setFocusableInTouchMode(false);
		mBtnLogin.setEnabled(false);

		haveTaskRunning = true;
		ThreadPoolFactory.getNormalPool().execute(mReadRFIDTask);
	}

	private void connetFailue() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_title_alarm_48);
		builder.setTitle("提示");
		builder.setMessage("连接失败，前置是否打开？IP是否正确?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				haveTaskRunning = true;
				mBtnLogin.setEnabled(false);
				showProgressDialog();
				ThreadPoolFactory.getNormalPool().execute(mNetTask);

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mBtnLogin.setEnabled(true);
			}
		});
		builder.show();
	}

	private void showProgressDialog() {
		if (null == loginDialog) {
			loginDialog = new ProgressDialog(CheckUserInfoActivity.this);
			loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loginDialog.setTitle("正在验证••••");
			loginDialog.setIndeterminate(false);
			loginDialog.setCancelable(true);
			loginDialog.setCanceledOnTouchOutside(true);
			loginDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					mNetTask.stop();
				}
			});
		}
		loginDialog.show();
	}

	private void dismissProgressDialog() {
		if (loginDialog != null && loginDialog.isShowing()) {
			loginDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (mType == LOGIN_AGAIN) {
			mFingerInfo = true;
			if (!ClickUtil.isFastDoubleClick(2500)) {
				ToastUtil.showToastInCenter("未进行复核，再次点击退出程序");
			} else {
				sendBroadcast(new Intent(MyContexts.ACTION_EXIT_APP));
				finish();
			}
		} else {
			// 复核 //不用退出整个进程
			finish();
		}
	}
	public boolean onTouchEvent(android.view.MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);// 当用户点击页面空白处，软键盘自动消失
		return false;
	}

	@Override
	protected void onResume() {

		super.onResume();

		if (!isFingerSearching && null != mFingerPrint) {
			isFingerSearching = true;
			mfManager.registListener(mFingerListener);
			mFingerPrint.startSearchFinger();
		}

		// 控制 oled屏 显示
		if (OLEDOperation.enable && OLEDOperation.mLoginWordLib.size() > 0) {
			OLEDOperation.getInstance().open();
			if (FingerPrint.enable) {
				OLEDOperation.getInstance().showTextOnLoginOled("还可以选择",
						"按指纹登录");
			} else {
				OLEDOperation.getInstance()
						.showTextOnLoginOled("扫描您的IC", "卡登录");
			}
		}
		// ThreadPoolFactory.getNormalPool().execute(mInitFrigerTask);//初始化指纹模块
		haveTaskRunning = false;
	}

	@Override
	protected void onDestroy() {

//		unregisterReceiver(mSrceenInfoReceiver);
		mFinger = null;

		if (null != mFingerPrint) {
			isFingerSearching = false;
			mfManager.unregistListener(mFingerListener);
			mFingerPrint.stopSearchFinger();
		}
		OLEDOperation.getInstance().close();
		if (!mFingerInfo) {// 表示指纹验证不通过
//			Intent intent = new Intent(this, SrceenOnOffService.class);
//			stopService(intent);
			if (null != mFingerPrint)
				mFingerPrint.closeFinger();
			// RFIDOperation.getInstance().close();
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
		} else {
			if (null != mFingerPrint) {
				// mFingerPrint.closeFinger();
				// RFIDOperation.getInstance().closeRf();
				mFingerPrint = null;
			}
		}
		super.onDestroy();
	}

	class ReadRFIDTask implements Runnable {
		private boolean stoped;

		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			haveTaskRunning = true;
			byte[] id = null;
			int count = 0;
			stoped = false;
			while ((id == null)) {
				if (stoped || 150 < count++) {
					mHandler.sendEmptyMessage(READ_RFID_FAULTED);
					return;
				}
				if (0 == count % 5) {
					mHandler.sendEmptyMessage(READING_RFID);
				}
				// 认证读卡操作, 激活操作
				id = RFIDOperation.getInstance().findCard();
				SystemClock.sleep(20);
			}
			String checkerId = ArrayUtils.bytes2HexString(id).substring(0, 8);
			if (mType == 1
					&& (StaticString.userId).equals(checkerId)) {// 复核不能同一人
				mHandler.sendEmptyMessage(CHECK_USER_INFO);
				return;
			}
			if (mType == 0
					&& !(StaticString.userId).equals(checkerId)) {// 唤醒必须同一人
				mHandler.sendEmptyMessage(CHECK_USER_INFO);
				return;
			}
//			Message msg = mHandler.obtainMessage();
//			msg.what = READ_RFID_SUCCESS;
//			msg.obj = checkerId;
//			mHandler.sendMessage(msg);
			StaticString.userIdcheck = checkerId;
			mHandler.sendEmptyMessage(READ_RFID_SUCCESS);
			haveTaskRunning = false;
		}
	}

	/**
	 * 
	 * @ClassName: NetThread
	 * @Description: 检查网络连接线程
	 * @author Keung
	 * @date 2015-3-2 下午02:39:14
	 * 
	 */
	class NetTask implements Runnable {
		private boolean stoped;

		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {

			// 连接之后就会有数据StaticString.information(PrintThread里面返回)
			/*
			 * 建立 socket连接 sConnet.connect() 中开启 PrintThread线程对
			 * StaticString.information 赋值
			 */
			if (!SocketConnet.getInstance().isConnect()) {
				long time = System.currentTimeMillis();
				stoped = false;
				int connCount = 0;
				while (false == mSocketConn.connect(0) || mSocketConn == null
						|| !mSocketConn.isConnect()) { // 连接失败
					if (stoped || 15 < ++connCount) {
						mHandler.sendEmptyMessage(CONNECT_FAILED);
						return;
					}
					LogsUtil.s("socket conntect faile : " + connCount);
				}
				LogsUtil.s("socket connected");
				LogsUtil.s("sConnet.connect()用时:"
						+ (System.currentTimeMillis() - time));
			}

			StaticString.password = mEtPsw.getText().toString().trim();

			for (int i = 0; i < 2; i++) {
				if (mType == 0) {
					// 同一个人唤醒
					mSocketConn.communication(SendTask.CODE_LOGIN);
				} else {
					mSocketConn.communication(SendTask.CODE_LOGIN_CHECK);
				}
				if (ReplyParser.waitReply()) {
					// 连接成功
					mHandler.sendEmptyMessage(CONNECT_SUCCESS);
					return;
				} else {
					continue;
				}
			}

			mHandler.sendEmptyMessage(CONNECT_FAILED);
		}// end run()
	}

	/**
	 * 
	 * @ClassName: NetFingerLoginTask
	 * @Description: 指纹登录服务器匹配
	 * @author Keung
	 * @date 2015-3-2 下午02:39:14
	 * 
	 */
	class NetFingerLoginTask implements Runnable {
		private boolean stoped;

		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {

			for (int i = 0; i < 2; i++) {
				mSocketConn.communication(997, null);
				if (!ReplyParser.waitReplyShort()) {
					continue;
				}
				
				String[] split = StaticString.information
						.split(" ");
				if ("01".equals(split[1])) {
					StaticString.userIdcheck = split[6];
					mHandler.sendEmptyMessage(FRIGER_LOGIN_SUCCESS);
//					Message msg = mHandler.obtainMessage();
//					msg.what = FRIGER_LOGIN_SUCCESS;
//					msg.obj = split[6];
//					mHandler.sendMessage(msg);
					return;
				} else {
					// 重新搜索 用户不可用等其他原因
					mHandler.sendEmptyMessage(FRIGER_LOGIN_FAILUE);
					return;
				}

			}
			mHandler.sendEmptyMessage(FRIGER_LOGIN_FAILUE);
		}// end run()
	}
}
