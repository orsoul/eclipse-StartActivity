package com.fanfull.db;

import org.greenrobot.greendao.query.QueryBuilder;

import com.db.DBService;
import com.db.PileInfoDao;
import com.entity.PileInfo;

/**
 * 墨水屏 相关 的 本地数据库 操作
 * @author orsoul
 *
 */
public class ScreenDBHelper {

	/**
	 * 根据 堆id 查询堆信息
	 * @param pileId
	 * @return
	 */
	public static PileInfo queryPileFromPileId(String pileId) {
		PileInfo pileInfo = null;
		if (pileId == null) {
			return null;
		}
		
		PileInfoDao dao = DBService.getService().getPileInfoDao();
		QueryBuilder<PileInfo> builder = dao.queryBuilder();
		builder.where(PileInfoDao.Properties.PileID.eq(pileId));
		pileInfo = builder.unique();
		
//		pileInfo = new PileInfo();
//		pileInfo.setBagType("100元");
//		pileInfo.setBagNum((int) (Math.random() * 100));
//		pileInfo.setMoneyType("完整");
//		pileInfo.setClearingType("");
		return pileInfo;
	}
	/**
	 * 根据 屏id 查询堆信息
	 * @param screenId
	 * @return
	 */
	public static PileInfo queryPileFromScreenId(String screenId) {
		// TODO Auto-generated method stub
		
		if (screenId == null) {
			return null;
		}
		
		PileInfo pileInfo = new PileInfo();
		pileInfo.setBagType("100元");
		pileInfo.setBagNum((int) (Math.random() * 100));
		pileInfo.setMoneyType("完整");
		pileInfo.setClearingType("");
		return pileInfo;
	}
	/**
	 * 根据堆中的 袋id 查询堆信息
	 * @param bytesToHexString
	 * @return
	 */
	public static PileInfo queryPileFromBagId(String bagId) {
		// TODO Auto-generated method stub
		return null;
	}
	public static PileInfo queryTrayFromBagId(String bagId) {
		// TODO Auto-generated method stub
		return null;
	}

}
