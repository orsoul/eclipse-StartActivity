package com.fanfull.spi;

public class SPIpoint {
	private int xPoint = 0;
    private int yPoint = 0;
	public void setPoint(int xPoint ,int yPoint) {
		this.xPoint = xPoint;
		this.yPoint = yPoint;
	}
	public int getxPoint() {
		return xPoint;
	}
	public int getyPoint() {
		return yPoint;
	}
}
