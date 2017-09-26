package com.fanfull.utils;

public class ClickUtil {
	private static long time;
	/**
	 * @param ctr 在给定 gas 内, 执行 ClickTwiceRunnable.intimeToDo(),
	 * 				超出 gas ,执行 ClickTwiceRunnable.outtimeToDo()
	 * @param gas 两次 点击的时间隔
	 */
	public static void clickTwiceToDo(ClickTwiceRunnable ctr, long gas) {
		if (System.currentTimeMillis() - time < gas) {
			ctr.intimeToDo();
		} else {
			ctr.outtimeToDo();
		}
		time = System.currentTimeMillis();
	}
	/**
	 * @param ctr
	 */
	public static void clickTwiceToDo(ClickTwiceRunnable ctr) {
		clickTwiceToDo(ctr, 2500);
	}
	public interface ClickTwiceRunnable {
		/**
		 * 在时间隔内 执行此方法
		 */
		void intimeToDo();
		/**
		 * 超过时间隔执行 此方法
		 */
		void outtimeToDo();
	}
	
	private static long lastClickTime;
	/**
	 * @param timeGap
	 * @return 判断是否为 快速的双击,
	 */
	public static boolean isFastDoubleClick(long timeGap) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (timeD < timeGap) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
