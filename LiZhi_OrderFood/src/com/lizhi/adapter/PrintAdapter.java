package com.lizhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class PrintAdapter extends BaseListAdapter<String> {
	Context context;
	public PrintAdapter(Context context) {
		this.context=context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.orderid_item, null);
			holder = new ViewHolder();
			holder.vOrder = (TextView) convertView.findViewById(R.id.order);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		String order = (String) data.get(position);
		holder.vOrder.setText(order);
		return convertView;
	}
	private class ViewHolder{
		TextView vOrder;
	}
}
