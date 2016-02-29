package com.lizhi.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lizhi.adapter.GoodAdapter;
import com.lizhi.app.dialog.PicDialog;
import com.lizhi.bean.Good;
import com.markmao.pulltorefresh.widget.XListView;
import com.markmao.pulltorefresh.widget.XListView.IXListViewListener;
import com.pos.lizhiorder.R;
 
//所有商品
public class GoodsFragment1 extends NoBugFragment implements OnClickListener{
 
	private Context contex;
	private XListView xlistview;
	private GoodAdapter adapter;
	private List<Good>list=new ArrayList<Good>();
	private int index=0;
	private boolean flag=false;
	private RelativeLayout rl_showdetail;
	private LinearLayout ll_edit;
	private Button btn_add,btn_save,btn_cancle;
	private ImageView iv_goodspic;
	private PicDialog dialog;
	private EditText et_name;
	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_goods1, null);
		
		return view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		contex=get_Activity();
		InitUI();
	}
	private void InitUI() {
		// TODO Auto-generated method stub
		//btn_save,btn_cancle
		et_name=(EditText) view.findViewById(R.id.et_name);
		iv_goodspic=(ImageView) view.findViewById(R.id.iv_goodspic);
		iv_goodspic.setOnClickListener(this);
		btn_save=(Button) view.findViewById(R.id.btn_save);
		btn_cancle=(Button) view.findViewById(R.id.btn_cancle);
		btn_save.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
		rl_showdetail=(RelativeLayout) view.findViewById(R.id.rl_showdetail);
		ll_edit=(LinearLayout) view.findViewById(R.id.ll_edit);
		btn_add=(Button) view.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(this);
		xlistview=(XListView) getView().findViewById(R.id.ll_goods);
		for(int i=0;i<10;i++){
			Good good=new Good();
			good.setName("香草加糖咖啡");
			good.setPrice("36.00");
			list.add(good);
		}
		adapter=new GoodAdapter(contex, list, index);
		xlistview.setAdapter(adapter);
		xlistview.setPullLoadEnable(true);// 下拉刷新 /滚动
		xlistview.setAutoLoadEnable(true);// 成功
		
		xlistview.setXListViewListener(new IXListViewListener() {

			// 下拉: 显示第一页，清空集合，重新加载第一页
			@Override
			public void onRefresh() {
				if(!flag){
					flag=!flag;
					stopTopOrButtom();
					 
				}
			}

			// 滚动：添加下一页，刷新列表
			@Override
			public void onLoadMore() {
				if(!flag){
					flag=!flag;
					for(int i=0;i<10;i++){
						Good good=new Good();
						good.setName("香草加糖咖啡");
						good.setPrice("36.00");
						list.add(good);				
					}
					adapter.update(list);
					stopTopOrButtom();
				}
			}
		});
		xlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(index!=(position-1)){
					index=position-1;
					adapter.update(index);
				}
			}
		});
	}
	private void stopTopOrButtom() {
		xlistview.stopRefresh();// 隐藏顶部等待视图
		xlistview.stopLoadMore();// 隐藏底部等待视图
		xlistview.setRefreshTime(getTime());
		flag=false;
	}
	private String getTime() {
		Date date = new Date();
		SimpleDateFormat formate = new SimpleDateFormat("MM月dd日 HH:mm");
		return formate.format(date);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/*private RelativeLayout rl_showdetail;
		private LinearLayout ll_edit;*/
		switch (v.getId()) {
		case R.id.iv_goodspic:{
			dialog=new PicDialog(contex, new PicDialog.CallBack() {
				
				@Override
				public void callback(int pic, String name) {
					// TODO Auto-generated method stub
					iv_goodspic.setImageResource(pic);
					et_name.setText(name);
				}
			});
			dialog.show();
		}
		break;
		case R.id.btn_add:{
			rl_showdetail.setVisibility(View.GONE);
			ll_edit.setVisibility(View.VISIBLE);
		}
			break;
		case R.id.btn_save:{
			rl_showdetail.setVisibility(View.VISIBLE);
			ll_edit.setVisibility(View.GONE);
		}
		break;
		case R.id.btn_cancle:{
			rl_showdetail.setVisibility(View.VISIBLE);
			ll_edit.setVisibility(View.GONE);
		}
		break;
		default:
			break;
		}
	}
}
