package com.lizhi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhi.app.dialog.BankSelectDialog;
import com.lizhi.app.dialog.IncomeSuccessDialog;
import com.pos.lizhiorder.R;

public class AccountFragment3 extends BaseFragment{
	private Context context;
	private LinearLayout selectBank;
	BankSelectDialog dialog;
	IncomeSuccessDialog icDialog;
	private Button pay;
	private ImageView iv_account3;
	private TextView tv_account3;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_account3,container, false);
		selectBank=(LinearLayout)mViewGroup.findViewById(R.id.ll_account3_bank);
		pay=(Button)mViewGroup.findViewById(R.id.bt_account3_pay);
		return mViewGroup;
	}
	 @Override  
	public void onActivityCreated(Bundle savedInstanceState) {  
	        super.onActivityCreated(savedInstanceState);  
	        context=getActivity();  
	        initView();
	 }		
	 private void initView(){
		 tv_account3=(TextView) getView().findViewById(R.id.tv_account3);
		 iv_account3=(ImageView) getView().findViewById(R.id.iv_account3);
		 selectBank.setOnClickListener(l);
		 pay.setOnClickListener(l);
	 }
	 OnClickListener l=new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				switch(arg0.getId()){
				case R.id.ll_account3_bank:
					//dialog=new BankSelectDialog(context);
					dialog=new BankSelectDialog(context,new BankSelectDialog.BankSelectListener() {
						
						@Override
						public void refreshUI(String bank, int url, int position) {
							 iv_account3.setBackgroundResource(url);
							 tv_account3.setText(bank);
							
						}
					});
					dialog.show();
					break;
				case R.id.bt_account3_pay:
					icDialog=new IncomeSuccessDialog(context);
					icDialog.show();
					//ipDialog=new InputPasswordDialog(context);
					//ipDialog.show();
					break;
				}
			}
			 
		 };
}
