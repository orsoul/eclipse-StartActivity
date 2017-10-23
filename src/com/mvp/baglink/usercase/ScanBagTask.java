package com.mvp.baglink.usercase;

import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;
import com.mvp.BaseListener;

/**
 * 扫描超高频袋信息
 * 
 * @author root
 *
 */
public class ScanBagTask implements Runnable{

	private BaseListener mListener;
	
	public ScanBagTask(BaseListener listener){
		this.mListener = listener;
	}
	
	@Override
	public void run() {
		UHFOperation mUHFOp = UHFOperation.getInstance();
		mUHFOp.setPower(8, 15, 1, 0, 0);
		mUHFOp.open();
		int flag = 0;
		while(flag < 50){
			flag++;
			if(mUHFOp.findOne()){
				final String epc =ArrayUtils.bytes2HexString(mUHFOp.mEPC);
				String bagID = epc;
				String head = epc.substring(0, 2);
				LogsUtil.d("head=" + head);
				if(head.equals("04")||head.equals("05")){
					bagID = epc.substring(0, 18);
				}
				System.out.println("袋ID："+bagID);
				mListener.result(bagID);
				mUHFOp.close();
				return;
			}
		}
		if(flag>=50){
			mListener.failure(null);
		}
	}

}
