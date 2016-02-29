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
//消息管理
public class MessageFragment extends BaseFragment{	
	private Context context;
	private List<Fragment> fragments;
	private RadioGroup group;
	private RadioButton order,system;
	private UnderlinePageIndicator indicator;
	private ViewPager viewPager;
	private MyFragmentPagerAdapter adapter;
	private Callback callback;
	public int flag=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_message,container, false);
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
		group=(RadioGroup)getView().findViewById(R.id.rg_message);
		order=(RadioButton)getView().findViewById(R.id.rb_order);
		//system=(RadioButton)getView().findViewById(R.id.rb_system);
		indicator=(UnderlinePageIndicator)getView().findViewById(R.id.indicator_message);
		viewPager=(ViewPager) getView().findViewById(R.id.vp_message);
		adapter=new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(), viewPager, fragments);
		group.setOnCheckedChangeListener(l_radio);
		initViewpager();
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(l_page);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
	}
	OnCheckedChangeListener l_radio=new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			
			switch(arg1){
			case R.id.rb_order:
				viewPager.setCurrentItem(0);
				break;
		/*	case R.id.rb_system:
				viewPager.setCurrentItem(1);
				break;*/
			}
		}
		
	};
	private void initViewpager() {
		MessageFragment1 fragment1=new MessageFragment1();
	//	MessageFragment2 fragment2=new MessageFragment2();
		fragment1.setCallback(new MessageFragment1.Callback() {
			
			@Override
			public void callback() {
				  
				callback.callback();
			}
		});
		fragments.add(fragment1);
		//fragments.add(fragment2);
	}
	OnPageChangeListener l_page=new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			
		}

		@Override
		public void onPageSelected(int arg0) {
			
			switch(arg0){
			case 0:
				order.setChecked(true);
				MessageFragment.this.setFlag(1);
				break;
			/*case 1:
				system.setChecked(true);
				break;
			*/
			}
		}
		
	};
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}
	public interface Callback{
		public void callback();
	}
}
