package com.mvp.bagLinkCenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;

public class BagLinkCenterActivity extends BaseActivity {
	private Button initLink, scanBagLink,scanBagLink1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findView();
	}

	private void findView() {
		setContentView(R.layout.activity_bag_link_center);
		initLink = (Button) findViewById(R.id.initLink);
		scanBagLink = (Button) findViewById(R.id.scanBagLink);
		scanBagLink1 = (Button) findViewById(R.id.scanBagLink1);

		initLink.setOnClickListener(this);
		scanBagLink.setOnClickListener(this);
		scanBagLink1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.initLink:
			intent = new Intent(this, com.mvp.bagLink3.BagLinkActivity.class);
			startActivity(intent);
			break;
		case R.id.scanBagLink:
			intent = new Intent(this, com.mvp.bagLink2.BagLinkActivity.class);
			startActivity(intent);
			break;
		/*case R.id.scanBagLink1:
			intent = new Intent(this, com.mvp.baglink1.BagLinkActivity.class);
			startActivity(intent);
			break;*/
		default:
			break;
		}
	}
}
