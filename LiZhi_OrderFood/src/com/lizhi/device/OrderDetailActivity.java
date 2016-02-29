//package com.erp.order.activity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.AlertDialog;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Base64;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.centerm.smartpos.bean.PrintDataObject;
//import com.centerm.smartpos.dev.IPrintDev;
//import com.centerm.smartpos.dev.impl.SmartPayFactory;
//import com.centerm.zxing.QRCodeEncoder;
//import com.duiduifu.http.AsyncListener;
//import com.duiduifu.http.AsyncRunner;
//import com.duiduifu.util.AppConstantByUtil;
//import com.duiduifu.util.MD5Util;
//import com.duiduifu.view.TipsToast;
//import com.erp.order.R;
//import com.erp.order.adapter.ApplyAdapter;
//import com.erp.order.adapter.CommodityListAdapter;
//import com.erp.order.adapter.ApplyAdapter.ImgCallBack;
//import com.erp.order.app.MyApplication;
//import com.erp.order.bean.Apply;
//import com.erp.order.bean.Commodity;
//import com.erp.order.dialog.Loading;
//
//public class OrderDetailActivity extends BaseActivity implements OnClickListener,ImgCallBack{
//	
//	TextView vOrderMessage,vShangpingMessage,vApplyMessage;
//	RelativeLayout vDingdanLayer,vShangpingLayer,vApplyLayer;
//	
//	TextView vTransactionState,vRealMoney,vCreateTime,vYewuDanhao,vConfirmTime,vBuyerAccount,vBuyerNike,vShouhuoPerson,
//			 vMobilePhone,vPhone,vShouhuoAddress;
//	TextView vOrderCancel,vOrderSend,vOrderOK,vOrderPrint,vOrderService,vOrderRemind,vOrderPay;
//	
//	ListView vShangpingList,vApplyList;
//	View endView;
//	
//	Loading loading;
//	
//	Button vBack;
//	ImageView read_icon;
//	ArrayList<Commodity> commodityList = new ArrayList<Commodity>();
//	ArrayList<Apply>applies=new ArrayList<Apply>();
//	CommodityListAdapter commodityAdapter;
//	ApplyAdapter applyAdapter;
//	
//	String nums,orderDate,realName,shipAddress,shipMobile,shipName,shipTel,statusId,statusName,printTwo,
//		   totalAmount,userName,shopName,shopContact,shopPhone,shopId,shopAddress,paymentName,shipTime,businessId,paymentId;
//	
//	String terminalId,userId,loginSign,orderId,allowCancel,allowSendGoods,allowRemindToUser,allowApplay,allowPay,memo="",memoMsg,attrName,attrContactName,attrPhone,isRead,isPay;
//	int position;
//	
//	boolean isChange = false,isApplyed=false,isYuyue=false;
//	
//	private Handler mHandler;
//	
//	int length,currentPage=1;
//	private String totalpage;
//	AlertDialog dlg; 
//	IPrintDev printDev=SmartPayFactory.getPrinterDev();
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.activity_order_detail);
//		((MyApplication)getApplication()).addActivity(this);
//		loading=new Loading(this);
//		loading.setCancelable(false);
//		
//		loginSign=getIntent().getStringExtra("loginSign");
//		terminalId=getIntent().getStringExtra("terminalId");
//		userId=getIntent().getStringExtra("userId");
//		orderId=getIntent().getStringExtra("orderId");
//		position=getIntent().getIntExtra("position", -1);
//		shopName=getIntent().getStringExtra("shopName");
//		shopContact=getIntent().getStringExtra("shopContact");
//		shopPhone=getIntent().getStringExtra("shopPhone");
//		shopId=getIntent().getStringExtra("shopId");
//		shopAddress=getIntent().getStringExtra("shopAddress");
//		initUI();
//	}
//
//	private void initUI() {
//		
//		dlg = new AlertDialog.Builder(this).create();
//		
//		vOrderMessage = (TextView) this.findViewById(R.id.order_message);
//		vShangpingMessage = (TextView) this.findViewById(R.id.shangping_message);
//		vApplyMessage=(TextView)this.findViewById(R.id.apply_message);
//		
//		vDingdanLayer = (RelativeLayout) this.findViewById(R.id.digndan_layer);
//		vShangpingLayer = (RelativeLayout) this.findViewById(R.id.shangping_layer);
//		vApplyLayer=(RelativeLayout)this.findViewById(R.id.apply_layer);
//		
//		vTransactionState = (TextView) this.findViewById(R.id.transaction_state);
//		vRealMoney = (TextView) this.findViewById(R.id.real_money);
//		vCreateTime = (TextView) this.findViewById(R.id.create_time);
//		vYewuDanhao = (TextView) this.findViewById(R.id.yewu_danhao);
//		vConfirmTime = (TextView) this.findViewById(R.id.confirm_time);
//		vBuyerAccount = (TextView) this.findViewById(R.id.buyer_account);
//		vBuyerNike = (TextView) this.findViewById(R.id.buyer_nike);
//		vShouhuoPerson = (TextView) this.findViewById(R.id.shouhuo_person);
//		vMobilePhone = (TextView) this.findViewById(R.id.mobile_phone);
//		vPhone = (TextView) this.findViewById(R.id.phone);
//		vShouhuoAddress = (TextView) this.findViewById(R.id.shouhuo_address);
//		
//		vOrderCancel = (TextView) this.findViewById(R.id.order_cancel);
//		vOrderSend = (TextView) this.findViewById(R.id.order_send);
//		vOrderOK = (TextView) this.findViewById(R.id.order_ok);
//		vOrderPrint = (TextView) this.findViewById(R.id.order_print);
//		vOrderRemind=(TextView)this.findViewById(R.id.order_remind);
//		vOrderService=(TextView)this.findViewById(R.id.order_service);
//		vOrderPay=(TextView)this.findViewById(R.id.order_pay);
//		read_icon=(ImageView)this.findViewById(R.id.read_icon);
//		
//		vShangpingList = (ListView) this.findViewById(R.id.shangping_list);
//		vApplyList=(ListView)this.findViewById(R.id.apply_list);
//		endView = this.getLayoutInflater().inflate(R.layout.item_end_layer, null);
//		vShangpingList.addFooterView(endView, null, false);
//		
//		
//		vBack = (Button) this.findViewById(R.id.back_btn);
//		
//		vOrderMessage.setOnClickListener(this);
//		vShangpingMessage.setOnClickListener(this);
//		vApplyMessage.setOnClickListener(this);
//		vOrderCancel.setOnClickListener(this);
//		vOrderSend.setOnClickListener(this);
//		vOrderOK.setOnClickListener(this);
//		vOrderService.setOnClickListener(this);
//		vOrderPrint.setOnClickListener(this);
//		vOrderPay.setOnClickListener(this);
//		
//		vBack.setOnClickListener(this);
//		
//		commodityAdapter = new CommodityListAdapter();
//		commodityAdapter.setLayoutInflater(this.getLayoutInflater());
//		commodityAdapter.update(commodityList);
//		vShangpingList.setAdapter(commodityAdapter);
//		
//		applyAdapter=new ApplyAdapter(this,this);
//		applyAdapter.setLayoutInflater(this.getLayoutInflater());
//		applyAdapter.update(applies);
//		vApplyList.setAdapter(applyAdapter);
//		vApplyList.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView arg0, int arg1) {
//				
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//				if((firstVisibleItem + visibleItemCount == totalItemCount) &&(totalItemCount > 1)){		//监听到ListView或者GridView滑到底
//					
//					if(totalpage!=null){
//						if (currentPage<Integer.parseInt(totalpage)) {
//							
//							currentPage++;
//							loadApply();	//加载更多数据
//					}
//					}
//				
//			
//			}
//			}
//
//		});
//		
//		mHandler=new Handler(){
//
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				switch (msg.what) {
//				case AppConstantByUtil.SUCCESS:
//					String m = msg.obj.toString();
//					loading.cancel();
//					
////					if(statusId!=null && !"".equals(statusId)){
////						if(Integer.parseInt(statusId) == 1){	//待发货
////							vOrderOK.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.VISIBLE);
////							vOrderSend.setVisibility(View.VISIBLE);
////						}
////						if(Integer.parseInt(statusId) == 2){	//待收货
////							vOrderOK.setVisibility(View.VISIBLE);
////							vOrderCancel.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////						}
////						if(Integer.parseInt(statusId) == 3){	//成功
////							vOrderOK.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////							
////						}
////						if(Integer.parseInt(statusId) == 4){	//关闭
////							vOrderOK.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////						}
////						if(Integer.parseInt(statusId) == 5){	//代付款
////							vOrderOK.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.VISIBLE);
////						}
////					}
//					if (allowCancel.equals("1")) {
//						vOrderCancel.setVisibility(View.VISIBLE);
//					}else {
//						vOrderCancel.setVisibility(View.GONE);
//					}
//					if (allowApplay.equals("1")) {
//						vOrderService.setVisibility(View.VISIBLE);
//					}else {
//						vOrderService.setVisibility(View.GONE);
//					}
//					if (allowRemindToUser.equals("1")) {
//						vOrderRemind.setVisibility(View.VISIBLE);
//					}else {
//						vOrderRemind.setVisibility(View.GONE);
//					}
//					if (allowSendGoods.equals("1")) {
//						vOrderSend.setVisibility(View.VISIBLE);
//					}else {
//						vOrderSend.setVisibility(View.GONE);
//					}
//					
//					vTransactionState.setText(statusName);
//					if (allowPay.equals("1")) {
//						vOrderPay.setVisibility(View.VISIBLE);
//					}
//					vRealMoney.setText(totalAmount);
//					vCreateTime.setText(orderDate);
//					vYewuDanhao.setText(orderId);
//					vBuyerAccount.setText(userName);
//					vBuyerNike.setText(realName);
//					vShouhuoPerson.setText(shipName);
//					 vMobilePhone.setText(shipMobile);
//					 vPhone.setText(shipTel);
//					if (isRead.equals("0")) {
//						read_icon.setVisibility(View.VISIBLE); 
//					}else {
//						read_icon.setVisibility(View.GONE);
//					}
//					 vShouhuoAddress.setText(shipAddress);
//					commodityAdapter.update(commodityList);
//					commodityAdapter.notifyDataSetChanged();
//					break;
//				case AppConstantByUtil.ERROR:
//					loading.cancel();
//					String m1 = msg.obj.toString();
//					TipsToast.makeText(OrderDetailActivity.this, m1, Toast.LENGTH_SHORT).show();
//					break;
//				case 5:
//					loading.cancel();
//					String m2 = msg.obj.toString();
//					TipsToast.makeText(OrderDetailActivity.this, m2, Toast.LENGTH_SHORT).show();
//					break;
//				case 6:
//					loading.cancel();
//					String m3 = msg.obj.toString();
//					TipsToast.makeText(OrderDetailActivity.this, m3, Toast.LENGTH_SHORT).show();
//					break;
//				case 7:
////					if(statusId!=null && !"".equals(statusId)){
////						if(Integer.parseInt(statusId) == 1){	//待发货
////							vTransactionState.setText("待发货");
////							vOrderOK.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.VISIBLE);
////							vOrderSend.setVisibility(View.VISIBLE);
////						}
////						if(Integer.parseInt(statusId) == 2){	//待收货
////							vTransactionState.setText("待收货");
////							vOrderOK.setVisibility(View.VISIBLE);
////							vOrderCancel.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////						}
////						if(Integer.parseInt(statusId) == 3){	//成功
////							vTransactionState.setText("成功");
////							vOrderOK.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////							
////						}
////						if(Integer.parseInt(statusId) == 4){	//关闭z 
////							vTransactionState.setText("关闭");
////							vOrderOK.setVisibility(View.GONE);
////							vOrderCancel.setVisibility(View.GONE);
////							vOrderSend.setVisibility(View.GONE);
////						}
////					}
//					loadListData();
//					isChange = true;
//					loading.cancel();
//					break;
//				case 8:
//					loading.cancel();
//					String m8 = msg.obj.toString();
//					TipsToast.makeText(OrderDetailActivity.this, m8, Toast.LENGTH_SHORT).show();
//					break;
//				case 9:
//					loading.cancel();
//					String m9 = msg.obj.toString();
//					TipsToast.makeText(OrderDetailActivity.this, m9, Toast.LENGTH_SHORT).show();
//					break;
//				case 10:
//					loading.cancel();
//					String m10 = msg.obj.toString();
//					TipsToast.makeText(OrderDetailActivity.this, m10, Toast.LENGTH_SHORT).show();
//					break;
//				case 11:
//					loading.cancel();
//					break;
//				case 12:
//					applyAdapter.update(applies);
//					applyAdapter.notifyDataSetChanged();
//					loading.dismiss();
//					isApplyed=true;
//					break;
//				default:
//					break;
//				}
//			}
//			
//			
//		};
//		
//		loadListData() ;
//		
//		
//		
//	}
//	/*
//	 * 读取订单留言
//	 */
//	public void readApply() {
//		HashMap<String, Object>mHashMap=new HashMap<String, Object>();
//		mHashMap.put("terminalId", terminalId);
//		mHashMap.put("userId", userId);
//		mHashMap.put("loginSign", loginSign);
//		mHashMap.put("sign", MD5Util.createSign(loginSign,userId,terminalId,orderId));
//		mHashMap.put("orderId", orderId);
//		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"shop/SendMessage/changeErpOrderMsgRead.do", "POST", mHashMap.entrySet(), new AsyncListener() {
//			
//			@Override
//			public void onException(Object exceptionInfo) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onComplete(String values) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//	}
//	
//	/**
//	 * 发消息给主线程更新UI
//	 * 
//	 * @param what
//	 */
//	public void sendMessageToHandler(int what,String msg) {
//
//		Message message = mHandler.obtainMessage();
//		message.what = what;
//		message.obj = msg;
//		mHandler.sendMessage(message);
//	}
//	
//	
//	
//	private void loadListData() {
//		
//		loading.show();
//
//		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
//		
//		mHashMap.put("terminalId", terminalId);
//		
//		
//		mHashMap.put("userId",userId);
//		
//		
//		mHashMap.put("loginSign",loginSign);
//
//		mHashMap.put("orderId",orderId);
//		
//		
//		String sign=MD5Util.createSign(loginSign,terminalId,userId,orderId);
//		
//		
//		mHashMap.put("sign",sign);
//		
//		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"shop/Order/getOrder.do", "GET", mHashMap.entrySet(), new AsyncListener() {
//			
//			@Override
//			public void onException(Object exceptionInfo) {
//				sendMessageToHandler(AppConstantByUtil.ERROR,AppConstantByUtil.app_network_exception_tip);
//			}
//			
//			@Override
//			public void onComplete(String values) {
//				System.out.println(values);
//				commodityList.clear();
//				try {
//					JSONObject jsonObject=new JSONObject(values);
//					
//					String msg= jsonObject.getString("message");
//					
//					String code=jsonObject.getString("code");
//					
//					
//					if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
//						
//						nums = jsonObject.getString("nums");
//						orderDate = jsonObject.getString("orderDate");
//						realName = jsonObject.getString("realName");
//						shipAddress = jsonObject.getString("shipAddress");
//						shipMobile = jsonObject.getString("shipMobile");
//						shipName = jsonObject.getString("shipName");
//						shipTel = jsonObject.getString("shipTel");
//						statusId = jsonObject.getString("statusId");
//						statusName = jsonObject.getString("statusName");
//						totalAmount = jsonObject.getString("totalAmount");
//						userName = jsonObject.getString("userName");
//						allowCancel=jsonObject.getString("allowCancel");
//						allowSendGoods=jsonObject.getString("allowSendGoods");
//						allowRemindToUser=jsonObject.getString("allowRemindToUser");
//						allowApplay=jsonObject.getString("allowApplay");
//						businessId=jsonObject.getString("businessId");
//						allowPay=jsonObject.getString("allowPay");
//						printTwo=jsonObject.getString("printTwo");
//						paymentId=jsonObject.getString("paymentId");
//						if (businessId.equals("14")) {
//							memo=jsonObject.getString("memo");
//						}
//						isPay=jsonObject.getString("isPay");
//						if (jsonObject.has("leaveMessage")) {
//							memoMsg=jsonObject.getString("leaveMessage");
//						}
//						if (jsonObject.has("isRead")) {
//							isRead=jsonObject.getString("isRead");
//						}
//						if (businessId.equals("16")) {
//							isYuyue=true;
//							memo=jsonObject.getJSONObject("orderItems").getString("memo");
//							attrName=jsonObject.getJSONObject("orderItems").getString("merchandiseName");
//							attrContactName=jsonObject.getJSONObject("orderItems").getString("contactName");
//							attrPhone=jsonObject.getJSONObject("orderItems").getString("mobile");
//						}
//						if(jsonObject.has("paymentName")){
//							paymentName = jsonObject.getString("paymentName");
//						}else{
//							paymentName = null;
//						}
//						if(jsonObject.has("shipTime")){
//							shipTime = jsonObject.getString("shipTime");
//						}else{
//							shipTime = null;
//						}
//						JSONArray jsonArray=new JSONArray(jsonObject.getString("items"));
//						for (int i = 0; i < jsonArray.length(); i++) {
//							length=jsonArray.length();
//							Commodity o=new Commodity();
//							o.setCount(jsonArray.getJSONObject(i).getString("amount"));
//							o.setStock(jsonArray.getJSONObject(i).getString("inventory"));
//							o.setName(jsonArray.getJSONObject(i).getString("merchandiseName"));
//							o.setNumber(jsonArray.getJSONObject(i).getString("nums"));
//							o.setPrice(jsonArray.getJSONObject(i).getString("price"));
//							o.setUnit(jsonArray.getJSONObject(i).getString("unitName"));
//							commodityList.add(o);
//						}
//						sendMessageToHandler(AppConstantByUtil.SUCCESS,msg);
//						
//					}else{
//						sendMessageToHandler(5,msg);
//					}
//				
//				} catch (Exception e) {
//					e.printStackTrace();
//					sendMessageToHandler(6,AppConstantByUtil.app_json_exception_tip);
//
//				}
//				
//			}
//		});
//	}
//	
//	
//	
//	private void setStateData(String orderId,String processCode,String reason,String reasonContent) {
//		
//		loading.show();
//
//		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
//		
//		mHashMap.put("terminalId", terminalId);
//		
//		
//		mHashMap.put("userId",userId);
//		
//		
//		mHashMap.put("loginSign",loginSign);
//		
//		
//		mHashMap.put("orderId", orderId);
//		
//		mHashMap.put("processCode", processCode);
//		
//		mHashMap.put("reason", reason);
//		mHashMap.put("fileUrl", "");
//		mHashMap.put("content", reasonContent);
//		mHashMap.put("processer", "2");
//		mHashMap.put("channelId", "");
//		
//		String sign=MD5Util.createSign(terminalId,loginSign, userId,orderId,processCode,reason,"",reasonContent,"");
//		
//		
//		mHashMap.put("sign",sign);
//		
//		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"shop/Order/processOrder.do", "GET", mHashMap.entrySet(), new AsyncListener() {
//			
//			@Override
//			public void onException(Object exceptionInfo) {
//				sendMessageToHandler(8,AppConstantByUtil.app_network_exception_tip);
//			}
//			
//			@Override
//			public void onComplete(String values) {
//				System.out.println("-------"+values);
//				try {
//					JSONObject jsonObject=new JSONObject(values);
//					
//					String msg= jsonObject.getString("message");
//					
//					String code=jsonObject.getString("code");
//					
//					
//					if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
//						sendMessageToHandler(7,statusId);
//						
//					}else{
//						sendMessageToHandler(9,msg);
//					}
//				
//				} catch (Exception e) {
//					e.printStackTrace();
//					sendMessageToHandler(10,AppConstantByUtil.app_json_exception_tip);
//
//				}
//				
//			}
//		});
//	}
//	
//
//	
//	@Override
//	public void onClick(View v) {
//		
//		if(v.getId()==R.id.back_btn){
//			if(isChange){
//				Intent intent = new Intent();
//				intent.putExtra("statusId", statusId);
//				intent.putExtra("position", position);
//				this.setResult(RESULT_OK, intent);
//			}
////			this.finish();
//			((MyApplication)getApplication()).finishActivity(OrderDetailActivity.this);
//			overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
//			return;
//		}
//		
//		if(v.getId()==R.id.order_message){
//			vApplyLayer.setVisibility(View.GONE);
//			vShangpingLayer.setVisibility(View.GONE);
//			vDingdanLayer.setVisibility(View.VISIBLE);
//			return;
//		}
//		if(v.getId()==R.id.shangping_message){
//			vDingdanLayer.setVisibility(View.GONE);
//			vShangpingLayer.setVisibility(View.VISIBLE);
//			vApplyLayer.setVisibility(View.GONE);
//			return;
//		}
//		if(v.getId()==R.id.apply_message){
//			vDingdanLayer.setVisibility(View.GONE);
//			vShangpingLayer.setVisibility(View.GONE);
//			vApplyLayer.setVisibility(View.VISIBLE);
//			read_icon.setVisibility(View.GONE);
//			if (!isApplyed) {
//				readApply();
//				loadApply();
//			}
//			return;
//		}
//		if(v.getId()==R.id.order_cancel){
//
//			Intent i = new Intent(OrderDetailActivity.this,OrderCancelActivity.class);
//			i.putExtra("loginSign", loginSign);
//			i.putExtra("userId", userId);
//			i.putExtra("terminalId", terminalId);
//			i.putExtra("orderId", orderId);
//			OrderDetailActivity.this.startActivityForResult(i, 8);
//			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//			return;
//		}
//		if (v.getId()==R.id.order_pay) {
//			Intent intent=new Intent();
//			intent.putExtra("merchandiseName", "订单"+orderId);
//			
//			intent.putExtra("journalId", orderId);
//			double amount=0.00;
//			for (int i = 0; i < commodityList.size(); i++) {
//				amount+=Integer.valueOf(commodityList.get(i).getNumber())*Double.valueOf(commodityList.get(i).getPrice());
//			}
//			intent.putExtra("payAmount",String.valueOf(amount));
//			
//			intent.putExtra("userId",userId);
//			
//			intent.putExtra("loginSign",loginSign);
//			
//			intent.putExtra("terminalId", terminalId);
//			
//			intent.putExtra("packageName", "com.erp.order");
//			
//			intent.putExtra("className", "com.erp.order.activity.MainActivity");
//			
//			intent.putExtra("resultClassName", "com.erp.order.activity.OrderResultActivity");
//			
//			intent.putExtra("shopName", shopName);
//			
//			intent.putExtra("memo", "手机充值");
//			intent.putExtra("shopPhone", shopPhone);
//			intent.putExtra("shopAddress", shopAddress);
//			if (businessId.equals("55")) {//刷卡消费
//				intent.putExtra("charType1", "0");//备付金支付，0未不支持，1为支持
//				intent.putExtra("charType2", "1");//刷卡支付，0未不支持，1为支持
//				intent.putExtra("charType3", "1");//现金支付，0未不支持，1为支持
//				intent.putExtra("payTypeId", "0");//刷到商家本身为0
//			}else if (businessId.equals("13")) {//备付金充值
//				intent.putExtra("charType1", "0");
//				intent.putExtra("charType2", "1");
//				intent.putExtra("charType3", "0");
//				intent.putExtra("payTypeId", "1");//刷到商家本身为0
//			}else if (businessId.equals("00")) {//普通商品
//				intent.putExtra("charType1", "0");
//				intent.putExtra("charType2", "1");
//				intent.putExtra("charType3", "1");
//				intent.putExtra("payTypeId", "0");//刷到商家本身为0
//			}else if (businessId.equals("09")) {//充兑币
//				intent.putExtra("charType1", "0");
//				intent.putExtra("charType2", "1");
//				intent.putExtra("charType3", "0");
//				intent.putExtra("payTypeId", "1");//刷到商家本身为0
//			}else {								  //其他增值业务
//				intent.putExtra("charType1", "1");
//				intent.putExtra("charType2", "1");
//				intent.putExtra("charType3", "0");
//				intent.putExtra("payTypeId", "1");//刷到商家本身为0
//			}
//			intent.putExtra("shopContact", shopContact);
//			intent.putExtra("shopId", shopId);
//			ComponentName componetName = new ComponentName(
//	                "com.duiduifu.app.pay",
//	                "com.duiduifu.app.pay.PayActivity"); 
//			
//	        intent.setAction("android.intent.action.VIEW");  
//	        intent.setComponent(componetName);
//	        startActivityForResult(intent, 0);
//	        ((MyApplication)getApplication()).AppExit(OrderDetailActivity.this);
//	        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//		}
//		if(v.getId()==R.id.order_send){
//	    	dlg.show();
//	    	Window window = dlg.getWindow();
//	    	window.setContentView(R.layout.dialog);
//	    	Button bt1=(Button) window.findViewById(R.id.Button01);
//	    	bt1.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					setStateData(orderId,"2","","");
//					dlg.cancel();
//				}
//			});
//	    	Button btn=(Button) window.findViewById(R.id.Button02);	
//	    	btn.setOnClickListener(new  OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dlg.cancel();
//				}
//			});
//			
//			return;
//		}
//		if(v.getId()==R.id.order_ok){
//			
//	    	dlg.show();
//	    	Window window = dlg.getWindow();
//	    	window.setContentView(R.layout.dialog);
//	    	Button bt1=(Button) window.findViewById(R.id.Button01);
//	    	bt1.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
////					setStateData(orderId,"3","");
//					dlg.cancel();
//				}
//			});
//	    	Button btn=(Button) window.findViewById(R.id.Button02);
//	    	btn.setOnClickListener(new  OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dlg.cancel();
//				}
//			});
////			
//			return;
//		}
//		if (v.getId()==R.id.order_remind) {
//			dlg.show();
//	    	Window window = dlg.getWindow();
//	    	window.setContentView(R.layout.dialog);
//	    	Button bt1=(Button) window.findViewById(R.id.Button01);
//	    	bt1.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					setStateData(orderId,"3","","");
//					dlg.cancel();
//				}
//			});
//	    	Button btn=(Button) window.findViewById(R.id.Button02);
//	    	btn.setOnClickListener(new  OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dlg.cancel();
//				}
//			});
////			
//			return;
//		}
//		if (v.getId()==R.id.order_service) {
//			dlg.show();
//	    	Window window = dlg.getWindow();
//	    	window.setContentView(R.layout.dialog);
//	    	Button bt1=(Button) window.findViewById(R.id.Button01);
//	    	bt1.setOnClickListener(new OnClickListener() {
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					setStateData(orderId,"4","","");
//					dlg.cancel();
//				}
//			});
//	    	Button btn=(Button) window.findViewById(R.id.Button02);
//	    	btn.setOnClickListener(new  OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dlg.cancel();
//				}
//			});
////			
//			return;
//		}
//		if(v.getId()==R.id.order_print){
//			loading.show();
//			new Thread(){
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					super.run();
//					if (isYuyue) {
//						List<PrintDataObject>list=new ArrayList<PrintDataObject>();
//						list.add(new PrintDataObject(shopName, "50", "", "center","","true"));
//						list.add(new PrintDataObject("订单号:  "+orderId, "10", "", "left","","true"));
//						list.add(new PrintDataObject("下单时间:  "+orderDate+"\n", "10", "", "left","","true"));
//						list.add(new PrintDataObject("-------------------------------\n", "10", "", "left","","true"));
//						list.add(new PrintDataObject("预约:  "+attrName, "10", "", "left","","true"));
//						list.add(new PrintDataObject("联系人:  "+attrContactName, "10", "", "left","","true"));
//						list.add(new PrintDataObject("联系电话:  "+attrPhone, "10", "", "left","","true"));
//						list.add(new PrintDataObject("备注:  \n"+"        "+memo, "10", "", "left","","true"));
//						list.add(new PrintDataObject("\n\n", "10", "", "left","","true"));
//						String str=null;
//						try {
//							str=printDev.print(list);
//							if (str.indexOf("G013")==-1) {
//								if (str.indexOf("G009")!=-1) {
//									sendMessageToHandler(10,"没有打印纸");
//									return;
//								}else {
//									sendMessageToHandler(6,"准备失败打印机异常");
//									return;	
//								}
//							}
//						     sendMessageToHandler(10,"");
//						} catch (Exception e) {
//							e.printStackTrace();
//							sendMessageToHandler(10,"准备失败打印机异常");
//						}
//					}else {
//						if (printTwo.equals("1")) {
//							if (businessId.equals("14")) {
//								if (!memo.equals("")) {
//									for (int j = 0; j < 3; j++) {
//										List<PrintDataObject>list=new ArrayList<PrintDataObject>();
//										list.add(new PrintDataObject(memo, "50", "", "center","","true"));
//										list.add(new PrintDataObject(shopName+"\n", "20", "", "center","","true"));
//										list.add(new PrintDataObject("订单号:  "+orderId, "10", "", "left","","true"));
//										list.add(new PrintDataObject("下单时间:  "+orderDate, "10", "", "left","","true"));
//										list.add(new PrintDataObject("操作员:   "+shopContact, "10", "", "left","","true"));
//										if (paymentId.equals("1")) {
//											list.add(new PrintDataObject("状态:  "+"现场支付", "10", "", "left","","true"));
//										}else {
//											list.add(new PrintDataObject("状态:  "+"已在线付款", "10", "", "left","","true"));
//										}
//										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//										list.add(new PrintDataObject("", "", "", "","","true"));
//										int num = 0;
//										double totalsPrice=0.00;
//										if (j==0) {
//											list.add(new PrintDataObject("名称                    数量", "10", "", "left", "", "true"));
//											for (int i = 0; i < commodityList.size(); i++) {
//												list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"                          "+commodityList.get(i).getNumber(), "20", "", "left", "", "true"));
//												num+=Integer.parseInt(commodityList.get(i).getNumber());
//												totalsPrice+=Integer.parseInt(commodityList.get(i).getNumber())*Double.parseDouble(commodityList.get(i).getPrice());
//											}
//										}else {
//											list.add(new PrintDataObject("名称          数量        小计", "10", "", "left", "", "true"));
//											for (int i = 0; i < commodityList.size(); i++) {
//												list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"               "+commodityList.get(i).getNumber()+"           "+Double.parseDouble(commodityList.get(i).getPrice()), "10", "", "left", "", "true"));
//												num+=Integer.parseInt(commodityList.get(i).getNumber());
//												totalsPrice+=Integer.parseInt(commodityList.get(i).getNumber())*Double.parseDouble(commodityList.get(i).getPrice());
//											}
//										}
//										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//										if (j==0) {
//											list.add(new PrintDataObject("备注:"+memoMsg+"\n", "10", "", "left","","true"));
//										}else if (j==1) {
//											list.add(new PrintDataObject("总计:                  "+totalsPrice, "10", "", "left","","true"));
//											list.add(new PrintDataObject("折扣:                  "+"0.00", "10", "", "left","","true"));
//											list.add(new PrintDataObject("支付金额:              "+totalsPrice, "10", "", "left","","true"));
//											list.add(new PrintDataObject("找赎:                  "+"0.00", "10", "", "left","","true"));
//										}else if(j==2){
//											list.add(new PrintDataObject("总计:                  "+totalsPrice, "10", "", "left","","true"));
//											list.add(new PrintDataObject("折扣:                  "+"0.00", "10", "", "left","","true"));
//											list.add(new PrintDataObject("支付金额:              "+totalsPrice, "10", "", "left","","true"));
//											list.add(new PrintDataObject("找赎:                  "+"0.00", "10", "", "left","","true"));
//										}
//										List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
//										Bitmap bitmap = null;
//										list2.add(new PrintDataObject("\n\n"));
//										if (j==2) {
//											list.add(new PrintDataObject("欢迎再次光临"+shopName, "10", "", "center","","true"));
//											list.add(new PrintDataObject("地址:"+shopAddress, "10", "", "center","","true"));
//											list.add(new PrintDataObject("电话"+shopPhone, "10", "", "center","","true"));
//											list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//											list.add(new PrintDataObject("我家微店在这里", "10", "", "center","","true"));
//											list.add(new PrintDataObject("http://www.duiduifu.com", "10", "", "center","","true"));
//											String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(terminalId.getBytes(), Base64.DEFAULT).replaceAll(" ", "");
//											if (bString!=null) {
//												Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//												Matcher bm = p.matcher(bString);
//												bString = bm.replaceAll("");
//											}
//											bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
//										}
//										String str=null;
//										try {
//											str=printDev.print(list);
//											if (j==2) {
//												str=printDev.printBmp(bitmap, true);
//												str=printDev.printBmp(BitmapFactory.decodeResource(OrderDetailActivity.this.getResources(), R.drawable.icon), true);
//											}
//											
//											str=printDev.print(list2);
//											if (str.indexOf("G013")==-1) {
//												if (str.indexOf("G009")!=-1) {
//													sendMessageToHandler(10,"没有打印纸");
//													return;
//												}else {
//													sendMessageToHandler(10,"准备失败打印机异常");
//													return;
//												}
//											}
//											System.out.println("------------长度"+list.size());
//											list.clear();
//											sendMessageToHandler(10,"");
//										} catch (Exception e) {
//											sendMessageToHandler(10,"准备失败打印机异常");
//										}
//									}
//								}else {
//									for (int j = 0; j < 2; j++) {
//										List<PrintDataObject>list=new ArrayList<PrintDataObject>();
//										list.add(new PrintDataObject(shopName, "50", "", "center","","true"));
//										if (j==0) {
//											list.add(new PrintDataObject("（商户留存联）", "10", "", "center","","true"));
//										}else {
//											list.add(new PrintDataObject("（顾客留存联）", "10", "", "center","","true"));
//										}
//										list.add(new PrintDataObject("单号:  "+orderId, "10", "", "left","","true"));
//										list.add(new PrintDataObject("时间:  "+orderDate, "10", "", "left","","true"));
//										list.add(new PrintDataObject("操作员:   "+shopContact, "10", "", "left","","true"));
//										if(shipName!=null && !"".equals(shipName)){
//											list.add(new PrintDataObject("收货人:   "+shipName, "10", "", "left","","true"));
//										}
//										if(shipMobile!=null && !"".equals(shipMobile)){
//											list.add(new PrintDataObject("收货人手机: "+shipMobile, "10", "", "left","","true"));
//										}
//										if(shipTel!=null && !"".equals(shipTel)){
//											list.add(new PrintDataObject("收货人电话: "+shipTel, "10", "", "left","","true"));
//										}
//										if(shipAddress!=null && !"".equals(shipAddress)){
//											list.add(new PrintDataObject("收货地址:  "+shipAddress, "10", "", "left","","true"));
//										}
//										if(paymentName!=null && !"".equals(paymentName)){
//											list.add(new PrintDataObject("支付方式:  "+paymentName, "10", "", "left","","true"));
//										}
//										if(shipTime!=null && !"".equals(shipTime)){
//											list.add(new PrintDataObject("配送时间:  "+shipTime, "10", "", "left","","true"));
//										}
//										if(memoMsg!=null && !"".equals(memoMsg)){
//											list.add(new PrintDataObject("用户留言:  "+memoMsg, "10", "", "left","","true"));
//										}
//										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//										list.add(new PrintDataObject("", "", "", "","","true"));
//										list.add(new PrintDataObject("名称       数量       单价", "10", "", "left", "", "true"));
//										for (int i = 0; i < commodityList.size(); i++) {
//											list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"           "+commodityList.get(i).getNumber()+"         "+commodityList.get(i).getPrice(), "10", "", "left", "", "true"));
//										}
//										list.add(new PrintDataObject("订单状态:"+statusName, "10", "", "left","","true"));
//										list.add(new PrintDataObject("", "", "", "","","true"));
//										list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "", "center","","true"));
//										list.add(new PrintDataObject("", "", "", "","","true"));
//										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//										list.add(new PrintDataObject("总计:                       "+totalAmount, "10", "", "left","","true"));
//										if (isPay.equals("1")) {
//											list.add(new PrintDataObject("已付:                       "+totalAmount, "10", "", "left","","true"));
//										}else{
//											list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
//										}
//										list.add(new PrintDataObject("", "", "", "","","true"));
//										list.add(new PrintDataObject("", "", "", "","","true"));
//										if (j==0) {
//											list.add(new PrintDataObject("客户签名: _____________________\n\n", "10", "left", "", "true"));
//										}
//										list.add(new PrintDataObject("欢迎再次光临"+shopName, "10", "", "center","","true"));
//										list.add(new PrintDataObject("地址:"+shopAddress, "10", "", "center","","true"));
//										list.add(new PrintDataObject("电话"+shopPhone, "10", "", "center","","true"));
//										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//										if (j==1) {
//											list.add(new PrintDataObject("我家微店在这里", "10", "", "center","","true"));
//											list.add(new PrintDataObject("http://www.duiduifu.com", "10", "", "center","","true"));
//										}
//										List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
//										list2.add(new PrintDataObject("\n\n"));
//										String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(terminalId.getBytes(), Base64.DEFAULT);
//										if (bString!=null) {
//											Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//											Matcher bm = p.matcher(bString);
//											bString = bm.replaceAll("");
//										}
//										Bitmap bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
//										String str=null;
//										try {
//											str=printDev.print(list);
//											if (j==1) {
//												str=printDev.printBmp(bitmap, true);
//											}
//											str=printDev.printBmp(BitmapFactory.decodeResource(OrderDetailActivity.this.getResources(), R.drawable.icon), true);
//											
//											str=printDev.print(list2);
//											if (str.indexOf("G013")==-1) {
//												if (str.indexOf("G009")!=-1) {
//													sendMessageToHandler(10, "没有打印纸");
//													return;
//												}else {
//													sendMessageToHandler(10, "准备失败打印机异常");
//													return;
//												}
//											}
//											System.out.println("------------长度"+list.size());
//											list.clear();
//											sendMessageToHandler(10, "");
//										} catch (Exception e) {
//											sendMessageToHandler(10, "准备失败打印机异常");
//										}
//									}
//								}
//							}else {
//								for (int j = 0; j < 2; j++) {
//									List<PrintDataObject>list=new ArrayList<PrintDataObject>();
//									list.add(new PrintDataObject(shopName, "50", "", "center","","true"));
//									if (j==0) {
//										list.add(new PrintDataObject("（商户留存联）", "10", "", "center","","true"));
//									}else {
//										list.add(new PrintDataObject("（顾客留存联）", "10", "", "center","","true"));
//									}
//									list.add(new PrintDataObject("单号:  "+orderId, "10", "", "left","","true"));
//									list.add(new PrintDataObject("时间:  "+orderDate, "10", "", "left","","true"));
//									list.add(new PrintDataObject("操作员:   "+shopContact, "10", "", "left","","true"));
//									if(shipName!=null && !"".equals(shipName)){
//										list.add(new PrintDataObject("收货人:   "+shipName, "10", "", "left","","true"));
//									}
//									if(shipMobile!=null && !"".equals(shipMobile)){
//										list.add(new PrintDataObject("收货人手机: "+shipMobile, "10", "", "left","","true"));
//									}
//									if(shipTel!=null && !"".equals(shipTel)){
//										list.add(new PrintDataObject("收货人电话: "+shipTel, "10", "", "left","","true"));
//									}
//									if(shipAddress!=null && !"".equals(shipAddress)){
//										list.add(new PrintDataObject("收货地址:  "+shipAddress, "10", "", "left","","true"));
//									}
//									if(paymentName!=null && !"".equals(paymentName)){
//										list.add(new PrintDataObject("支付方式:  "+paymentName, "10", "", "left","","true"));
//									}
//									if(shipTime!=null && !"".equals(shipTime)){
//										list.add(new PrintDataObject("配送时间:  "+shipTime, "10", "", "left","","true"));
//									}
//									if(memoMsg!=null && !"".equals(memoMsg)){
//										list.add(new PrintDataObject("用户留言:  "+memoMsg, "10", "", "left","","true"));
//									}
//									list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//									list.add(new PrintDataObject("", "", "", "","","true"));
//									list.add(new PrintDataObject("名称       数量       单价", "10", "", "left", "", "true"));
//									for (int i = 0; i < commodityList.size(); i++) {
//										list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"           "+commodityList.get(i).getNumber()+"         "+commodityList.get(i).getPrice(), "10", "", "left", "", "true"));
//									}
//									list.add(new PrintDataObject("订单状态:"+statusName, "10", "", "left","","true"));
//									list.add(new PrintDataObject("", "", "", "","","true"));
//									list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "", "center","","true"));
//									list.add(new PrintDataObject("", "", "", "","","true"));
//									list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//									list.add(new PrintDataObject("总计:                       "+totalAmount, "10", "", "left","","true"));
//									if (isPay.equals("1")) {
//										list.add(new PrintDataObject("已付:                       "+totalAmount, "10", "", "left","","true"));
//									}else{
//										list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
//									}
//									list.add(new PrintDataObject("", "", "", "","","true"));
//									list.add(new PrintDataObject("", "", "", "","","true"));
//									if (j==0) {
//										list.add(new PrintDataObject("客户签名: _____________________\n\n", "10", "left", "", "true"));
//									}
//									list.add(new PrintDataObject("欢迎再次光临"+shopName, "10", "", "center","","true"));
//									list.add(new PrintDataObject("地址:"+shopAddress, "10", "", "center","","true"));
//									list.add(new PrintDataObject("电话"+shopPhone, "10", "", "center","","true"));
//									list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//									if (j==1) {
//										list.add(new PrintDataObject("我家微店在这里", "10", "", "center","","true"));
//										list.add(new PrintDataObject("http://www.duiduifu.com", "10", "", "center","","true"));
//									}
//									List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
//									list2.add(new PrintDataObject("\n\n"));
//									String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(terminalId.getBytes(), Base64.DEFAULT);
//									if (bString!=null) {
//										Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//										Matcher bm = p.matcher(bString);
//										bString = bm.replaceAll("");
//									}
//									Bitmap bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
//									String str=null;
//									try {
//										str=printDev.print(list);
//										if (j==1) {
//											str=printDev.printBmp(bitmap, true);
//										}
//										str=printDev.printBmp(BitmapFactory.decodeResource(OrderDetailActivity.this.getResources(), R.drawable.icon), true);
//										
//										str=printDev.print(list2);
//										if (str.indexOf("G013")==-1) {
//											if (str.indexOf("G009")!=-1) {
//												sendMessageToHandler(10, "没有打印纸");
//												return;
//											}else {
//												sendMessageToHandler(10, "准备失败打印机异常");
//												return;
//											}
//										}
//										System.out.println("------------长度"+list.size());
//										list.clear();
//										sendMessageToHandler(10, "");
//									} catch (Exception e) {
//										sendMessageToHandler(10, "准备失败打印机异常");
//									}
//								}
//							}
//							
//						}else {
//							List<PrintDataObject>list=new ArrayList<PrintDataObject>();
//							list.add(new PrintDataObject(shopName, "50", "", "center","","true"));
//							list.add(new PrintDataObject("单号:  "+orderId, "10", "", "left","","true"));
//							list.add(new PrintDataObject("时间:  "+orderDate, "10", "", "left","","true"));
//							list.add(new PrintDataObject("操作员:   "+shopContact, "10", "", "left","","true"));
//							if(shipName!=null && !"".equals(shipName)){
//								list.add(new PrintDataObject("收货人:   "+shipName, "10", "", "left","","true"));
//							}
//							if(shipMobile!=null && !"".equals(shipMobile)){
//								list.add(new PrintDataObject("收货人手机: "+shipMobile, "10", "", "left","","true"));
//							}
//							if(shipTel!=null && !"".equals(shipTel)){
//								list.add(new PrintDataObject("收货人电话: "+shipTel, "10", "", "left","","true"));
//							}
//							if(shipAddress!=null && !"".equals(shipAddress)){
//								list.add(new PrintDataObject("收货地址:  "+shipAddress, "10", "", "left","","true"));
//							}
//							if(paymentName!=null && !"".equals(paymentName)){
//								list.add(new PrintDataObject("支付方式:  "+paymentName, "10", "", "left","","true"));
//							}
//							if(shipTime!=null && !"".equals(shipTime)){
//								list.add(new PrintDataObject("配送时间:  "+shipTime, "10", "", "left","","true"));
//							}
//							list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//							list.add(new PrintDataObject("", "", "", "","","true"));
//							list.add(new PrintDataObject("名称       数量       单价", "10", "", "left", "", "true"));
//							for (int i = 0; i < commodityList.size(); i++) {
//								list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"           "+commodityList.get(i).getNumber()+"         "+commodityList.get(i).getPrice(), "10", "", "left", "", "true"));
//							}
//							list.add(new PrintDataObject("订单状态:"+statusName, "10", "", "left","","true"));
//							list.add(new PrintDataObject("", "", "", "","","true"));
//							list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "", "center","","true"));
//							list.add(new PrintDataObject("", "", "", "","","true"));
//							list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//							list.add(new PrintDataObject("总计:                       "+totalAmount, "10", "", "left","","true"));
//							if (isPay.equals("1")) {
//								list.add(new PrintDataObject("已付:                       "+totalAmount, "10", "", "left","","true"));
//							}else{
//								list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
//							}
//							list.add(new PrintDataObject("", "", "", "","","true"));
//							list.add(new PrintDataObject("", "", "", "","","true"));
//							list.add(new PrintDataObject("欢迎再次光临"+shopName, "10", "", "center","","true"));
//							list.add(new PrintDataObject("地址:"+shopAddress, "10", "", "center","","true"));
//							list.add(new PrintDataObject("电话"+shopPhone, "10", "", "center","","true"));
//							list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
//							list.add(new PrintDataObject("扫一扫，有惊喜！", "10", "", "center","","true"));
//							List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
//							list2.add(new PrintDataObject("\n\n"));
//							String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(terminalId.getBytes(), Base64.DEFAULT);
//							if (bString!=null) {
//								Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//								Matcher bm = p.matcher(bString);
//								bString = bm.replaceAll("");
//							}
//							Bitmap bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
//							BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.icon);
//							Bitmap iconBitmap=bd.getBitmap();
//							
//							BitmapFactory.decodeResource(OrderDetailActivity.this.getResources(), R.drawable.icon);
//							String str=null;
//							try {
//								str=printDev.print(list);
//								str=printDev.printBmp(bitmap, true);
//								str=printDev.printBmp(BitmapFactory.decodeResource(OrderDetailActivity.this.getResources(), R.drawable.icon), true);
//								str=printDev.print(list2);
//								if (str.indexOf("G013")==-1) {
//									if (str.indexOf("G009")!=-1) {
//										sendMessageToHandler(10, "没有打印纸");
//										return;
//									}else {
//										sendMessageToHandler(10, "准备失败打印机异常");
//										return;
//									}
//								}
//								list.clear();
//								sendMessageToHandler(10, "");
//							} catch (Exception e) {
//								sendMessageToHandler(10, "准备失败打印机异常");
//							}
//						}
//					}
////					sendMessageToHandler(11, "");
//				}
//				
//			}.start();
//			
//			return;
//		}
//		
//		
//	}
//	/*
//	 * 获取留言列表
//	 */
//	private void loadApply() {
//		// TODO Auto-generated method stub
//		loading.show();
//		HashMap<String, Object>mHashMap=new HashMap<String, Object>();
//		mHashMap.put("loginSign", loginSign);
//		mHashMap.put("userId", userId);
//		mHashMap.put("terminalId", terminalId);
//		mHashMap.put("orderId", orderId);
//		mHashMap.put("currentPage", "1");
//		mHashMap.put("pageSize", "10");
//		mHashMap.put("sign", MD5Util.createSign(loginSign,terminalId,userId,orderId));
//		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"shop/Order/getOrderMessageByOrderIdForTerminal.do", "POST", mHashMap.entrySet(), new AsyncListener() {
//			
//			@Override
//			public void onException(Object exceptionInfo) {
//				// TODO Auto-generated method stub
//				sendMessageToHandler(AppConstantByUtil.ERROR, AppConstantByUtil.app_network_exception_tip);
//			}
//			
//			@Override
//			public void onComplete(String values) {
//				// TODO Auto-generated method stub
//				System.out.println(values);
//				try {
//					JSONObject jsonObject=new JSONObject(values);
//					if (jsonObject.getString("code").equals("1")) {
//						JSONArray jsonArray=new JSONArray(jsonObject.getString("erpOrderMessageList"));
//						for (int i = 0; i < jsonArray.length(); i++) {
//							Apply apply=new Apply();
//							apply.setContents(jsonArray.getJSONObject(i).getString("content"));
//							apply.setCreat_name(jsonArray.getJSONObject(i).getString("createName"));
//							apply.setCreat_time(jsonArray.getJSONObject(i).getString("createTime"));
//							apply.setImg(jsonArray.getJSONObject(i).getString("attach"));
//							apply.setOrder_id(jsonArray.getJSONObject(i).getString("orderId"));
//							applies.add(apply);
//						}
//						sendMessageToHandler(12, "成功");
//					}else {
//						sendMessageToHandler(AppConstantByUtil.ERROR, jsonObject.getString("msg"));
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					sendMessageToHandler(AppConstantByUtil.ERROR, AppConstantByUtil.app_json_exception_tip);
//				}
//			}
//		});
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		dlg.dismiss();
//		loading.dismiss();
//		super.onDestroy();
//	}
//	
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		if(requestCode == 8 && resultCode==RESULT_OK){
//			String reason = data.getStringExtra("reason");
//			setStateData(orderId,"1",reason,data.getStringExtra("reason"));
//		}
//	}
//	
//	@Override
//	public void onBackPressed() {
//		return;
//	}
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode==KeyEvent.KEYCODE_HOME) {
//			((MyApplication)getApplication()).AppExit(OrderDetailActivity.this);
//			overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
//		}else if(keyCode==KeyEvent.KEYCODE_BACK){
//			if(isChange){
//				Intent intent = new Intent();
//				intent.putExtra("statusId", statusId);
//				intent.putExtra("position", position);
//				this.setResult(RESULT_OK, intent);
//			}
//			((MyApplication)getApplication()).finishActivity(OrderDetailActivity.this);
//			overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
//		}
//		return true;
//	}
//	/*
//	 * 查看留言大图
//	 */
//	@Override
//	public void ToBigPic(String imgUrl) {
//		// TODO Auto-generated method stub
//		Intent intent=new Intent(OrderDetailActivity.this, BigPicActivity.class);
//		intent.putExtra("imgUrl", imgUrl);
//		startActivity(intent);
//		overridePendingTransition(R.anim.push_left_out, R.anim.push_right_in);
//	}
//}
