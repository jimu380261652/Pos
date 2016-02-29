package com.lizhi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.pos.lizhiorder.R;

//收银台
public class PosFragment extends NoBugFragment {
	private Context context;
	FragmentManager fm;
	private int pre_index = 0;
	private PosFragment1 pos1;
	private PosFragment2 pos2;

	private boolean flag;// 判断是否需要隐藏

	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_pos, null);

		flag = false;

		return view;
	}

	public void initFragment() {
		pos1 = new PosFragment1();
		pos1.setCallback(new PosFragment1.Callback() {

			@Override
			public void callback() {
				// TODO Auto-generated method stub

				setTab(1);

			}
		});
		pos2 = new PosFragment2();
		pos2.setCallback(new PosFragment2.Callback() {

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

			if (pos1.isAdded()) {
				fm.beginTransaction().show(pos1).commit();
			} else {
				fm.beginTransaction().add(R.id.fra_pos, pos1).commit();
				// fm.beginTransaction().replace(R.id.fra_pos, pos1).commit();
			}
			flag = true;
			setPre_index(0);
		}
			break;
		case 1: {

			if (pos2.isAdded()) {

				fm.beginTransaction().show(pos2).commit();
			} else {

				fm.beginTransaction().add(R.id.fra_pos, pos2).commit();
				// fm.beginTransaction().replace(R.id.fra_pos, pos2).commit();
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
			fm.beginTransaction().hide(pos1).commit();
		}
			break;
		case 1: {
			fm.beginTransaction().hide(pos2).commit();
		}
			break;

		}
	}

	public void setPre_index(int pre_index) {
		this.pre_index = pre_index;
	}
}
