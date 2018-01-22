package com.fanfull.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import android.os.SystemClock;

import com.fanfull.contexts.StaticString;
import com.fanfull.utils.ArrayUtils;
import com.fanfull.utils.LogsUtil;

/**
 * @ClassName: SocketConnet
 * @Description: socket连接
 */
public class SocketConnet implements Runnable {
	/** 通讯消息字段 分隔符:空格 */
	public static final String CH_SPLIT = " ";
	/** 发送消息 的 头标识:$ */
	public static final String CH_SEND_HEAD = "$";
	/** 接收消息 的 头标识:* */
	public static final String CH_RECEIVE_HEAD = "*";
	/** 接收、发送消息的 尾标识:# */
	public static final String CH_END = "#";

	private final String TAG = SocketConnet.class.getSimpleName();

	private static boolean enable;

	public static boolean isEnable() {
		return enable;
	}

	public static void setEnable(boolean enable) {
		SocketConnet.enable = enable;
	}

	private Socket sSocket = null;
	private OutputStream out = null;
	private InputStream in = null;

	private Thread mRecThread;
	private TimeoutThread mTimeoutThread;

	/** 连接天线编号 */
	private int mConnNum = -1;
	/** 回复编号 */
	private int commNum = -1;
	private int count = 0;
	/**
	 * 是否被动接收 服务器端 发来信息
	 */
	private boolean isPassiveReceive = true;

	private RecieveListener mRecListener;

	private static final SocketConnet sSocketConnet = new SocketConnet();

	private SocketConnet() {
	}

	public static SocketConnet getInstance() {
		return sSocketConnet;
	}

	public boolean isConnect() {
		// return -1 != mConnNum;
		return (null != sSocket) && (sSocket.isConnected()) && (-1 != mConnNum)
				&& (!sSocket.isInputShutdown());
	}

	/**
	 * @param recListener
	 * @des 设置接收到后台回复的回调函数
	 */
	public void setRecieveListener(RecieveListener recListener) {
		LogsUtil.d(TAG, "recListener:" + recListener);
		mRecListener = recListener;
		if (null != mTimeoutThread) {
			mTimeoutThread.setRecieveListener(recListener);
		}
	}

	/**
	 * @param commNum
	 *            指定接收线程 响应的回复编号, 设为-1响应所有
	 */
	public synchronized void setCommNum(int commNum) {
		this.commNum = commNum;
	}

	public synchronized int getCommNum() {
		return commNum;
	}

	/**
	 * 
	 * @param isPassiveReceive
	 *            是否被动接收 服务器端 发来信息,若设为true,所有来自服务端的消息都会触发回调函数
	 */
	public void setPassiveReceive(boolean isPassiveReceive) {
		this.isPassiveReceive = isPassiveReceive;
	}

	/**
	 * @return 返回 当前 连接 的天线系统, 0 == 1号门, 1 == 2号门, 其他 == 3号门, -1 == 未创建连接
	 */
	public int getConnectedDoorNum() {
		return mConnNum;
	}

	/**
	 * @Description: socket通信.在此方法中创建socket,并开启一个子线程线程接收 服务器 的发来的 信息
	 * @param num
	 *            0 == 连接 第一道天线; 1 == 第二到天线, 其他数字 == 第3道天线
	 * @return 连接成功返回 true
	 */
	public boolean connect(int num) {
		return connect(createSocket(num));
	}

	public boolean connect(Socket socket) {
		if (null == socket) {
			mConnNum = -1;
			return false;
		}
		try {
			sSocket = socket;
			out = sSocket.getOutputStream();
			in = sSocket.getInputStream();
		} catch (IOException e) {
			LogsUtil.s("Socket.getOutputStream() faile");
			// 连接失败
			if (null != mRecListener) {
				mRecListener.onConnect(null, -1);
			}
			mConnNum = -1;
			return false;
		}

		mRecThread = new Thread(this);
		mRecThread.start();
		mTimeoutThread = new TimeoutThread();
		mTimeoutThread.setRecieveListener(mRecListener);
		mTimeoutThread.start();

		// ThreadPoolFactory.getNormalPool().execute(printTask);
		String serverIp = ((InetSocketAddress) sSocket.getRemoteSocketAddress())
				.getAddress().getHostAddress();
		int port = sSocket.getPort();
		LogsUtil.d(TAG, "socket conn success serverIp: " + serverIp + " Prot: "
				+ port);
		SystemClock.sleep(50);// 等待 TimeoutThread 的充分运行？
		if (null != mRecListener) {
			mRecListener.onConnect(serverIp, port);
		}
		return true;
	}

	/**
	 * @param num
	 *            0 == 连接 第一道天线; 1 == 第二到天线, 其他数字 == 第3道天线
	 * @param timeOut
	 *            socket 连接 等待 时间
	 * @return 返回 创建好连接的 socket
	 */
	private Socket createSocket(int num, int timeOut) {

		Socket socket = new Socket();
		InetSocketAddress socketAddress = null;
		try {
			if (0 == num) {
				socketAddress = new InetSocketAddress(StaticString.IP0,
						StaticString.PORT0);
			} else if (1 == num) {
				socketAddress = new InetSocketAddress(StaticString.IP1,
						StaticString.PORT1);
			} else {
				socketAddress = new InetSocketAddress(StaticString.IP2,
						StaticString.PORT2);
			}
			socket.connect(socketAddress, timeOut);

		} catch (IOException e) {
			LogsUtil.d(TAG, "socket connect failed socketAddress:"
					+ socketAddress);
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
			}
			return null;
		}
		mConnNum = num;
		return socket;
	}

	private Socket createSocket(int num) {
		return createSocket(num, 3000);
	}

	/**
	 * @param num
	 * @return 返回 num 的字符串形式:num不满3位的,在前面补0;num为负数,原样返回
	 */
	private String getIntString(int num) {
		String reVal = null;
		reVal = String.valueOf(num);
		if (num < 0) {
			return reVal;
		}

		switch (reVal.length()) {
		case 1:
			reVal = "00" + reVal;
			break;
		case 2:
			reVal = "0" + reVal;
			break;
		default:
			break;
		}

		return reVal;
	}

	/**
	 * 向服务端发送信息
	 * 
	 * @param isWait4Recieve
	 *            是否等待服务端回复;设为false对回复不进行计时
	 * @param taskid
	 *            通讯编号
	 * @param info
	 *            通讯参数
	 * @return 信息发送成功返回true
	 */
	public boolean communication(boolean isWait4Recieve, int taskid,
			String... info) {
		count++;
		setCommNum(count);
		StaticString.information = null;
		boolean sendSuccess = send(SendTask.getInfo(taskid,
				getIntString(count), info));
		if (sendSuccess && isWait4Recieve) {
			mTimeoutThread.startTime();
		}
		return sendSuccess;
	}

	/**
	 * 向服务端发送信息,对回复进行计时
	 * 
	 * @param taskid
	 *            通讯编号
	 * @param info
	 *            通讯参数
	 * @return 信息发送成功返回true
	 */
	public boolean communication(int taskid, String... info) {
		return communication(true, taskid, info);
	}

	/**
	 * 向服务端发送信息,对回复进行计时
	 * 
	 * @param taskid
	 *            通讯编号
	 * @return 信息发送成功返回true
	 */
	public boolean communication(int taskid) {
		return communication(taskid, null);
	}

	/**
	 * @param data
	 *            待发送数据
	 * @return 数据发送成功返回true
	 */
	public boolean send(byte[] data) {
		if (null == data || null == out || !isConnect()) {
			return false;
		}

		try {
			out.write(data);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @param text
	 *            待发送字符串，按utf-8解码后发送
	 * @return 数据发送成功返回true
	 */
	public boolean send(String text) {
		if (null == text) {
			return false;
		}
		try {
			return send(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	/**
	 * 
	 * @Description: 关闭网络连接
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void close() {
		mConnNum = -1;

		if (null != mTimeoutThread) {
			mTimeoutThread.stopThread();
			mTimeoutThread = null;
		}
		if (null != mRecThread) {
			mRecThread.interrupt();
			mRecThread = null;
		}
		try {
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (sSocket != null) {
				sSocket.close();
				sSocket = null;
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		LogsUtil.w(TAG, "receiveThread run");
		try {
			// BufferedInputStream reader = new BufferedInputStream(in);
			byte recBuf[] = new byte[1024 << 2]; // 每次接受数据的buf
			byte tempBuf[] = null; // 缓存所有不完整的数据包
			int recLen;
			while ((recLen = in.read(recBuf)) != -1) {
				/* 处理收到的数据包，将不完整数据包合并为 */
				if (recBuf[0] == 42 && recBuf[recLen - 1] != 35) {
					// 1. 收到 第一个 不完整的数据包 (*开头，非#结尾)
					tempBuf = Arrays.copyOf(recBuf, recLen);
					LogsUtil.d(TAG, "第一个 不完整: " + recLen);
					continue;
				} else if (recBuf[0] != 42 && recBuf[recLen - 1] != 35) {
					// 2. 收到不完整的数据包 (非*开头，非#结尾)
					LogsUtil.d(TAG, "第x个 不完整: " + recLen);
					// LogsUtil.d(TAG, ArrayUtils.bytes2HexString(recBuf, 0,
					// recLen));
					if (null != tempBuf) {
						tempBuf = ArrayUtils.concatArray(tempBuf,
								Arrays.copyOf(recBuf, recLen));
					}
					continue;
				} else if (recBuf[0] != 42 && recBuf[recLen - 1] == 35) {
					// 3. 收到最后一个 不完整的数据包 (非*开头，以#结尾)
					LogsUtil.d(TAG, "最后 不完整: " + recLen);
					// LogsUtil.d(TAG, ArrayUtils.bytes2HexString(recBuf, 0,
					// recLen));
					if (null != tempBuf) {
						tempBuf = ArrayUtils.concatArray(tempBuf,
								Arrays.copyOf(recBuf, recLen));
						recLen = tempBuf.length;
						// 往下执行
					} else {
						continue;
					}
				} else {
					// 4. 一次性收到完整数据包，往下执行
					LogsUtil.d(TAG, "一次性收到完整: " + recLen);
					tempBuf = recBuf;
				}

				String str = new String(tempBuf, 0, recLen, "gbk");
				StaticString.information = str;
				LogsUtil.e(TAG, "RecieverTask " + str.length() + " :" + str);

				int cn = getCommNum();
				if ((cn < 0 && isPassiveReceive)) {// cn < 0 响应所有回复
					if (null != mRecListener) {
						LogsUtil.e(TAG, "RecListener != null");
						mRecListener.onRecieve(tempBuf, recLen);
						mRecListener.onRecieve(str);
					}
					// } else if (null != str && str.endsWith(cn + "#")) {//
					// 响应指定的回复
				} else if (null != str) {// 响应指定的回复
					setCommNum(-1);
					mTimeoutThread.stopTime();// 停止计时
					if (null != mRecListener) {
						LogsUtil.w(TAG, "RecListener != null");
						mRecListener.onRecieve(recBuf, recLen);
						mRecListener.onRecieve(str);
					}
				}
				tempBuf = null;

			} // end while()
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != mTimeoutThread) {
			mTimeoutThread.stopThread();
		}
		if (null != sSocket) {
			close();
		}
		if (null != mRecListener) {
			mRecListener.onDisconnect();
		}
		LogsUtil.e(TAG, "连接断开");
		LogsUtil.w(TAG, "receiveThread finish");
	}
}
