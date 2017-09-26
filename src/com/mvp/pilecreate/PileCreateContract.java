package com.mvp.pilecreate;

import java.util.Map;

public class PileCreateContract {

	interface Presenter {
		/**
		 * 创建堆
		 */
		void create(String bagModel, String moneyType, String moneyModel,String storeNum);

		/**
		 * 从服务器获取库房列表
		 */
		void getStoreNum(String userId);

		/**
		 * 扫描袋锁 获取 袋类型, 券别, 清分 信息
		 */
		void scan();
	}

	interface View {
		void createPileSuccess();

		void createPileFailure();
		
		void onFailure(String error);

		void onGetStoreNumResult(Map<String, Integer> storeMap);

		//void onScanResult(byte[] bagId);
	}
}
