package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pos.lizhiorder.R;

public class IntroduceDialog extends BaseDialog implements android.view.View.OnClickListener{
	private Context context;
	private Button btn;
	private EditText introduce;
	private CallBack callback;
	private String str;
	public IntroduceDialog(Context context,String str) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.str=str;
		InitUI();
	}
	private void InitUI() {
		// TODO Auto-generated method stub
		View v = getLayoutInflater().inflate(R.layout.dialog_introduce, null, false);
		btn=(Button) v.findViewById(R.id.btn_ok);
		btn.setOnClickListener(this);
		introduce=(EditText) v.findViewById(R.id.et_introduce);
		introduce.setText(str);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(395,
				LayoutParams.WRAP_CONTENT);
		this.addContentView(v, lp);
	}
	public interface CallBack{
		public void callback(String str);
	}
	public void setCallback(CallBack callback) {
		this.callback = callback;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btn_ok){
			str=introduce.getText().toString();
			callback.callback(str);
			IntroduceDialog.this.dismiss();
		}
	}
}
