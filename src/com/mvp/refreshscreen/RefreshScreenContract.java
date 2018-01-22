package com.mvp.refreshscreen;

import com.entity.PileInfo;
import com.entity.ScreenInfo;

public class RefreshScreenContract {
	public interface View{
		void scanBagSuccess(String bagID);
		void scanBagFailure(String error);
		
		void scanScreenSuccess(String screenID);
		void scanScreenFailure(String info);
		
		void refreshData(PileInfo info);
		void writeScreenSuccess();
		
		void error(String error);
	}
	
	interface Presenter{
		void scanBag();
		
		void scanScreen();
		
		void writeScreen();
		
		void light();
	}

}
