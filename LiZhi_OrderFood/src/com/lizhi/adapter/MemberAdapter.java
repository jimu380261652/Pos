package com.lizhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lizhi.bean.ERP_Member;
import com.pos.lizhiorder.R;


public class MemberAdapter extends BaseAdapter{
	private Context context;
	private List<ERP_Member> members;
	public MemberAdapter(Context context, List<ERP_Member> members){
		this.context=context;
		this.members=members;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.member_list, null);
			holder = new ViewHolder();
			holder.name=(TextView)convertView.findViewById(R.id.tv_member_name);
			holder.phone=(TextView)convertView.findViewById(R.id.tv_member_phone);
			holder.type=(TextView)convertView.findViewById(R.id.tv_member_type);
			holder.time=(TextView)convertView.findViewById(R.id.tv_member_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(position<members.size()){
			ERP_Member em = members.get(position);
			holder.name.setText(em.getMemberId());
			holder.time.setText(em.getCreateTime());
			holder.phone.setText(em.getMobile());
			holder.type.setText(em.getMemberType());
		}
		return convertView;
	}
	private class ViewHolder{
		TextView name,phone,type,time;
	}
	public void update(List<ERP_Member> members){
		this.members=members;
		notifyDataSetChanged();
	}
}
