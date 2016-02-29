package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.pulltorefresh.PullToRefreshBase;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.duiduifu.pulltorefresh.PullToRefreshListView;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.lizhi.adapter.AccountAdapter;
import com.lizhi.app.dialog.SelectPopup;
import com.lizhi.app.dialog.SelectPopup.OnItemCallBack;
import com.lizhi.app.main.Loading;
import com.lizhi.bean.ERP_Account;
import com.lizhi.util.Constants;
import com.lizhi.util.DateUtil;
import com.lizhi.util.JsonUtil;
import com.pos.lizhiorder.R;

public class AccountFragment1 extends BaseFragment {
	private Context context;
	private int totalpage = 0;// 总页数
	private TextView select, balance;
	private LinearLayout top;
	private ListView running;
	private AccountAdapter adapter;
	private SelectPopup pop_select;// 选择项弹出框
	private List<String> selectItems;
	private int pageSize = 10, currentPage = 1;
	private List<ERP_Account> aList, eaPay, eaIncome;
	private Handler mMainHadler;
	private PullToRefreshListView pulllv_pro;
	private ERP_Account ea;
	private Loading loading;
	List<ERP_Account> TempList=new ArrayList<ERP_Account>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_account1,
				container, false);
		return mViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		initSelectItems();
		initView();
	}

	private void initView() {
		loading=new Loading(context);
		pulllv_pro = (PullToRefreshListView) getView().findViewById(
				R.id.lv_running);
		eaIncome = new ArrayList<ERP_Account>();
		eaPay = new ArrayList<ERP_Account>();
		aList = new ArrayList<ERP_Account>();
		balance = (TextView) getView().findViewById(R.id.tv_account1_balance);
		running = pulllv_pro.getRefreshableView();
		select = (TextView) getView().findViewById(R.id.tv_account1_select);
		top = (LinearLayout) getView().findViewById(R.id.ll_account1_top);
		select.setOnClickListener(l);
		// aList=getData();
		adapter = new AccountAdapter(context, aList);
		running.setAdapter(adapter);
		mMainHadler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if(loading.isShowing()){
						loading.dismiss();
					}
					adapter.update(aList);
					running.setAdapter(adapter);
					for (int i = 0; i < TempList.size(); i++) {
						if (aList.get(i).getAMOUNT() < 0) {
							eaPay.add(aList.get(i));
						} else {
							eaIncome.add(aList.get(i));
						}
					}
					TempList.clear();
					pulllv_pro.onRefreshComplete();
					break;
				default:
					break;
				}

			}
		};
		pulllv_pro
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						if (currentPage < totalpage) {
							currentPage++;
							accountOpen();
						}

						// listener.setpage(currentPage);

					}
				});
		pulllv_pro.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						DateUtil.Date2String(new Date(), "yyyy-MM-dd HH:mm"));

				currentPage = 1;

				aList.clear();
				eaPay.clear();
				eaIncome.clear();
				accountOpen();
			}
		});
		accountOpen();

	}

	// private void isPay() {
	//
	// for(int i=0;i<aList.size();i++){
	// ea=aList.get(i);
	// String temp=ea.getOPERATION();
	// if(temp.equals("2")&&temp.equals("5")&&temp.equals("8"));
	// eaIncome.add(ea);
	// }

	// }
	private void accountOpen() {
		loading.show();
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("userId", Constants.shop.getUserId());
		mHashMap.put("loginSign", Constants.loginSign);
		// mHashMap.put("shopId", Constants.shop.getShopId());
		mHashMap.put("pageSize", pageSize + "");
		mHashMap.put("currentPage", currentPage + "");
		// mHashMap.put("accountType", "1");
		// mHashMap.put("queryDate","30");
		String sign = MD5Util.createSign(Constants.loginSign
				+ Constants.terminalId + pageSize+ currentPage + Constants.shop.getUserId()
				+ "");
		mHashMap.put("sign", sign);
		Log.i("account", Constants.shop.getLoginSign());
		final int temp = currentPage;
		AsyncRunner
				.getInstance()
				.request(
						AppConstantByUtil.IP
								+ "lizi-api/shop/Account/queryAccountJournalInfo.do",
						"GET", mHashMap.entrySet(), new AsyncListener() {

							@Override
							public void onComplete(String values) {

								try {
									JSONObject jsonObject = new JSONObject(
											values);
									
									String msg = jsonObject
											.getString("message");

									String code = jsonObject.getString("code");
									if (code.equals(String
											.valueOf(AppConstantByUtil.SUCCESS))) {
										totalpage = Integer.parseInt(jsonObject
												.getString("totalpage"));
										
										if (Integer.parseInt(jsonObject
												.getString("totalpage")) >=temp  ) {
											//List<ERP_Account> TempList=new ArrayList<ERP_Account>();
											TempList = JsonUtil.getAccount(values);
											aList.addAll(TempList);
											sendMessageToHandler(1, "");
										}
										
									} else {

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

	OnClickListener l = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.tv_account1_select:
				openPop();
			}
		}
	};

	private void initSelectItems() {
		selectItems = new ArrayList<String>();
		selectItems.add("全部");
		selectItems.add("收入");
		selectItems.add("支出");
	}

	private void openPop() {
		if (pop_select == null) {
			pop_select = new SelectPopup(context, selectItems, 400);
			pop_select.setCallBack(new OnItemCallBack() {

				@Override
				public void onItemClick(int pos) {

					select.setText(selectItems.get(pos));
					if (selectItems.get(pos).equals("全部")) {
						pulllv_pro.setPullToRefreshEnabled(true);
						adapter.update(aList);
					} else if (selectItems.get(pos).equals("支出")) {
						pulllv_pro.setPullToRefreshEnabled(false);
						adapter.update(eaPay);
					} else {
						pulllv_pro.setPullToRefreshEnabled(false);
						adapter.update(eaIncome);
					}
					running.setAdapter(adapter);
				}
			});
			pop_select.showAsDropDown(top, 0, 0);
		} else if (pop_select.isShowing()) {
			pop_select.dismiss();
		} else {
			pop_select.showAsDropDown(top, 0, 0);
		}
	}

	public void sendMessageToHandler(int what, String msg) {

		Message message = mMainHadler.obtainMessage();
		message.what = what;
		message.obj = msg;
		mMainHadler.sendMessage(message);
	}
	/*
	 * private List<ERP_Account> getData(){ List<ERP_Account> accounts=new
	 * ArrayList<ERP_Account>(); ERP_Account temp=new ERP_Account();
	 * temp.setACCOUNT_BALANCE(250.00f);
	 * temp.setACCOUNT_JOURNAL_ID("2015050216584"); temp.setAMOUNT(-50.00f);
	 * temp.setCREATE_TIME("2015-08-04 14:23"); temp.setOPERATION("话费充值");
	 * accounts.add(temp); temp=new ERP_Account();
	 * temp.setACCOUNT_BALANCE(254.00f);
	 * temp.setACCOUNT_JOURNAL_ID("2015050216584"); temp.setAMOUNT(4.00f);
	 * temp.setCREATE_TIME("2015-08-01 12:00"); temp.setOPERATION("分润");
	 * accounts.add(temp); temp=new ERP_Account();
	 * temp.setACCOUNT_BALANCE(354.00f);
	 * temp.setACCOUNT_JOURNAL_ID("2015050216584"); temp.setAMOUNT(100.00f);
	 * temp.setCREATE_TIME("2015-07-24 16:13"); temp.setOPERATION("账户充值");
	 * accounts.add(temp); return accounts; }
	 */
}
