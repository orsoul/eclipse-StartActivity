package com.mvp.hand;

import java.util.Map;

public class HandContract {

	
	public interface Presenter{
		void readScreen();
		
		void writeScreen(String moneyTotal,int bagNum);
		
		void initScreen(String bagType, String moneyType,int version,String series);
		
		void light();
		
		/**
		 * 创建堆
		 */
		void create(String bagModel, String moneyType, String moneyModel,String storeNum);
		
	}
	
	interface View {
		void createPileSuccess();

		void createPileFailure();
		
		void onFailure(String error);
	}
}
