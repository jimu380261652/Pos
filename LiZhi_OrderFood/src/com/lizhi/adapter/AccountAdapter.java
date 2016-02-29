package com.lizhi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.bean.ERP_Account;
import com.pos.lizhiorder.R;



public class AccountAdapter extends BaseAdapter {
	private Context context;
	private List<ERP_Account> accounts;
	public AccountAdapter(Context context, List<ERP_Account> accounts){
		this.context=context;
		this.accounts=accounts;
	}
	public void update( List<ERP_Account> accounts){
		this.accounts=accounts;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(accounts.size()<10){
			return 10;
		}
		return accounts.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return accounts.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.account_list, null);
			holder = new ViewHolder();
			holder.accountJourId = (TextView) convertView.findViewById(R.id.account_id);
			holder.createTime = (TextView) convertView.findViewById(R.id.account_time);
			holder.operation = (TextView) convertView.findViewById(R.id.account_type);
			holder.amount = (TextView) convertView.findViewById(R.id.account_amount);
			holder.account_balance = (TextView) convertView.findViewById(R.id.account_balance);
			holder.item=(LinearLayout) convertView.findViewById(R.id.ll_account_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if (position%2!=0) {
			holder.item.setBackgroundColor(context.getResources().getColor(R.color.white_gray));
		}else {
			holder.item.setBackgroundColor(context.getResources().getColor(R.color.white));
		}
		if(position<accounts.size()){
			ERP_Account ea = accounts.get(position);
			holder.accountJourId.setText(ea.getACCOUNT_JOURNAL_ID());
			holder.createTime.setText(ea.getCREATE_TIME());
			holder.operation.setText(ea.getOPERATION());
			holder.amount.setText(ea.getAMOUNT()+"");
			holder.account_balance.setText(ea.getACCOUNT_BALANCE()+"");
		}
		
		return convertView;
		
	}
	
	
	private class ViewHolder{
		TextView account_balance,amount,createTime,operation,accountJourId;
		LinearLayout item;
	}

}
