package com.db;

import android.content.Context;

import com.db.BagInfoDao;
import com.db.DaoMaster;
import com.db.DaoSession;
import com.db.PileInfoDao;
import com.db.ScreenInfoDao;
import com.db.TrayInfoDao;
import com.fanfull.base.BaseApplication;

/**
 * Created by andy on 17-2-10.
 */

public class DBService {

    private static final String DB_NAME = "greedDaoDemo.db";
    private DaoSession daoSession;
    private static DBService mDBService;

    private DBService(){
    	init(BaseApplication.getContext());
    }

    public static DBService getService(){
        if(mDBService==null){
            mDBService = new DBService();
        }
        return mDBService;
    }

    public void init(Context context) {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);

        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());

        daoSession = daoMaster.newSession();
    }

    public BagInfoDao getBagInfoDao(){
        return daoSession.getBagInfoDao();
    }

    public PileInfoDao getPileInfoDao(){
        return daoSession.getPileInfoDao();
    }

    public ScreenInfoDao getScreenInfoDao(){
        return daoSession.getScreenInfoDao();
    }

    public TrayInfoDao getTrayInfoDao(){
        return daoSession.getTrayInfoDao();
    }
}
