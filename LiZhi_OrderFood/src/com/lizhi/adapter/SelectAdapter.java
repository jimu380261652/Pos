package com.lizhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pos.lizhiorder.R;

/**
 * 选择项设配器
 * @author ZengPeiWei
 *
 */
public class SelectAdapter extends BaseAdapter {

	private Context context;
    private List<String> items;
    private int pos=0;//当前选择的项
	public SelectAdapter(Context context,List<String> items,int pos) {
		this.context=context;
		this.items=items;
		this.pos=pos;
	}
    public void update(int pos){
    	this.pos=pos;
    	notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			
			viewHolder = new ViewHolder();
			
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_select, null);
			viewHolder.item=(TextView) convertView.findViewById(R.id.tv_select_text);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.item.setText(items.get(position));
		if(position==pos){
			viewHolder.item.setTextColor(context.getResources().getColor(R.color.skyblue));
		}else{
			viewHolder.item.setTextColor(context.getResources().getColor(R.color.black));
		}
		return convertView;
	}
	
	
	private final class ViewHolder {
		public TextView item;
	}
	
	
}
