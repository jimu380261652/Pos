package com.lizhi.app.dialog;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.duiduifu.util.CommonUtil;
import com.lizhi.adapter.SelectAdapter;
import com.pos.lizhiorder.R;
//商品入库
public class SelectPopup extends PopupWindow implements OnItemClickListener{

	
	private Context context;
	private OnItemCallBack callback;
	private ListView listview;
	private SelectAdapter adapter;
	private int position=0;//当前选择项的位置
	public SelectPopup(Context context,List<String> items,int leftMargin) {
		super(context);
		this.context = context;
		View view=LayoutInflater.from(context).inflate(R.layout.pop_select, null);
		listview=(ListView) view.findViewById(R.id.lv_select);
		adapter=new SelectAdapter(context, items, position);
		MarginLayoutParams params=(MarginLayoutParams) listview.getLayoutParams();
		params.leftMargin=new CommonUtil(context).dip2px(leftMargin);
		listview.setLayoutParams(params);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		setContentView(view);
		setOutsideTouchable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
		setFocusable(true);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
	}
    public void setCallBack(OnItemCallBack callback){
    	this.callback=callback;
    }
    public void clear(){
    	position=0;
    	adapter.update(position);
    }
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		this.dismiss();
		if(position!=arg2){
			this.position=arg2;
			adapter.update(position);
			if(callback!=null){
				callback.onItemClick(arg2);
			}
		}
	}
	public interface OnItemCallBack{
		void onItemClick(int pos);
	}


}
