package com.fanfull.view;

import java.util.Comparator;

public class CircleComparable implements Comparator{
	
	@Override
	public int compare(Object obj1, Object obj2) {
		  CircleChildRect u1 = (CircleChildRect)obj1;
		  CircleChildRect u2 = (CircleChildRect)obj2;
		  if(u1.getLeft()>u2.getLeft()){
		   return 1;
		  }else{
		   return -1;
		  }
	}
}
