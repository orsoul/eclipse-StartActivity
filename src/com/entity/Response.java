package com.entity;

public class Response {
	/**
	 * 请求是否成功的标志
	 */
	private boolean success;

	/**
	 * 请求成功/请求失败的原因
	 */
	private String msg;

	/**
	 * 请求的结果集
	 */
	private Object result = "";

	public Response() {

	}

	public Response(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}

	public Response(boolean success, String msg, Object result) {
		this.success = success;
		this.msg = msg;
		this.result = result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
