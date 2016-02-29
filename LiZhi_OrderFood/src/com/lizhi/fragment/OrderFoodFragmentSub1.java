package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lizhi.adapter.GetGoodsAdapte;
import com.lizhi.adapter.ProductStyle1Adapte;
import com.lizhi.adapter.ProductStyle1Adapte.ProductStyle1AdapteCallBack;
import com.lizhi.adapter.TabAdapter;
import com.lizhi.app.dialog.GetOrderDialog;
import com.lizhi.bean.Temporary;
import com.lizhi.widget.HorizonListView;
import com.pos.lizhiorder.R;

//点餐，有商品时
public class OrderFoodFragmentSub1 extends NoBugFragment implements OnClickListener, OnItemClickListener,ProductStyle1AdapteCallBack {
    private Context context;
    private Intent intent;
    private View view;
    // UI
    HorizonListView hl_tab;
    EditText ed_search;
    GridView gv_product;
    ListView lv_order;
    TextView tv_cost, tv_ok, tv_remark;
    ImageView img_del;
    
    
    // 参数
    private TabAdapter adapter_tab;
    private GetGoodsAdapte adapter_order;
    private ProductStyle1Adapte adapter_product;

    private List<String> tab_items = new ArrayList<String>();
    private List<Temporary> products = new ArrayList<Temporary>();
    private List<Temporary> product_in_orders = new ArrayList<Temporary>();
    Temporary temporary;

    private double cost_amount = 0;    
    /*
     * 花式咖啡、单品咖啡、果汁
     */
    private int[] urls = { R.drawable.pic4, R.drawable.pic1, R.drawable.pic2, R.drawable.pic8,
            R.drawable.pic7, R.drawable.c1, R.drawable.c2, R.drawable.c3 };
    private int[] urls1 = { R.drawable.pic6, R.drawable.pic9, R.drawable.pic3, R.drawable.pic5,
            R.drawable.c4, R.drawable.c5, R.drawable.c6 };
    private int[] urls2 = { R.drawable.g11, R.drawable.g12, R.drawable.g13, R.drawable.g14,
            R.drawable.g15 };
    private String[] names1 = { "焦糖玛其朵", "抹茶星冰乐", "焦糖咖啡", "拿铁", "浓缩咖啡", "美式咖啡", "卡布奇诺" };
    private String[] names = { "浓郁摩卡", "曼特宁", "红豆可可", "珍珠奶茶", "台湾奶茶", "玛琪雅朵", "冰咖啡", "曼巴咖啡" };
    private String[] names2 = { "鲜榨菠萝汁", "鲜榨芒果汁", "鲜榨草莓汁", "鲜榨橙汁", "鲜榨西瓜汁" };

    private GetOrderDialog dialog1;   

    public interface Callback {
        public void callback(List<Temporary> product_in_orders);
    }

    @Override
    public View oncreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(get_Activity(), R.layout.fragment_order_food_sub1, null);
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
        tv_cost = (TextView) view.findViewById(R.id.tv_cost);
        tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);
        tv_remark = (TextView) view.findViewById(R.id.tv_remark);
        tv_remark.setOnClickListener(this);

        img_del = (ImageView) view.findViewById(R.id.img_del);
        img_del.setOnClickListener(this);
        hl_tab = (HorizonListView) view.findViewById(R.id.hl_tab);
        hl_tab.setOnItemClickListener(this);
        gv_product = (GridView) view.findViewById(R.id.gv_product);
        gv_product.setOnItemClickListener(this);
        lv_order = (ListView) view.findViewById(R.id.lv_order);
        lv_order.setOnItemClickListener(this);

        makeData();        

        ed_search = (EditText) view.findViewById(R.id.ed_search);
        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    search();
                    return true;
                }
                return false;
            }
        });

        tab_items.add("全部");
        tab_items.add("咖啡");
        tab_items.add("奶茶");
        adapter_tab = new TabAdapter(context, tab_items);
        hl_tab.setAdapter(adapter_tab);
        adapter_tab.setItems(tab_items);
        adapter_tab.notifyDataSetChanged();

        adapter_order = new GetGoodsAdapte(context, product_in_orders, new GetGoodsAdapte.CallBack() {

            @Override
            public void changenum(int position, int num, List<Temporary> Temps) {
                // TODO Auto-generated method stub
                Temps.get(position).setNum(num);
                adapter_order.update(Temps);
                GetAmount();
            }
        });
        lv_order.setAdapter(adapter_order);

        adapter_product = new ProductStyle1Adapte(context,product_in_orders,this);
        gv_product.setAdapter(adapter_product);
        
        
        initUiData();
    }

    private void makeData(){
        temporary = new Temporary();
        temporary.setName("名字1");
        temporary.setNum(1);
        temporary.setNativeLogo(R.drawable.pic1);
        product_in_orders.add(temporary);

        temporary = new Temporary();
        temporary.setName("名字2");
        temporary.setNum(2);
        temporary.setNativeLogo(R.drawable.pic2);
        product_in_orders.add(temporary);

        temporary = new Temporary();
        temporary.setName("名字3");
        temporary.setNum(3);
        temporary.setNativeLogo(R.drawable.pic3);
        product_in_orders.add(temporary);

        temporary = new Temporary();
        temporary.setName("名字4");
        temporary.setNum(4);
        temporary.setNativeLogo(R.drawable.pic4);
        product_in_orders.add(temporary);

        temporary = new Temporary();
        temporary.setName("名字5");
        temporary.setNum(5);
        temporary.setNativeLogo(R.drawable.pic5);
        product_in_orders.add(temporary);

        temporary = new Temporary();
        temporary.setName("名字6");
        temporary.setNum(6);
        temporary.setNativeLogo(R.drawable.pic6);
        product_in_orders.add(temporary);

    }

    private void initUiData(){
    	httpGetProduct();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.tv_ok:
            break;

        case R.id.tv_remark:
            break;

        case R.id.img_del:
            break;
        }
    }
    
    private void httpGetProduct(){
    	
    }


    private void GetAmount() {
        cost_amount = 0;
        for (int i = 0; i < product_in_orders.size(); i++) {
            cost_amount += product_in_orders.get(i).getNum() * 32;
        }
        tv_cost.setText("￥" + cost_amount);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
        case R.id.hl_tab:
            Log.d("myTest", "the hl_tab clicked");
            adapter_tab.update(position);
            httpGetProduct();
            
            
            break;
        case R.id.lv_order:
            Log.d("myTest", "the lv_order clicked");
            break;
        case R.id.gv_product:
            Log.d("myTest", "the gv_product clicked");
            break;
        default:
            break;
        }
    }

	@Override
	public void changenum(int position, int num, List<Temporary> list) {
		// TODO Auto-generated method stub
		
	}
}
