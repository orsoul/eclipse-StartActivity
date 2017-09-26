package com.mvp.baglink;

import java.util.ArrayList;
import java.util.List;

import com.entity.PileInfo;
import com.fanfull.fff.R;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PileListAdapter extends BaseAdapter{

	private List<PileInfo> mList;
	/**
	 * 当前用户选择的堆
	 */
	private int clickPosition = -1;
	
	/**
	 * 用户当前点击的视图
	 */
	private ViewHolder mHolder;
	
	public PileListAdapter(List<PileInfo> list){
		List<PileInfo> list1 = new ArrayList<PileInfo>();
		for(PileInfo info :list){
			if(info.getBagNum() == 0){
				list1.add(info);
			}
		}
		mList = list1;
	}
	
	public int getClickPosition(){
		return clickPosition;
	}
	
	@Override
	public int getCount() {
		return mList == null? 0:mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pile_list, parent, false);
			viewHolder.pileID = (TextView)convertView.findViewById(R.id.pileID);
			viewHolder.totalNum = (TextView) convertView.findViewById(R.id.totalNum);
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
			viewHolder.layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					viewHolder.layout.setBackgroundColor(Color.RED);
					if(clickPosition != position){
						System.out.println("点击位置："+position);
						clickPosition = position;
						if(mHolder!=null){
							mHolder.layout.setBackgroundColor(Color.BLACK);	
							mHolder.isClick = false;
						}
						mHolder = viewHolder;
					}
				}
			});
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		PileInfo pileInfo = mList.get(position);
		viewHolder.pileID.setText(pileInfo.getPileName());
		viewHolder.totalNum.setText(pileInfo.getTotalAmount());
		if(clickPosition != position){
			viewHolder.layout.setBackgroundColor(Color.BLACK);
		}
		return convertView;
	}
	
	class ViewHolder{
		private boolean isClick = false;
		private TextView pileID, totalNum;
		private RelativeLayout layout;
	}

}
