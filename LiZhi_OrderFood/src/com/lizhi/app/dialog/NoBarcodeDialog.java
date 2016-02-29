package com.lizhi.app.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.pos.lizhiorder.R;

//商品入库
public class NoBarcodeDialog extends BaseDialog implements
		android.view.View.OnClickListener {

	Context mContext;
	private CallBack callback;
	
	 
	public NoBarcodeDialog(Context context,CallBack callback) {
		super(context);
		 
		this.callback = callback;
		mContext = context;
		 
		initUI();
	}

	@SuppressLint("InlinedApi")
	private void initUI() {
	 
		View v = getLayoutInflater().inflate(R.layout.dialog_stock, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(300,300);
		 
		this.addContentView(v, lp);
	}
	@Override
	public void onClick(View v) {}
	public interface CallBack {
		public void onConfirmClick(String price);
	}

}
