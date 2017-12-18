package com.mvp.center;

import java.util.Timer;
import java.util.TimerTask;

import com.fanfull.contexts.StaticString;
import com.net.MessageListener;
import com.net.Net;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

/**
 * 人行Http访问有问题，启用该服务保持连接
 * 
 * @author Administrator
 * 
 */
public class ConnectService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i("ConnectService", "心跳服务开启");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getPermission();
		return super.onStartCommand(intent, flags, startId);
	}

	private Net net = Net.getInstance();
	private int flag = 1;

	public void getPermission() {
		String[] keys = { "cardID" };
		String[] values = { StaticString.userId };
		if (values[0] == null) {
			values[0] = "59EF3044";
		}
		net.get(Net.PATH_GET_USER_PRLEPERMISSION, keys, values,
				new MessageListener() {

					@Override
					public void onSuccess(final String msg) {
						Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									Thread.sleep(5 * 1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								Log.i("心跳次数", "" + flag++);
								getPermission();
							}
						});
						thread.start();
					}

					@Override
					public void onFailure() {
						Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									Thread.sleep(2 * 1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								Log.i("心跳次数", "" + flag++);
								getPermission();
							}
						});
						thread.start();
					}
				});
	}
}
