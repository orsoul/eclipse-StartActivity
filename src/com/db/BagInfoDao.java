package com.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.entity.BagInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BAG_INFO".
*/
public class BagInfoDao extends AbstractDao<BagInfo, String> {

    public static final String TABLENAME = "BAG_INFO";

    /**
     * Properties of entity BagInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property BagID = new Property(0, String.class, "bagID", true, "BAG_ID");
        public final static Property PileID = new Property(1, String.class, "pileID", false, "PILE_ID");
        public final static Property TrayID = new Property(2, String.class, "trayID", false, "TRAY_ID");
        public final static Property Update_time = new Property(3, String.class, "update_time", false, "UPDATE_TIME");
    }


    public BagInfoDao(DaoConfig config) {
        super(config);
    }
    
    public BagInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BAG_INFO\" (" + //
                "\"BAG_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: bagID
                "\"PILE_ID\" TEXT," + // 1: pileID
                "\"TRAY_ID\" TEXT," + // 2: trayID
                "\"UPDATE_TIME\" TEXT);"); // 3: update_time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BAG_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BagInfo entity) {
        stmt.clearBindings();
 
        String bagID = entity.getBagID();
        if (bagID != null) {
            stmt.bindString(1, bagID);
        }
 
        String pileID = entity.getPileID();
        if (pileID != null) {
            stmt.bindString(2, pileID);
        }
 
        String trayID = entity.getTrayID();
        if (trayID != null) {
            stmt.bindString(3, trayID);
        }
 
        String update_time = entity.getUpdate_time();
        if (update_time != null) {
            stmt.bindString(4, update_time);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BagInfo entity) {
        stmt.clearBindings();
 
        String bagID = entity.getBagID();
        if (bagID != null) {
            stmt.bindString(1, bagID);
        }
 
        String pileID = entity.getPileID();
        if (pileID != null) {
            stmt.bindString(2, pileID);
        }
 
        String trayID = entity.getTrayID();
        if (trayID != null) {
            stmt.bindString(3, trayID);
        }
 
        String update_time = entity.getUpdate_time();
        if (update_time != null) {
            stmt.bindString(4, update_time);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public BagInfo readEntity(Cursor cursor, int offset) {
        BagInfo entity = new BagInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // bagID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // pileID
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // trayID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // update_time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BagInfo entity, int offset) {
        entity.setBagID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPileID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTrayID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setUpdate_time(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final String updateKeyAfterInsert(BagInfo entity, long rowId) {
        return entity.getBagID();
    }
    
    @Override
    public String getKey(BagInfo entity) {
        if(entity != null) {
            return entity.getBagID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BagInfo entity) {
        return entity.getBagID() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
