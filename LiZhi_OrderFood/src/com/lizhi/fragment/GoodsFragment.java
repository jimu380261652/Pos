package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lizhi.adapter.MyFragmentPagerAdapter;
import com.lizhi.widget.UnderlinePageIndicator;
import com.pos.lizhiorder.R;

//商品管理
public class GoodsFragment extends NoBugFragment implements OnClickListener{
 
	private Context context;

	private RadioButton radio1, radio2, radio3;
	private ImageView classify;
	private RadioGroup group;
 
	private ViewPager viewPager;
	private UnderlinePageIndicator indicator;
	private MyFragmentPagerAdapter adapter;
	private List<Fragment> fragments;
	private CallBack callback;
	OnCheckedChangeListener l_radio = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			switch (arg1) {
			case R.id.rb_goods1: {

				viewPager.setCurrentItem(0);

			}
				break;

			case R.id.rb_goods2: {

				viewPager.setCurrentItem(1);

			}
				break;
			case R.id.rb_goods3: {

				viewPager.setCurrentItem(2);

			}
				break;
	 
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
			case 2: {
				radio3.setChecked(true);
			}
				break;
			}
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_goods, null);
		
		return view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = get_Activity();
		fragments = new ArrayList<Fragment>();
		initView();
	}
	private void initView() {
		classify=(ImageView) view.findViewById(R.id.iv_classify);
		classify.setOnClickListener(this);
		group = (RadioGroup) getView().findViewById(R.id.rg_goods);
		radio1 = (RadioButton) getView().findViewById(R.id.rb_goods1);
		radio2 = (RadioButton) getView().findViewById(R.id.rb_goods2);
		radio3 = (RadioButton) getView().findViewById(R.id.rb_goods3);
		viewPager = (ViewPager) getView().findViewById(R.id.vp_goods);
		indicator = (UnderlinePageIndicator) getView().findViewById(
				R.id.indicator_goods);
		 
		initViewpager();
		adapter = new MyFragmentPagerAdapter(getActivity()
				.getSupportFragmentManager(), viewPager, fragments);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(l_page);
		group.setOnCheckedChangeListener(l_radio);
	}

	private void initViewpager() {
		GoodsFragment1 frag1 = new GoodsFragment1();
		GoodsFragment2 frag2 = new GoodsFragment2();
		GoodsFragment3 frag3 = new GoodsFragment3();
		GoodsFragment4 frag4 = new GoodsFragment4();
		fragments.add(frag1);
		fragments.add(frag2);
		fragments.add(frag3);
		fragments.add(frag4);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_classify:{//界面跳转
			callback.callback();
		}
			break;
		default:
			break;
		}
	}
	public interface CallBack{
		public void callback();
	}
	public void setCallback(CallBack callback) {
		this.callback = callback;
	}
}