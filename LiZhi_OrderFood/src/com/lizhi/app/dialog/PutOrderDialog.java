package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lizhi.bean.TemporarilyOrder;
import com.pos.lizhiorder.R;

public class PutOrderDialog extends BaseDialog implements android.view.View.OnClickListener{
	TextView tv_dialog_cancel;
	TextView tv_dialog_confirm;
	LinearLayout order_ll;
	Context context;
	PutOrderListener mlistener; 
	TemporarilyOrder temp;
	boolean flag;//flase是挂单，true是取单
	public PutOrderDialog(Context arg0,PutOrderListener mlistener,TemporarilyOrder temp,boolean flag) {
		super(arg0);
		this.temp=temp;
		this.context=arg0;
		this.mlistener=mlistener;
		this.flag=flag;
		this.setCancelable(false);
		initUI();
	}
	public interface PutOrderListener {  
        
        public void Put(TemporarilyOrder t);  
        public void Get(TemporarilyOrder t);  
    }  
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tv_dialog_cancel){
			this.dismiss();
		}else if (v.getId()==R.id.tv_dialog_confirm) {
			if(flag){
				mlistener.Get(temp);
			}else {
				//加入判断本地不超过5条记录
				mlistener.Put(temp);//存在本地
			}
			
			this.dismiss();
		}
		
	}
	public void  initUI(){
		View v = getLayoutInflater().inflate(R.layout.dialog_putorder, null);	
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(400, 350);
		tv_dialog_cancel=(TextView) v.findViewById(R.id.tv_dialog_cancel);
		tv_dialog_confirm=(TextView) v.findViewById(R.id.tv_dialog_confirm);
		order_ll=(LinearLayout) v.findViewById(R.id.order_ll);
		this.addContentView(v, lp);
	
	}
}
