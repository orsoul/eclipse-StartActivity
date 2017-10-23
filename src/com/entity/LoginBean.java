package com.entity;

import com.fanfull.contexts.StaticString;
import com.fanfull.socket.SendTask;

/**
 *	
 */
public class LoginBean {
	/** 用户登录ID */
	private String userID;
	/** 复核用户ID */
	private String checkerID;
	/**
	 * 密码
	 */
	private String password;

	/**
	 * 用户权限
	 */
	private String permissions;

	/**
	 * 服务器时间
	 */
	private String time;

	/**
	 * 与用户相关联的机构ID
	 */
	private String organID;

	/** 成功登录 */
	private boolean isLogin;
	/** 是老手持 */
	private boolean isOldHand;
	/** 是人行 */
	private boolean isCenter;
	/** 已复核登录 */
	private boolean isCheck;

	/**
	 * 与用户绑定的指纹信息
	 */
	private String fingerID;

	public LoginBean(String loginInfo) {
		
	}
	
	public boolean parseLoginInfo(String loginInfo) {
		if (null == loginInfo) {
			return false;
		}
		String[] split = loginInfo.split(SendTask.INFO_SEPARATOR);

		if (split.length < 4 || !split[0].equals("*01")
				|| !split[1].equals("01")) {
			return false;
		}
		
		permissions = split[2];
		StaticString.permission = split[2];
		
		isLogin = true;
		

		if (4 < split.length) {

		} else {

		}
		return false;
	}

	@Override
	public String toString() {
		String string = "UserInfo:";
		string += "用户ID：" + userID;
		string += " 密码：" + password;
		string += " 用户权限:" + permissions;
		string += " 登录时间:" + time;
		string += " 机构ID:" + organID;
		string += " 指纹信息:" + fingerID;
		return string;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String loginTime) {
		this.time = loginTime;
	}

	public String getOrganID() {
		return organID;
	}

	public void setOrganID(String organID) {
		this.organID = organID;
	}

	public String getFingerID() {
		return fingerID;
	}

	public void setFingerID(String fingerID) {
		this.fingerID = fingerID;
	}
}
