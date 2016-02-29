package com.lizhi.app.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.lizhiorder.R;
//商品入库
public class GuaDanDialog extends BaseDialog implements android.view.View.OnClickListener {

	
	Context mContext;
	private String content;
	private CallBack callback;
	public GuaDanDialog(Context context,String content) {
		super(context);
		mContext = context;
		this.content=content;
		this.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK){
					return true;
				}
				return false;

			}
		});
		initUI();
	}

	
	@SuppressLint("InlinedApi")
	private void initUI() {
//		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		View v = getLayoutInflater().inflate(R.layout.dialog_guadan, null);	
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(850, 450);
		TextView tv_content=(TextView) v.findViewById(R.id.tv_dialog_content);
		TextView cancel=(TextView) v.findViewById(R.id.tv_dialog_cancel);
		TextView confirm=(TextView) v.findViewById(R.id.tv_dialog_confirm);
		tv_content.setText(content);
		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
		
		this.addContentView(v, lp);
	}
	public void setCallBack(CallBack callback){
		this.callback=callback;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_dialog_cancel:{
			this.cancel();
		}
		break;
		case R.id.tv_dialog_confirm:{
			if(callback!=null){
				callback.onConfirmClick();
			}
		}
		break;
		}
	}
	
	public void showDialog(){
		this.show();
	}
	
	public interface CallBack{
		public void onConfirmClick();
	}

}
