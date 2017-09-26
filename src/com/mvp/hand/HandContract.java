package com.mvp.hand;

public class HandContract {

	
	public interface Presenter{
		void readScreen();
		
		void writeScreen(String moneyTotal,int bagNum);
		
		void initScreen(String bagType, String moneyType,int version,String series);
		
		void light();
		
	}
}
