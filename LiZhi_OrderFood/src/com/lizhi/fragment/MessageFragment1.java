package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.http.UploadMessage;
import com.duiduifu.pulltorefresh.PullToRefreshBase;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.duiduifu.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.duiduifu.pulltorefresh.PullToRefreshListView;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.OrderMessageAdapter;
import com.lizhi.app.main.Loading;
import com.lizhi.bean.OrderMessage;
import com.lizhi.util.Constants;
import com.lizhi.util.DateUtil;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

public class MessageFragment1 extends BaseFragment implements OnClickListener {
	private ReceiveBroadCast receiveBroadCast;
	private Context context;
	private int pageSize = 12;
	private int currentPage = 1;
	private Loading load;
	// 普通订单
	private ListView lv_order;
	PullToRefreshListView pullLv_order;
	private List<OrderMessage> list = new ArrayList<OrderMessage>();
	private OrderMessageAdapter adapter;

	// 普通订单与增值订单

	private View line1, line2;
	// 详情
	private TextView tv_orderid, tv_message;

	private Button bt_order_item;
	private int pos_order_normal = 0;// 普通订单列表选中位置

	private OrderMessage message;// 当前消息
	private Callback callback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_message1,
				container, false);
		return mViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		context = getActivity();
		// printDev = SmartPayFactory.getPrinterDev();
		load = new Loading(context);

		initView();
		new GetOrderMessageTask().execute();// 获取订单
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(receiveBroadCast!=null)
			context.unregisterReceiver(receiveBroadCast);
	}
	@Override
	public void onResume() {
		 
		super.onResume();
		receiveBroadCast = new ReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.duiduifu.fragment.MessageFragment1"); // 只有持有相同的action的接受者才能接收此广播
		context.registerReceiver(receiveBroadCast, filter);
	}
	private void initView() {
		pullLv_order = (PullToRefreshListView) getView().findViewById(
				R.id.lv_order);
		lv_order = pullLv_order.getRefreshableView();
		getView().findViewById(R.id.lv_order_add).setVisibility(View.INVISIBLE);
		// tv_orderid,tv_message
		tv_orderid = (TextView) getView().findViewById(R.id.tv_orderid);
		tv_message = (TextView) getView().findViewById(R.id.tv_message);

		line1 = getView().findViewById(R.id.line1);
		line2 = getView().findViewById(R.id.line2);

		bt_order_item = (Button) getView().findViewById(R.id.bt_order_item);
		bt_order_item.setVisibility(View.GONE);
		/*
		 * getView().findViewById(R.id.mCheckSwithcButton)
		 * .setVisibility(View.GONE);
		 */

		// getView().findViewById(R.id.tv_order_get).setVisibility(View.GONE);
		bt_order_item.setOnClickListener(this);

		lv_order.setOnItemClickListener(l_order);
		pullLv_order
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {

						currentPage++;
						new GetOrderMessageTask().execute();
					}
				});
		pullLv_order.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						DateUtil.Date2String(new Date(), "yyyy-MM-dd HH:mm"));
				reflesh();
			}
		});
 
		// 设置控件的数据
		setViewData();

	}

	// 设置控件的数据
	private void setViewData() {
		message = new OrderMessage();

		adapter = new OrderMessageAdapter(context, list, pos_order_normal,
				new OrderMessageAdapter.Callback() {

					@Override
					public void IsRead(int position) {
						// 调用改变订单阅读状态
						Read(position);
					}
				});
		lv_order.setAdapter(adapter);

	}

	public void Read(final int position) {

		HashMap<String, Object> mHashMap = new HashMap<String, Object>();

		mHashMap.put("loginSign", Constants.loginSign);
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("userId", Constants.shop.getUserId());
		mHashMap.put("messageId", list.get(position).getMessageId());
		mHashMap.put("sign", MD5Util.createSign(Constants.loginSign,
				Constants.shop.getUserId(), Constants.terminalId,
				list.get(position).getMessageId()));

		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "AppPay/payByWXScanPay.do", "GET",
				mHashMap.entrySet(),

				new AsyncListener() {

					@Override
					public void onComplete(String values) {
						try {
							JSONObject jsonObject = new JSONObject(values);
							String msg = jsonObject.getString("message");

							String code = jsonObject.getString("code");
							if (code.equals("1")) {
								list.get(position).setReaded("1");
							} else {
								sendMessageToHandler(5, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							sendMessageToHandler(5,
									AppConstantByUtil.app_json_exception_tip);
						}

					}

					@Override
					public void onException(Object exceptionInfo) {
						sendMessageToHandler(5,
								AppConstantByUtil.app_json_exception_tip);

					}
				});

	}

	private OnItemClickListener l_order = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			pos_order_normal = position - 1;
			adapter.update(pos_order_normal);
			message = list.get(pos_order_normal);
			tv_orderid.setText(message.getOrderId());
			tv_message.setText(message.getMessage());
		}
	};

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		// 普通订单
		case R.id.bt_order_item: {
			 
			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();
			Fragment fragment = null;
			Constants.message=message.getOrderId();
			Intent intent = new Intent();
			if (message.getBusinessId().equals("00")) {
				// 普通订单
				 Constants.page=0;
				 intent.setAction("com.duiduifu.fragment.OrderFragment1");
					intent.putExtra("orderId", message.getOrderId());
			} else if (message.getBusinessId().equals("17")) {
				// 预定
				Constants.page=2;
				intent.setAction("com.duiduifu.fragment.OrderFragment4");
				intent.putExtra("orderId", message.getOrderId());
			} else if (message.getBusinessId().equals("16")) {
				// 预约
				Constants.page=3;
				intent.setAction("com.duiduifu.fragment.OrderFragment5");
				intent.putExtra("orderId", message.getOrderId());
			}
			context.sendBroadcast(intent);		 
			callback.callback();
		}
			break;

		}
	}

	// 根据消息状态筛选后更新订单列表
	/*
	 * private void updateOrderByCondition(int index_status, int index_time, int
	 * index_choose) {
	 * 
	 * }
	 */

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
			case 5:
				TipsToast.makeText(context, msg.obj.toString(), 1).show();
				break;
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

	/*
	 * private void printOrder() { List<PrintDataObject> list = new
	 * ArrayList<PrintDataObject>();
	 * 
	 * list.add(new PrintDataObject("商户名称：" + Constants.shop.getShopName(), "",
	 * "false", "left", "false", "true")); list.add(new
	 * PrintDataObject("商户联系方式：" + Constants.shop.getShopPhone(), "", "false",
	 * "left", "false", "true")); list.add(new PrintDataObject("交易时间：" +
	 * order_detail.getOrderDate(), "", "false", "left", "false", "true"));
	 * list.add(new PrintDataObject("会员号：", "", "false", "left", "false",
	 * "true")); list.add(new PrintDataObject("联系方式：" +
	 * order_detail.getShipMobile(), "", "false", "left", "false", "true"));
	 * list.add(new PrintDataObject("联系人：" + order_detail.getShipName(), "",
	 * "false", "left", "false", "true")); list.add(new PrintDataObject("配送地址："
	 * + order_detail.getShipAddress(), "", "false", "left", "false", "true"));
	 * list.add(new PrintDataObject("配送费：" + order_detail.getDispatchFee(), "",
	 * "false", "left", "false", "true"));
	 * 
	 * list.add(new PrintDataObject("商品名称 规格 单价 数量 小计", "", "false", "left",
	 * "false", "true"));
	 * 
	 * list.add(new PrintDataObject("名称         单价       数量", "10", "", "left",
	 * "", "true")); for (int i = 0; i < order_detail.getItems().size(); i++) {
	 * ERP_OrderItem item = new ERP_OrderItem(); item =
	 * order_detail.getItems().get(i); list.add(new
	 * PrintDataObject(item.getMerchandiseName() + "( " + item.getUnitName() +
	 * " )" + "\n" + "             " + item.getPrice() + " " + "         " +
	 * item.getNums(), "10", "", "left", "", "true")); } list.add(new
	 * PrintDataObject("小计：" + order_detail.getTotalAmount(), "", "false",
	 * "left", "false", "true")); list.add(new PrintDataObject("总额：" +
	 * order_detail.getTotalAmount(), "", "false", "left", "false", "true"));
	 * list.add(new PrintDataObject("合计：" + order_detail.getTotalAmount(), "",
	 * "false", "left", "false", "true")); list.add(new PrintDataObject("付款方式："
	 * + order_detail.getPaymentName(), "", "false", "left", "false", "true"));
	 * list.add(new PrintDataObject("付款金额：" + order_detail.getTotalAmount(), "",
	 * "false", "left", "false", "true")); list.add(new
	 * PrintDataObject("找零金额：0", "", "false", "left", "false", "true"));
	 * list.add(new PrintDataObject("   默认计算单位：人民币（元）", "", "false", "left",
	 * "false", "true")); list.add(new PrintDataObject("   温馨提醒：请妥善保管好订单小票", "",
	 * "false", "left", "false", "true")); List<PrintDataObject> list2 = new
	 * ArrayList<PrintDataObject>(); list2.add(new PrintDataObject("\n\n"));
	 * String str = null; try { str = this.printDev.print(list); str =
	 * this.printDev.print(list2); Log.i("log", "文本数据正常打印"); list.clear();
	 * list2.clear(); } catch (Exception e) { e.printStackTrace(); Log.i("log",
	 * "打印文本数据出错"); } }
	 */

	// 获取消息列表
	private class GetOrderMessageTask extends AsyncTask<String, String, String> {

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

				if (JsonUtil.isSuccess(result)) {
					if (JsonUtil.Total(result) >= currentPage) {
						List<OrderMessage> temp = JsonUtil
								.getOrderMessage(result);

						if (temp != null && temp.size() > 0) {
							list.addAll(temp);
							adapter.update(list, pos_order_normal);

						}
					}

					if (list.size() > 0) {
						message = list.get(pos_order_normal);
						if (!"".equals(message.getMessageId())) {
							// tv_orderid,tv_message
							tv_orderid.setText(message.getOrderId());
							tv_message.setText(message.getMessage());
							bt_order_item.setVisibility(View.VISIBLE);
						}else
							bt_order_item.setVisibility(View.GONE);
						// new GetOrderMessageDetailTask().execute();
					}

				} else {
					bt_order_item.setVisibility(View.GONE);
					TipsToast.makeText(context, JsonUtil.getMessage(result),
							Toast.LENGTH_SHORT).show();
					Log.i("异常消息", JsonUtil.getMessage(result));
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
			map.put("currentPage", currentPage + "");
			map.put("pageSize", pageSize + "");
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.shop.getUserId() + Constants.terminalId
					+ pageSize + currentPage);
			map.put("sign", sign);
			try {

				String result = UploadMessage
						.sendPost(
								AppConstantByUtil.IP
										+ "lizi-api/shop/SendMessage/messageListByTerminal.do",
								map, "");
				return result;
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;

		}

	}

	// 获取消息详细信息
	/*
	 * private class GetOrderMessageDetailTask extends AsyncTask<String, String,
	 * String> {
	 * 
	 * @Override protected void onPreExecute() {
	 * 
	 * load.show(); }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * 
	 * load.cancel(); if (StringUtil.isEmpty(result)) {
	 * TipsToast.makeText(context, Constants.get_nodata,
	 * Toast.LENGTH_SHORT).show(); } else { Log.i("pos", result); if
	 * (JsonUtil.isSuccess(result)) { order_detail =
	 * JsonUtil.getOrderDetail(result); // setOrderDetail();
	 * Log.i("pos_businessid", order_detail.getBusinessId());
	 * Log.i("pos_detail", order_detail.getItems().get(0) .toString()); if
	 * ("00".equals(order_detail.getBusinessId())) {
	 * adapter_product.update(order_detail.getItems());
	 * lv_product.setAdapter(adapter_product); }
	 * 
	 * else { adapter_product.update(order_detail.getItems(),
	 * order_detail.getOrderItems(), true, order_detail.getStatusName(),
	 * order_detail.getMemo()); lv_product.setAdapter(adapter_product); } } else
	 * { TipsToast.makeText(context, JsonUtil.getMessage(result),
	 * Toast.LENGTH_SHORT).show(); } } super.onPostExecute(result); }
	 * 
	 * @Override protected String doInBackground(String... params) {
	 * 
	 * Map<String, String> map = new HashMap<String, String>();
	 * map.put("terminalId", Constants.terminalId); map.put("userId",
	 * Constants.shop.getUserId()); map.put("loginSign", Constants.loginSign);
	 * map.put("orderId", message.getOrderId()); String sign =
	 * MD5Util.createSign(Constants.loginSign + Constants.terminalId +
	 * Constants.shop.getUserId() + message.getOrderId()); map.put("sign",
	 * sign); try { String result =
	 * UploadMessage.sendPost(AppConstantByUtil.HOST + "Order/getOrder.do", map,
	 * ""); return result; } catch (Exception e) {
	 * 
	 * e.printStackTrace(); } return null;
	 * 
	 * }
	 * 
	 * }
	 */
	public void reflesh() {
		if (currentPage != 1)
			pos_order_normal = 0;
		currentPage = 1;
		 
		pageSize = 10;
		list.clear();
		new GetOrderMessageTask().execute();
	}
	class ReceiveBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			currentPage = 0;
			list.clear();
			pos_order_normal=0;
			new GetOrderMessageTask().execute();

		}
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public interface Callback {
		public void callback();
	}
	 
}
