package com.fanfull.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.text.format.DateFormat;
import android.util.TimeFormatException;

public class CmdUtil {
	public static String execCommand(String command) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command);
		try {
			if (proc.waitFor() != 0) {
				System.err.println("exit value = " + proc.exitValue());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}

			String reVal = stringBuffer.toString();
			return reVal;
		} catch (InterruptedException e) {
			System.err.println(e);
			return null;
		} finally {
			try {
				proc.destroy();
			} catch (Exception e2) {
			}
		}
	}

	public static void execSh(String cmd) throws IOException, InterruptedException {
		Process process = null;
		DataOutputStream os = null;
		BufferedReader in = null;

		try {
			System.out.println("process pre:" + process);
			process = Runtime.getRuntime().exec("/system/xbin/su");
			System.out.println("process pro:" + process);
			os = new DataOutputStream(process.getOutputStream());
			
			os.writeBytes(cmd + ""); // 这里可以执行具有root 权限的程序了
			os.flush();
			int waitFor = process.waitFor();
			System.out.println("waitFor:" + waitFor);
			if (waitFor != 0) {
				System.out.println("exit value = " + process.exitValue());
			}
			
			in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}
			System.out.println("run res:\n" + stringBuffer.toString());
			
		} finally {
			if (os != null) {
				os.close();
			}
			if (in != null) {
				in.close();
			}
			process.destroy();
		}
	}
}
