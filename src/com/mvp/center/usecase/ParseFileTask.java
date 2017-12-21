package com.mvp.center.usecase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.db.BagInfoDao;
import com.db.DBService;
import com.db.PileInfoDao;
import com.db.ScreenInfoDao;
import com.db.TrayInfoDao;
import com.entity.BagInfo;
import com.entity.PileInfo;
import com.entity.ScreenInfo;
import com.entity.TrayInfo;
import com.fanfull.base.BaseApplication;
import com.mvp.center.TimeListener;

public class ParseFileTask implements Runnable{

	private String path;
    private TimeListener listener;

    public ParseFileTask(String path,TimeListener listener){
        this.path = path;
        this.listener = listener;
    }

    
    @Override
	public void run() {
		try {
			
            Date date = new Date();
            startTime = date.getTime();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(new FileInputStream(path)),"GBK"));
            String line = null;
            WriteDB writeDB = new WriteDB();
            new Thread(writeDB).start();
            int flag = 1;
            while ((line = reader.readLine())!=null){
                parase(line);
                if(queue.size() == 8000){
                    Log.i("停止插入的次数", ""+flag++);
                    Thread.currentThread().sleep(2*1000);
                }
            }
            reader.close();
            writeDB.stop();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            listener.onFailure(1);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(2);
        } catch (Exception e) {
            listener.onFailure(3);
            e.printStackTrace();
        }
	}

    long startTime;
    String update_time = "0";
    private int flag = 0;
    private void parase(String s) throws Exception{
    	if(s.equals("Table:bag")){
    		flag = 1;
    	}else if(s.equals("Table:pile")){
    		flag = 2;
    	}else if(s.equals("Table:screen")){
    		flag = 3;
    	}else if(s.equals("Table:tray")){
    		flag = 4;
    	}else if(s.contains("Table:update_time:")){
    		update_time = s.replace("Table:update_time:", "");
    	}
    	else{
    		writeDB(flag,s);
        }
    }

    private void writeDB(int flag, String s) throws Exception{
        switch (flag){
            case 1:
                writeBag(s);
                break;
            case 2:
                writePile(s);
                break;
            case 3:
                writeScreen(s);
                break;
            case 4:
                writeTray(s);
                break;
        }
    }

    LinkedBlockingQueue queue = new LinkedBlockingQueue();
    private void writeBag(String s) throws Exception{
    	Log.i("", "写入袋数据："+s);
        String[] list = s.split("\\|");
        BagInfo bag = new BagInfo();
        bag.setBagID(list[0]);
        bag.setPileID(list[1]);
        bag.setTrayID(list[2]);
        bag.setUpdate_time(update_time);
        queue.put(bag);
    }

    private void writePile(String s) throws Exception{
    	Log.i("", "写入堆数据："+s);
        String[] list = s.split("\\/");
        PileInfo pile = new PileInfo();
        pile.setPileID(list[0]);
        pile.setBagType(list[1]);
        pile.setMoneyType(list[2]);
        pile.setTotalAmount(list[3]);
        pile.setOrganID(list[4]);
        pile.setSerialNum(list[5]);
        pile.setScreenNum(Integer.valueOf(list[6]));
        pile.setBagNum(Integer.valueOf(list[7]));
        pile.setRefresh_flag(Integer.valueOf(list[8]));
        pile.setPileName(list[9]);
        pile.setSeries(list[10]);
        pile.setTime(list[11]);
        pile.setMoneyModel(list[12]);
        pile.setUpdate_time(update_time);
        queue.put(pile);
    }
    private void writeScreen(String s) throws Exception{
    	Log.i("", "写入屏数据："+s);
        String[] list = s.split("\\|");
        ScreenInfo screen = new ScreenInfo();

        screen.setScreenID(list[0]);
        screen.setPileID(list[1]);
        screen.setIsUse(true);
        screen.setIsInit(true);
        screen.setSerialNum(list[4]);
        screen.setRefresh_flag(5);
        screen.setUpdate_time(update_time);
        queue.put(screen);
    }
    private void writeTray(String s) throws Exception{
    	Log.i("", "写入托盘数据："+s);
        String[] list = s.split("\\|");
        TrayInfo tray = new TrayInfo();

        tray.setTrayID(list[0]);
        tray.setTotalAmount(list[1]); 
        tray.setBagNum(Integer.parseInt(list[2]));
        tray.setMoneyType(list[3]);
        tray.setMoneyModel(list[4]);
        tray.setBagType(list[5]);
        tray.setUpdate_time(update_time);
        
        queue.put(tray);
    }


class WriteDB implements Runnable {
    private boolean isRun = true;
    int flag = 1;
    @Override
    public void run() {
    	BagInfoDao mBagInfoDao = DBService.getService().getBagInfoDao();
    	PileInfoDao mPileInfoDao = DBService.getService().getPileInfoDao();
    	ScreenInfoDao mScreenInfoDao = DBService.getService().getScreenInfoDao();
    	TrayInfoDao mTrayInfoDao = DBService.getService().getTrayInfoDao();
        List<BagInfo> bagInfos = new ArrayList<BagInfo>();
        while (isRun || !queue.isEmpty()){
            Object obj = null;
            try {
            	if(!queue.isEmpty()){
            		obj = queue.take();
            	}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(obj instanceof BagInfo){
                bagInfos.add((BagInfo) obj);
                if(bagInfos.size() == 2000){
                    Log.i("批量插入次数",""+flag++);
                    mBagInfoDao.insertOrReplaceInTx(bagInfos);
                    bagInfos.clear();
                }
            }else if(obj instanceof PileInfo){
            	mPileInfoDao.insertOrReplace((PileInfo) obj);
            }else if(obj instanceof ScreenInfo){
            	mScreenInfoDao.insertOrReplace((ScreenInfo) obj);
            }else if(obj instanceof TrayInfo){
            	mTrayInfoDao.insertOrReplace((TrayInfo) obj);
            }
        }
        Log.i("袋缓存数",bagInfos.size()+"");
        Log.i("队列数",queue.size()+"");
        if(!bagInfos.isEmpty()){
        	mBagInfoDao.insertOrReplaceInTx(bagInfos);
            bagInfos.clear();
        }
        Date date = new Date();
        
        queue = null;
        long endTime = date.getTime();
        SharedPreferences preferences=BaseApplication.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("update_time", update_time);
		editor.commit();
        listener.onFinish(endTime-startTime);
    }

    public void stop(){
        isRun =false;
    }
}
}
