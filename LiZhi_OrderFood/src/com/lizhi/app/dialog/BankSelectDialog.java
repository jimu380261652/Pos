package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lizhi.adapter.BankAdapter;
import com.lizhi.app.dialog.GuaDanDialog.CallBack;
import com.pos.lizhiorder.R;

public class BankSelectDialog extends BaseDialog implements android.view.View.OnClickListener{
	private Context mContext;
	private String content;
	private CallBack callback;
	private BankAdapter adapter;
	private ListView lv_bank;
	private int[] urls={R.drawable.gs,R.drawable.js,R.drawable.zg,R.drawable.ny,
			R.drawable.jt,R.drawable.zs,R.drawable.ms,R.drawable.gd,R.drawable.gdyz};
	private String[] banks={"中国工商银行","中国建设银行","中国银行","中国农业银行","交通银行","招商银行","民生银行","光大银行","广东邮政储蓄"};
	public interface BankSelectListener {  
        /** 
         * 回调函数，用于在Dialog的监听事件触发后刷新fragment的UI显示 
         * position让fragment判断是哪个银行
         */  
        public void refreshUI(String bank,int url,int position);  
    }  
      
    private BankSelectListener listener;  
    /** 
     * 带监听器参数的构造函数 
     */ 
	public BankSelectDialog(Context context,BankSelectListener listener) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.listener=listener;
		initUI();
	}
	public BankSelectDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	//	this.listener=listener;
		initUI();
	}
	public void initUI(){
		View v = getLayoutInflater().inflate(R.layout.dialog_bankselect, null);	
		adapter=new BankAdapter(mContext,urls,banks);
		lv_bank=(ListView)v.findViewById(R.id.lv_bank);
		lv_bank.setAdapter(adapter);
		if(listener!=null){
			lv_bank.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					 listener.refreshUI(banks[position],urls[position],position); 
					 BankSelectDialog.this.dismiss();
				}
			});
		}
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(500, 310);	
		this.addContentView(v, lp);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
