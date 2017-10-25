package com.mvp.pilecreate2;

import com.fanfull.base.BaseActivity;
import com.fanfull.contexts.MyContexts;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.SPUtils;
import com.fanfull.utils.SoundUtils;
import com.fanfull.utils.ToastUtil;
import com.mvp.pilecreate2.PileCreateTwoContract.Presenter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PileCreateTwoActivity extends BaseActivity implements OnClickListener,PileCreateTwoContract.View{
	private Button btn_ok;
	private TextView tv_bagModel,tv_moneyType,tv_moneyModel,tv_num;
	private ImageView iv_back;
	private DialogUtil mDialog;
	private Presenter mPresenter = new PileCreateTwoPresenter(this);
	private String storeID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pile_create_two);
		btn_ok = (Button) findViewById(R.id.create_two_ok);
		tv_bagModel = (TextView) findViewById(R.id.create_two_bagModel);
		tv_moneyType = (TextView) findViewById(R.id.create_two_moneyType);
		tv_moneyModel = (TextView) findViewById(R.id.create_two_moneyModel);
		tv_num = (TextView) findViewById(R.id.create_two_num);
		iv_back = (ImageView) findViewById(R.id.create_two_back);
		btn_ok.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		mDialog = new DialogUtil(this);
		
		String result = SPUtils.getString(MyContexts.KEY_CREATE_PRESENTER);
		LogsUtil.e("xxqq",result);
		if(!"KEY_CREATE_PRESENTER".equals(result)&&!"".equals(result)&&result != null){
			String[] arrStr = result.split("--");
			String bagModel = arrStr[0];
			LogsUtil.e("xxqq",arrStr.length);
			tv_bagModel.setText(""+bagModel);
			tv_moneyType.setText(""+arrStr[1]);
			tv_moneyModel.setText(""+arrStr[2]);
			tv_num.setText(""+arrStr[3]);
			storeID = arrStr[4];
		}else{
			DialogUtil dialogUtil = new DialogUtil(PileCreateTwoActivity.this);
			dialogUtil.showDialogFinishActivity("请先申请权限");
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.create_two_ok:
			String moneyType = tv_moneyType.getText().toString().trim();
			String moneyModel = tv_moneyModel.getText().toString().trim();
			String bagModel = tv_bagModel.getText().toString().trim();
			mPresenter.createStore(moneyType, moneyModel, bagModel, StaticString.userId, storeID);
			break;
		case R.id.create_two_back:
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	public void onCreateSuccess() {
		// TODO Auto-generated method stub
		mDialog.dismissProgressDialog();
		SoundUtils.playDropSound();
		ToastUtil.showToastInCenter("创建堆成功");
		finish();
	}
	@Override
	public void onCreateFailure() {
		// TODO Auto-generated method stub
		SoundUtils.playFailedSound();
		mDialog.dismissProgressDialog();
		mDialog.showDialog("创建堆失败");
	}

	@Override
	public void onConnectionError(String msg) {
		// TODO Auto-generated method stub
		mDialog.showDialogFinishActivity(msg);
	}



}
