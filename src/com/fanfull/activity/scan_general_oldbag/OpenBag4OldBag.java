package com.fanfull.activity.scan_general_oldbag;

import java.util.Arrays;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfull.background.ActivityUtil;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.entity.OperationBean;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.BarCodeOperation;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.ActivityHeadItemView;
import com.fanfull.view.CoverBagItemView;

/**
 * @Description: 鄂州 开袋
 */
public class OpenBag4OldBag extends BaseActivity {
	private ActivityHeadItemView mTvTitle;
	private TextView mTvPlanAmount;
	private TextView mTvTotalAmount;
	private TextView mTvScanAmount;

	private CoverBagItemView mVReadShow;
	private CoverBagItemView mVEPCShow;
	private CoverBagItemView mVWriteShow;

	private Button mBtnConfirm;
	private Button mBtnCancel;
	private TextView mTvLock;

	private int type = 0;// 当前activity的类型
	private OperationBean BaseInf;// 当前bean

	private final int TIME_OUT = 1;// 条码比对失败
	private final int READ_EPC_SUCCESS = 2;
	private final int READ_EPC_FAILED = 3;
	private final int READ_BARCODE_START = 4;// 开始读条码
	private final int WRONG_BARCODE = 5;// 读到条码数据错误
	private final int READ_BARCODE_BAD = 6;// 条码后台验证错误
	private final int READ_BARCODE_OK = 7;// 读条码成功
	private final int WRITE_EPC_START = 8;
	private final int WRITE_EPC_SUCCESS = 9;
	private final int WRITE_EPC_FAILED = 10;
	private final int SHOW_LOCK_RESULT = 11;
	private final int SHOW_LOCK_FAILED = 12;
	private final int NO_LOCK = 13;

	// 任务进度
	private final int READ_EPC = 1;
	private final int READ_BARCODE = 2;
	private final int WRITE_EPC = 3;
	private final int WRITE_EPC_OVER = 4;
	private int mStep = READ_EPC;

	private String barcode;

	private BarCodeOperation mBarcodeOP;
	private UHFOperation mUHFOp = null;
	ReadBarcodeTask mReadBarcodeTask = new ReadBarcodeTask();
	WriteEPCTask mWriteEPCTask = new WriteEPCTask();

	// byte[] data = new byte[12];
	private byte[] mBarcodeBuf;
	private byte[] mPreEPC; // 记录 未插条码卡的 EPC信息

	private boolean haveTaskRunning;

	
	private boolean isFirstScanend = true;
	private String mTotalFinish;// 总完成数量
	private String mPersonFinish;// 个人完成数量
	private String mPlanNumber;// 个人完成数量
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_bag_old_bag);

		ActivityUtil.getInstance().addActivityToList(this);

		/**/
		Intent intent = getIntent();
		type = intent.getIntExtra(MyContexts.KEY_OPERATION_TYPE, 0);// 收到上个页面发过来的数据.把上个页面的postion传递过来,默认为0(0到4都有可能。点击位置)
		BaseInf = new OperationBean(type);// 得到上个页面点击的位置，并赋值给conTask,conPi,typeName
		LogsUtil.d(TAG, "type : " + type);

		findView();
		
		mBarcodeOP = BarCodeOperation.getInstance();
		mUHFOp = UHFOperation.getInstance();
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
		mTvTitle = (ActivityHeadItemView) findViewById(R.id.v_coverbagactivity_title);
		mTvTitle.setText("开  袋");
		
		mVEPCShow = (CoverBagItemView) findViewById(R.id.v_cover_oldbag_show1);
		mVReadShow = (CoverBagItemView) findViewById(R.id.v_cover_oldbag_show2);
		mVWriteShow = (CoverBagItemView) findViewById(R.id.v_cover_oldbag_show3);
		mVWriteShow.setVisibility(View.VISIBLE);
		
		mTvPlanAmount = (TextView) findViewById(R.id.plan_amount);
		mTvTotalAmount = (TextView) findViewById(R.id.finish_amount);// 完成总数量
		mTvScanAmount = (TextView) findViewById(R.id.person_scan_amount);// 个人扫描总数量

		mBtnConfirm = (Button) findViewById(R.id.btn_ok);// 确认按钮
		mBtnConfirm.setOnClickListener(this);
		mBtnCancel = (Button) findViewById(R.id.btn_cancel);// 取消按钮
		mBtnCancel.setOnClickListener(this);
		mTvLock = (TextView) findViewById(R.id.tv_check_cover_bag_ez_lock); // 结束批
		mTvLock.setOnClickListener(this);
		mTvLock.setEnabled(false);
		
		ViewUtil.requestFocus(mBtnConfirm);
		}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if(SPUtils.getBoolean(OpenBag4ez.this,
//				MyContexts.KEY_SMALL_SRCEEN_ENABLE, false)){
//			OLEDOperation.getInstance().openSpi();
//			new SPIThread(OLEDOperation.fd_spi,OLEDOperation.hardware,0,0,
//					SPIInstore.instore_5,SPIInstore.instore_6).start();
//		}
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_ok:// 扫描操作按键
			mBtnConfirm.setText("扫描");
			if (mStep == READ_EPC) { // 第一次按确定键, 读条码
				initUi ();
				mVEPCShow.setDoing(true);
				mBtnConfirm.setClickable(false);
				ThreadPoolFactory.getNormalPool().execute(mReadEPCTask);
			} else if (mStep == READ_BARCODE) {
				mHandler.sendEmptyMessage(READ_BARCODE_START);
			} else if (mStep == WRITE_EPC) {
				mHandler.sendEmptyMessage(WRITE_EPC_START);
				// ThreadPoolFactory.getNormalPool().execute(mWriteEPCTask);
			} else if (mStep == WRITE_EPC_OVER) {
				finish();
			}
			break;
		case R.id.btn_cancel:// 取消按键
			doBack ();
			break;
		case R.id.tv_check_cover_bag_ez_lock:// 锁定批
			if (!haveTaskRunning) {
				buildLockPi();
			}
			break;
		default:
			break;
		}
	}

	public void buildLockPi() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
				MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
		builder.setPositiveButton("确定",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					StaticString.information = null;
					SocketConnet.getInstance().communication(7);// 锁定批
					ThreadPoolFactory.getNormalPool().execute(
						new Runnable() {
							@Override
							public void run() {
							// 在子线程中 检查 StaticString.information
							// 是否为空
							if (ReplyParser.waitReply()) {
								mHandler.sendEmptyMessage(SHOW_LOCK_RESULT);
							}else {
								mHandler.sendEmptyMessage(SHOW_LOCK_FAILED);
							}
						}
						});
					}
				});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	private void doBack() {
		// TODO Auto-generated method stub
		if (!haveTaskRunning) {
			finish();
		} else if (mStep == READ_EPC) { // 当前处于 读取 EPC 阶段
			mVEPCShow.setDoing(false);
			mReadEPCTask.stop();
			mBtnConfirm.setClickable(true);
			haveTaskRunning = false;
		} else if (mStep == READ_BARCODE) { // 之前 读EPC或写EPC 失败,重新开始 读EPC的 逻辑
			mVReadShow.setDoing(false);
			mReadBarcodeTask.stop();
			mBtnConfirm.setClickable(true);
			haveTaskRunning = false;
		} else if (mStep == WRITE_EPC) {
			mVWriteShow.setDoing(false);
			mWriteEPCTask.stop();
			mBtnConfirm.setClickable(true);
			haveTaskRunning = false;
		}
	}

	/**
	 * 处理数据
	 */
	int j = 0;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case READ_EPC_SUCCESS:

				mStep = READ_BARCODE;
				mVEPCShow.setChecked(true);

				mHandler.sendEmptyMessageDelayed(READ_BARCODE_START,1000);
				// mHandler.sendEmptyMessageDelayed(READ_BARCODE_START, 1000);
				break;
			case NO_LOCK:
				new DialogUtil(OpenBag4OldBag.this).
				showNegativeReplyDialog(MyContexts.DIALOG_MESSAGE_EPC_NO_MATCH,MyContexts.TEXT_OK);
			case READ_EPC_FAILED:// 6
				SoundUtils.playFailedSound();
				mVEPCShow.setDoing(false);
				mBtnConfirm.setClickable(true);
				haveTaskRunning = false;
				break;
			case READ_BARCODE_START:// 第一步读条码操作 1
				// operation_state.setText("读条码操作...");
				// if (isDoing) {
				// return;
				// }
				mBtnConfirm.setClickable(false);

				BaseInf.setStateOperation(1);
//				mBarcodeOP.connection();// 初始化摄像头
				// mBarcodeOP.scan(); // 打开摄像头开关

				mVReadShow.setDoing(true);
				ThreadPoolFactory.getNormalPool().execute(mReadBarcodeTask);
				break;
			
			case WRONG_BARCODE:// 21
				mBarcodeOP.stopScan();
				mVReadShow.setDoing(false);
				mBtnConfirm.setClickable(true);
				mBtnConfirm.setText("重新扫描");
				SoundUtils.playFailedSound();
				new DialogUtil(OpenBag4OldBag.this).
				showNegativeReplyDialog(MyContexts.DIALOG_MESSAGE_BARCODE_WRONG,MyContexts.TEXT_OK);
				mStep = READ_EPC;
				break;
			case READ_BARCODE_BAD: // 31 后台回复错误
				LogsUtil.s("READ_BARCODE_BAD");
				haveTaskRunning = false;
				SoundUtils.playFailedSound();
				new DialogUtil(OpenBag4OldBag.this).
				showNegativeReplyDialog(MyContexts.DIALOG_MESSAGE_BARCODE_WRONG,MyContexts.TEXT_OK);
				mVWriteShow.setDoing(false);
				mBtnConfirm.setClickable(true);
				mBtnConfirm.setText("重新扫描");
				// 这里有个小bug，dialog应该在Activity之前先关闭，这里没有关闭，有时会在后台报窗体泄露
				// TipDialog tip1 = new TipDialog();
				// tip1.crateDialog(CoverBagActivity.this, 0);
				BaseInf.setStateOperation(0); // 后台判定重新操作
				mStep = READ_EPC;
				break;
			case TIME_OUT:
				Toast.makeText(OpenBag4OldBag.this, "等待时间过长", 0).show();
				// MyToast.closeWaiting();
				break;
			case READ_BARCODE_OK:// 11
				LogsUtil.s("READ_BARCODE_OK");
				mReadBarcodeTask.stop();
				mStep = WRITE_EPC;
				mVReadShow.setChecked(true);
				BaseInf.setStateOperation(2);// 记录当前状态
				// new Thread(mReadUHFTask).start(); // 寻找900M高频卡
				mBarcodeOP.close();
				mHandler.sendEmptyMessageDelayed(WRITE_EPC_START, 2000);
				break;
			case WRITE_EPC_START:
				mVWriteShow.setDoing(true);
				mBtnConfirm.setClickable(false);
				haveTaskRunning = true;
				ThreadPoolFactory.getNormalPool().execute(mWriteEPCTask);
				break;
			// case EPC_NO_MATCH:
			// showDialog(DIALOG_EPC_NO_MATCH);
			case WRITE_EPC_FAILED:
				LogsUtil.s("WRITE_EPC_FAILED");
				haveTaskRunning = false;
				mVWriteShow.setDoing(false);
				mBtnConfirm.setClickable(true);
				mBtnConfirm.setText("重新扫描");
				break;
			case WRITE_EPC_SUCCESS:// 111
				LogsUtil.s("WRITE_EPC_SUCCESS");
				// playNumber(String.valueOf(Integer.parseInt(mPersonFinish)));
				SoundUtils.playNumber(mPersonFinish);

				haveTaskRunning = false;
				mVWriteShow.setChecked(true);
				mBtnConfirm.setClickable(true);
				mTvLock.setEnabled(true);
				mStep = WRITE_EPC_OVER;

				//
				mTvScanAmount.setText(mPersonFinish.replaceFirst("^0+", ""));// 去掉
				// 数字前面的
				// '0'
				mTvTotalAmount.setText(mTotalFinish.replaceFirst("^0+", ""));
				mTvPlanAmount.setText(mPlanNumber.replaceFirst("^0+", ""));
				
				if(!mTvTotalAmount.toString().equals(mPlanNumber.toString())){
					mBtnConfirm.setText("继续扫描");
					//扫描完一个后关闭显示屏
					if(isFirstScanend){
						if(SPUtils.getBoolean(OpenBag4OldBag.this,
								MyContexts.KEY_SMALL_SRCEEN_ENABLE, false)){
							OLEDOperation.getInstance().close();
						}
						isFirstScanend = false;
					}
				}else{
					mBtnConfirm.setText("封袋完成");
//					if(SPUtils.getBoolean(OpenBag4ez.this,
//							MyContexts.KEY_SMALL_SRCEEN_ENABLE, false)){
//						OLEDOperation.getInstance().openSpi();
//						new SPIThread(OLEDOperation.fd_spi,OLEDOperation.hardware,0,0,SPIInstore.instore_8).start();
//					}
				}
					
				
				mBtnCancel.setText("退出");
				BaseInf.setStateOperation(0);// 记录当前状态
				
				break;
				
			case SHOW_LOCK_RESULT:// 111
				haveTaskRunning = false;
				final String info = ReplyParser.parseReply(StaticString.information);
				if("锁定批成功".equals(info)){
					Drawable nav_up=getResources().getDrawable(R.drawable.lock);
					nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
					mTvLock.setCompoundDrawables(null, nav_up,null , null);
					mTvLock.setText("已锁定");
					mTvLock.setEnabled(false);
					new TipDialog().createDialog(OpenBag4OldBag.this,1);
				}else {
					new TipDialog().createDialog(OpenBag4OldBag.this,0);
				}
				break;
			case SHOW_LOCK_FAILED:
				new DialogUtil(OpenBag4OldBag.this).
				showPostiveReplyDialog(OpenBag4OldBag.this,MyContexts.LOCK_PI_TIMEOUT);
				break;
			}
		}

	};
	private void initUi (){
		mVReadShow.setChecked(false);
		mVEPCShow.setChecked(false);
		mVWriteShow.setChecked(false);
	}
//	byte[] result = null;// 加密byte 返回8个字节
	byte[] front = new byte[4];//

	public Boolean pasrePiNumber() {
		boolean retVal = false;
		LogsUtil.d("pasrePiNumber", "pasrePiNumber information:" + StaticString.information);
		// int info = Integer.valueOf(StaticString.information.substring(4,
		// 6));//查询消息的返回值的第4,5位是否包括8，或者7\
		String info = null;
		if (StaticString.information.length() < 20) {
			LogsUtil.d("pasrePiNumber", "information反馈错误");
			info = StaticString.information.substring(
					StaticString.information.indexOf(" "),
					StaticString.information.lastIndexOf(" ")).trim();
			// 将回复信息处理截取第一个空格到最后一个空格之间的数据
		} else {
			info = StaticString.information.substring(
					StaticString.information.length() - 25,
					StaticString.information.lastIndexOf(" ")).trim();
			// 查询消息的返回值的第4,5位是否包括8，或者7，返回20位数据
			String first2 = info.substring(0, 4) + "00" + "00";
			String str = info.substring(4, info.length());
			try {
				front = ArrayUtils.hexString2Bytes(first2);
				// 前面2位返回信息前4位按16进制写成2位，后面4位为0
				LogsUtil.d("front.length",
						front.length + " front:" + first2);
				// result = DES.encrypt(DES.hexStr2ByteArr(str),epcTid);//加密
//				result = ArrayUtils.hexStringToBytes(str); // 不加密 陈鑫 2015-8-25 21:27:25
//													// modify
//				LogsUtil.d("pasrePiNumber",
//						result.length + "加密的内容:" + (str));

				LogsUtil.d("pasrePiNumber", "info.length " + info.length());// 20位数据

				String index = StaticString.information.substring(
						StaticString.information.indexOf(" "),
						StaticString.information.lastIndexOf(" ")).trim();// 将回复信息处理

				//information: *02 08E28 01105 20004 31600 0000F F0871 01001 00000 30701 15070 71620 20000 01001 60223 16120 70001 00019999004 003#
				mPersonFinish = index.substring(76,80);// 个人完成数量
						
				mTotalFinish = index.substring(80,84);// 总完成数量
						
				mPlanNumber = index.substring(84,88);// 计划数量
				LogsUtil.d(TAG, "information: " + StaticString.information);
				LogsUtil.d(TAG, "mTotalFinish: " + mTotalFinish);
				LogsUtil.d(TAG, "mPersonFinish: " + mPersonFinish);
				BaseInf.setPfinishNumber(mPersonFinish);
				BaseInf.setFinishNumber(mTotalFinish);
				// playNumber(String.valueOf(Integer.parseInt(pfinish)));
				retVal = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		LogsUtil.d("pasrePiNumber", "info:" + info);

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
		case KeyEvent.KEYCODE_HOME:
			doBack();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
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
		// Intent intent = new Intent(CoverBagActivity.this,
		// GeneralActivity.class);
		// startActivity(intent);
		finish();
	}

	/**
	 * 开启线程监听串口 取出条码读头数据
	 */
	class ReadBarcodeTask implements Runnable {
		private boolean stoped;
		public void stop() {
			stoped = true;
		}

		public void run() {
			mBarcodeOP.connection();//重新打开串口，GPIO上电
			stoped = false;
			haveTaskRunning = true;
			int n = 0;
			byte buf[] = new byte[100];
			while ((n = mBarcodeOP.hardware.read(mBarcodeOP.fd, buf, 48)) > 0) ;// clear buf 
			mBarcodeOP.scan();
			SystemClock.sleep(200);
			while (!stoped) {
			
				if (mBarcodeOP.hardware.select(mBarcodeOP.fd, 0, 200) == 1) {// 当串口存在数据，异步读取数据，修改UI
					SystemClock.sleep(100);
					

					mBarcodeBuf = new byte[40];
					int len = mBarcodeOP.hardware.read(mBarcodeOP.fd,
							mBarcodeBuf, 40);

					if (len == 23) {// 读到正确的数据
						mBarcodeOP.hardware.setGPIO(1, 1);// 关闭读头
						LogsUtil.s("barcode解码前hex:"
								+ ArrayUtils.bytes2HexString(mBarcodeBuf));
						mBarcodeOP.hardware.DecodeBarcode(mBarcodeBuf);// 解码
						LogsUtil.s("barcode解码后hex:"
								+ ArrayUtils.bytes2HexString(mBarcodeBuf));
						barcode = new String(mBarcodeBuf); // 把摄像头扫到的内容转成String类型(之前是byte[])...................

						LogsUtil.s("barcode String:" + barcode);

						StaticString.barcode = barcode.trim();// 去掉空格
//						bagop.setBarValue(StaticString.barcode);

						StaticString.bagid = ArrayUtils
								.bytes2HexString(mUHFOp.mEPC);
						stoped = true;
						mBarcodeOP.stopScan();
						mHandler.sendEmptyMessage(READ_BARCODE_OK);
					} else {// 读到异常的数据
						if(ArrayUtils.bytes2HexString(mBarcodeBuf).startsWith("FF")){
							LogsUtil.s("barcode错误:"+ArrayUtils.bytes2HexString(mBarcodeBuf));
							continue;
						}
						
						LogsUtil.s("barcode错误:"+ArrayUtils.bytes2HexString(mBarcodeBuf));
						mBarcodeOP.stopScan();
						stoped = true;
						mHandler.sendEmptyMessage(WRONG_BARCODE);
						//SystemClock.sleep(200);
					}
					// break;// 当有数据时，跳出循环
				} else {
					Log.v(TAG, "---don't hava data---");
				}
				SystemClock.sleep(50);
			//	mBarcodeOP.stopScan();
			}// end while()
			while ((n = mBarcodeOP.hardware.read(mBarcodeOP.fd, buf, 48)) > 0) ;// clear buf 
			mBarcodeOP.close();//关闭GPIO，关闭串口，关闭条码模块
			mBarcodeOP.stopScan();
			haveTaskRunning = false;
			LogsUtil.s("barcodeTask end");
		}
	};

	/**
	 * 
	 * @ClassName: writeBag
	 * @Description:写入基金袋条码
	 * @author Keung
	 * @date 2015-3-17 下午02:30:31
	 * 
	 */

	ReadEPCTask mReadEPCTask = new ReadEPCTask();

	class ReadEPCTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}

		@Override
		public void run() {
			LogsUtil.d("Read EPC task run");
			haveTaskRunning = true;
			stoped = false;
			int count = 0;
			final int TIMES = 40;// 读取 EPC 的次数
			final int GAS = 50; // 读取 EPC 间隔,单位 毫秒

			while (!stoped) {

				if (mUHFOp.findOne()) {// 单次读取EPC

					// mPreEPC = Arrays.copyOf(mUHFOp.mEPC, mUHFOp.mEPC.length);
//					mPreEPC = mUHFOp.mEPC;

					byte[] tid = mUHFOp.readUHF(mUHFOp.mEPC, 1, 0x20, 2, 0, 8);

					if (null == tid) {
						continue;
					}

					String epcStr = ArrayUtils.bytes2HexString(mUHFOp.mEPC);
					String tidStr = ArrayUtils.bytes2HexString(tid);

					if (!epcStr.contains(tidStr) || !epcStr.endsWith("FF")) {
						mHandler.sendEmptyMessage(NO_LOCK);
						return;
					}

					// LogsUtil.v(
					// TAG,
					// "Unlock EPC: "
					// + StringUtils.bytesToHexString(mUHFOp.mEPC));
					// LogsUtil.v(
					// TAG,
					// " Lock  EPC: "
					// + StringUtils.bytesToHexString(mPreEPC));
					mHandler.sendEmptyMessage(READ_EPC_SUCCESS);
					break;
				} else {
					SystemClock.sleep(GAS);
				}

				// TIMES * GAS 毫秒后停止 读取 EPC
				if (TIMES < count++) {
					mHandler.sendEmptyMessage(READ_EPC_FAILED);
					break;
				}

			} // end while()

			LogsUtil.d("Read EPC task end");
			haveTaskRunning = false;
		}// end run()
	};

	class WriteEPCTask implements Runnable {
		private boolean stoped;
		private final int TIMES = 50;// 读取epc的次数
		private final int GAS = 50;// 读取epc的时间隔，毫秒

		public void stop() {
			stoped = true;
		}

		@Override
		public void run() {
		
			stoped = false;
			int count = 0;

			byte[] data = new byte[12];

			data[0] = (byte) 0xEE;
			data[1] = (byte) 0xEE;
			data[2] = front[0];
			data[3] = front[1];
			
			for (int i = 4; i < mUHFOp.mEPC.length; i++) {
				data[i] = mUHFOp.mEPC[i];
			}
			
			/* 写入 EPC */
			count = 0;
			
			while (!stoped) {
				if (TIMES < count++) {
					mHandler.sendEmptyMessage(WRITE_EPC_FAILED);
					break;
				}
				if (mUHFOp.writeUHF(mUHFOp.mEPC, Arrays.copyOfRange(mBarcodeBuf, 0,32), 1, 0x20, 3, 0x00)) {
					data = ArrayUtils.concatArray(Arrays.copyOfRange(mBarcodeBuf, 32,38), mUHFOp.mEPC);
					if (mUHFOp.writeUHF(mUHFOp.mEPC, data, 1, 0x20, 3, 0x10)) {
					StaticString.information = null;// 判断回复信息之间清空之前信息
					SocketConnet.getInstance().communication(52);// 上传数据批编号(例如果是开袋操作则conPi
					
					if (ReplyParser.waitReply()) {

						if (pasrePiNumber()) {
							mHandler.sendEmptyMessage(WRITE_EPC_SUCCESS);
							return;
						} else {// 异常回复 02
							mHandler.sendEmptyMessage(READ_BARCODE_BAD);
							return;
						}
					} else {// 超时回复之后就返回上层 收不到信息就返回上层页面
						mHandler.sendEmptyMessage(TIME_OUT);
					}
					break;
					} else {
						SystemClock.sleep(GAS);
					}
				} else {
					SystemClock.sleep(GAS);
				}
			}
		}
	};
	public void onClickBack(View v) {
		doBack();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogsUtil.d(TAG, "onDestroy");
//		bagop.close();
		mStep = WRITE_EPC_OVER;
//		if(null != mBarcodeOP){
//			mBarcodeOP.close();
//		}
		
		if(null != mUHFOp){
			LogsUtil.d(TAG, "--close--uhf---");
			mUHFOp.close();
		}
		if(SPUtils.getBoolean(OpenBag4OldBag.this,
				MyContexts.KEY_SMALL_SRCEEN_ENABLE, false)){
			OLEDOperation.getInstance().close();
		}
		ActivityUtil.getInstance().removeActivityFromList(this);
	}
}
