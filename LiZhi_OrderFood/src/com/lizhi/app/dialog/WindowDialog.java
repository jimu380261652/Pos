package com.lizhi.app.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlibrary.app.dialog.SystemAlertDialog;
import com.duiduifu.util.CommonUtil;
import com.duiduifu.util.MD5Util;
import com.pos.lizhiorder.R;

public class WindowDialog extends SystemAlertDialog implements android.view.View.OnClickListener{
	EditText pwd_ed;
	Button cancel_btn;
	Context context;
	CommonUtil mCommonUtil;
	TextView vLogin;
	TextView vEndAll;
	public WindowDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.setCancelable(false);
		initUI();
	}

	private void initUI() {
		// TODO Auto-generated method stub
		View v = getLayoutInflater().inflate(R.layout.dialog_pwd, null);	
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(400, 350);
		pwd_ed=(EditText)v.findViewById(R.id.pwd_ed);
		vLogin = (TextView) v.findViewById(R.id.login_user);
		cancel_btn=(Button)v.findViewById(R.id.cancel_btn);
		vEndAll = (TextView) v.findViewById(R.id.end_all);
		cancel_btn.setOnClickListener(this);
		vEndAll.setOnClickListener(this);
		mCommonUtil=new CommonUtil(context);
		vLogin.setText(mCommonUtil.PreferenceTakeS("loginUser", ""));
		this.addContentView(v, lp);
	}

	@Override
	public void onClick(View v) {
		if (v.getId()==R.id.cancel_btn) {
			if (MD5Util.createSign(pwd_ed.getText().toString()).equals(
					mCommonUtil.PreferenceTakeS("pwd", ""))) {
				pwd_ed.setText("");
				this.dismiss();
			} else {
				Toast.makeText(context, "密码错误", 1000).show();
			}
			return;
		}
		
		if(v.getId()==R.id.end_all){
				Intent intent = new Intent("com.duiduifu.app.endAll");
				intent.putExtra("message", "");
				context.sendBroadcast(intent);
				this.dismiss();
		}
	}

}
