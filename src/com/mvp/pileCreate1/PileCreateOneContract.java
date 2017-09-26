package com.mvp.pileCreate1;

import java.util.Map;

import com.entity.Response;
import com.entity.RootInfo;

public class PileCreateOneContract {

	interface Presenter{
		/**
		 * 从服务器获取用户权限信息
		 */
		void getPilePermission(String userID);
		/**
		 * 获取库房信息
		 */
		void getPileStore(String userID);
		/**
		 * 申请权限
		 */
		void applyPermission(String cardID,String moneyType,String moneyModel,String bagModel,String storeID);
		/**
		 * 查询袋ID
		 */
		void getBagID();
		/*
		 * 通过bagID申请权限
		 */
		void applyPermissionByBagID(String bagID,String cardID,String storeID);
	}
	
	interface View{
		
		void createPileSuccess(RootInfo rInfo);
		void applyPermissionSuccess(String msg);
		void createPileFailure();
		void onFailure(String error);
		void onGetStoreNumResult(Map<String, Integer> storeMap);
		void getBigIDSuccess(String bagID);
		void getBigIDError(String msg);
		void applyPermissionSuccessByBigID(String result);
		void applyPermissionErrorByBigID();
	}
}
