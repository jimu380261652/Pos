package com.lizhi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//1.fix  状态消失
public abstract class NoBugFragment extends Fragment {

	protected View view;

	// 当Fragment 从FragmentActvity移除
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (view != null) {
			// ViewGroup五大布局的父类
			ViewGroup parent = (ViewGroup) view.getParent();// 布局
			if (parent != null) {
				parent.removeView(view);
			}
		}
	}

	
	/**
	 * 不根据Fragment在FragmentActivity状态决定返回值 
	 * @return
	 */
	public Activity get_Activity() {
		return activity;
	}

	private Activity activity;

	// 当Fragment 从FragmentActvity添加
	// 返回当前Framgent显示的内容
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// super.getActivity()返回所在的集合FragmentActivty
		if (view == null) {
			activity = getActivity();
			view = oncreateView(inflater, container, savedInstanceState);
		}
		return view;// 1.同一个View是不能两次添加到一个布局中
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	public abstract View oncreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
	
}
