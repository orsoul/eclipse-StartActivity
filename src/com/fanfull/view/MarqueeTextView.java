package com.fanfull.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

/*
 * 自定义 TextView, 实现 滚动文本
 */
/**
 * 
 * TODO 单行文本跑马灯控件
 *
 * @author hohu
 * @version 1.3
 * Create At : 2016-1-24 下午09:35:03
 */
public class MarqueeTextView extends TextView implements OnClickListener {

//	public MarqueeTextView(Context context) {
//		super(context);
//		// TODO Auto-generated constructor stub
//	}
//	
//	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		// TODO Auto-generated constructor stub
//	}
//
//	public MarqueeTextView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	@ExportedProperty(category = "focus")
//	public boolean isFocused() {
//		// TODO Auto-generated method stub
//		return true;
//	}

    public final static String TAG = MarqueeTextView.class.getSimpleName();
     
    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "hhojj";//文本内容
  
    public MarqueeTextView(Context context) {
        super(context);
        initView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }



    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
     
    /**
     * 初始化控件
     */
    private void initView()
    {
        setOnClickListener(this);
    }
     
    /**
     * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
     */
    public void init(WindowManager windowManager)
    {
        paint = getPaint();
        text = getText().toString();
        textLength = paint.measureText(text);
        viewWidth = getWidth();
        if(viewWidth == 0)
        {
            if(windowManager != null)
            {
                Display display = windowManager.getDefaultDisplay();
                viewWidth = display.getWidth();
            }
        }
        step = textLength;
        temp_view_plus_text_length = viewWidth + textLength;
        temp_view_plus_two_text_length = viewWidth + textLength * 2;
        y = getTextSize() + getPaddingTop();
    }
     
    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
         
        ss.step = step;
        ss.isStarting = isStarting;
         
        return ss;
         
    }
     
    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
         
        step = ss.step;
        isStarting = ss.isStarting;

    }
     
    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;
        public float step = 0.0f;
        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
             
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            if(b != null && b.length > 0)
                isStarting = b[0];
            step = in.readFloat();
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll()
    {
        isStarting = true;
        invalidate();
    }
     
    /**
     *停止滚动
     */
    public void stopScroll()
    {
        isStarting = false;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
     try
     {
      canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
            if(!isStarting)
            {
                return;
            }
            step += 1.5;
            if(step > temp_view_plus_two_text_length)
                step = textLength;
            invalidate();

     }catch(NullPointerException e)
     {
     System.out.println("发生异常的原因为 :"+e.getMessage());
     }
     
    }

    @Override
    public void onClick(View v) {
        if(isStarting)
            stopScroll();
        else
            startScroll();
         
    }

}

