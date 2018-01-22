package com.fanfull.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.db.Finger;
import com.fanfull.db.FingerDbService;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.BarCodeOperation;
import com.fanfull.hardwareAction.FingerManager;
import com.fanfull.hardwareAction.FingerManager.FingerListener;
import com.fanfull.hardwareAction.FingerPrint;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.op.RFIDOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DateUtils;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.WiFiUtil;
import com.orsoul.view.IPEditText;

/**
 * @Description:登陆界面
 */

public class LoginActivity extends BaseActivity {

	protected static final int MSG_OPEN_RFID_SUCCESS = 0;
	protected static final int CONNECT_FAILED = 1;
	protected static final int READ_RFID_SUCCESS = 2;
	protected static final int READING_RFID = 3;
	protected static final int READ_RFID_FAULTED = 4;
	protected static final int LOGIN = 5;
	protected static final int PASSWORD_WRONG = 6;
	protected static final int CONNECT_SUCCESS = 8;
	protected static final int MSG_LOGIN = 7;// 登录返回成功
	protected static final int MSG_TIMEOUT = 31;// 登录返回成功

	protected static final int MSG_OPEN_RFID_FAILED = 9;

	protected static final int FRIGER_CONNECT_FAILED = 11;
	protected static final int FRIGER_MATCH_NOT = 12;
	protected static final int FRIGER_MATCH_SUCCESS = 13;
	protected static final int FRIGER_START_LOGIN = 15;
	protected static final int LOAD_WORD_END = 16;

	protected static final int FRIGER_LOGIN_FAILUE = 17;
	protected static final int FRIGER_LOGIN_SUCCESS = 18;

	private static final int FINGER_UPDATE_PROGRESS = 19;// 更新指纹更新的进度
	private static final int MESS_SHOW_UPDATE_DIALOG = 20;// 显示更新对话框

	private static final int FINGER_UPDATE_HAPPEND_ERROR = 21;// 更新指纹过程中出现错误
	private static final int WIFI_CONNECT_SUCCESS = 22;
	private final String USER_INFO = "点击扫描IC卡";
	private final String[] USER_SCANNING = { "正在扫描.", "正在扫描..", "正在扫描...",
			"正在扫描....", "正在扫描.....", };
	private int pointScanning = 0;
	private int mFingerId = -1;

	private TextView mTvUser;
	private EditText mEtPsw;
	private Button mBtnLogin, mBtnExit;// 登陆，退出
	private Animation mShake;// 密码框抖动动画

	// 指纹操作
	private FingerPrint mFingerPrint;

	private ConnectivityManager mConnManager;
	private ProgressDialog loginDialog;

	private SocketConnet mSocketConn;
	private ConnectTask mConnTask;
	private ReadRFIDTask mReadRFIDTask;
	private LoginTask mLoginTask;
	private NetFingerLoginTask mNetFingerLoginTask;
	private UpdateFingerTask mUpdateFingerTask;// 更新指纹任务

	private Finger mFinger = null;

	private boolean mSocketIsnotConnnet = false;// 处理指纹串口打开在网络先连接的情况下。
	/**
	 * 扫描RFID的线程正在执行
	 */
	private boolean haveTaskRunning;

	// 2016-04-20 增加指纹操作
	private FingerManager mfManager;
	// 进入应用时，检测是否更新 当从设置或者主界面返回按键时，不需要更新
	private boolean isNeedUpdateFinger = true;
	private FingerDbService mDbService;

	/* 指纹更新进度条 */
	private ProgressBar mFingerProgress;
	private Dialog mFingerUpdateDialog;
	private ProgressDialog wifiConnectDialog;
	private int mFingerProgressValue = 0;

	private String mUserId;
	private int wifi_flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler(new MyHandlerCallback());
		mDiaUtil = new DialogUtil(this);
		
//		initRFID();

		// 设置网络连接
		mSocketConn = SocketConnet.getInstance();
		mConnTask = new ConnectTask();
		mLoginTask = new LoginTask();
		mNetFingerLoginTask = new NetFingerLoginTask();
		mUpdateFingerTask = new UpdateFingerTask();
		mReadRFIDTask = new ReadRFIDTask();

		if (networkIsAvailable()) {
			mDiaUtil.showProgressDialog("正在建立连接...");
			ThreadPoolFactory.getNormalPool().execute(mConnTask);
		} else {
			LogsUtil.d(TAG, "network not Available");
			showDialogWiFiSetting();
		}
	}

	protected void initData() {
		/** 打开RFID */
		mDbService = FingerDbService.getInstance(this);// 指纹数据库操作
	}

	@Override
	protected void initView() {
		// 去除title
		setContentView(R.layout.activity_login);

		findViewById(R.id.v_title).setOnClickListener(this);

		mTvUser = (TextView) findViewById(R.id.tv_login_user);
		mTvUser.setOnClickListener(this);

		mShake = AnimationUtils.loadAnimation(this, R.anim.shake);
		mEtPsw = (EditText) findViewById(R.id.et_login_psw);
		mEtPsw.setOnClickListener(this);

		mBtnLogin = (Button) findViewById(R.id.btn_login_login);
		mBtnLogin.setOnClickListener(this);

		mBtnExit = (Button) findViewById(R.id.btn_login_cancel);
		mBtnExit.setOnClickListener(this);
		mBtnExit.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (SocketConnet.getInstance().isConnect()) {
					startActivity(new Intent(getApplicationContext(),
							UploadBagIdActivity.class));
				} else {
					ToastUtil.showToastInCenter("未连接前置");
				}
				return true;
			}
		});

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

	@Override
	protected void onWiFiStatChanged() {
		if (WiFiUtil.isConnected() && !SocketConnet.getInstance().isConnect()
				&& null != mConnTask && !haveTaskRunning) {
			// WiFi已连接，未连接前置，尝试连接前置
			ThreadPoolFactory.getNormalPool().execute(mConnTask);
		}
	}

	private FingerListener mFingerListener = new FingerListener() {

		@Override
		public void openFingerSerialPortSuccess(boolean flag) {

			LogsUtil.d(TAG, "打开指纹串口:" + flag);
			if (!flag) {
				mHandler.sendEmptyMessage(FRIGER_CONNECT_FAILED);
			} else {
				// 为了区别pause --- resume的情况
				if (!isNeedUpdateFinger) {
					mFingerPrint.startSearchFinger();
				} else {
					if (mSocketConn.isConnect()) {
						ThreadPoolFactory.getNormalPool().execute(
								mUpdateFingerTask);
					} else {

						// 当网络失败，重连成功时，仍可继续更新指纹
						mSocketIsnotConnnet = true;
					}
				}
			}
		}

		@Override
		public void getLocalFingerSucess(int n) {

			mFingerPrint.stopSearchFinger();
			mFingerId = n;
			LogsUtil.d(TAG, "得到的指纹编号:" + mFingerId);
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

	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);// 当用户点击页面空白处，软键盘自动消失
	}

	@Override
	public void onBackPressed() {
		if (haveTaskRunning) {
			mReadRFIDTask.stop();
			haveTaskRunning = false;
		} else {
			clickTwiceFinish();
		}

	}

	@Override
	protected void onStart() {
		// 当返回到登录界面时，此时RFID和指纹模块都是关闭的
//		initRFID();
		super.onStart();
	}

	@Override
	protected void onResume() {

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
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (OLEDOperation.enable) {
			OLEDOperation.getInstance().close();
		}
		if (null != mFingerPrint) {
			mfManager.unregistListener(mFingerListener);
			mFingerPrint.stopSearchFinger();
			// mFingerPrint.closeFinger();
		}
		super.onPause();
		// if (null != mRFIDOp) {
		// mRFIDOp.closeRf();
		// }
	}

	@Override
	protected void onDestroy() {
		LogsUtil.d(TAG, TAG + " onDestroy()");
		closeAll();
		if (mSocketConn != null) {
			// if (mSocketConn.isConnect()) {
			// mSocketConn.communication(30);
			// SystemClock.sleep(100);
			// }
			mSocketConn.close();
			mSocketConn = null;

		}

		super.onDestroy();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(0);
	}

	@Override
	public void onClick(View v) {
		// TODO
		LogsUtil.d(TAG, "onClick haveTaskRunning?" + haveTaskRunning);
		switch (v.getId()) {
		case R.id.v_title:// 点击标题 打开WiFi设置，隐藏功能
			WiFiUtil.openWiFiSetting();
			if (mFingerPrint != null) {
				mFingerPrint.closeFinger();
			}
			break;
		case R.id.tv_login_user:
			
			if (!haveTaskRunning) {
				startScanRFID();
			}
			break;
		case R.id.btn_login_login:
			login();
			break;
		case R.id.btn_login_cancel:
			if (haveTaskRunning) {
				mReadRFIDTask.stop();
				haveTaskRunning = false;
			} else {
				onBackPressed();
			}
			break;
		default:
			break;
		}
	}

	private void initRFID() {
		if (RFIDOperation.getInstance().isOpen()) {
			mHandler.sendEmptyMessage(MSG_OPEN_RFID_SUCCESS);
			return;
		}
		ThreadPoolFactory.getNormalPool().execute(new Runnable() {
			@Override
			public void run() {
				haveTaskRunning = true;
				try {
					int count = 0;
					while (false == RFIDOperation.getInstance().openAndWakeup()) {
						if (3 < ++count) {
							mHandler.sendEmptyMessage(MSG_OPEN_RFID_FAILED);
							return;
						}
//						SystemClock.sleep(50);
					}
					mHandler.sendEmptyMessage(MSG_OPEN_RFID_SUCCESS);
				} catch (Exception ex) {
					ex.printStackTrace();
					mHandler.sendEmptyMessage(MSG_OPEN_RFID_FAILED);
					haveTaskRunning = false;
					return;
				}
				haveTaskRunning = false;
			}
		});
	}

	private boolean willLogin;

	private void login() {
		if (TextUtils.isEmpty(mTvUser.getText())) {
			SoundUtils.playFailedSound();
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_please_scan_logincard));
			return;
		}
		if (TextUtils.isEmpty(mEtPsw.getText())) {
			SoundUtils.playFailedSound();
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_please_input_password));
			return;
		}

		if (!networkIsAvailable()) {
			SoundUtils.playFailedSound();
			showDialogWiFiSetting();
			return;
		}
		SoundUtils.playDropSound();
		mDiaUtil.showProgressDialog();
		haveTaskRunning = true;
		if (mSocketConn.isConnect()) {
			// showProgressDialog();
			ThreadPoolFactory.getNormalPool().execute(mLoginTask);
		} else {
			willLogin = true;// 置为true，执行socket连接后 接着 执行 登录操作
			ThreadPoolFactory.getNormalPool().execute(mConnTask);
		}
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

	private void startScanRFID() {
		SoundUtils.play(SoundUtils.DROP_SOUND);
		mTvUser.setText(null);
		mTvUser.setHint(USER_SCANNING[0]);
		pointScanning = 1;

		// 清除密码框的内容和焦点
		mEtPsw.setText(null);
		mEtPsw.clearFocus();
		mEtPsw.setFocusableInTouchMode(false);

		haveTaskRunning = true;
		ThreadPoolFactory.getNormalPool().execute(mReadRFIDTask);
	}

	/**
	 * 比较从服务器获得到用户信息，与本地数据库中的信息，得到差异的部分
	 * 
	 * @param serverFingers
	 * @param clist
	 * @return
	 */
	private List<Finger> compareServerToClient(List<Finger> serverFingers,
			List<Finger> clist) {
		// 本地没有指纹，则全使用服务器的信息
		if (clist.size() == 0)
			return serverFingers;

		List<Finger> list = new ArrayList<Finger>();
		int i = 0;
		for (i = 0; i < serverFingers.size(); i++) {
			Finger sFinger = serverFingers.get(i);
			int j = 0;
			for (j = 0; j < clist.size(); j++) {
				Finger cFinger = clist.get(j);
				if (sFinger.getUser_id().equals(cFinger.getUser_id())
						&& sFinger.getFinger_version() == cFinger
								.getFinger_version())
					break;
			}
			if (j == clist.size()) {
				// 表示本地没有该用户的该版本
				list.add(sFinger);
			}
		}
		return list;
	}

	/**
	 * 显示 打开wifi设置的 对话框
	 */
	private void showDialogWiFiSetting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_title_wifi);
		builder.setTitle("网络提示信息");
		builder.setMessage("网络不可用，请先设置网络！");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				WiFiUtil.openWiFiSetting();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	private void showDialogSetIP() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.dialog_title_alarm_48);
		builder.setTitle("提示");
		final LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
		View v = inflater.inflate(R.layout.dilaog_ip, null);
		final com.orsoul.view.IPEditText ipEditText = (IPEditText) v
				.findViewById(R.id.v_setting_ip_set_ip1);
		ipEditText.setIp(StaticString.IP0);
		builder.setView(v);
		builder.setMessage("连接失败，前置是否打开？IP是否正确?");
		builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StaticString.IP0 = ipEditText.getText().toString();
				SPUtils.putString(MyContexts.KEY_IP0, StaticString.IP0);
				haveTaskRunning = true;
				ThreadPoolFactory.getNormalPool().execute(mConnTask);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 显示指纹更新对话框
	 */
	private void showUpdateDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(LoginActivity.this);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mFingerProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		builder.setPositiveButton("确定", null);
		builder.setNegativeButton("稍后更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (mUpdateFingerTask != null) {
					mUpdateFingerTask.stop();// 取消更新
				}
			}
		});
		mFingerUpdateDialog = builder.create();
		mFingerUpdateDialog.show();
	}

	private void closeAll() {
		OLEDOperation.mLoginWordLib.clear();
		OLEDOperation.mLoginWordLib = null;
		OLEDOperation.mWordLib.clear();
		OLEDOperation.mWordLib = null;
		UHFOperation.getInstance().close();
		RFIDOperation.getInstance().close();
		BarCodeOperation.getCloseInstance().close();

		if (null != mFingerPrint) {
			LogsUtil.d(TAG, "mFingerPrint != null");
			// mFingerPrint.stopSearchFinger();
			// mFingerPrint.closeFinger();
			mFingerPrint.stopSearchAndClose();
		}
		OLEDOperation.getInstance().close();
	}

	private class MyHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
//			mDiaUtil.dismissProgressDialog();
			switch (msg.what) {
			case MSG_TIMEOUT:
				haveTaskRunning = false;
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showReplyDialog();
				break;
			case MSG_OPEN_RFID_FAILED:
				mDiaUtil.dismissProgressDialog();
				mDiaUtil.showDialogFinishActivity("高频卡模块启动失败,请重新启动程序.");
				break;
			case MSG_OPEN_RFID_SUCCESS:
				if (FingerPrint.enable) {
					mfManager = FingerManager.getInstance();
					mfManager.registListener(mFingerListener);
					mFingerPrint = FingerPrint.getInstance();
					ThreadPoolFactory.getNormalPool().execute(new Runnable() {
						@Override
						public void run() {
							mFingerPrint.open();
						}
					});
				}
				break;
			case READ_RFID_FAULTED:
				SoundUtils.playFailedSound();
				mTvUser.setHint(USER_INFO);
				break;
			case READ_RFID_SUCCESS:
				mTvUser.setText(mUserId);
				mEtPsw.setFocusableInTouchMode(true);
				mEtPsw.requestFocus();
				mEtPsw.setText("000000");
				break;
			case READING_RFID:
				mTvUser.setHint(USER_SCANNING[pointScanning % 5]);
				pointScanning++;
				break;
			case CONNECT_SUCCESS: /* socket连接成功 */
				mDiaUtil.dismissProgressDialog();
				if (willLogin) {
					ThreadPoolFactory.getNormalPool().execute(mLoginTask);
				} else {
					mDiaUtil.dismissProgressDialog();
				}
				if (mSocketIsnotConnnet) {
					ThreadPoolFactory.getNormalPool()
							.execute(mUpdateFingerTask);
					mSocketIsnotConnnet = false;
				}
				// 服务器连接在指纹打开之后
				break;
			case CONNECT_FAILED:
				mDiaUtil.dismissProgressDialog();
				SoundUtils.playFailedSound();
				showDialogSetIP();
				break;

			case MSG_LOGIN:
				mDiaUtil.dismissProgressDialog();
				haveTaskRunning = false;
				if (null == StaticString.information) {
					return true;
				}
				// *01 01 1111113A 170605140819 002701001 004 009#
				String[] split = StaticString.information.split(" ");
				if ("01".equals(split[1])) {
					StaticString.permission = split[2];

					if (4 < split.length) {
						/* 进来到这里 ，说明是 新前置登录 */
						try {
							// 同步时间 需要将应用配置为系统应用
//							DateUtils.syncDateTime("20" + split[3]);
							long time = DateUtils.parseString2Date("20" + split[3], "yyyyMMddHHmmss").getTime();
							boolean setTimeSuccess = SystemClock.setCurrentTimeMillis(time);
							LogsUtil.d(TAG, "设置系统时间成功？ " + setTimeSuccess);
						} catch (Exception e) {
							Log.e(TAG, "同步时间时异常，可能是权限不够");
						}

						StaticString.orgId = split[4];// 保存机构号
						StaticString.userLast3Id = split[5].substring(0, 3);// 记录当前登录用户编号后三位
						SPUtils.putBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE,
								false);
					} else {
						SPUtils.putBoolean(MyContexts.KEY_USE_OLDBAG_ENABLE,
								true);
					}

					if (SPUtils.getBoolean(MyContexts.KEY_CHECK_LOGIN)) {
						StaticString.userIdcheck = null;
					} else {
						StaticString.userIdcheck = StaticString.userId;
					}

					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(intent);
				} else {
					SoundUtils.playFailedSound();
					StaticString.information = null;
					ToastUtil.showToastInCenter(getResources().getString(
							R.string.login_passwd_error));
					mEtPsw.startAnimation(mShake);
				}
				break;
			case FRIGER_MATCH_SUCCESS:
				mFinger = mDbService.queryFingerByFingerID(mFingerId + "");
				if (mFinger != null) {
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
			case FRIGER_LOGIN_FAILUE:// 服务器指纹验证不成功
				mFingerPrint.startSearchFinger();
				break;
			case FRIGER_LOGIN_SUCCESS:// 指纹验证成功
				mTvUser.setText(StaticString.userId);
				mEtPsw.setText("*****");
				mHandler.sendEmptyMessage(FRIGER_START_LOGIN);
				break;
			case FRIGER_CONNECT_FAILED:
				new DialogUtil(LoginActivity.this).showPostiveReplyDialog(
						LoginActivity.this, "指纹模块启动失败,请重新打开程序.");
				break;
			case FRIGER_START_LOGIN:
				Intent intent = new Intent(LoginActivity.this,
						PermissionActivity.class);
				startActivity(intent);
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

			case FINGER_UPDATE_PROGRESS:
				// 指纹进度更新
				// 设置进度条位置
				LogsUtil.d(TAG, "value=" + mFingerProgressValue);
				mFingerProgress.setProgress(mFingerProgressValue);
				if (100 == mFingerProgressValue) {
					mFingerUpdateDialog.dismiss();
					mFingerPrint.startSearchFinger();
				}
				break;
			case MESS_SHOW_UPDATE_DIALOG:
				showUpdateDialog();
				break;
			case FINGER_UPDATE_HAPPEND_ERROR:
				new TipDialog().createDialog(LoginActivity.this,
						"更新指纹发生错误，请尝试重启应用！", 0);
				break;
			case WIFI_CONNECT_SUCCESS:
				wifiConnectDialog.dismiss();
				break;
			}
			return true;
		}

	}

	private class ReadRFIDTask implements Runnable {
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
				if (stoped || 50 < count++) {
					mHandler.sendEmptyMessage(READ_RFID_FAULTED);
					RFIDOperation.getInstance().closeRF();
					haveTaskRunning = false;
					return;
				}
				if (0 == count % 5) {
					mHandler.sendEmptyMessage(READING_RFID);
				}
				// 认证读卡操作, 激活操作
				id = RFIDOperation.getInstance().findCard();
				SystemClock.sleep(50);
			}
			mUserId = ArrayUtils.bytes2HexString(id);
			// mUserId = StaticString.userId.substring(0, 8);
			mHandler.sendEmptyMessage(READ_RFID_SUCCESS);
			RFIDOperation.getInstance().closeRF();
			haveTaskRunning = false;
			LogsUtil.d(TAG, "scanning stop");
		}
	}

	/**
	 * 创建 socket 连接
	 */
	private class ConnectTask implements Runnable {
		private boolean stoped;

		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			haveTaskRunning = true;
			long time = System.currentTimeMillis();
			stoped = false;
			int connCount = 0;
			while (false == mSocketConn.connect(0)) { // 连接失败
				LogsUtil.d(TAG, "socket conntect faile : " + connCount);
				if (stoped || 0 < ++connCount) {
					mHandler.sendEmptyMessage(CONNECT_FAILED);
					haveTaskRunning = false;
					return;
				}
			}
			LogsUtil.d(TAG, "connect()用时:"
					+ (System.currentTimeMillis() - time));
			mHandler.sendEmptyMessage(CONNECT_SUCCESS);
			haveTaskRunning = false;
		}// end run()
	}

	/**
	 * 
	 */
	private class LoginTask implements Runnable {
		@Override
		public void run() {
			haveTaskRunning = true;
			LogsUtil.w(TAG, LoginTask.class.getSimpleName() + " run");
			StaticString.userId = mTvUser.getText().toString().trim();
			StaticString.password = mEtPsw.getText().toString().trim();
			mSocketConn.communication(1);
			if (ReplyParser.waitReply()) {
				mHandler.sendEmptyMessage(MSG_LOGIN);
			} else {
				mHandler.sendEmptyMessage(MSG_TIMEOUT);
			}
			LogsUtil.w(TAG, LoginTask.class.getSimpleName() + " end");
			haveTaskRunning = false;
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
			StaticString.information = null;

			for (int i = 0; i < 2; i++) {
				mSocketConn.communication(997, null);
				if (!ReplyParser.waitReply()) {// 超时再发一次
					continue;
				}
				String[] split = StaticString.information.split(" ");
				// if (null == split || split.length < 8) {
				// mHandler.sendEmptyMessage(FRIGER_LOGIN_FAILUE);
				// return;
				// }
				if ("01".equals(split[1])) {
					try {
						StaticString.userId = Long.toHexString(
								Long.parseLong(split[6])).toUpperCase();
						LogsUtil.d(TAG, "finger getUserid="
								+ StaticString.userId);
						mHandler.sendEmptyMessage(FRIGER_LOGIN_SUCCESS);
					} catch (Exception e) {// NumberFormatException
						e.printStackTrace();
						ToastUtil.showToastInCenter("服务器回复异常");
						mHandler.sendEmptyMessage(FRIGER_LOGIN_FAILUE);
					}

					return;
				} else {
					// 重新搜索 用户不可用,等其他原因
					mHandler.sendEmptyMessage(FRIGER_LOGIN_FAILUE);
					return;
				}
			}
			mHandler.sendEmptyMessage(FRIGER_LOGIN_FAILUE);

		}// end run()
	}

	/**
	 * 
	 * @ClassName: UpdateFingerTask
	 * @Description: 更新指纹信息
	 * @author Keung
	 * @date 2015-3-2 下午02:39:14
	 * 
	 */
	class UpdateFingerTask implements Runnable {
		private boolean stoped;
		private int version;// 总版本数

		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			haveTaskRunning = true;
			isNeedUpdateFinger = false;
			/** 最先检测，需不需要更新指纹 */
			int fingerUpdateVersionId = SPUtils.getInt(
					MyContexts.KEY_FINGER_UPDATE_VERSION, 0);
			/** 将当前本地的版本编号发给服务器 */
			StaticString.information = null;
			List<Finger> serverFingers = new ArrayList<Finger>();// 保存服务器的用户指纹信息
			int x = 0;
			for (x = 0; x < 2; x++) {
				SocketConnet.getInstance().communication(1000,
						new String[] { fingerUpdateVersionId + "" });
				if (!ReplyParser.waitReply()) {
					continue;
				}
				if (StaticString.information.startsWith("*14 00 00 ")) {
					// 服务器客户端版本一致
					mFingerPrint.startSearchFinger();
					haveTaskRunning = false;
					return;
				} else if (StaticString.information.startsWith("*14 00 66 ")) {
					// 本地高于服务器
					mFingerPrint.startSearchFinger();
					haveTaskRunning = false;
					return;
				} else if (StaticString.information.startsWith("*02 005 ")) {
					// fuwuqi error
					mFingerPrint.startSearchFinger();
					haveTaskRunning = false;
					return;
				}
				/** 需要更新的情况下，则将获得服务器所有用户信息的最高版本 */
				// [{"userId":"userIddata1","version":"versionData1"},{"userId":"userIddata2","version":"versionData2"}]
				fingerUpdateVersionId = Integer
						.parseInt(StaticString.information.split(" ")[3]);// 拿到服务器的版本。
				try {
					JSONArray array;
					array = new JSONArray(
							StaticString.information.split(" ")[2]);
					for (int i = 0; i < array.length(); i++) {
						Finger finger = new Finger();
						finger.setUser_id(array.getJSONObject(i).getString(
								"UserID"));
						finger.setFinger_version(array.getJSONObject(i).getInt(
								"Version"));
						serverFingers.add(finger);
					}
					break;// 跳出循环
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (x == 2) {
				/** 连接失败 */
				mHandler.sendEmptyMessage(CONNECT_FAILED);
				return;
			}

			/** 这是本地数据库中，已经存在的指纹，加载本地所有指纹和服务器匹配，挑选出，需要更新的用户指纹信息 */
			List<Finger> list = mDbService.loadAllFinger();
			List<Finger> comparList = compareServerToClient(serverFingers, list);
			LogsUtil.i(TAG, "comparList.size=" + comparList.size());
			if (comparList.size() == 0) {
				/** 不需要更新的情况下，即可直接搜索指纹 */
				SPUtils.putInt(MyContexts.KEY_FINGER_UPDATE_VERSION,
						fingerUpdateVersionId);
				mFingerPrint.startSearchFinger();
				haveTaskRunning = false;
				return;
			} else {
				mHandler.sendEmptyMessage(MESS_SHOW_UPDATE_DIALOG);
				/** 开始取差异的指纹信息 将差异的用户信息，一个一个发送给服务器 */
				for (int i = 0; i < comparList.size() && !stoped; i++) {
					/** -----------start------handle one finger-------- */
					LogsUtil.d(TAG, "--start:差异部分开始下载---");
					/** 循环发送差异部分 */
					int k = 1, m = 0, n = 0;
					for (k = 1; k < 4; k++) {// 一个人有三个指纹
						SocketConnet.getInstance().communication(
								1001,
								new String[] {
										comparList.get(i).getUser_id(),
										comparList.get(i).getFinger_version()
												+ "", k + "" });
						if (ReplyParser.waitReply()) {
							if (StaticString.information.startsWith("*14 00")) {
								continue;
							}
							LogsUtil.d("-----get one finger-success-----start downloader to fingerstore----");

							/**
							 * 从本地数据库中，获取一个唯一的指纹编号， 将服务器得到的指纹下载到手持中。
							 * 这里使用了一个临时的文件就是中间中转
							 */
							Long fingerId = mDbService.getKeyIdLong();
							LogsUtil.d(TAG, "指纹编号:" + fingerId);
							/** 开始下载到手持中 */
							if (mFingerPrint.downLoadChar(
									getApplicationContext(),
									Integer.parseInt(fingerId + ""), k + "",
									StaticString.information.split(" ")[6])) {
								/** 下载成功 */
								// *14 01 2(总数) 1（序号） 087101001002 1（版本）
								// 0301561500
								Finger finger = new Finger();
								finger.setFinger_id(Integer.parseInt(fingerId
										+ ""));
								finger.setFinger_version(Integer
										.parseInt(StaticString.information
												.split(" ")[5]));
								finger.setUser_id(StaticString.information
										.split(" ")[4]);
								finger.setFinger_sno(Integer
										.parseInt(StaticString.information
												.split(" ")[3]));
								mDbService.saveFinger(finger);

								m = Integer.parseInt(StaticString.information
										.split(" ")[2]);
								n = Integer.parseInt(StaticString.information
										.split(" ")[3]);
								if (m == n) {
									break;
								}
							} else {
								/** 当次下载失败，进行失败处理,原则上也是不可能失败，出现失败，说明指纹模块有问题 */
								mHandler.removeMessages(FINGER_UPDATE_HAPPEND_ERROR);
								mHandler.sendEmptyMessage(FINGER_UPDATE_HAPPEND_ERROR);
								return;
							}
						} else {
							continue;
						}
					}// end 每一个用户三个指纹
					/** 计算进度条位置 */
					mFingerProgressValue = (int) (((float) (i + 1) / comparList
							.size()) * 100);
					mHandler.removeMessages(FINGER_UPDATE_PROGRESS);
					mHandler.sendEmptyMessage(FINGER_UPDATE_PROGRESS);
				}
				/** -----------end------handle all users-------- */
				LogsUtil.d(TAG, "fingerUpdateVersionId="
						+ fingerUpdateVersionId);
				SPUtils.putInt(MyContexts.KEY_FINGER_UPDATE_VERSION,
						fingerUpdateVersionId);
			}
			haveTaskRunning = false;
		}// end run()
	}
}
