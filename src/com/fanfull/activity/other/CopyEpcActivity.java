package com.fanfull.activity.other;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.operation.BagOperation;
import com.fanfull.operation.NFCBagOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.view.ActivityHeadItemView;
import com.fanfull.view.CoverBagItemView;

public class CopyEpcActivity extends BaseActivity{
	private final String TAG = CopyEpcActivity.class.getSimpleName();

	private BagOperation mBagOp;
	private NFCBagOperation mNfcBagOp;
	private UHFOperation mUHFOp;
	private byte mUid [];
	
	private ReadRFIDTask mReadRFIDTask;
	private ReadEPCTask mReadEPCTask; // 读 EPC 任务
	
	private CoverBagItemView mVReadBagLock;
	private CoverBagItemView mVReadUhfLock;
	private CoverBagItemView mVWriteUhfLock;
	
	private Button mBtnConfirm;
	private Button mBtnCancel;
	
	private final static int GET_BAG_ID = 1;
	private final static int READ_RFID_SUCCESS = 2;
	private final static int READ_RFID_FAILED = 3;

	private final static int INIT_EPC = 4;
	private final static int INIT_EPC_SUCCESS = 5;
	private final static int INIT_EPC_FAILED = 6;

	private final static int READ_EPC = 7;
	private final static int READ_EPC_SUCCESS = 8;
	private final static int READ_EPC_FAILED = 9;
	
	private final static int READ_BAGLOCK_START = 10;
	private final static int READ_UHF_START = 11;
	
	private final int BAG_NO_INIT = 4;
	private final int BAG_HAD_INIT = 5;
	
	private boolean haveTaskRunning = false;
	private final int READ_BAGLOACK = 1;
	private final int READ_UHF = 2;
	
	private int mStep = READ_BAGLOACK;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_iostore_bag_new);
		
		mUHFOp = UHFOperation.getInstance();;
		mBagOp = BagOperation.getInstance();
		mReadRFIDTask = new ReadRFIDTask();
		mReadEPCTask = new ReadEPCTask();
		
		findViewById(R.id.v_saomiao_num).setVisibility(View.GONE);
		ActivityHeadItemView title = (ActivityHeadItemView)findViewById(R.id.v_coverbagactivity_title);
		title.setText("复制EPC");
		findViewById(R.id.v_new_coverbag_read_barcode).setVisibility(View.GONE);
		mVReadBagLock = (CoverBagItemView) findViewById(R.id.v_new_coverbag_read_bagLock);
		mVReadBagLock.setText("袋码        ");
		mVReadUhfLock = (CoverBagItemView) findViewById(R.id.v_new_coverbag_write_bagLock);
		mVReadUhfLock.setText("标签码    ");
		mVWriteUhfLock = (CoverBagItemView) findViewById(R.id.v_new_coverbag_update_uhf);
		mVWriteUhfLock.setText("复制信息");
		mVWriteUhfLock.setVisibility(View.GONE);
		findViewById(R.id.v_new_coverbag_update_uhf).setVisibility(View.GONE);
		findViewById(R.id.v_new_coverbag_net_check).setVisibility(View.GONE);
		
		// 确认按钮
		mBtnConfirm = (Button) findViewById(R.id.btn_ok);
		mBtnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				initUi();
				mHandler.sendEmptyMessage(READ_BAGLOCK_START);
			}
		});

		// 取消按钮
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (haveTaskRunning) {
					if(mStep == READ_BAGLOACK){
						mReadRFIDTask.stop();
						mHandler.sendEmptyMessage(READ_RFID_FAILED);
					}else {
						mReadEPCTask.stop();
						mHandler.sendEmptyMessage(READ_EPC_FAILED);
					}
				}else {
					finish();
				}
			}
		});
	}
	
	
	public void onClickBack(View v) {
		mBtnCancel.performClick();
	}
    /**
	 * 读 RFID卡 取得 袋ID
	 * 
	 */
	class ReadRFIDTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		public void run() {
			final int TIMES = 200;// 读取 袋锁的次数
			int count = 0;
			stoped = false;

			while (!stoped) {
				if (TIMES < ++count) {
					mHandler.sendEmptyMessage(READ_RFID_FAILED);
					stoped = true;
					return;
				}
				mUid = mBagOp.getUid();
				if (null != mUid && mUid.length == 7) {
					mNfcBagOp = mBagOp.getNfcBagOperation();
				} else {
					// 读卡失败
					SystemClock.sleep(20);
					continue;
				}
				

				int j = -1;
				j = (null == mNfcBagOp ? -1 : mNfcBagOp.readBagID());
				if (j == BAG_NO_INIT) {
					// 未初始化
					mHandler.sendEmptyMessage(BAG_NO_INIT);
					stoped = true;
					return;
				}else if(j == BAG_HAD_INIT) {
					mHandler.sendEmptyMessage(READ_RFID_SUCCESS);
					stoped = true;
					return;
				}else {
					SystemClock.sleep(20);
					continue;
				}
					
				}
		}
	};
	
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
				ThreadPoolFactory.getNormalPool().execute(mReadRFIDTask);
				break;

			case READ_RFID_SUCCESS:
				LogsUtil.s("READ_BAG_SUCCESS");
				mVReadBagLock.setChecked(true);
				mReadRFIDTask.stop();
				mHandler.sendEmptyMessage(READ_UHF_START);
				break;
				
			case READ_RFID_FAILED:
				mVReadBagLock.setDoing(false);
				haveTaskRunning = false;
				mReadRFIDTask.stop();
				mBtnConfirm.setEnabled(true);
				break;

			/** 读Tid操作 */
			case READ_UHF_START:// 读tid
				mStep = READ_UHF;
				mVReadUhfLock.setDoing(true);
				haveTaskRunning = true;
				mBtnConfirm.setEnabled(false);
				ThreadPoolFactory.getNormalPool().execute(mReadEPCTask);
				break;

			case READ_EPC_SUCCESS:
				LogsUtil.s("BAG_INIT_EPC_SUCCESS");
				mReadEPCTask.stop();
				haveTaskRunning = false;
				mVReadUhfLock.setChecked(true);
				break;

			case READ_EPC_FAILED:
				mVReadUhfLock.setDoing(false);
				haveTaskRunning = false;
				mReadEPCTask.stop();
				mBtnConfirm.setEnabled(true);
				break;
			}

		}

	};

	/**
	 * 界面过程显示恢复到初始状态
	 */
	private void initUi() {
		mVReadBagLock.setChecked(false);
		mVReadUhfLock.setChecked(false);
		mBtnConfirm.setText("扫描");
		mStep = READ_BAGLOACK;

	}
	
	/**
	 * 超高频读取EPC线程
	 */
	class ReadEPCTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			LogsUtil.d("Read EPC task run");
			stoped = false;
			int count = 0;
			final int TIMES = 100;// 读取 EPC 的次数
			final int GAS = 50; // 读取 EPC 间隔,单位 毫秒

			setpower (6,15);
			while (!stoped) {
				if (TIMES < count++) {
					mHandler.sendEmptyMessage(READ_EPC_FAILED);
					stoped = true;
					return;
				}
				if (!mUHFOp.findOne() && !ArrayUtils.bytes2HexString(mUHFOp.mEPC).startsWith("E20")) {// 单次读取EPC
					SystemClock.sleep(20);
					continue;
				}

				if ( mUHFOp.writeUHF(mUHFOp.mEPC,ArrayUtils
						.hexString2Bytes(StaticString.bagid),1, 0x20, 1, 0x02)) {
					byte tmpByte[] = null;
					tmpByte = mUHFOp.readUHF(new byte[0], 1,0x20, 1, 0x02, 12);
					if(StaticString.bagid.equals(ArrayUtils.bytes2HexString(tmpByte))){
						LogsUtil.d(TAG, "写入的袋ID和读出的一致");
						// 读出验证，是否写入成功
						mHandler.sendEmptyMessage(READ_EPC_SUCCESS);
						stoped = true;
						return;
					}else {
						continue;
					}
				} else {
					LogsUtil.d(TAG, "拷贝袋ID到UHF中失败！");
					SystemClock.sleep(10);
					continue;
				}
			} // end while()
			haveTaskRunning = false;
		}// end run()
	};
	
	private boolean setpower (int n,int m){
		/**设置初始化前的功率*/
		int dReadPower = SPUtils.getInt(getApplicationContext(),
				MyContexts.KEY_POWER_READ_INSTORE, n);
		int dWritePower = SPUtils.getInt(getApplicationContext(),
				MyContexts.KEY_POWER_WRITE_INSTORE, m);
		if (mUHFOp.setPower(dReadPower, dWritePower)) {
			LogsUtil.s("初始前 设置 功率成功");
			return true;
		} else {
			LogsUtil.s("初始前 设置 功率失败");
			return false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mBagOp = null;
		if(null != mUHFOp){
			LogsUtil.d(TAG, "--close--uhf---");
			mUHFOp.close();
		}
	}
}
