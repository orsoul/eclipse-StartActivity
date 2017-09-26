package com.entity;

public class RootInfo {

	 private String msg;

	 private ResultInfo result;

	 private boolean success;

	 public void setMsg(String msg){
	 this.msg = msg;
	 }
	 public String getMsg(){
	 return this.msg;
	 }
	 public void setResult(ResultInfo result){
	 this.result = result;
	 }
	 public ResultInfo getResult(){
	 return this.result;
	 }
	 public void setSuccess(boolean success){
	 this.success = success;
	 }
	 public boolean getSuccess(){
	 return this.success;
	 }
}
