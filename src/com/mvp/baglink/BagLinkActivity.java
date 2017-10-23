package com.mvp.baglink;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.entity.BagInfo;
import com.entity.PileInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.baglink.BagLinkContract.Presenter;
import com.mvp.pilecreate.PileCreateActivity;

public class BagLinkActivity extends BaseActivity implements OnClickListener,com.mvp.baglink.BagLinkContract.View{
	private Button scanBag;
	
	private RelativeLayout layout;
	private ListView pileList;
	private Button sure, cancle;

	private Presenter mPresenter;
	
	/**
	 * 用户扫描袋的标志位，当用户扫描时该状态为true
	 */
	private boolean scanFlag = false;
	
	/**
	 * 堆列表内容
	 */
	private List<PileInfo> mList;
	
	/**
	 * ListView的适配器
	 */
	private PileListAdapter mAdapter;
	
	/**
	 * 扫描到的袋信息
	 */
	private BagInfo mBagInfo;
	
	/**
	 * 用户选择的堆位置
	 */
	private int position;
	private DialogUtil mDiaUtil = new DialogUtil(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bag_link);
		findView();
		setView();
		
		mPresenter = new BagLinkPresenter(this);
	}

	/**
	 * 视图的初始化
	 */
	private void setView() {
		layout.setVisibility(View.GONE);
	}

	private void findView() {
		scanBag = (Button) findViewById(R.id.scanBag);
		
		layout = (RelativeLayout) findViewById(R.id.pile_layout);
		pileList = (ListView) findViewById(R.id.pileList);
		sure = (Button) findViewById(R.id.sure);
		cancle = (Button) findViewById(R.id.cancle);
		
		scanBag.setOnClickListener(this);
		sure.setOnClickListener(this);
		cancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scanBag:
			if(!scanFlag){
				scanFlag = true;
				mDiaUtil.showProgressDialog("请扫描袋锁");
				mPresenter.scanBag();
			}
			break;
		case R.id.sure:
			position = mAdapter.getClickPosition();
			if(position!=-1){
				mPresenter.linkBagAndPile(mBagInfo, mList.get(position));
			}else{
				Log.e("","请选择堆");
			}
			break;
		case R.id.cancle:
			layout.setVisibility(View.GONE);
			scanBag.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void scanBagSuccess(List<PileInfo> piles, BagInfo bagInfo) {
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		if(piles != null){
			scanFlag = false;
			mList = piles;
			mBagInfo = bagInfo;
			List<PileInfo> list1 = new ArrayList<PileInfo>();
			for(PileInfo info :piles){
				if(info.getBagNum() == 0){
					list1.add(info);
				}
			}
			
			if(list1.size()!=0){
				layout.setVisibility(View.VISIBLE);
				scanBag.setVisibility(View.GONE);
				mAdapter = new PileListAdapter(list1);
				pileList.setAdapter(mAdapter);	
			}else{
				Intent intent = new Intent(this, PileCreateActivity.class);
				startActivity(intent);
			}
		}else{
			scanFlag = false;
			Intent intent = new Intent(this, PileCreateActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public void scanBagFailure(String error) {
		scanFlag = false;
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}

	@Override
	public void linkBagAndPileSuccess() {
		layout.setVisibility(View.GONE);
		scanBag.setVisibility(View.VISIBLE);
		SoundUtils.playDropSound();
		mDiaUtil.dismissProgressDialog();
		ToastUtil.showToastInCenter("关联成功");
	}

	@Override
	public void linkBagAndPileFailure(String error) {
		SoundUtils.playFailedSound();
		mDiaUtil.dismissProgressDialog();
		mDiaUtil.showDialog(error);
	}
}
