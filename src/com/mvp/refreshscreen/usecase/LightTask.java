package com.mvp.refreshscreen.usecase;

import java.util.Timer;
import java.util.TimerTask;

import android.os.SystemClock;

import com.fanfull.hardwareAction.UHFOperation;
import com.fanfull.utils.ArrayUtils;
import com.hardware.Hardware;

public class LightTask implements Runnable{
	private Hardware hardware;
	private boolean stop = true;
	private UHFOperation mUhfOperation;
	private String mSreenEPC;
	public LightTask(String mSreenEPC){
		hardware = Hardware.getInstance();
		hardware.openGPIO();
		this.mSreenEPC = mSreenEPC;
		mUhfOperation = UHFOperation.getInstance();
	}
	
	@Override
	public void run() {
		stop = true;
		hardware.setGPIO(0, 6); // 开灯
		byte[] tid;
		Timer timer = new Timer(); 
		timer.schedule(task, 5000);
		while(stop){
			SystemClock.sleep(300);
			if(mUhfOperation.findOne()){
				System.out.println("正在查找...");
				if((tid = mUhfOperation.readTIDNogl())== null){
					continue;
				}
				if(!ArrayUtils.bytes2HexString(tid).equals(mSreenEPC)){
					continue;
				}else{
					byte[] read = mUhfOperation.readUHF(mUhfOperation.mEPC, 1, 0x20,3,0x1E,4);
					if(read==null||read.length<4){
						System.out.println("无数据");
						continue;
					}
					System.out.println("相应的值 "+read[3]);
					if(read[3]!=0x55){
						System.out.println("查找成功");
						hardware.setGPIO(1, 6);
						break;
					}
				}
				
			}
		}
	}

    TimerTask task = new TimerTask() {  
  
        @Override  
        public void run() { 
        	stop = false;
        	hardware.setGPIO(1, 6);
        }  
    };  
}
