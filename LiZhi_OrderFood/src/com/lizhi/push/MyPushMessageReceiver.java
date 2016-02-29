package com.lizhi.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.centerm.smartpos.bean.PrintDataObject;
import com.centerm.smartpos.dev.IPrintDev;
import com.centerm.smartpos.dev.impl.SmartPayFactory;
import com.centerm.smartpos.sys.ISystemInf;
import com.centerm.smartpos.sys.impl.SysImplFactory;
import com.centerm.zxing.QRCodeEncoder;
import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.http.UploadMessage;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.bean.ERP_OrderDetail;
import com.lizhi.util.Constants;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 0 - Success 10001 - Network Problem 30600 - Internal
 * Server Error 30601 - Method Not Allowed 30602 - Request Params Not Valid
 * 30603 - Authentication Failed 30604 - Quota Use Up Payment Required 30605 -
 * Data Required Not Found 30606 - Request Time Expires Timeout 30607 - Channel
 * Token Timeout 30608 - Bind Relation Not Found 30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {
	/** TAG to Log */
	public static final String TAG = "pos";
	private Context context;
	MediaPlayer mp;
	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.i(TAG, responseString);
		this.context = context;
		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		/*
		 * if (errorCode == 0) { Utils.setBind(context, true); }
		 */
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
		//Constants.channelId = channelId;
		
		// 生产环境使用
		Constants.channelId=channelId;
		//Constants.terminalId=getTermSN();
		 
		// if (!Utils.hasBind(context))
		updatePush(channelId);
	}

	private void updatePush(String channelId) {

		String sign = MD5Util
				.createSign(channelId + Constants.terminalId, "03");
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("channelId", channelId);
		mHashMap.put("terminalId", Constants.terminalId);
		if (null != Constants.shop)
			mHashMap.put("userId", Constants.shop.getUserId());
		mHashMap.put("model", "03");
		mHashMap.put("sign", sign);
		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "ShopLogin/addDevice.do", "POST",
				mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onException(Object exceptionInfo) {

					}

					@Override
					public void onComplete(String values) {

						Log.i("pos adddevice", values);
						if (JsonUtil.isSuccess(values)) {
						}
					}
				});
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		this.context = context;
		String messageString = "透传消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		Log.i(TAG, messageString);

		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(message)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(message);
				String orderId = null;
				if (customJson.has("orderId")) {
					orderId = customJson.getString("orderId");
				}
				String isPrint = null;
				
				if (customJson.has("isPrint")) {
					isPrint = customJson.getString("isPrint");
				}
				/*SoundPool soundPool;
				soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
			 
				money= soundPool.load(context, R.raw.ding, 1);
			 
				soundPool
						.setOnLoadCompleteListener(new OnLoadCompleteListener() {

							public void onLoadComplete(SoundPool soundPool,
									int sampleId, int status) {
								Log.i("打印2", status+"");
								soundPool.play(money, 1, 1, 0, 0, 2);
								
							}
						});*/
				
                /*mp.setOnCompletionListener(new OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                 });*/
				if ("0".equals(isPrint)) {					
					mp = MediaPlayer.create(context, R.raw.ding);
	                mp.start();
					new GetOrderDetailTask().execute(orderId);
					Intent intent = new Intent();
					intent.setAction("com.duiduifu.fragment.MessageFragment1");
					context.sendBroadcast(intent);
				}if(orderId.equals(Constants.orderid)) {
					
					Intent intent = new Intent();
					intent.setAction("com.duiduifu.fragment.CashierFragment1");
					intent.putExtra("orderid", orderId);
					context.sendBroadcast(intent);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	private void printOrder(ERP_OrderDetail detail) {
		Intent intent = new Intent();
		intent.putExtra("orderId", "");
		intent.setAction("com.duiduifu.fragment.OrderFragment1");
		context.sendBroadcast(intent);
		IPrintDev printDev = SmartPayFactory.getPrinterDev();
		for (int j = 0; j < 2; j++) {
			List<PrintDataObject> list = new ArrayList<PrintDataObject>();
			list.add(new PrintDataObject(Constants.shop.getShopName(), "50",
					"", "center", "", "true"));
			if (j == 0) {
				list.add(new PrintDataObject("（商户留存联）", "10", "", "center", "",
						"true"));
			} else {
				list.add(new PrintDataObject("（顾客留存联）", "10", "", "center", "",
						"true"));
			}
			list.add(new PrintDataObject("单号:  " + detail.getOrderId(), "10",
					"", "left", "", "true"));
			list.add(new PrintDataObject("时间:  " + detail.getOrderDate(), "10",
					"", "left", "", "true"));
			list.add(new PrintDataObject(
					"操作员:   " + Constants.shop.getUserId(), "10", "", "left",
					"", "true"));
			if (!StringUtil.isEmpty(detail.getShipName())) {
				list.add(new PrintDataObject("收货人:   " + detail.getShipName(),
						"10", "", "left", "", "true"));
			}
			if (!StringUtil.isEmpty(detail.getShipMobile())) {
				list.add(new PrintDataObject(
						"收货人手机: " + detail.getShipMobile(), "10", "", "left",
						"", "true"));
			}
			if (!StringUtil.isEmpty(detail.getShipTel())) {
				list.add(new PrintDataObject("收货人电话: " + detail.getShipTel(),
						"10", "", "left", "", "true"));
			}
			if (!StringUtil.isEmpty(detail.getShipAddress())) {
				list.add(new PrintDataObject("收货地址:  "
						+ detail.getShipAddress(), "10", "", "left", "", "true"));
			}
			if (!StringUtil.isEmpty(detail.getPaymentName())) {
				list.add(new PrintDataObject("支付方式:  "
						+ detail.getPaymentName(), "10", "", "left", "", "true"));
			}
			if (!StringUtil.isEmpty(detail.getShipTime())) {
				list.add(new PrintDataObject("配送时间:  " + detail.getShipTime(),
						"10", "", "left", "", "true"));
			}
			if (!StringUtil.isEmpty(detail.getLeaveMessage())) {
				list.add(new PrintDataObject("用户留言:  "
						+ detail.getLeaveMessage(), "10", "", "left", "",
						"true"));
			}
			list.add(new PrintDataObject("------------------------------", "",
					"", "", "", "true"));
			list.add(new PrintDataObject("", "", "", "", "", "true"));
			list.add(new PrintDataObject("名称       数量       单价", "10", "",
					"left", "", "true"));
			for (int i = 0; i < detail.getItems().size(); i++) {
				list.add(new PrintDataObject(detail.getItems().get(i)
						.getMerchandiseName()
						+ "\n"
						+ "           "
						+ detail.getItems().get(i).getNums()
						+ "         "
						+ detail.getItems().get(i).getPrice(), "10", "",
						"left", "", "true"));
			}
			list.add(new PrintDataObject("订单状态:" + detail.getStatusName(),
					"10", "", "left", "", "true"));
			list.add(new PrintDataObject("", "", "", "", "", "true"));
			// list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "",
			// "center","","true"));
			list.add(new PrintDataObject("", "", "", "", "", "true"));
			list.add(new PrintDataObject("------------------------------", "",
					"", "", "", "true"));
			list.add(new PrintDataObject("总计:                       "
					+ detail.getTotalAmount(), "10", "", "left", "", "true"));
			if (detail.getIsPay().equals("1")) {
				list.add(new PrintDataObject("已付:                       "
						+ detail.getTotalAmount(), "10", "", "left", "", "true"));
			} else {
				list.add(new PrintDataObject(
						"已付:                       " + "0", "10", "", "left",
						"", "true"));
			}
			list.add(new PrintDataObject("", "", "", "", "", "true"));
			list.add(new PrintDataObject("", "", "", "", "", "true"));
			if (j == 0) {
				list.add(new PrintDataObject("客户签名: _____________________\n\n",
						"10", "left", "", "true"));
			}
			list.add(new PrintDataObject("欢迎再次光临"
					+ Constants.shop.getShopName(), "10", "", "center", "",
					"true"));
			list.add(new PrintDataObject("地址:"
					+ Constants.shop.getShopAddress(), "10", "", "center", "",
					"true"));
			list.add(new PrintDataObject("电话" + Constants.shop.getShopPhone(),
					"10", "", "center", "", "true"));
			list.add(new PrintDataObject("------------------------------", "",
					"", "", "", "true"));
			if (j == 1) {
				// list.add(new PrintDataObject("我家微店在这里", "10", "",
				// "center","","true"));
				// list.add(new PrintDataObject("http://www.duiduifu.com", "10",
				// "", "center","","true"));
			}
			List<PrintDataObject> list2 = new ArrayList<PrintDataObject>();
			list2.add(new PrintDataObject("\n\n"));
			String bString = AppConstantByUtil.PRINT_IP
					+ "reg?s="
					+ Base64.encodeToString(Constants.shop.getShopId()
							.getBytes(), Base64.DEFAULT)
					+ "&tm="
					+ Base64.encodeToString(Constants.terminalId.getBytes(),
							Base64.DEFAULT);
			if (bString != null) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher bm = p.matcher(bString);
				bString = bm.replaceAll("");
			}
			Bitmap bitmap = QRCodeEncoder.getQRCodeBmp(bString, 300);
			String str = null;
			try {
				str = printDev.print(list);
				if (j == 1) {
					str = printDev.printBmp(bitmap, true);
				}
				// str=printDev.printBmp(BitmapFactory.decodeResource(context.getResources(),
				// R.drawable.icon), true);

				str = printDev.print(list2);
				if (str.indexOf("G013") == -1) {
					if (str.indexOf("G009") != -1) {
						sendMessageToHandler(10, "没有打印纸");
						return;
					} else {
						sendMessageToHandler(10, "准备失败打印机异常");
						return;
					}
				}
				System.out.println("------------长度" + list.size());
				list.clear();
				sendMessageToHandler(11, "");
			} catch (Exception e) {
				sendMessageToHandler(10, "准备失败打印机异常");
			}
		}
	}

	private void printYuyue(ERP_OrderDetail detail) {
		Intent intent = new Intent();
		intent.putExtra("orderId", "");
		intent.setAction("com.duiduifu.fragment.OrderFragment5");
		context.sendBroadcast(intent);
		Log.i("pos", "print yuyue");
		IPrintDev printDev = SmartPayFactory.getPrinterDev();
		List<PrintDataObject> list = new ArrayList<PrintDataObject>();
		list.add(new PrintDataObject(Constants.shop.getShopName(), "50", "",
				"center", "", "true"));
		list.add(new PrintDataObject("订单号:  " + detail.getOrderId(), "10", "",
				"left", "", "true"));
		list.add(new PrintDataObject("下单时间:  " + detail.getOrderDate() + "\n",
				"10", "", "left", "", "true"));
		list.add(new PrintDataObject("-------------------------------\n", "10",
				"", "left", "", "true"));
		if (detail.getOrderItems() != null) {
			list.add(new PrintDataObject("预约:  "
					+ detail.getOrderItems().getMerchandiseName(), "10", "",
					"left", "", "true"));
			list.add(new PrintDataObject("联系人:  "
					+ detail.getOrderItems().getContactName(), "10", "",
					"left", "", "true"));
			list.add(new PrintDataObject("联系电话:  "
					+ detail.getOrderItems().getMobile(), "10", "", "left", "",
					"true"));
			list.add(new PrintDataObject("备注:  \n" + "        "
					+ detail.getOrderItems().getMemo(), "10", "", "left", "",
					"true"));
		}
		list.add(new PrintDataObject("\n\n", "10", "", "left", "", "true"));
		String str = null;
		try {
			str = printDev.print(list);
			if (str.indexOf("G013") == -1) {
				if (str.indexOf("G009") != -1) {
					sendMessageToHandler(10, "没有打印纸");
					return;
				} else {
					sendMessageToHandler(6, "准备失败打印机异常");
					return;
				}
			}
			sendMessageToHandler(11,"");
		} catch (Exception e) {
			e.printStackTrace();
			sendMessageToHandler(10, "准备失败打印机异常");
		}
	}

	private void printYuDing(ERP_OrderDetail detail) {
		Intent intent = new Intent();
		intent.putExtra("orderId", "");
		intent.setAction("com.duiduifu.fragment.OrderFragment4");
		context.sendBroadcast(intent);
		IPrintDev printDev = SmartPayFactory.getPrinterDev();
		List<PrintDataObject> list = new ArrayList<PrintDataObject>();
		list.add(new PrintDataObject(Constants.shop.getShopName(), "50", "",
				"center", "", "true"));
		list.add(new PrintDataObject("订单号:  " + detail.getOrderId(), "10", "",
				"left", "", "true"));
		list.add(new PrintDataObject("下单时间:  " + detail.getOrderDate() + "\n",
				"10", "", "left", "", "true"));
		list.add(new PrintDataObject("-------------------------------\n", "10",
				"", "left", "", "true"));
		if (detail.getItems() != null && detail.getItems().size() > 0)
			list.add(new PrintDataObject("预定:  "
					+ detail.getItems().get(0).getMerchandiseName(), "10", "",
					"left", "", "true"));
		list.add(new PrintDataObject("联系人:  " + detail.getOrderItems().getContactName(), "10", "",
				"left", "", "true"));
		list.add(new PrintDataObject("联系电话:  " + detail.getOrderItems().getMobile(), "10",
				"", "left", "", "true"));
		list.add(new PrintDataObject("备注:  \n" + "        " + detail.getMemo(),
				"10", "", "left", "", "true"));
		list.add(new PrintDataObject("\n\n", "10", "", "left", "", "true"));
		String str = null;
		try {
			str = printDev.print(list);
			if (str.indexOf("G013") == -1) {
				if (str.indexOf("G009") != -1) {
					sendMessageToHandler(10, "没有打印纸");
					return;
				} else {
					sendMessageToHandler(6, "准备失败打印机异常");
					return;
				}
			}
			sendMessageToHandler(11, "");
		} catch (Exception e) {
			e.printStackTrace();
			sendMessageToHandler(10, "准备失败打印机异常");
		}
	}

	// 获取圈子
	private class GetOrderDetailTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(String result) {

			if (StringUtil.isEmpty(result)) {
			} else {
				Log.i("pos", result);
				if (JsonUtil.isSuccess(result)) {
					ERP_OrderDetail order_detail = JsonUtil
							.getOrderDetail(result);
					if ("16".equals(order_detail.getBusinessId())) {
						printYuyue(order_detail);
					} else if ("17".equals(order_detail.getBusinessId())) {
						printYuDing(order_detail);
					} else {
						printOrder(order_detail);
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
			map.put("orderId", params[0]);
			String sign = MD5Util.createSign(Constants.loginSign
					+ Constants.terminalId + Constants.shop.getUserId()
					+ params[0]);
			map.put("sign", sign);
			Log.i("pos", map.toString());
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

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 10:
				String m10 = msg.obj.toString();
				TipsToast.makeText(context, m10, Toast.LENGTH_SHORT).show();
				break;
			case 11:
				mp.release();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + title + "\" description=\""
				+ description + "\" customContent=" + customContentString;
		Log.d(TAG, notifyString);

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (!customJson.isNull("mykey")) {
					myvalue = customJson.getString("mykey");
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		Log.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			Utils.setBind(context, false);
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
	}
/*
 * 获取是sn号
 * */
	public static String getTermSN() {
		String sn = null;
		try {
			ISystemInf sys = SysImplFactory.getSystemInf();
			sn = sys.readSerialNum();
		} catch (Throwable e) {
			sn = "";
		}
		return sn;
	}

}
