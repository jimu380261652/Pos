package com.lizhi.app.dialog;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lizhi.adapter.QudangAdapter;
import com.lizhi.bean.TemporarilyOrder;
import com.pos.lizhiorder.R;
//商品入库
public class QuDanDialog extends BaseDialog implements android.view.View.OnClickListener {

	QuDanListener listener;
	Context mContext;
	Button btn_canle;
	ListView lv_retail;
	List<TemporarilyOrder> temps;
	public QuDanDialog(Context context,List<TemporarilyOrder> temps,QuDanListener listener) {
		super(context);
		this.temps=temps;
		this.listener=listener;
		mContext = context;
		initUI();
	}
	
	public interface QuDanListener{
		public void additem(int position);
	}
 
	private void initUI() {
 		View v = getLayoutInflater().inflate(R.layout.dialog_qudan, null);	
 		btn_canle=(Button) v.findViewById(R.id.btn_canle);
 		btn_canle.setOnClickListener(this);
		lv_retail=(ListView) v.findViewById(R.id.lv_retail);
		lv_retail.setAdapter(new QudangAdapter(mContext, temps));
		lv_retail.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 listener.additem(position);
				
			}
		});
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(450, 450);
		
		this.addContentView(v, lp);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_canle:
			QuDanDialog.this.dismiss(); 
			break;

		default:
			break;
		}
	}
}
