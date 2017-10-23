package com.fanfull.activity.scan_general;

import java.util.Arrays;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.op.RFIDOperation;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.view.CoverBagItemView;

/**
 * 
 * 换袋页面
 */
public class ChangeBagActivity extends BaseActivity {

	private final int MSG_SELECT_CHANGE_BAG = 1;
	private final int MSG_READ_NFC_FAILED = 2;
	private final int MSG_CHECK_NFC_FAILED = 3;
	private final int MSG_CHECK_NFC_SUCCESS = 4;

	private final int STEP_CHECK_LOCK = 1;
	private int mStep = STEP_CHECK_LOCK;

	private ReadRFIDTask mReadRFIDTask;

	/** nfc（04-07）4个地址内容，16byte */
	private byte[] nfcData04_07;
	/** nfc（10-18）9个地址内容，36byte */
	private byte[] nfcData10_18;
	/** nfc（30-3F）16个地址内容，64byte */
	private byte[] nfcData30_3F;

	private CoverBagItemView mVshow1;
	private CoverBagItemView mVshow2;
	private CoverBagItemView mVshow3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDiaUtil = new DialogUtil(this);
		mHandler = new Handler(new MyHandlerCallback());

		mReadRFIDTask = new ReadRFIDTask();

		RFIDOperation.getInstance().openTemp(false);

		// mRecieveListener = new MyRecieverListener();
		// SocketConnet.getInstance().setRecieveListener(mRecieveListener);
		//
		// SocketConnet.getInstance().communication(SendTask.CODE_CHANGE_BAG);
		// mDiaUtil.showProgressDialog();

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_change_bag4new);

		mVshow1 = (CoverBagItemView) findViewById(R.id.v_show1);
		mVshow2 = (CoverBagItemView) findViewById(R.id.v_show2);
		mVshow3 = (CoverBagItemView) findViewById(R.id.v_show3);

		findViewById(R.id.btn_ok).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);

	}

	private void reSetView() {
		mVshow1.setChecked(false);
		mVshow2.setChecked(false);
		mVshow3.setChecked(false);
	}

	private void checkNfc(byte[] data10_18, byte[] uid) {
		if (null == uid || uid.length < 7 || null == data10_18
				|| data10_18.length < 36) {
			return;
		}

		LogsUtil.d(TAG, "addr10:" + ArrayUtils.bytes2HexString(data10_18));

		/* 获取 密钥 */
		int i = (14 - 10) << 2;// 密钥编号 存在 NFC0x14地址 第1个byte
		int flag = decodeFlag(data10_18[0], data10_18[i], uid);

		// int miyueNum = data10_18[i] & 0x0F; // 密钥 编号
		// byte miyue = ArrayUtils.getFlagData(uid, miyueNum);// 密钥
		// LogsUtil.d(TAG, "密钥编号：" + miyueNum);
		// LogsUtil.d(TAG, "密钥：" + miyue);
		// LogsUtil.d(TAG, "加密标志位：" + data10_18[0]);
		// int flag = -1;
		// int x = miyue ^ data10_18[0];
		// int x2 = (uid[1] & 0xFF) % 10;
		// for (i = 1; i < 6; i++) {
		// if (x == ArrayUtils.flagData[5 * x2 + i]) {
		// flag = i;
		// break;
		// }
		// }
		LogsUtil.d(TAG, "标志位：F" + flag);

		/* 电压 */
		i = ((17 - 10) << 2) + 3;// 电压 存在 NFC0x17地址 第4个byte
		int v = (data10_18[i] & 0xFF);
		LogsUtil.d(TAG, "电压记录值=" + v);
		LogsUtil.d(TAG, "电压记录值=" + (2.5 * v / 128));
	}

	/**
	 * 获取明文 标志位
	 * 
	 * @param flag
	 *            加密的标志位;位于NFC地址0x10第1个byte
	 * @param miyueNum
	 *            加密的密钥编号;位于NFC地址0x14第1个byte
	 * @param uid
	 *            NFC的uid，用于解密
	 * @return 1：F1；2：F2 ... 5：F5。 -1：解密失败
	 */
	private int decodeFlag(byte flag, byte miyueNum, byte[] uid) {

		miyueNum = (byte) (miyueNum & 0x0F); // 解密 密钥 编号
		byte miyue = ArrayUtils.getFlagData(uid, miyueNum);// 产生 密钥

		LogsUtil.d(TAG, "加密标志位：" + flag);
		LogsUtil.d(TAG, "明文密钥编号：" + miyueNum);
		LogsUtil.d(TAG, "密钥：" + miyue);

		int reVal = -1;
		int x1 = miyue ^ flag;
		int x2 = (uid[1] & 0xFF) % 10;
		for (int i = 0; i < 5; i++) {
			if (x1 == ArrayUtils.flagData[x2 * 5 + i]) {
				reVal = i + 1;
				break;
			}
		}
		return reVal;
	}

	private void checkNfc2(byte[] data04_08) {
		if (null == data04_08 || data04_08.length < 20) {
			return;
		}
		LogsUtil.d(TAG,
				"ncf bagid:" + ArrayUtils.bytes2HexString(data04_08, 0, 12));
		LogsUtil.d(
				TAG,
				"ncf tid:"
						+ ArrayUtils.bytes2HexString(data04_08, 12,
								data04_08.length));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			if (STEP_CHECK_LOCK == mStep) {
			}
			break;
		case R.id.btn_cancel:

			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		RFIDOperation.getInstance().close();
		super.onDestroy();
	}

	private class MyHandlerCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			mDiaUtil.dismissProgressDialog();
			switch (msg.what) {
			case MSG_SELECT_CHANGE_BAG:
				mReadRFIDTask = new ReadRFIDTask();
				// nfcData04_07 = new byte[16];
				// nfcData10_18 = new byte[36];
				break;
			case MSG_CHECK_NFC_SUCCESS:
				break;
			case MSG_CHECK_NFC_FAILED:
				if (3 == msg.arg1) {// 标志位为F3，提示先开袋
				}
				break;
			case MSG_READ_NFC_FAILED:
				SoundUtils.playFailedSound();
				mDiaUtil.showDialog(msg.obj);
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class MyRecieverListener extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {
			mHandler.sendEmptyMessage(MSG_SELECT_CHANGE_BAG);
		}

		@Override
		public void onTimeout() {
		}
	}

	private class ReadRFIDTask implements Runnable {
		private final long RUNTIME = 5000;

		@Override
		public void run() {
			LogsUtil.d(TAG, ReadRFIDTask.class.getSimpleName() + " run");

			long startTime = System.currentTimeMillis();
			
			long runTime = RUNTIME - (System.currentTimeMillis() - startTime);// 剩余运行时间
			
			Message msg = mHandler.obtainMessage();
			
			/* 1, 检查袋锁 */
			if (null == nfcData10_18) {
				nfcData10_18 = new byte[36];
			}
			Arrays.fill(nfcData10_18, (byte) 0x00);
			boolean readSucces = RFIDOperation.getInstance().readNFCInTime(
					0x10, nfcData10_18, runTime, null);
			if (!readSucces) {
				msg.what = MSG_READ_NFC_FAILED;
				msg.obj = "读标志位失败";
				mHandler.sendMessage(msg);
				return;
			}

			int i = (14 - 10) << 2;// 密钥编号 存在 NFC0x14地址 第1个byte
			int flag = decodeFlag(nfcData10_18[0], nfcData10_18[i],
					RFIDOperation.sLastUid);
			if (3 == flag) {// F3, 未开袋

			} else if (4 == flag) {// F4, 已开袋

			} else {
				msg.what = MSG_CHECK_NFC_FAILED;
				msg.arg1 = flag;
				mHandler.sendMessage(msg);
			}
			
			/* 2, 检查袋锁通过，读NFC封签事件码 */
			if (null == nfcData30_3F) {
				nfcData30_3F = new byte[64];
			}
			Arrays.fill(nfcData30_3F, (byte) 0x00);
			runTime = RUNTIME - (System.currentTimeMillis() - startTime);
			readSucces = RFIDOperation.getInstance().readNFCInTime(
					0x30, nfcData30_3F, runTime, null);
			
			if (!readSucces) {
				msg.what = MSG_READ_NFC_FAILED;
				msg.obj = "读封签事件码失败";
				mHandler.sendMessage(msg);
				return;
			}
			msg.what = MSG_CHECK_NFC_SUCCESS;
			mHandler.sendMessage(msg);
			
			LogsUtil.d(TAG, ReadRFIDTask.class.getSimpleName() + " run finish");
		}// end run()

	}
}
