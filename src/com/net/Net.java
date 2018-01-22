package com.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.utils.URLEncodedUtils;

import com.fanfull.base.BaseApplication;
import com.fanfull.contexts.StaticString;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by andy on 17-2-13.
 */

public class Net {
	private static Net mNet;
	private static NetWork mNetWork;
	private static String ip = "192.168.11.111";
	//private static String ip =StaticString.IP0;
	private static int port = 8080;
	/**
	 * 参数为 update_time, organID
	 */
	public static final String DOWNLOAD = "/LnkScreen/sync/download";
	public static final String CHECK_STATUS = "/LnkScreen/sync/checkStatus";
	public static final String BAG_INSERT = "/LnkScreen/bag/insert";
	public static final String BAG_LINK = "/LnkScreen/bag/link";
	public static final String CREATE_PILE = "/LnkScreen/pile/createNew";
	public static final String BAG_LINK_PILE = "/LnkScreen/bag/bagLinkPile";
	public static final String LINK_SCREEN = "/LnkScreen/screen/link";
	public static final String SCREEN_DATA = "/LnkScreen/screen/getRefreshData";
	public static final String BAGS_DOWNLOAD = "/LnkScreen/sync/getBags";
	public static final String UPLOAD2 = "/LnkScreen/sync/upload2";
	public static final String UPLOAD = "/LnkScreen/sync/upload";
	public static final String PATH_GET_STROENUM_LIST = "/LnkScreen/store/getList";
	public static final String PATH_GET_PILEPERMISSION = "/LnkScreen/user/getPilePermission";
	public static final String PATH_APPLY_PILEPERMISSION = "/LnkScreen/user/grantPermission";
	public static final String PATH_SCAN_START = "/LnkScreen/screen/enable";
	public static final String PATH_SCAN_STOP = "/LnkScreen/screen/disable";
	public static final String PATH_SCAN_REGISTER = "/LnkScreen/screen/register";
	public static final String PATH_SCAN_CANCLE = "/LnkScreen/screen/cancel";
	public static final String PATH_GET_USER_PRLEPERMISSION = "/LnkScreen/user/getPermission";
	public static final String NEW_PILE_LINK = "/LnkScreen/bag/newBagLink";
	public static final String PATH_APPLY_PREMISSION_BAGID = "/LnkScreen/user/grantPermission1";
	public static final String DATA_DOWNLOAD = "/LnkScreen/sync/update";

	private Net() {
		mNetWork = new NetWork();
	}

	public static Net getInstance() {
		if (mNet == null) {
			mNet = new Net();
		}
		return mNet;
	}

	public void getFile(String url, String[] keys, String[] values,
			MessageListener listener) {
		String params = "";
		for (int i = 0; i < keys.length; i++) {
			if (i == 0) {
				params += keys[i] + "=" + URLEncoder.encode(values[i]);
			} else {
				params += "&" + keys[i] + "=" + URLEncoder.encode(values[i]);
			}
		}
		
		URL url1;
		try {
			url1 = new URL("http", ip , port , url+"?"+params);
			mNetWork.getFile(url1, listener);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void getFile(String url, String keys, String values,
			MessageListener listener) {
		String params = keys + "=" + values;
		
		URL url1;
		try {
			url1 = new URL("http", ip , port , url+"?"+params);
			mNetWork.getFile(url1, listener);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void get(String url, String[] keys, String[] values,
			MessageListener listener) {
		String params = "";
		for (int i = 0; i < keys.length; i++) {
			if (i == 0) {
				params += keys[i] + "=" + URLEncoder.encode(values[i]);
			} else {
				params += "&" + keys[i] + "=" + URLEncoder.encode(values[i]);
			}
		}
		URL url1;
		try {
			url1 = new URL("http", ip , port , url+"?"+params);
			mNetWork.get(url1, listener);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void get(String url, String keys, String values,
			MessageListener listener) {
		String params = keys + "=" + values;
		URL url1;
		try {
			url1 = new URL("http", ip , 8080 , url+"?"+params);
			mNetWork.get(url1, listener);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 返回网络连接情况
	 * 
	 * @return true为网络连通、false为网络连接失败
	 */
	public boolean isWifiConnected() {
		Context context = BaseApplication.getContext();
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				if (mWiFiNetworkInfo.getState().equals(
						NetworkInfo.State.DISCONNECTED)) {
					return false;
				} else if (mWiFiNetworkInfo.getState().equals(
						NetworkInfo.State.CONNECTED)) {
					return true;
				}
			}
		}
		return false;
	}

	public void uploadFile(String upload1, File file, String string) {
		try {
			URL url = new URL("http", ip , 8080 , upload1);
			mNetWork.uploadFile(url, file, string);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void upload(String url, String content, MessageListener listener) {
		try {
			URL url1 = new URL("http", ip , 8080 , url);
			mNetWork.upload(url1, content, listener);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
}
