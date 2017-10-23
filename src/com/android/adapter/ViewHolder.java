package com.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
  
public class ViewHolder{  
    private final SparseArray<View> mViews;  
    private int mPosition;  
    private View mConvertView;  
    
    private ViewHolder(Context context, ViewGroup parent, int layoutId,  
            int position)   {  
        this.mPosition = position;  
        this.mViews = new SparseArray<View>();  
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,  
                false);  
        // setTag  
        mConvertView.setTag(this);  
    }  
  
    /** 
     * 拿到一个ViewHolder对象 
     *  
     * @param context 
     * @param convertView 
     * @param parent 
     * @param layoutId 
     * @param position 
     * @return 
     */  
    public static ViewHolder get(Context context, View convertView,  
            ViewGroup parent, int layoutId, int position) {  
        if (convertView == null)  {  
            return new ViewHolder(context, parent, layoutId, position);  
        }  
        return (ViewHolder) convertView.getTag();  
    }  
  
    public View getConvertView()  {  
        return mConvertView;  
    }  
  
    /** 
     * 通过控件的Id获取对于的控件，如果没有则加入views 
     *  
     * @param viewId 
     * @return 
     */  
    public <T extends View> T getView(int viewId)  {  
        View view = mViews.get(viewId);  
        if (view == null)  
        {  
            view = mConvertView.findViewById(viewId);  
            mViews.put(viewId, view);  
        }  
        return (T) view;  
    }  
  
    /** 
     * 为TextView设置字符串 
     *  
     * @param viewId 
     * @param text 
     * @return 
     */  
    public ViewHolder setText(int viewId, String text)    {  
        TextView view = getView(viewId);  
        view.setText(text);  
        return this;  
    }  
    
    /** 
     * 为TextView设置字符颜色
     *  
     * @param viewId 
     * @param text 
     * @return 
     */  
    public ViewHolder setTextColor(int viewId, int color)    {  
        TextView view = getView(viewId);  
        view.setTextColor(color);
        return this;  
    }  
    
    /** 
     * 为TextView设置字符颜色
     *  
     * @param viewId 
     * @param text 
     * @return 
     */  
    public ViewHolder setTextSize(int viewId, float size)    {  
        TextView view = getView(viewId);  
        view.setTextSize(size);
        return this;  
    } 
    
  
    /** 
     * 为ImageView设置图片 
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public ViewHolder setImageResource(int viewId, int drawableId)  {  
        ImageView view = getView(viewId);  
        view.setImageResource(drawableId);  
  
        return this;  
    }  
  
    /** 
     * 为ImageView设置图片 
     *  
     * @param viewId 
     * @param drawableId 
     * @return 
     */  
    public ViewHolder setImageBitmap(int viewId, Bitmap bm)  {  
        ImageView view = getView(viewId);  
        view.setImageBitmap(bm);  
        return this;  
    }  
  
//    /** 
//     * 为ImageView设置图片   通过Volley框架加载网络图片
//     *  
//     * @param viewId 
//     * @param drawableId 
//     * @return 
//     */  
//    public ViewHolder setImageByUrl(int viewId, String url)  {  
//        NetworkImageView networkImageView = getView(viewId);  
//      //创建一个RequestQueue对象
//    	RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//    	//创建一个ImageLoader
//    	ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageCache() {
//    		@Override
//    		public void putBitmap(String url, Bitmap bitmap) {
//    		}
//
//    		@Override
//    		public Bitmap getBitmap(String url) {
//    			return null;
//    		}
//    	});
//    	networkImageView.setDefaultImageResId(R.drawable.ic_normal_pic);
//    	networkImageView.setErrorImageResId(R.drawable.ic_normal_pic);
//    	//设置url和ImageLoader对象
//    	networkImageView.setImageUrl(url,imageLoader);
//        return this;  
//    }  
    
    /**
     * 提供当前pos
     * @return pos
     */
    public int getPosition()  {  
        return mPosition;  
    }  
  
}  
