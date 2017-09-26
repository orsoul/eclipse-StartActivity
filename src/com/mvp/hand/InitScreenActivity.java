package com.mvp.hand;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.entity.PileInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.mvp.hand.HandContract.Presenter;

public class InitScreenActivity extends BaseActivity implements
		com.mvp.refreshscreen.RefreshScreenContract.View {
	private Spinner bizhong;
	private RadioGroup leixing, leixing2;
	private RadioButton wanzheng, cansun;
	private RadioButton weifudian, yifudian, weiqingfen, yiqingfen;

	private RadioGroup version, version1, version2;
	private RadioButton sitao, wutao;
	private RadioButton baling, jiuling, jiuliu;
	private RadioButton jiujiu, lingwu, yiwu;

	private Button init;
	private DialogUtil dialogUtil = new DialogUtil(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_init);

		findView();
	}

	private String leixing1_str = "完整";
	private String leixing2_str = "已清分";
	private int version_value= 80;
	private String series="4";

	private void findView() {
		bizhong = (Spinner) findViewById(R.id.bizhong);
		leixing = (RadioGroup) findViewById(R.id.leixing);
		leixing2 = (RadioGroup) findViewById(R.id.leixing2);

		wanzheng = (RadioButton) findViewById(R.id.wanzheng);
		cansun = (RadioButton) findViewById(R.id.cansun);

		weifudian = (RadioButton) findViewById(R.id.weifudian);
		yifudian = (RadioButton) findViewById(R.id.yifudian);
		weiqingfen = (RadioButton) findViewById(R.id.weiqingfen);
		yiqingfen = (RadioButton) findViewById(R.id.yiqingfen);

		version = (RadioGroup) findViewById(R.id.version);
		version1 = (RadioGroup) findViewById(R.id.version1);
		version2 = (RadioGroup) findViewById(R.id.version2);

		sitao = (RadioButton) findViewById(R.id.sitao);
		wutao = (RadioButton) findViewById(R.id.wutao);

		baling = (RadioButton) findViewById(R.id.baling);
		jiuling = (RadioButton) findViewById(R.id.jiuling);
		jiuliu = (RadioButton) findViewById(R.id.jiuliu);

		jiujiu = (RadioButton) findViewById(R.id.jiujiu);
		lingwu = (RadioButton) findViewById(R.id.lingwu);
		yiwu = (RadioButton) findViewById(R.id.yiwu);

		init = (Button) findViewById(R.id.init);

		leixing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == wanzheng.getId()) {
					leixing1_str = "完整";
				} else if (checkedId == cansun.getId()) {
					leixing1_str = "残损";
				}
			}
		});

		leixing2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == weifudian.getId()) {
					leixing2_str = "未复点";
				} else if (checkedId == yifudian.getId()) {
					leixing2_str = "已复点";
				} else if (checkedId == weiqingfen.getId()) {
					leixing2_str = "未清分";
				} else if (checkedId == yiqingfen.getId()) {
					leixing2_str = "已清分";
				}

			}
		});

		version.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == sitao.getId()) {
					series = "4";
					version_value = 80;
					version1.setVisibility(View.VISIBLE);
					version2.setVisibility(View.GONE);
				} else if (checkedId == wutao.getId()) {
					series = "5";
					version_value = 99;
					version1.setVisibility(View.GONE);
					version2.setVisibility(View.VISIBLE);
				}
			}
		});

		version1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == baling.getId()) {
					version_value = 80;
				} else if (checkedId == jiuling.getId()) {
					version_value = 90;
				} else if (checkedId == jiuliu.getId()) {
					version_value = 96;
				}
			}
		});

		version2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == jiujiu.getId()) {
					version_value = 99;
				} else if (checkedId == lingwu.getId()) {
					version_value = 5;
				} else if (checkedId == yiwu.getId()) {
					version_value = 15;
				}
			}
		});
		init.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogUtil.showProgressDialog();
				presenter.initScreen(bagModel, leixing1_str + "|"
						+ leixing2_str, version_value, series);
			}
		});

		bizhong.setOnItemSelectedListener(new OnItemSelectedListener() {
			String[] values = getResources().getStringArray(R.array.bagModel);

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				bagModel = values[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				bagModel = values[0];
			}
		});

	}

	private HandPresenter presenter = new HandPresenter(this);

	private String bagModel;

	@Override
	public void scanBagSuccess(String bagID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanBagFailure(String error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanScreenSuccess(String screenID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanScreenFailure(String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshData(PileInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeScreenSuccess() {
		dialogUtil.dismissProgressDialog();
		presenter.light();
	}

	@Override
	public void error(String error) {
		dialogUtil.dismissProgressDialog();
		dialogUtil.showDialog(error);

	}

}
