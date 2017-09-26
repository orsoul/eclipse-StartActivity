package com.mvp.screenlink.usecase;

import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.mvp.BaseListener;

/**
 * 扫描屏
 * 
 * @author root
 *
 */
public class ScanScreenTask implements Runnable{

	private BaseListener mListener;
	
	public ScanScreenTask(BaseListener listener){
		this.mListener = listener;
	}
	
	
	@Override
	public void run() {
		byte[] tid;
		String mTid,mEpc;
		UHFOperation mUHFOp = UHFOperation.getInstance();
		mUHFOp.setPower(8, 15, 1, 0, 0);
		mUHFOp.open();
		int flag = 0;
		while(flag < 50){
			flag++;
			if(mUHFOp.findOne()){
				if((tid = mUHFOp.readTIDNogl())== null){
					continue;
				}else {
					mTid = ArrayUtils.bytes2HexString(tid);
				}
				
				mEpc = ArrayUtils.bytes2HexString(mUHFOp.mEPC);
				mListener.result(mTid);
				break;
			}
		}
		if(flag>=50){
			mListener.failure(null);	
		}
		
	}

}
