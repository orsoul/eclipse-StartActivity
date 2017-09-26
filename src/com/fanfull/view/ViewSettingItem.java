package com.fanfull.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fanfull.fff.R;
import com.fanfull.view.SwitchButton.OnChangedListener;

/**
 * @author Administrator
 * @description  自定义组合控件
 */
public class ViewSettingItem extends FrameLayout {
	private TextView mTv;
	private SwitchButton mCb;
	private View mItem;
	private ItemCheckedChangeListener listener;

	/**
	 * 配置文件中，反射实例化设置属性参数
	 * 
	 * @param context
	 * @param attrs
	 */
	public ViewSettingItem(final Context context, AttributeSet attrs) {
		super(context, attrs);

		 mItem = View
				.inflate(getContext(), R.layout.view_setting_item, null);
		mTv = (TextView) mItem.findViewById(R.id.tv_v_setting);
		mCb = (SwitchButton) mItem.findViewById(R.id.cb_v_setting);
		addView(mItem);

		
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.ViewSettingItem);

		CharSequence text = typedArray
				.getText(R.styleable.ViewSettingItem_android_text);
		if (text != null) {
			mTv.setText(text);
		}
		typedArray.recycle();
		
		mCb.setOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(SwitchButton switchButton, boolean checkState) {
				// TODO Auto-generated method stub
				Resources resource = (Resources) context.getResources();
				ColorStateList csl = null;
				if (checkState) {
					csl = (ColorStateList) resource
							.getColorStateList(R.color.checked);
				} else {
					csl = (ColorStateList) resource
							.getColorStateList(R.color.sliverwhite);
				}
				mTv.setTextColor(csl);
				if (null != listener) {
					listener.onCheckedChanged(ViewSettingItem.this, checkState);
				}
			}
		});
//		mCb.setOnChangedListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				// TODO Auto-generated method stub
//				Resources resource = (Resources) context.getResources();
//				ColorStateList csl = null;
//				if (isChecked) {
//					csl = (ColorStateList) resource
//							.getColorStateList(R.color.checked);
//				} else {
//					csl = (ColorStateList) resource
//							.getColorStateList(R.color.sliverwhite);
//				}
//				mTv.setTextColor(csl);
//				if (null != listener) {
//					listener.onCheckedChanged(ViewSettingItem.this, isChecked);
//				}
//			}
//		});

		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCb.setChecked(!mCb.isChecked());
			}
		});

	}

	/**
	 * 代码实例化调用该构造函数
	 * 
	 * @param context
	 */
	public ViewSettingItem(Context context) {
		this(context, null);
	}

	public void setChecked(boolean checked) {
		mCb.setChecked(checked);
	}
	public boolean isChecked() {
		return mCb.isChecked();
	}

	public void setCheckedChangedListener(ItemCheckedChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * @param text
	 */
	public void setText(String text) {
		mTv.setText(text);
	}
	
	public interface ItemCheckedChangeListener {
		public void onCheckedChanged(ViewSettingItem view, boolean isChecked);
	}
	public void obtainFocus () {
		if(null != mItem) {
			mTv.setFocusable(true);
			mTv.setFocusableInTouchMode(false);
			mTv.requestFocus();
			mTv.requestFocusFromTouch();
		}
	}
}
