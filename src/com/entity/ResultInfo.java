package com.entity;

import java.util.List;

public class ResultInfo {
	private PermissionInfo permission;

	private List<ListInfo> list ;

	public void setPermission(PermissionInfo permission){
	this.permission = permission;
	}
	public PermissionInfo getPermission(){
	return this.permission;
	}
	public void setList(List<ListInfo> list){
	this.list = list;
	}
	public List<ListInfo> getList(){
	return this.list;
	}
}
