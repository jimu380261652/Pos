package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.bean.Good;
import com.pos.lizhiorder.R;

public class GoodAdapter extends BaseAdapter{
	private Context context;
	private List<Good>list=new ArrayList<Good>();
	private int index;
	public GoodAdapter(Context context,List<Good>list,int index){
		this.index=index;
		this.context=context;
		this.list=list;
	}
	public void update(int index){
		this.index=index;
		notifyDataSetChanged();
	}
	public void update(List<Good>list){
		this.list=list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(list.size()>10)
			return list.size();
		return 10;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_good, null);
			 holder.name=(TextView) convertView.findViewById(R.id.name);
			 holder.price=(TextView) convertView.findViewById(R.id.price);
			 holder.ll=(LinearLayout) convertView.findViewById(R.id.ll_item);
			 convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(list.size()>position){
			holder.name.setText(list.get(position).getName());
			holder.price.setText(list.get(position).getPrice());
			if(index==position){
				holder.ll.setBackgroundColor(context.getResources().getColor(R.color.order_item));
				//holder.line.setVisibility(View.VISIBLE);
			}else{
				holder.ll.setBackgroundColor(context.getResources().getColor(R.color.white));
				//holder.line.setVisibility(View.INVISIBLE);
			}
		}
		return convertView;
	}
	private class ViewHolder {
		TextView name,price;
		LinearLayout ll;
	}
}
