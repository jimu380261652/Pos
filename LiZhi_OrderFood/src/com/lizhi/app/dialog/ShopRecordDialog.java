package com.lizhi.app.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.lizhi.adapter.OrderAdapter;
import com.lizhi.adapter.OrderItemAdapter;
import com.lizhi.app.dialog.SelectPopup.OnItemCallBack;
import com.lizhi.app.main.Loading;
import com.lizhi.bean.ERP_Order;
import com.lizhi.bean.ERP_OrderDetail;
import com.lizhi.util.Constants;
import com.lizhi.util.DataUtil;
import com.lizhi.util.DateUtil;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

public class ShopRecordDialog extends BaseDialog implements
		android.view.View.OnClickListener {
	private Context context;
	private String orderUserId = "";

	private static final int pageSize = 10;
	private int currentPage = 1;
	private Loading load;
	// 普通订单
	private ListView lv_order;
	PullToRefreshListView pullLv_order;
	private OrderAdapter adapter_order;

	// 订单详情商品列表
	private ListView lv_product;
	private OrderItemAdapter adapter_product;
	// 普通订单与增值订单
	private TextView tv_normal;

	private int order_flag = 1;// 1为普通订单 2为增值订单
	// 筛选项
	private LinearLayout top;
	private TextView status, time, choose, showAll;
	// 订单详情
	private TextView order_id, order_total, order_discount, order_code,
			order_number, order_price, order_address, order_time,
			order_realPrice, order_mobile, order_payway, order_change,
			order_status;

	private List<ERP_Order> orders_normal;// 普通订单列表
	private int pos_order_normal = 0;// 普通订单列表选中位置

	private List<ERP_Order> orders_conditon;// 条件筛选后订单列表
	private ERP_Order order;// 当前订单
	private ERP_OrderDetail order_detail;// 当前订单详情

	// 筛选条件弹出框
	private SelectPopup pop_status, pop_time, pop_choose,pop_order;
	private List<String> items_status, items_time, items_choose, items_order;
	private int pos_status = 0, pos_time = 0, pos_choose = 0,pos_order=0;// 条件筛选位置
	private LinearLayout ll_status;
	// 订单类型，订单来源
	private String bussnessId, orderTypeId,Ostatus;

	public ShopRecordDialog(Context context, String orderUserId) {
		super(context);
		this.orderUserId = orderUserId;
		this.context = context;
		initUI();
	}

	public interface ShopRecordListener {

	}

	public void initUI() {
		View v = getLayoutInflater().inflate(R.layout.dialog_record, null);
		orders_normal = new ArrayList<ERP_Order>();
		orders_conditon = new ArrayList<ERP_Order>();
		load = new Loading(context);

		pullLv_order = (PullToRefreshListView) v.findViewById(R.id.lv_order);
		lv_order = pullLv_order.getRefreshableView();

		lv_product = (ListView) v.findViewById(R.id.lv_order_detail);
		tv_normal = (TextView) v.findViewById(R.id.tv_order_normal);

		top = (LinearLayout) v.findViewById(R.id.ll_order_top);
		status = (TextView) v.findViewById(R.id.tv_order_status_choose);
		time = (TextView) v.findViewById(R.id.tv_order_time_choose);
		choose = (TextView) v.findViewById(R.id.tv_order_condition_choose);
		showAll = (TextView) v.findViewById(R.id.tv_order_showall);

		order_id = (TextView) v.findViewById(R.id.tv_order_id);
		order_total = (TextView) v.findViewById(R.id.tv_order_total);
		order_address = (TextView) v.findViewById(R.id.tv_order_address);
		order_change = (TextView) v.findViewById(R.id.tv_order_change);
		order_status = (TextView) v.findViewById(R.id.tv_order_status);
		order_discount = (TextView) v.findViewById(R.id.tv_order_discount);
		order_code = (TextView) v.findViewById(R.id.tv_order_code);
		order_mobile = (TextView) v.findViewById(R.id.tv_order_mobile);
		order_number = (TextView) v.findViewById(R.id.tv_order_number);
		order_payway = (TextView) v.findViewById(R.id.tv_order_payway);
		order_price = (TextView) v.findViewById(R.id.tv_order_price);
		order_realPrice = (TextView) v.findViewById(R.id.tv_order_realPrice);
		order_time = (TextView) v.findViewById(R.id.tv_order_time);

		ll_status = (LinearLayout) v.findViewById(R.id.ll_status);
		ll_status.setVisibility(View.VISIBLE);

		status.setOnClickListener(this);
		time.setOnClickListener(this);
		choose.setOnClickListener(this);
		showAll.setOnClickListener(this);
		tv_normal.setOnClickListener(this);

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
				if (currentPage != 0)
					pos_order_normal = 0;
				currentPage = 0;

				orders_normal.clear();
				new GetOrderTask().execute();
			}
		});

		// 设置控件的数据
		setViewData();
		initPopItems();
		bussnessId = "00";
		orderTypeId = "2";
		Ostatus="0";
		new GetOrderTask().execute();// 获取订单
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(900,
				900);
		this.addContentView(v, lp);
	}
	// 设置订单详情数据
		private void setOrderDetail() {
			/* if ("00".equals(order_detail.getBusinessId())) { */
			order_id.setText("订单编号：" + order_detail.getOrderId());
			order_total.setText("原价总额：" + order_detail.getTotalAmount());
			order_discount.setText("总额折扣：");
			order_code.setText("会员号码：" + order.getUserName());
			order_number.setText("商品数量：" + order.getNums());
			if (order_detail.getPaymentId().equals("3")) {
				BigDecimal bd = new BigDecimal(Double.parseDouble(order_detail
						.getTotalAmount()));
				bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
				order_price.setText("订单金额："
						+bd);
			} else
				order_price.setText("订单金额：" + order_detail.getTotalAmount());
			order_address.setText("送货地址：" + order.getShipAddress());
			order_time.setText("交易时间：" + order.getOrderDate());
			order_realPrice.setText("付款金额：");
			order_mobile.setText("联系方式：" + order.getShipMobile());
			if (order_detail.getPaymentId().equals("5"))
				order_payway.setText("付款方式：刷卡支付");
			else if (order_detail.getPaymentId().equals("3")) {
				order_payway.setText("付款方式：现金支付");
			} else if (order_detail.getPaymentId().equals("4")) {
				order_payway.setText("付款方式：备付金支付");
			} else if (order_detail.getPaymentId().equals("2")) {
				order_payway.setText("付款方式：在线支付");
			} else if (order_detail.getPaymentId().equals("1"))
				order_payway.setText("付款方式：货到付款");
			/*else if (order_detail.getPaymentId().equals("2")) {
				order_payway.setText("付款方式：在线支付");
			}*/
			order_change.setText("订单状态：" + order_detail.getStatusName());
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

	// 设置控件的数据
	private void setViewData() {
		order = new ERP_Order();
		order_detail = new ERP_OrderDetail();
		adapter_order = new OrderAdapter(context, orders_normal,
				pos_order_normal);
		lv_order.setAdapter(adapter_order);

		adapter_product = new OrderItemAdapter(context,
				order_detail.getItems(), false);
		lv_product.setAdapter(adapter_product);
		DataUtil.setListViewHeight(context, lv_product, 7);
	}

	private void initPopItems() {
		items_status = new ArrayList<String>();
		items_choose = new ArrayList<String>();
		items_time = new ArrayList<String>();
		items_order = new ArrayList<String>();
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
		items_order.add("线上订单");
		items_order.add("线下订单");
		items_order.add("预约订单");
		items_order.add("预定订单");
		tv_normal.setText(items_order.get(0));
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

			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("消费记录", result);
				if (JsonUtil.isSuccess(result)) {
					List<ERP_Order> orders_temp = JsonUtil
							.getOrdersNormal(result);
					if (orders_temp != null && orders_temp.size() > 0) {
						orders_normal.addAll(orders_temp);
						adapter_order.update(orders_normal, pos_order_normal);
					}
					if (orders_normal.size() > 0) {
						order = orders_normal.get(pos_order_normal);
						if (!"".equals(order_detail.getOrderId()))
							new GetOrderDetailTask().execute();
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
			map.put("status", Ostatus);// 订单状态 0 全部，1 待发货，2 待收货，3 成功，4 关闭
			map.put("orderTypeId", orderTypeId);// app下单
			if(bussnessId.length()>0)
				map.put("bussnessId", bussnessId);
			map.put("currentPage", currentPage + "");
			map.put("pageSize", pageSize + "");
			map.put("orderUserId", orderUserId);
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.terminalId + Constants.shop.getUserId() + Ostatus
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
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.terminalId + Constants.shop.getUserId()
					+ order.getOrderId());
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

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		 
		case R.id.tv_order_normal:{
			openPopType();
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

		 
		}
	}
	private void openPopType(){

		if (pop_order == null) {
			pop_order = new SelectPopup(context, items_order,0);
			pop_order.setCallBack(new OnItemCallBack() {

				@Override
				public void onItemClick(int pos) {

					tv_normal.setText(items_order.get(pos));
					if(pos!=pos_order){
						pos_order=pos;
						pos_status=0;
						pos_time=0;
						pos_choose=0;
						currentPage = 1;
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
						/*items_order.add("线上订单");
						items_order.add("线下订单");
						items_order.add("预约订单");
						items_order.add("预定订单");*/
						
						/*Map<String, String> map = new HashMap<String, String>();
						map.put("terminalId", Constants.terminalId);
						map.put("userId", Constants.shop.getUserId());
						map.put("loginSign", Constants.loginSign);
						map.put("status", "0");// 订单状态 0 全部，1 待发货，2 待收货，3 成功，4 关闭
						map.put("orderTypeId", orderTypeId);// app下单
						map.put("bussnessId", bussnessId);
						map.put("currentPage", currentPage + "");
						map.put("pageSize", pageSize + "");
						map.put("orderUserId", orderUserId);*/
						if(pos==0){
							orderTypeId="2";
							bussnessId="00";
							Ostatus="0";
						}else if (pos==1) {
							Ostatus="3";
							orderTypeId="3";
							bussnessId="";
						}else if (pos==2) {
							Ostatus="0";
							orderTypeId="2";
							bussnessId="17";
						}else{
							Ostatus="0";
							orderTypeId="2";
							bussnessId="16";
						}
						orders_normal.clear();
						pos_order_normal = 0;
						adapter_order.update(orders_normal, pos_order_normal);
						new GetOrderTask().execute();
						
					}
					//updateOrderByCondition(pos_status, pos_time, pos_choose);
				}
			});
			pop_order.showAsDropDown(top, 0, 0);
		} else if (pop_order.isShowing()) {
			pop_order.dismiss();
		} else {
			pop_order.showAsDropDown(top, 0, 0);
		}
	
	}
	private void openPopStatus() {
		if (pop_status == null) {
			pop_status = new SelectPopup(context, items_status, 150);
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
			pop_time = new SelectPopup(context, items_time, 230);
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
			pop_choose = new SelectPopup(context, items_choose, 330);
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
}
