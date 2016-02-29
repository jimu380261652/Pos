package com.lizhi.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lizhi.bean.ERP_Order;
import com.lizhi.util.DateUtil;
import com.pos.lizhiorder.R;


/**
 * @author YangTaoLin
 *  订单
 */
public class OrderAdapter extends BaseAdapter {
	private Context context;
	private List<ERP_Order> orders;
	private int pos;
	public OrderAdapter(Context context,List<ERP_Order> orders,int pos){
		this.context=context;
		this.pos=pos;
		this.orders=orders;
	}
	public void update(List<ERP_Order> orders,int pos){
		this.pos=pos;
		this.orders=orders;
		notifyDataSetChanged();
	}
	public void update(int pos){
		this.pos=pos;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orders.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orders.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	/**
	 * 
	 */
	private  class ViewHolder {
		 TextView id,member,price,time;
		 RelativeLayout ll;
		 View line;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_order, null);
			viewHolder = new ViewHolder();
			viewHolder.ll=(RelativeLayout) convertView.findViewById(R.id.rl_order);
			viewHolder.line=convertView.findViewById(R.id.tv_line);
			viewHolder.id=(TextView) convertView.findViewById(R.id.tv_order_id);
			viewHolder.price=(TextView) convertView.findViewById(R.id.tv_order_price);
			viewHolder.member=(TextView) convertView.findViewById(R.id.tv_order_member);
			viewHolder.time=(TextView) convertView.findViewById(R.id.tv_order_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ERP_Order order = (ERP_Order) orders.get(position);
		viewHolder.id.setText("订单:"+order.getOrderId());
		viewHolder.price.setText("￥"+order.getTotalAmount()+"");
		viewHolder.member.setText("会员:"+order.getUserName());
		viewHolder.time.setText(DateUtil.getTimeString(order.getOrderDate()));
		if(pos==position){
			viewHolder.ll.setBackgroundColor(context.getResources().getColor(R.color.white));
			viewHolder.line.setVisibility(View.VISIBLE);
		}else{
			viewHolder.ll.setBackgroundColor(context.getResources().getColor(R.color.white_gray));
			viewHolder.line.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	
}
