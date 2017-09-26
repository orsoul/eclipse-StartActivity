package com.fanfull.spi;

import java.util.ArrayList;
import java.util.List;


public class SPIgetPoint {
	private final String TAG = "SPIgetPoint";
	private final int textWitdh = 24;
	private final int textHeigth = 24;
	private final int screenWitdh = 160;
	private final int screenHeigth = 128;
	private final int partformGap = 4;//行行之间的间隙
    private	List<SPIpoint>mPoints = new ArrayList<SPIpoint>();
    public  List<SPIpoint>  getSpIpoint(int num) {
    	int m=0,n=0;
    	switch (num) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			SPIpoint spIpoint0 = new SPIpoint();
			spIpoint0.setPoint((int)(0.5*(screenWitdh-(num*textWitdh))),(int)(0.5*(screenHeigth-(1*textHeigth))));//1表示一行，高度相同
			//Log.d("TAG", spIpoint0.getxPoint()+"-----"+spIpoint0.getyPoint());
			mPoints.add(spIpoint0);
			break;
		case 7://表示把7个字分拆成上三下四
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
			if(num%2 == 0){
				m = n = num/2;
			}else {
				m = n =6;//奇数的时候，考虑到不对齐影响美观，所以水平还是都是从开头开始摆放
			}
			SPIpoint spIpoint1 = new SPIpoint();
			spIpoint1.setPoint((int) (0.5*(screenWitdh-(m*textWitdh))),(int) (0.5*(screenHeigth-(2*textHeigth))));//1表示一行，高度相同
			mPoints.add(spIpoint1);
			SPIpoint spIpoint2 = new SPIpoint();
			spIpoint2.setPoint((int) (0.5*(screenWitdh-(n*textWitdh))),textHeigth+partformGap+(int) (0.5*(screenHeigth-(2*textHeigth))));//2表示一行，高度相同
			mPoints.add(spIpoint2);
			break;
		case 13://表示把7个字分拆成上三下四
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
			m = n =6;//三行都是从开头开始摆放
			SPIpoint spIpoint3 = new SPIpoint();
			int tmpHeigth = (int) (0.5*(screenHeigth-(3*textHeigth)-2*partformGap));
			spIpoint3.setPoint((int) (0.5*(screenWitdh-(m*textWitdh))),tmpHeigth);//1表示一行，高度相同
			mPoints.add(spIpoint3);
			SPIpoint spIpoint4 = new SPIpoint();
			spIpoint4.setPoint((int) (0.5*(screenWitdh-(n*textWitdh))),textHeigth+partformGap+tmpHeigth);//2表示一行，高度相同
			mPoints.add(spIpoint4);
			SPIpoint spIpoint5 = new SPIpoint();
			spIpoint5.setPoint((int) (0.5*(screenWitdh-(n*textWitdh))),2*(textHeigth+partformGap)+tmpHeigth);//2表示一行，高度相同
			mPoints.add(spIpoint5);
			break;
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
			m = n =6;//三行都是从开头开始摆放
			SPIpoint spIpoint6 = new SPIpoint();
			int tmpHeigth1 = (int) (0.5*(screenHeigth-(4*textHeigth)-3*partformGap));
			spIpoint6.setPoint((int) (0.5*(screenWitdh-(m*textWitdh))),tmpHeigth1);//1表示一行，高度相同
			mPoints.add(spIpoint6);
			SPIpoint spIpoint7 = new SPIpoint();
			spIpoint7.setPoint((int) (0.5*(screenWitdh-(n*textWitdh))),textHeigth+partformGap+tmpHeigth1);//2表示一行，高度相同
			mPoints.add(spIpoint7);
			SPIpoint spIpoint8 = new SPIpoint();
			spIpoint8.setPoint((int) (0.5*(screenWitdh-(n*textWitdh))),2*(textHeigth+partformGap)+tmpHeigth1);//2表示一行，高度相同
			mPoints.add(spIpoint8);
			SPIpoint spIpoint9 = new SPIpoint();
			spIpoint9.setPoint((int) (0.5*(screenWitdh-(n*textWitdh))),3*(textHeigth+partformGap)+tmpHeigth1);//2表示一行，高度相同
			mPoints.add(spIpoint9);
			break;
		default:
			break;
		}
		return mPoints ;
    	 
     }
}
