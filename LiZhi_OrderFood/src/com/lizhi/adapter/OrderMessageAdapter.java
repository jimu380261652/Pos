package com.lizhi.adapter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lizhi.bean.OrderMessage;
import com.pos.lizhiorder.R;
//消息
public class OrderMessageAdapter extends BaseAdapter{
	private Callback callback;
	private Context context;
	private int pos=0;
	private List<OrderMessage>list=new ArrayList<OrderMessage>();
	public OrderMessageAdapter(Context context,List<OrderMessage>list,int pos,Callback callback){
		this.callback=this.callback;
		this.context=context;
		this.list=list;
		this.pos=pos;
	}
	public void update(List<OrderMessage>list,int pos){
		this.pos=pos;
		this.list=list;
		notifyDataSetChanged();
	}
	public void update(int pos){
		this.pos=pos;
		notifyDataSetChanged();
	}
	public interface Callback{
		public void IsRead(int position);
	}
	
	@Override
	public int getCount() {
		 
		return list.size();
	}
	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		holder holder=null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.order_message_list, null);
			holder = new holder();
			holder.ll=(RelativeLayout) convertView.findViewById(R.id.rl_order);
			holder.tv_order_id=(TextView)convertView.findViewById(R.id.tv_order_id);
			holder.time=(TextView)convertView.findViewById(R.id.tv_order_time);
			holder.redPoint=(ImageView)convertView.findViewById(R.id.iv_left_red);
			holder.tv_line=(View)convertView.findViewById(R.id.tv_line);
			convertView.setTag(holder);
		}else{
			holder = (holder) convertView.getTag();
		}
		OrderMessage ll=(OrderMessage) list.get(position);
		holder.tv_order_id.setText("订单:"+ll.orderId);
		holder.time.setText(ll.getCreateTime());
		
		if(pos==position){
			holder.ll.setBackgroundColor(context.getResources().getColor(R.color.white));
			holder.tv_line.setVisibility(View.VISIBLE);
			if(ll.getReaded().equals("1")){
				callback.IsRead(position);//更改消息read状态
				holder.redPoint.setVisibility(View.GONE);
			}
		}else{
			holder.ll.setBackgroundColor(context.getResources().getColor(R.color.white_gray));
			holder.tv_line.setVisibility(View.GONE);
			if(ll.getReaded().equals("0")){
				holder.redPoint.setVisibility(View.GONE);
			}else {
				holder.redPoint.setVisibility(View.VISIBLE);
				callback.IsRead(position);//更改消息read状态
				
			}
		}
 
		return convertView;
	}
	private class holder{
		View tv_line;
		TextView tv_order_id,time;
		ImageView redPoint;
		RelativeLayout ll;
	}
	
	
}
