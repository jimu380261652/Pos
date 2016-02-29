package com.lizhi.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.http.UploadMessage;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.google.zxing.WriterException;
import com.lizhi.adapter.CashAdapter2;
import com.lizhi.app.dialog.IntroduceDialog;
import com.lizhi.app.main.Loading;
import com.lizhi.app.printcode.EncodingHandler;
import com.lizhi.bean.Good;
import com.lizhi.bean.Temporary;
import com.lizhi.util.Constants;
import com.lizhi.util.DataUtil;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

public class CashFragment2 extends NoBugFragment implements OnClickListener {
	private Context context;
	private Loading loading;
	private String orderid;//订单号
	private String amount,payment,code_url,reasoncode;
	private Handler mMainHadler;
	private ListView lv_goods;
	private List<Good> list = new ArrayList<Good>();
	private List<Temporary> temps = new ArrayList<Temporary>();
	private CashAdapter2 adapter2;
	private Button introduce;
	private IntroduceDialog dialog;
	private PopupWindow popup;
	private TextView tv_introduce, money,factmoney,shouldmoney;
	private ImageView back;
	private CallBack callback;
	// 支付方式选择
	private Button cash, mem, card, zfb, wx, baidu, other,btn_bill;
	private int paytype=3;
	// 数字键
	private Button btn10, btn20, btn50, btn100, btn7, btn4, btn1, btn8, btn5,
			btn2, btn0, btn9, btn6, btn3, btnpoint, btnEnter;
	private ImageButton btndelete;
	// 输入框
	private EditText  et_useaintegral, et_tableNo, et_factmoney,
			et_change;

	// 判断选择哪个输入框
	private int index = 0;
	// 输入框数据
	private StringBuffer num = new StringBuffer("");
	
	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(get_Activity(), R.layout.fragment_cash2, null);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = get_Activity();
		Bundle bundle = getArguments();
		temps = (List<Temporary>) bundle.get("list");
		if (temps != null) {
			InitData();
		}

		InitUI();
	}

	private void InitData() {
		// TODO Auto-generated method stub
		/*
		 * Good g = new Good(); g.setName("菲林咖啡"); g.setPrice("20.00");
		 * g.setSize("中"); g.setNum(1); list.add(g); g = new Good();
		 * g.setName("曼特宁"); g.setPrice("75.00"); g.setSize("加奶"); g.setNum(1);
		 * list.add(g); g = new Good(); g.setName("红豆可可"); g.setPrice("15.00");
		 * g.setSize("大杯"); g.setNum(1); list.add(g);
		 */
		for (int i = 0; i < temps.size(); i++) {
			Good g = new Good();
			g.setPrice("32.00");
			g.setSize("中");
			g.setNum(temps.get(i).getNum());
			g.setName(temps.get(i).getName());
			list.add(g);
		}
	}

	private void InitUI() {
		// TODO Auto-generated method stub
		//factmoney,shouldmoney;
		loading=new Loading(context);
		btn_bill=(Button) view.findViewById(R.id.btn_bill);
		factmoney=(TextView) view.findViewById(R.id.tv_fristline);
		shouldmoney=(TextView) view.findViewById(R.id.tv_shouldmoney);
		money = (TextView) view.findViewById(R.id.tv_ordermoney);
		back = (ImageView) view.findViewById(R.id.tv_back);
		back.setOnClickListener(this);
		tv_introduce = (TextView) view.findViewById(R.id.tv_introduce);
		lv_goods = (ListView) view.findViewById(R.id.lv_goods);
		adapter2 = new CashAdapter2(context, list);
		lv_goods.setAdapter(adapter2);
		adapter2.setCallback(new CashAdapter2.CallBack() {
			@Override
			public void setnum(int position, int count) {
				// TODO Auto-generated method stub
				list.get(position).setNum(count);
				adapter2.notifyDataSetChanged();
			}

			@Override
			public void delete(int position) {
				// TODO Auto-generated method stub
				list.remove(position);
				adapter2.notifyDataSetChanged();
			}
		});
		introduce = (Button) view.findViewById(R.id.btn_introduce);
		introduce.setOnClickListener(this);

		cash = (Button) view.findViewById(R.id.btn_cash);
		mem = (Button) view.findViewById(R.id.btn_mem);
		card = (Button) view.findViewById(R.id.btn_card);
		zfb = (Button) view.findViewById(R.id.btn_zfb);
		wx = (Button) view.findViewById(R.id.btn_wx);
		baidu = (Button) view.findViewById(R.id.btn_baidu);
		other = (Button) view.findViewById(R.id.btn_other);
		cash.setOnClickListener(this);
		mem.setOnClickListener(this);
		card.setOnClickListener(this);
		zfb.setOnClickListener(this);
		wx.setOnClickListener(this);
		baidu.setOnClickListener(this);
		other.setOnClickListener(this);
		btn10 = (Button) view.findViewById(R.id.btn10);
		btn20 = (Button) view.findViewById(R.id.btn20);
		btn50 = (Button) view.findViewById(R.id.btn50);
		btn100 = (Button) view.findViewById(R.id.btn100);
		btn7 = (Button) view.findViewById(R.id.btn7);
		btn4 = (Button) view.findViewById(R.id.btn4);
		btn1 = (Button) view.findViewById(R.id.btn1);
		btn8 = (Button) view.findViewById(R.id.btn8);
		btn5 = (Button) view.findViewById(R.id.btn5);
		btn2 = (Button) view.findViewById(R.id.btn2);
		btn0 = (Button) view.findViewById(R.id.btn0);
		btn9 = (Button) view.findViewById(R.id.btn9);
		btn6 = (Button) view.findViewById(R.id.btn6);
		btn3 = (Button) view.findViewById(R.id.btn3);
		btnpoint = (Button) view.findViewById(R.id.btnpoint);
		btnEnter = (Button) view.findViewById(R.id.btnEnter);
		btndelete = (ImageButton) view.findViewById(R.id.btndelete);
		btn_bill.setOnClickListener(this);
		btn10.setOnClickListener(this);
		btn20.setOnClickListener(this);
		btn50.setOnClickListener(this);
		btn100.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn0.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btnpoint.setOnClickListener(this);
		btnEnter.setOnClickListener(this);
		btndelete.setOnClickListener(this);
		//et_memberid = (EditText) view.findViewById(R.id.et_memberid);
		et_useaintegral = (EditText) view.findViewById(R.id.et_useaintegral);
		et_tableNo = (EditText) view.findViewById(R.id.et_tableNo);
		et_factmoney = (EditText) view.findViewById(R.id.et_factmoney);
		et_change = (EditText) view.findViewById(R.id.et_change);
		//et_vouchers = (EditText) view.findViewById(R.id.et_vouchers);
		//et_memberid.setOnClickListener(this);
		et_useaintegral.setOnClickListener(this);
		et_tableNo.setOnClickListener(this);
		et_factmoney.setOnClickListener(this);
		//et_vouchers.setOnClickListener(this);
		show(temps);
		mMainHadler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case AppConstantByUtil.SUCCESS:// 1
					//tv_guandang.setVisibility(View.INVISIBLE);
					/*mers.clear();
					adapter.update(mers);
					lv_retail.setAdapter(adapter);
					cancel.setText("商品0件");
					total.setText("总计：￥ 0");*/
					openDialog();

					break;
				case AppConstantByUtil.ERROR:// 0
					if (loading.isShowing())
						loading.cancel();
					TipsToast.makeText(context, msg.obj.toString(), 1).show();
					break;
				case 15:
					loading.cancel();
					/*
					 * Intent intent = new Intent();
					 * intent.setAction("com.duiduifu.fragment.OrderFragment2");
					 * context.sendBroadcast(intent);
					 */
					WxScancode(orderid, amount);
					//sendbroadcast();
					break;
				case 16:
					loading.cancel();
					//sendbroadcast();
					try {
						double price = 0;
						if (temps.size() > 0) {

							for (int i = 0; i < temps.size(); i++) {
								price = price
										+ temps.get(i).getNum()
										* 0.01;
							}
							
							DecimalFormat df = new DecimalFormat("#.##");
							ComponentName component = new ComponentName(
									"com.centerm.df.main",
									"com.centerm.df.main.activity.MainControlActivity");
							Intent intent = new Intent();
							intent.setComponent(component);
							Bundle bundle = new Bundle();
							bundle.putString("trs_tp", "1");// 1
							bundle.putString("proc_cd", "009000");
							bundle.putString("amt", df.format(price));// 15.00
							Constants.price=df.format(price);
							intent.putExtras(bundle);
							startActivityForResult(intent, 1);

							// User.payType="刷卡";
						} else {
							Toast.makeText(context, "没有购买商品", 1).show();
						}

					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
						Toast.makeText(context, "Activity Not Exit", 1).show();
					} catch (Exception e) {
						Toast.makeText(context, "Another Exception", 1).show();
					} /*finally {
						cancel.setText("商品0件");
						total.setText("总计：￥ 0");
						mers.clear();
						adapter.update(mers);
						lv_retail.setAdapter(adapter);
					}*/
					break;
				case 17:
					loading.cancel();
					temps.clear();
					list.clear();
					adapter2.update(list);
					 
					 
				/*	cancel.setText("商品0件");
					total.setText("总计：￥ 0");*/
					TipsToast.makeText(context, msg.obj.toString(), 1).show();
					payForTerminal("0", "03", "0", "0");
					//sendbroadcast();
					break;
				default:
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_introduce: {
			dialog = new IntroduceDialog(context, tv_introduce.getText()
					.toString());

			dialog.setCallback(new IntroduceDialog.CallBack() {

				@Override
				public void callback(String str) {
					// TODO Auto-generated method stub
					tv_introduce.setText(str);
				}
			});
			dialog.show();
		}
			break;

		case R.id.btn_cash: {
			paytype=3;
			setpaybtn();
			cash.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;
		case R.id.btn_mem: {
			paytype=-1;
			setpaybtn();
			mem.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;
		case R.id.btn_card: {
			paytype=5;
			setpaybtn();
			card.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;
		case R.id.btn_zfb: {
			paytype=-1;
			setpaybtn();
			zfb.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;
		case R.id.btn_wx: {
			paytype=2;
			setpaybtn();
			wx.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;
		case R.id.btn_baidu: {
			paytype=-1;
			setpaybtn();
			baidu.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;
		case R.id.btn_other: {
			paytype=-1;
			setpaybtn();
			other.setBackgroundResource(R.drawable.pay_btn_press);
		}
			break;

		case R.id.et_memberid: {// 1
			if (index != 1) {
				num.delete(0, num.length());
			}
			index = 1;
			// TipsToast.makeText(context, "show", 1).show();
		}
			break;
		case R.id.et_useaintegral: {// 2
			if (index != 2) {
				num.delete(0, num.length());
			}
			index = 2;
			// TipsToast.makeText(context, "show", 1).show();
		}
			break;
		case R.id.et_tableNo: {// 3
			if (index != 3) {
				num.delete(0, num.length());
			}
			index = 3;
			// TipsToast.makeText(context, "show", 1).show();
		}
			break;
		case R.id.et_factmoney: {// 4
			if (index != 4) {
				num.delete(0, num.length());
			}
			index = 4;
			// TipsToast.makeText(context, "show", 1).show();
		}
			break;

		case R.id.et_vouchers: {// 5
			if (index != 5) {
				num.delete(0, num.length());
			}
			index = 5;
			// TipsToast.makeText(context, "show", 1).show();
		}
			break;

		case R.id.btnEnter: {

			if (index == 4) {
				// et_change
				DecimalFormat df = new DecimalFormat("#.##");
				double d = Double.parseDouble(num.toString()) - 88.00;
				et_change.setText(df.format(d));
			}else if (index == 2){
				show(temps);
			}
			num.delete(0, num.length());
			index = 0;
		}
			break;
		case R.id.btndelete: {
			if (num.length() > 0) {
				num.delete(num.length() - 1, num.length());
				findfouse();
			}
		}
			break;
		case R.id.btn10: {
			num.append("10");
			findfouse();
		}
			break;
		case R.id.btn20: {
			num.append("20");
			findfouse();
		}
			break;
		case R.id.btn50: {
			num.append("50");
			findfouse();
		}
			break;
		case R.id.btn100: {
			num.append("100");
			findfouse();
		}
			break;
		case R.id.btn0: {
			num.append("0");
			findfouse();
		}
			break;
		case R.id.btn1: {
			num.append("1");
			findfouse();
		}
			break;
		case R.id.btn2: {
			num.append("2");
			findfouse();
		}
			break;
		case R.id.btn3: {
			num.append("3");
			findfouse();
		}
			break;
		case R.id.btn4: {
			num.append("4");
			findfouse();
		}
			break;
		case R.id.btn5: {
			num.append("5");
			findfouse();
		}
			break;
		case R.id.btn6: {
			num.append("6");
			findfouse();
		}
			break;
		case R.id.btn7: {
			num.append("7");
			findfouse();
		}
			break;
		case R.id.btn8: {
			num.append("8");
			findfouse();
		}
			break;
		case R.id.btn9: {
			num.append("9");
			findfouse();
		}
			break;
		case R.id.btnpoint: {
			num.append(".");
			findfouse();
		}
			break;
		case R.id.tv_back: {
			list.clear();
			callback.callback();
		}
			break;
		case R.id.btn_bill:{
			Makeorder();
		}
		break;
		default:
			break;
		}
	}

	private void Makeorder() {
		// TODO Auto-generated method stub
		if(list.size()>0){
			if(paytype==5||paytype==2||paytype==3){
				loading.show();
				HashMap<String, Object> mHashMap = new HashMap<String, Object>();
				mHashMap.put("shopId", Constants.shop.getShopId());

				mHashMap.put("paymentId", paytype+"");

				mHashMap.put("userId", Constants.shop.getUserId());
				mHashMap.put("terminalId", Constants.terminalId);
				mHashMap.put("loginSign", Constants.loginSign);
				mHashMap.put("sign", MD5Util.createSign(Constants.loginSign,
						Constants.terminalId, Constants.shop.getUserId(), paytype+""));
				DecimalFormat df = new DecimalFormat("#.##");

				for (int i = 0; i < temps.size(); i++) {
					mHashMap.put("orderitemslist[" + i + "].merchandiseId", "201602261519490212");
					mHashMap.put("orderitemslist[" + i + "].nums", list.get(i).getNum());

					mHashMap.put("orderitemslist[" + i + "].price","0.01");
					if (paytype==3) {

						double b = (double) temps.get(i).getNum()
								* 0.01;
						mHashMap.put("orderitemslist[" + i + "].amount", df.format(b));
						 
					} else {
						mHashMap.put(
								"orderitemslist[" + i + "].amount",
								df.format(temps.get(i).getNum()
										* 0.01));				 
					}

				}
				final String pay = paytype+"";
				AsyncRunner.getInstance().request(
						AppConstantByUtil.HOST + "Order/addOrder.do", "GET",
						mHashMap.entrySet(), new AsyncListener() {

							@Override
							public void onComplete(String values) {

								try {
									JSONObject jsonObject = new JSONObject(values);
									String msg = jsonObject.getString("message");

									String code = jsonObject.getString("code");
									if (code.equals("1")) {
										// WxScancode
										orderid = jsonObject.getString("orderId");
										
										/*
										 * amount=jsonObject.getDouble("totalMoney")+"";
										 * System.out.println("钱"+amount);
										 */
										JSONArray jsonArray = new JSONArray(jsonObject
												.getString("orderInfos"));
										double d = 0;
										for (int i = 0; i < jsonArray.length(); i++) {
											if (jsonArray.getJSONObject(i)
													.has("amount")) {
												d += jsonArray.getJSONObject(i)
														.getDouble("amount");

											}
										}
										amount = d + "";
										if (pay.equals("2")) {// 微信扫码
											payment = "2";
											Constants.orderid = orderid;
											sendMessageToHandler(15, msg);
										} else if (pay.equals("3")) {
											payment = "3";
											sendMessageToHandler(17, "结算完成");

										} else if (pay.equals("5")) {
											payment = "5";
											Constants.orderid = orderid;
											// payForTerminal("0", "05", "0", "0");
											sendMessageToHandler(16, msg);
										}

									} else {
										sendMessageToHandler(AppConstantByUtil.ERROR,
												msg);
									}
								} catch (JSONException e) {

									e.printStackTrace();
								}

							}

							@Override
							public void onException(Object exceptionInfo) {
								sendMessageToHandler(AppConstantByUtil.ERROR,
										"网络异常，请检查");
							}

						});

			
			}else if(paytype==-1){
				TipsToast.makeText(context, "暂未开通该支付方式", 1).show();
			}
		}else{
			TipsToast.makeText(context, "没有选购的商品", 1).show();
		}
		
	}

	public void setpaybtn() {

		cash.setBackgroundResource(R.drawable.pay_btn_normal);
		mem.setBackgroundResource(R.drawable.pay_btn_normal);
		card.setBackgroundResource(R.drawable.pay_btn_normal);
		zfb.setBackgroundResource(R.drawable.pay_btn_normal);
		wx.setBackgroundResource(R.drawable.pay_btn_normal);
		baidu.setBackgroundResource(R.drawable.pay_btn_normal);
		other.setBackgroundResource(R.drawable.pay_btn_normal);

	}

	public void findfouse() {
		/*
		 * et_memberid, et_useaintegral, et_tableNo, et_factmoney, ,
		 * et_vouchers;
		 */
		if (index == 1) {
			//et_memberid.setText(num.toString());
		} else if (index == 2) {
			et_useaintegral.setText(num.toString());
		} else if (index == 3) {
			et_tableNo.setText(num.toString());
		} else if (index == 4) {
			et_factmoney.setText(num.toString());
			show(temps);
		} else if (index == 5) {
			//et_vouchers.setText(num.toString());
		}
	}

	public interface CallBack {
		public void callback();
	}

	public void setCallback(CallBack callback) {
		this.callback = callback;
	}

	public void show(List<Temporary> temps) {

		double amount = 0,discount=0;
		for (int i = 0; i < temps.size(); i++) {
			amount += temps.get(i).getNum() * 32;
		}
		money.setText("" + amount);
		discount=Double.parseDouble(et_useaintegral.getText().toString())/100;
		factmoney.setText("订单金额："+amount+"元    优惠抵扣："+discount+"元");
		shouldmoney.setText(amount-discount+"");
		/*InitData();
		adapter2.update(list);*/
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
	public void WxScancode(String orderid, String amount) {
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();

		mHashMap.put("loginSign", Constants.loginSign);
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("userId", Constants.shop.getUserId());
		mHashMap.put("orderId", orderid);
		mHashMap.put("paymentId", "05");
		mHashMap.put("orderAmount", amount);
		String item = "";
		for (int i = 0; i < temps.size(); i++) {
			item = item + temps.get(i).getName();
		}
		mHashMap.put("item", item);
		mHashMap.put("detail", item);
		mHashMap.put("sign",
				MD5Util.createSign(Constants.loginSign, orderid, amount));
		mHashMap.put("spbillCreateIp", "127.0.0.1");
		AsyncRunner.getInstance().request(
				// http://192.168.2.190:8080//community-api/shopterminal/AppPay/payByWXScanPay.do
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
								code_url = jsonObject.getString("code_url");
								if (TextUtils.isEmpty(code_url)) {
									sendMessageToHandler(
											AppConstantByUtil.ERROR,
											"code_url为空");
								} else {
									sendMessageToHandler(
											AppConstantByUtil.SUCCESS, msg);
								}
							} else {
								sendMessageToHandler(AppConstantByUtil.ERROR,
										msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							sendMessageToHandler(AppConstantByUtil.ERROR,
									AppConstantByUtil.app_json_exception_tip);
						}
					}

					@Override
					public void onException(Object exceptionInfo) {
						sendMessageToHandler(AppConstantByUtil.ERROR,
								AppConstantByUtil.app_json_exception_tip);

					}
				});
	}
	/*
	 * 终端支付
	 */
	public void payForTerminal(String paypwd,String payType,
			String batchno, String systraceno) {
		// loading.show();
		// paypwd = MD5Util.createSign(paypwd);
		// loading.show();
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("loginSign", Constants.loginSign);
		mHashMap.put("shopId", Constants.shop.getShopId());
		mHashMap.put("userId", Constants.shop.getUserId());
		mHashMap.put("orderId", orderid);
		mHashMap.put("password", paypwd);
		mHashMap.put("payType", payType);
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("batchno", batchno);
		mHashMap.put("systraceno", systraceno);
		mHashMap.put("sign", MD5Util.createSign(Constants.loginSign,
				Constants.terminalId, paypwd, orderid, payType));
		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "AppPay/payForTerminal.do", "POST",
				mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onException(Object exceptionInfo) {
						sendMessageToHandler(AppConstantByUtil.ERROR,
								AppConstantByUtil.app_network_exception_tip);
					}

					@Override
					public void onComplete(String values) {
						System.out.println("--------------------" + values);
						try {
							JSONObject mJsonObject = new JSONObject(values);
							if (mJsonObject.getString("code").equals("1")) {

								sendMessageToHandler(14,
										mJsonObject.getString("message"));

							} else {
								sendMessageToHandler(AppConstantByUtil.ERROR,
										mJsonObject.getString("message"));
							}
						} catch (Exception e) {
							e.printStackTrace();
							sendMessageToHandler(AppConstantByUtil.ERROR,
									AppConstantByUtil.app_json_exception_tip);
						}
					}
				});
	}
	/*
	 * 弹出一个PopupWindow
	 */
	public void openDialog() {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.dialog_cashier_wx, null);
		ImageView iv_codeurl = (ImageView) view.findViewById(R.id.iv_codeurl);// 点击二维码退出
		try {
			Bitmap map = EncodingHandler.createQRCode(code_url, 400);
			iv_codeurl.setImageBitmap(map);
		} catch (WriterException e) {

			e.printStackTrace();
			sendMessageToHandler(AppConstantByUtil.ERROR, "无法生成二维码");
		}
		iv_codeurl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popup.dismiss();
			}
		});
		popup = new PopupWindow(view,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT / 2,
				DataUtil.getScreenHeight(context) - btn_bill.getMeasuredHeight());
		// 使其聚集
		popup.setFocusable(true);
		// 设置允许在外点击消失
		popup.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.showAtLocation(btn_bill, Gravity.NO_GRAVITY, 0, 0);
		//tv_cancleorder.setVisibility(View.VISIBLE);
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Log.i("pos", "on result");
		switch (resultCode) {
		// 支付成功
		case Activity.RESULT_OK:
			Log.i("pos", "success");
			/*
			 * // 支付成功 Bundle bundle = data.getExtras(); // 解析支付结果 String cardno
			 * = bundle.getString("cardno");// 卡号 String batchno =
			 * bundle.getString("batchno");// 批次号 String systraceno =
			 * bundle.getString("systraceno");// 流水号- String merid =
			 * bundle.getString("merid");// 商户号 String termid =
			 * bundle.getString("termid");// 终端号 //payByCash("", "01", batchno,
			 * systraceno, termid);
			 */
			/*mers.clear();
			adapter.update(mers);
			lv_retail.setAdapter(adapter);
			cancel.setText("商品0件");
			total.setText("总计：￥ 0");*/
			 
			TipsToast.makeText(context, "刷卡成功", 1).show();
			payForTerminal("0", "06", "0", "0");
			Constants.orderid="";
			break;
		// 支付失败
		case Activity.RESULT_CANCELED:
			Bundle bundle2 = data.getExtras();
			String reason = bundle2.getString("reason");
			if (reason != null) {
				TipsToast.makeText(getActivity(), reason, 1000).show();
				Log.i("pos", "fail:" + reason);
			} 
			new AlertDialog.Builder(context)
			.setTitle("重新刷卡")
			.setCancelable(false)
			.setItems(R.array.change,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface Mydialog,
								int which) {

							if (which == 0) {// 微信扫码
								DecimalFormat df = new DecimalFormat("#.##");
								ComponentName component = new ComponentName(
										"com.centerm.df.main",
										"com.centerm.df.main.activity.MainControlActivity");
								Intent intent = new Intent();
								intent.setComponent(component);
								Bundle bundle = new Bundle();
								bundle.putString("trs_tp", "1");// 1
								bundle.putString("proc_cd", "009000");
								bundle.putString("amt", Constants.price);// 15.00
								intent.putExtras(bundle);
								startActivityForResult(intent, 1);
							} else if (which == 1) {
								new GetReasonTask().execute("1");
							}

						}
					}).create().show();
			
			break;
		default:

			Log.i("pos", "nothing");
			break;
		}
	}
	public class GetReasonTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			loading.show();
		}

		@Override
		protected void onPostExecute(String result) {

			loading.cancel();
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
						/*if (!flag)
							new SendOrderTask().execute("9");
						else
							new SendOrderTask().execute("2");*/
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
			map.put("orderId",orderid);
			// order.getOrderId()
			map.put("person", "2");
			map.put("processCode", params[0]);
			String sign = MD5Util.createSign(Constants.terminalId,
					Constants.loginSign, Constants.shop.getUserId(), "2",
					params[0], orderid);
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
	private class SendOrderTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			loading.show();
		}

		@Override
		protected void onPostExecute(String result) {

			loading.cancel();
			if (StringUtil.isEmpty(result)) {
				TipsToast.makeText(context, Constants.get_nodata,
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("pos", result);
				if (JsonUtil.isSuccess(result)) {
					/*if (flag) {
						TipsToast.makeText(context, "发货成功", Toast.LENGTH_SHORT)
								.show();
						send.setVisibility(View.GONE);
					} else {*/
					 

						TipsToast.makeText(context, "取消成功", Toast.LENGTH_SHORT)
								.show();

						/*cancel.setVisibility(View.GONE);

						ok.setVisibility(View.GONE);*/
				 
					//new GetOrderDetailTask().execute();
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
			map.put("orderId", orderid);
			map.put("processCode", params[0]);
			if (reasoncode.length()>1) {
				map.put("reason", reasoncode.split(",")[0]);
				reasoncode=reasoncode.split(",")[0];
				Log.i("原因码", reasoncode);
			}else {
				map.put("reason", "");
			}
			map.put("processer", "2");
			map.put("channelId", "04");
			String sign = MD5Util.createSign(Constants.terminalId
					+ Constants.loginSign + Constants.shop.getUserId()
					+ orderid + params[0] + reasoncode + "" + ""
					+ "04");
			reasoncode="";
			Constants.orderid="";
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
	
}
