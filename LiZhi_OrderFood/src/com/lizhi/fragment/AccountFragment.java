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
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lizhi.adapter.MyFragmentPagerAdapter;
import com.lizhi.widget.UnderlinePageIndicator;
import com.pos.lizhiorder.R;
//收银台
public class AccountFragment extends BaseFragment{
	private Context context;
	private List<Fragment> fragments;
	private RadioGroup group;
	private RadioButton running,recharge,withdraw;
	private ViewPager viewPager;
	private MyFragmentPagerAdapter adapter;
	private UnderlinePageIndicator indicator;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_account,container, false);
		
		return mViewGroup;
	}
	@Override  
	public void onActivityCreated(Bundle savedInstanceState) {  
		super.onActivityCreated(savedInstanceState);  
		context=getActivity();        
		fragments=new ArrayList<Fragment>();
		initView();
	}
	public void initView(){
		group=(RadioGroup)getView().findViewById(R.id.rg_account);
		running=(RadioButton)getView().findViewById(R.id.rb_running);
		recharge=(RadioButton)getView().findViewById(R.id.rb_recharge);
		withdraw=(RadioButton)getView().findViewById(R.id.rb_withdraw);
		
		viewPager=(ViewPager) getView().findViewById(R.id.vp_account);
		indicator=(UnderlinePageIndicator) getView().findViewById(R.id.indicator_account);
		group.setOnCheckedChangeListener(l_radio);
		adapter=new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), viewPager, fragments);
		initViewpager();
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(l_page);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
	}
	private void initViewpager() {
		AccountFragment1 fragment1=new AccountFragment1();
		AccountFragment2 fragment2=new AccountFragment2();
		AccountFragment3 fragment3=new AccountFragment3();
		fragments.add(fragment1);
		fragments.add(fragment2);
		fragments.add(fragment3);
	}
	OnCheckedChangeListener l_radio=new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			switch(arg1){
			case R.id.rb_running:
				viewPager.setCurrentItem(0);
				break;
			case R.id.rb_recharge:
				viewPager.setCurrentItem(1);
				break;
			case R.id.rb_withdraw:
				viewPager.setCurrentItem(2);
				break;
			}
		}	
	};
	OnPageChangeListener l_page=new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			switch(arg0){
			case 0:{
				running.setChecked(true);
			}
			break;
			case 1:{
				recharge.setChecked(true);
			}
			break;
			case 2:{
				withdraw.setChecked(true);
			}
			break;
			}
			
		}
		
	};
}
