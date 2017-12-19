package com.fanfull.op;

import com.fanfull.utils.LogsUtil;

/**
 * 扫描高频的抽象类,必须实现扫描结果的回调方法.
 * 
 * @author orsoul
 * 
 */
public abstract class BarcodeReadTask implements Runnable {
	/** 读取数据缓冲 */
	private byte[] dataBuf;
	/** 读取数据 持续时间 */
	protected long runTime = 5000;
	/** 读取数据 次数 */
	protected int readTimes = 1;
	protected boolean isRunning;
	private int msgWhat = -1;

	public void setMsgWhat(int msgWhat) {
		this.msgWhat = msgWhat;
	}

	public int getMsgWhat() {
		return msgWhat;
	}

	@Override
	public void run() {
		isRunning = true;
		BarcodeOperation.getInstance().scanData(dataBuf, readTimes, runTime,
				this);
		onScanFinish();
		isRunning = false;
		LogsUtil.w("barcode read", "end");
	}

	/**
	 * @param readSuccess
	 *            true：读取成功；
	 * @param dataBuf
	 *            读到的数据
	 * @param isReadM1
	 *            true：读的是M1卡；false:读的是NFC卡
	 * @param msgWhat
	 */
	public abstract void onReadResult(byte[] dataBuf, int len, int msgWhat);
	public void onScanFinish(){};

	/**
	 * @return 正在读取数据返回 true
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param dataBuf
	 *            存储所读数据的byte[]。若为读M1卡，此数组长度应为[1,16]
	 */
	public void setDataBuf(byte[] dataBuf) {
		this.dataBuf = dataBuf;
	}

	/**
	 * @param runTime
	 *            尝试进行 读取数据的 时间
	 */
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

	public void setReadTimes(int readTimes) {
		this.readTimes = readTimes;
	}
	
	
}