package com.fanfull.utils;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import com.fanfull.base.BaseApplication;

/**
 * 弹出Toast的一个工具类，这里主要是增加了对系统Toast背景的修改
 * 
 * @author Administrator
 * 
 */
public class ToastUtil {
	/**
	 * @param text
	 * @Description 在屏幕中间 弹出土司 , LENGTH_SHORT
	 */
	public static void showToastInCenter(Object text) {
		Toast toast = Toast.makeText(BaseApplication.getContext(), String.valueOf(text), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * @param text
	 * @param act
	 * @description 在UI线程,屏幕中间 弹出土司 , LENGTH_SHORT
	 */
	public static void showToastOnUiThreadInCenter(final Object text,
			Activity act) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(BaseApplication.getContext(),
						String.valueOf(text), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});

	}
}
