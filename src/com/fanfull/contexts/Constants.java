package com.fanfull.contexts;

public class Constants {
	public static enum OP_TYPE {
		// 利用构造函数传参
		/** 21 */
		COVER_BAG("21"), /** 22 */
		IN_STORE("22"), /** 23 */
		OUT_STORE("23"), /** 24 */
		CHECK_COVER("24"), /** 25 */
		CHANGE_BAG("25"), /** 26 */
		OPEN_BAG("26"), /** 40 */
		DEL_BAG("40");

		// 定义私有变量
		private String nCode;

		// 构造函数，枚举类型只能为私有
		private OP_TYPE(String _nCode) {

			this.nCode = _nCode;

		}

		@Override
		public String toString() {

			return String.valueOf(this.nCode);

		}
	}
}
