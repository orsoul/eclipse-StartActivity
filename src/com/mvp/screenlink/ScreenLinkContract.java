package com.mvp.screenlink;

import java.util.List;

import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.ScreenInfo;

public class ScreenLinkContract {
	
	interface View{
		void scanBagSuccess(String bagID);
		void scanBagFailure(String error);
		
		void scanScreenSuccess(String screenID);
		void scanScreenFailure();
		
		void linkScreenAndPileSuccess();
		void linkScreenAndPileFailure(String error);
	}
	
	interface Presenter{
		void scanBag();
		
		void scanScreen();
		
		void linkScreenAndPile();
	}
}
