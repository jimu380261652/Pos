package com.lizhi.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lizhi.bean.MessageList;
import com.pos.lizhiorder.R;
//系统通知
public class SystemNotifyAdapter extends BaseListAdapter<MessageList>{
	private Context context;
	public SystemNotifyAdapter(Context context){
		this.context=context;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.right_message_list, null);
			holder = new ViewHolder();
			holder.date=(TextView)convertView.findViewById(R.id.tv_message2_date);
			holder.body=(TextView)convertView.findViewById(R.id.tv_message2_body);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		MessageList ml=(MessageList)data.get(position);
		holder.date.setText(ml.getDate());
		holder.body.setText(ml.getBody());
		return convertView;
	}
	private class ViewHolder{
		TextView date,body;
		
	}
}
