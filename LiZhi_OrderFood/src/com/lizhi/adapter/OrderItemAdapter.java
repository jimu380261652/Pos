package com.lizhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.bean.ERP_OrderItem;
import com.pos.lizhiorder.R;

/**
 * @author YangTaoLin 订单详情 商品
 */
public class OrderItemAdapter extends BaseAdapter {
	private Context context;
	private List<ERP_OrderItem> items;
	private ERP_OrderItem orderInfo;
	InventoryAdapterCallback mCallback;
	private boolean isNormal = true;// 是不是普通商品
	boolean isInventory, flag;
	String statueName = "";
	String memo = "";

	public List<ERP_OrderItem> getItems() {
		return items;
	}

 

	public OrderItemAdapter(Context context, List<ERP_OrderItem> items,
			boolean flag) {
		this.context = context;
		this.items = items;
		this.flag = flag;
	}

	public OrderItemAdapter(Context context, List<ERP_OrderItem> items) {
		this.context = context;
		this.items = items;

	}

	public void update(List<ERP_OrderItem> items, ERP_OrderItem orderInfo,
			boolean isNormal, String statueName, String memo) {
		clear();
		this.items = items;
		this.memo = memo;
		this.statueName = statueName;
		this.orderInfo = orderInfo;
		this.isNormal = isNormal;
		notifyDataSetChanged();
	}

	public void update(List<ERP_OrderItem> items) {
		clear();
		this.items = items;
		notifyDataSetChanged();
	}
	public void clear(){
		items.clear();
	}
	public void setCallback(InventoryAdapterCallback callback) {
		this.mCallback = callback;
	}

	public void setIsInventory(boolean isInventory) {
		this.isInventory = isInventory;
	}

	/**
	 * 
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (items.size() < 7) {
			return 7;
		}
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

	private final class ViewHolder {
		public TextView name, size, price, discount, number, total;
		public LinearLayout l1;
		public View line1, line2, line3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_order_detail, null);
			viewHolder = new ViewHolder();
			viewHolder.l1 = (LinearLayout) convertView
					.findViewById(R.id.ll_order_detail);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_pro_name);
			viewHolder.size = (TextView) convertView
					.findViewById(R.id.tv_pro_size);
			viewHolder.price = (TextView) convertView
					.findViewById(R.id.tv_pro_price);
			viewHolder.discount = (TextView) convertView
					.findViewById(R.id.tv_pro_discount);
			viewHolder.number = (TextView) convertView
					.findViewById(R.id.tv_pro_num);
			viewHolder.total = (TextView) convertView
					.findViewById(R.id.tv_pro_total);
			viewHolder.line1 = convertView.findViewById(R.id.line1);
			viewHolder.line2 = convertView.findViewById(R.id.line2);
			viewHolder.line3 = convertView.findViewById(R.id.line3);
			if (flag) {
				viewHolder.line3.setVisibility(View.GONE);
				viewHolder.line2.setVisibility(View.GONE);
				viewHolder.line1.setVisibility(View.GONE);
				viewHolder.size.setVisibility(View.GONE);
				viewHolder.discount.setVisibility(View.GONE);
				viewHolder.number.setVisibility(View.GONE);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position < items.size()) {
			ERP_OrderItem product = items.get(position);
			if (flag) {
				viewHolder.name.setText(memo);
				viewHolder.price.setText(product.getPrice() + "");
				viewHolder.total.setText(statueName);
			} else {
				viewHolder.name.setText(product.getMerchandiseName());
				viewHolder.size.setText(product.getUnitName());
				// viewHolder.discount.setText(product.get)
				viewHolder.number.setText(product.getNums() + "");
				viewHolder.price.setText(product.getPrice() + "");
				viewHolder.total.setText(product.getAmount() + "");
				/*
				 * }else{ if(!flag){
				 * viewHolder.name.setText(orderInfo.getMerchandiseName());
				 * viewHolder.size.setText(product.getUnitName()); //
				 * viewHolder.discount.setText(product.get)
				 * viewHolder.number.setText(product.getNums()+"");
				 * viewHolder.price.setText(product.getPrice()+"");
				 * viewHolder.total.setText(statueName); }else {
				 * viewHolder.name.setText(memo);
				 * viewHolder.price.setText(product.getPrice()+"");
				 * viewHolder.total.setText(statueName);
				 * 
				 * 
				 * }
				 */
			}

			if (position % 2 != 0) {
				viewHolder.l1.setBackgroundColor(context.getResources()
						.getColor(R.color.white_gray));
			} else {
				viewHolder.l1.setBackgroundColor(context.getResources()
						.getColor(R.color.white));
			}
			
		}
		return convertView;
	}

	public interface InventoryAdapterCallback {
		public void onDeleteItem(int position);

		public void onNotify();
	}

}
