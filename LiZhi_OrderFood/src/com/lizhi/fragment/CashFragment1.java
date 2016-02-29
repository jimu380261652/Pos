package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.CashAdapter1;
import com.lizhi.adapter.GetGoodsAdapte;
import com.lizhi.app.dialog.GetOrderDialog;
import com.lizhi.app.dialog.GoodsItemDialog;
import com.lizhi.bean.Temporary;
import com.pos.lizhiorder.R;

//收银台
public class CashFragment1 extends NoBugFragment implements OnClickListener {
	private Context context;
	private View view;
	private ListView GetGoods;// 搭单列表
	private GetGoodsAdapte adapter;

	private CashAdapter1 adapter1;
	private Button btn_order;// 下单
	private Callback callback;// 顶部图标改为返回，并且跳转到CashFragment2
	private GridView gridview;
	private TextView goods, waitorder, money;// 待接单按钮
	private ImageView add;// ,add2,add1,minus1,minus2;
	private GoodsItemDialog dialog;
	private List<Temporary> list = new ArrayList<Temporary>();
	private int WhichCash = 1;// 判断是哪个类型
	/*
	 * 花式咖啡、单品咖啡、果汁
	 */

	private int[] urls = { R.drawable.pic4, R.drawable.pic1, R.drawable.pic2,
			R.drawable.pic8, R.drawable.pic7, R.drawable.c1, R.drawable.c2,
			R.drawable.c3 };
	private int[] urls1 = { R.drawable.pic6, R.drawable.pic9, R.drawable.pic3,
			R.drawable.pic5, R.drawable.c4, R.drawable.c5, R.drawable.c6 };
	private int[] urls2 = { R.drawable.g11, R.drawable.g12, R.drawable.g13,
			R.drawable.g14, R.drawable.g15 };
	private String[] names1 = { "焦糖玛其朵", "抹茶星冰乐", "焦糖咖啡", "拿铁", "浓缩咖啡", "美式咖啡",
			"卡布奇诺" };
	private String[] names = { "浓郁摩卡", "曼特宁", "红豆可可", "珍珠奶茶", "台湾奶茶", "玛琪雅朵",
			"冰咖啡", "曼巴咖啡" };
	private String[] names2 = { "鲜榨菠萝汁", "鲜榨芒果汁", "鲜榨草莓汁", "鲜榨橙汁", "鲜榨西瓜汁" };

	private RadioButton cash1, cash2, cash3;
	private GetOrderDialog dialog1;
	private double amount = 0;

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public interface Callback {
		public void callback(List<Temporary> list);
	}

	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_cash1, null);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = get_Activity();
		initview();
	}

	private void initview() {
		GetGoods = (ListView) view.findViewById(R.id.lv_GetGoods);
		GetGoods.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				list.remove(position);
				adapter.update(list);
				GetAmount();
				return false;
			}
		});

		adapter = new GetGoodsAdapte(context, list,
				new GetGoodsAdapte.CallBack() {

					@Override
					public void changenum(int position, int num,
							List<Temporary> Temps) {
						// TODO Auto-generated method stub
						Temps.get(position).setNum(num);
						adapter.update(Temps);
						GetAmount();
					}
				});
		money = (TextView) view.findViewById(R.id.tv_money);
		GetGoods.setAdapter(adapter);
		waitorder = (TextView) view.findViewById(R.id.waitorder);
		waitorder.setOnClickListener(this);
		cash1 = (RadioButton) view.findViewById(R.id.rb_cash1);
		cash2 = (RadioButton) view.findViewById(R.id.rb_cash2);
		cash3 = (RadioButton) view.findViewById(R.id.rb_cash3);
		cash1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {

					adapter1.update(names, urls);
					WhichCash = 1;
				}
			}
		});
		cash2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					adapter1.update(names1, urls1);
					WhichCash = 2;
				}
			}
		});
		cash3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					adapter1.update(names2, urls2);
					WhichCash = 3;
				}
			}
		});
		add = (ImageView) view.findViewById(R.id.iv_add);
		add.setOnClickListener(this);
		goods = (TextView) view.findViewById(R.id.tv_goods2);
		gridview = (GridView) view.findViewById(R.id.gv_home);
		adapter1 = new CashAdapter1(context, names, urls);

		gridview.setAdapter(adapter1);
		btn_order = (Button) view.findViewById(R.id.btn_order);
		btn_order.setOnClickListener(this);
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (WhichCash == 1) {
					dialog = new GoodsItemDialog(context, urls[position],
							names[position],
							new GoodsItemDialog.GoodsItemListener() {

								public void callback(String name, int num) {
									Temporary t = new Temporary();
									t.setName(name);
									t.setNum(num);
									list.add(t);
									GetAmount();
									adapter.update(list);

								}

							});
					dialog.show();
				} else if (WhichCash == 2) {
					dialog = new GoodsItemDialog(context, urls1[position],
							names1[position],
							new GoodsItemDialog.GoodsItemListener() {

								public void callback(String name, int num) {
									Temporary t = new Temporary();
									t.setName(name);
									t.setNum(num);
									list.add(t);
									GetAmount();
									adapter.update(list);

								}

							});
					dialog.show();
				} else {
					dialog = new GoodsItemDialog(context, urls2[position],
							names2[position],
							new GoodsItemDialog.GoodsItemListener() {

								@Override
								public void callback(String name, int num) {
									Temporary t = new Temporary();
									t.setName(name);
									t.setNum(num);
									list.add(t);
									GetAmount();
									adapter.update(list);

								}

							});
					dialog.show();
				}

			}
		});

		goods.setText("2.鲜榨西瓜汁    1");
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_order: {
			if (list.size() > 0){
				/*add.setVisibility(View.VISIBLE);

				goods.setTextColor(context.getResources()
						.getColor(R.color.black));*/
				callback.callback(list);
				
			}			
			else
				TipsToast.makeText(context, "请选择商品", 1).show();
		}
			break;
		case R.id.iv_add: {
			add.setVisibility(View.INVISIBLE);

			goods.setTextColor(context.getResources()
					.getColor(R.color.darkgray));
			Temporary t = new Temporary();
			t.setName("鲜榨西瓜汁");
			list.add(t);
			GetAmount();
			adapter.update(list);
		}
			break;
		case R.id.waitorder: {
			dialog1 = new GetOrderDialog(context);
			dialog1.show();
		}
			break;
		}
	}

	private void GetAmount() {
		amount = 0;
		for (int i = 0; i < list.size(); i++) {
			amount += list.get(i).getNum() * 32;
		}
		money.setText("￥" + amount);
	}
}
