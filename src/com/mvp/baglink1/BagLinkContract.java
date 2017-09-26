package com.mvp.baglink1;

public interface BagLinkContract {

	interface View{
	
		void scanTrayBagSuccess(String bagID);
		
		void scanPileBagSuccess(String bagID);
		
		void scanScreenSuccess(String screenID);
		
		void scanFailure(String error);
		
		void writeScreenSuccess();
		
		void writeScreenFailure();
	}
	
	interface Presenter{
		
		/**
		 * 下载数据
		 */
		void download();
		
		/**
		 * 扫描托盘袋
		 */
		void scanTrayBag();
		
		/**
		 * 扫描屏
		 */
		void scanScreen();
		
		/**
		 * 扫描堆中袋
		 */
		void scanPileBag();
		
		/**
		 * 统计数据
		 */
		void count();
		
		/**
		 * 刷新屏
		 */
		void refreshScreen();
		
		void light();
		/**
		 * 保存各种操作数据，作为后续提交后台的依据
		 */
		void saveInfo();
		
		/**
		 * 文件上传
		 */
		void upload();
	}
	
}
