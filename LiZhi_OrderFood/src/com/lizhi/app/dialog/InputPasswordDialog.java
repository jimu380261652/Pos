package com.lizhi.app.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pos.lizhiorder.R;

public class InputPasswordDialog extends BaseDialog implements
		android.view.View.OnClickListener{
	private Context mContext;
	private EditText et1, et2, et3, et4, et5, et6;
	private Button confirm_btn,cancel_btn;
	private StringBuilder sb=new StringBuilder();
	public interface InputPasswordListener {  
        
        public void GivePass(String pass);  
    }  
      
    private InputPasswordListener mlistener;  
	public InputPasswordDialog(Context context,InputPasswordListener listener) {
		super(context);
		mlistener=listener;
		mContext = context;
		initUI();
	}
	
	public void initUI() {
		View v = getLayoutInflater().inflate(R.layout.dialog_input_password,
				null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(600,
				420);
		this.addContentView(v, lp);
		confirm_btn=(Button) findViewById(R.id.confirm_btn);
		cancel_btn=(Button) findViewById(R.id.cancel_btn);
		confirm_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		et1 = (EditText) findViewById(R.id.et_dialog_ip1);
		et2 = (EditText) findViewById(R.id.et_dialog_ip2);
		et3 = (EditText) findViewById(R.id.et_dialog_ip3);
		et4 = (EditText) findViewById(R.id.et_dialog_ip4);
		et5 = (EditText) findViewById(R.id.et_dialog_ip5);
		et6 = (EditText) findViewById(R.id.et_dialog_ip6);
		et1.addTextChangedListener(textChange1);
		et2.addTextChangedListener(textChange2);
		et3.addTextChangedListener(textChange3);
		et4.addTextChangedListener(textChange4);
		et5.addTextChangedListener(textChange5);
		et6.addTextChangedListener(textChange6);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.confirm_btn://获取密码
			if(sb.toString().length()>5){
				mlistener.GivePass(sb.toString());
				InputPasswordDialog.this.dismiss();
			}
			else {
				Toast.makeText(mContext, "请输入6位数字", 1).show();
			}
			break;
		case R.id.cancel_btn:
			InputPasswordDialog.this.dismiss();
			break;
		default:
			break;
		}
	}

	TextWatcher textChange1 = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable e) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			sb.append(et1.getText());
			et1.setFocusable(false);
			et2.setFocusable(true);

		}

	};
	TextWatcher textChange2 = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable e) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			sb.append(et2.getText());
			et2.setFocusable(false);
			et3.setFocusable(true);

		}

	};
	TextWatcher textChange3 = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable e) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			sb.append(et3.getText());
			et3.setFocusable(false);
			et4.setFocusable(true);
		}

	};
	TextWatcher textChange4 = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable e) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			sb.append(et4.getText());
			et4.setFocusable(false);
			et5.setFocusable(true);

		}

	};
	TextWatcher textChange5 = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable e) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			sb.append(et5.getText());
			et5.setFocusable(false);
			et6.setFocusable(true);
		}

	};
	TextWatcher textChange6=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			sb.append(et6.getText());
		}
	};
}
