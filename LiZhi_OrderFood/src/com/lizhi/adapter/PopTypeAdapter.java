package com.lizhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.bean.Type;
import com.pos.lizhiorder.R;

public class PopTypeAdapter extends BaseAdapter {
 

	private Context context;
 
	private ArrayList<Type>details=new ArrayList<Type>();
	private Callback callback;

 

	public PopTypeAdapter(Context context, ArrayList<Type> details,Callback callback) {
		this.callback=callback;
		this.context = context;
		this.details = details;

	}

	public void update(ArrayList<Type> details) {
		this.details = details;

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return details.size();
	}

	@Override
	public Object getItem(int position) {

		return details.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_pop_type, null);
			holder.type_ll=(LinearLayout) convertView.findViewById(R.id.type_ll);
			holder.name=(TextView) convertView.findViewById(R.id.tv_type_name);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.type_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 callback.set(details.get(position));
				
			}
		});
		holder.name.setText(details.get(position).getName());
		return convertView;
	}

	private class ViewHolder {
		TextView name;
		LinearLayout type_ll;
	}

	public interface Callback{
		public void set(Type type);
	}

}
