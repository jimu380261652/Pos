package com.lizhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class PicAdapter extends BaseAdapter {
	private Context context;
	private int[] urls;
	private String[] names;
	public PicAdapter(Context context,String[] names,int[] urls){
		this.context=context;
		this.names=names;
		this.urls=urls;
	}
	public void update(String[] names,int[] urls){
		this.names=names;
		this.urls=urls;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return urls.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return urls[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater=LayoutInflater.from(context);
			convertView=inflater.inflate(R.layout.adapter_pic, null);	
			holder.image=(ImageView) convertView.findViewById(R.id.image);
			holder.text=(TextView)convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setImageResource(urls[position]);
		holder.text.setText(names[position]);
		return convertView;
	}
	private class ViewHolder {
		TextView text;
		ImageView image;
 
	}
}