package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class HintDialog extends BaseDialog implements
android.view.View.OnClickListener{
	private HintListener listener;
	private Context context;
	private TextView btn_ok,btn_cancle;
	public HintDialog(Context context,HintListener listener) {
		super(context);
		this.context=context;
		this.listener=listener;
		initUI();
	}
	public interface HintListener{
		public void show();
	}
	public void initUI() {
		View v = getLayoutInflater().inflate(R.layout.dialog_hint, null);
		btn_ok = (TextView) v.findViewById(R.id.btn_ok);
		btn_cancle = (TextView) v.findViewById(R.id.btn_cancle);
		btn_ok.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(400,
				400);
		this.addContentView(v, lp);
	}
	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		case R.id.btn_ok:
			listener.show();
			HintDialog.this.dismiss();
			break;
		case R.id.btn_cancle:
			HintDialog.this.dismiss();
			break;
		default:
			break;
		}
	}

}
