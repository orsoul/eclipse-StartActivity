package com.fanfull.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.fanfull.fff.R;

public class SwitchButton extends View {
	private String TAG = SwitchButton.class.getSimpleName();
	
	private Bitmap mOnBitmap;
	private Bitmap mOffBitmap;
	private boolean mNowStatus =  false;
	
	//当前的x�?
	private float mNowXpoint = 0;
	//按下时的x�?
	private float mDownXpoint = 0;
	
	private OnChangedListener mChangedListener;
	
	public SwitchButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initUi();
	}
	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initUi ();
	}


	private void initUi() {
		// TODO Auto-generated method stub
		mOnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_on_icon);
		mOffBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_off_icon);
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//定义画笔
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float xPoint = 0;
		
		//根据当前的x距离去绘制显示的背景图片 �?��者关状�?
		if(mNowXpoint ==  0){
			canvas.drawBitmap(mOffBitmap, matrix, paint);
		}else {
			canvas.drawBitmap(mOnBitmap, matrix, paint);
		}
	}
	public void setChecked(boolean ischeck){
		if(ischeck){
			mNowXpoint = mOnBitmap.getWidth()/2 ;
		}else {
			mNowXpoint = 0;
		}
		mNowStatus = ischeck;
		if(mChangedListener != null){
			mChangedListener.OnChanged(SwitchButton.this, mNowStatus);
		}
		//刷新界面
		invalidate();
	}
	public void setOnChangedListener(OnChangedListener listener){
		this.mChangedListener = listener;
	}
	public boolean isChecked (){
		return mNowStatus;
	}
	
	//对外提供改变的接口
	public interface OnChangedListener{
		public void OnChanged(SwitchButton switchButton, boolean checkState);
	}
}
