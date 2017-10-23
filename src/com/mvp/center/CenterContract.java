package com.mvp.center;

import com.entity.Response;
import com.entity.RootInfo;

import android.content.Context;

interface CenterContract {

	interface View {
		void syncStatus(Response response);
		
		void syncSuccess();
		
		void error(String error);
		
		void userSuccess(String msg);
		void userError();
		
		
	}

	interface Presenter {
		void checkSyncStatus(Context context);

		void download();
		
		void upload();
		
		void getPermission();
	}
}
