package com.fanfull.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fanfull.background.ActivityUtil;
import com.fanfull.contexts.StaticString;
import com.fanfull.fff.R;
import com.fanfull.utils.LogsUtil;

	/**
	 * @author Administrator 权限页面
	 * 
	 */
	public class PermissionActivity extends Activity {

		private static final String TAG = StartActivity.class.getSimpleName();
		
		private FrameLayout mPermissionContent;// 权限显示
		private TextView mTv1;
		private TextView mTv2;
		private TextView mTv3;
		private TextView mTv4;
		private TextView mTv5;
		private TextView mTv6;
		private TextView mTv7;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_permission);
			
			ActivityUtil.getInstance().addActivityToList(this);	
			
			mTv1 = (TextView) findViewById(R.id.cover_permission);
			mTv2 = (TextView) findViewById(R.id.in_permission);
			mTv3 = (TextView) findViewById(R.id.out_permission);
			mTv4 = (TextView) findViewById(R.id.check_permission);
			mTv5 = (TextView) findViewById(R.id.recheck_permission);
			mTv6 = (TextView) findViewById(R.id.open_permission);
			mTv7 = (TextView) findViewById(R.id.coverin_permission);
			
			
			
			mPermissionContent = (FrameLayout) findViewById(R.id.power_content);
			mPermissionContent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(PermissionActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				}
			});
			
			
			checkOpPermission ();
		
		}


		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			ActivityUtil.getInstance().removeActivityFromList(this);

		}
		@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					/**需要检测Activity 是否已经被销毁*/
					if(!PermissionActivity.this.isFinishing()){
						Intent intent = new Intent(PermissionActivity.this,
								MainActivity.class);
						startActivity(intent);
						finish();
					}
				}

			}, 3000);
		}
		
		private void checkOpPermission() {
			// TODO Auto-generated method stub
			if (StaticString.information != null) {
				LogsUtil.d(TAG, "---="+StaticString.information);//---=*01 01 1111113A 001#
				if ("01".equals(StaticString.information.substring(1, 3))) {
					String info1 = StaticString.information.substring(7, 8);
					if (info1.equals("1")) {
						mTv1.setText("封袋：允许");
					} else {
						mTv1.setText("封袋：不允许");
					}
					String info2 = StaticString.information.substring(8, 9);
					if (info2.equals("1")) {
						mTv2.setText("入库：允许");
					} else {
						mTv2.setText("入库：不允许");
					}
					String info3 = StaticString.information.substring(9, 10);
					if (info3.equals("1")) {
						mTv3.setText("出库：允许");
					} else {
						mTv3.setText("出库：不允许");
					}
					String info4 = StaticString.information.substring(10, 11);
					if (info4.equals("1")) {
						mTv4.setText("验封：允许");
					} else {
						mTv4.setText("验封：不允许");
					}
					String info5 = StaticString.information.substring(11, 12);
					if (info5.equals("1")) {
						mTv5.setText("复核：允许");
					} else {
						mTv5.setText("复核：不允许");
					}
					String info6 = StaticString.information.substring(12, 13);
					if (info6.equals("1")) {
						mTv6.setText("开袋：允许");
					} else {
						mTv6.setText("开袋：不允许");
					}
					String info7 = StaticString.information.substring(13, 14);
					if (info7.equals("1")) {
						mTv7.setText("封签并入库：允许");
					} else {
						mTv7.setText("封签并入库：不允许");
					}
				}
			}
		}

	}

