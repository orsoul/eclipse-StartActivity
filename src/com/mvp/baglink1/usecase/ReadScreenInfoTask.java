package com.mvp.baglink1.usecase;

import android.os.SystemClock;

import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.mvp.BaseListener;
/**
 * 读取墨水屏中的信息
 * 
 * @author root
 *
 */
public class ReadScreenInfoTask implements Runnable{
	private BaseListener listener;
	
	public ReadScreenInfoTask(BaseListener listener){
		this.listener =listener;
	}
	
	@Override
	public void run() {
		UHFOperation mUhfOperation = UHFOperation.getInstance();
		mUhfOperation.setPower(20, 20, 1, 0, 0);
		mUhfOperation.open();
		int count = 0;
		while(50 > count++){
			SystemClock.sleep(50);
			if(mUhfOperation.findOne()){
				
				byte[] wByte1 = mUhfOperation.readUHF1(mUhfOperation.mEPC, 1, 0x02, 3, 0x0, 30);
				if(wByte1 == null){
					System.out.println("数据问题1");
					continue;
				}
				byte[] wByte2 = mUhfOperation.readUHF1(mUhfOperation.mEPC, 1, 0x02, 3, 0xF, 30);
				if(wByte2 == null){
					System.out.println("数据问题2");
					continue;
				}
				byte[] wByte3 = mUhfOperation.readUHF1(mUhfOperation.mEPC, 1, 0x02, 3, 0x1E, 4);
				if(wByte3 == null){
					System.out.println("数据问题3");
					continue;
				}
				String wByte = ArrayUtils.bytes2HexString(wByte1)+ArrayUtils.bytes2HexString(wByte2)+ArrayUtils.bytes2HexString(wByte3);
				listener.result(wByte);
				return;
			}
		}
		listener.failure("扫描失败");
	}
}
