package com.fanfull.socket;
public interface RecieveListener {
		void onRecieve(byte[] data, int len);
		void onRecieve(String recString);
		void onDisconnect();
		void onTimeout();
	}