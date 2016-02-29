package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duiduifu.http.UploadMessage;
import com.duiduifu.pulltorefresh.PullToRefreshBase;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.duiduifu.pulltorefresh.PullToRefreshListView;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.MemberAdapter;
import com.lizhi.app.dialog.ShopRecordDialog;
import com.lizhi.bean.ERP_Member;
import com.lizhi.util.Constants;
import com.lizhi.util.DateUtil;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

public class MemberFragment2 extends BaseFragment {
	private Context context;

	private ListView memberList;
	private MemberAdapter adapter;
	PullToRefreshListView pullLv_order;
	private   int pageSize = 10;
	private int currentPage = 1;
	private List<ERP_Member> members;

	private int memberSelect = 0;
	private ERP_Member member;
	private TextView memberId, createTime, mobile, memberType, address,
			memoName, level, email, realName, credits;
	private Button record_btn;
	private ShopRecordDialog dialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle SavedInstanceState) {
		View MyViewGroup = inflater.inflate(R.layout.fragment_member2,
				container, false);
		return MyViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		initView();
	}

	public void initView() {
		member = new ERP_Member();
		pullLv_order = (PullToRefreshListView) getView().findViewById(
				R.id.lv_member2_left);
		memberList = pullLv_order.getRefreshableView();
		members = new ArrayList<ERP_Member>();
		adapter = new MemberAdapter(context, members);
		new memberTask().execute();
		memberList.setAdapter(adapter);
		memberId = (TextView) getView().findViewById(R.id.tv_member2_id);
		createTime = (TextView) getView().findViewById(
				R.id.tv_member2_createTime);
		mobile = (TextView) getView().findViewById(R.id.tv_member2_mobile);
		memberType = (TextView) getView().findViewById(
				R.id.tv_member2_memberType);
		address = (TextView) getView().findViewById(R.id.tv_member2_address);
		memoName = (TextView) getView().findViewById(R.id.tv_member2_memoName);
		level = (TextView) getView().findViewById(R.id.tv_member2_level);
		email = (TextView) getView().findViewById(R.id.tv_member2_email);
		realName = (TextView) getView().findViewById(R.id.tv_member2_realName);
		credits = (TextView) getView().findViewById(R.id.tv_member2_credits);
		memberList.setOnItemClickListener(l_member);
		record_btn = (Button) getView().findViewById(R.id.record_btn);
		record_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowDialog();
			}
		});
		pullLv_order
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {

						currentPage++;
						new memberTask().execute();
					}
				});
		pullLv_order.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						DateUtil.Date2String(new Date(), "yyyy-MM-dd HH:mm"));

				currentPage = 0;
				memberSelect = 0;
				members.clear();
				new memberTask().execute();
			}
		});
	}

	// 显示弹出框
	public void ShowDialog() {
		dialog = new ShopRecordDialog(context, member.getUserId());
		dialog.show();
		Log.i("orderUserId", member.getUserId());
		/*
		 * SearchByBarcodeDialog dialogFragment = SearchByBarcodeDialog
		 * .newInstance(barcode, false, null);
		 * dialogFragment.show(getActivity().getSupportFragmentManager(),
		 * "SearchByBarcodeDialog");
		 */
	}

	private class memberTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			pullLv_order.onRefreshComplete();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				if (JsonUtil.isSuccess(result)) {
					Log.i("MemberFragment2", result);
					List<ERP_Member> temp = JsonUtil.getMemberList(result);
					if (temp != null && temp.size() > 0) {
						members.addAll(temp);
						adapter.update(members);
					}
					if (members.size() > 0) {
						member = members.get(memberSelect);
						if (!"".equals(member.getMemberId()))
							new GetMemberDetailTask().execute();
					}
				}

			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {

			String sign = MD5Util.createSign(Constants.shop.getLoginSign()
					+ Constants.terminalId + Constants.shop.getUserId() + "");
			HashMap<String, String> mHashMap = new HashMap<String, String>();
			mHashMap.put("terminalId", Constants.terminalId);
			mHashMap.put("mobile", "");
			mHashMap.put("userId", Constants.shop.getUserId());
			mHashMap.put("pageSize", pageSize + "");
			mHashMap.put("currentPage", currentPage + "");
			mHashMap.put("loginSign", Constants.shop.getLoginSign());
			mHashMap.put("sign", sign);
			try {
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Member/queryMemberList.do", mHashMap, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	private OnItemClickListener l_member = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			memberSelect = position-1;
			member = members.get(memberSelect);
			new GetMemberDetailTask().execute();
		}
	};

	private class GetMemberDetailTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(String result) {

			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("MemberFragment2", result);
				if (JsonUtil.isSuccess(result)) {
					member = JsonUtil.getMemberDetail(result);
					setMemberDetail();
				} else {
					TipsToast.makeText(context, JsonUtil.getMessage(result),
							Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {

			Map<String, String> map = new HashMap<String, String>();
			map.put("terminalId", Constants.terminalId);
			map.put("userId", Constants.shop.getUserId());
			map.put("loginSign", Constants.loginSign);
			map.put("memberId", member.getMemberId());
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.shop.getUserId() + Constants.terminalId
					+ member.getMemberId());
			map.put("sign", sign);
			try {
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Member/memberDetail.do", map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	private void setMemberDetail() {
		memberId.setText(member.getMemberId());
		createTime.setText(member.getCreateTime());
		mobile.setText(member.getMobile());
		memberType.setText(member.getMemberType());
		address.setText(member.getAddress());
		memoName.setText(member.getMemoName());
		level.setText(member.getLevel());
		email.setText(member.getEmail());
		realName.setText(member.getRealName());
		credits.setText(member.getCredits());
	}

	public void setMemberDetail(ERP_Member member) {
		memberId.setText(member.getMemberId());
		createTime.setText(member.getCreateTime());
		mobile.setText(member.getMobile());
		memberType.setText(member.getMemberType());
		address.setText(member.getAddress());
		memoName.setText(member.getMemoName());
		level.setText(member.getLevel());
		email.setText(member.getEmail());
		realName.setText(member.getRealName());
		credits.setText(member.getCredits());
	}

	public void setMember() {
		pageSize = 10;
		if(currentPage!=1)
			memberSelect = 0;
		currentPage = 1;	
		members.clear();
		new memberTask().execute();
	}
}
