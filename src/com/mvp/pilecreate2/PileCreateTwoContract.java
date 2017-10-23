package com.mvp.pilecreate2;

public class PileCreateTwoContract {
	interface Presenter{
		void createStore(String moneyType,String moneyModel,String bagModel,String cardID,String storeID);
	}
	interface View{
		void onCreateSuccess();
		void onCreateFailure();
		void onConnectionError(String msg);
	}
	
}
