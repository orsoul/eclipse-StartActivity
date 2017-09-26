package com.fanfull.background;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.fanfull.base.BaseApplication;
import com.fanfull.utils.LogsUtil;

public class ActivityUtil {
	private final static String TAG = ActivityUtil.class.getSimpleName();

	private static ActivityUtil instance;
	// private Collection<Activity> activityCollection;
	private LinkedList<Activity> activityCollection;

	// private static LinkedHashMap<String, Activity> actMap;

	private ActivityUtil() {
		activityCollection = new LinkedList<Activity>();
		// activityCollection = new TreeSet<Activity>();
		// activityCollection = new HashSet<Activity>();
		// Set<String> keySet = actMap.keySet();

	}

	public static ActivityUtil getInstance() {
		if (instance == null) {
			instance = new ActivityUtil();
		}
		return instance;
	}

	public void addActivityToList(Activity activity) {
		if (activity != null && !activityCollection.contains(activity)) {
			activityCollection.add(activity);
		}
		LogsUtil.d(TAG, "addActivityToList():" + activityCollection);
	}

	public void removeActivityFromList(Activity activity) {
		if (activityCollection != null && activityCollection.size() > 0) {
			activityCollection.remove(activity);
			LogsUtil.d(TAG, "removeActivityFromList():"
					+ activity.getClass().getSimpleName());
		}
	}

	/**
	 * 关闭 ActivityList中 所有 Activity
	 */
	public void closeAllActivity() {
		LogsUtil.d(TAG, "closeAllActivity():" + activityCollection);
		if (null == activityCollection || activityCollection.isEmpty()) {
			return;
		}

		while (!activityCollection.isEmpty()) {
			Activity activity = activityCollection.removeLast();
			if (null != activity && !activity.isFinishing()) {
				activity.finish();
				LogsUtil.d(TAG, "finish " + activity.getClass().getSimpleName());
			}
		}

		// for (Iterator<Activity> iterator = activityCollection.iterator();
		// iterator
		// .hasNext();) {
		// Activity activity = iterator.next();
		// activityCollection.remove(activity);
		// if (null != activity && !activity.isFinishing()) {
		// LogsUtil.d(TAG, "remove " + activity.getClass().getSimpleName());
		// activity.finish();
		// }
		// }
	}

	/**
	 * 判断某个Acitivity是否在前台
	 * 
	 * @param activityName
	 *            Acitivity完整名称，com.fanfull.activity.CheckUserInfoActivity
	 * 
	 * @return 是在前台运行 返回true
	 */
	public static boolean isForeground(String activityName) {
		if (TextUtils.isEmpty(activityName)) {
			return false;
		}

		ActivityManager am = (ActivityManager) BaseApplication.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			// LogsUtil.d(TAG, "clzActivity:" + className);
			LogsUtil.d(TAG, "topActivity:" + cpn.getClassName());
			LogsUtil.d(TAG, "numActivities:" + list.get(0).numActivities);
			LogsUtil.d(TAG, "numRunning:" + list.get(0).numRunning);
			if (activityName.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断某个App是否 已经运行
	 * 
	 * @param packege
	 * @return 已在运行 返回true
	 */
	public static boolean isRunning(String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}

		ActivityManager am = (ActivityManager) BaseApplication.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		if (null == list || 0 == list.size()) {
			return false;
		}

		for (RunningAppProcessInfo info : list) {
			LogsUtil.d(TAG, info.processName + " running");
			if (packageName.equals(info.processName)) {
				return true;
			}
		}

		return false;
	}
}
