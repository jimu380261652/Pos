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

import com.lizhi.bean.Merchandise;
import com.pos.lizhiorder.R;


/**
 * @author YangTaoLin
 *  商品管理 库存
 */
public class StockAdapter extends BaseAdapter {
	private Context context;
	private List<Merchandise> commoditys;
	InventoryAdapterCallback mCallback;
	boolean isInventory;
 
	public StockAdapter(Context context,List<Merchandise> commoditys ){
		this.context=context;
		this.commoditys=commoditys;
	 
	}
	public void update(List<Merchandise> commoditys ){
 
		this.commoditys=commoditys;
		notifyDataSetChanged();
	}
	/*public void update(int pos){
		this.pos=pos;
		notifyDataSetChanged();
	}*/
	public void setCallback(InventoryAdapterCallback callback){
		this.mCallback = callback;
	}
	public void setIsInventory(boolean isInventory) {
		this.isInventory=isInventory;
	}
	@Override
	public int getCount() {
		
		if(commoditys.size()<10){
			return 10;
		}
		return commoditys.size();
	}
	@Override
	public Object getItem(int position) {
		
		return commoditys.get(position);
	}
	@Override
	public long getItemId(int position) {
		
		return position;
	}
	/**
	 * 
	 */
	private final class ViewHolder {
		public TextView id,code,name,size,status,priceIn,salePrice,stock;
		public LinearLayout l1;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_inventory, null);
			viewHolder = new ViewHolder();
			viewHolder.l1=(LinearLayout)convertView.findViewById(R.id.ll_stock);
			viewHolder.id=(TextView) convertView.findViewById(R.id.tv_stock_id);
			viewHolder.code=(TextView) convertView.findViewById(R.id.tv_stock_code);
			viewHolder.name=(TextView) convertView.findViewById(R.id.tv_stock_name);
			viewHolder.size=(TextView) convertView.findViewById(R.id.tv_stock_size);
			viewHolder.status=(TextView) convertView.findViewById(R.id.tv_stock_status);
			viewHolder.priceIn=(TextView) convertView.findViewById(R.id.tv_stock_priceIn);
			viewHolder.salePrice=(TextView) convertView.findViewById(R.id.tv_stock_saleprice);
			viewHolder.stock=(TextView) convertView.findViewById(R.id.tv_stock_stock);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(position<commoditys.size()){
			Merchandise barcode = (Merchandise) commoditys.get(position);
			viewHolder.id.setText(barcode.getBarCode());
			viewHolder.code.setText(barcode.getMerchandiseId());
			viewHolder.name.setText(barcode.getMerchandiseName());
			viewHolder.priceIn.setText("");
			viewHolder.size.setText("");
			DecimalFormat df = new DecimalFormat("#.##");	
			if(barcode.getPrice().length()>0)
			viewHolder.salePrice.setText(df.format(Double.parseDouble(barcode.getPrice()))+"元");
			else
				viewHolder.salePrice.setText(barcode.getPrice());
			if(barcode.getClose().equals("1"))
				viewHolder.status.setText("上架");
			else
				viewHolder.status.setText("下架");
			viewHolder.stock.setText(barcode.getInvertory()+"");
		}
		
//		viewHolder.price.setText(BigDecimal.valueOf(barcode.getSalesPrice()).setScale(2,RoundingMode.HALF_EVEN).toString());
		if (position%2!=0) {
			viewHolder.l1.setBackgroundColor(context.getResources().getColor(R.color.white_gray));
		}else {
			viewHolder.l1.setBackgroundColor(context.getResources().getColor(R.color.white));
		}
		return convertView;
	}
	/*@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mCallback.onNotify();
	}*/
	public interface InventoryAdapterCallback{
		public void onDeleteItem(int position);
		//public void onNotify();
	}
	
	
}
