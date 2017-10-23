package com.orsoul.view;

import com.fanfull.fff.R;
import com.fanfull.utils.LogsUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class ProcessView extends View{

	private boolean isShowText;
	private int mTextPos = 1;
	private int mColorHead ;
	private int mColorOut ;
	private int mColorText;
	
	private Paint mPaint;
	
    private static final int DEFAULT_MIN_WIDTH = 400; 
    private static final int RED = 230, GREEN = 85, BLUE = 35; 
    private static final int MIN_ALPHA = 30; 
    private static final int MAX_ALPHA = 255; 
    private static final float RaduisPercent = 0.65f; 
    private static final float WidthPercent = 0.08f; 
    private static final float MIDDLE_WAVE_RADUIS_PERCENT = 0.9f; 
    private static final float WAVE_WIDTH = 5f;
	
    //圆环颜色
    private static int[] Colors = new int[]{
            Color.argb(MAX_ALPHA, RED, GREEN, BLUE),
            Color.argb(MIN_ALPHA, RED, GREEN, BLUE),
            Color.argb(MIN_ALPHA, RED, GREEN, BLUE)};

    private float width; //自定义view的宽度
    private float height; //自定义view的高度
    private float currentAngle = 0f; //当前旋转角度
    private float raduis; //自定义view的最大半径
    private float firstWaveRaduis;
    private float secondWaveRaduis;
	
    private boolean mBreakLoad = false;
	public ProcessView(Context context) {
		super(context);
		init ();
	}

    private Thread thread = new Thread(){
        @Override
        public void run() {
            while(!mBreakLoad){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                postInvalidate();
            }
        }
    };
	
	public ProcessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init ();
		TypedArray tArray = context.getTheme().obtainStyledAttributes(attrs,R.styleable.ProcessChart, 0, 0);
	    
		try {
			isShowText = tArray.getBoolean(R.styleable.ProcessChart_showText, false);
		    mTextPos = tArray.getInteger(R.styleable.ProcessChart_labelPosition, 32);
		    mColorHead = tArray.getColor(R.styleable.ProcessChart_colorhead, Color.RED);
		    mColorOut = tArray.getColor(R.styleable.ProcessChart_colorout, Color.BLUE);
		    mColorText = tArray.getColor(R.styleable.ProcessChart_colortext, Color.BLUE);
		    LogsUtil.d("isShowText="+isShowText);
		    LogsUtil.d("mTextPos="+mTextPos);
		    LogsUtil.d("mColorIn="+mColorHead);
		    LogsUtil.d("mColorOut="+mColorOut);
	    } finally {
	    	 tArray.recycle();
	    }
	   
	    
	}
	
	public ProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init ();
	}
	
	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		thread.start();
	}
	
    private void resetParams() {
        width = getWidth();
        height = getHeight();
        raduis = Math.min(width, height)/2;
    }
    
    private void initPaint() {
    	mPaint.reset();
        mPaint.setAntiAlias(true);
    }
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		resetParams ();
		
		
	    Rect targetRect = new Rect((int)width/2, (int)height/2, 112, 145);  //左上右下
	    mPaint.setStrokeWidth(1);  
	    mPaint.setTextSize(32);  
	    String testString = "请对准...";  
	    mPaint.setColor(Color.TRANSPARENT);  
	    canvas.drawRect(targetRect, mPaint);  
	    mPaint.setColor(mColorText);  
	    FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();  
	    int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;  
	    mPaint.setTextAlign(Paint.Align.CENTER);  
	    canvas.drawText(testString, targetRect.centerX(), baseline, mPaint);  
		
		
		 initPaint();
        //将画布中心设为原点(0,0), 方便后面计算坐标
        canvas.translate(width / 2, height / 2);

        //转起来
        canvas.rotate(-currentAngle, 0, 0);
        if (currentAngle >= 360f){
            currentAngle = currentAngle - 360f;
        } else{
            currentAngle = currentAngle + 2f;
        }

        //画渐变圆环
        float Width = raduis * WidthPercent;//圆环宽度
        //圆环外接矩形
        RectF rectF = new RectF(-raduis * RaduisPercent, -raduis * RaduisPercent, raduis * RaduisPercent, raduis * RaduisPercent);
        initPaint();
        mPaint.setStrokeWidth(Width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(new SweepGradient(0, 0, Colors, null));
        canvas.drawArc(rectF, 0, 360, false, mPaint);
        
        //画旋转头部圆
        initPaint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorHead);//头颜色
        canvas.drawCircle(raduis * RaduisPercent, 0, Width / 2, mPaint);
//
        //实现类似水波涟漪效果
        initPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(WAVE_WIDTH);
        secondWaveRaduis = calculateWaveRaduis(secondWaveRaduis);
        firstWaveRaduis = calculateWaveRaduis(secondWaveRaduis + raduis*(MIDDLE_WAVE_RADUIS_PERCENT - RaduisPercent) - raduis*WidthPercent/2);
        mPaint.setColor(Color.argb(calculateWaveAlpha(secondWaveRaduis), RED, GREEN, BLUE));
        canvas.drawCircle(0, 0, secondWaveRaduis, mPaint); //画第二个圆（初始半径较小的）

        initPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(WAVE_WIDTH);
        mPaint.setColor(Color.argb(calculateWaveAlpha(firstWaveRaduis), RED, GREEN, BLUE));
        canvas.drawCircle(0, 0, firstWaveRaduis, mPaint); //画第一个圆（初始半径较大的）
	}
	
    /**
     * 计算波纹圆的半径
     * @param waveRaduis
     * @return
     */
    private float calculateWaveRaduis(float waveRaduis){
        if(waveRaduis < raduis*RaduisPercent + raduis*WidthPercent/2){
            waveRaduis = raduis*RaduisPercent + raduis*WidthPercent/2;
        }
        if(waveRaduis > raduis*MIDDLE_WAVE_RADUIS_PERCENT + raduis*(MIDDLE_WAVE_RADUIS_PERCENT - RaduisPercent) - raduis*WidthPercent/2){
            waveRaduis = waveRaduis - (raduis*MIDDLE_WAVE_RADUIS_PERCENT + raduis*(MIDDLE_WAVE_RADUIS_PERCENT - RaduisPercent) - raduis*WidthPercent/2) + raduis*WidthPercent/2 + raduis*RaduisPercent;
        }
            waveRaduis += 0.6f;
        return waveRaduis;
    }

    /**
     * 根据波纹圆的半径计算不透明度
     * @param waveRaduis
     * @return
     */
    private int calculateWaveAlpha(float waveRaduis){
        float percent = (waveRaduis-raduis*RaduisPercent-raduis*WidthPercent/2)/(raduis-raduis*RaduisPercent-raduis*WidthPercent/2);
        if(percent >= 1f){
            return 0;
        }else{
            return (int) (MIN_ALPHA*(1f-percent));
        }
    }
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mesure(widthMeasureSpec), mesure(heightMeasureSpec));
	}

	private int mesure(int widthMeasureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}else {
			if(specMode == MeasureSpec.AT_MOST){
				result = Math.min(specSize, result);
			}
		}
		return result;
	}
	
	public void startLoad (){
		
	};
	
	public void stopLoad (){
		mBreakLoad = true;
	};
}
