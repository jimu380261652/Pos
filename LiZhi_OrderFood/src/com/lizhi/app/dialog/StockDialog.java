package com.lizhi.app.dialog;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lizhi.adapter.PopTypeAdapter;
import com.lizhi.bean.Goods;
import com.lizhi.bean.Type;
import com.pos.lizhiorder.R;

//商品入库
public class StockDialog extends BaseDialog implements
		android.view.View.OnClickListener {

	Context mContext;
	private CallBack callback;
	private EditText et_barcode, et_name, et_size, et_priceIn,
			et_salePrice, et_num, et_stock;
	private String close = "1";
	private TextView tv_status,tv_type;
	private LinearLayout ll_status,ll_type;
	private PopupWindow pop_status,pop_type;
	private Goods good;
	private ArrayList<Type>details=new ArrayList<Type>();
	public StockDialog(Context context, ArrayList<Type>details,CallBack callback) {
		super(context);
		this.details=details;
		this.callback = callback;
		mContext = context;
		this.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;

			}
		});
		initUI();
	}

	@SuppressLint("InlinedApi")
	private void initUI() {
		good = new Goods();
		// this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		View v = getLayoutInflater().inflate(R.layout.dialog_stock, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(500,
				LayoutParams.WRAP_CONTENT);
		TextView cancel = (TextView) v.findViewById(R.id.tv_dialog_cancel);
		TextView confirm = (TextView) v.findViewById(R.id.tv_dialog_confirm);

		et_barcode = (EditText) v.findViewById(R.id.et_stock_barcode);
		//et_code = (EditText) v.findViewById(R.id.et_stock_code);
		et_name = (EditText) v.findViewById(R.id.et_stock_name);
		et_size = (EditText) v.findViewById(R.id.et_stock_size);
		et_priceIn = (EditText) v.findViewById(R.id.et_stock_priceIn);
		et_salePrice = (EditText) v.findViewById(R.id.et_stock_salePrice);
		et_stock = (EditText) v.findViewById(R.id.et_stock_stock);
		et_num = (EditText) v.findViewById(R.id.et_stock_newNum);

		tv_status = (TextView) v.findViewById(R.id.tv_stock_status);
		ll_status = (LinearLayout) v.findViewById(R.id.ll_stock_status);
		tv_type=(TextView) v.findViewById(R.id.tv_stock_type);
		ll_type=(LinearLayout) v.findViewById(R.id.ll_stock_type);
		
		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
		ll_status.setOnClickListener(this);
		ll_type.setOnClickListener(this);
		this.addContentView(v, lp);
	}

	/*
	 * public void setCallBack(CallBack callback){ this.callback=callback; }
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_dialog_cancel:{
			this.cancel();
		}
		break;
		case R.id.tv_dialog_confirm:{
			//this.cancel();
			good.setBarcode(et_barcode.getText().toString().trim());
			good.setCode("");//商品编码
			good.setName(et_name.getText().toString().trim());
			good.setSize(et_size.getText().toString().trim());
			good.setPriceIn(et_priceIn.getText().toString().trim());//进货价
			good.setSalePrice(et_salePrice.getText().toString().trim());//售价
			good.setStock(et_stock.getText().toString().trim());//库存
			good.setNum(et_num.getText().toString().trim());//新增数量
		//	good.setStock(et_stock.getText().toString().trim());//商品新增数量
			good.setClose(close);
			/*if(TextUtils.isEmpty(good.getName())||TextUtils.isEmpty(good.getBarcode())
					||TextUtils.isEmpty(good.getSalePrice())||TextUtils.isEmpty(good.getCode())
					||TextUtils.isEmpty(good.getSize())||TextUtils.isEmpty(good.getPriceIn())||
					TextUtils.isEmpty(good.getStock())||TextUtils.isEmpty(good.getNum())){
				TipsToast.makeText(mContext, "不得为空", 1).show();
			}else {*/
		 
					callback.onConfirmClick(good);
					StockDialog.this.dismiss();
				
			//}
			
		}
		break;
		case R.id.ll_stock_status:{
			openPop();
		}
		break;
		case R.id.ll_stock_type:{
			openPop2();
		}
		break;
		case R.id.tv_status_shangjia:{
			tv_status.setText("上架");
			close="1";
			pop_status.dismiss();
		}
		break;
		case R.id.tv_status_xiajia:{
			tv_status.setText("下架");
			close="0";
			pop_status.dismiss();
		}
		break;
		}
	}

	private void openPop() {
		if (pop_status != null && pop_status.isShowing()) {
			pop_status.dismiss();
		} else {
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.pop_status, null);
			TextView shangjia = (TextView) view
					.findViewById(R.id.tv_status_shangjia);
			shangjia.setOnClickListener(this);
			TextView xiajia = (TextView) view
					.findViewById(R.id.tv_status_xiajia);
			xiajia.setOnClickListener(this);
			if (tv_status.getText().toString().equals("上架")) {
				shangjia.setTextColor(mContext.getResources().getColor(
						R.color.skyblue));
			} else {
				xiajia.setTextColor(mContext.getResources().getColor(
						R.color.skyblue));
			}
			pop_status = new PopupWindow(view, tv_status.getMeasuredWidth(),
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			pop_status.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));
			pop_status.setOutsideTouchable(true);// 设置触摸外面时消失
			pop_status.setFocusable(true);
			pop_status.setTouchable(true);

			pop_status.showAsDropDown(tv_status, 0, 5);
		}

	}
	private void openPop2() {
		if (pop_type != null && pop_type.isShowing()) {
			pop_type.dismiss();
		} else {
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.pop_list, null);
			ListView list=(ListView) view.findViewById(R.id.lv_list);
			list.setAdapter(new PopTypeAdapter(mContext, details,new PopTypeAdapter.Callback() {
				
				@Override
				public void set(Type type) {
					// TODO Auto-generated method stub
					
				}
			}));
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					 tv_type.setText(details.get(position).getName());
					 good.setType(details.get(position).getId());
					 pop_type.dismiss();
					
				}
			});
			pop_type = new PopupWindow(view, tv_type.getMeasuredWidth(),
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			pop_type.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));
			pop_type.setOutsideTouchable(true);// 设置触摸外面时消失
			pop_type.setFocusable(true);
			pop_type.setTouchable(true);

			pop_type.showAsDropDown(tv_type, 0, 5);
		}

	}
	public void showDialog() {
		this.show();
	}

	public interface CallBack {
		public void onConfirmClick(Goods good);
	}

}
