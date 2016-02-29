package com.lizhi.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.bean.TemporarilyOrder;
import com.pos.lizhiorder.R;

public class QudangAdapter extends BaseAdapter {
	private Context context;
	private List<TemporarilyOrder> temps;
	public QudangAdapter(Context context,List<TemporarilyOrder> temps) {
		this.context = context;
		this.temps=temps;
	}

	private class ViewHolder {
		TextView time,name,num,total;
		LinearLayout item;
	}

	@Override
	public int getCount() {

		return  temps.size();
	}

	@Override
	public Object getItem(int position) {

		return  temps.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_qudang, null);
			holder.item = (LinearLayout) convertView
					.findViewById(R.id.item);

			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.num = (TextView) convertView.findViewById(R.id.tv_num);
			holder.total = (TextView) convertView.findViewById(R.id.tv_total);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//System.out.println("适配器"+Constants.temps.get(position).getMers().size());
		holder.time.setText( temps.get(position).getDate());
		int num=0;
		double total=0;
		for(int i=0;i<temps.get(position).getMers().size();i++){
			total+=temps.get(position).getMers().get(i).getNum()*Double.parseDouble(temps.get(position).getMers().get(i).getPrice());
			num+=temps.get(position).getMers().get(i).getNum();
		}
		holder.num.setText(""+num);
		DecimalFormat df = new DecimalFormat("#.##");
		holder.total.setText(df.format(total));
		if (position % 2 != 0) {
			holder.item.setBackgroundColor(context.getResources().getColor(
					R.color.white_gray));
		} else {
			holder.item.setBackgroundColor(context.getResources().getColor(
					R.color.white));
		}
		return convertView;
	}
}
