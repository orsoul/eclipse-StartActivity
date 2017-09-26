package com.mvp.hand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;

public class HandOperActivity extends BaseActivity implements OnClickListener {

	private Button init, refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hand_oper);

		findView();
	}

	private void findView() {
		init = (Button) findViewById(R.id.initScreen);
		refresh = (Button) findViewById(R.id.refresh_screen);

		init.setOnClickListener(this);
		refresh.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent;
		switch (v.getId()) {
		case R.id.initScreen:
			intent = new Intent(this,InitScreenActivity.class);
			startActivity(intent);
			break;

		case R.id.refresh_screen:
			intent = new Intent(this,RefreshScreenActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
