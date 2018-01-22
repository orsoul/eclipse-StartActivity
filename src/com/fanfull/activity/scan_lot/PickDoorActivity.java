package com.fanfull.activity.scan_lot;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.TYPE_OP;
import com.fanfull.entity.MemberItem;
import com.fanfull.fff.R;

public class PickDoorActivity extends BaseActivity {

	private ArrayList<MemberItem> mInStoreList = null;
	private ArrayList<MemberItem> mOutStoreList = null;

	private ListView mListView = null;
	private CommonAdapter<MemberItem> mInStoreAdapter = null;
	private CommonAdapter<MemberItem> mOutStoreAdapter = null;
	private CommonAdapter<MemberItem> mCurrentAdapter = null;

	private InStoreOnItemClickListener mIOOnItemListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int intExtra = getIntent().getIntExtra(TYPE_OP.KEY_TYPE, 0);
		if (TYPE_OP.IN_STORE_DOOR == intExtra) {
			showInStoreListView();
		} else {
			showOutStoreListView();
		}
	}

	@Override
	protected void initView() {
		setContentView(R.layout.activity_general_scan);

		mListView = (ListView) this.findViewById(R.id.list);
		mIOOnItemListener = new InStoreOnItemClickListener();
		mListView.setOnItemClickListener(mIOOnItemListener);

	}

	/**
	 * 显示 入库选择 ListView
	 */
	private void showInStoreListView() {
		if (null == mInStoreList) {
			mInStoreList = new ArrayList<MemberItem>();

			MemberItem item0 = new MemberItem();
			item0.name = "交接操作";
			item0.nameEnglish = " 进入第一道天线门";

			MemberItem item1 = new MemberItem();
			item1.name = "完整券入库";
			item1.nameEnglish = " 进入一楼第二道天线门";

			MemberItem item2 = new MemberItem();
			item2.name = "残损券入库";
			item2.nameEnglish = " 进入二楼道天线门";

			MemberItem item3 = new MemberItem();
			item3.name = "返  回";
			item3.nameEnglish = "go back";

			mInStoreList.add(item0);
			mInStoreList.add(item1);
			mInStoreList.add(item2);
			mInStoreList.add(item3);

			mInStoreAdapter = new CommonAdapter<MemberItem>(
					getApplicationContext(), mInStoreList,
					R.layout.item_general_activity_member) {

				@Override
				public void convert(ViewHolder helper, MemberItem item,
						boolean isSelect) {
					// TODO Auto-generated method stub
					helper.setText(R.id.name, item.name);
					helper.setText(R.id.nameEnglish, item.nameEnglish);
					helper.setTextColor(R.id.name, Color.BLUE);
					helper.setTextColor(R.id.nameEnglish, Color.BLACK);

					if (isSelect) {
						helper.getConvertView().setBackgroundResource(
								R.drawable.selector_item_general_list_focus);
					} else {
						helper.getConvertView().setBackgroundResource(
								R.drawable.selector_item_general_list);
					}
				}
			};
			if (null == mIOOnItemListener) {
				mIOOnItemListener = new InStoreOnItemClickListener();
			}
		}

		mListView.setOnItemClickListener(mIOOnItemListener);
		mListView.setAdapter(mInStoreAdapter);
		mCurrentAdapter = mInStoreAdapter;
		mListView.post(new Runnable() {
			@Override
			public void run() {
				setListViewItmeFocus(0);
			}
		});
	}

	/**
	 * 显示 出库选择 ListView
	 */
	private void showOutStoreListView() {
		// TODO Auto-generated method stub
		if (null == mOutStoreList) {
			mOutStoreList = new ArrayList<MemberItem>();

			MemberItem item0 = new MemberItem();
			item0.name = "完整券出库";
			item0.nameEnglish = " 进入第二道天线门";

			MemberItem item1 = new MemberItem();
			item1.name = "残损券出库";
			item1.nameEnglish = " 进入二楼天线门";

			MemberItem item2 = new MemberItem();
			item2.name = "返  回";
			item2.nameEnglish = "go back";

			mOutStoreList.add(item0);
			mOutStoreList.add(item1);
			mOutStoreList.add(item2);

			mOutStoreAdapter = new CommonAdapter<MemberItem>(
					getApplicationContext(), mOutStoreList,
					R.layout.item_general_activity_member) {

				@Override
				public void convert(ViewHolder helper, MemberItem item,
						boolean isSelect) {
					// TODO Auto-generated method stub
					helper.setText(R.id.name, item.name);
					helper.setText(R.id.nameEnglish, item.nameEnglish);
					helper.setTextColor(R.id.name, Color.BLUE);
					helper.setTextColor(R.id.nameEnglish, Color.BLACK);

					if (isSelect) {
						helper.getConvertView().setBackgroundResource(
								R.drawable.selector_item_general_list_focus);
					} else {
						helper.getConvertView().setBackgroundResource(
								R.drawable.selector_item_general_list);
					}
				}
			};

			if (null == mIOOnItemListener) {
				mIOOnItemListener = new InStoreOnItemClickListener();
			}
		}

		mListView.setOnItemClickListener(mIOOnItemListener);
		mListView.setAdapter(mOutStoreAdapter);
		mOutStoreAdapter.notifyDataSetInvalidated();
		mCurrentAdapter = mOutStoreAdapter;
		mListView.post(new Runnable() {
			@Override
			public void run() {
				setListViewItmeFocus(0);
			}
		});
	}

	private void setListViewItmeFocus(int pos) {
		if (null == mListView || null == mCurrentAdapter || pos < 0) {
			return;
		}

		mListView.setSelection(0);
		mCurrentAdapter.setSection(0);
		mCurrentAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		setResult(-1);
		super.onBackPressed();
	}
	private class InStoreOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {
			setResult(position);
			finish();
		}
	}
}
