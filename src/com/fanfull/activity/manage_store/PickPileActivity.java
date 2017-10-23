package com.fanfull.activity.manage_store;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fanfull.fff.R;

public class PickPileActivity extends Activity {
	public static final String KEY_PILE_INFOS = "KEY_PILE_INFOS";
	
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_pile);
		
		mListView = (ListView) findViewById(R.id.lv_pick_pile);
		
		list = new ArrayList<String>();
		for (int i = 0; i < 19; i++) {
			String s = "ABC" + i + "	223袋\n100元 完整/未清分 ";
//			String s = "ABC" + i + "	100元 完整/未清分 223袋";
			list.add(s);
		}
		
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, list);
		
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
//				intent.putExtra(KEY_PILE_INFOS, list.get(position));
				intent.putExtra(KEY_PILE_INFOS, new String[]{position + "", "100元", "完整", "已清分"});
				setResult(1, intent);
				finish();
			}
		});
		
		
	}


}
