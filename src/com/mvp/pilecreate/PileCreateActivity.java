package com.mvp.pilecreate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.pilecreate.PileCreateContract.Presenter;

/**
 * 新建堆界面
 * 
 * @author root
 * 
 */
public class PileCreateActivity extends BaseActivity implements
		com.mvp.pilecreate.PileCreateContract.View {

	private final int MSG_GET_STORE_NUM_SUCCES = 1;
	private final int MSG_GET_STORE_NUM_FAILED = 2;
	private final int MSG_SCAN_SUCCES = 3;
	private final int MSG_SCAN_FAILED = 4;
	private final int MSG_CREATE_SUCCES = 5;
	private final int MSG_CREATE_FAILED = 6;

	private final String JSON_KEY_SUCCESS = "success";
	private final String JSON_KEY_RESULT = "result";

	private Spinner spBagModel;
	private Spinner spMoneyModel;
	private Spinner spMoneyType;
	private Spinner spStoreNum;

	private Button btnOk;
	private Button btnScan;

	private String argBag;
	private String argMoneyMode;
	private String argMoneyType;
	private String argStoreId;

	private Presenter mPileCreatePresenter = new PileCreatePresenter(this);
	private DialogUtil mDiaUtil = new DialogUtil(this);
	

	private Map<String, Integer> storeMap;
	private ArrayList<String> storeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPileCreatePresenter.getStoreNum(StaticString.userId);
		mDiaUtil.showProgressDialog();
	}

	private void findView() {
		setContentView(R.layout.activity_create_pile);

		spBagModel = (Spinner) findViewById(R.id.sp_create_pile_store_bagModel);
		spMoneyModel = (Spinner) findViewById(R.id.sp_create_pile_store_moneyModel);
		spMoneyType = (Spinner) findViewById(R.id.sp_create_pile_store_moneyType);
		spStoreNum = (Spinner) findViewById(R.id.sp_create_pile_store_num);
		
		btnOk = (Button) findViewById(R.id.btn_create_pile_ok);
		btnScan = (Button) findViewById(R.id.btn_create_pile_scan);
	}
	
	private void setView() {

		btnOk.setOnClickListener(this);
		btnScan.setOnClickListener(this);

		storeList = new ArrayList<String>(storeMap.keySet());

		ArrayAdapter<String> aspnCountries = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, storeList);

		aspnCountries
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spStoreNum.setAdapter(aspnCountries);

		MyOnItemSelectedListener onItemSelectedListener = new MyOnItemSelectedListener();
		spBagModel.setOnItemSelectedListener(onItemSelectedListener);
		spMoneyType.setOnItemSelectedListener(onItemSelectedListener);
		spMoneyModel.setOnItemSelectedListener(onItemSelectedListener);
		spStoreNum.setOnItemSelectedListener(onItemSelectedListener);

	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create_pile_ok:
			if (null == argBag || argBag.startsWith("-")) {
				ToastUtil.showToastInCenter("请 选 择 券 别");
			} else {
				mDiaUtil.showProgressDialog();
				mPileCreatePresenter.create(argBag, argMoneyType, argMoneyMode,
						argStoreId);
			}
			break;
		case R.id.btn_create_pile_scan:
			mDiaUtil.showProgressDialog("请靠近袋锁进行扫描");
			mPileCreatePresenter.scan();
			break;
		default:
			break;
		}
	}

	@Override
	public void onGetStoreNumResult(Map<String, Integer> storeMap) {
		if(storeMap!=null){
			mDiaUtil.dismissProgressDialog();
			this.storeMap = storeMap;
			findView();
			setView();	
		}else{
			SoundUtils.playFailedSound();
			mDiaUtil.dismissProgressDialog();
			mDiaUtil.showDialogFinishActivity("获取库房列表失败");
		}
		
	}

	@Override
	public void createPileSuccess() {
		mDiaUtil.dismissProgressDialog();
		SoundUtils.playDropSound();
		ToastUtil.showToastInCenter("创建堆成功");
		spBagModel.setSelection(0);
	}

	@Override
	public void createPileFailure() {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog("创建堆失败");
	}
	
	View hideView;
	boolean setViewHide = true;
	class MyOnItemSelectedListener implements OnItemSelectedListener {
		private String[] bagModels = getResources().getStringArray(
				R.array.bagModel);
		private String[] moneyTypes = getResources().getStringArray(
				R.array.moneyType);
		private String[] moneyModels = getResources().getStringArray(
				R.array.moneyModel);

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			LogsUtil.d(TAG, "AdapterView:" + parent.getId());
			LogsUtil.d(TAG, "getId:" + view.getId());
			LogsUtil.d(TAG, "position:" + id);
			LogsUtil.d(TAG, "   Id:" + id);
			
			if (view instanceof TextView) {
				// 设置 选项文字居中
				((TextView) view).setGravity(Gravity.CENTER);
			}
			
			switch (parent.getId()) {
			case R.id.sp_create_pile_store_bagModel:
				if (0 == position) {
					btnOk.setEnabled(false);
				} else {
					btnOk.setEnabled(true);
				}
				argBag = bagModels[position];
				break;
			case R.id.sp_create_pile_store_moneyType:
				argMoneyType = moneyTypes[position];
				break;
			case R.id.sp_create_pile_store_moneyModel:
				argMoneyMode = moneyModels[position];
				break;
			case R.id.sp_create_pile_store_num:
				String key = spStoreNum.getSelectedItem().toString();
				argStoreId = storeMap.get(key).toString();
				
				break;

			default:
				break;
			}
			LogsUtil.d(TAG, "argBag:" + argBag);
			LogsUtil.d(TAG, "argMoneyType:" + argMoneyType);
			LogsUtil.d(TAG, "argMoneyMode:" + argMoneyMode);
			LogsUtil.d(TAG, "argStoreId:" + argStoreId);

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onDestroy() {
		if (null != mDiaUtil) {
			mDiaUtil.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onFailure(String error) {
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialogFinishActivity("回复数据异常");
	}

}
