package com.lizhi.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.duiduifu.bean.Sf_Message;
import com.duiduifu.bean.Sf_Order;
import com.duiduifu.db.DbUtil;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.ValidateUtil;
import com.lizhi.app.printcode.ConstantPrint;
import com.lizhi.app.printcode.EncodingHandler;
import com.lizhi.app.printcode.TangshiPrinterHelp;
import com.lizhi.app.printcode.WaiMaiPrinterHelper;
import com.lizhi.push.MyApplication;
import com.wizarpos.devices.AccessException;

public class HeartService extends Service {

	private final IBinder mBinder = new SyncBinder();
	private HeartSyncCallback mCallback;
	
	DbUtil dbUtil = ((MyApplication)getApplication()).getDbUtil();
	
	public class SyncBinder extends Binder{
		public HeartService getSyncService(){
			return HeartService.this;
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	//Service创建时回调
	@Override
	public void onCreate() {
		super.onCreate();
		handler.post(updateThread);
		//handler.postDelayed(runnable, 5000);
		//new SocketThread().start();
		
		
	}
	
	//Service开始时回调
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	
	//Service结束时回调
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		handler.removeCallbacks(updateThread);
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}
	
	
	//=============== 请求服务器  =============
	
	public void onHeartbeatService(String url){
		// 构造HttpClient的实例
		PostMethod postMethod = null;
		try {
			
			HttpClient httpClient = new HttpClient();

			// 设置 Http 连接超时为5秒
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(15000);

			// 创建post方法的实例
			postMethod = new PostMethod(url);

			// 处理中文乱码
			postMethod.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

			// 设置请求超时为60 秒
			postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);

			// 使用系统提供的默认的恢复策略
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler());
			
				
				HashMap<String, Object> messageMap = dbUtil.SelectLastMessage();
				if((Boolean)messageMap.get("resultCode")){
					postMethod.addParameter("消息的最新的id",String.valueOf(messageMap.get(Sf_Message.messageId)));
				}
				
				HashMap<String, Object> orderMap = dbUtil.SelectLastOrder();
				if((Boolean)orderMap.get("resultCode")){
					postMethod.addParameter("订单的最新的id",String.valueOf(orderMap.get(Sf_Order.orderId)));
				}

				// 执行postMethod
				int statusCode = httpClient.executeMethod(postMethod);

				//statusCode=200,请求成功
				if (statusCode == HttpStatus.SC_OK) {

					// 读取内容 ,第一种方式获取
					String result = new String(postMethod.getResponseBody(),"UTF-8");

					// 返回数据
					if(result!=null && !result.isEmpty()){
						onServiceResultDispose(result);
					}else{
						onServiceResultDispose(null);
					}

				}else{
					
					//返回码错误
					onServiceResultDispose(null);
				}


			} catch (HttpException e) {
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				onServiceResultDispose(null);
				e.printStackTrace();
				
			} catch (IOException e) {
				// 发生网络异常
				onServiceResultDispose(null);
				e.printStackTrace();
				
			} catch(Exception e){
				
				onServiceResultDispose(null);
				e.printStackTrace();
				
			} finally {
				// 释放连接
				postMethod.releaseConnection();
			}
	}
	
	public void onServiceResultDispose(String jsonResult){
		int messageNum = 0;
		int orderNum = 0;
		
		try {
			
			if(jsonResult==null || jsonResult.isEmpty()){
				//当json为空的时候
			}else{
				JSONObject jsonObject = new JSONObject(jsonResult);
				String code=jsonObject.getString("code");
				if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
					//获取JSON中信息
					if(!jsonObject.isNull("message")){
						JSONArray jsonArray = new JSONArray(jsonObject.getString("message"));
						
						String messageId,title,message,memo,flag;
						HashMap<String,Object> mMessageHashMap;
						
						for(int i = 0;i<jsonArray.length();i++){
							messageId = jsonArray.getJSONObject(i)
									.getString(Sf_Message.messageId);
							title= jsonArray.getJSONObject(i)
									.getString(Sf_Message.title);
							message = jsonArray.getJSONObject(i)
									.getString(Sf_Message.message);
							memo = jsonArray.getJSONObject(i)
									.getString(Sf_Message.memo);
							flag = Sf_Message.NEW;
							if(!ValidateUtil.isEmpty(messageId)){
								mMessageHashMap = dbUtil.SelectMessageIsExist(messageId);
								//如果存在，就更新
								if((Boolean) mMessageHashMap
										.get("resultCode")){
									dbUtil.UpdateMessage(messageId, title, message, memo, flag);
								}else{
									//如果不存在，就插入
									dbUtil.InsertMessage(messageId, title, message, memo, flag);
								}
								
							}
						}
					}
					
					//获取JSON中的订单
					if(!jsonObject.isNull("order")){
						JSONArray jsonArray = new JSONArray(jsonObject.getString("order"));
						
						String orderId,orderName,orderContent,memo,flag;
						HashMap<String,Object> mOrderHashMap;
						
						for(int i = 0;i<jsonArray.length();i++){
							orderId = jsonArray.getJSONObject(i)
									.getString(Sf_Order.orderId);
							orderName= jsonArray.getJSONObject(i)
									.getString(Sf_Order.orderName);
							orderContent = jsonArray.getJSONObject(i)
									.getString(Sf_Order.orderContent);
							memo = jsonArray.getJSONObject(i)
									.getString(Sf_Order.memo);
							flag = Sf_Order.NEW;
							if(!ValidateUtil.isEmpty(orderId)){
								mOrderHashMap = dbUtil.SelectOrderIsExist(orderId);
								//如果存在，就更新
								if((Boolean) mOrderHashMap
										.get("resultCode")){
									dbUtil.UpdateOrder(orderId, orderName, orderContent, memo, flag);
								}else{
									//如果不存在，就插入
									dbUtil.InsertOrder(orderId, orderName, orderContent, memo, flag);
								}
								
							}
						}
					}
					
				}
			}
			
			messageNum = dbUtil.SelectMessageCountForFlag(Sf_Message.NEW);
			orderNum = dbUtil.SelectOrderCountForFlag(Sf_Order.NEW);
			
			if(mCallback!=null){
				mCallback.setHintNumber(messageNum, orderNum);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	
	
	

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		};
	};
	
	Runnable updateThread = new Runnable(){
		@Override
		public void run() {
			new InformationSyncThread().start();
			handler.postDelayed(updateThread, 1000*60*5);	//五分钟
		}
		
	};
	
	public class InformationSyncThread extends Thread{
		@Override
		public void run() {
			onHeartbeatService(null);
		}

	}
	
	public void setHeartSyncCallback(HeartSyncCallback callback){
		this.mCallback = callback;
	}
	
	
	
	public interface HeartSyncCallback{
		public void setHintNumber(int messageNum,int orderNum);
	}

	
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			new Thread() {

				public void run() {
					try {
						// 链接服务器
						SocketAddress target = new InetSocketAddress(
								"192.168.88.189", 2008);
						DatagramSocket client = new DatagramSocket();
						String message = "";
						byte[] sendbuf = message.getBytes();
						DatagramPacket pack = new DatagramPacket(sendbuf,
								sendbuf.length, target);
						client.send(pack);
						receive(client);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}.start();

			handler.postDelayed(this, 5000);
		}
	};
	DatagramSocket socket = null;

	class SocketThread extends Thread {
		

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
				try {
				
					// 创建DatagramSocket对象并指定一个端口号，注意，如果客户端需要接收服务器的返回数据,
					// 还需要使用这个端口号来receive，所以一定要记住
					if(socket==null || socket.isClosed()){

						socket = new DatagramSocket(8081);

					}
					// 使用InetAddress(Inet4Address).getByName把IP地址转换为网络地址192.168.88.189  183.60.181.99
					InetAddress serverAddress = InetAddress.getByName("192.168.88.189");
					// Inet4Address serverAddress = (Inet4Address)
					// Inet4Address.getByName("192.168.1.32");
					String str = "192.168.88.15";// 设置要发送的报文
					byte data[] = str.getBytes();// 把字符串str字符串转换为字节数组
					// 创建一个DatagramPacket对象，用于发送数据。
					// 参数一：要发送的数据 参数二：数据的长度 参数三：服务端的网络地址 参数四：服务器端端口号
					DatagramPacket packet = new DatagramPacket(data, data.length,
							serverAddress, 8085);

					socket.send(packet);// 把数据发送到服务端。
					receive(socket);
					
					//socket.close();
				} catch (SocketException e) {
					
					e.printStackTrace();
					
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		
			

		}

	}
	
	// 接受服务器返回信息
	private static void receive(DatagramSocket client) {

		try {
			for (;;) {

				byte[] buf = new byte[1024];

				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				client.receive(packet);

				String receiveMessage = new String(packet.getData(), 0,
						packet.getLength());

				if(receiveMessage.length()>0){
					
					String[] str=receiveMessage.split(",");
							
					String shopName=str[0];
					
					String paymentId=str[1];
					
					String date=str[2];
					
					String shopContact=str[3];
					
					String memo=str[4];
					
					String payAmount=str[5];

					String orderDataStr=str[6];
					
					
					setJson(shopName,paymentId,date,shopContact,memo,payAmount);
					setJsonArray(orderDataStr);
			    	ConstantPrint c = new TangshiPrinterHelp();
					ConstantPrint c2 = new WaiMaiPrinterHelper();
					try {
						c.print(EncodingHandler.createImage(AppConstantByUtil.shopId, 350, 350));
						c2.print(EncodingHandler.createImage(AppConstantByUtil.shopId, 350, 350));
					} catch (AccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//生成json
			public static void setJson(String shopName,String paymentId,String date,String shopContact,String memo,String payAmount){
				JSONObject jsonObject=new JSONObject();
				try {
					jsonObject.put("shopName", shopName);
					jsonObject.put("paymentId", paymentId);
					jsonObject.put("date", date);
					jsonObject.put("shopContact", shopContact);
					jsonObject.put("memo", memo);
					jsonObject.put("payAmount", payAmount);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String str=jsonObject.toString();
				TangshiPrinterHelp.json=str;
			}

			//生成jsonArray
			public static void setJsonArray(String data){
				
				try {
					JSONArray jsonArray=new JSONArray();
					
					JSONObject jsonObject;

					String[] groupStr=data.split("\\|");
					for(int i=0;i<groupStr.length;i++){
						
						String[] singleStr=groupStr[i].split("#");
						jsonObject=new JSONObject();
						
						jsonObject.put("name", singleStr[0]);
						
						jsonObject.put("buyNum", singleStr[1]);
						
						jsonObject.put("price", singleStr[2]);
						
						jsonArray.put(jsonObject);
						
					}
					
					String str=jsonArray.toString();
							
					TangshiPrinterHelp.jsonArray=str;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}

}
