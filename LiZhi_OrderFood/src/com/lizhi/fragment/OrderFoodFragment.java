package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lizhi.bean.Temporary;
import com.pos.lizhiorder.R;

//点餐
public class OrderFoodFragment extends NoBugFragment {
	private Context context;
	FragmentManager fm;
	private int pre_index = 0;
	// private CashFragment1 cash1;
	private OrderFoodFragmentSub1 cash1;
	private CashFragment2 cash2;
	private List<Temporary> list = new ArrayList<Temporary>();
	private boolean flag;// 判断是否需要隐藏

	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_order_food, null);

		flag = false;
		return view;
	}

	public void initFragment() {
		cash1 = new OrderFoodFragmentSub1();
		// cash1 = new CashFragment1();
		// cash1.setCallback(new CashFragment1.Callback() {
		//
		// @Override
		// public void callback(List<Temporary>temp) {
		// // TODO Auto-generated method stub
		// list=temp;
		//
		// setTab(1);
		//
		// }
		// });
		cash2 = new CashFragment2();
		cash2.setCallback(new CashFragment2.CallBack() {

			@Override
			public void callback() {
				// TODO Auto-generated method stub
				setTab(0);

			}
		});

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = get_Activity();
		fm = ((FragmentActivity) context).getSupportFragmentManager();
		initFragment();
		setTab(pre_index);
	}

	public void setTab(int position) {
		if (flag)
			hideTab();

		switch (position) {
		case 0: {

			if (cash1.isAdded()) {
				fm.beginTransaction().show(cash1).commit();
			} else {
				fm.beginTransaction().add(R.id.fra_cash, cash1).commit();
				// fm.beginTransaction().replace(R.id.fra_cash, cash1).commit();
			}
			flag = true;
			setPre_index(0);
		}
			break;
		case 1: {

			if (cash2.isAdded()) {
				cash2.show(list);
				fm.beginTransaction().show(cash2).commit();
			} else {
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
				cash2.setArguments(bundle);
				fm.beginTransaction().add(R.id.fra_cash, cash2).commit();
				// fm.beginTransaction().replace(R.id.fra_cash, cash2).commit();
			}
			setPre_index(1);

		}
			break;
		}
		fm.beginTransaction().commit();
		pre_index = position;
	}

	// 隐藏上一个Fragment
	private void hideTab() {
		switch (pre_index) {
		case 0: {
			fm.beginTransaction().hide(cash1).commit();
		}
			break;
		case 1: {
			fm.beginTransaction().hide(cash2).commit();
		}
			break;

		}
	}

	public void setPre_index(int pre_index) {
		this.pre_index = pre_index;
	}
}
