package com.mvp.hand;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;

public class RefreshScreenActivity extends BaseActivity implements com.mvp.baglink1.BagLinkContract.View{

	private EditText bags,money;
	private Button refresh;
	
	private HandPresenter presenter = new HandPresenter(this);
	private DialogUtil mDialogUtil = new DialogUtil(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_screen_refresh);
		findView();
	}
	private int bagNum;
	private String moneyTotal;

	private void findView() {
		bags = (EditText) findViewById(R.id.bags);
		money = (EditText) findViewById(R.id.money);
		refresh = (Button) findViewById(R.id.refresh);
		
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bagNum = 0;
				if(bags.length()!=0){
					bagNum = Integer.valueOf(bags.getText().toString());	
				}
				
				moneyTotal = money.getText().toString();
				System.out.println(moneyTotal + " "+bagNum);
				mDialogUtil.showProgressDialog();
				presenter.readScreen();
				
			}
		});
	}

	@Override
	public void scanTrayBagSuccess(String bagID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scanPileBagSuccess(String bagID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scanScreenSuccess(String screenID) {
		presenter.writeScreen(moneyTotal, bagNum);
	}

	@Override
	public void scanFailure(String error) {
		mDialogUtil.dismissProgressDialog();
		mDialogUtil.showDialog(error);
	}

	@Override
	public void writeScreenSuccess() {
		mDialogUtil.dismissProgressDialog();
		presenter.light();
	}

	@Override
	public void writeScreenFailure() {
		mDialogUtil.dismissProgressDialog();
		mDialogUtil.showDialog("写屏失败");
	}
}
