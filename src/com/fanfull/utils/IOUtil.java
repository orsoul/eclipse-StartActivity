package com.fanfull.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 * @description 字节流 转 字符串
	 */
	public static String convertInputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}
}
