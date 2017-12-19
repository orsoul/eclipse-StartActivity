package com.mvp.screenManage;

public class ScreenManageContract {
	interface View{
		void scanScreenFailure();
		void scanScreenSuccess(String screenID);
		void scanSuccess(String msg);
		void scanFailure(String msg);
		void onFailure(String msg);
	}
	
	interface Presenter{
		
		void scanScreen();
		void scanStart(String screenID,String cardID);
		void scanStop(String screenID,String cardID);
		void scanRegister(String screenID,String cardID);
		void scanCancle(String screenID,String cardID);
	}
}
