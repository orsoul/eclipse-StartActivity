package com.fanfull.op;

/**
 * 扫描超高频的抽象类,必须实现扫描结果的回调方法.新建对象的默认参数被设为读取EPC区的12个byte,无过滤.
 * 在运行子类前通过更改相应字段可调整读取区域、读取数据长度等
 * 
 * @author orsoul
 * 
 */
public abstract class UHFReadTask implements Runnable {
	/** 过滤数据, 无过滤设为 null */
	private byte[] filter;
	/** 过滤区域, 1=epc, 2=tid, 3=use, 其他参数无效 */
	private int mmb = 1;
	/** 过滤区域的起始地址, 单位:字 */
	private int msa = 0x02;
	/** 打算读取的区域, 1=epc, 2=tid, 3=use, 其他参数无效 */
	private int mb = 1;
	/** 读取区域 的起始地址 */
	private int sa = 0x02;
	/** 读取数据长度.EPC区16byte,一般只用后12byte; TID最长为12byte; USE区? */
	private int dataLen = 12;
	/** 尝试进行 读取数据的 时间 */
	private long runTime = 5000;

	private boolean isRunning;
	private int msgWhat = -1;

	public void setMsgWhat(int msgWhat) {
		this.msgWhat = msgWhat;
	}

	@Override
	public void run() {
		isRunning = true;

		byte[] bs = new byte[dataLen];
		boolean readSuccess = UHFOperation.getInstance().readUHFInTime(mb, sa,
				bs, runTime, filter, mmb, msa);
		if (!readSuccess) {
			bs = null;
		}
		onReadResult(bs, msgWhat);

		isRunning = false;
	}

	/**
	 * @param bs
	 *            若读取成功 该参数为读到的数据, 否则为null
	 * @param msgWhat
	 */
	public abstract void onReadResult(byte[] bs, int msgWhat);

	/**
	 * @return 正在读取数据返回 true
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param filter
	 *            过滤数据, 无过滤设为 null
	 */
	public void setFilter(byte[] filter) {
		this.filter = filter;
	}

	/**
	 * @param mmb
	 *            过滤区域, 1=epc, 2=tid, 3=use, 其他参数无效
	 */
	public void setMmb(int mmb) {
		this.mmb = mmb;
	}

	/**
	 * @param msa
	 *            过滤区域的起始地址, 单位:字
	 */
	public void setMsa(int msa) {
		this.msa = msa;
	}

	/**
	 * @param mb
	 *            打算读取的区域, 1=epc, 2=tid, 3=use, 其他参数无效
	 */
	public void setMb(int mb) {
		this.mb = mb;
	}

	/**
	 * @param sa
	 *            读取区域 的起始地址
	 */
	public void setSa(int sa) {
		this.sa = sa;
	}

	/**
	 * @param dataLen
	 *            读取数据长度.EPC区16byte,一般只用后12byte; TID最长为12byte; USE区
	 */
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

	/**
	 * @param runTime
	 *            尝试进行 读取数据的 时间
	 */
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

}