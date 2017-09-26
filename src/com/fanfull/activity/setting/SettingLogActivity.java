package com.fanfull.activity.setting;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.adapter.CommonAdapter;
import com.android.adapter.ViewHolder;
import com.fanfull.fff.R;
import com.fanfull.hardwareAction.RFIDOperation;
import com.fanfull.utils.LogsUtil;

public class SettingLogActivity extends Activity {
	private final static String TAG = SettingLogActivity.class.getSimpleName();
	private String mCurrentPath = "/data/data/com.fanfull.fff/logs/";
	private InputStream dataInputStream = null;
	private GridView mGridView;
	private ArrayList<String> list = new ArrayList<String>();
	
	private ScrollView mSView;
	private TextView mTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.log_activity_main);
		
		mGridView = (GridView) findViewById(R.id.gridView1);
		File file = new File(mCurrentPath);
		
		mSView = (ScrollView) findViewById(R.id.scrollView1);
		mTextView= (TextView) findViewById(R.id.textView1);
		
		if(!file.exists() || !file.isDirectory()){
			LogsUtil.d("FileDir", "目录夹空");
			mGridView.setVisibility(View.GONE);
			mSView.setVisibility(View.VISIBLE);
			mTextView.setTextColor(Color.RED);
			return;
		}else {
			File[] files = file.listFiles();
			if(files == null){
				LogsUtil.d("NULL", "读取目录出错");
				return;
			}
			LogsUtil.d("files", "files.len="+files.length);
			
			if(files.length == 0){
				mGridView.setVisibility(View.GONE);
				mSView.setVisibility(View.VISIBLE);
				mTextView.setTextColor(Color.RED);
				return;
			}else {
				mTextView.setTextColor(Color.WHITE);
				for (File file2 : files) {
					list.add(file2.getName());
				}
				mGridView.setAdapter(new CommonAdapter<String>(getApplicationContext(),list,R.layout.file_item)  {
			
					@Override
					public void convert(ViewHolder helper, String item, boolean isSelect) {
						// TODO Auto-generated method stub
						helper.setText(R.id.textView1, item);
					}
			
				});
				
				mGridView.setOnItemClickListener(new OnItemClickListener() {
			
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						byte[] b  = null;
						try {
							//	randomAccessFile = new RandomAccessFile(filePath,"rw");
							InputStream in = new FileInputStream(mCurrentPath+list.get(position));
							long len = getFileLength(mCurrentPath+list.get(position));
							dataInputStream = new DataInputStream(in);
							if(null != dataInputStream){
			//							Log.d(TAG, "Size="+dataInputStream.)
								b = new byte[(int) len];
								dataInputStream.read(b);
							}
							
							if(b != null){
								mTextView.setText("");
								mGridView.setVisibility(View.GONE);
								mSView.setVisibility(View.VISIBLE);
								String s = new String(b);
								mTextView.setText(s);
								
							}else {
								Log.d(TAG, "读取数据空");
							}
							
							} catch (FileNotFoundException e) {
								Log.d(TAG, "Exception :"+e.getMessage());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				});
			}
		
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		RFIDOperation.getInstance().closeRf();
	}
	public long getFileLength( String path){
		long i =0;
		try {
			i= new File(path).length();
		} catch (Exception e) {
		}
		return i;
	}
	
	public void onClickBack (View view){
		if(mGridView.getVisibility() == View.GONE && !"当前没有日志信息".equals(mTextView.getText().toString())){
			mGridView.setVisibility(View.VISIBLE);
			mSView.setVisibility(View.GONE);
			mTextView.setText("");
		}else {
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mGridView.getVisibility() == View.GONE && !"当前没有日志信息".equals(mTextView.getText().toString())){
			mGridView.setVisibility(View.VISIBLE);
			mSView.setVisibility(View.GONE);
			mTextView.setText("");
		}else {
			super.onBackPressed();
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(mGridView.getVisibility() == View.GONE && !"当前没有日志信息".equals(mTextView.getText().toString())){
				mGridView.setVisibility(View.VISIBLE);
				mSView.setVisibility(View.GONE);
				mTextView.setText("");
				return true;
			}else {
				super.onBackPressed();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
