package com.fanfull.view;

/**
 * 
 * @ClassName: CirlrChildRect
 * @Description: 旋转菜单子视图
 * @author Keung
 * @date 2014-12-29 下午02:03:20
 * 
 */
public class CircleChildRect {

	private int id;
	private int left;
	private int top;
	private int right;
	private int bottom;

	public CircleChildRect() {
		
	}

    
	public CircleChildRect(int id, int left, int top, int right, int bottom) {
		super();
		this.id = id;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}


	@Override
	public String toString() {
		return "CircleChildRect [id=" + id + ", left=" + left + ", top=" + top
				+ ", right=" + right + ", bottom=" + bottom + "]";
	}
	
}
