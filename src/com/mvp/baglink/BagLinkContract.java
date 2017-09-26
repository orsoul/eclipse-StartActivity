package com.mvp.baglink;

import java.util.List;

import com.entity.BagInfo;
import com.entity.PileInfo;

interface BagLinkContract {

	interface View{
		void scanBagSuccess(List<PileInfo> piles, BagInfo bagInfo);
		void scanBagFailure(String error);
		
		void linkBagAndPileSuccess();
		void linkBagAndPileFailure(String error);
	}
	
	interface Presenter{
		void scanBag();
		
		void linkBagAndPile(BagInfo bagInfo, PileInfo pileInfo);
	}
}
