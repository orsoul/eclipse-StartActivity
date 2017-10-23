package com.mvp.pileCreate1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.adapter.ViewHolder;
import com.entity.ListInfo;
import com.entity.PermissionInfo;
import com.entity.PileInfo;
import com.entity.Response;
import com.entity.ResultInfo;
import com.entity.RootInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.mvp.baglink.PileListAdapter;
import com.mvp.pileCreate1.ContentAdapter.CallBack;
import com.mvp.pileCreate1.PileCreateOneContract.Presenter;
import com.mvp.pileCreate1.PileCreateOneContract.View;
import com.mvp.pilecreate.PileCreateActivity;
import com.mvp.pilecreate2.PileCreateTwoActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class PileCreateOneActivity extends BaseActivity implements View, CallBack{
	private final int MSG_PERMISSION_NO = 2;
	private final int MSG_PERMISSION_YES = 3;
	private final int MSG_NULL_STORAGE = 1;
	private Handler mHandler = new Handler(new HandlerCallBack());
	private Presenter mPileCreateOnePresenter = new PileCreateOnePresenter(this);
	private ResultInfo resultInfo;
	private ListView lv_list;
	private List<ListInfo> listInfos;
	/**
	 * 堆列表内容
	 */
	private List<ResultInfo> mList;
	
	/**
	 * ListView的适配器
	 */
	private PileListAdapter mAdapter;
	
	private DialogUtil mDiaUtil = new DialogUtil(this);
	
	private Button btn_pile_create_1;
	private Map<String, Integer> storeMap;
	private ArrayList<String> storeList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pile_create_one);
		btn_pile_create_1 = (Button) findViewById(R.id.btn_pile_create_1);
		btn_pile_create_1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(android.view.View v) {
				// TODO Auto-generated method stub
				mDiaUtil.showProgressDialog("正在扫描袋");
				mPileCreateOnePresenter.getBagID();
			}
		});
		findView();
		mDiaUtil.showProgressDialog();
		mPileCreateOnePresenter.getPileStore(StaticString.userId);
		mPileCreateOnePresenter.getPilePermission(StaticString.userId);
		
	}	
	private void findView() {
		// TODO Auto-generated method stub
		lv_list = (ListView) findViewById(R.id.pile_one_List);
	}

	@Override
	public void createPileSuccess(RootInfo rInfo) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		if(!rInfo.getSuccess()){
			msg.what = MSG_NULL_STORAGE;
			resultInfo = rInfo.getResult();
			PermissionInfo info = resultInfo.getPermission();
			if(info != null){
				String premission = SPUtils.getString(MyContexts.KEY_CREATE_PRESENTER);
				String premission1 = info.getBagModel()+"--"+info.getMoneyType()+"--"+info.getMoneyModel();
				premission = premission.substring(0, premission1.length());
				
				LogsUtil.e("p1"+premission+"&&"+"p2"+premission1);
				msg.obj = rInfo.getMsg();
				if(premission != null && premission1 != null){
					if(premission.equals(premission1)){
						msg.obj = "你已拥有此权限";
					}
				}
			}else{
				msg.obj = "当前没有待入库数据";
			}
		}else{
			resultInfo =rInfo.getResult();
			if(resultInfo.getPermission()==null){
				msg.what = MSG_PERMISSION_NO;
			}else if(resultInfo.getPermission().getBagModel() != null &&!"".equals(resultInfo.getPermission().getBagModel())){
				msg.what = MSG_PERMISSION_YES;
			}
			msg.obj = resultInfo;
		}
		//msg.what = MSG_PILEPERMISSION_SUCCES;
		
		mHandler.sendMessage(msg);
		
	}
	
	@Override
	public void createPileFailure() {
		// TODO Auto-generated method stub
		
	}
	private ArrayList<String> list;
	
	private ContentAdapter storeadapter;
	public PermissionInfo permissionInfo;
	class HandlerCallBack implements Handler.Callback, OnItemClickListener {

		private ResultInfo rInfo;

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_NULL_STORAGE:
				LogsUtil.e(msg.obj.toString());
				mDiaUtil.showDialogFinishActivity(msg.obj.toString());
				break;
			case MSG_PERMISSION_NO:
				storeList = new ArrayList<String>();
				if(storeMap!=null){
					for (String key : storeMap.keySet()) {
						storeList.add(key);
					}	
				}
				
				
				rInfo = (ResultInfo) msg.obj;
				listInfos = rInfo.getList();
				list = new ArrayList<String>();
				for (ListInfo l : listInfos) {
					String result = l.getBagModel()+"--"+l.getMoneyType()+"--"+l.getMoneyModel();
					list.add(result);
				}
				storeadapter = new ContentAdapter(PileCreateOneActivity.this, storeList,PileCreateOneActivity.this);
				lv_list.setAdapter(storeadapter);
				lv_list.setOnItemClickListener(this);
				break;
			case MSG_PERMISSION_YES:
				rInfo = (ResultInfo) msg.obj;
				permissionInfo = rInfo.getPermission();
//				SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, permissionInfo.getBagModel()
//						+"-"+permissionInfo.getMoneyType()+"-"
//						+permissionInfo.getMoneyModel());
				String result = SPUtils.getString(MyContexts.KEY_CREATE_PRESENTER);
				if(!"KEY_CREATE_PRESENTER".equals(result)&&!"".equals(result)&&result != null){
					storeList = new ArrayList<String>();
					if(storeMap!=null){
						for (String key : storeMap.keySet()) {
							storeList.add(key);
						}	
					}
					
					listInfos = rInfo.getList();
					list = new ArrayList<String>();
					for (ListInfo l : listInfos) {
						String results = l.getBagModel()+"--"+l.getMoneyType()+"--"+l.getMoneyModel();
						list.add(results);
					}
					storeadapter = new ContentAdapter(PileCreateOneActivity.this, storeList,PileCreateOneActivity.this);
					lv_list.setAdapter(storeadapter);
					lv_list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0,
								android.view.View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				AlertDialog.Builder builder = new Builder(PileCreateOneActivity.this);
				builder.setTitle("提示");
				builder.setMessage("已拥有权限："+permissionInfo.getBagModel()
									+"-"+permissionInfo.getMoneyType()+"-"
									+permissionInfo.getMoneyModel());
				builder.setPositiveButton("更换", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						storeList = new ArrayList<String>();
						for (String key : storeMap.keySet()) {
							storeList.add(key);
						}
						listInfos = rInfo.getList();
						list = new ArrayList<String>();
						for (ListInfo l : listInfos) {
							String result = l.getBagModel()+"--"+l.getMoneyType()+"--"+l.getMoneyModel();
							list.add(result);
						}
						storeadapter = new ContentAdapter(PileCreateOneActivity.this, storeList,PileCreateOneActivity.this);
						lv_list.setAdapter(storeadapter);
						lv_list.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									android.view.View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								
							}
						});
					}
				});
				builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				});
				builder.show();
				break;
			default:
				break;
			}
			return false;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, android.view.View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public void showPermissionDialog(Context context,PermissionInfo permissionInfo){
		
	}
	@Override
	public void onFailure(String error) {
		// TODO Auto-generated method stub
		mDiaUtil.showDialogFinishActivity(error);
		
	}

	int count = 0;
	int storeID;
	String storeName;
	private String createPresenter;
	@Override
	public void click(android.view.View v) {
		// TODO Auto-generated method stub
		if(count == 0){
			btn_pile_create_1.setVisibility(android.view.View.VISIBLE);
			String key= storeList.get((Integer)v.getTag());
			storeName = key;
			storeID = storeMap.get(key);
			storeadapter.clear();
			storeadapter = new ContentAdapter(PileCreateOneActivity.this, list,PileCreateOneActivity.this);
			lv_list.setAdapter(storeadapter);
			count = 1;
		}else if(count == 1){
			final ListInfo info = listInfos.get((Integer)v.getTag());
			createPresenter = info.getBagModel()+"--"+info.getMoneyType()+"--"+info.getMoneyModel()+"--"+storeName+"--"+storeID;
			new AlertDialog.Builder(PileCreateOneActivity.this)
			.setTitle("提示")
			.setMessage("申请权限"+ createPresenter)
			.setIcon(R.drawable.selector_coverbag_lock)
			.setPositiveButton("申请", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mPileCreateOnePresenter.applyPermission(StaticString.userId, info.getMoneyType(), info.getMoneyModel(), info.getBagModel(),String.valueOf(storeID));
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).show();
		}		
	}
	@Override
	public void onGetStoreNumResult(Map<String, Integer> storeMap) {
		// TODO Auto-generated method stub
		if(storeMap!=null){
			this.storeMap = storeMap;
			mDiaUtil.dismissProgressDialog();
		}else{
			SoundUtils.playFailedSound();
			mDiaUtil.dismissProgressDialog();
			mDiaUtil.showDialogFinishActivity("获取库房列表失败");
		}
	}
	@Override
	public void applyPermissionSuccess(String msg) {
		// TODO Auto-generated method stub
		
		if(msg==null||"".equals(msg)||"该权限已被申请".equals(msg)){
			SoundUtils.playFailedSound();
			mDiaUtil.showDialogFinishActivity("该权限已被申请");
			return;
		}
		SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, createPresenter);
		new AlertDialog.Builder(PileCreateOneActivity.this)
		.setTitle("提示")
		.setMessage("申请成功！是否建堆？")
		.setIcon(R.drawable.selector_coverbag_lock)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PileCreateOneActivity.this, PileCreateTwoActivity.class);
				startActivity(intent);
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
			}
		}).show();
	}
	private String bagID;
	@Override
	public void getBigIDSuccess(String bagID) {
		// TODO Auto-generated method stub
		this.bagID = bagID;
		LogsUtil.e("bagID"+bagID,"userID"+StaticString.userId+"storeID"+storeID);
		mPileCreateOnePresenter.applyPermissionByBagID(bagID, StaticString.userId, String.valueOf(storeID));
		this.bagID  = null;
	}
	@Override
	public void getBigIDError(String msg) {
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(msg);
	}
	@Override
	public void applyPermissionSuccessByBigID(String result) {
		mDiaUtil.dismissProgressDialog();
		SPUtils.putString(MyContexts.KEY_CREATE_PRESENTER, result);
		mDiaUtil.showDialogFinishActivity(result+"申请成功");
	}
	@Override
	public void applyPermissionErrorByBigID() {
		mDiaUtil.showDialogFinishActivity("权限申请异常");
	}
	
	
}
