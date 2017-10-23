package com.mvp.bagLink2;

import com.entity.PileInfo;
import com.entity.TrayInfo;

public interface BagLinkContract {

	public interface View{
		
		void scanTrayBagSuccess(TrayInfo trayInfo);
		
		void scanTrayBagFailure(String error);
		
		void scanPileBagSuccess(PileInfo pileInfo);
		
		void scanPileBagFailure(String error);
		
		void linkSuccess(PileInfo pileInfo);
		
		void linkFailure(String error);
	}
	
	public interface Presenter{
		void scanTrayBag();
		
		void scanPileBag();
		
		void scanPileBagTwo();
		
		void link();
	}
}
