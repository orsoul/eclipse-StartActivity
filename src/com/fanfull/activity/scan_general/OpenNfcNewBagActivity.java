package com.fanfull.activity.scan_general;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.background.ActivityUtil;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.hardwareAction.RFIDOperation;
import com.fanfull.op.UHFOperation;
import com.fanfull.operation.BagOperation;
import com.fanfull.operation.NFCBagOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.AESCoder;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.Lock3Util;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;
import com.fanfull.view.CoverBagItemView;

/**
 * @Description: 新锁 开袋 InStoreNfcActivity 在正常开袋之前需要检测如下东西： 1.检测是否已经完成封签
 * 
 *               如果以上四点均通过后: 1.手持改写标志位，以及更新目录索引信息，以及交接信息，
 *               2.手持向服务器发送封签事件码，封签码，以及袋Id到服务器，服务器验证是否可以正常开袋。
 */
public class OpenNfcNewBagActivity extends BaseActivity {

	private TextView mTvPlanAmount;
	private TextView mTvTotalAmount;
	private TextView mTvScanAmount;

	private CoverBagItemView mVReadBarCode;
	private CoverBagItemView mVReadBagLock;
	private CoverBagItemView mVReadUhfLock;
	private CoverBagItemView mVNetCheck;
	private CoverBagItemView mVUpdateBagLock;

	private ActivityHeadItemView mTitle;
	private Button mBtnConfirm;
	private Button mBtnCancel;
	private TextView mTvLock;

	private int type = 0;// 当前activity的类型

	private final int SHOW_LOCK_RESULT = 11;
	private final int SHOW_LOCK_FAILED = 12;

	private final int BAG_NO_INIT = 4;

	private final static int READ_BAG_NO_COVER = 0x998;
	private final static int READ_BAG_FAILED = 0x1000;
	private final static int READ_BAG_SUCCESS = 0x1001;
	private final static int READ_BAGLOCK_START = 0x1002;// 开始读袋锁
	private final static int UPDATE_BAGLOCK_START = 0x1003;// 开始更新袋锁
	private final static int READ_UHF_START = 0x1004;// 开始更新uhf
	private final static int NET_CHECK_START = 0x1005;// 开始服务器校验

	private final static int CHECK_FIRST_NET_SUCCESS = 0x1008;
	private final static int CHECK_BARCODE_NET_SUCCESS = 0x1009;
	private final static int CHECK_BARCODE_NET_FAILED = 0x1010;
	private final static int CHECK_BARCODE_NET_TIMEOUT = 0x1011;

	private final static int BAG_WRITE_FLAG_SUCCESS = 0x1012;
	private final static int BAG_WRITE_FLAG_FAILED = 0x1013;

	private final static int BAG_READ_EPC_SUCCESS = 0x1016;
	private final static int BAG_READ_EPC_FAILED = 0x1017;

	private final static int BAG_NOTICE_CHANGE_NEW_BAG = 0x1024;// 电池电量不够提示返修

	// 任务进度
	private static final int READ_BAGLOACK = 1;
	private static final int READ_UHF = 2;
	private static final int NET_CHECK = 3;
	private static final int UPDATE_BAGLOCK = 4;
	private static final int COVER_OVER = 5;

	private int mStep = READ_BAGLOACK;

	private NFCBagOperation mNfcBagOp = null;
	private byte mUid[] = null;
	private byte mTid[] = null;
	private byte[] mIndexInfo = null;

	private UHFOperation mUHFOp = null;
	private BagOperation mBagOp = null;
	private ReadBagInItTask mBagInItTask;
	private NetOpenTask mNetOpenTask;// 进行开袋
	private WriteRFIDTask mWriteRfidTask;
	private ReadAndInitEPCTask mReadInitEPCTask;

	private boolean haveTaskRunning;

	private int mTotalFinish;// 总完成数量
	private int mPersonFinish;// 个人完成数量
	private int mPlanNumber;// 个人完成数量

	private byte mExchangeStartAddr = 0x0;

	private boolean isFirstScanend = true;

	private byte[] mBqTidInNfc = null;// 存在NFC卡中的TID值
	private String mEventCode = null;// 存在NFC卡中的封签事件码

	private String mExchangeInfo = "";
	private String mLogsIndexNumber = "";// 接收服务器的唯一记录编号
	private byte mFlag[];// 记录当前锁中标志
	private byte mCurrentMiyue;// 得到当前密钥

	private long timetmp = 0;// 记录本次操作的时间，可删除

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mUHFOp = UHFOperation.getInstance();
		if (mUHFOp.open(false) < 0) {
			ToastUtil.showToastInCenter(getResources().getString(
					R.string.text_init_uhf_failed));
			return;
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iostore_bag_new);

		/**/
		Intent intent = getIntent();
		type = intent.getIntExtra(MyContexts.KEY_OPERATION_TYPE, 0);
		LogsUtil.d(TAG, "type : " + type);

		findView();

		mUHFOp = UHFOperation.getInstance();
		mBagOp = BagOperation.getInstance();

		/** 第一 读袋码，以及验证是否启用 */
		mBagInItTask = new ReadBagInItTask();
		/** 第二 读TID，将UID写入到UHF中 */
		mReadInitEPCTask = new ReadAndInitEPCTask();
		/** 第三 请求服务器校验是否合法封袋 */
		mNetOpenTask = new NetOpenTask();
		/** 第四 更新袋锁，写标志位以及索引信息 */
		mWriteRfidTask = new WriteRFIDTask();

		SystemClock.sleep(100);
		if (Lock3Util.setCoverPower(true) < 1) {
			ToastUtil.showToastInCenter("功率设置失败");
		}
	}

	/**
	 * 
	 * @Title: initView
	 * @Description: 初始化界面
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	protected void findView() {
		mTitle = (ActivityHeadItemView) findViewById(R.id.v_coverbagactivity_title);
		mTitle.setText("开  袋");

		mVReadBarCode = (CoverBagItemView) findViewById(R.id.v_new_coverbag_read_barcode);
		mVReadBarCode.setVisibility(View.GONE);
		mVReadBagLock = (CoverBagItemView) findViewById(R.id.v_new_coverbag_read_bagLock);
		mVReadBagLock.setText("袋码        ");
		mVReadUhfLock = (CoverBagItemView) findViewById(R.id.v_new_coverbag_write_bagLock);
		mVReadUhfLock.setText("标签码    ");
		mVNetCheck = (CoverBagItemView) findViewById(R.id.v_new_coverbag_net_check);
		mVNetCheck.setText("信息校验");
		mVUpdateBagLock = (CoverBagItemView) findViewById(R.id.v_new_coverbag_update_uhf);
		mVUpdateBagLock.setText("更新信息");

		mTvPlanAmount = (TextView) findViewById(R.id.plan_amount);
		mTvTotalAmount = (TextView) findViewById(R.id.finish_amount);// 完成总数量
		mTvScanAmount = (TextView) findViewById(R.id.person_scan_amount);// 个人扫描总数量

		mBtnConfirm = (Button) findViewById(R.id.btn_ok);// 确认按钮
		mBtnConfirm.setOnClickListener(this);

		mBtnCancel = (Button) findViewById(R.id.btn_cancel);// 取消按钮
		mBtnCancel.setOnClickListener(this);
		mBtnCancel.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				mStep = READ_BAGLOACK;
				haveTaskRunning = false;
				initUi();
				return true;// 事件拦截
			}
		});

		mTvLock = (TextView) findViewById(R.id.over); // 结束批
		mTvLock.setOnClickListener(this);

		ViewUtil.requestFocus(mBtnConfirm);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (OLEDOperation.enable) {
			OLEDOperation.getInstance().open();
			OLEDOperation.getInstance().showTextOnOled("请将手持", "靠近袋锁");
		}
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_ok:// 扫描操作按键
			mBtnConfirm.setText("扫描");
			if (mStep == READ_BAGLOACK) { // 第一次按确定键, 读袋锁
				initUi();
				timetmp = System.currentTimeMillis();
				mHandler.sendEmptyMessage(READ_BAGLOCK_START);
			} else if (mStep == READ_UHF) {
				mHandler.sendEmptyMessage(READ_UHF_START);
			} else if (mStep == NET_CHECK) {
				mHandler.sendEmptyMessage(NET_CHECK_START);
			} else if (mStep == UPDATE_BAGLOCK) {
				mHandler.sendEmptyMessage(UPDATE_BAGLOCK_START);
			} else if (mStep == COVER_OVER) {
				finish();
			}
			break;
		case R.id.btn_cancel:// 取消按键
			doBack();
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

	private void doBack() {
		// TODO Auto-generated method stub
		if (!haveTaskRunning) {
			finish();
		} else if (mStep == READ_BAGLOACK) { // 读袋锁阶段，初始化信息，启用码
			mVReadBagLock.setDoing(false);
			mBagInItTask.stop();
			mBtnConfirm.setEnabled(true);
			haveTaskRunning = false;
		} else if (mStep == READ_UHF) { // 读TID
			mVReadUhfLock.setDoing(false);
			mReadInitEPCTask.stop();
			mBtnConfirm.setEnabled(true);
			haveTaskRunning = false;
		} else if (mStep == NET_CHECK) {
			mBtnConfirm.setEnabled(true);
			mVNetCheck.setDoing(false);
			mBtnConfirm.setEnabled(true);
			haveTaskRunning = false;
			// 更新袋锁，索引，条码，封签信息，标志位
		} else if (mStep == UPDATE_BAGLOCK) { //
			mVUpdateBagLock.setDoing(false);
			mWriteRfidTask.stop();
			haveTaskRunning = false;
			// 将条码等数据发送到服务器验证，是否已经封签过
		}
	}

	/**
	 * 处理数据
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case READ_BAGLOCK_START:
				haveTaskRunning = true;
				mVReadBagLock.setDoing(true);
				mBtnConfirm.setEnabled(false);
				ThreadPoolFactory.getNormalPool().execute(mBagInItTask);
				break;

			case READ_BAG_SUCCESS:
				LogsUtil.s("READ_BAG_SUCCESS");
				mVReadBagLock.setChecked(true);
				mBagInItTask.stop();
				mHandler.sendEmptyMessage(READ_UHF_START);
				break;

			case BAG_NO_INIT:
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				mBagInItTask.stop();
				new DialogUtil(OpenNfcNewBagActivity.this)
						.showNegativeReplyDialog(MyContexts.BAG_LOACK_NOT_INIT,
								MyContexts.TEXT_CANCEL);
				mVReadBagLock.setDoing(false);
				mBtnConfirm.setEnabled(true);
				break;

			/** 电压已经低于2.85V 此时需要提示用户需要返厂维修 */
			case BAG_NOTICE_CHANGE_NEW_BAG:
				new DialogUtil(OpenNfcNewBagActivity.this)
						.showNegativeReplyDialog("该袋拆封后，需返厂维修",
								MyContexts.TEXT_OK);
				break;

			/** 在开袋时,标志位不正常，此时需要指纹处理 */
			case READ_BAG_NO_COVER:
				SoundUtils.playFailedSound();
				mVReadBagLock.setDoing(false);
				mBtnConfirm.setText("重新扫描");
				mStep = READ_BAGLOACK;// 重新开始
				mBtnConfirm.setEnabled(true);
				TipDialog tp = new TipDialog();
				tp.createDialog(OpenNfcNewBagActivity.this, "该袋未正常封签", 0);
				break;

			case READ_BAG_FAILED:
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				mBagInItTask.stop();
				mVReadBagLock.setDoing(false);
				mBtnConfirm.setEnabled(true);
				break;

			/** 读Tid操作 */
			case READ_UHF_START:// 读uhf的tid
				mVReadUhfLock.setDoing(true);
				haveTaskRunning = true;
				mStep = READ_UHF;
				mBtnConfirm.setEnabled(false);
				ThreadPoolFactory.getNormalPool().execute(mReadInitEPCTask);
				break;
			case BAG_READ_EPC_SUCCESS:
				LogsUtil.s("BAG_INIT_EPC_SUCCESS");
				mReadInitEPCTask.stop();
				haveTaskRunning = true;
				mVReadUhfLock.setChecked(true);
				mHandler.sendEmptyMessage(NET_CHECK_START);
				break;

			case BAG_READ_EPC_FAILED:
				mVReadUhfLock.setDoing(false);
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				mReadInitEPCTask.stop();
				mBtnConfirm.setEnabled(true);
				break;

			/** 开始上传开袋任务信息到服务器验证 */
			case NET_CHECK_START:
				haveTaskRunning = true;
				mBtnConfirm.setEnabled(false);
				mVNetCheck.setDoing(true);
				ThreadPoolFactory.getNormalPool().execute(mNetOpenTask);
				break;

			/** 开始上传开袋任务，服务器反馈成功，得到扫描数量,但是没有更新UI */
			case CHECK_FIRST_NET_SUCCESS:// 6
				haveTaskRunning = true;
				mVNetCheck.setChecked(true);
				mHandler.sendEmptyMessage(UPDATE_BAGLOCK_START);
				break;

			/** 开始上传开袋任务，服务器反馈失败 */
			case CHECK_BARCODE_NET_FAILED:// 6
				haveTaskRunning = false;
				mBtnConfirm.setEnabled(true);
				mVNetCheck.setDoing(false);
				mBtnConfirm.setText("重新扫描");
				mStep = READ_BAGLOACK;// 重新开始
				new TipDialog().createDialog(OpenNfcNewBagActivity.this, 0);
				break;

			/** 开始上传开袋任务，服务器反馈超时 */
			case CHECK_BARCODE_NET_TIMEOUT:
				haveTaskRunning = false;
				mBtnConfirm.setEnabled(true);
				mVNetCheck.setDoing(false);
				mBtnConfirm.setText("重新扫描");
				mStep = READ_BAGLOACK;// 重新开始
				ToastUtil.showToastInCenter("网络错误，等待时间过长");
				break;

			/** 开始更新锁片和标签信息 */
			case UPDATE_BAGLOCK_START:
				mVUpdateBagLock.setDoing(true);
				haveTaskRunning = true;
				mStep = UPDATE_BAGLOCK;
				mBtnConfirm.setEnabled(false);
				ThreadPoolFactory.getNormalPool().execute(mWriteRfidTask);
				break;
			/** 更新锁片标志位索引交接信息成功 */
			case BAG_WRITE_FLAG_SUCCESS:
				LogsUtil.s("BAG_WRITE_BARCODE_SUCCESS");
				mWriteRfidTask.stop();
				mVUpdateBagLock.setChecked(true);
				mHandler.sendEmptyMessage(CHECK_BARCODE_NET_SUCCESS);
				break;

			/** 更新标志位信息失败 */
			case BAG_WRITE_FLAG_FAILED:
				ThreadPoolFactory.getNormalPool().execute(
						new NetOpenFailedTask());
				mVUpdateBagLock.setDoing(false);
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				mWriteRfidTask.stop();
				mBtnConfirm.setEnabled(true);
				mStep = READ_BAGLOACK;
				mBtnConfirm.setText("重新扫描");
				break;

			/** 写入交接信息成功，开始更新UI界面显示 */
			case CHECK_BARCODE_NET_SUCCESS:
				ToastUtil.showToastInCenter("本次花时:"
						+ (System.currentTimeMillis() - timetmp) + "ms");
				haveTaskRunning = false;
				// 等开袋服务器完成
				SoundUtils.playNumber(mPersonFinish);
				mBtnConfirm.setEnabled(true);
				mTvScanAmount.setText("" + mPersonFinish);
				mTvTotalAmount.setText("" + mTotalFinish);
				mTvPlanAmount.setText("" + mPlanNumber);

				// if ("".equals(mTvTotalAmount.getText().toString())) {
				// mTvTotalAmount.setText("0");
				// }
				// if ("".equals(mTvPlanAmount.getText().toString())) {
				// mTvPlanAmount.setText("0");
				// }

				if (!mTvTotalAmount.toString().equals("" + mPlanNumber)) {
					mBtnConfirm.setText("继续");
					mStep = READ_BAGLOACK;// 重新开始
				} else {
					mBtnConfirm.setText("完成");
					// 小屏提示锁定批
					if (OLEDOperation.enable) {
						OLEDOperation.getInstance().open();
						OLEDOperation.getInstance().showTextOnOled("请锁定该批");
					}
				}
				mBtnCancel.setText("退出");
				// 关闭屏幕
				if (isFirstScanend) {
					if (OLEDOperation.enable) {
						OLEDOperation.getInstance().close();
					}
					isFirstScanend = false;
				}
				break;

			/** 锁定批成功 */
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
					new TipDialog().createDialog(OpenNfcNewBagActivity.this, 1);
				} else {
					new TipDialog().createDialog(OpenNfcNewBagActivity.this, 0);
				}
				break;
			/** 锁定批失败 */
			case SHOW_LOCK_FAILED:
				new DialogUtil(OpenNfcNewBagActivity.this)
						.showPostiveReplyDialog(OpenNfcNewBagActivity.this,
								MyContexts.LOCK_PI_TIMEOUT);
				break;
			}

		}

	};

	/**
	 * 界面过程显示初始化，恢复到最开始状态
	 */
	private void initUi() {
		mBtnConfirm.setText("扫描");
		mVReadBagLock.setChecked(false);
		mVReadUhfLock.setChecked(false);
		mVNetCheck.setChecked(false);
		mVUpdateBagLock.setChecked(false);
		// Arrays.fill(mThreadStatus,Status.FAILURED );

	}

	public void buildLockPi() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
				MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				StaticString.information = null;
				SocketConnet.getInstance().communication(7);// 锁定批
				ThreadPoolFactory.getNormalPool().execute(new Runnable() {
					@Override
					public void run() {
						// 在子线程中 检查 StaticString.information
						// 是否为空
						if (ReplyParser.waitReply()) {
							mHandler.sendEmptyMessage(SHOW_LOCK_RESULT);
						} else {
							mHandler.sendEmptyMessage(SHOW_LOCK_FAILED);
						}
					}
				});
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	public void showPostiveReplyDialog(Context context, String info) {
		final Activity activity = (Activity) context;

		AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("提示");
		builder.setMessage(info);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		builder.show();
	}

	public boolean pasrePiNumber() {
		boolean retVal = false;
		if (null == StaticString.information) {
			return retVal;
		}
		if (StaticString.information.length() < 16) {
			LogsUtil.d("pasrePiNumber", "information反馈错误");
			return retVal;
		}
		try {
			String[] str = StaticString.information.split(" ");
			String personFinish = StaticString.information.substring(6, 10);// 个人完成数量
			String totalFinish = StaticString.information.substring(10, 14);// 总完成数量
			String planNumber = StaticString.information.substring(14, 18);// 计划数量

			mPersonFinish = Integer.parseInt(personFinish);// 个人完成数量
			mTotalFinish = Integer.parseInt(totalFinish);// 总完成数量
			mPlanNumber = Integer.parseInt(planNumber);// 计划数量

			LogsUtil.d(TAG, "mTotalFinish: " + mTotalFinish);
			LogsUtil.d(TAG, "mPersonFinish: " + mPersonFinish);
			mExchangeInfo = str[2].substring(1, str[2].length());// 丢掉最前面的0
			mLogsIndexNumber = str[3];
			LogsUtil.d(TAG, "mExchangeInfo=" + mExchangeInfo);
			LogsUtil.d(TAG, "mLogsIndexNumber=" + mExchangeInfo);

			retVal = true;

			if ("9999".equals(mPlanNumber)) {
				// mPlanNumber = "-";

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retVal;
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
		case KeyEvent.KEYCODE_BACK:
			doBack();
			break;
		case KeyEvent.KEYCODE_HOME:
			this.finish();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		doBack();
	}

	// 重写 activity退出
	public void finish(Boolean exitFlag) {
		StaticString.information = null;
		if (exitFlag) {
			setResult(0);
		} else {
			setResult(1);
		}
		finish();
	}

	public void onClickBack(View v) {
		doBack();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogsUtil.d(TAG, "onDestroy");
		// bagop.close();
		if (OLEDOperation.enable) {
			OLEDOperation.getInstance().close();
		}

		mStep = COVER_OVER;
		RFIDOperation.getInstance().closeRf();
		mBagOp = null;
		if (null != mUHFOp) {
			LogsUtil.d(TAG, "--close--uhf---");
			mUHFOp.close();
		}
		mUHFOp = null;
		mHandler.removeCallbacksAndMessages(null);
		ActivityUtil.getInstance().removeActivityFromList(this);
	}

	/**
	 * 通过读取索引 确定这一次交接信息需要写的位置 记录该位置的是地址0x20的低两位，即其最后一个字节
	 * 
	 * @param data
	 *            0x20 四字节数据
	 * @return 0x20低两位 即交接信息需要写的位置
	 */
	public byte getNextExchangItemAddr(byte[] data) {
		if (data == null || (null != data && data.length < 4)) {
			return 0x40;
		} else {
			return (byte) (data[3]);
		}
	}

	/**
	 * 第1步，验证袋是否初始化以及是否启用
	 * 
	 * @ClassName: readLockBag
	 * @Description: 读基金袋,根据UID检测是哪种卡操作，再验证袋版本是否 初始化(读取第1块区或者是起始为0x05的位置)
	 *               以及是否已经启用。
	 */
	class ReadBagInItTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		public void run() {
			final int TIMES = 100;// 读取 袋锁的次数
			int count = 0;
			stoped = false;

			while (!stoped) {
				if (TIMES < ++count) {
					mHandler.sendEmptyMessage(READ_BAG_FAILED);
					stoped = true;
					return;
				}
				mUid = mBagOp.getUid();
				if (null != mUid && mUid.length == 7) {
					mNfcBagOp = mBagOp.getNfcBagOperation();
				} else {
					SystemClock.sleep(50);
					continue;
				}

				int j = -1;
				j = (null == mNfcBagOp ? -1 : mNfcBagOp.readBagID());
				LogsUtil.d(TAG, "j= " + j);
				if (j == -1)
					continue;// 扫描失败
				if (j == 5) { // 已初始化

					/** 读取密钥信息 */
					byte miyue = (null == mNfcBagOp ? 0x00 : mNfcBagOp
							.readMiyue());
					if (0xaa == (miyue & 0xFF) || 0x00 == miyue) {
						SystemClock.sleep(50);
						continue;
					}
					int t = 0;
					try {
						t = Integer.parseInt(ArrayUtils.bytes2HexString(
								new byte[] { miyue }).substring(1, 2));
					} catch (Exception e) {
						t = 0;
					}
					mCurrentMiyue = ArrayUtils.getFlagData(mUid, t);

					/** 读取标志位信息 */
					mFlag = (null == mNfcBagOp ? null : mNfcBagOp.readTwoFlag());
					if (null == mFlag) {
						SystemClock.sleep(50);
						continue;// 标志位异常
					}

					/** 在已经初始化和启用的情况下，再检测标志位 */
					if ((byte) ArrayUtils.flagData[5 * ((mUid[1] & 0xff) % 10) + 2] != (byte) (mFlag[0] ^ mCurrentMiyue)) {
						/** 不等于F3的情况 */
						LogsUtil.d(TAG,
								"没有正常封签操作 " + ArrayUtils.bytes2HexString(mFlag));
						mHandler.sendEmptyMessage(READ_BAG_NO_COVER);
						return;
					}

					/** 读取目录索引 */
					mIndexInfo = (null == mNfcBagOp ? null : mNfcBagOp
							.readIndexAddr());
					if (null == mIndexInfo) {
						continue;
					} else if (mIndexInfo.length > 3 && mIndexInfo[3] < 0x40) {
						mIndexInfo = new byte[] { (byte) 0xff, (byte) 0xff,
								(byte) 0xff, 0x40 };
					}

					/** 读取袋中存放的TID信息 */
					mBqTidInNfc = mNfcBagOp.readBqTid();
					if (null == mBqTidInNfc) {
						SystemClock.sleep(20);
						continue;
					}

					/** 读取袋中存放的封签事件码信息 */
					mEventCode = mNfcBagOp.readBagBarCode();
					if (null == mEventCode) {
						SystemClock.sleep(20);
						continue;
					}
					// StaticString.bagtidcode = mBagTidcodeInNfc;

					mHandler.sendEmptyMessage(READ_BAG_SUCCESS);
					return;
				} else if (j == BAG_NO_INIT) {
					// 未初始化
					mHandler.sendEmptyMessage(BAG_NO_INIT);
					stoped = true;
					return;
				} else {
					// 读卡失败
					SystemClock.sleep(50);
				}

			}
			stoped = true;
		}
	}

	/**
	 * 第2步，读取TID号,拷贝初始化信息，标志位信息到UHF中。
	 * 
	 * @ClassName: ReadEPCTask
	 * @Description:读超高的tid
	 * 
	 */
	class ReadAndInitEPCTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			haveTaskRunning = true;
			stoped = false;
			int count = 0;

			final int TIMES = 50;// 读取 EPC 的次数

			while (!stoped) {
				mTid = null;
				if (TIMES < ++count) {
					stoped = true;
					mHandler.sendEmptyMessage(BAG_READ_EPC_FAILED);
					break;
				}

				if (null == mUHFOp.readUHFInTime(UHFOperation.MB_TID, 0x03, 6,
						1000, mBqTidInNfc, UHFOperation.MB_TID, 0x03)) {
					continue;
				}
				
				byte[] eventCode = ArrayUtils.hexString2Bytes(mEventCode);
				boolean encrypt = AESCoder.myEncrypt(eventCode, mBqTidInNfc, false);
				LogsUtil.d(TAG, "解密EventCode成功？" + encrypt);
				
				// 封袋的时候在封签事件码嵌入清分信息。 去掉清分信息，还原封签事件码 mEventCode
				eventCode[3] = (byte) (eventCode[3] & 0x0F);
				
				StaticString.bagtidcode = ArrayUtils.bytes2HexString(eventCode);
				
				LogsUtil.d(TAG, "解密前：" + mEventCode);
				LogsUtil.d(TAG, "解密后：" + StaticString.bagtidcode);
				
				StaticString.tid = ArrayUtils.bytes2HexString(mBqTidInNfc);

				mHandler.sendEmptyMessage(BAG_READ_EPC_SUCCESS);
				stoped = true;
				break;
			}
			haveTaskRunning = false;
		}// end run()
	};

	/**
	 * 第3步，发送开袋信息。服务器验证是否可以正确封袋
	 * 
	 * @author Administrator
	 * 
	 */
	class NetOpenTask implements Runnable {
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LogsUtil.d("check barcode NetTask  start");
			StaticString.information = null;// 判断回复信息之间清空之前信息
			SocketConnet.getInstance().communication(799);
			if (!ReplyParser.waitReply()) {
				mHandler.sendEmptyMessage(CHECK_BARCODE_NET_TIMEOUT);
				return;
			}
			LogsUtil.d(TAG, "开袋，返回:" + StaticString.information + "---len="
					+ StaticString.information.length());
			if (pasrePiNumber()) {
				mHandler.sendEmptyMessage(CHECK_FIRST_NET_SUCCESS);
			} else {// 异常回复 02
				mHandler.sendEmptyMessage(CHECK_FIRST_NET_SUCCESS);
				// 网络验证始终通过 2016.11.11
				// mHandler.sendEmptyMessage(CHECK_BARCODE_NET_FAILED); //
				//
			}
		}
	}

	/**
	 * 开袋操作 第三步，改地址0x10标志位f4。
	 * 
	 * @author WriteRFIDTask
	 * 
	 */
	class WriteRFIDTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			LogsUtil.d("write RFID task run");
			stoped = false;
			int count = 0;
			final int TIMES = 100;// 写条码信息的最大次数，超时算写入失败

			while (!stoped) {
				if (TIMES < ++count) {
					stoped = true;
					mHandler.sendEmptyMessage(BAG_WRITE_FLAG_FAILED);
					return;
				}
				byte[] tmpID = mBagOp.getUid();
				if (!ArrayUtils.bytes2HexString(mUid).equals(
						ArrayUtils.bytes2HexString(tmpID))) {
					LogsUtil.d(TAG, "读写卡号不一致");
					SystemClock.sleep(50);
					continue;
				}

				/** 得到交接信息需要写的位置 */
				mExchangeStartAddr = getNextExchangItemAddr(mIndexInfo);
				/** 更新交接目录索引 在当前位置 +3 */
				mIndexInfo[mIndexInfo.length - 1] += (byte) 0x3;

				/** 写目录索引信息到高频卡中 */
				if (!mNfcBagOp.writeIndexInfo(mIndexInfo)) {
					// 失败处理
					continue;
				}
				/** 写交接信息到高频卡中 */
				if (!mNfcBagOp.writeExangeInfo(mExchangeStartAddr,
						ArrayUtils.hexString2Bytes(mExchangeInfo + "D"))) {
					// 失败处理
					continue;

				}

				// 产生随机数
				int tmp = new Random().nextInt(10);
				byte bt = ArrayUtils.getFlagData(mUid, tmp);
				/** 写密钥模式信息到高频卡中 */
				if (!mNfcBagOp.writeModeChoice(ArrayUtils.hexString2Bytes("A"
						+ tmp))) {
					// 失败处理
					continue;
				}

				/** 读电压值 */
				byte mVbuf[];
				if ((mVbuf = mNfcBagOp.readMv()) == null) {
					continue;
				}
				byte mvbyte = mVbuf[3];
				int t = ArrayUtils.byteToInt2(mvbyte);
				LogsUtil.d(TAG, "电压记录值=" + t);
				LogsUtil.d(TAG, "电压记录值2=" + (mVbuf[3] & 0xFF));
				float v = (float) (2.5 * t) / 128;

				/** 开始写标志位信息 */
				if (v <= 2.85) {
					LogsUtil.d(TAG, "电压已经低于2.85了。");
					if (mNfcBagOp
							.writeFlag((byte) ((byte) ArrayUtils.flagData[5 * ((mUid[1] & 0xff) % 10) + 4] ^ bt))) {
						LogsUtil.s(" F5写入标志位到nfc卡成功");
						mHandler.sendEmptyMessage(BAG_WRITE_FLAG_SUCCESS);
						mHandler.sendEmptyMessage(BAG_NOTICE_CHANGE_NEW_BAG);
						break;
					} else {
						continue;
					}
				} else {// 正常电压
					if (mNfcBagOp
							.writeFlag((byte) ((byte) ArrayUtils.flagData[5 * ((mUid[1] & 0xff) % 10) + 3] ^ bt))) {
						LogsUtil.s(" F4写入标志位到nfc卡成功");
						mHandler.sendEmptyMessage(BAG_WRITE_FLAG_SUCCESS);
						break;
					} else {
						continue;
					}
				}
			}// end run()
		}
	};

	/**
	 * 第六步，手持写开袋信息失败，通知服务器
	 * 
	 * @author Administrator
	 * 
	 */
	class NetOpenFailedTask implements Runnable {

		@Override
		public void run() {
			StaticString.information = null;// 判断回复信息之间清空之前信息
			for (int i = 0; i < 2; i++) {
				SocketConnet.getInstance().communication(888,
						new String[] { mLogsIndexNumber });
				if (ReplyParser.waitReply()) {
					break;
				}
			}
			// 此时需要将标志位，复位到F3;
			/** 寻卡操作 */
			byte tmpp[] = null;
			tmpp = mBagOp.getUid();
			if (null == tmpp || (null != tmpp && tmpp.length != 7)) {
				return;
			}
			int tmp = new Random().nextInt(10);
			byte bt = ArrayUtils.getFlagData(mUid, tmp);
			/** 最后写密钥信息 */
			if (!mNfcBagOp.writeModeChoice(ArrayUtils
					.hexString2Bytes("A" + tmp))) {
				return;
			}
			/** 最后写标志位 f2 信息 */
			if (mNfcBagOp
					.writeFlag((byte) ((byte) ArrayUtils.flagData[5 * ((mUid[1] & 0xff) % 10) + 2] ^ bt))) { // 更改标志位
				LogsUtil.d(TAG, "复位标志位到F3");
			} else {
				// 失败处理
				return;
			}

			/** 更新交接目录索引 在当前位置 +3 */
			mIndexInfo[mIndexInfo.length - 1] -= (byte) 0x3;

			/** 写目录索引信息复位到高频卡中 */
			mNfcBagOp.writeIndexInfo(mIndexInfo);
		}
	}
}
