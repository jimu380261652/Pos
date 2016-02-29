package com.lizhi.app.dialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lizhi.adapter.CloseAdapter;
import com.lizhi.bean.Good;
import com.lizhi.bean.GoodsCategory;
import com.pos.lizhiorder.R;

public class GoodsCloseDialog extends BaseDialog implements android.view.View.OnClickListener{
	private Context context;
	private View view;
	private ListView lv_goods;
	private CloseAdapter adapter;
	private Button btn_ok;
	private List<GoodsCategory> list = new ArrayList<GoodsCategory>();
	private String[] names1 = { "焦糖玛其朵", "抹茶星冰乐", "焦糖咖啡", "拿铁", "浓缩咖啡", "美式咖啡",
			"卡布奇诺" };
	private String[] names = { "浓郁摩卡", "曼特宁", "红豆可可", "珍珠奶茶", "台湾奶茶", "玛琪雅朵",
			"冰咖啡", "曼巴咖啡" };
	private String[] names2 = { "鲜榨菠萝汁", "鲜榨芒果汁", "鲜榨草莓汁", "鲜榨橙汁", "鲜榨西瓜汁" };

	public GoodsCloseDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		InitUI();
	}

	private void InitUI() {
		// TODO Auto-generated method stub
		view = getLayoutInflater().inflate(R.layout.dialog_goodsclose, null);
		btn_ok=(Button) view.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		lv_goods = (ListView) view.findViewById(R.id.lv_goods);
		addcategory("花式咖啡", names);
		addcategory("单品咖啡", names1);
		addcategory("果汁", names2);
		adapter = new CloseAdapter(context, list);
		lv_goods.setAdapter(adapter);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				400, 600);
		this.addContentView(view, lp);
	}

	// 添加假数据
	private void addcategory(String name, String[] str) {
		GoodsCategory gc = new GoodsCategory();
		gc.setName(name);
		
		List<Good> glist = new ArrayList<Good>();
		for (int i = 0; i < str.length; i++) {
			Good g = new Good();
			g.setName(str[i]);
			glist.add(g);
		}
		gc.setList(glist);
		list.add(gc);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btn_ok){
			GoodsCloseDialog.this.dismiss();
		}
	}
}
