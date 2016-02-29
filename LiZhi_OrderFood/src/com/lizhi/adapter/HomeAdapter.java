package com.lizhi.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class HomeAdapter extends BaseAdapter {
	private Context context;
	private int[] urls;
	private String[] names;
	public HomeAdapter(Context context,int[] urls,String[] names) {
		this.context = context;
		this.urls=urls;
		this.names=names;
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
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.adapter_home, null);	
		ImageView image=(ImageView) view.findViewById(R.id.image);
		TextView text=(TextView)view.findViewById(R.id.tv_home_name);
		image.setImageResource(urls[position]);
		text.setText(names[position]);
		return view;
	}
	
	

}
