package com.mvp.center;

/**
 * Created by andy on 17-2-15.
 */

public interface TimeListener {
	byte b = 0;

	void onFinish(long time);

	void onFailure(int flag);
}
                                                  