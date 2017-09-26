package com.fanfull.socket;

import com.fanfull.utils.LogsUtil;


/**
 * 计时线程.等待服务端回复超时时，通知应用层
 */
public class TimeoutThread extends Thread {
    private static final String TAG = TimeoutThread.class.getSimpleName();

    /** 计时时间 */
    private static final long TIMEOUT = 5000;

    /** 超时处理 RecieveListener */
    private RecieveListener mRecieveListener;

    /**
     * @return
     */
    public RecieveListener getRecieveListener() {
        return mRecieveListener;
    }

    /**
     * @param recieveListener
     */
    public void setRecieveListener(RecieveListener recieveListener) {
        mRecieveListener = recieveListener;
    }

    /** 终止线程 */
    public void stopThread() {
        this.interrupt();
    }

    /** 开始计时 */
    public void startTime() {
        if (this.getState() == State.WAITING) {
            synchronized (this) {
                this.notify();
            }
        }
    }

    /** 停止计时 */
    public void stopTime() {
        if (this.getState() == State.TIMED_WAITING) {
            synchronized (this) {
                this.notify();
            }
        }
    }

    @Override
    public void run() {
        LogsUtil.w(TAG, TimeoutThread.class.getSimpleName() + " run");

        while (true) {
            LogsUtil.w(TAG, TimeoutThread.class.getSimpleName() + " wait -");
            // 1，等待 唤醒
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                // 被中断 退出 线程
                break;
            }

            LogsUtil.w(TAG, TimeoutThread.class.getSimpleName() + " wait " + TIMEOUT);
            // 2，被唤醒 后进行 计时
            long time = System.currentTimeMillis();
            try {
                synchronized (this) {
                    this.wait(TIMEOUT);
                }
                time = System.currentTimeMillis() - time;
            } catch (InterruptedException e) {
                // 被中断 退出 线程
                break;
            }
            LogsUtil.d(time);
            if (TIMEOUT <= time && null != mRecieveListener) {
            	SocketConnet.getInstance().setCommNum(-1);
                // 3，超时
                mRecieveListener.onTimeout();
            }
        }
        LogsUtil.w(TAG, TimeoutThread.class.getSimpleName() + " run finish");
    }
}