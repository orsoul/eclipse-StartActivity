package com.fanfull.operation;

import android.os.SystemClock;
import android.util.Log;

import com.fanfull.contexts.StaticString;
import com.fanfull.hardwareAction.RFIDOperation;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;

/**
 * 读取高频RFID卡里面的内容
 * 
 * @ClassName: BagOperation
 * @Description: 基金袋操作
 * @author Keung
 * @date 2014-11-7 上午10:17:50
 * 
 */
public class BagOperation {
	private final static String TAG = BagOperation.class.getSimpleName();
	public static final int BAG_NO_LOCK = 1;// 袋子未锁
	public static final int BAG_LOCKED = 2;// 袋子已锁
	
	public static final int BAG_MATCH = 3;// 正确的袋子,已初始化
	public static final int BAG_NO_INIT = 4;// 袋子未初始化
	public static final int READ_BAG_FAILED = 5;// 条码 未 扫描
	
	public static final int BAG_TYPE_NO_MATCH = 6;// 袋型号不匹配
	public static final int BARCODE_NO_SCANNED = 7;// 条码 未 扫描
	String bagID = null;
	private String BarValue = null;
	// 交接内容
	private String organization = "0000000000";
	private String people = "001002003";
	private String time = "000000000000";
	private String operation = "0";
	private String thing = "0";
	private String result = "0";
	private String number = "16";
	private String check = "00";
	private String replayID = null;// 比较回复的袋ID
	
	private static RFIDOperation mRFIDOp;
	private byte []mUid = null; 
	private NFCBagOperation mNfcBagOperation = null;
	private static BagOperation mBagOperation;
	
	public static BagOperation getInstance (){
		if(mBagOperation == null){
			mBagOperation = new BagOperation();
			mRFIDOp = RFIDOperation.getInstance();
//			initRfid ();
//			// 初始化高频模块
		}
		return mBagOperation;
	}
//	public BagOperation() {
//		
//		
//
//	}
	public boolean connection (){
		return mRFIDOp.connection();
	}
	/**
	 * 获取UID 用来判定接下来是对NFC 卡 操作 还是M1卡操作
	 * @return
	 */
	public byte[] getUid (){
		return (mUid = mRFIDOp.activatecard());
	}
	/**
	 * 获取NFC卡 袋锁操作对象，减少代码操作，故在此将两种卡进行关联。
	 * @return
	 */
	public NFCBagOperation getNfcBagOperation (){
		if(null != mNfcBagOperation){
			return mNfcBagOperation;
		}else {
			mNfcBagOperation = new NFCBagOperation();
			return  mNfcBagOperation;
		}
		
	}
	/**
	 * @return 0 成功；1 扫描失败；2 补写条码；3，袋型号不匹配；4 条码扫描不对
	 * @Description: 读袋ID == 读 RFID 第一块区
	 */
	public int readBagID() {
//		mRFIDOp.reset();
		int count = 0;
		while (bagID == null) {
			if (40 < ++count) {
				return READ_BAG_FAILED;// 扫描失败
			}
			
			mRFIDOp.activatecard();
			bagID = mRFIDOp.readBlock(1);
			
			SystemClock.sleep(50);
		}
		int bagVersion = Integer.valueOf(bagID.substring(0, 2));// 袋ID版本
		StaticString.bagid = bagID.substring(0, 26);
		LogsUtil.d(TAG, "140 StaticString.bagid:" + StaticString.bagid);
		if (bagVersion == 5) {
			bagID = null;
			return bagVersion;
		} else {
			bagID = null;
			return BAG_NO_INIT;// 版本不对需要补写
		}
	}
	
	/**
	 * 读取第九块位置的标志位信息
	 * @return -1 读取失败，0初始化时没有更改标志位，1锁片没锁好，2已插上锁片 3可以正常开袋 4错误，单片机没有将其循环利用
	 */
	public int readInitFlag() {//判断锁片有没有锁好
		int count = 0;
		String flag = null;
		while (flag==null) {
			if (20 < ++count) {
				return -1;// 扫描失败
			}
			flag =  mRFIDOp.readBlock(9);
			SystemClock.sleep(20);
		}
		return Integer.parseInt(flag.substring(0, 2));
	}
	
	
//	public void close () {
//		mRFIDOp.close();
//	}
	public void closeRf () {
		mRFIDOp.closeRf();
	}
	/**
	 * 
	 * @Title: checkBag
	 * @Description: 校验基金袋中类型
	 * @return 0,成功；1,扫描失败；2，补写条码；3，袋型号不匹配；4,条码扫描不对
	 * @throws
	 */
	public int checkBag() {
		LogsUtil.d(TAG, "checkBag+BarValue" + BarValue);
		int count = 0;
		int barvalue = 0;
		if (BarValue != null) {
			barvalue = Integer.valueOf(BarValue.substring(17, 19));// 条码类型
		} else {
			return BARCODE_NO_SCANNED;
		}
		while (bagID == null) {
			mRFIDOp.activatecard();
			bagID = mRFIDOp.readBlock(1);
			count++;
			if (count > 20) {
				return READ_BAG_FAILED;// 扫描不到
			}
		}
		bagID = bagID.substring(0, 18);
		int bagtype = Integer.valueOf(bagID.substring(6, 8));// 袋类型
		LogsUtil.d("bagtype" + bagtype + "tiama" + barvalue);
		LogsUtil.d("bagId" + bagID);
		LogsUtil.d("replay" + replayID);
		
		if (bagID.endsWith(replayID)) {
			if (bagtype == barvalue) {
				bagID = null;
				return BAG_MATCH;
			} else {
				bagID = null;
				return BAG_TYPE_NO_MATCH;
			}
		} else {
			bagID = null;
			return BAG_NO_INIT;// 版本不对需要补写
		}
	}
	/**
	 * 
	 * @Title: readBarCOde
	 * @Description:读出基金袋锁芯片中的条码比对
	 * @param @return 设定文件
	 * @return ture,比对条码成功，false比对条码失败
	 * @throws
	 */
	public Boolean readBarCode() {
		int count = 0;
		String data = null;
		while (data == null) {
			data = mRFIDOp.readBarCode();
			count++;
			if (count > 50) {
				return false;
			}
		}
		StaticString.barcode = data;
		System.err.println("barcode:" + data);
		return true;
	}

	
	/**
	 * 
	 * @Title: readBarCOde
	 * @Description:读出基金袋锁芯片中的条码比对
	 * @param @return 设定文件
	 * @return ture,比对条码成功，false比对条码失败
	 * @throws
	 */
	public String readBagBarCode() {
		int count = 0;
		String data = null;
		while (data == null) {
			data = mRFIDOp.readBagBarCode();
			count++;
			if (count > 50) {
				return null;
			}
		}
		StaticString.bagbarcode = data;
		System.err.println("bagbarcode:" + data);
		return data;
	}
	
	/**
	 * 使用RFID卡读取第9 块区的数据
	 * 
	 * @Title: readBagLock
	 * @Description:读出基金袋锁
	 * @param @return 设定文件
	 * @return 0没有扫到袋子；1锁片没锁好；2成功；
	 * @throws
	 */
	/**
	 * @return 0没有扫到袋子；1锁片没锁好；2成功；
	 * @Description:读出基金袋锁 = 读取第9 块区的数据
	 */
	
	public int readBagLock() {
	//	mRFIDOp.reset();
		String data = null;
		int count = 0;
		while (data == null) {
			mRFIDOp.activatecard();
			data = mRFIDOp.readBlock(9);
			count++;
			if (count > 60) {
				return READ_BAG_FAILED;// 扫描不到
			}
			SystemClock.sleep(50);
		}
		Log.v(TAG + "readBagLock()读第9 块区数据" + "RFID", data + " data.length:"
				+ data.length());
		if (data.endsWith("9")) {
			return BAG_LOCKED;
		} else {
			return BAG_NO_LOCK;
		}
	}

	/**
	 * 
	 * @Title: writeTrace
	 * @Description: 往锁芯片中写入痕迹
	 *               将35个数字转化为30个十六进制数（每七个转化为一个6位十六进制数，少了自动补0），将每两个转为byte
	 *               【】写入到第16块区
	 * 
	 * @param 要写的痕迹字符串
	 * @return Boolean 返回类型
	 * @throws
	 */
	public Boolean writeTrace(int index) {
		StringBuffer trace = new StringBuffer();
		StringBuffer tempString = new StringBuffer();

		tempString.append(organization).append(people).append(time)
				.append(operation).append(thing).append(result).append(number);
		LogsUtil.d(tempString.toString());
		for (int a = 0; a < 5; a++) {
			int tempindex = Integer.valueOf(tempString.substring(a * 7,// 转化为int时前面为的0自动消失
					((a + 1) * 7)));
			LogsUtil.d("tempindex=" + tempindex);
			String content = String.valueOf(Integer.toHexString(tempindex));
			if (content.length() < 7) {
				int j = content.length();
				for (int i = j; i < 6; i++) {
					content = "0" + content;
				}
			}
			trace.append(content);
			LogsUtil.d(content);
		}

		int count = 0;// 写入次数级数
		LogsUtil.d("finish tracedata init");
		byte[] data = ArrayUtils.hexString2Bytes(new String(trace));
		if (data != null) {
			while (!(mRFIDOp.writeBlock(index, data))) {
				// 可做提示操作

				count++;// 超过十次失败退出
				if (count > 20) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return
	 * @description 生成 新的 袋ID
	 */
	public byte[] getNewBagID() {
		LogsUtil.d(TAG, "writeBagID");
		uid = new byte[4];
		String bagID = null;
		// 05310100100210307091411071425050000100共38位数据
		// 0531(地区ID4位) 01（网点ID2位） 001（库ID3位）002103（流水号6位） 07 (币种2位) 09(袋型号2位)
		// 141107（日期6位）142505（时间6位） 0(残损1位)0(清分1位)0（复点1位）01（人头2位）00(补2位00)
		// 袋ID数据组成 版本2位（04开头），地区码，
		uid = mRFIDOp.activatecard();
		bagID = mRFIDOp.readBlock(1);
		if (null == bagID) {
			return null;
		}
		LogsUtil.s("RFID uid : " + ArrayUtils.bytes2HexString(uid));
		LogsUtil.s("RFID bagID pre: " + bagID);
		StringBuffer newBagID = new StringBuffer();
		// 在stringBuffer中新建一个元素以04开头，然后附加读到第1块区的第1位到第3位。。。。。
		newBagID.append("04")
				.append(StaticString.barcode.substring(1, 4)); // 地区码

		char ch = StaticString.barcode.charAt(31);
		newBagID.append(ch);
		if ('1' == ch) {
			newBagID.append(StaticString.barcode.charAt(33));
		} else {
			newBagID.append(StaticString.barcode.charAt(32));
		}
		
		newBagID.append(StaticString.barcode.substring(
						StaticString.barcode.length() - 21,
						StaticString.barcode.length() - 19)).append('0');// 袋型号
		byte[] id = new byte[16];
		for (int i = 0; i < 5; i++) {// 前4位
										// 数是截取newid(id[0],id[1],id[2],id[3]),前4位组成版本号（04）,地区码，残损标志，袋型号
			id[i] = (byte) (Integer.valueOf(newBagID.charAt(i * 2) - 48) * 16 + Integer
					.valueOf(newBagID.charAt(i * 2 + 1) - 48));
		}
		for (int i = 0; i < uid.length; i++) { // 第5位到第8位(id[4],id[5],id[6],id[7]是读取厂家码4位)
			id[5 + i] = uid[i];
		}
		id[9] = (byte) 0x00;
		for (int i = 0; i < 9; i++) { // 校验码id[8](与前面所有的数异或)
			id[9] ^= id[i];
		}
		for (int i = 10; i < id.length; i++) { // id[9],id[10]....全设为 0x00
			id[i] = 0x00;
		}
		LogsUtil.s("RFID bagID new: " + ArrayUtils.bytes2HexString(id));// id=0444400021A39DCED100000000000000(16个byte)以16进制输出
		return id;
	}

	/**
	 * 得到新锁的ID内容
	 * @param data 扫描Epc得到的Tid号
	 * @return
	 */
	public byte[] getNewBagID(byte []data) {
		LogsUtil.d(TAG, "writeBagID");
		// 05310100100210307091411071425050000100共38位数据
		// 0531(地区ID4位) 01（网点ID2位） 001（库ID3位）002103（流水号6位） 07 (币种2位) 09(袋型号2位)
		// 141107（日期6位）142505（时间6位） 0(残损1位)0(清分1位)0（复点1位）01（人头2位）00(补2位00)
		// 袋ID数据组成 版本2位（04开头），地区码，
		StringBuffer newBagID = new StringBuffer();
		// 在stringBuffer中新建一个元素以04开头，然后附加读到第1块区的第1位到第3位。。。。。
		newBagID.append("04")
				.append(StaticString.barcode.substring(1, 4)); // 地区码

		char ch = StaticString.barcode.charAt(31);
		newBagID.append(ch);
		if ('1' == ch) {
			newBagID.append(StaticString.barcode.charAt(33));
		} else {
			newBagID.append(StaticString.barcode.charAt(32));
		}
		
		newBagID.append(StaticString.barcode.substring(
						StaticString.barcode.length() - 21,
						StaticString.barcode.length() - 19)).append('0');// 袋型号
		byte[] id = new byte[12];
		for (int i = 0; i < 4; i++) {// 前4位
										// 数是截取newid(id[0],id[1],id[2],id[3]),前4位组成版本号（04）,地区码，残损标志，袋型号
			id[i] = (byte) (Integer.valueOf(newBagID.charAt(i * 2) - 48) * 16 + Integer
					.valueOf(newBagID.charAt(i * 2 + 1) - 48));
		}
		for (int i = 0; i < data.length; i++) { // 第5位到第8位(id[4],id[5],id[6],id[7],id[8],id[9]是读取厂家码6位)  即共10位数据
			id[4 + i] = data[i];
		}
		id[10] = (byte) 0x00;
		for (int i = 0; i < 10; i++) { // 校验码id[10](与前面所有的数异或)
			id[10] ^= id[i];
		}
		for (int i = 11; i < id.length; i++) { // id[9],id[10]....全设为 0x00
			id[i] = 0x00;
		}
		LogsUtil.s("RFID bagID new: " + ArrayUtils.bytes2HexString(id));// id=0444400021A39DCED100000000000000(16个byte)以16进制输出
		return id;
	}
	/**
	 * 第十块存放启用码
	 * @return
	 */
	public byte[] readEnableBagCode (){
		byte[] data = null;
		int count = 0;
		while (data == null) {
			mRFIDOp.activatecard();
			data = mRFIDOp.readBlockToByte(10);
			count++;
			if (count > 60) {
				return null;// 扫描不到
			}
			SystemClock.sleep(50);
		}
		LogsUtil.d(TAG, "qqq:"+ArrayUtils.bytes2HexString(data));
		byte[] tmp = new byte[10];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = data[i];
		}
		return tmp;
	}
	
	/**
	 * 第十六块存放目录索引
	 * @return
	 */
	public byte[] readIndexBlock (){
		byte [] data = null;
		int count = 0;
		while (data == null) {
			mRFIDOp.activatecard();
			data = mRFIDOp.readBlockToByte(16);
			count++;
			if (count > 30) {
				return null;// 扫描不到
			}
			SystemClock.sleep(50);
		}
		return data;//12个字节数据
	}
	
	/**
	 * 初始化基金袋的时候用到的方法
	 * 
	 * @Title: writeBagID
	 * @Description: 补写基金袋ID
	 * @param @return 设定文件
	 * @return Boolean 返回类型
	 * @throws
	 */
	public byte[] uid = null;

	/**
	 * @return 生成 袋ID 号, 并写入 第一块区
	 */
	public byte[] writeBagID() {
		LogsUtil.d(TAG, "writeBagID");
		uid = new byte[4];
		String bagID = null;
		// 05310100100210307091411071425050000100共38位数据
		// 0531(地区ID4位) 01（网点ID2位） 001（库ID3位）002103（流水号6位） 07 (币种2位) 09(袋型号2位)
		// 141107（日期6位）142505（时间6位） 0(残损1位)0(清分1位)0（复点1位）01（人头2位）00(补2位00)
		// 袋ID数据组成 版本2位（04开头），地区码，
		StringBuffer newBagID = new StringBuffer();
		int count = 0;
		while (bagID == null) {
			uid = mRFIDOp.activatecard();
			bagID = mRFIDOp.readBlock(1);
			count++;// 超过 次失败退出
			if (count > 100) {
				LogsUtil.s("读取RFID第一区失败");
				return null;
			}
			SystemClock.sleep(50);
		}
		// 在stringBuffer中新建一个元素以04开头，然后附加读到第1块区的第1位到第3位。。。。。
		newBagID.append("04")
				.append(StaticString.barcode.substring(1, 4))
				// 地区码
				.append(StaticString.barcode.substring(
						StaticString.barcode.length() - 32,
						StaticString.barcode.length() - 31))// 残损标志
				.append(StaticString.barcode.substring(
						StaticString.barcode.length() - 21,
						StaticString.barcode.length() - 19));// 袋型号
		byte[] id = new byte[16];
		for (int i = 0; i < 4; i++) {// 前4位
										// 数是截取newid(id[0],id[1],id[2],id[3]),前4位组成版本号（04）,地区码，残损标志，袋型号
			id[i] = (byte) (Integer.valueOf(newBagID.charAt(i * 2) - 48) * 16 + Integer
					.valueOf(newBagID.charAt(i * 2 + 1) - 48));
		}
		for (int i = 0; i < uid.length; i++) { // 第5位到第8位(id[4],id[5],id[6],id[7]是读取厂家码4位)
			id[4 + i] = uid[i];
		}
		id[8] = (byte) 0x00;
		for (int i = 0; i < 8; i++) { // 校验码id[8](与前面所有的数异或)
			id[8] ^= id[i];
		}
		for (int i = 9; i < id.length; i++) { // id[9],id[10]....全设为 0x00
			id[i] = 0x00;
		}
		LogsUtil.d(TAG, "new id=" + ArrayUtils.bytes2HexString(id));// id=0444400021A39DCED100000000000000(16个byte)以16进制输出
		while (!mRFIDOp.writeindex(1, id)) { // 把上面的数据都写到第一块区
			count++;
			if (count > 100) {
				LogsUtil.s("写入RFID第一区失败");
				return null;
			}
			SystemClock.sleep(50);
		}
		LogsUtil.s("写入RFID第一区成功");
		return id;
	}
	public byte[] writeBagID2(byte[] newID) {
		LogsUtil.d(TAG, "writeBagID");
//		uid = new byte[4];
//		String bagID = null;
		// 05310100100210307091411071425050000100共38位数据
		// 0531(地区ID4位) 01（网点ID2位） 001（库ID3位）002103（流水号6位） 07 (币种2位) 09(袋型号2位)
		// 141107（日期6位）142505（时间6位） 0(残损1位)0(清分1位)0（复点1位）01（人头2位）00(补2位00)
		// 袋ID数据组成 版本2位（04开头），地区码，
//		StringBuffer newBagID = new StringBuffer();
		int count = 0;
		while (bagID == null) {
			uid = mRFIDOp.activatecard();
			bagID = mRFIDOp.readBlock(1);
			count++;// 超过 次失败退出
			if (count > 100) {
				LogsUtil.s("读取RFID第一区失败");
				return null;
			}
			SystemClock.sleep(50);
		}
		while (!mRFIDOp.writeindex(1, newID)) { // 把上面的数据都写到第一块区
			count++;
			if (count > 60) {
				LogsUtil.s("写入RFID第一区失败");
				return null;
			}
			SystemClock.sleep(50);
		}
		LogsUtil.s("写入RFID第一区成功");
		return newID;
	}

	/**
	 * 
	 * @Title: writeBlock
	 * @Description: 写入袋码
	 * @param block块区
	 *            ，byte 内容
	 * @return Boolean 返回类型
	 * @throws
	 */
	public boolean writeBlock(int blockID, byte[] data) {
		return mRFIDOp.writeBlock(blockID, data);
	}

	/**
	 * 写标志位，将标志位写在第九块。
	 * @param data
	 * @return
	 */
	public boolean writeFlag (byte data){
		byte[] tmp = new byte[1];
		tmp[0] = data;
		return writeBlock(9,tmp);
		
	}
	/**
	 * 读标志位，将标志位从第九块读出。
	 * @return byte
	 */
	public byte readFlag (){
		byte [] tmp = mRFIDOp.readBlockToByte(9);
		return null == tmp ? null : tmp[0];
	}
	
	/**
	 * 写标志位，将标志位写在第二块。
	 * @param data
	 * @return
	 */
	public boolean writeTid (byte[] data){
		return writeBlock(2,data);
		
	}
	/**
	 * 读TID，将标志位从第二块读出。
	 * @return byte
	 */
	public byte[] readTid (){
		byte []tmp = new byte[7];
		byte []tmp2 =  mRFIDOp.readBlockToByte(2);
		if(tmp2 != null && tmp2.length >= 6){
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp2[i];
			}
		}
		return tmp;
	}
	/**
	 * 
	 * @Title: judgeBlock
	 * @Description: 判断是否是密码区
	 * @param @param block
	 * @param @return 设定文件
	 * @return Boolean 返回类型
	 * @throws
	 */
	public int judgeBlock(int block) {
		if (block < 60) {// 写到六十块区复写以前的块区
			if ((block + 1) % 4 == 0) {
				return block + 1;
			} else {
				return block;
			}
		} else {
			return 17;
		}
	}
	
	public String getOrganization() {
		if(mUid!=null && mUid.length==7){
			mNfcBagOperation.getOrganization();
		}
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
		if(mUid!=null && mUid.length==7){
			mNfcBagOperation.setOrganization(organization);
		}
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getThing() {
		return thing;
	}

	public void setThing(String thing) {
		this.thing = thing;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

	public String getBarValue() {
		if(mUid!=null && mUid.length==7){
			mNfcBagOperation.getBarValue();
		}
		return BarValue;
	}

	public void setBarValue(String barValue) {
		LogsUtil.d(TAG, "here:"+mUid.length);
		if(mUid!=null && mUid.length==7){
			mNfcBagOperation.setBarValue(BarValue);
		}
		BarValue = barValue;
	}

	public String getReplayID() {
		return replayID;
	}

	public void setReplayID(String replayID) {
		this.replayID = replayID;
	}

}
