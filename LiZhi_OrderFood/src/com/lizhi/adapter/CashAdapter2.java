package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lizhi.bean.Good;
import com.pos.lizhiorder.R;
 

public class CashAdapter2 extends BaseAdapter {
	private Context context;
	private List<Good> list = new ArrayList<Good>();
	private CallBack callback;
	public void setCallback(CallBack callback) {
		this.callback = callback;
	}

	public CashAdapter2(Context context, List<Good> list) {
		this.context = context;
		this.list = list;
	}

	public void update(List<Good> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_cash2, null);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.size = (TextView) convertView.findViewById(R.id.tv_size);
			holder.minus = (ImageView) convertView.findViewById(R.id.iv_minus);
			holder.add = (ImageView) convertView.findViewById(R.id.iv_add);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position) != null) {
			 
			holder.name.setText((position + 1) + "."
					+ list.get(position).getName());
			holder.money.setText("¥" + list.get(position).getPrice() + "x"
					+ list.get(position).getNum());
			holder.size.setText(list.get(position).getSize());
			holder.add.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 int count=list.get(position).getNum();
					 count++;
					 callback.setnum(position, count);
						
				}
			});
			holder.minus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int count=list.get(position).getNum();
					if (count > 1) {
						count--; 
						callback.setnum(position, count);
				 

					}else{
						callback.delete(position);
					}
				}
			});
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView name, money, size;
		private ImageView minus, add;
	}
	public interface CallBack{
		public void setnum(int position,int count);
		//public void minus()
		public void delete(int position);
	}
}
