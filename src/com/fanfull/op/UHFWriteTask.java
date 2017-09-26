package com.fanfull.op;
/**
 * 写超高频的抽象类,必须实现扫描结果的回调方法.新建对象的默认参数被设为读取EPC区的12个byte,无过滤.
 * 在运行子类前通过更改相应字段可调整读取区域、读取数据长度等
 * 
 * @author orsoul
 * 
 */
public abstract class UHFWriteTask implements Runnable {
	private byte[] filter;
	private int mmb = UHFOperation.MB_EPC;
	private int msa = 0x02;

	private byte[] data;
	private int mb = UHFOperation.MB_EPC;
	private int sa = 0x02;
	private long runTime = 5000;

	private boolean isRunning;
	private int msgWhat = -1;

	public void setMsgWhat(int msgWhat) {
		this.msgWhat = msgWhat;
	}

	@Override
	public void run() {
		isRunning = true;

		boolean writeSuccess = UHFOperation.getInstance().writeUHFInTime(data, mb, sa,
				 runTime, filter, mmb, msa);

		onWriteResult(writeSuccess, mb, data, msgWhat);

		isRunning = false;
	}

	/**
	 * @param writeSuccess
	 *            若写入成功 返回true
	 * @param msgWhat
	 */
	public abstract void onWriteResult(boolean writeSuccess, int mb, byte[] data, int msgWhat);

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
     * @param data 写入的数据
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
	 * @param mb
	 *            打算写的区域, 1=epc, 2=tid, 3=use, 其他参数无效
	 */
	public void setMb(int mb) {
		this.mb = mb;
	}

	/**
	 * @param sa
	 *            写入区域 的起始地址
	 */
	public void setSa(int sa) {
		this.sa = sa;
	}


	/**
	 * @param runTime
	 *            尝试进行 写入数据的 时间
	 */
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

}