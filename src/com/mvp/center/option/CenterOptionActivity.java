package com.mvp.center.option;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.ToastUtil;
import com.mvp.pileCreate1.PileCreateOneActivity;
import com.mvp.pilecreate.PileCreateActivity;
import com.mvp.pilecreate2.PileCreateTwoActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CenterOptionActivity extends BaseActivity implements OnClickListener {
	private Button btnCreatepile1,btnCreatepile2,btnCreatopile3;
	private ImageView ivBack,ivSync;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center_option);
		findView();
	}

	private void findView() {
		// TODO Auto-generated method stub
		ivBack = (ImageView) findViewById(R.id.option_create_back);
		btnCreatepile1 = (Button) findViewById(R.id.option_createpile1);
		btnCreatepile2 = (Button) findViewById(R.id.option_createpile2);
		btnCreatopile3 = (Button) findViewById(R.id.option_createpile3);
		ivBack = (ImageView) findViewById(R.id.option_create_back);
		btnCreatepile1.setOnClickListener(this);
		btnCreatepile2.setOnClickListener(this);
		btnCreatopile3.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.option_createpile1:
			intent = new Intent(this, PileCreateActivity.class);
			startActivity(intent);
			break;
		case R.id.option_createpile2:
			intent = new Intent(this, PileCreateOneActivity.class);
			startActivity(intent);
			break;
		case R.id.option_createpile3:
			intent = new Intent(this,PileCreateTwoActivity.class);
			startActivity(intent);
			break;
		case R.id.option_create_back:
			finish();
			break;
		default:
			break;
		}
	}

}
