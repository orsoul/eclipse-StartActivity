package com.fanfull.socket;

/**
 * 实现了 RecieveListener 接口 的抽象类， 继承此类可避免实现所有的接口方法，而只根据需要重写相应的方法
 * 
 * @author orsoul
 *
 */
public abstract class RecieveListenerAbs implements RecieveListener {

	@Override
	public void onConnect(String serverIp,  int serverPort) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onRecieve(byte[] data, int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecieve(String recString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTimeout() {
		// TODO Auto-generated method stub

	}

}
