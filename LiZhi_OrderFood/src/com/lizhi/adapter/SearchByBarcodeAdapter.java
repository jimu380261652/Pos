package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.bean.Merchandise;
import com.pos.lizhiorder.R;

public class SearchByBarcodeAdapter extends BaseAdapter {

	private int totlenum = 0;
	private Merchandise retail;
	private Context context;
	private List<Merchandise> details = new ArrayList<Merchandise>();

	public List<Merchandise> getDetails() {
		return details;
	}

	public void setDetails(List<Merchandise> details) {
		this.details = details;
	}

	/*
	 * public void setCallback(RetailAdapterCallback callback) { this.callback =
	 * callback; }
	 */

	public SearchByBarcodeAdapter(Context context, List<Merchandise> details) {
		this.context = context;
		this.details = details;

	}

	public void update(List<Merchandise> details) {
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
			/*
			 * if (!flag) { convertView = LayoutInflater.from(context).inflate(
			 * R.layout.adapter_retail_list, null); holder.id = (TextView)
			 * convertView.findViewById(R.id.tv_pro_id); holder.size =
			 * (TextView) convertView .findViewById(R.id.tv_pro_size);
			 * holder.discount = (TextView) convertView
			 * .findViewById(R.id.tv_pro_discount); holder.total = (TextView)
			 * convertView .findViewById(R.id.tv_pro_total); holder.delete =
			 * (TextView) convertView .findViewById(R.id.tv_pro_del);
			 * 
			 * holder.decrese = (ImageView) convertView
			 * .findViewById(R.id.iv_pro_decrease); holder.increase =
			 * (ImageView) convertView .findViewById(R.id.iv_pro_increase); if
			 * (details.size() <= position) {
			 * holder.decrese.setVisibility(View.GONE);
			 * holder.increase.setVisibility(View.GONE); }
			 * 
			 * holder.number = (TextView) convertView
			 * .findViewById(R.id.tv_pro_number);
			 * 
			 * convertView.setTag(holder);
			 * 
			 * } else {
			 */

			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_retail_list1, null);
			holder.barCode = (TextView) convertView
					.findViewById(R.id.tv_pro_barCode);
			holder.buy = (TextView) convertView.findViewById(R.id.tv_pro_buy);
			// tv_pro_buy

			holder.item = (LinearLayout) convertView
					.findViewById(R.id.ll_retail_item);

			holder.name = (TextView) convertView.findViewById(R.id.tv_pro_name);

			holder.price = (TextView) convertView
					.findViewById(R.id.tv_pro_price);

			holder.stock = (TextView) convertView
					.findViewById(R.id.tv_pro_stock);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position < details.size()) {
			retail = (Merchandise) details.get(position);

			/*
			 * if (!flag) { final int Invertory =
			 * Integer.parseInt(retail.getInvertory()); final double price = new
			 * BigDecimal(retail.getPrice()) .setScale(2,
			 * BigDecimal.ROUND_HALF_UP).doubleValue();
			 * holder.id.setText((position + 1) + "");
			 * holder.name.setText(retail.getMerchandiseName());
			 * holder.price.setText(price + ""); holder.size.setText("");// 规格
			 * holder.number.setText(retail.getNum() + "");// 数量默认为1
			 * holder.delete.setText("删除"); holder.delete.setOnClickListener(new
			 * OnClickListener() {
			 * 
			 * @Override public void onClick(View v) {
			 * 
			 * details.remove(temp); RetailAdapter.this.update(details);
			 * 
			 * callback.delete(Integer.parseInt(holder.id.getText() .toString())
			 * - 1, details); } });
			 * 
			 * holder.decrese = (TextView) convertView
			 * .findViewById(R.id.iv_pro_decrease); holder.increase = (TextView)
			 * convertView .findViewById(R.id.iv_pro_increase);
			 * 
			 * 
			 * holder.decrese.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { int num =
			 * Integer.parseInt(holder.number.getText().toString());
			 * //holder.number.getText(); if (num > 1) { num--;
			 * holder.number.setText(num + ""); double f1 = new BigDecimal(price
			 * * retail.getNum()) .setScale(2, BigDecimal.ROUND_HALF_UP)
			 * .doubleValue(); DecimalFormat df = new DecimalFormat("#.##");
			 * holder.total.setText(df.format(price* num));
			 * 
			 * callback.addnum(position, false, details); } } });
			 * holder.increase.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { //int num =
			 * retail.getNum(); int num =
			 * Integer.parseInt(holder.number.getText().toString()); if
			 * (Integer.parseInt(holder.stock.getText().toString())>0) { num++;
			 * holder.number.setText(num + ""); double f1 = new BigDecimal(price
			 * * retail.getNum()) .setScale(2, BigDecimal.ROUND_HALF_UP)
			 * .doubleValue(); DecimalFormat df = new DecimalFormat("#.##");
			 * holder.total.setText(df.format(price* num));
			 * callback.addnum(position, true, details); }
			 * 
			 * } });
			 * holder.stock.setText(Integer.parseInt(retail.getInvertory())
			 * -1+""); DecimalFormat df = new DecimalFormat("#.##");
			 * holder.total.setText(df.format(price * retail.getNum()));
			 * this.setTotlenum(totlenum + retail.getNum()); } else {
			 */
			if (retail.getInvertory().equals("0")
					|| retail.getClose().equals("0"))
				holder.buy.setText("不可购买");
			holder.stock.setText(retail.getInvertory());
			holder.name.setText(retail.getMerchandiseName());
			holder.price.setText(retail.getPrice());
			holder.barCode.setText(retail.getBarCode());

		}
		if (position % 2 != 0) {
			holder.item.setBackgroundColor(context.getResources().getColor(
					R.color.white_gray));
		} else {
			holder.item.setBackgroundColor(context.getResources().getColor(
					R.color.white));
		}
		return convertView;
	}

	private class ViewHolder {
		TextView barCode, id, name, size, price, discount, stock, delete,
				total, number, buy;
		LinearLayout item;

	}

	public int getTotlenum() {
		return totlenum;
	}

	public void setTotlenum(int totlenum) {
		this.totlenum = totlenum;
	}

	/*
	 * public interface RetailAdapterCallback {
	 * 
	 * public void addnum(int position, boolean flag, List<Merchandise> mers);
	 * 
	 * public void delete(int position, List<Merchandise> mers); }
	 */

}
