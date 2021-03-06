package com.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.entity.PileInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PILE_INFO".
*/
public class PileInfoDao extends AbstractDao<PileInfo, String> {

    public static final String TABLENAME = "PILE_INFO";

    /**
     * Properties of entity PileInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property PileID = new Property(0, String.class, "pileID", true, "PILE_ID");
        public final static Property BagType = new Property(1, String.class, "bagType", false, "BAG_TYPE");
        public final static Property MoneyType = new Property(2, String.class, "moneyType", false, "MONEY_TYPE");
        public final static Property MoneyModel = new Property(3, String.class, "moneyModel", false, "MONEY_MODEL");
        public final static Property TotalAmount = new Property(4, String.class, "totalAmount", false, "TOTAL_AMOUNT");
        public final static Property OrganID = new Property(5, String.class, "organID", false, "ORGAN_ID");
        public final static Property SerialNum = new Property(6, String.class, "serialNum", false, "SERIAL_NUM");
        public final static Property ScreenNum = new Property(7, int.class, "screenNum", false, "SCREEN_NUM");
        public final static Property BagNum = new Property(8, int.class, "bagNum", false, "BAG_NUM");
        public final static Property Refresh_flag = new Property(9, int.class, "refresh_flag", false, "REFRESH_FLAG");
        public final static Property Update_time = new Property(10, String.class, "update_time", false, "UPDATE_TIME");
        public final static Property PileName = new Property(11, String.class, "pileName", false, "PILE_NAME");
        public final static Property Series = new Property(12, String.class, "series", false, "SERIES");
        public final static Property Time = new Property(13, String.class, "time", false, "TIME");
        public final static Property ClearingType = new Property(14, String.class, "clearingType", false, "CLEARING_TYPE");
        public final static Property StoreId = new Property(15, String.class, "storeId", false, "STORE_ID");
    }


    public PileInfoDao(DaoConfig config) {
        super(config);
    }
    
    public PileInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PILE_INFO\" (" + //
                "\"PILE_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: pileID
                "\"BAG_TYPE\" TEXT," + // 1: bagType
                "\"MONEY_TYPE\" TEXT," + // 2: moneyType
                "\"MONEY_MODEL\" TEXT," + // 3: moneyModel
                "\"TOTAL_AMOUNT\" TEXT," + // 4: totalAmount
                "\"ORGAN_ID\" TEXT," + // 5: organID
                "\"SERIAL_NUM\" TEXT," + // 6: serialNum
                "\"SCREEN_NUM\" INTEGER NOT NULL ," + // 7: screenNum
                "\"BAG_NUM\" INTEGER NOT NULL ," + // 8: bagNum
                "\"REFRESH_FLAG\" INTEGER NOT NULL ," + // 9: refresh_flag
                "\"UPDATE_TIME\" TEXT," + // 10: update_time
                "\"PILE_NAME\" TEXT," + // 11: pileName
                "\"SERIES\" TEXT," + // 12: series
                "\"TIME\" TEXT," + // 13: time
                "\"CLEARING_TYPE\" TEXT," + // 14: clearingType
                "\"STORE_ID\" TEXT);"); // 15: storeId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PILE_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PileInfo entity) {
        stmt.clearBindings();
 
        String pileID = entity.getPileID();
        if (pileID != null) {
            stmt.bindString(1, pileID);
        }
 
        String bagType = entity.getBagType();
        if (bagType != null) {
            stmt.bindString(2, bagType);
        }
 
        String moneyType = entity.getMoneyType();
        if (moneyType != null) {
            stmt.bindString(3, moneyType);
        }
 
        String moneyModel = entity.getMoneyModel();
        if (moneyModel != null) {
            stmt.bindString(4, moneyModel);
        }
 
        String totalAmount = entity.getTotalAmount();
        if (totalAmount != null) {
            stmt.bindString(5, totalAmount);
        }
 
        String organID = entity.getOrganID();
        if (organID != null) {
            stmt.bindString(6, organID);
        }
 
        String serialNum = entity.getSerialNum();
        if (serialNum != null) {
            stmt.bindString(7, serialNum);
        }
        stmt.bindLong(8, entity.getScreenNum());
        stmt.bindLong(9, entity.getBagNum());
        stmt.bindLong(10, entity.getRefresh_flag());
 
        String update_time = entity.getUpdate_time();
        if (update_time != null) {
            stmt.bindString(11, update_time);
        }
 
        String pileName = entity.getPileName();
        if (pileName != null) {
            stmt.bindString(12, pileName);
        }
 
        String series = entity.getSeries();
        if (series != null) {
            stmt.bindString(13, series);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(14, time);
        }
 
        String clearingType = entity.getClearingType();
        if (clearingType != null) {
            stmt.bindString(15, clearingType);
        }
 
        String storeId = entity.getStoreId();
        if (storeId != null) {
            stmt.bindString(16, storeId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PileInfo entity) {
        stmt.clearBindings();
 
        String pileID = entity.getPileID();
        if (pileID != null) {
            stmt.bindString(1, pileID);
        }
 
        String bagType = entity.getBagType();
        if (bagType != null) {
            stmt.bindString(2, bagType);
        }
 
        String moneyType = entity.getMoneyType();
        if (moneyType != null) {
            stmt.bindString(3, moneyType);
        }
 
        String moneyModel = entity.getMoneyModel();
        if (moneyModel != null) {
            stmt.bindString(4, moneyModel);
        }
 
        String totalAmount = entity.getTotalAmount();
        if (totalAmount != null) {
            stmt.bindString(5, totalAmount);
        }
 
        String organID = entity.getOrganID();
        if (organID != null) {
            stmt.bindString(6, organID);
        }
 
        String serialNum = entity.getSerialNum();
        if (serialNum != null) {
            stmt.bindString(7, serialNum);
        }
        stmt.bindLong(8, entity.getScreenNum());
        stmt.bindLong(9, entity.getBagNum());
        stmt.bindLong(10, entity.getRefresh_flag());
 
        String update_time = entity.getUpdate_time();
        if (update_time != null) {
            stmt.bindString(11, update_time);
        }
 
        String pileName = entity.getPileName();
        if (pileName != null) {
            stmt.bindString(12, pileName);
        }
 
        String series = entity.getSeries();
        if (series != null) {
            stmt.bindString(13, series);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(14, time);
        }
 
        String clearingType = entity.getClearingType();
        if (clearingType != null) {
            stmt.bindString(15, clearingType);
        }
 
        String storeId = entity.getStoreId();
        if (storeId != null) {
            stmt.bindString(16, storeId);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public PileInfo readEntity(Cursor cursor, int offset) {
        PileInfo entity = new PileInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // pileID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // bagType
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // moneyType
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // moneyModel
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // totalAmount
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // organID
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // serialNum
            cursor.getInt(offset + 7), // screenNum
            cursor.getInt(offset + 8), // bagNum
            cursor.getInt(offset + 9), // refresh_flag
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // update_time
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // pileName
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // series
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // time
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // clearingType
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15) // storeId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PileInfo entity, int offset) {
        entity.setPileID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setBagType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMoneyType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMoneyModel(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTotalAmount(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOrganID(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSerialNum(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setScreenNum(cursor.getInt(offset + 7));
        entity.setBagNum(cursor.getInt(offset + 8));
        entity.setRefresh_flag(cursor.getInt(offset + 9));
        entity.setUpdate_time(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPileName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setSeries(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setTime(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setClearingType(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setStoreId(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
     }
    
    @Override
    protected final String updateKeyAfterInsert(PileInfo entity, long rowId) {
        return entity.getPileID();
    }
    
    @Override
    public String getKey(PileInfo entity) {
        if(entity != null) {
            return entity.getPileID();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PileInfo entity) {
        return entity.getPileID() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
