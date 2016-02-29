package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.pos.lizhiorder.R;

public class IncomeSuccessDialog extends BaseDialog implements android.view.View.OnClickListener{
	private Context mContext;
	public IncomeSuccessDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;	
		initUI();
	}
	public void initUI(){
		View v = getLayoutInflater().inflate(R.layout.dialog_income_success, null);	
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(600, 400);	
		this.addContentView(v, lp);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
