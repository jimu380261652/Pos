package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.lizhi.adapter.MyFragmentPagerAdapter;
import com.lizhi.bean.ERP_Member;
import com.lizhi.util.Constants;
import com.lizhi.util.JsonUtil;
import com.lizhi.widget.UnderlinePageIndicator;
import com.pos.lizhiorder.R;

//会员管理
public class MemberFragment extends BaseFragment {
	private Context context;
	private List<Fragment> fragments;
	private RadioGroup group;
	private RadioButton add, list;
	private UnderlinePageIndicator indicator;
	private ViewPager viewPager;
	private MyFragmentPagerAdapter adapter;
	private EditText et_search;
	private Handler mMainHadler;
	private ERP_Member temp;
	private Fragment fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_member, container,
				false);

		return mViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		fragments = new ArrayList<Fragment>();
		initView();
	}

	public void initView() {
		group = (RadioGroup) getView().findViewById(R.id.rg_member);
		add = (RadioButton) getView().findViewById(R.id.rb_member_add);
		list = (RadioButton) getView().findViewById(R.id.rb_member_list);
		indicator = (UnderlinePageIndicator) getView().findViewById(
				R.id.indicator_member);
		viewPager = (ViewPager) getView().findViewById(R.id.vp_member);
		et_search = (EditText) getView().findViewById(R.id.et_order_search);

		adapter = new MyFragmentPagerAdapter(getActivity()
				.getSupportFragmentManager(), viewPager, fragments);
		group.setOnCheckedChangeListener(l_radio);
		initViewpager();
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(l_page);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		et_search.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(
								v.getApplicationWindowToken(), 0);
					}
					String search = et_search.getText().toString();
					if (TextUtils.isEmpty(search)
							|| !(search.matches("^[0-9]*$"))) {
						Toast.makeText(context, "不可为空且需为位数字", 1).show();

					} else {

						Search(search);
						return false;
					}

				}
				return false;
			}
		});
		mMainHadler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					MemberFragment2 mem = (MemberFragment2) getActivity()
							.getSupportFragmentManager().findFragmentByTag(
									"MemberFragment2");
					mem.setMemberDetail(temp);

					break;
				}
			}
		};
	}

	/**
	 * 发消息给主线程更新UI
	 * 
	 * @param what
	 */
	public void sendMessageToHandler(int what, String msg) {

		Message message = mMainHadler.obtainMessage();
		message.what = what;
		message.obj = msg;
		mMainHadler.sendMessage(message);
	}

	public void Search(String search) {
		String terminalId = Constants.terminalId;
		String userId = Constants.shop.getUserId();

		String userName = Constants.shop.getUserName();
		String loginSign = Constants.shop.getLoginSign();
		String sign = MD5Util.createSign(loginSign + userId + terminalId
				+ search);
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("terminalId", terminalId);
		mHashMap.put("userId", userId);
		mHashMap.put("loginSign", loginSign);
		mHashMap.put("memberId", search);
		mHashMap.put("sign", sign);

		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "Member/memberDetail.do", "GET",
				mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onComplete(String values) {

						try {
							JSONObject jsonObject = new JSONObject(values);

							String msg = jsonObject.getString("message");

							String code = jsonObject.getString("code");
							Log.i("pos", values);
							if (code.equals(String
									.valueOf(AppConstantByUtil.SUCCESS))) {
								temp = JsonUtil.getMemberDetail(values);
								if (temp != null
										&& temp.getMobile().length() == 11) {
									sendMessageToHandler(1, msg);
								}
							} else if (code.equals("-1")) {
								Toast.makeText(context, "不是会员了",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onException(Object exceptionInfo) {

					}

				});
	}

	OnCheckedChangeListener l_radio = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {

			switch (arg1) {
			case R.id.rb_member_add:
				et_search.setVisibility(View.GONE);
				HideKeyboard();
				viewPager.setCurrentItem(1);

				break;
			case R.id.rb_member_list:
				et_search.setVisibility(View.VISIBLE);
				HideKeyboard();
				viewPager.setCurrentItem(0);
				break;
			}
		}

	};

	private void initViewpager() {
		MemberFragment1 fragment1 = new MemberFragment1();
		MemberFragment2 fragment2 = new MemberFragment2();

		fragments.add(fragment2);
		fragments.add(fragment1);
	}

	OnPageChangeListener l_page = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

			switch (arg0) {
			case 1:

				add.setChecked(true);
				break;
			case 0:

				list.setChecked(true);
				break;
			}
		}

	};

	private void HideKeyboard() {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (((Activity) context).getCurrentFocus() != null)
			imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getWindowToken(), 0);

		/*
		 * if (imm.isActive()) { // 一直是true
		 * 
		 * imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		 * InputMethodManager.HIDE_IMPLICIT_ONLY);
		 * 
		 * getActivity().getWindow().setSoftInputMode(
		 * WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); }
		 */
	}
}
