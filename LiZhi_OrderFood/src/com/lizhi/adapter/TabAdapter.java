package com.lizhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.pos.lizhiorder.R;

public class TabAdapter extends BaseAdapter {
	private Context context;
	private List<String> items;
	private int pos=0;
	public TabAdapter(Context context,List<String> items) {
		this.context = context;
		this.items=items;
	}
    public void update(int pos){
    	this.pos=pos;
    	notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Cache cache=null;
		if(view==null){
			LayoutInflater inflater=LayoutInflater.from(context);
			view=inflater.inflate(R.layout.adapter_tab, null);	
			cache=new Cache();
			cache.content=(TextView) view.findViewById(R.id.tv_item);
			cache.line=view.findViewById(R.id.line);
			view.setTag(cache);
		}else{
			cache=(Cache) view.getTag();
		}
		cache.content.setText(items.get(position));
		if(position==pos){
			cache.content.setTextColor(context.getResources().getColor(R.color.red));
			cache.line.setVisibility(View.VISIBLE);
		}else{
			cache.content.setTextColor(context.getResources().getColor(R.color.gray));
			cache.line.setVisibility(View.GONE);
		}
		return view;
	}
	private class Cache{
		private TextView content;
		private View line;
		public Cache(){
			
		}
	}
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
		notifyDataSetChanged();
	}
	

}
