package com.lizhi.app.dialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lizhi.adapter.PicAdapter;
import com.pos.lizhiorder.R;

public class PicDialog extends BaseDialog implements OnClickListener{
	private Context context;
	private View view;
	private GridView gv_pic;
	private PicAdapter adapter; 
	private int[] urls = { R.drawable.pic4, R.drawable.pic1, R.drawable.pic2,
			R.drawable.pic8, R.drawable.pic7, R.drawable.c1, R.drawable.c2,
			R.drawable.c3 };
	private String[] names = { "浓郁摩卡", "曼特宁", "红豆可可", "珍珠奶茶", "台湾奶茶", "玛琪雅朵",
			"冰咖啡", "曼巴咖啡" };
	private CallBack callback;
	public interface CallBack{
		public void callback(int pic,String name);
	}
	public PicDialog(Context context,CallBack callback) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.callback=callback;
		InitUI();
	}
	private void InitUI() {
		// TODO Auto-generated method stub
		view=getLayoutInflater().inflate(R.layout.dialog_pic, null);	
		gv_pic=(GridView) view.findViewById(R.id.gv_pic);
		adapter=new PicAdapter(context, names, urls);
		gv_pic.setAdapter(adapter);
		gv_pic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				callback.callback(urls[position], names[position]);
				PicDialog.this.dismiss();
			}
		});
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
		this.addContentView(view, lp);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
