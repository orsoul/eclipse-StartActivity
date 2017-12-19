package com.fanfull.db;

import java.util.List;

import android.content.Context;

import com.fanfull.base.BaseApplication;
import com.fanfull.db.FingerDao.Properties;
import com.fanfull.utils.LogsUtil;

public class FingerDbService {  
      
    private static final String TAG = FingerDbService.class.getSimpleName();  
    private static FingerDbService instance;  
    private static Context appContext;  
    private DaoSession mDaoSession;  
    private FingerDao mFingerDao;
      
      
    private FingerDbService() {  
    }  
  
    public static FingerDbService getInstance(Context context) {  
        if (instance == null) {  
            instance = new FingerDbService();  
            if (appContext == null){  
                appContext = context.getApplicationContext();  
            }  
            instance.mDaoSession = BaseApplication.getDaoSession(context);
            instance.mFingerDao = instance.mDaoSession.getFingerDao();  
        }  
        return instance;  
    }  
      
    /**
     * load one finger info by id
     * @return finger
     */  
    public Finger loadFinger(long id) {  
        return mFingerDao.load(id);  
    }  
    
    /**
     * load all finger info
     * @return list
     */
    public List<Finger> loadAllFinger(){  
        return mFingerDao.loadAll();  
    }  
     
    public List<Finger> queryFingerByUserId(String userid){ 
    	List<Finger> list = mFingerDao.queryBuilder()
				.where(Properties.User_id.eq(userid))
				.list();
    	return list;
    }
    
    public Finger queryFingerByFingerID(String fingerid){ 
    	Finger finger = null;
    	finger = mFingerDao.queryBuilder()
				.where(Properties.Finger_id.eq(fingerid)).unique();
    	return finger;
    }
    /** 
     * query list with where clause 
     * ex: begin_date_time >= ? AND end_date_time <= ? 
     * @param where where clause, include 'where' word 
     * @param params query parameters 
     * @return 
     */  
      
    public List<Finger> queryFinger(String where, String... params){  
        return mFingerDao.queryRaw(where, params); 
    }  
    /** 
     * insert or update finger 
     * @param finger 
     * @return insert or update finger id 
     */  
    public long saveFinger(Finger finger){  
        return mFingerDao.insertOrReplace(finger);  
    }  
    public void updateFinger (Finger finger){
    	mFingerDao.update(finger);
    }
      
    /** 
     * insert or update noteList use transaction 
     * @param list 
     */  
    public void saveFingerLists(final List<Finger> list){  
            if(list == null || list.isEmpty()){  
                 return;  
            }  
            mFingerDao.getSession().runInTx(new Runnable() {  
            @Override  
            public void run() {  
                for(int i=0; i<list.size(); i++){  
                	Finger finger = list.get(i);  
                    mFingerDao.insertOrReplace(finger);  
                }  
            }  
        });  
          
    }  
      
    /** 
     * delete all finger 
     */  
    public void deleteAllFinger(){  
        mFingerDao.deleteAll();  
    }  
      
    /** 
     * delete finger by id 
     * @param id 
     */  
    public void deleteFinger(long id){  
        mFingerDao.deleteByKey(id);  
        LogsUtil.i(TAG, "delete");  
    }  
      
    public void deleteFinger(Finger finger){  
        mFingerDao.delete(finger);  
    }  
    public void deleteFingerByCardid(List<Finger> finger){  
        mFingerDao.deleteInTx(finger);
    } 
    
    public Long getKeyIdLong (){
    	List<Finger> list = mFingerDao.loadAll();
    	LogsUtil.d(TAG, "loadAll size="+list.size());
    	if(list == null || list.size() == 0) return (long) 1;
    	else {
			return mFingerDao.getKey(list.get(list.size()-1))+1;
		}
    }
    
}  
