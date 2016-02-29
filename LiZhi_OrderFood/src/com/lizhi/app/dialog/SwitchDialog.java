package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class SwitchDialog extends BaseDialog {
	private Context context;
	private TextView btn_ok,btn_cancle;
	private SwitchListener listener;
	public SwitchDialog(Context context,  
			SwitchListener listener) {
		super(context);
		this.context = context;
		this.listener=listener;
		initUI();
	}

	public interface SwitchListener {
		public void Switch();
		public void Switch2();
	}

	public void initUI() {
		View v = getLayoutInflater().inflate(R.layout.dialog_switch, null);
		btn_ok= (TextView) v.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.Switch();
				SwitchDialog.this.dismiss();
			}
		});
		btn_cancle=(TextView) v.findViewById(R.id.btn_cancle);
		btn_cancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.Switch2();
				SwitchDialog.this.dismiss();
			}
		});
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(500,
				350);
		this.addContentView(v, lp);
	}

	 

}
