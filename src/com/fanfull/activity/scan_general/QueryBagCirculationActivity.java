package com.fanfull.activity.scan_general;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.entity.BankInfoBean;
import com.fanfull.factory.ThreadPoolFactory;
import com.fanfull.fff.R;
import com.fanfull.op.RFIDReadTask;
import com.fanfull.socket.RecieveListenerAbs;
import com.fanfull.socket.SendTask;
import com.fanfull.socket.SocketConnet;
import com.fanfull.utils.AESCoder;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.DateUtils;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.ViewUtil;

/**
 * 袋流转 查询。
 * 
 * @step1 扫描NFC中封签事件码
 * @step2 上传事件码 并 下拉流转记录进行显示
 * 
 * @author orsoul
 * 
 */
public class QueryBagCirculationActivity extends BaseActivity {

	private final int MSG_TIMEOUT = 1;
	/** 银行代号列表 */
	private final int MSG_GET_BANKLIST = 2;
	/** 袋流转信息 */
	private final int MSG_GET_BAG_CIRCULATION = 3;
	/** 扫描NFC封签事件码 */
	private final int MSG_SCAN_EVENT_CODE = 4;
	/** 扫描NFC中的TID */
	private final int MSG_SCAN_NFC_TID = 5;

	private JSONObject mBankCodeJson;
	private JSONArray mJsonArray;
	/** key为功能号，value为功能名称 */
	private SparseArray<String> map;
	private List<BankInfoBean> mBankInfoBeanList = new ArrayList<BankInfoBean>();

	private ListView mListview;
	private CommonAdapter<BankInfoBean> mAdapter;

	private ReadNFCTask mReadNFCtNfcTask;
	/** 封签事件码 30byte */
	private byte[] mEventCodeBuf;
	/** NFC中保存的锁片tid 6byte */
	private byte[] mPieceTid;
	private Button btnScan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		mDiaUtil = new DialogUtil(this);
		mHandler = new Handler(new MyHandlerCallback());

		/* 设置 读取NFC */
		mEventCodeBuf = new byte[30];//
		mPieceTid = new byte[6];//
		mReadNFCtNfcTask = new ReadNFCTask();

		mRecieveListener = new MyReceiver();
		SocketConnet.getInstance().setRecieveListener(mRecieveListener);

		// 先从 本地 获取 银行代号列表
		String bankCodeJson = SPUtils.getString(MyContexts.KEY_BANK_CODE_JSON,
				null);
		if (null != bankCodeJson) {
			try {
				mBankCodeJson = new JSONObject(bankCodeJson);
			} catch (JSONException e) {
			}
		}

		// 从 前置 获取 银行代号列表
		if (null == mBankCodeJson) {
			mDiaUtil.showProgressDialog();
			SocketConnet.getInstance().communication(
					SendTask.CODE_BANK_CODE_LIST);
		} else {

		}

		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_query_bag_circulation);
		btnScan = (Button) findViewById(R.id.btn_activity_circulation_scan);
		btnScan.setOnClickListener(this);
		ViewUtil.requestFocus(btnScan);

		mListview = (ListView) findViewById(R.id.lv_in_out_store_task);
		mListview.setDivider(null);
		mAdapter = new CommonAdapter<BankInfoBean>(getApplicationContext(),
				mBankInfoBeanList, R.layout.item_bag_circulation_list) {

			@Override
			public void convert(ViewHolder helper, BankInfoBean item,
					boolean isSelect) {

				helper.setText(R.id.tv_item_in_store_tasklist_num,
						item.getTvNum());
				helper.setText(R.id.tv_item_in_store_bank_name,
						item.getTvBankName());
				helper.setText(R.id.tv_item_in_store_info, item.getTvInfo());

				if (isSelect) {
					helper.getConvertView().setBackgroundColor(
							getApplicationContext().getResources().getColor(
									R.color.orangered));
				} else {
					helper.getConvertView().setBackgroundColor(
							getApplicationContext().getResources().getColor(
									R.color.transparent));
				}

			}
		};
		mListview.setAdapter(mAdapter);

	}

	private void initMap() {
		if (null == map) {
//			map = new HashMap<Integer, String>();
			map = new SparseArray<String>();
			map.put(1, "封袋");
			map.put(2, "入库");
			map.put(3, "出库");
			map.put(4, "验封");
			map.put(5, "未知");
			map.put(6, "开袋");
			map.put(7, "封入");
			map.put(8, "出开");
			map.put(9, "接收");
		}
	}

	private void parseBankCodeJson() {

		mBankInfoBeanList.clear();

		for (int i = 0; i < mJsonArray.length(); i++) {

			int keyFuncNum = 0;
			String keyBankCode = "";
			String bankName = "";
			String time = "";
			try {
				JSONObject jsonObj = mJsonArray.getJSONObject(i);
				keyFuncNum = jsonObj.getInt("function");
				keyBankCode = jsonObj.getString("organcode");
				time = jsonObj.getString("time");
				bankName = mBankCodeJson.getString(keyBankCode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			BankInfoBean bankInfoBean = new BankInfoBean();

			bankInfoBean.setTvNum(map.get(keyFuncNum));
			bankInfoBean
					.setTvBankName(bankName);
			Date date = DateUtils.parseString2Date(time, "yyyyMMddHHmmss");
			time = DateUtils.getStringTime(date, DateUtils.FORMAT_NORMAL);
			bankInfoBean.setTvInfo(time);

			mBankInfoBeanList.add(bankInfoBean);
		} // for()
		LogsUtil.d(TAG, "mBankInfoBeanList size:" + mBankInfoBeanList.size());

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_activity_circulation_scan:
			// 清空 ListView
			mBankInfoBeanList.clear();
			mAdapter.notifyDataSetChanged();
			
			// 扫描 NFC中的 封签事件码
			mDiaUtil.showProgressDialog("正在扫描事件码...");
			mReadNFCtNfcTask.setDataBuf(mEventCodeBuf);
			mReadNFCtNfcTask.setSa(0x30); // 封签事件码 存储地址
			mReadNFCtNfcTask.setRunTime(4000);
			mReadNFCtNfcTask.setMsgWhat(MSG_SCAN_EVENT_CODE);
			ThreadPoolFactory.getNormalPool().execute(mReadNFCtNfcTask);
			break;
		default:
			break;
		}
	}

	private class MyHandlerCallback implements Handler.Callback {

		@Override
		public boolean handleMessage(Message msg) {
			mDiaUtil.dismissProgressDialog();
			switch (msg.what) {
			case MSG_GET_BANKLIST:
				initMap();
				if (null != mBankCodeJson) {
					// parseBankCodeJson();

				} else {
					mDiaUtil.showDialog("前置回复异常，可能是数据格式错误");
				}
				break;
			case MSG_GET_BAG_CIRCULATION:
				String[] split = msg.obj.toString().split(" ");
				if ("01".equals(split[1])) {
					try {
						mJsonArray = new JSONArray(split[2]);
						parseBankCodeJson();
					} catch (JSONException e) {
						e.printStackTrace();
						mJsonArray = null;
						mDiaUtil.showDialog("前置回复异常，可能是数据格式错误");
					}
				} else if ("02".equals(split[1])) {
					mDiaUtil.showDialog("无该袋信息");
				} else {
					mDiaUtil.showDialog("出现未知异常");
				}

				break;
			case MSG_SCAN_EVENT_CODE:
				mDiaUtil.setProgressDialogTitle("正在扫描NFC中TID...");
				mReadNFCtNfcTask.setDataBuf(mPieceTid);
				mReadNFCtNfcTask.setSa(0x07); // 封签事件码 存储地址
				mReadNFCtNfcTask.setRunTime(4000);
				mReadNFCtNfcTask.setMsgWhat(MSG_SCAN_NFC_TID);
				ThreadPoolFactory.getNormalPool().execute(mReadNFCtNfcTask);
				break;
			case MSG_SCAN_NFC_TID:
				LogsUtil.d(TAG,
						"解密前:" + ArrayUtils.bytes2HexString(mEventCodeBuf));
				AESCoder.myEncrypt(mEventCodeBuf, mPieceTid, false);
				// 封袋的时候在封签事件码嵌入了清分信息。 去掉清分信息，还原封签事件码
				mEventCodeBuf[3] = (byte) (mEventCodeBuf[3] & 0x0F);
				String eventStr = ArrayUtils.bytes2HexString(mEventCodeBuf);
				LogsUtil.d(TAG,
						"以还原:" + ArrayUtils.bytes2HexString(mEventCodeBuf));
				SocketConnet.getInstance().communication(
						SendTask.CODE_BAG_CIRCULATION, eventStr);
				mDiaUtil.showProgressDialog("正在查询...");
				break;
			case MSG_TIMEOUT:
				if (msg.obj == null) {
					mDiaUtil.showDialog(getResources().getString(
							R.string.text_recieve_timeout));
				} else {
					mDiaUtil.showDialog(msg.obj);
				}
				break;
			default:
				break;
			}
			return true;
		}

	}

	private class MyReceiver extends RecieveListenerAbs {
		@Override
		public void onRecieve(String recString) {

			if (recString.startsWith("*{")) {
				// 得到 银行代号 列表
				String jsonStr = null;
				try {
					String[] split = recString.split(" ");
					jsonStr = split[0].substring(1);

					// mJsonArray = new JSONArray(
					// "[{\"function\":1,\"organcode\":\"002706001\",\"time\":\"20170426155745\"},{\"function\":2,\"organcode\":\"002701001\",\"time\":\"20170426155745\"},{\"function\":8,\"organcode\":\"002701001\",\"time\":\"20170426155745\"}]");
					mBankCodeJson = new JSONObject(jsonStr);

				} catch (JSONException e) {
					LogsUtil.d("JSONException ", jsonStr);
					mBankCodeJson = null;
				}
				mHandler.sendEmptyMessage(MSG_GET_BANKLIST);
			} else if (recString.startsWith("*41 ")) { // *41 xx jsonList 包号#
				// 得到 袋流转信息
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_GET_BAG_CIRCULATION;
				msg.obj = recString;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onTimeout() {
			mHandler.sendEmptyMessage(MSG_TIMEOUT);
		}
	}

	private class ReadNFCTask extends RFIDReadTask {
		@Override
		public void onReadResult(boolean readSuccess, byte[] dataBuf,
				boolean isReadM1, int msgWhat) {
			if (readSuccess) {
				mHandler.sendEmptyMessage(msgWhat);
			} else {
				Message msg = mHandler.obtainMessage();
				msg.what = MSG_TIMEOUT;
				msg.obj = "扫描袋锁失败";
				mHandler.sendMessage(msg);
			}

		}

	}
}
