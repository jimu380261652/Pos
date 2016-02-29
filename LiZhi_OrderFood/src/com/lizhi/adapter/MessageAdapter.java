package com.lizhi.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lizhi.bean.ERP_Message;
import com.pos.lizhiorder.R;
//消息
public class MessageAdapter extends BaseListAdapter<ERP_Message>{
	private Context context;
	private int pos=0;
	public MessageAdapter(Context context,int pos){
		this.context=context;
		this.pos=pos;
	}
	public void update(int pos){
		this.pos=pos;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.left_message_list, null);
			holder = new ViewHolder();
			holder.itemName=(TextView)convertView.findViewById(R.id.tv_left_name);
			holder.time=(TextView)convertView.findViewById(R.id.tv_left_time);
			holder.redPoint=(ImageView)convertView.findViewById(R.id.iv_left_red);
			holder.blueLine=(ImageView)convertView.findViewById(R.id.iv_left_blue);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ERP_Message ll=(ERP_Message) data.get(position);
		if(pos==position){
			holder.blueLine.setVisibility(View.VISIBLE);
		}else{
			holder.blueLine.setVisibility(View.GONE);
		}
//		holder.itemName.setText(ll.getItemname());
//		holder.time.setText(ll.getTime());
		return convertView;
	}
	private class ViewHolder{
		TextView itemName,time;
		ImageView redPoint,blueLine;
	}
}
