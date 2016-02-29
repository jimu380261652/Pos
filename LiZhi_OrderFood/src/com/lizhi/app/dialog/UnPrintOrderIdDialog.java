package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.lizhiorder.R;
/**
 * 未打印的订单号弹出框
 * @author user
 *
 */
public class UnPrintOrderIdDialog extends BaseDialog implements android.view.View.OnClickListener{
	PrintCallBack callBack;
	Context context;
	TextView cancel_tv,check_tv;
	public UnPrintOrderIdDialog(Context context,PrintCallBack callBack) {
		super(context);
		// TODO Auto-generated constructor stub
		this.callBack=callBack;
		this.context=context;
		initUI();
	}
	private void initUI() {
		// TODO Auto-generated method stub
		View view=getLayoutInflater().inflate(R.layout.dialog_print, null);
		RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		cancel_tv=(TextView)view.findViewById(R.id.cancel_tv);
		cancel_tv.setOnClickListener(this);
		check_tv=(TextView)view.findViewById(R.id.check_tv);
		check_tv.setOnClickListener(this);
		addContentView(view, lp);
	}
	public interface PrintCallBack{
		public void checkOrder();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_tv:
			dismiss();
			break;
		case R.id.check_tv:
			callBack.checkOrder();
			dismiss();
			break;
		default:
			break;
		}
	}
}
