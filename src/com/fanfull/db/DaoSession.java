package com.fanfull.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.fanfull.db.Finger;

import com.fanfull.db.FingerDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig fingerDaoConfig;

    private final FingerDao fingerDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        fingerDaoConfig = daoConfigMap.get(FingerDao.class).clone();
        fingerDaoConfig.initIdentityScope(type);

        fingerDao = new FingerDao(fingerDaoConfig, this);

        registerDao(Finger.class, fingerDao);
    }
    
    public void clear() {
        fingerDaoConfig.getIdentityScope().clear();
    }

    public FingerDao getFingerDao() {
        return fingerDao;
    }

}
