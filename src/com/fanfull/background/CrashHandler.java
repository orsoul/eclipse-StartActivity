package com.fanfull.background;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;

import com.fanfull.utils.DateUtils;
import com.fanfull.utils.LogsUtil;
import com.fanfull.utils.ToastUtil;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = CrashHandler.class.getSimpleName();

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
//		Intent intent1 = new Intent(mContext,SrceenOnOffService.class);
//		mContext.stopService(intent1);
        
//		FingerPrint.getInstance().closeFinger();
//		RFIDOperation.getInstance().close();
//		UHFOperation.getCloseInstance().close();
//		BarCodeOperation.getCloseInstance().close();
//		OLEDOperation.getInstance().close();
	
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
			LogsUtil.e(TAG, "系统处理异常");
			ex.printStackTrace();
		} else {
//			try {
//				Thread.sleep(2500);
//			} catch (InterruptedException e) {
//				LogsUtil.e(TAG, "error : "+ e);
//			}
//			LogsUtil.e(TAG, "自定义处理异常");
			
//			ActivityUtil.getInstance().cleanActivityList();
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		//打印错误信息
		
		LogsUtil.e(TAG, "自定义处理异常");
		ex.printStackTrace();
		
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				ToastUtil.showToastInCenter("很抱歉,程序出现异常,即将退出!");
				Looper.loop();
			}
		}.start();
		
//		//收集设备参数信息 
//		collectDeviceInfo(mContext);
		
		//保存日志文件 
		saveCrashInfo2File(ex);
		LogsUtil.e(TAG,"saveCrashInfo2File()");
		
//		mContext.sendBroadcast(new Intent(MyContexts.ACTION_EXIT_APP));
		ActivityUtil.getInstance().closeAllActivity();
		SystemClock.sleep(2500);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
				LogsUtil.v("uncaunhtException", versionName+versionCode);
			}
		} catch (NameNotFoundException e) {
			LogsUtil.e(TAG, "an error occured when collect package info"+e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				LogsUtil.v(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				LogsUtil.e(TAG, "an error occured when collect crash info"+e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = DateUtils.getStringTime(timestamp, "yyMMdd HH:mm:ss");
			String fileName = time + ".txt\n";
//			if (Environment.getExternalStorageState().equals(
//					Environment.MEDIA_MOUNTED)) {
//				String path = "/sdcard/crash/";
//				File dir = new File(path);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				FileOutputStream fos = new FileOutputStream(path + fileName);
//				fos.write(sb.toString().getBytes());
//				fos.close();
//			}
			String path = "/data/data/com.fanfull.fff/logs/";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
				LogsUtil.v("CrashHandler", "make dir");
			}
			FileOutputStream fos = new FileOutputStream(path + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
			LogsUtil.e(TAG,"写入data文件！");
//			try {
//				FileOutputStream fileos = mContext.openFileOutput("crash.txt",mContext.MODE_APPEND);
//				fileos.write(sb.toString().getBytes());
//				fileos.flush();
//				fileos.write(fileName.getBytes());
//				fileos.close();
//				LogsUtil.v("CrashHandler", "remark error");
//			} catch (java.io.FileNotFoundException e) {
//				e.printStackTrace();
//			}
			return fileName;
		} catch (Exception e) {
			LogsUtil.e(TAG, "an error occured while writing file..."+ e);
		}
		return null;
	}
}
