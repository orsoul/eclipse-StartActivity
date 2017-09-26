package com.fanfull.op;

/**
 * 扫描高频的抽象类,必须实现扫描结果的回调方法.
 * 
 * @author orsoul
 * 
 */
public abstract class RFIDReadTask implements Runnable {
	/** 读取区域 的起始地址 */
	private int sa;
	/** 读取数据缓冲 */
	private byte[] dataBuf;
	private byte[] uid;
	/** 尝试进行 读取数据的 时间 */
	private long runTime = 5000;
	/** 是否为读M1卡 */
	private boolean isReadM1;
	private boolean isRunning;
	private int msgWhat = -1;

	public void setMsgWhat(int msgWhat) {
		this.msgWhat = msgWhat;
	}

	@Override
	public void run() {
		isRunning = true;
		boolean readSuccess;
		if(isReadM1) {
			readSuccess=RFIDOperation.getInstance().readM1(sa,dataBuf,runTime,uid);
		} else {
			readSuccess = RFIDOperation.getInstance().readNFCInTime(sa,
					dataBuf, runTime, uid);
		}

        onReadResult(readSuccess, dataBuf,isReadM1, msgWhat);

		isRunning = false;
	}

	/**
	 * @param readSuccess true：读取成功；
	 * @param dataBuf 读到的数据
	 * @param isReadM1 true：读的是M1卡；false:读的是NFC卡
	 * @param msgWhat
	 */
	public abstract void onReadResult(boolean readSuccess, byte[] dataBuf, boolean isReadM1, int msgWhat);

	/**
	 * @return 正在读取数据返回 true
	 */
	public boolean isRunning() {
		return isRunning;
	}
	/**
	 * @param sa
	 *            读取区域 的起始地址, 单位：字（字长4byte）;若为读M1卡，则表示块区号
	 */
	public void setSa(int sa) {
		this.sa = sa;
	}
	/**
	 * @param runTime
	 *            尝试进行 读取数据的 时间
	 */
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

    /**
     * @param dataBuf 存储所读数据的byte[]。若为读M1卡，此数组长度应为[1,16]
     */
    public void setDataBuf(byte[] dataBuf) {
        this.dataBuf = dataBuf;
    }

    /**
     * @param uid RFID的uid，通过寻卡获得，若设为null，读数据前会先进行寻卡操作
     */
    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    /**
     * @param readM1 设置为true读M1卡，设为false读NFC卡
     */
    public void setReadM1(boolean readM1) {
        isReadM1 = readM1;
    }
}