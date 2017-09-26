package com.fanfull.activity.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.contexts.MyContexts;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.NfcOperation;
import com.fanfull.hardwareAction.RFIDOperation;
import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.operation.BagOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.SearchDevicesView;
import com.fanfull.view.SearchDevicesView.OnStatusChangedListener;

public class SearchDevicesActivity extends Activity implements OnClickListener {
	private final static String TAG = SearchDevicesActivity.class.getSimpleName();
	private SearchDevicesView mSearchDeviceview;

	
	private UHFOperation mUhfOp;
	private ScanTask mScanTask;
	
	private final static int MSG_SCAN_SUCCESS = 1;
	private final static int MSG_SCAN_FAILED = 2;
	private final static int READ_EXACHANGE_INFO_SUCCESS = 3;
	private final static int READ_EXACHANGE_INFO_FAILED = 4;
	private String mFindEpc = "035321011888880000000001";
	
	private BagOperation mBagOp = null;
	private byte mUid [] = null;
	
	private ReadBagIndexTask mReadBagIndexTask;
	
	private int mImgId [] = new int[]{R.id.device_icon_id_1,R.id.device_icon_id_2,R.id.device_icon_id_3,
			R.id.device_icon_id_4,};
	private ImageView mDeviceImg [] = new ImageView[4];
	
	private int mCurrentPos = 0;
	private int mPos = 0;
	
	private ListView mListView;
	private boolean isHaving = false;
	private List<Map<String,String>> mList = new ArrayList<Map<String,String>>();
	private CommonAdapter<Map<String, String>> mAdapter;
	
	private Button mScanButton,mLedButton,mRingButton,mLogButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.search_device);
		
		mUhfOp = UHFOperation.getInstance();
		mScanTask = new ScanTask();
		
		mScanButton = (Button) findViewById(R.id.btn_search);
		mLedButton = (Button) findViewById(R.id.btn_light);
		mRingButton = (Button) findViewById(R.id.btn_ring);
		mLogButton = (Button) findViewById(R.id.btn_log);
		
		mScanButton.setOnClickListener(this);
		mLedButton.setOnClickListener(this);
		mRingButton.setOnClickListener(this);
		mLogButton.setOnClickListener(this);
		
		ViewUtil.requestFocus(mScanButton);
		mBagOp = BagOperation.getInstance();
		mReadBagIndexTask = new ReadBagIndexTask();
		
		mSearchDeviceview = (SearchDevicesView) findViewById(R.id.search_device_view);
		mSearchDeviceview.setWillNotDraw(false);
		
		for (int i = 0; i < mImgId.length; i++) {
			mDeviceImg[i] = (ImageView) findViewById(mImgId[i]);
			mDeviceImg[i].setVisibility(View.GONE);
		}
		
		mListView = (ListView) findViewById(R.id.log_id);
		
		mSearchDeviceview.OnStatusChangedListener(new OnStatusChangedListener() {
			
			@Override
			public void OnStatusChanged(boolean checkState) {
				// TODO Auto-generated method stub
				if(checkState){
					mDeviceImg[mCurrentPos].setVisibility(View.GONE);
					mCurrentPos = getNeedDisplyId ();
					ThreadPoolFactory.getNormalPool().execute(mScanTask);
				}else {
					mScanTask.stop();
				}
			}
		});
		
		mAdapter = new CommonAdapter<Map<String, String>>(getApplicationContext(), mList, R.layout.search_item_list) {

			@Override
			public void convert(ViewHolder helper,
					Map<String, String> item, boolean isSelect) {
				// TODO Auto-generated method stub
				helper.setText(R.id.task_type, item.get("type"));//报警类型
				helper.setText(R.id.task_num, item.get("num"));//报警次数
				if(isSelect){
					helper.getConvertView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orangered));
				}else {
					helper.getConvertView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
				}
			}
		};
		mListView.setAdapter(mAdapter);
	}
	
	
	@Override
	public void onClick(View v) {
//		if(!ClickUtil.isFastDoubleClick(500))return;
		switch (v.getId()) {
		case  R.id.im_item_activity_back://返回
			finish();
			break;
		case R.id.btn_search:
			if(!mSearchDeviceview.getSearchStatus()){
				mScanButton.setText("停止");
				mSearchDeviceview.setSearching(true);
			}else {
				mScanButton.setText("搜索");
				mSearchDeviceview.setSearching(false);
			}
			break;
		case R.id.btn_light:
			Toast.makeText(SearchDevicesActivity.this, "解锁命令",Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_ring:
			Toast.makeText(SearchDevicesActivity.this, "唤醒命令",Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_log:
			if(!isHaving){
				ThreadPoolFactory.getNormalPool().execute(mReadBagIndexTask);
				isHaving = true;
			}
			break;
		default:
			break;
		}
	}
	public void onClickBack(View v) {
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSearchDeviceview.setSearching(false);
		mReadBagIndexTask.stop();
		RFIDOperation.getInstance().closeRf();
		if(null != mUhfOp){
			mUhfOp.close();
			mUhfOp = null;
		}
		if(null != mScanTask){
			mScanTask.stop();
			mScanTask = null;
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		int d = 1;
		switch (keyCode) {
		case KeyEvent.KEYCODE_4:
			 d = -1;
		case KeyEvent.KEYCODE_6:
			mPos = (mPos + d + 4) % 4;
			
			if(mPos == 0)ViewUtil.requestFocus(mScanButton);
			if(mPos == 1)ViewUtil.requestFocus(mLedButton);
			if(mPos == 2)ViewUtil.requestFocus(mRingButton);
			if(mPos == 3)ViewUtil.requestFocus(mLogButton);
		break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SCAN_SUCCESS:
				//扫描到
//				mScanTask.stop();
//				mSearchDeviceview.setSearching(false);
				if(mDeviceImg[mCurrentPos].getVisibility() == View.GONE){
					SoundUtils.playInitSuccessSound();
					mDeviceImg[mCurrentPos].setVisibility(View.VISIBLE);
				}
				
				break;
			case MSG_SCAN_FAILED:
				if(mDeviceImg[mCurrentPos].getVisibility() == View.VISIBLE){
					SoundUtils.playFailedSound();
					mDeviceImg[mCurrentPos].setVisibility(View.GONE);
				}
				break;
				
			case READ_EXACHANGE_INFO_SUCCESS:
				isHaving = true;
				LogsUtil.d(TAG, "mList.size()="+mList.size());
				mAdapter.notifyDataSetChanged();
				break;
			case READ_EXACHANGE_INFO_FAILED:
				isHaving = false;
				SoundUtils.playFailedSound();
				new TipDialog().createDialog(SearchDevicesActivity.this,"读取袋锁报警信息失败，请靠近重试", 0);
				break;
			default:
				break;
			}
			
			return false;
		}
	});
	
	private boolean setpower (int n){
		/**设置初始化前的功率*/
		int dReadPower = SPUtils.getInt(getApplicationContext(),
				MyContexts.KEY_POWER_READ_INSTORE, n);
		int dWritePower = SPUtils.getInt(getApplicationContext(),
				MyContexts.KEY_POWER_WRITE_INSTORE, n);
		if (null != mUhfOp && mUhfOp.setPower(dReadPower, dWritePower)) {
			LogsUtil.s("初始前 设置 功率成功");
			return true;
		} else {
			LogsUtil.s("初始前 设置 功率失败");
			return false;
		}
	}
	
	protected int getNeedDisplyId() {
		// TODO Auto-generated method stub
		return new Random().nextInt(4);
	}
	/**
	 * 读报警记录
	 * @author Administrator
	 *
	 */
	class ReadBagIndexTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}
		public void run() {
			final int TIMES = 40;
			int count = 0;
			stoped = false;
			while (!stoped) {
				if (TIMES < ++count) {
					mHandler.sendEmptyMessage(READ_EXACHANGE_INFO_FAILED);
					stoped = true;
					return;
				}
				//先判断是哪种卡操作。
				mUid = mBagOp.getUid();
				if(null == mUid){// 读卡失败
					LogsUtil.d("没有卡");
					SystemClock.sleep(50);
					continue;
				}
				// 已初始化
				String tmp  = NfcOperation.getInstance().reaAddr((byte)0x17);
				LogsUtil.d(TAG, "报警记录信息 tmp= "+tmp);
				if(tmp != null && tmp.length() > 4){
					Map<String, String> map = new HashMap<String, String>();
					map.put("type", "报警类型");
					map.put("num", "报警次数");
					mList.add(map);
					map.clear();
					
					map.put("type", "非法开袋");
					map.put("num", tmp.substring(0, 2));
					mList.add(map);
					map.clear();
					
					map.put("type", "低电压");
					map.put("num", tmp.substring(2, 4));
					mList.add(map);
					map.clear();
					
					map.put("type", "上电超时");
					map.put("num", tmp.substring(4, 6));
					mList.add(map);
					map.clear();
					mHandler.sendEmptyMessage(READ_EXACHANGE_INFO_SUCCESS);
					break;
				}else {
					// 读目录索引信息失败
					SystemClock.sleep(100);
				}
			} 
		}
	}
	
	class ScanTask implements Runnable {
		private boolean stopped;

		public void stop() {
			stopped = true;
		}
		private int n = 0;
		private int TIMES = 30;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			stopped = false;
			int power = SPUtils.getInt(getApplicationContext(),
					MyContexts.KEY_POWER_READ_COVER, 20);
			if(power < 25)power = 25;
			setpower(power);
			n = 0;
			while (!stopped) {
				if(TIMES <= ++ n){
					mHandler.sendEmptyMessage(MSG_SCAN_FAILED);
					n = 0;
				}
				if (mUhfOp.findOne()) {
					mUhfOp.mEPC[mUhfOp.mEPC.length - 1] = 0;
					String epc = ArrayUtils.bytes2HexString(mUhfOp.mEPC);
					if (!mFindEpc.equals(epc)) {
						mHandler.sendEmptyMessage(MSG_SCAN_SUCCESS);
						n = 0; 
					}
				}

				SystemClock.sleep(50);

			}// end while
			setpower(20);
		}

	}
}
