package com.mvp.pileCreate1;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.fanfull.fff.R;

public class ContentAdapter extends BaseAdapter implements OnClickListener{
	private List<String> mList;
	private LayoutInflater mInflater;
	private CallBack mCallBack;
	public interface CallBack{
		public void click(View v);
	}
	public  ContentAdapter(Context context,List<String> list,CallBack callBack){
		mList = list;
		mInflater = LayoutInflater.from(context);
		mCallBack = callBack;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public android.view.View getView(int position,
			android.view.View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_one_list, null);
			holder = new ViewHolder();
			holder.button = (Button) convertView.findViewById(R.id.item_option_createpile1);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.button.setText(mList.get(position));
		holder.button.setOnClickListener(this);
		holder.button.setTag(position);
		return convertView;
	}

	public class ViewHolder{
		public Button button;
	}
	
	@Override
	public void onClick(android.view.View v) {
		// TODO Auto-generated method stub
		mCallBack.click(v);
	}
	public void clear() {
		mList.clear();
	    notifyDataSetChanged();
	}
}
