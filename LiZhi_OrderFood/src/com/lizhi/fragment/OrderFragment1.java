package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.bean.PrintDataObject;
import com.centerm.smartpos.dev.IPrintDev;
import com.centerm.smartpos.dev.impl.SmartPayFactory;
import com.duiduifu.http.UploadMessage;
import com.duiduifu.pulltorefresh.PullToRefreshBase;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.duiduifu.pulltorefresh.PullToRefreshListView;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.OrderAdapter;
import com.lizhi.adapter.OrderItemAdapter;
import com.lizhi.app.dialog.SelectPopup;
import com.lizhi.app.dialog.SelectPopup.OnItemCallBack;
import com.lizhi.app.main.Loading;
import com.lizhi.bean.ERP_Order;
import com.lizhi.bean.ERP_OrderDetail;
import com.lizhi.bean.ERP_OrderItem;
import com.lizhi.util.Constants;
import com.lizhi.util.DataUtil;
import com.lizhi.util.DateUtil;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

//线上订单
public class OrderFragment1 extends BaseFragment implements OnClickListener {

	private Context context;
	private String orderId="";//接收订单消息中的orderId
	private ReceiveBroadCast receiveBroadCast;
	private String flag = "";
	private static final int pageSize = 10;
	private int currentPage = 1;
	private Loading load;
	private String reasoncode = "";// 原因码
	// 普通订单
	private ListView lv_order;
	PullToRefreshListView pullLv_order;
	private OrderAdapter adapter_order;
	// 增值订单
	private ListView lv_order_add;
	PullToRefreshListView pullLv_order_add;
	private OrderAdapter adapter_order_add;
	// 订单详情商品列表
	private ListView lv_product;
	private OrderItemAdapter adapter_product;
	// 普通订单与增值订单
	private TextView tv_normal, tv_add;
	private View line1, line2;
	private int order_flag = 1;// 1为普通订单 2为增值订单
	// 筛选项
	private LinearLayout top;
	private TextView status, time, choose, showAll;
	// 订单详情
	private TextView order_id, order_total, order_discount, order_code,
			order_number, order_price, order_address, order_time,
			order_realPrice, order_mobile, order_payway, order_change,
			order_status;
	private Button cancel, print, send, ok, get;

	private List<ERP_Order> orders_normal;// 普通订单列表
	private int pos_order_normal = 0;// 普通订单列表选中位置
	private List<ERP_Order> orders_add;// 增值订单列表
	private int pos_order_add = 0;// 增值订单列表选中位置
	private List<ERP_Order> orders_conditon;// 条件筛选后订单列表
	private ERP_Order order;// 当前订单
	private ERP_OrderDetail order_detail;// 当前订单详情
	// 开关按钮
	// private CheckSwitchButton mCheckSwithcButton;
	// 筛选条件弹出框
	private SelectPopup pop_status, pop_time, pop_choose;
	private List<String> items_status, items_time, items_choose;
	private int pos_status = 0, pos_time = 0, pos_choose = 0;// 条件筛选位置
	private LinearLayout ll_status;
	/** 打印机设备 */
	private IPrintDev printDev = null;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_order1, container,
				false);
		return mViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		context = getActivity();
		printDev = SmartPayFactory.getPrinterDev();

		orders_add = new ArrayList<ERP_Order>();
		orders_normal = new ArrayList<ERP_Order>();
		orders_conditon = new ArrayList<ERP_Order>();
		load = new Loading(context);

		initView();
		initPopItems();
		new GetOrderTask().execute();// 获取订单
	}
	 
	@Override
	public void onResume() {
 
		super.onResume();
	 
		receiveBroadCast = new ReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.duiduifu.fragment.OrderFragment1"); // 只有持有相同的action的接受者才能接收此广播
		context.registerReceiver(receiveBroadCast, filter);
	}
	@Override
	public void onPause() {
	 
		super.onPause();
		if(receiveBroadCast!=null)
		context.unregisterReceiver(receiveBroadCast);
	}
	
	private void initView() {
		pullLv_order = (PullToRefreshListView) getView().findViewById(
				R.id.lv_order);
		lv_order = pullLv_order.getRefreshableView();
		pullLv_order_add = (PullToRefreshListView) getView().findViewById(
				R.id.lv_order_add);
		lv_order_add = pullLv_order_add.getRefreshableView();
		lv_product = (ListView) getView().findViewById(R.id.lv_order_detail);
		tv_normal = (TextView) getView().findViewById(R.id.tv_order_normal);
		tv_add = (TextView) getView().findViewById(R.id.tv_order_add);
		line1 = getView().findViewById(R.id.line1);
		line2 = getView().findViewById(R.id.line2);
		top = (LinearLayout) getView().findViewById(R.id.ll_order_top);
		status = (TextView) getView().findViewById(R.id.tv_order_status_choose);
		time = (TextView) getView().findViewById(R.id.tv_order_time_choose);
		choose = (TextView) getView().findViewById(
				R.id.tv_order_condition_choose);
		showAll = (TextView) getView().findViewById(R.id.tv_order_showall);

		order_id = (TextView) getView().findViewById(R.id.tv_order_id);
		order_total = (TextView) getView().findViewById(R.id.tv_order_total);
		order_address = (TextView) getView()
				.findViewById(R.id.tv_order_address);
		order_change = (TextView) getView().findViewById(R.id.tv_order_change);
		order_status = (TextView) getView().findViewById(R.id.tv_order_status);
		order_discount = (TextView) getView().findViewById(
				R.id.tv_order_discount);
		order_code = (TextView) getView().findViewById(R.id.tv_order_code);
		order_mobile = (TextView) getView().findViewById(R.id.tv_order_mobile);
		order_number = (TextView) getView().findViewById(R.id.tv_order_number);
		order_payway = (TextView) getView().findViewById(R.id.tv_order_payway);
		order_price = (TextView) getView().findViewById(R.id.tv_order_price);
		order_realPrice = (TextView) getView().findViewById(
				R.id.tv_order_realPrice);
		order_time = (TextView) getView().findViewById(R.id.tv_order_time);
		ok = (Button) getView().findViewById(R.id.bt_order_Ok);
		cancel = (Button) getView().findViewById(R.id.bt_order_cancel);
		get = (Button) getView().findViewById(R.id.bt_order_Get);
		print = (Button) getView().findViewById(R.id.bt_order_print);
		send = (Button) getView().findViewById(R.id.bt_order_send);
		ll_status = (LinearLayout) getView().findViewById(R.id.ll_status);
		ll_status.setVisibility(View.VISIBLE);
		/*
		 * mCheckSwithcButton = (CheckSwitchButton) getView().findViewById(
		 * R.id.mCheckSwithcButton);
		 * mCheckSwithcButton.setVisibility(View.GONE);
		 * getView().findViewById(R.id.tv_order_get).setVisibility(View.GONE);
		 * if (mCheckSwithcButton.isChecked()) { get.setVisibility(View.GONE); }
		 * else { get.setVisibility(View.VISIBLE); }
		 */
		print.setOnClickListener(this);
		/*
		 * ok.setVisibility(View.GONE); cancel.setVisibility(View.GONE);
		 * send.setVisibility(View.GONE); get.setVisibility(View.GONE);
		 */
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);

		send.setOnClickListener(this);
		get.setOnClickListener(this);
		status.setOnClickListener(this);
		time.setOnClickListener(this);
		choose.setOnClickListener(this);
		showAll.setOnClickListener(this);
		tv_normal.setOnClickListener(this);
		tv_add.setOnClickListener(this);
		lv_order.setOnItemClickListener(l_order);
		pullLv_order
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {

						currentPage++;
						new GetOrderTask().execute();
					}
				});
		pullLv_order.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						DateUtil.Date2String(new Date(), "yyyy-MM-dd HH:mm"));
				if (currentPage != 1)
					pos_order_normal = 0;
				currentPage = 1;

				orders_normal.clear();
				new GetOrderTask().execute();
			}
		});

		// 设置控件的数据
		setViewData();

	}

	// 设置控件的数据
	private void setViewData() {
		order = new ERP_Order();
		order_detail = new ERP_OrderDetail();
		adapter_order = new OrderAdapter(context, orders_normal,
				pos_order_normal);
		lv_order.setAdapter(adapter_order);
		adapter_order_add = new OrderAdapter(context, orders_add, pos_order_add);
		lv_order_add.setAdapter(adapter_order_add);
		adapter_product = new OrderItemAdapter(context,
				order_detail.getItems(), false);
		lv_product.setAdapter(adapter_product);
		DataUtil.setListViewHeight(context, lv_product, 7);
	}

	// 设置订单详情数据
	private void setOrderDetail() { 
	 
		// if ("00".equals(order_detail.getBusinessId())) {
		order_id.setText("订单编号：" + order_detail.getOrderId());//
		order_total.setText("总额：" + order_detail.getTotalAmount());
		order_discount.setText("总额折扣：");
		order_code.setText("会员号码：" + order_detail.getUserName());
		order_number.setText("最终总额：" + order_detail.getTotalAmount());
		order_price.setText("付款金额：" + order_detail.getTotalAmount());
		order_address.setText("交易时间：" + order_detail.getOrderDate());
		order_time.setText("找零金额：0");
		if (order_detail.getPaymentId().equals("1"))
			order_realPrice.setText("付款方式：货到付款");
		else if (order_detail.getPaymentId().equals("2")) {
			order_realPrice.setText("付款方式：在线支付");
		}

		order_payway.setText("订单状态：" + order_detail.getStatusName());
		// order_mobile.setText("付款方式：");
		// order_payway.setText("付款方式：");

		/*
		 * } else { order_id.setText("订单编号：" + order_detail.getOrderId());
		 * order_total.setText("总额：" + order.getTotalAmount());
		 * order_discount.setText("总额折扣："); order_code.setText("会员号码：" +
		 * order.getUserName()); order_number.setText("最终总额："
		 * +order.getTotalAmount()); order_price.setText("应付：" +
		 * order.getTotalAmount()); order_address.setText("送货地址：" +
		 * order.getShipAddress()); order_time.setText("交易时间：" +
		 * order.getOrderDate()); order_realPrice.setText("实付：");
		 * order_mobile.setText("联系方式：" + order.getShipMobile());
		 * order_payway.setText("付款金额：");
		 * 
		 * }
		 */
		// private int statusId;//订单状态 0 全部，1 待发货，2 待收货，3 成功，4 关闭
		/*
		 * if (order_detail.getStatusId().equals("1")) {
		 * order_payway.setText("订单状态："+order_detail.getStatusName()); } else if
		 * (order_detail.getStatusId().equals("2")) {
		 * order_payway.setText("订单状态：待收货"); } else if
		 * (order_detail.getStatusId().equals("3")) {
		 * order_payway.setText("订单状态：成功"); } else if
		 * (order_detail.getStatusId().equals("4")) {
		 * order_payway.setText("订单状态：关闭"); }
		 */
	}

	private OnItemClickListener l_order = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			pos_order_normal = position - 1;

			adapter_order.update(pos_order_normal);
			order = orders_normal.get(pos_order_normal);
			new GetOrderDetailTask().execute();
		}
	};

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.bt_order_Ok: {
			flag = "3";
			new SendOrderTask().execute("8");
		}
			break;
		case R.id.bt_order_Get: {
			flag = "1";// 接单
			new SendOrderTask().execute("9");
			// new GetReasonTask().execute("9");
		}
			break;
		case R.id.bt_order_cancel: {
			if (!StringUtil.isEmpty(order.getOrderId())) {
				// new CancelOrderTask().execute();
				flag = "4";
				new GetReasonTask().execute("1");
			}
		}
			break;
		case R.id.bt_order_print: {
			printOrder();
		}
			break;
		case R.id.bt_order_send: {
			if (!StringUtil.isEmpty(order.getOrderId())) {
				flag = "2";
				new SendOrderTask().execute("2");// 发货
				// new SendOrderTask().execute();

			}
		}
			break;

		case R.id.tv_order_status_choose: {
			openPopStatus();
		}
			break;
		case R.id.tv_order_time_choose: {
			openPopTime();
		}
			break;
		case R.id.tv_order_condition_choose: {
			openPopCondition();
		}
			break;
		case R.id.tv_order_showall: {
			pos_status = 0;
			pos_time = 0;
			pos_choose = 0;
			status.setText(items_status.get(0));
			time.setText(items_time.get(0));
			choose.setText(items_choose.get(0));
			if (pop_status != null) {
				pop_status.clear();
			}
			if (pop_time != null) {
				pop_time.clear();
			}
			if (pop_choose != null) {
				pop_choose.clear();
			}
			updateOrderByCondition(pos_status, pos_time, pos_choose);
		}
			break;

		// 普通订单
		case R.id.tv_order_normal: {
			pullLv_order.setVisibility(View.VISIBLE);
			pullLv_order_add.setVisibility(View.GONE);
			line1.setVisibility(View.VISIBLE);
			line2.setVisibility(View.GONE);
			order_flag = 1;
		}
			break;
		// 增值订单
		case R.id.tv_order_add: {
			pullLv_order.setVisibility(View.GONE);
			pullLv_order_add.setVisibility(View.VISIBLE);
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.VISIBLE);
			order_flag = 2;
		}
			break;
		}
	}

	private void initPopItems() {
		items_status = new ArrayList<String>();
		items_choose = new ArrayList<String>();
		items_time = new ArrayList<String>();
		items_status.add("状态");
		items_status.add("待发货");
		items_status.add("待收货");
		items_status.add("成功");
		items_status.add("关闭");
		status.setText(items_status.get(0));
		items_time.add("时间");
		items_time.add("今天");
		items_time.add("最近7天");
		items_time.add("最近30天");
		time.setText(items_time.get(0));
		items_choose.add("筛选");
		choose.setText(items_choose.get(0));
	}

	private void openPopStatus() {
		if (pop_status == null) {
			pop_status = new SelectPopup(context, items_status, 310);
			pop_status.setCallBack(new OnItemCallBack() {

				@Override
				public void onItemClick(int pos) {

					status.setText(items_status.get(pos));
					pos_status = pos;
					updateOrderByCondition(pos_status, pos_time, pos_choose);
				}
			});
			pop_status.showAsDropDown(top, 0, 0);
		} else if (pop_status.isShowing()) {
			pop_status.dismiss();
		} else {
			pop_status.showAsDropDown(top, 0, 0);
		}
	}

	private void openPopTime() {
		if (pop_time == null) {
			pop_time = new SelectPopup(context, items_time, 430);
			pop_time.setCallBack(new OnItemCallBack() {

				@Override
				public void onItemClick(int pos) {

					time.setText(items_time.get(pos));
					pos_time = pos;
					updateOrderByCondition(pos_status, pos_time, pos_choose);
				}
			});
			pop_time.showAsDropDown(top, 0, 0);
		} else if (pop_time.isShowing()) {
			pop_time.dismiss();
		} else {
			pop_time.showAsDropDown(top, 0, 0);
		}
	}

	private void openPopCondition() {
		if (pop_choose == null) {
			pop_choose = new SelectPopup(context, items_choose, 550);
			pop_choose.setCallBack(new OnItemCallBack() {

				@Override
				public void onItemClick(int pos) {

					choose.setText(items_choose.get(pos));
					pos_choose = pos;
					updateOrderByCondition(pos_status, pos_time, pos_choose);
				}
			});
			pop_choose.showAsDropDown(top, 0, 0);
		} else if (pop_choose.isShowing()) {
			pop_choose.dismiss();
		} else {
			pop_choose.showAsDropDown(top, 0, 0);
		}
	}

	// 根据订单状态筛选后更新订单列表
	private void updateOrderByCondition(int index_status, int index_time,
			int index_choose) {
		load.show();
		// 清空筛选列表
		orders_conditon.clear();
		// 筛选状态
		if (index_status == 0) {
			orders_conditon.addAll(orders_normal);
		} else {
			for (ERP_Order item : orders_normal) {
				if (item.getStatusId() == index_status) {
					orders_conditon.add(item);
				}
			}
		}
		List<ERP_Order> temp = new ArrayList<ERP_Order>();
		for (ERP_Order item : orders_conditon) {
			// 筛选时间

			Log.i("pos time", index_status + "" + index_time + ""
					+ index_choose);
			if (index_time == 1 && !DateUtil.isInDays(item.getOrderDate(), 1)) {
				temp.add(item);
			}
			if (index_time == 2 && !DateUtil.isInDays(item.getOrderDate(), 7)) {
				temp.add(item);
			}
			if (index_time == 3 && !DateUtil.isInDays(item.getOrderDate(), 30)) {
				temp.add(item);
			}

			// 筛选条件

		}
		orders_conditon.removeAll(temp);

		pos_order_normal = 0;
		load.cancel();
		if (orders_conditon.size() > 0) {
			order = orders_conditon.get(pos_order_normal);
			new GetOrderDetailTask().execute();
		}
		adapter_order.update(orders_conditon, pos_order_normal);
	}

	/**
	 * 发消息给主线程更新UI
	 * 
	 * @param what
	 */
	public void sendMessageToHandler(int what, String msg) {

		Message message = mHandler.obtainMessage();
		message.what = what;
		message.obj = msg;
		mHandler.sendMessage(message);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 没有打印纸
			case 6:
				load.cancel();
				String m3 = msg.obj.toString();
				TipsToast.makeText(context, m3, Toast.LENGTH_SHORT).show();
				break;
			// 准备失败打印机异常
			case 10:
				load.cancel();
				String m10 = msg.obj.toString();
				TipsToast.makeText(context, m10, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	};

	private void printOrder() {
		List<PrintDataObject> list = new ArrayList<PrintDataObject>();

		list.add(new PrintDataObject("商户名称" + Constants.shop.getShopName(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("商户联系方式：" + Constants.shop.getShopPhone(),
				"", "false", "left", "false", "true"));
		list.add(new PrintDataObject("交易时间：" + order_detail.getOrderDate(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("会员号：" + order_detail.getUserName(), "", "false",
				"left", "false", "true"));
		list.add(new PrintDataObject("联系方式：" + order_detail.getShipMobile(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("联系人：" + order_detail.getShipName(), "", "false",
				"left", "false", "true"));
		list.add(new PrintDataObject("配送地址：" + order_detail.getShipAddress(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("配送费：" + order_detail.getDispatchFee(),
				"", "false", "left", "false", "true"));
		list.add(new PrintDataObject("商品名称 规格 单价 数量 小计", "", "false", "left",
				"false", "true"));
		for (int i = 0; i < order_detail.getItems().size(); i++) {
			ERP_OrderItem item = new ERP_OrderItem();
			item = order_detail.getItems().get(i);
			list.add(new PrintDataObject(item.getMerchandiseName() + " "
					+ item.getUnitName() + " " + item.getPrice() + " "
					+ item.getNums() + " " + item.getAmount(), "", "false",
					"left", "false", "true"));
		}
		list.add(new PrintDataObject("总额：" + order_detail.getTotalAmount(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("合计：" + order_detail.getTotalAmount(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("付款方式：" + order_detail.getPaymentName(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("付款金额：" + order_detail.getTotalAmount(), "",
				"false", "left", "false", "true"));
		list.add(new PrintDataObject("找零金额：0", "", "false", "left", "false",
				"true"));
		list.add(new PrintDataObject("   默认计算单位：人民币（元）", "", "false", "left",
				"false", "true"));
		list.add(new PrintDataObject("   温馨提醒：请妥善保管好订单小票", "", "false", "left",
				"false", "true"));
		List<PrintDataObject> list2 = new ArrayList<PrintDataObject>();
		list2.add(new PrintDataObject("\n\n"));
		String str = null;
		try {
			str = this.printDev.print(list);
			str = this.printDev.print(list2);
			Log.i("log", "文本数据正常打印");
			list.clear();
			list2.clear();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("log", "打印文本数据出错");
		}
	}

	// 获取圈子
	private class GetOrderTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			load.show();
		}

		@Override
		protected void onPostExecute(String result) {

			load.cancel();
			pullLv_order.onRefreshComplete();
			pullLv_order_add.onRefreshComplete();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("线上订单", result);
				if (JsonUtil.isSuccess(result)) {
					if (JsonUtil.GetTotal(result) >= currentPage) {

						List<ERP_Order> orders_temp = JsonUtil
								.getOrdersNormal(result);

						if (orders_temp != null && orders_temp.size() > 0) {
							orders_normal.addAll(orders_temp);
							adapter_order.update(orders_normal,
									pos_order_normal);
						}

					}

					if (orders_normal.size() > 0) {
						order = orders_normal.get(pos_order_normal);
						if (!"".equals(order_detail.getOrderId()))
							new GetOrderDetailTask().execute();
					} else {
						// cancel, print, send, ok, get;
						cancel.setVisibility(View.GONE);
						print.setVisibility(View.GONE);
						send.setVisibility(View.GONE);
						ok.setVisibility(View.GONE);
						get.setVisibility(View.GONE);
					}
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
			map.put("status", "0");// 订单状态 0 全部，1 待发货，2 待收货，3 成功，4 关闭
			map.put("orderTypeId", "2");// app下单
			map.put("bussnessId", "00");
			map.put("currentPage", currentPage + "");
			map.put("pageSize", pageSize + "");
			map.put("orderUserId", "");
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.terminalId + Constants.shop.getUserId() + "0"
					+ currentPage + pageSize);
			map.put("sign", sign);
			try {
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Order/getOrders.do", map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	// 获取订单详细信息
	private class GetOrderDetailTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			load.show();
		}

		@Override
		protected void onPostExecute(String result) {

			load.cancel();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("pos", result);
				if (JsonUtil.isSuccess(result)) {
					order_detail = JsonUtil.getOrderDetail(result);
					setOrderDetail();
					Log.i("pos_businessid", order_detail.getBusinessId());
					Log.i("pos_detail", order_detail.getItems().get(0)
							.toString());
					if ("00".equals(order_detail.getBusinessId())) {
						adapter_product.update(order_detail.getItems());
						lv_product.setAdapter(adapter_product);
					} else {
						adapter_product.update(order_detail.getItems(),
								order_detail.getOrderItems(), true,
								order_detail.getStatusName(),
								order_detail.getMemo());
						lv_product.setAdapter(adapter_product);
					}
					String status = order_detail.getStatusId();
					String paymentId = order_detail.getPaymentId();
					print.setVisibility(View.VISIBLE);
					// private Button cancel, send, ok, get;
					/*
					 * if (mCheckSwithcButton.isChecked()) {
					 * get.setVisibility(View.GONE);
					 * cancel.setVisibility(View.VISIBLE);
					 * send.setVisibility(View.GONE);
					 * ok.setVisibility(View.GONE); } else {
					 */
					/*
					 * if (status.equals("9")) {
					 * get.setVisibility(View.VISIBLE);
					 * cancel.setVisibility(View.VISIBLE);
					 * ok.setVisibility(View.GONE);
					 * send.setVisibility(View.GONE); } else {
					 * get.setVisibility(View.GONE); if (status.equals("3") ||
					 * status.equals("4") || status.equals("8")) {
					 * get.setVisibility(View.GONE);
					 * cancel.setVisibility(View.GONE);
					 * ok.setVisibility(View.GONE);
					 * send.setVisibility(View.GONE); } else {
					 * cancel.setVisibility(View.VISIBLE);
					 * 
					 * if (status.equals("1")) {
					 * send.setVisibility(View.VISIBLE);
					 * ok.setVisibility(View.GONE); } else if
					 * (status.equals("2")) { if (paymentId.equals("1")) {
					 * ok.setVisibility(View.VISIBLE);
					 * send.setVisibility(View.GONE); } else if
					 * (paymentId.equals("2")) { ok.setVisibility(View.GONE);
					 * send.setVisibility(View.GONE); } }
					 * 
					 * } }
					 */
					// private Button cancel, send, ok, get;
					if (paymentId.equals("1")) {
						// 货到付款
						if (status.equals("2")) {// 待收货
							cancel.setVisibility(View.VISIBLE);
							send.setVisibility(View.GONE);
							ok.setVisibility(View.VISIBLE);
							get.setVisibility(View.GONE);
						}
					} else if (paymentId.equals("2")) {
						ok.setVisibility(View.GONE);
						if (status.equals("2")) {// 待收货
							cancel.setVisibility(View.VISIBLE);
							send.setVisibility(View.GONE);
							get.setVisibility(View.GONE);
						}
					}
					if (status.equals("3") || status.equals("4")
							|| status.equals("8")) {
						cancel.setVisibility(View.GONE);
						send.setVisibility(View.GONE);
						ok.setVisibility(View.GONE);
						get.setVisibility(View.GONE);
					} else if (status.equals("9")) {
						cancel.setVisibility(View.VISIBLE);
						send.setVisibility(View.GONE);
						ok.setVisibility(View.GONE);
						get.setVisibility(View.VISIBLE);
					} else if (status.equals("5")) {
						cancel.setVisibility(View.VISIBLE);
						send.setVisibility(View.GONE);
						ok.setVisibility(View.GONE);
						get.setVisibility(View.GONE);
					} else if (status.equals("1")) {
						cancel.setVisibility(View.VISIBLE);
						send.setVisibility(View.VISIBLE);
						ok.setVisibility(View.GONE);
						get.setVisibility(View.GONE);
					}
					if(Constants.message.length()>0){
						Constants.message="";
						adapter_order.update(-1);
					}
					orderId="";
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
			if(orderId.length()>0){
				map.put("orderId", orderId);
			}else if(Constants.message.length()>0){
				map.put("orderId", Constants.message);
				orderId=Constants.message;				 
			}
			else{
				map.put("orderId", order.getOrderId());
				orderId=order.getOrderId();
			}
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.terminalId + Constants.shop.getUserId()
					+ orderId);
			map.put("sign", sign);
			try {
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Order/getOrder.do", map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	// 有权限问题
	private class CancelOrderTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			load.show();
		}

		@Override
		protected void onPostExecute(String result) {

			load.cancel();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("pos", result);
				if (JsonUtil.isSuccess(result)) {

					orders_normal.remove(order);
					adapter_order.update(orders_normal, pos_order_normal);
					if (pos_order_normal == adapter_order.getCount() - 1) {
						pos_order_normal = 0;
						adapter_order.update(pos_order_normal);
					}

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
			map.put("orderId", order.getOrderId());
			map.put("person", "2");
			map.put("processCode", "1");
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.terminalId + Constants.shop.getUserId()
					+ order.getOrderId());
			map.put("sign", sign);
			try {
				// http://192.168.2.190:8080//community-api/shopterminal/Order/reasonList.do
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Order/reasonList.do", map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	// 发货
	private class SendOrderTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			load.show();
		}

		@Override
		protected void onPostExecute(String result) {

			load.cancel();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {

				Log.i("pos", result);
				if (JsonUtil.isSuccess(result)) {
					if (flag.equals("2")) {
						TipsToast.makeText(context, "发货成功", Toast.LENGTH_SHORT)
								.show();
						send.setVisibility(View.GONE);

						if (order_detail.getPaymentId().equals("1"))
							ok.setVisibility(View.VISIBLE);
						else if (order_detail.getPaymentId().equals("2")) {
							ok.setVisibility(View.GONE);
						}
					} else if (flag.equals("1")) {
						TipsToast.makeText(context, "接单成功", Toast.LENGTH_SHORT)
								.show();

						get.setVisibility(View.GONE);
						send.setVisibility(View.VISIBLE);
						printOrder();
					} else if (flag.equals("3")) {

						TipsToast.makeText(context, "成功", Toast.LENGTH_SHORT)
								.show();

						cancel.setVisibility(View.GONE);

						ok.setVisibility(View.GONE);
					}
					else if (flag.equals("4")) {

						TipsToast.makeText(context, "取消成功", Toast.LENGTH_SHORT)
								.show();

						/*cancel.setVisibility(View.GONE);

						ok.setVisibility(View.GONE);*/
					}
					new GetOrderDetailTask().execute();
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
			map.put("orderId", order.getOrderId());
			map.put("processCode", params[0]);
			map.put("fileUrl", "");
			map.put("content", "");
			if (reasoncode.length() > 0) {
				map.put("reason", reasoncode.split(",")[0]);
				reasoncode=reasoncode.split(",")[0];
			} else {

				map.put("reason", "");
				//reasoncode = "";
			}
			map.put("processer", "2");
			map.put("channelId", "04");
			String sign = MD5Util.createSign(Constants.terminalId
					+ Constants.loginSign + Constants.shop.getUserId()
					+ order.getOrderId() + params[0] + reasoncode + "" + ""
					+ "04");
			reasoncode = "";
			map.put("sign", sign);
			 
			try {
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Order/processOrder.do", map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	public class GetReasonTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			load.show();
		}

		@Override
		protected void onPostExecute(String result) {

			load.cancel();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("pos", result);
				if (JsonUtil.isSuccess(result)) {

					try {
						JSONArray array = new JSONObject(result)
								.getJSONArray("reasonList");
						for (int i = 0; i < array.length(); i++) {
							reasoncode = reasoncode
									+ array.getJSONObject(i).getString(
											"reasonCode") + ",";
						}
						/*
						 * if (!flag) new SendOrderTask().execute("9"); else
						 */
						new SendOrderTask().execute("1");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						TipsToast
								.makeText(context, JsonUtil.getMessage(result),
										Toast.LENGTH_SHORT).show();
					}
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
			map.put("orderId", order.getOrderId());
			// order.getOrderId()
			map.put("person", "2");
			map.put("processCode", "1");
			String sign = MD5Util.createSign(Constants.terminalId,
					Constants.loginSign, Constants.shop.getUserId(), "2", "1",
					order.getOrderId());
			map.put("sign", sign);
			try {
				String result = UploadMessage.sendPost(AppConstantByUtil.HOST
						+ "Order/reasonList.do", map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}
		

	}
	class ReceiveBroadCast extends BroadcastReceiver {
	 
		@Override
		public void onReceive(Context context, Intent intent) {
			orderId= intent.getExtras().getString("orderId");
			if(orderId.length()<=0){
				currentPage = 1;
				orders_normal.clear();
				new GetOrderTask().execute();
			}else{
				adapter_order.update(-1);
				new GetOrderDetailTask().execute();
			}

		}
	}
	//消息界面跳转
	/*public void ShowMessage(String str){
		order.setOrderId(str);
		adapter_order.update(-1);		
		new GetOrderDetailTask().execute();
	}*/
	//刷新界面
	public void refresh(){
		currentPage = 0;
		orders_normal.clear();
		new GetOrderTask().execute();
		
	}
 
}
