package com.fanfull.activity.other;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.ViewUtil;
import com.fanfull.view.OutStoreTaskInputerView;
import com.orsoul.view.SpinerAdapter;
import com.orsoul.view.SpinerAdapter.IOnItemSelectListener;
import com.orsoul.view.SpinerPopWindow;

/**
 * @author Administrator 出库 任务单 录入, 暂未使用
 */
public class OutStoreInputTask extends BaseActivity {
	private com.fanfull.view.OutStoreTaskInputerView mV100;
	private com.fanfull.view.OutStoreTaskInputerView mV50;
	private com.fanfull.view.OutStoreTaskInputerView mV20;
	private com.fanfull.view.OutStoreTaskInputerView mV10;
	private com.fanfull.view.OutStoreTaskInputerView mV5;
	private com.fanfull.view.OutStoreTaskInputerView mV1;
	private EditText mEt;
	private Button mBtnOk;
	private Button mBtnCancel;

	private AlertDialog diaNumberIputer;
	private SpinerAdapter mQuAdapter;
	private List<String>  mQuListType = new ArrayList<String>();  //类型列表

	 //设置PopWindow
    private SpinerPopWindow mQuSpinerPopWindow;
    private String mQuString[];
    private TextView mQuTView;
    
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		setContentView(R.layout.fragment_out_store);

		mBtnOk = (Button) findViewById(R.id.btn_out_store_ok);
		mBtnOk.setOnClickListener(this);

		mBtnCancel = (Button) findViewById(R.id.btn_out_store_cancel);
		mBtnCancel.setOnClickListener(this);

		mEt = (EditText) findViewById(R.id.et_out_store_total);
		mEt.setOnClickListener(this);

		// mV100 = (OutStoreTaskInputer) findViewById(R.id.v_out_store_100);
		// mV50 = (OutStoreTaskInputer) findViewById(R.id.v_out_store_50);
		// mV20 = (OutStoreTaskInputer) findViewById(R.id.v_out_store_20);
		// mV10 = (OutStoreTaskInputer) findViewById(R.id.v_out_store_10);
		// mV5 = (OutStoreTaskInputer) findViewById(R.id.v_out_store_5);
		// mV1 = (OutStoreTaskInputer) findViewById(R.id.v_out_store_1);
		ViewUtil.requestFocus(mBtnOk);
		mQuTView = (TextView) findViewById(R.id.tv_value_bank);
		
	    //初始化地区数据
        mQuString = getResources().getStringArray(R.array.array_name_bank);
        for(int i = 0; i < mQuString.length; i++){
            mQuListType.add(mQuString[i]);
        }

        mQuAdapter = new SpinerAdapter(this,mQuListType);
        //初始化地区下拉PopWindow
        mQuSpinerPopWindow = new SpinerPopWindow(this);
        mQuSpinerPopWindow.setAdatper(mQuAdapter);
        mQuSpinerPopWindow.setItemListener(new IOnItemSelectListener() {
			
			@Override
			public void onItemClick(int pos) {
				// TODO Auto-generated method stub
				  if (pos >= 0 && pos <= mQuListType.size()){
			            String value = mQuListType.get(pos);
			            mQuTView.setText(value.toString());
			        }
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.et_out_store_total:
			showNumberIputer();
			break;
		case R.id.btn_out_store_ok:

			break;
		case R.id.btn_out_store_cancel:

			break;
	    case R.id.bt_dropdown_diqu:
			   showQuSpinWindow();
		default:
			break;
		}
	}

    /**
     * 显示银行下拉框
     */
    private void showQuSpinWindow(){
        mQuSpinerPopWindow.setWidth(mQuTView.getWidth());
        mQuSpinerPopWindow.showAsDropDown(mQuTView);
    }
	/**
	 * @return 统计袋数
	 */
	private int getTotalBag() {
		try {
			return mV100.getNumber() + mV50.getNumber() + mV20.getNumber()
					+ mV10.getNumber() + mV5.getNumber() + mV1.getNumber();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
	 * 显示 币种/袋数 输入
	 */
	private void showNumberIputer() {
		if (null == diaNumberIputer) {
			LayoutInflater factory = getLayoutInflater();
			final View v = factory.inflate(
					R.layout.dialog_out_store_number_input, null);
			mV100 = (OutStoreTaskInputerView) v.findViewById(R.id.v_out_store_100);
			mV50 = (OutStoreTaskInputerView) v.findViewById(R.id.v_out_store_50);
			mV20 = (OutStoreTaskInputerView) v.findViewById(R.id.v_out_store_20);
			mV10 = (OutStoreTaskInputerView) v.findViewById(R.id.v_out_store_10);
			mV5 = (OutStoreTaskInputerView) v.findViewById(R.id.v_out_store_5);
			mV1 = (OutStoreTaskInputerView) v.findViewById(R.id.v_out_store_1);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(v)
					.setTitle("设置币种/袋数")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									int totalBag = getTotalBag();
									if (0 != totalBag) {
										mEt.setText("合计袋数: "+String.valueOf(totalBag));
										mBtnOk.setEnabled(true);
									}
								}
							}).setNegativeButton("取消", null);
			diaNumberIputer = builder.create();
		} else if (!diaNumberIputer.isShowing()) {
			diaNumberIputer.show();
		}
	}
}
