package com.fanfull.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanfull.fff.R;



/**
 * 自定义控件实现 数字的输入
 * 
 * @author
 * 
 *         2015-10-8
 */
public class OutStoreTaskInputerView extends LinearLayout {

	private CheckBox mCb;
	private EditText mEtNumber;
	private Button mBtnPlus;
	private Button mBtnMinus;

	public OutStoreTaskInputerView(Context context) {
		super(context, null);
		// TODO Auto-generated constructor stub
	}

	public OutStoreTaskInputerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		/**
		 * 初始化控件
		 */
		View view = LayoutInflater.from(context).inflate(
				R.layout.myview_outstore_task_inputer, null);
		mBtnPlus = (Button) view.findViewById(R.id.tv_number_inputer_plus);
		mBtnPlus.setEnabled(false);

		mBtnMinus = (Button) view.findViewById(R.id.tv_number_inputer_minus);
		mBtnMinus.setEnabled(false);

		mEtNumber = (EditText) view.findViewById(R.id.et_number_inputer_num);
		mEtNumber.setEnabled(false);
		mCb = (CheckBox) view.findViewById(R.id.cb_number_inputer2);
		mCb.setChecked(false);
		addView(view);

		/*
		 * 首先通过context.obtainStyledAttributes获得所有属性数组;
		 * 然后通过TypedArray类的getXXXX()系列接口获得相应的值。
		 */
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.OutStoreTaskInputerView);

		CharSequence text = typedArray
				.getText(R.styleable.OutStoreTaskInputerView_android_text);
		if (text != null) {
			mCb.setText(text);
		}

		int color = typedArray
				.getColor(
						R.styleable.OutStoreTaskInputerView_android_textColor,
						0xffffff);
		mCb.setTextColor(color);

		typedArray.recycle();

		initEvent();
	}

	private void initEvent() {
		mBtnPlus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int num = getNumber() + 1;
				if (num <= 30) {
					setNumber(num);
				}
			}
		});
		mBtnMinus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int num = getNumber() - 1;
				if (0 < num) {
					setNumber(num);
				} else {
					setNumber(1);
				}
			}
		});
		mCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mEtNumber.setEnabled(isChecked);
				mBtnPlus.setEnabled(isChecked);
				mBtnMinus.setEnabled(isChecked);
			}
		});
		// 限制 编辑框 输入的 数值 范围 在 1-30范围内
		mEtNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					Editable text = mEtNumber.getText();
					int num = Integer.parseInt(text.toString());
					if (30 < num) {
						text.delete(text.length() - 1, text.length());
					}
				} catch (NumberFormatException nfe) {
					// 编辑框可能会出现 空字符串, 忽略 parseInt()抛出的异常
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public int getNumber() {
		if (!isChecked()) {
			return 0;
		}
		try {
			return Integer.parseInt(mEtNumber.getText().toString());
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public String getStringNumber() {
		if (!isChecked()) {
			return "00";
		}
		try {
			String reVal = mEtNumber.getText().toString();
			if (reVal.length() < 2) {
				reVal = 0 + reVal;
			}
			return reVal;
		} catch (NumberFormatException nfe) {
			return "00";
		}
	}

	public boolean isChecked() {
		return mCb.isChecked();
	}

	/**
	 * @param text
	 * 设置 描述信息
	 */
	public void setDesc(Object text) {
		mCb.setText(String.valueOf(text));
	}

	/**
	 * @param num
	 * 设置 编辑框内 数字
	 */
	public void setNumber(int num) {
		mEtNumber.setText(String.valueOf(num));
	}

	public void setChecked(boolean checked) {
		mCb.setChecked(checked);
	}

	@Override
	public void setFocusable(boolean focusable) {
		mEtNumber.setFocusable(focusable);
	}

	public void setEtOnEditorActionListener(
			TextView.OnEditorActionListener listener) {
		mEtNumber.setOnEditorActionListener(listener);
	}
	public void setInputType(int inputType) {
		mEtNumber.setInputType(inputType);
	}
}
