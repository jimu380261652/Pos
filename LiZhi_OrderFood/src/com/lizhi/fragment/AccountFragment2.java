package com.lizhi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lizhi.app.dialog.BankSelectDialog;
import com.lizhi.app.dialog.IncomeSuccessDialog;
import com.lizhi.app.dialog.InputPasswordDialog;
import com.pos.lizhiorder.R;

public class AccountFragment2 extends BaseFragment{
	private Context context;
	private LinearLayout selectBank;
	private Button income;
	private EditText moneny;
	private ImageView iv_account2;
	private TextView tv_account2;
	BankSelectDialog dialog;
	IncomeSuccessDialog icDialog;
	InputPasswordDialog ipDialog;
	String edittext,pass;
	Boolean b;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_account2,container, false);
		selectBank=(LinearLayout)mViewGroup.findViewById(R.id.ll_account2_bank);
		income=(Button)mViewGroup.findViewById(R.id.bt_account2_income);
		moneny=(EditText)mViewGroup.findViewById(R.id.et_account2_moneny);
		return mViewGroup;
	}
	 @Override  
	public void onActivityCreated(Bundle savedInstanceState) {  
	        super.onActivityCreated(savedInstanceState);  
	        context=getActivity();  
	        initView();
	 }		
	 private void initView(){
		 iv_account2=(ImageView) getView().findViewById(R.id.iv_account2);
		 tv_account2=(TextView) getView().findViewById(R.id.tv_account2);
		 income.setOnClickListener(l);
		 selectBank.setOnClickListener(l);
	 }
	 OnClickListener l=new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			
			switch(arg0.getId()){
			case R.id.ll_account2_bank:
				dialog=new BankSelectDialog(context,new BankSelectDialog.BankSelectListener() {
					
					@Override
					public void refreshUI(String bank, int url, int position) {
						 iv_account2.setBackgroundResource(url);
						 tv_account2.setText(bank);
						
					}
				});
				dialog.show();
				break;
			case R.id.bt_account2_income:
				if(TextUtils.isEmpty(moneny.getText())){
					Toast.makeText(context, "请输入提现金额", 1).show();
				}else{
					ipDialog=new InputPasswordDialog(context, new InputPasswordDialog.InputPasswordListener(){

						@Override
						public void GivePass(String p) {//获取用户输入的密码
							 pass=p;
							 
						}
						
					});
					ipDialog.show();
					//充值
					
				}
				
			}
		}
		 
	 };
}
