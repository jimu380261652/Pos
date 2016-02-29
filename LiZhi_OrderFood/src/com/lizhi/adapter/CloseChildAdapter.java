
package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lizhi.bean.Good;
import com.pos.lizhiorder.R;

public class CloseChildAdapter extends BaseAdapter {
	private Context context;
	private List<Good> list = new ArrayList<Good>();

	public CloseChildAdapter(Context context, List<Good> list) {
		this.context = context;
		this.list = list;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_childclose, null);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.stock = (TextView) convertView.findViewById(R.id.tv_stock);
			holder.btn = (ToggleButton) convertView.findViewById(R.id.tb_close);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(list.get(position).getName());
		holder.stock.setText("2500");
		holder.btn
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						holder.btn.setChecked(isChecked);
						if (isChecked) {
							Resources resource = context.getResources();
							ColorStateList csl = (ColorStateList) resource
									.getColorStateList(R.color.black);
							holder.stock.setTextColor(csl);
							
						} else {
							Resources resource = context.getResources();
							ColorStateList csl = (ColorStateList) resource
									.getColorStateList(R.color.dark_gray);
							holder.stock.setTextColor(csl);
						}
					}
				});

		return convertView;
	}

	private class ViewHolder {
		TextView name, stock;
		ToggleButton btn;
	}

}
