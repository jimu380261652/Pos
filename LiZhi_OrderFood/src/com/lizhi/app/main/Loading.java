package com.lizhi.app.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.pos.lizhiorder.R;


public class Loading extends Dialog {

	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;

	public Loading(Context context) {
		super(context, R.style.Dialog);
		
		this.mContext = context;
		
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.loading, null);
		setContentView(layout);
		
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = 0; // ȥ���ڸ�
		lp.alpha = 1.0f;
this.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK){
					return true;
				}
				return false;

			}
		});
		getWindow().setAttributes(lp);

	}

//	public Loading(BusinessFragment businessFragment) {
//		// TODO Auto-generated constructor stub
//		
//		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.loading, null);
//		setContentView(layout);
//		
//		lp = getWindow().getAttributes();
//		lp.gravity = Gravity.CENTER;
//		lp.dimAmount = 0; // ȥ���ڸ�
//		lp.alpha = 1.0f;
//		getWindow().setAttributes(lp);
//	}
}