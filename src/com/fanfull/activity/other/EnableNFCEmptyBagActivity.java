package com.fanfull.activity.other;



import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fanfull.background.ActivityUtil;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.fff.R.color;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.hardwareAction.RFIDOperation;
import com.fanfull.operation.BagOperation;
import com.fanfull.operation.NFCBagOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ViewUtil;
import com.orsoul.view.MyProgressBar;
import com.orsoul.view.NumberInputer;

public class EnableNFCEmptyBagActivity extends BaseActivity{
	private final static String TAG = EnableNFCEmptyBagActivity.class.getSimpleName();
		private final static int SHOW_LOCK_RESULT = 10;


		private boolean haveTaskRunning = false;// 扫捆线程是否在进行
		

		private final int SAME_BARCODE_DIALOG = 1;
		private final int WRONG_BARCODE_DIALOG = 3;
		private final int OVER_LOCK_DIALOG = 4;
		private final int CHANGE_NUMBER_DIALOG = 5;

		private TextView mTvLock;
		private TextView mTvPlan;
		private TextView mTvReal;
		private MyProgressBar mProgreeBar;
		private Button mBtnStart;


		private int mPlanScanNum;
		private TextView mLockTextView;

		
		private ReadBagTask mReadBagTask;
		private BagOperation mBagOp = null;
		private NFCBagOperation mNfcBagOp = null;
		private byte mUid [] = null;
		
		private final static int READ_BAG_FAILED = 0x1000;
		private final static int BAG_MATCH =       5;
		private final static int READ_BAG_SUCCESS= 0x1003;
		private final static int BAG_NO_INIT =     4;//版本不匹配
		
		private final static int BAG_ENABLE_LOCAL_UNENABLE=0;//该袋已经注销
		private final static int BAG_ENABLE_LOCAL_SUCCESS =     1;
		private final static int BAG_ENABLE_LOCAL_FAILED =     2;
		
		private final static int BAG_ENABLE_NET_SUCCESS =     5;
		private final static int BAG_ENABLE_NET_FAILED =     6;
		private final static int BAG_ENABLE_NET_TIMEOUT =     7;
		
		private final static int BAG_WRITE_ENABLE_SUCCESS =     8;
		private final static int BAG_WRITE_ENABLE_FAILED =     9;
		
		private byte[] mEnableCode = null;
		
		private NetTask mNetTask = null;
		private WriteRFIDTask mWriteRFIDTask = null;
		
		private AlertDialog mDialog  = null;
		private int mStep = 0;
		private final static int COME_OVER=     1;//已经完成
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_enable_bag);
			ActivityUtil.getInstance().addActivityToList(this);
			
			mReadBagTask = new ReadBagTask();
			mNetTask = new NetTask();
			mWriteRFIDTask = new WriteRFIDTask();
			
			mBagOp = BagOperation.getInstance();
//			mBagOp.connection();
			findViews();
			
			

		}

		private void findViews() {
			// TODO Auto-generated method stub

			mTvLock = (TextView) findViewById(R.id.tv_scanbunch_activity_lock);
			mTvLock.setOnClickListener(this);

			mBtnStart = (Button) findViewById(R.id.scan_ok);
			mBtnStart.setOnClickListener(this);

			mPlanScanNum = SPUtils.getInt(EnableNFCEmptyBagActivity.this,
					MyContexts.KEY_BUNCH_NUM, 10);
			mLockTextView = (TextView) findViewById(R.id.tv_scanbunch_activity_lock);
			mTvPlan = (TextView) findViewById(R.id.scan_planamount);// 计划数量
			mTvPlan.setText(mPlanScanNum + "");
			mTvPlan.setOnClickListener(this);

			mTvReal = (TextView) findViewById(R.id.real_amount);// 实际数量
			mProgreeBar = (MyProgressBar) findViewById(R.id.pb_scanbunch_activity);
			mProgreeBar.setMax(mPlanScanNum);
			mProgreeBar.setText("请扫描袋锁");
			ViewUtil.requestFocus(mBtnStart);

		}
		
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			if (OLEDOperation.enable) {
				OLEDOperation.getInstance().open();
				OLEDOperation.getInstance().showTextOnOled("请将手持","靠近袋锁");
			}
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.scan_ok:
				if(!haveTaskRunning){
					if(mStep != COME_OVER){
						mBtnStart.setEnabled(false);
						haveTaskRunning = true;
						ThreadPoolFactory.getNormalPool().execute(mReadBagTask);
					}else {
						finish();
					}
					
				}
				break;
			case R.id.im_item_activity_back:
				if(!haveTaskRunning){
					closeAndFinsh();
				}else {
					haveTaskRunning= false;
					mReadBagTask.stop();
					mWriteRFIDTask.stop();
					changFocusToTrue();
				}
				break;
			case R.id.tv_scanbunch_activity_lock:
				if (!haveTaskRunning) {
					showDialog(OVER_LOCK_DIALOG);
				}
				break;
			case R.id.scan_planamount:
				if (!haveTaskRunning) {
					showDialog(CHANGE_NUMBER_DIALOG);
				}
				break;

			}
		}
		Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case SHOW_LOCK_RESULT:
					final String info = ReplyParser.parseReply(StaticString.information);
					if(null!=info && info.equals("锁定批成功")){
						Drawable nav_up=getResources().getDrawable(R.drawable.lock);
						nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
						mLockTextView.setCompoundDrawables(null, nav_up,null , null);
						mLockTextView.setEnabled(false);
					}
					new TipDialog().createDialog(EnableNFCEmptyBagActivity.this, 0);
					break;
				case READ_BAG_FAILED:
					haveTaskRunning = false;
					dealFailedInfo("扫描袋锁失败，请靠近袋锁!");
					mBtnStart.setEnabled(true);
					break;
				case BAG_ENABLE_LOCAL_SUCCESS://已经空袋已经启用
					haveTaskRunning = false;
					dealFailedInfo("读袋已经启用！");
					mBtnStart.setEnabled(true);
					break;
				case BAG_ENABLE_LOCAL_UNENABLE://已经空袋已经注销
					haveTaskRunning = false;
					dealFailedInfo("读袋已经注销！");
					mBtnStart.setEnabled(true);
					break;
				case BAG_NO_INIT:
					haveTaskRunning = false;
					dealFailedInfo("读袋尚未初始化，请先初始化！");
					mBtnStart.setEnabled(true);
					break;
				case BAG_ENABLE_LOCAL_FAILED:
				//	dealFailedInfo("读袋尚未启用！");
					haveTaskRunning = true;//请求服务器启用空袋
					ThreadPoolFactory.getNormalPool().execute(mNetTask);
					break;
					
				case BAG_ENABLE_NET_SUCCESS:
					haveTaskRunning = true;
					ThreadPoolFactory.getNormalPool().execute(mWriteRFIDTask);
					break;
				case BAG_ENABLE_NET_FAILED:
					haveTaskRunning = false;
					mWriteRFIDTask.writeError();
					ThreadPoolFactory.getNormalPool().execute(mWriteRFIDTask);
					dealFailedInfo(null);
					break;	
				case BAG_ENABLE_NET_TIMEOUT:
					haveTaskRunning = false;
					dealFailedInfo("验证启用码超时");
					mWriteRFIDTask.writeError();
					ThreadPoolFactory.getNormalPool().execute(mWriteRFIDTask);
					break;	
				case BAG_WRITE_ENABLE_SUCCESS:
					haveTaskRunning = false;
					LogsUtil.s("ok");
					mScannedNum++;
					// 判断 扫捆 是否完成
					if (mScannedNum == mPlanScanNum) {
						mWriteRFIDTask.stop();
						SoundUtils.playNumber(mScannedNum);
						update(); // 更新相关控件
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								SoundUtils.playScanFinishSound();
							}
						}, 700);
						mProgreeBar.setText("启用空袋已完成!");
						mBtnStart.setText("退出");
						mBtnStart.setEnabled(true);
						mStep = COME_OVER;
					} else if (mScannedNum < mPlanScanNum) {
						SoundUtils.playNumber(mScannedNum);
						update(); // 更新相关控件
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mBtnStart.performClick();
							}
						}, 2000);
					} else {
						LogsUtil.e("超出计划数量");
					}
					break;
				case BAG_WRITE_ENABLE_FAILED:
					mBtnStart.setEnabled(true);
					LogsUtil.s("READ_BARCODE_BAD");
					dealFailedInfo("启用空袋失败");
					break;
			
				default:
					break;
				}
			}

		};
		
		
		private void dealFailedInfo (String info){
			changFocusToTrue();
			if(null == info){
				createDialog(ReplyParser.parseReply(StaticString.information), 0);
			}else {
				createDialog(info,0);
			}
			mProgreeBar.setText("启用已停止!");
			mBtnStart.setText("重新启用");
		}
		public void createDialog( final String info,final int flag) {
			AlertDialog.Builder builder = new Builder(EnableNFCEmptyBagActivity.this);
			builder.setMessage(info);
			builder.setTitle("提示");

			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (flag == 0) {
						// nothing
					} else if (flag == 1) {
						EnableNFCEmptyBagActivity.this.finish();
					}
				}
			});
			mDialog = builder.create();
			mDialog.show();
		}
		
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			
			if (OLEDOperation.enable) {
				OLEDOperation.getInstance().close();
			}
			
			if(mBagOp != null) {
				RFIDOperation.getInstance().closeRf();
				mBagOp = null;
			}
			ActivityUtil.getInstance().removeActivityFromList(this);
		}
		
		/**
		 * 记录 已扫描的 捆数
		 */
		int mScannedNum = 0; // 记录 已扫描的 捆数
		int z = 1;

		/**
		 * @Title: pasrePiNumber
		 * @Description:解析批信息
		 * @param 设定文件
		 * @return booelan : ture,数据正确，下一步;false,数据异常
		 */
		public Boolean pasrePiNumber() {
			String str = StaticString.information.substring(6, 8);
			LogsUtil.d(TAG,"收到启用回复信息:"+StaticString.information+"----"+str);
			if (! "02".equals(StaticString.information.substring(1, 3)) && !"00".equals(str)) {
				return true;
			}
			return false;
		}

		private void update() {
			mTvReal.setText(mScannedNum + "");
			mProgreeBar.incrementProgressBy(1);
		}
	 
		/**
		 * 设置 确定按钮字体 为黑色 和 可点击
		 */
		public void changFocusToTrue() {
			mBtnStart.setTextColor(getResources().getColor(color.black));
			mBtnStart.setEnabled(true);
		}

		/**
		 * @param number
		 * @return 返回 numberb 的字符串形式, number不足两位的在前面 补 '0'
		 */
		public static String getInt(int number) {
			if (number < 10) {
				return "0" + number;
			} else {
				return Integer.toString(number);
			}
		}

		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}


		private void closeAndFinsh() {
			finish();
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (haveTaskRunning) {
					haveTaskRunning = false;
					mReadBagTask.stop();
					mWriteRFIDTask.stop();
					changFocusToTrue();
				} else {
					EnableNFCEmptyBagActivity.this.finish();
				}
			}
			return false;
		}

		@Override
		@Deprecated
		protected Dialog onCreateDialog(int id) {
			AlertDialog.Builder builder = new Builder(this);
			switch (id) {
				/* 锁定批的 对话框 */
			case OVER_LOCK_DIALOG:
				// AlertDialog.Builder builder = new Builder(this);
				builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage(
						MyContexts.DIALOG_MESSAGE_LOCK_BUNCH);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								StaticString.information = null;
								SocketConnet.getInstance().communication(7);// 锁定批
								startLockPi ();
							}
						});
				builder.setNegativeButton("取消", null);
				return builder.create();
			case SAME_BARCODE_DIALOG:
				// builder.setTitle(MyContexts.DIALOG_TITLE).setMessage(MyContexts.MESSAGE_SCANED);
				// return builder.create();
			case WRONG_BARCODE_DIALOG:
				builder.setTitle(MyContexts.TEXT_DIALOG_TITLE).setMessage("");
				return builder.create();
			case CHANGE_NUMBER_DIALOG:
				// 设置 对话框 的样式
				View view = View.inflate(getApplicationContext(),
						R.layout.dialog_scan_bunch_plan_num, null);
				builder.setView(view);

				// 为 对话框 上的按钮设置监听器
				final NumberInputer vNumInputer = (NumberInputer) view
						.findViewById(R.id.v_item_nubmer_input);
				vNumInputer.setNumber(mPlanScanNum);
				OnClickListener myDialogListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.btn_item_dialog_set_one:
							vNumInputer.setNumber(1);
							break;
						case R.id.btn_item_dialog_set_ten:
							vNumInputer.setNumber(10);
							break;
						case R.id.btn_item_dialog_set_twinty:
							vNumInputer.setNumber(20);
							break;
						}
					}
				};
				view.findViewById(R.id.btn_item_dialog_set_one).setOnClickListener(
						myDialogListener);
				view.findViewById(R.id.btn_item_dialog_set_ten).setOnClickListener(
						myDialogListener);
				view.findViewById(R.id.btn_item_dialog_set_twinty)
						.setOnClickListener(myDialogListener);

				builder.setTitle("修改启用数量")
						.setPositiveButton("保存",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										int num = vNumInputer.getNumber();
										if (30 < num) {
											num = 30;
										} else if (num <= mScannedNum) {
											num = mScannedNum + 1;
										}
										mTvPlan.setText(String.valueOf(num));
										mPlanScanNum = num;
										mProgreeBar.setMax(mPlanScanNum);
										mProgreeBar.setProgress(mScannedNum);
										SPUtils.putInt(getApplicationContext(),
												MyContexts.KEY_BUNCH_NUM,
												mPlanScanNum);
									}
								}).setNegativeButton("取消", null);
				return builder.create();
			}
			return super.onCreateDialog(id);
		}

		protected void startLockPi() {
			// TODO Auto-generated method stub
			ThreadPoolFactory.getNormalPool().execute(new Runnable() {
				@Override
				public void run() {
					// 在子线程中 检查 StaticString.information
					// 是否为空
					if (ReplyParser.waitReply()) {
						mHandler.sendEmptyMessage(SHOW_LOCK_RESULT);
					}
				}
			});
		}

		@Override
		@Deprecated
		protected void onPrepareDialog(int id, Dialog dialog) {
			super.onPrepareDialog(id, dialog);
			switch (id) {
			case SAME_BARCODE_DIALOG:
				((AlertDialog) dialog).setMessage(MyContexts.DIALOG_MESSAGE_SCANED);
				break;
			case WRONG_BARCODE_DIALOG:
				((AlertDialog) dialog)
						.setMessage(MyContexts.DIALOG_MESSAGE_BARCODE_WRONG);
				break;

			default:
				break;
			}
		}

		private void showSameBarcodeDialog() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showDialog(SAME_BARCODE_DIALOG);
				}
			});
		}

		private void dismissSameBarcodeDialog() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					dismissDialog(SAME_BARCODE_DIALOG);
				}
			});
		}
	/**
	 * @ClassName: readLockBag
	 * @Description: 读基金袋,检测 袋版本是否 初始化(读取第1块区或者是起始为0x05的位置)
	 */
	class ReadBagTask implements Runnable {
		private boolean stoped;
		private boolean isGetID = false;

		private void stop() {
			stoped = true;
		}
		public void run() {
			haveTaskRunning = true;
			final int TIMES = 50;// 读取 EPC 的次数
			int count = 0;
			stoped = false;
			String tmpString =null;
			while (!stoped) {
				if (TIMES < ++count) {
					mHandler.sendEmptyMessage(READ_BAG_FAILED);
					stoped = true;
					return;
				}
				if(!isGetID){
					mUid = (null == mUid ? mBagOp.getUid():mUid);
					if(null != mUid && mUid.length == 7){
						mNfcBagOp = mBagOp.getNfcBagOperation();
						isGetID =  true;
					}else {
						continue;
					}	
				}
				
				int j = -1;
				if(null == mNfcBagOp){
					mHandler.sendEmptyMessage(READ_BAG_FAILED);
					return;
				}else {
					j = mNfcBagOp.readBagID();
				}
				
				if (j == BAG_MATCH) { 
					// 已初始化
					mEnableCode = mNfcBagOp.readEnableBagCode();
					//解密后的启用码
					mEnableCode = (mEnableCode == null ? null : ArrayUtils.deciphering(mEnableCode, mUid));
					tmpString = (mEnableCode == null ? null : ArrayUtils.bytes2HexString(mEnableCode));
					Log.d(TAG, "mEnableCode2= "+tmpString);
					if(tmpString == null ){
						mHandler.sendEmptyMessage(READ_BAG_FAILED);
						stoped = true;
						return;
					}else if(tmpString != null && tmpString.equalsIgnoreCase("FFDDFFEE")){
						mHandler.sendEmptyMessage(BAG_ENABLE_LOCAL_SUCCESS);
						stoped = true;
						return;
					}else if(tmpString != null && tmpString.equalsIgnoreCase("EEEEEEEE")){//该袋已经注销
						mHandler.sendEmptyMessage(BAG_ENABLE_LOCAL_UNENABLE);
						stoped = true;
						return;
					}else {
						mHandler.sendEmptyMessage(BAG_ENABLE_LOCAL_FAILED );//zyp 20160311 应该是读出全是0 说明还没有启用
						stoped = true;
						return;
					}
				} else if (j == BAG_NO_INIT) {
					// 未初始化
					mHandler.sendEmptyMessage(BAG_NO_INIT);
					stoped = true;
					return;
				} else {
					// 读卡失败
					SystemClock.sleep(200);
				}
				
			}
		}
	}
	class NetTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			StaticString.information = null;// 判断回复信息之间清空之前信息
			SocketConnet.getInstance().communication(882);//
		
			if (ReplyParser.waitReply()) {

				if (pasrePiNumber()) {
					mHandler.sendEmptyMessage(BAG_ENABLE_NET_SUCCESS);
				} else {// 异常回复 02
					mHandler.sendEmptyMessage(BAG_ENABLE_NET_FAILED);
				}
			} else {// 超时回复之后就返回上层 收不到信息就返回上层页面
				mHandler.sendEmptyMessage(BAG_ENABLE_NET_TIMEOUT);
			}
			LogsUtil.d("NetTask  end");
		}
	}
	
	/**
	 * 写启用码到地址0x11-0x13
	 * @author WriteRFIDTask
	 *
	 */
	class WriteRFIDTask implements Runnable {
		private boolean stoped;
		private boolean wtiteErrorInfo = false;;

		public void stop() {
			stoped = true;
		}
		public void writeError(){
			wtiteErrorInfo = true;
		}
		@Override
		public void run() {
			LogsUtil.d("write RFID task run");
			stoped = false;
			int count = 0;
			final int TIMES = 10;// 读取 EPC 的次数
			count = 0;
			byte tt[] = new byte[]{(byte)0xff,(byte)0xdd,(byte)0xff,(byte)0xee};
			if(wtiteErrorInfo){
				tt[1] = (byte)0xff;
				tt[3] = (byte)0xff;
			}
				
			while (!stoped) {
				if (TIMES < ++count) {
					stoped = true;
					mHandler.sendEmptyMessage(BAG_WRITE_ENABLE_FAILED);
					break;
				}
				if (mNfcBagOp.writeEnableCode(ArrayUtils.encryption(tt,mUid))) {
					// 写入启用码 成功
					LogsUtil.s(" 写入启用码 成功");
					stoped = true;
					mHandler.sendEmptyMessage(BAG_WRITE_ENABLE_SUCCESS);
					break;
				}
				
				SystemClock.sleep(50);
			}
			stoped = true;
			LogsUtil.d("690 write RFID task end");
		}// end run()
	};
}
