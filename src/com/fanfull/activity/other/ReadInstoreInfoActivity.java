package com.fanfull.activity.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.StaticString;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.OLEDOperation;
import com.fanfull.operation.BagOperation;
import com.fanfull.operation.NFCBagOperation;
import com.fanfull.socket.ReplyParser;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.TipDialog;
import com.fanfull.utils.ToastUtil;
import com.fanfull.utils.ViewUtil;

public class ReadInstoreInfoActivity extends BaseActivity {
	private final static String TAG = ReadInstoreInfoActivity.class.getSimpleName();
	private final static int READ_EXACHANGE_INFO_SUCCESS = 0;
	private final static int READ_EXACHANGE_INFO_FAILED = 1;
	private final static int NET_SUCCESS = 2;
	private final static int NET_FAILED = 3;
	
	private BagOperation mBagOp = null;
	private NFCBagOperation mNfcBagOp = null;
	private byte mUid [] = null;
	
	private ReadBagIndexTask mReadBagIndexTask;
	private NetTask mNetTask;
	private ListView mListView;
	private CommonAdapter<Map<String, String>> mAdapter;
	private List<Map<String,String>> mList = new ArrayList<Map<String,String>>();
	
	private Button mScanBtn;
	private Button mCancelBtn;
	
	private JSONArray mJsonArray = null;
	private int mCurrentPos = 0;
	
	private String mCoverInfString = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_readexchange_info);
		mListView = (ListView) findViewById(R.id.exchange_listView1);
		mScanBtn = (Button) findViewById(R.id.btn_ok);
		mScanBtn.setOnClickListener(this);
		ViewUtil.requestFocus(mScanBtn);
		
		mCancelBtn = (Button) findViewById(R.id.btn_cancel);
		mCancelBtn.setOnClickListener(this);
		
		mNetTask = new NetTask();
		mBagOp = BagOperation.getInstance();
		mReadBagIndexTask = new ReadBagIndexTask();
		
//		 [{"function":"funcdata1","userid":"机构号+用户后三位1","time":"时间数据1"},
//		  {"function":"funcdata2","userid":"机构号+用户后三位2","time":"时间数据2"}
//		 ] 1:封袋 2:入库 3:出库 4:验封 5:换袋 6:开袋
		
	}
	
	public void onClickBack (View view){
		finish();
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
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case READ_EXACHANGE_INFO_SUCCESS:
				mReadBagIndexTask.stop();
				mScanBtn.setText("扫描");
				mScanBtn.setEnabled(true);
				ThreadPoolFactory.getNormalPool().execute(mNetTask);
				break;
			case READ_EXACHANGE_INFO_FAILED:
				mScanBtn.setEnabled(true);
				mScanBtn.setText("重试");
				LogsUtil.d(TAG, "读取袋信息失败");
				ViewUtil.requestFocus(mScanBtn);
				SoundUtils.playFailedSound();
				TipDialog tp = new TipDialog();
				tp.createDialog(ReadInstoreInfoActivity.this,"读取袋信息失败", 0);
				break;
				
			case NET_SUCCESS:
				LogsUtil.d(StaticString.information.substring(1, StaticString.information.length()-5));
				try {
						mJsonArray = new JSONArray(StaticString.information.substring(1, StaticString.information.length()-5)+"]");
						StaticString.information = null;
						for (int i = 0; i < mJsonArray.length(); i++) {
							JSONObject jsonObject = mJsonArray.getJSONObject(i);
							HashMap<String, String>map = new HashMap<String, String>();
							if(jsonObject.getInt("function") == 1)map.put("func","封  袋");
							if(jsonObject.getInt("function") == 2)map.put("func","入  库");
							if(jsonObject.getInt("function") == 3)map.put("func","出  库");
							if(jsonObject.getInt("function") == 4)map.put("func","验  封");
							if(jsonObject.getInt("function") == 5)map.put("func","换  袋");
							if(jsonObject.getInt("function") == 6)map.put("func","开  袋");
							map.put("org", jsonObject.getString("organcode"));
							map.put("time", jsonObject.getString("time"));
							mList.add(map);
						}
					
				} catch (JSONException e) {
					mJsonArray = null;
					e.printStackTrace();
				}
				LogsUtil.d(TAG, "读取流转信息成功 mList.size="+mList.size());
				Collections.sort(mList, comp);  //按最近时间排序
				mAdapter = new CommonAdapter<Map<String, String>>(getApplicationContext(), mList, R.layout.item_task_list) {

					@Override
					public void convert(ViewHolder helper,
							Map<String, String> item, boolean isSelect) {
						helper.setText(R.id.taskid, item.get("func"));//功能标识
						helper.setText(R.id.taskaddr, item.get("org"));//银行名称
						helper.setText(R.id.task, item.get("time"));//时间
						if(isSelect){
							helper.getConvertView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orangered));
						}else {
							helper.getConvertView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
						}
					}
				};
				mListView.setAdapter(mAdapter);
				
				
				break;
			case NET_FAILED:
				ToastUtil.showToastInCenter("袋查询信息获取失败");
				break;
					
			default:
				break;
			}
		};
	};
	
	private long getLongtime (String time){
		//2016.05.05 09:51:08
		String year = time.substring(0, 4);
		String mon = time.substring(5, 7);
		String day = time.substring(8, 10);
		String hour = time.substring(11, 13);
		String min = time.substring(14, 16);
		String second = time.substring(17, 19);
		String str = year+mon+day+hour+min+second;
		Long lTime = (long) 0;
		try {
			 lTime = Long.parseLong(str);
		} catch (Exception e) {
			// TODO: handle exception
			 lTime = System.currentTimeMillis();
		}
		
		return lTime;
	}
	/**
	 * 按照时间进行排序
	 */
	private Comparator comp = new Comparator() {  
        public int compare(Object o1, Object o2) {  
        	Map<String, String>p1 = (Map<String, String>) o1;  
        	Map<String, String> p2 = (Map<String, String>) o2;  
            if (getLongtime(p1.get("time")) > getLongtime(p2.get("taskname"))) 
                return -1;  
            else if (getLongtime(p1.get("time")) == getLongtime(p2.get("taskname"))) 
                return 0;  
            else if (getLongtime(p1.get("time")) < getLongtime(p2.get("taskname"))) 
                return 1;  
            return 0;  
        }  
    };  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (OLEDOperation.enable) {
			OLEDOperation.getInstance().close();
		}
		
		if(null != mReadBagIndexTask){
			mReadBagIndexTask.stop();
			mReadBagIndexTask = null;
		}
		if(null != mJsonArray){
			mJsonArray = null;
		}
		
	};
	/**第五步，读取目录索引，获得交接信息的地址 以及更新索引
	 * 
	 * @ClassName: ReadBagIndexTask
	 * @Description: 读取目录索引，获得交接信息的开始地址，单条信息长度，已经有几条
	 */
	class ReadBagIndexTask implements Runnable {
		private boolean stoped;

		private void stop() {
			stoped = true;
		}
		public void run() {
			final int TIMES = 40;// 读取 EPC 的次数
			int count = 0;
			stoped = false;
			while (!stoped) {
				if (TIMES < ++count) {
					mHandler.sendEmptyMessage(READ_EXACHANGE_INFO_FAILED);
					stoped = true;
					return;
				}
				mUid = mBagOp.getUid();
				if(null != mUid && mUid.length == 7){
					mNfcBagOp = mBagOp.getNfcBagOperation();
				}else {
					// 读卡失败
					SystemClock.sleep(50);
					continue;
				}
				mCoverInfString = mNfcBagOp.readBagBarCode();
				if(null != mCoverInfString){
					mHandler.sendEmptyMessage(READ_EXACHANGE_INFO_SUCCESS);
				}else {
					continue;
				}
			} 
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			ThreadPoolFactory.getNormalPool().execute(mReadBagIndexTask);
			mScanBtn.setEnabled(false);
			mScanBtn.setText("扫描中...");
			
			mList.clear();
			break;
		case R.id.btn_cancel:
			if(mReadBagIndexTask != null){
				mReadBagIndexTask.stop();
				mReadBagIndexTask = null;
			}
			finish();
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		LogsUtil.d("keyCode:" + keyCode);
		int d = 1;
		switch (keyCode) {
		case KeyEvent.KEYCODE_4: // 焦点 上移
			finish();
			break;
		case KeyEvent.KEYCODE_2: // 焦点 上移
			d = -1;
		case KeyEvent.KEYCODE_8: // 焦点 下移
			int itemCount = mListView.getCount();
			mCurrentPos = mListView.getSelectedItemPosition();
			mCurrentPos = (mCurrentPos + d + itemCount) % itemCount;
			
			setListViewItmeFocus(mCurrentPos);
			LogsUtil.d("mCurrentPos:" + (mCurrentPos));
			break;
		case KeyEvent.KEYCODE_ENTER:
			View selectedView = mListView.getSelectedView();
			if (null != selectedView) {
				mListView.performItemClick(selectedView, mCurrentPos,
						mListView.getItemIdAtPosition(mCurrentPos));
				return true;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setListViewItmeFocus(int pos) {
		if (null == mListView || null == mAdapter || pos < 0) {
			return;
		}
		mListView.setSelection(pos);
		mAdapter.setSection(pos);
		mAdapter.notifyDataSetChanged();
	}
	
	
	/**
	 * 第六步，发送封签事件码
	 * @author Administrator
	 *
	 */
	class NetTask implements Runnable{ 

		@Override
		public void run() {
			// TODO Auto-generated method stub
				StaticString.information = null;
				SocketConnet.getInstance().communication(8000,new String[]{mCoverInfString});
				if (ReplyParser.waitReply()) {
					if(StaticString.information.length() > 0){
						mHandler.sendEmptyMessage(NET_SUCCESS);
					}else {
						mHandler.sendEmptyMessage(NET_FAILED);
					}
					
				}else {
					mHandler.sendEmptyMessage(NET_FAILED);
				}
		}
	}
}

