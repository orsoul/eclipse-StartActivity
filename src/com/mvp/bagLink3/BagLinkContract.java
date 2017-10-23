package com.mvp.bagLink3;

public interface BagLinkContract {

	public interface Presenter{
		void scanBag();
		
		/**
		 * 新建堆关联
		 */
		void linkNewPile();
		
	}
	
	public interface View{
		void scanBagSuccess();
		
		void scanBagFailure(String error);
		
		void linkSuccess();
		
		void linkFailure(String error);
	}
}
