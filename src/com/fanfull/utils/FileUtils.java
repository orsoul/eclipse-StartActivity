package com.fanfull.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

import com.fanfull.base.BaseApplication;

public class FileUtils {

	public static File createFile(String fileName) {
		File file =new File(BaseApplication.getContext().getFilesDir(), fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				Log.e("FileUtils", "创建文件失败");
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public static File deleteFile(String fileName) {
		File file =new File(BaseApplication.getContext().getFilesDir(), fileName);
		if(file.exists()){
			file.delete();
		}
		return file;
	}

	/**
	 * 写字符串到文件中
	 * 
	 * @param file
	 * @param content
	 * @param append	是否在文件后添加内容
	 * @return
	 */
	public static boolean writeFileFromString(File file, String content,
			boolean append) {
		if (file == null || content == null)
			return false;
		if (!file.exists()){
			Log.i("文件是否存在?","No");
			return false;
		}
			
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file, append));
			bw.write(content);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
