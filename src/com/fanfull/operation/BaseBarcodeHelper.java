package com.fanfull.operation;

import android.os.Handler;
import android.os.SystemClock;

import com.fanfull.contexts.StaticString;
import com.fanfull.hardwareAction.BarCodeOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;

/**
 * @author orsoul
 * @description barcode 扫描类
 *
 */
public abstract class BaseBarcodeHelper {
	private final String TAG = BaseBarcodeHelper.class.getSimpleName();
	public static final int READ_BARCODE_START = 1001;
	public static final int READ_BARCODE_OK = 1002;
	public static final int READ_BARCODE_BAD = 1003;
	public static final int WRONG_BARCODE = 1004;
	public static final int CONN_TIMEOUT = 1005;
	public static final int BARCODE_SCANNED_SHOW = 1006;
	public static final int BARCODE_SCANNED_DISSMISS = 1007;

	public BarCodeOperation mBarCodeOp;
	private ReadBarcodeTask mReadBarcodeTask;
	private Handler mHandler;

	public BaseBarcodeHelper(Handler handler) {
		mHandler = handler;
		if(null == mBarCodeOp)
			mBarCodeOp = BarCodeOperation.getInstance();
		mReadBarcodeTask = new ReadBarcodeTask();
	}
	
	public ReadBarcodeTask getReadbBarcodeTask() {
		return mReadBarcodeTask;
	}
	public void connection() {
		mBarCodeOp.connection();
	}
	public void scan() {
		mBarCodeOp.scan();
	}
	public void stopScan() {
		mBarCodeOp.stopScan();
	}
	public void close() {
		stopScan();
		mBarCodeOp.close();
	}

	public void handleResult(int result) {
		switch (result) {
		case READ_BARCODE_START:
			startScan();
			break;
		case READ_BARCODE_OK:
			handleOK();
			break;
		case READ_BARCODE_BAD:
			handleBad();
			break;
		case WRONG_BARCODE:
			handleWrong();
			break;
		case CONN_TIMEOUT:
			handleTimeout();
			break;
		case BARCODE_SCANNED_SHOW:
			handleSameBarcode();
			break;
		case BARCODE_SCANNED_DISSMISS:
			handleSameBarcodeFinish();
			break;
		}
	}

	/**
	 * 启动扫描
	 */
	public abstract void startScan();

	/**
	 * 处理 正确的 条码
	 */
	public abstract void handleOK();

	/**
	 * 处理 后台服务器反馈的 条码异常
	 */
	public abstract void handleBad();

	/**
	 * 处理 格式错误的 条码
	 */
	public abstract void handleWrong();

	/**
	 * 服务器 响应 超时
	 */
	public abstract void handleTimeout();
	
	/**
	 * 处理 扫到相同的 barcode,一般在此弹出对话框,在handleSameBarcodeFinish()隐藏对话框
	 */
	public abstract void handleSameBarcode();
	
	/**
	 * 在此 隐藏 提示对话框
	 */
	public abstract void handleSameBarcodeFinish();
	/**
	 * @return
	 * @description 服务器校验 通过
	 */
	protected boolean remoteCheckCorrect() {
		return false;
	}
	/**
	 * @return
	 * @description 本地校验 通过
	 */
	protected boolean localCheckCorrect() {
		return false;
	}
	/**
	 * @return
	 * @description 网络校验 通过
	 */
	protected boolean netCheckCorrect() {
		return false;
	}
	
	long timeMark = 0;
	
	public void mainBusiness() {
		byte[] buf = new byte[40];
		int len = mBarCodeOp.hardware.read(mBarCodeOp.fd, buf, buf.length);

		LogsUtil.s("barCode len: " + len);
		LogsUtil.s("barCode Hex:"
				+ ArrayUtils.bytes2HexString(buf));
//				LogsUtil.s("读缓存用时:" + (System.currentTimeMillis() - time));
//				time = System.currentTimeMillis();
		
		String barcode = new String(buf).trim();
		if (barcodeFormatCorrect(barcode)) {// 读到正确的数据 len ==
			// 22(20位条码数据),len=23,38位的条码数据,len=26,24wei
			// mBarCodeOp.hardware.setGPIO(1, 1);// 关闭读头
			// readHeadFlag = false;
			// mBarCodeOp.hardware.DecodeBarcode(buf);//解码

			StaticString.information = null;// 判断回复信息之间清空之前信息

			
//			if (!barcodeFormatCorrect(barcode)) {
//				mHandler.sendEmptyMessage(BaseBarcodeHelper.WRONG_BARCODE);
//				SystemClock.sleep(2000);
//				return;
//			}
			
			StaticString.barcode = barcode;// 去掉空格
			LogsUtil.s("StaticString.barcode:"
					+ StaticString.barcode);// 020400015A21508101516102(24位)

			// 本地校验通过
			if (localCheckCorrect()&& netCheckCorrect()) {
				// 在新线程中 向服务器发送数据
				SocketConnet.getInstance().communication(SendTask.CODE_UPLODE_PACKET);// 上传数据批编号(例如果是封袋操作则conPi
														// =
														// 23;*********)

//						LogsUtil.s("上传数据批编号用时:"
//								+ (System.currentTimeMillis() - time));
//						time = System.currentTimeMillis();

				// 判断 StaticString.information 是否 已赋值
				if (ReplyParser.waitReply()) {

//							LogsUtil.s("判断 StaticString.information用时:"
//									+ (System.currentTimeMillis() - time));
//							time = System.currentTimeMillis();
					// 服务器校验通过
					if (remoteCheckCorrect()) {

//								LogsUtil.s("正常回复pasrePiNumber用时:"
//										+ (System.currentTimeMillis() - time));
//								time = System.currentTimeMillis();

						mHandler.sendEmptyMessage(BaseBarcodeHelper.READ_BARCODE_OK);
						//SystemClock.sleep(200);// 读到正确的数据,推迟点亮读头
					} else {// 异常回复 02
//								LogsUtil.d("BAD_BARCODE 错误的捆数据");
						mHandler.sendEmptyMessage(BaseBarcodeHelper.READ_BARCODE_BAD);
					}
				} else {// 超时回复之后就返回上层 收不到信息就返回上层页面
//							if (2 < ++timeoutCount) {
//								LogsUtil.s("服务器回复超时!");
//								SoundUtils.playFailedSound();
						mHandler.sendEmptyMessage(CONN_TIMEOUT);
//								MyToast.showToastOnUIThread(
//										ScanBunchActivity2.this,
//										"服务器回复超时!", Toast.LENGTH_SHORT);
						SystemClock.sleep(1000);
//								timeoutCount = 0;
//							}
				}
				timeMark = System.currentTimeMillis();

				// SystemClock.sleep(500);
			} else {
				/* 当前 barcode 已被扫描过 */
//						LogsUtil.s("已被扫描");
				if (1500 < System.currentTimeMillis() - timeMark) {
//							SoundUtils.playFailedSound();
//							showSameBarcodeDialog();
					mHandler.sendEmptyMessage(BaseBarcodeHelper.BARCODE_SCANNED_SHOW);
					SystemClock.sleep(2000);
					mHandler.sendEmptyMessage(BaseBarcodeHelper.BARCODE_SCANNED_DISSMISS);
//							dismissSameBarcodeDialog();
					timeMark = System.currentTimeMillis();
					// SystemClock.sleep(1000);
				}
				SystemClock.sleep(50);
			}
		} else {// len != 26 读到异常的数据
//					LogsUtil.s("len != 26 读到异常的数据");
			mHandler.sendEmptyMessage(BaseBarcodeHelper.WRONG_BARCODE);
			SystemClock.sleep(500);
		}
	}
	
	public boolean barcodeFormatCorrect(String barcode) {
		
		if (null == barcode) {
			return false;
		}
		return barcode.matches("^[A-Za-z0-9]{5}[0-9][ABCD][A-Za-z0-9]{18}$");
		
//		String format = barcode.substring(8, 11);
		// 券别: 5A == 100元券
		// 券种: 1 == 未清分完整券; 2 == 已清分完整券; 3 == 未清分残损券; 4 == 已清分残损券
//		return format.matches("5[ABCDE][1234]");
	}
	public class ReadBarcodeTask implements Runnable {

		private final long SCAN_GAS = 50;
		
		private boolean stoped = false;
		
		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			LogsUtil.w(TAG, ReadBarcodeTask.class.getSimpleName() + " run");
//			long timeMark = 0;
			stoped = false;
			int n = 0;
			byte buf[] = new byte[100];
			while ((n = mBarCodeOp.hardware.read(mBarCodeOp.fd, buf, 48)) > 0) {}// clear buf 
			mBarCodeOp.scan();
			SystemClock.sleep(100);
			while (!stoped) {
				// SystemClock.sleep(100);
				if (mBarCodeOp.hardware.select(mBarCodeOp.fd, 1, 0) == 1) {// 当串口存在数据，异步读取数据，修改UI
					// 扫到 捆封签 控制读头停止扫描
					mBarCodeOp.stopScan();
					// 线程休眠, 等待读头将 封签码 解析完成
					SystemClock.sleep(100); //
					mainBusiness();
					stoped = true;
				} else {
					// 控制while循环的执行频率,提高性能
				//	mBarCodeOp.stopScan();
					SystemClock.sleep(SCAN_GAS);
					
				}

			}// end while
			mBarCodeOp.stopScan();
			LogsUtil.w(TAG, ReadBarcodeTask.class.getSimpleName() + " end");
		}// end run
	}

}
