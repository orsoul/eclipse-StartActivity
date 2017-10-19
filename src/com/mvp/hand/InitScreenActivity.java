package com.mvp.hand;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.entity.PileInfo;
import com.fanfull.base.BaseActivity;
import com.fanfull.fff.R;
import com.fanfull.utils.DialogUtil;
import com.fanfull.utils.ToastUtil;
import com.fanfull.view.MyRadioGroup;
import com.mvp.hand.HandContract.Presenter;

public class InitScreenActivity extends BaseActivity implements
		com.mvp.refreshscreen.RefreshScreenContract.View,com.mvp.hand.HandContract.View{
	//private Spinner bizhong;
	private Button quebie;
	private RadioGroup leixing;
	private MyRadioGroup leixing2;
	private RadioButton wanzheng, cansun,yuanfeng;
	private RadioButton weifudian, yifudian, weiqingfen, yiqingfen;

	private RadioGroup version, version1, version2;
	private RadioButton sitao, wutao;
	private RadioButton baling, jiuling, jiuliu;
	private RadioButton jiujiu, lingwu, yiwu;

	private MyRadioGroup mRadioGroup;
    private RadioButton p100,p50,p20,p10,p5,p1,p05,p02,p01;
    private RadioButton y1,y05,y01,y005,y002,y001;
    private ImageView line;
    
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
		//bizhong = (Spinner) findViewById(R.id.bizhong);
		quebie = (Button) findViewById(R.id.quebie);
		leixing = (RadioGroup) findViewById(R.id.leixing);
		leixing2 = (MyRadioGroup) findViewById(R.id.leixing2);

		wanzheng = (RadioButton) findViewById(R.id.wanzheng);
		cansun = (RadioButton) findViewById(R.id.cansun);
		yuanfeng = (RadioButton) findViewById(R.id.yuanfeng);

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
		
		line = (ImageView) findViewById(R.id.line);
		
		init = (Button) findViewById(R.id.init);

		leixing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == wanzheng.getId()) {
					leixing1_str = "完整";
					leixing2.setVisibility(View.VISIBLE);
					line.setVisibility(View.VISIBLE);
				} else if (checkedId == cansun.getId()) {
					leixing1_str = "残损";
					leixing2.setVisibility(View.VISIBLE);
					line.setVisibility(View.VISIBLE);
				} else if(checkedId == yuanfeng.getId()){
					leixing1_str = "原封";
					leixing2.setVisibility(View.GONE);
					line.setVisibility(View.GONE);
				}
			}
		});

		leixing2.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(MyRadioGroup group, int checkedId) {
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
				if(bagModel != null){
					if(leixing1_str.equals("原封")){
						leixing2_str = "已清分";
					}
					presenter.create(bagModel, leixing1_str, leixing2_str, "-1");
					
				}else{
					ToastUtil.showToastInCenter("请选择券别");
				}
				
			}
		});
		/*bizhong.setOnItemSelectedListener(new OnItemSelectedListener() {
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
		});*/
		
		final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quebie, null);
		mRadioGroup = (MyRadioGroup) dialogView.findViewById(R.id.content);
        p100 = (RadioButton) dialogView.findViewById(R.id.p100);
        p50 = (RadioButton) dialogView.findViewById(R.id.p50);
        p20 = (RadioButton) dialogView.findViewById(R.id.p20);
        p10 = (RadioButton) dialogView.findViewById(R.id.p10);
        p5 = (RadioButton) dialogView.findViewById(R.id.p5);
        p1 = (RadioButton) dialogView.findViewById(R.id.p1);
        p05 = (RadioButton) dialogView.findViewById(R.id.p05);
        p02 = (RadioButton) dialogView.findViewById(R.id.p02);
        p01 = (RadioButton) dialogView.findViewById(R.id.p01);
        y1 = (RadioButton) dialogView.findViewById(R.id.y1);
        y05 = (RadioButton) dialogView.findViewById(R.id.y05);
        y01 = (RadioButton) dialogView.findViewById(R.id.y01);
        y005 = (RadioButton) dialogView.findViewById(R.id.y005);
        y002 = (RadioButton) dialogView.findViewById(R.id.y002);
        y001 = (RadioButton) dialogView.findViewById(R.id.y001);
        mDialog = new AlertDialog.Builder(this).create();
        quebie.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				mDialog.show();
		        Window window = mDialog.getWindow();
		        window.getDecorView().setPadding(2, 34, 2, 2);
		        window.setGravity(Gravity.CENTER);
		        window.setContentView(dialogView);
		        WindowManager.LayoutParams lp = window.getAttributes();
		        lp.width = WindowManager.LayoutParams.FILL_PARENT;
		        lp.height = WindowManager.LayoutParams.FILL_PARENT;
		        window.setAttributes(lp);
			}
			
		});
        
        mRadioGroup.setOnCheckedChangeListener(new MyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MyRadioGroup group, int checkedId) {
            	mDialog.dismiss();
                mDialog.cancel();
                if(checkedId == p100.getId()){
                    Log.d(TAG, "选中："+p100.getText());
                    quebie.setText(p100.getText());
                    bagModel = "纸100元(200万-5.5)";
                }else if (checkedId == p50.getId()){
                    Log.d(TAG, "选中："+p50.getText());
                    quebie.setText(p50.getText());
                    bagModel = "纸50元(100万-5.5)";
                }else if (checkedId == p20.getId()){
                    Log.d(TAG, "选中："+p20.getText());
                    quebie.setText(p20.getText());
                    bagModel = "纸20元(40万-5.5)";
                }else if (checkedId == p10.getId()){
                    Log.d(TAG, "选中："+p10.getText());
                    quebie.setText(p10.getText());
                    bagModel = "纸10元(20万-5.5)";
                }else if (checkedId == p5.getId()){
                    Log.d(TAG, "选中："+p5.getText());
                    quebie.setText(p5.getText());
                    bagModel ="纸5元(10万-5.5)";
                }else if (checkedId == p1.getId()){
                    Log.d(TAG, "选中："+p1.getText());
                    quebie.setText(p1.getText());
                    bagModel ="纸1元(2万-5.5)";
                }else if(checkedId == p05.getId()){
                    Log.d(TAG, "选中："+p05.getText());
                    quebie.setText(p05.getText());
                    bagModel = "纸5角(2.5万-5.5)";
                }else if(checkedId == p02.getId()){
                    Log.d(TAG, "选中："+p02.getText());
                    quebie.setText(p02.getText());
                    bagModel="纸2角(1万-5.5)";
                }else if(checkedId == p01.getId()){
                    Log.d(TAG, "选中："+p01.getText());
                    quebie.setText(p01.getText());
                    bagModel = "纸1角(0.5万-5.5)";
                }else if(checkedId == y1.getId()){
                    Log.d(TAG, "选中："+y1.getText());
                    quebie.setText(y1.getText());
                    bagModel="硬1元(5万-5.5)";
                }else if(checkedId == y05.getId()){
                    Log.d(TAG, "选中："+y05.getText());
                    quebie.setText(y05.getText());
                    bagModel="硬5角(2.5万-5.5)";
                }else if(checkedId == y01.getId()){
                    Log.d(TAG, "选中："+y01.getText());
                    quebie.setText(y01.getText());
                    bagModel="硬1角(0.5万-5.5)";
                }else if(checkedId == y005.getId()){
                    Log.d(TAG, "选中："+y005.getText());
                    quebie.setText(y005.getText());
                    bagModel="硬5分(0.5万-5.5)";
                }else if(checkedId == y002.getId()){
                    Log.d(TAG, "选中："+y002.getText());
                    quebie.setText(y002.getText());
                    bagModel="硬2分(0.2万-5.5)";
                }else if(checkedId == y001.getId()){
                    Log.d(TAG, "选中："+y001.getText());
                    quebie.setText(y001.getText());
                    bagModel="硬1分(0.1万-5.5)";
                }
            }
        });
	}
	
	private AlertDialog mDialog;

	private HandPresenter presenter = new HandPresenter(this,this);

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
		presenter.insert();
	}

	@Override
	public void error(String error) {
		dialogUtil.dismissProgressDialog();
		dialogUtil.showDialog(error);

	}

	@Override
	public void createPileSuccess() {
		ToastUtil.showToastInCenter("创建堆成功，写入屏数据");
		if(leixing1_str.equals("原封")){
			presenter.initScreen(bagModel, "完整|原封", version_value, series);	
		}else{
			presenter.initScreen(bagModel, leixing1_str + "|"
					+ leixing2_str, version_value, series);		
		}
		
	}

	@Override
	public void createPileFailure() {
		ToastUtil.showToastInCenter("创建堆失败");
	}

	@Override
	public void onFailure(String error) {
		ToastUtil.showToastInCenter("网络错误");
	}

}
