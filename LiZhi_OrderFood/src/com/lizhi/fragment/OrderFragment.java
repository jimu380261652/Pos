package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lizhi.adapter.MyFragmentPagerAdapter;
import com.lizhi.util.Constants;
import com.lizhi.widget.UnderlinePageIndicator;
import com.pos.lizhiorder.R;

//订单管理
public class OrderFragment extends BaseFragment {
	private Context context;
	private RadioButton radio1, radio2;
	private RadioGroup group;
	private ViewPager viewPager;
	private UnderlinePageIndicator indicator;
	private MyFragmentPagerAdapter adapter;
	private List<Fragment> fragments;

	private int page = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_order, container,
				false);
		return mViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = getActivity();
		fragments = new ArrayList<Fragment>();
		initView();
	}

	private void initView() {
 
		group = (RadioGroup) getView().findViewById(R.id.rg_order);
		radio1 = (RadioButton) getView().findViewById(R.id.rb_order1);
		radio2 = (RadioButton) getView().findViewById(R.id.rb_order2);

		viewPager = (ViewPager) getView().findViewById(R.id.vp_order);
		indicator = (UnderlinePageIndicator) getView().findViewById(
				R.id.indicator_cashier);
		initViewpager();
		adapter = new MyFragmentPagerAdapter(getActivity()
				.getSupportFragmentManager(), viewPager, fragments);
		viewPager.setAdapter(adapter);
		setCurrent(Constants.page);
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(l_page);
		group.setOnCheckedChangeListener(l_radio);

	}

	OnCheckedChangeListener l_radio = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {

			switch (arg1) {
			case R.id.rb_order1: {
				HideKeyboard();
				if (getPage() == 0) {
					OrderFragment1 fragment = (OrderFragment1) getActivity()
							.getSupportFragmentManager().findFragmentByTag(
									"OrderFragment1");
					fragment.refresh();
					setPage(-1);
				}
				viewPager.setCurrentItem(0);
			}
				break;
			case R.id.rb_order2: {
				HideKeyboard();

				viewPager.setCurrentItem(1);
			}
				break;
			/*
			 * case R.id.rb_order3: { viewPager.setCurrentItem(2); } break;
			 */

			}
		}
	};
	OnPageChangeListener l_page = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0: {
				radio1.setChecked(true);

			}
				break;
			case 1: {
				radio2.setChecked(true);
			}
				break;
			/*
			 * case 2: { radio3.setChecked(true); } break;
			 */

			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private void initViewpager() {
		OrderFragment1 fragment1 = new OrderFragment1();
		OrderFragment2 fragment2 = new OrderFragment2();

		fragments.add(fragment1);
		fragments.add(fragment2);

	}

	private void HideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (((Activity) context).getCurrentFocus() != null)
			imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getWindowToken(), 0);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setCurrent(int i) {
		viewPager.setCurrentItem(i);
		setPage(i);
	}
}
