package com.fanfull.utils;

public class BagIdparser {
	
	public static String[] parserAll(byte[] bagId) {
		String str = ArrayUtils.bytes2HexString(bagId);
		
		String version = str.substring(0, 2);
		String org = str.substring(2, 5);
		String wanz = str.substring(5, 6);
		return null;
	}

}
