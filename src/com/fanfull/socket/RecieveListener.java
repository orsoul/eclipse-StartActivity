package com.fanfull.socket;
public interface RecieveListener {
		void onConnect(String serverIp,  int serverPort);
		void onRecieve(byte[] data, int len);
		void onRecieve(String recString);
		void onDisconnect();
		void onTimeout();
	}