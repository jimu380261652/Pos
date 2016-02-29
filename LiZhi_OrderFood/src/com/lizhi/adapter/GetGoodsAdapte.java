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

import com.lizhi.bean.Temporary;
import com.pos.lizhiorder.R;

public class GetGoodsAdapte extends BaseAdapter {
	private Context context;
	private List<Temporary> list = new ArrayList<Temporary>();
	private CallBack callback;

	public GetGoodsAdapte(Context context, List<Temporary> name,
			CallBack callback) {
		this.context = context;
		this.callback = callback;
		if (name != null)
			this.list = name;
	}

	public void update(List<Temporary> name) {
		list = name;
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
		if (getCount() > 0)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_getgoods, null);
			holder.name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.minus = (ImageView) convertView.findViewById(R.id.iv_minus);
			holder.add = (ImageView) convertView.findViewById(R.id.iv_add);
			holder.num = (TextView) convertView.findViewById(R.id.tv_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position) != null) {
			holder.num.setText(list.get(position).getNum() + "");

			holder.name.setText((position + 1) + "."
					+ list.get(position).getName());
			holder.add.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int num = Integer.parseInt(holder.num.getText().toString());
					num++;
					holder.num.setText(num + "");
					callback.changenum(position, num, list);
				}
			});
			holder.minus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int num = Integer.parseInt(holder.num.getText().toString());
					if (num > 1) {

						num--;

						holder.num.setText(num + "");
						callback.changenum(position, num, list);

					}
				}
			});
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView name, num;
		private ImageView minus, add;
	}

	public interface CallBack {

		public void changenum(int position, int num, List<Temporary> list);

	}

}
