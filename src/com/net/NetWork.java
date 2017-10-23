package com.net;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;

/**
 * Created by andy on 17-2-13.
 */

public class NetWork {

	private Handler mHandler = new Handler();

	/**
	 * 获取文件
	 * 
	 * @param urlPath
	 * @param param
	 * @param listener
	 */
	public void getFile(final URL url,final MessageListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				InputStream is = null;
				try {
					//URL url = new URL(urlPath + "?" + param);
					connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(5*1000);
					connection.setReadTimeout(5*1000);
					connection.setUseCaches(true);
					connection.setRequestMethod("GET");
					connection.connect();
					if(connection.getResponseCode() == 200){
						is = connection.getInputStream();
						String path = "/data/data/com.fanfull.fff/";
						Log.i("path", path);
						File file = new File(path + "increment.txt");
						Log.i("path", file.getAbsolutePath());
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						if (!file.exists()) {
							file.createNewFile();
						}

						OutputStream os = new FileOutputStream(file);

						byte[] buffer = new byte[4 * 1024];
						int len = 0;
						// 从输入六中读取数据,读到缓冲区中
						while ((len = is.read(buffer)) > 0) {
							os.write(buffer, 0, len);
						}
						os.flush();
						os.close();
						postSuccessView(file.getAbsolutePath(), listener);
					}else{
						postFailure(listener);
					}
					
				} catch (Exception e) {
					postFailure(listener);
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public void get(final URL url, final MessageListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				InputStream is = null;
				try {
					
					LogsUtil.e(url);
					connection = (HttpURLConnection) url.openConnection();
					/*connection.setConnectTimeout(2*1000);
					connection.setReadTimeout(2*1000);*/
					connection.setUseCaches(true);
					connection.setRequestMethod("GET");
					connection.connect();
					if(connection.getResponseCode() == 200){
						is = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(is));
						String s = null;
						String result = "";
						// 从输入六中读取数据,读到缓冲区中
						while (((s = reader.readLine()) != null)) {
							result += s;
						}
						Log.e("返回结果", result);
						postSuccessView(result, listener);
					}else{
						postFailure(listener);
					}
					
				} catch (Exception e) {
					postFailure(listener);
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public void postSuccessView(final String msg, final MessageListener listener) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				listener.onSuccess(msg);
			}
		});
	}

	public void postFailure(final MessageListener listener) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				listener.onFailure();
			}
		});
	}

	public void uploadFile(final URL url, final File uploadFile,
			final String newName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String end = "/r/n";
				String Hyphens = "--";
				String boundary = "*****";
				try {
					//URL url = new URL(uploadURL);
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();

					/* 允许Input、Output，不使用Cache */
					con.setDoInput(true);
					con.setDoOutput(true);
					con.setUseCaches(false);

					/* 设定传送的method=POST */
					con.setRequestMethod("POST");

					/* setRequestProperty */
					con.setRequestProperty("Connection", "Keep-Alive");
					con.setRequestProperty("Charset", "UTF-8");
					con.setRequestProperty("Content-Type",

					"multipart/form-data;boundary=" + boundary);

					/* 设定DataOutputStream */
					DataOutputStream ds = new DataOutputStream(con
							.getOutputStream());

					ds.writeBytes(Hyphens + boundary + end);
					ds.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
							+ newName + "\"" + end);
					ds.writeBytes(end);

					/* 取得文件的FileInputStream */
					FileInputStream fStream = new FileInputStream(uploadFile);

					/* 设定每次写入1024bytes */
					int bufferSize = 1024;
					byte[] buffer = new byte[bufferSize];
					int length = -1;

					/* 从文件读取数据到缓冲区 */
					while ((length = fStream.read(buffer)) != -1) {
						/* 将数据写入DataOutputStream中 */
						ds.write(buffer, 0, length);
					}
					ds.writeBytes(end);
					ds.writeBytes(Hyphens + boundary + Hyphens + end);
					fStream.close();
					ds.flush();

					/* 取得Response内容 */
					InputStream is = con.getInputStream();
					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					System.out.println("上传成功");
					ds.close();

				} catch (Exception e) {
					System.out.println("上传失败" + e.getMessage());
				}
			}
		}).start();
	}

	private static final int TIMEOUT = 10000;

	/**
	 * 上传数据
	 * 
	 * @param url
	 * @param content
	 */
	public void upload(final URL url, final String content,
			final MessageListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				byte[] sendData = content.getBytes();
				try {
					//URL url = new URL(urlPath);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(TIMEOUT);
					// 如果通过post提交数据，必须设置允许对外输出数据
					conn.setDoOutput(true);
					conn.setRequestProperty("Content-Type", "text/xml");
					conn.setRequestProperty("Charset", "utf-8");
					conn.setRequestProperty("Content-Length",
							String.valueOf(sendData.length));
					OutputStream outStream;
					outStream = conn.getOutputStream();
					outStream.write(sendData);
					outStream.flush();
					outStream.close();
					if (conn.getResponseCode() == 200) {
						// 获得服务器响应的数据
						BufferedReader in = new BufferedReader(
								new InputStreamReader(conn.getInputStream(),
										"utf-8"));
						// 数据
						String retData = null;
						String responseData = "";
						while ((retData = in.readLine()) != null) {
							responseData += retData;
						}
						in.close();
						listener.onSuccess(responseData);
					}else{
						listener.onFailure();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}
}
