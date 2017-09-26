package com.fanfull.view;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.fanfull.fff.R;

public class SearchDevicesView extends RelativeLayout{
	public Context context;
	public static final String TAG = "SearchDevicesView";
	
	@SuppressWarnings("unused")
	private long TIME_DIFF = 1500;
	
	private float offsetArgs = 0;
	private boolean isSearching = false;
	private Bitmap bitmap;
	private Bitmap bitmap1;
	private Bitmap bitmap2;
	
	private OnStatusChangedListener onStatusChangedListener;
	
	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
		offsetArgs = 0;
		
		if(!isSearching) {
			if(onStatusChangedListener != null) onStatusChangedListener.OnStatusChanged(false);
		}else{
			if(onStatusChangedListener != null) onStatusChangedListener.OnStatusChanged(true);
		}
		
		invalidate();
		
		
	}

	public SearchDevicesView(Context context) {
		super(context);
		this.context = context;
		initBitmap();
	}
	
	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initBitmap();
	}
	
	private void initBitmap(){
		if(bitmap == null){
			bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gplus_search_bg));
		}
		if(bitmap1 == null){
			bitmap1 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.locus_round_click));
		}
		if(bitmap2 == null){
			bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gplus_search_args_1));
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	
		//120---263----129----263
		canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, null);
		
		if(isSearching){
			
			Rect rMoon = new Rect(getWidth()/2-bitmap2.getWidth(),getHeight()/2,getWidth()/2,getHeight()/2+bitmap2.getHeight()); 
			canvas.rotate(offsetArgs,getWidth()/2,getHeight()/2);
			canvas.drawBitmap(bitmap2,null,rMoon,null);
			offsetArgs = offsetArgs + 3;
		}else{
		//	canvas.drawBitmap(bitmap2,  getWidth() / 2  - bitmap2.getWidth() , getHeight() / 2, null);
		}
		
		canvas.drawBitmap(bitmap1,  getWidth() / 2 - bitmap1.getWidth() / 2, getHeight() / 2 - bitmap1.getHeight() / 2, null);
		
		if(isSearching) invalidate();
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {	
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:		
//			handleActionDownEvenet(event);
//			return true;
//		case MotionEvent.ACTION_MOVE: 
//			return true;
//		case MotionEvent.ACTION_UP:
//			return true;
//		}
//		return super.onTouchEvent(event);
//	}
	
	
	public void OnStatusChangedListener(OnStatusChangedListener listener){
		this.onStatusChangedListener = listener;
	}
	//对外提供改变的接口
	public interface OnStatusChangedListener{
		public void OnStatusChanged( boolean checkState);
	}
	public boolean getSearchStatus() {
		// TODO Auto-generated method stub
		return isSearching;
	}
}