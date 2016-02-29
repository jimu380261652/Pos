package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.duiduifu.view.TipsToast;
import com.pos.lizhiorder.R;

public class GetOrderDialog extends BaseDialog implements android.view.View.OnClickListener{
	private Context context;
	private Button cancle,ok;
	public GetOrderDialog(Context context) {
		super(context);
		this.context=context;
		InitUI();
	}
	private void InitUI() {
		// TODO Auto-generated method stub
		View v = getLayoutInflater().inflate(R.layout.dialog_get_order, null, false);
		cancle=(Button) v.findViewById(R.id.btn_cancle);
		ok=(Button) v.findViewById(R.id.btn_ok);
		cancle.setOnClickListener(this);
		ok.setOnClickListener(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(395,
				650);
		this.addContentView(v, lp);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_cancle:{
			TipsToast.makeText(context, "取消订单", 1).show();
			GetOrderDialog.this.dismiss();
		}
			
			break;
		case R.id.btn_ok:{
			TipsToast.makeText(context, "接单", 1).show();
			GetOrderDialog.this.dismiss();
		}
			
			break;
			default:
				break;
		}
	}

}
