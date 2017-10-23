package com.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户信息 ------- 作为登录成功时的返回信息
 * 
 * @author andy
 *
 */
public class UserInfo {
	/**
	 * 用户ID
	 */
	private String userID;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 用户权限
	 */
	private String permissions;

	/**
	 * 用户的登录时间
	 */
	private String loginTime;

	/**
	 * 与用户相关联的机构ID
	 */
	private String organID;

	/**
	 * 与用户绑定的指纹信息
	 */
	private String fingerID;

	public UserInfo() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		loginTime = format.format(new Date());
	}

	@Override
	public String toString() {
		String string = "UserInfo:";
		string += "用户ID：" + userID;
		string += " 密码：" + password;
		string += " 用户权限:" + permissions;
		string += " 登录时间:" + loginTime;
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

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
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
