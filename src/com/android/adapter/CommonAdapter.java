package com.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 定义抽象公用适配器类
 * @author zyp
 * @date 05-16-2016
 *
 * @param <T> 泛型
 */
public abstract class CommonAdapter<T> extends BaseAdapter  {  
    protected LayoutInflater mInflater;  
    protected Context mContext;  
    protected List<T> mDatas;  
    protected final int mItemLayoutId;  
	private int pos = 0;
  
    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId)  {  
        this.mContext = context;  
        this.mInflater = LayoutInflater.from(mContext);  
        this.mDatas = mDatas;  
        this.mItemLayoutId = itemLayoutId;  
    }  
  
    @Override  
    public int getCount() {  
        return mDatas == null ? 0:mDatas.size();  
    }  
  
    @Override  
    public T getItem(int position){  
        return mDatas.get(position);  
    }  
  
    @Override  
    public long getItemId(int position)  {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent)   {  
        final ViewHolder viewHolder = getViewHolder(position, convertView,  
                parent);  
        
        //提供选中和非选中的效果处理
        convert(viewHolder, getItem(position),pos == position); 
        return viewHolder.getConvertView();  
  
    }  
  
    public abstract void convert(ViewHolder helper, T item,boolean isSelect);  
  
    private ViewHolder getViewHolder(int position, View convertView,  
            ViewGroup parent)  {  
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,  
                position);  
    }  
    /**
     * 提供选中行
     * @param pos
     */
	public void setSection (int pos){
		   this.pos = pos;
	}
}  

