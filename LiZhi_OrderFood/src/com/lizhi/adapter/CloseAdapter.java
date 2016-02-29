package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lizhi.bean.GoodsCategory;
import com.lizhi.util.Utility;
import com.pos.lizhiorder.R;

 

public class CloseAdapter extends BaseAdapter {
	private Context context;
	private List<GoodsCategory> list = new ArrayList<GoodsCategory>();

	public CloseAdapter(Context context, List<GoodsCategory> list) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
	 
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_close, null);
			holder.tv_category = (TextView) convertView
					.findViewById(R.id.tv_category);
			holder.iv_show = (ImageView) convertView.findViewById(R.id.iv_show);
			holder.lv_category = (ListView) convertView
					.findViewById(R.id.lv_category);
			holder.line1 = convertView.findViewById(R.id.line1);
			holder.line2 = convertView.findViewById(R.id.line2);
			holder.ll_top = (LinearLayout) convertView
					.findViewById(R.id.ll_top);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.iv_show.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (holder.ll_top.getVisibility() != View.VISIBLE) {
					 
					holder.lv_category.setAdapter(new CloseChildAdapter(
							context, list.get(position).getList()));
					Utility.setListViewHeightBasedOnChildren(holder.lv_category);
					holder.lv_category.setVisibility(View.VISIBLE);
					holder.line1.setVisibility(View.VISIBLE);
					holder.line2.setVisibility(View.VISIBLE);
					holder.ll_top.setVisibility(View.VISIBLE);
				} else {
					holder.lv_category.setVisibility(View.GONE);
					holder.line1.setVisibility(View.GONE);
					holder.line2.setVisibility(View.GONE);
					holder.ll_top.setVisibility(View.GONE);
				}
			}
		});
		holder.tv_category.setText(list.get(position).getName());

		return convertView;
	}

	private class ViewHolder {
		TextView tv_category;
		ImageView iv_show;
		ListView lv_category;
		View line1, line2;
		LinearLayout ll_top;

	}
}
