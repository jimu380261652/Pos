package com.lizhi.app.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import com.duiduifu.bean.Sf_BusinessConfig;
import com.duiduifu.db.DbUtil;
import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.CommonUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.util.ValidateUtil;
import com.pos.lizhiorder.R;
/*
 * 开机启动判断网络、下载apk
 */
public class LoadNetworkActivity extends BaseActivity{
	private CommonUtil mCommonUtil;
	private String terminalId,MAIN_APK_NAME,MAIN_URL,MAIN_VERSION,apkUrl,businessId,apkVersion,packageName,APK_NAME;
	private Handler handler;
	public int fileSize = 0;
	public int downLoadFileSize;
	public String apkFile = null;
	private DbUtil dbUtil;
	private boolean isUpdateFlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_network);
		final TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		getMainDownLoad(tm);
		tm.listen(new PhoneStateListener(){

			@Override
			public void onDataConnectionStateChanged(int state, int networkType) {
				// TODO Auto-generated method stub
				getMainDownLoad(tm);
			}

			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				// TODO Auto-generated method stub
				getMainDownLoad(tm);
			}
			
		}, PhoneStateListener.LISTEN_SERVICE_STATE|PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					checkMainVersion();
					break;
				case AppConstantByUtil.ERROR:
					break;
				case 2:
					Intent intent=new Intent(LoadNetworkActivity.this, FirstActivity.class);
					startActivity(intent);
					finish();
					break;
				default:
					break;
				}
			}
			
		};
	}
	private Handler downHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case AppConstantByUtil.ERROR:
				break;
			case AppConstantByUtil.MAX:
				break;
			case AppConstantByUtil.CONTINUE:
				break;
			case AppConstantByUtil.SUCCESS:
				File file=new File(apkFile);
				openFile(file);
				break;
			case AppConstantByUtil.LOAD_BUSINESS_CONFIG:
				int count = 0;//apk总数
				if (isUpdateFlag) {
					
					for (int i = 0; i < count; i++) {
						new DownLoadThread(apkUrl, APK_NAME);
					}
				}else {
					if (apkUrl.equals("")) {
						return;
					}
					if (isAppInstalled(Sf_BusinessConfig.packageName)) {
						//已安装
					}else {
						File file1=new File(mCommonUtil.getSDCardPath()+Sf_BusinessConfig.apkName);
						if (!file1.exists()) {
							for (int i = 0; i < count; i++) {
								new DownLoadThread(apkUrl, APK_NAME);
							}
						}else {
							openFile(file1);
						}
					}
				}
				isUpdateFlag=true;
				sendMessageToHandler(2, "");//全部apk下载安装完成
				break;
			case AppConstantByUtil.LOAD_EXCEPTION:
				break;
			default:
				break;
			}
		}
		
	};
	/*
	 * 检车主程序版本
	 */
       private void checkMainVersion() {
		// TODO Auto-generated method stub
		try {
			int mainServerVersion=Integer.valueOf(MAIN_VERSION);
			//获取本机主程序版本
			int mainLocalVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
			String downLoadPath=AppConstantByUtil.HOST+MAIN_URL;
			String fileName=MAIN_APK_NAME;
			if (mainLocalVersion<mainServerVersion) {
				File file=new File(mCommonUtil.getSDCardPath()+fileName);
				if (file.exists()) {
					openFile(file);
					return;
				}
				new DownLoadThread(downLoadPath,fileName).start();
			}else {
				toRequestOtherVersion();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
       /*
        * 获取其他apk的版本
        */
		private void toRequestOtherVersion() {
		// TODO Auto-generated method stub
		String sign=MD5Util.createSign(terminalId);
		HashMap<String, Object>mHashMap=new HashMap<String, Object>();
		mHashMap.put("terminalId", terminalId);
		mHashMap.put("sign", sign);
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST, "POST", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				// TODO Auto-generated method stub
				sendMessageToHandler(AppConstantByUtil.ERROR, "");
			}
			
			@Override
			public void onComplete(String values) {
				// TODO Auto-generated method stub
				try {
					HashMap<String, Object>mBusinessHashMap;
					JSONObject jsonObject=new JSONObject(values);
					JSONArray jsonArray=new JSONArray(jsonObject.getString("apks"));
					for (int i = 0; i < jsonArray.length(); i++) {
						apkUrl=jsonArray.getJSONObject(i).getString(Sf_BusinessConfig.apkUrl);
						businessId=jsonArray.getJSONObject(i).getString(Sf_BusinessConfig.businessId);
						apkVersion=jsonArray.getJSONObject(i).getString(Sf_BusinessConfig.version);
						packageName=jsonArray.getJSONObject(i).getString(Sf_BusinessConfig.packageName);
						APK_NAME=jsonArray.getJSONObject(i).getString(Sf_BusinessConfig.apkName);
						if (!ValidateUtil.isEmpty(apkUrl)&&!ValidateUtil.isEmpty(businessId)) {
							HashMap<String, Object>map=new HashMap<String, Object>();
							map.put(Sf_BusinessConfig.apkUrl, apkUrl);
							map.put(Sf_BusinessConfig.businessId, businessId);
							map.put(Sf_BusinessConfig.version, apkVersion);
							map.put(Sf_BusinessConfig.packageName, packageName);
							map.put(Sf_BusinessConfig.apkName, APK_NAME);
							mBusinessHashMap=dbUtil.SelectBusinessIsExist(businessId);
							//判断业务是否存在
							if ((Boolean)mBusinessHashMap.get("resultCode")) {
								//判断版本是否有更新
								if (!apkVersion.equals(mBusinessHashMap.get(Sf_BusinessConfig.version))||!isAppInstalled(packageName)) {
									map.put("isUpdateFlag", true);
								}else {
									//没有版本更新
									map.put("isUpdateFlag", false);
								}
							}else {
								if (businessId!=null&&!businessId.equals("")) {
									map.put("isUpdateFlag", true);
								}else {
									map.put("isUpdateFlag", false);
								}
							}
						}
					}
					sendMsg(AppConstantByUtil.LOAD_BUSINESS_CONFIG);

				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					sendMsg(AppConstantByUtil.LOAD_EXCEPTION);
				}
			}
		});
	}
		/*
		 * 检测apk是否安装
		 */
		protected boolean isAppInstalled(String url) {
			// TODO Auto-generated method stub
			PackageManager pm=this.getPackageManager();
			boolean installed=false;
			try {
				pm.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
				installed=true;
			} catch (PackageManager.NameNotFoundException e) {
				// TODO: handle exception
				installed=false;
			}
			return installed;
		}
		/*
		 * 打开apk程序代码
		 */
		private void openFile(File file) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}
		/*
		 * 下载线程
		 */
		class DownLoadThread extends Thread{
			private String downLoadUrl;
			private String fileName;
			public DownLoadThread(String downLoadUrl,String fileName) {
				this.downLoadUrl=downLoadUrl;
				this.fileName=fileName;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				//下载开始
				downFile(downLoadUrl,mCommonUtil.getSDCardPath(),fileName);
				Looper.loop();
				super.run();
			}
			
		}
		/*
		 * 获取主程序apk的url
		 */
		private void getMainDownLoad(TelephonyManager tm) {
		// TODO Auto-generated method stub
			terminalId=android.os.Build.SERIAL;
			if (isNetConnecting()) {
				try {
					String version = String.valueOf(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode);
					String sign = MD5Util.createSign(terminalId,version);
					HashMap<String, Object> mHashMap = new HashMap<String, Object>();
					mHashMap.put("terminalId", terminalId);
					mHashMap.put("sign", sign);
					mHashMap.put("version",version);
					AsyncRunner.getInstance().request(
							AppConstantByUtil.HOST+ "core/MainProgram/mainProgramUpdate.do", "GET",
							mHashMap.entrySet(), new AsyncListener() {

								@Override
								public void onException(Object exceptionInfo) {
									// TODO Auto-generated method stub
									sendMessageToHandler(AppConstantByUtil.ERROR,AppConstantByUtil.app_network_exception_tip);
								}
								@Override
								public void onComplete(String values) {
									// TODO Auto-generated method stub
									try {
										JSONObject jsonObject = new JSONObject(values);
										String msg = jsonObject.getString("message");
										String code = jsonObject.getString("code");
										if (code.equals(String.valueOf(AppConstantByUtil.SUCCESS))) {
											MAIN_APK_NAME = jsonObject.getString("APK_NAME");
											MAIN_URL = jsonObject.getString("URL");
											MAIN_VERSION = jsonObject.getString("VERSION");
											sendMessageToHandler(1, msg);

										} else {
											sendMessageToHandler(AppConstantByUtil.ERROR, msg);
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										sendMessageToHandler(AppConstantByUtil.ERROR,AppConstantByUtil.app_json_exception_tip);

									}

								}
							});
				}catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
		/*
		 * 下载文件
		 */
		public void downFile(String downLoadUrl, String sdCardPath,String fileName) {
			// TODO Auto-generated method stub
			//构造HttpClient实例
			HttpClient httpClient=new HttpClient();
			//设置HTTP连接超时为6秒
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(6000);
			//创建get方法的实例
			GetMethod getMethod=new GetMethod(downLoadUrl);
			//处理中文乱码
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			//设置请求超时为60秒
			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
			//使用系统提供的默认恢复策略
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			try {
				//执行getMethod
				int statusCode=httpClient.executeMethod(getMethod);
				if (statusCode==HttpStatus.SC_OK) {
					//读取输入流
					InputStream is=getMethod.getResponseBodyAsStream();
					//根据响应获取文件大小
					this.fileSize=(int)getMethod.getResponseContentLength();
					//判断输入流是否为空
					if (fileSize<=0||is==null) {
						sendMsg(AppConstantByUtil.ERROR);
						return;
					}
					FileOutputStream fos=new FileOutputStream(sdCardPath+fileName);
					//把数据存入路径+文件名
					byte buf[]=new byte[1024];
					//设置已下载的大小
					downLoadFileSize=0;
					sendMsg(AppConstantByUtil.MAX);
					//默认为下载完成
					int code=AppConstantByUtil.SUCCESS;
					//循环读取文件
					do {
						int numread=is.read(buf);
						//读取失败
						if (numread==-1) {
							break;
						}
						fos.write(buf, 0, numread);
						downLoadFileSize+=numread;
						sendMsg(AppConstantByUtil.CONTINUE);
					} while (true);
					this.apkFile=sdCardPath+fileName;
					//反馈下载的结果
					sendMsg(code);
					//关闭流
					is.close();
					fos.close();
				}else {
					sendMsg(AppConstantByUtil.ERROR);
				}
			} catch (HttpException e) {
				// TODO: handle exception
				//发生致命的异常，可能是协议不对或者返回的内容有问题
				File file=new File(mCommonUtil.getSDCardPath()+fileName);
				if (file.exists()) {
					file.delete();
				}
				e.printStackTrace();
			}catch (IOException e) {
				// TODO: handle exception
				File file=new File(mCommonUtil.getSDCardPath()+fileName);
				if (file.exists()) {
					file.delete();
				}
				//发生网络异常
				e.printStackTrace();
			}finally{
				//释放连接
				getMethod.releaseConnection();
			}
		}
		/*
		 * 发送消息到主线程
		 */
		private void sendMessageToHandler(int what,String msg) {
			// TODO Auto-generated method stub
			Message message=handler.obtainMessage();
			message.what=what;
			message.obj=msg;
			handler.sendMessage(message);
		}
		/*
		 * 发送消息到下载handler
		 */
		private void sendMsg(int what) {
			Message msg = new Message();
			msg.what = what;
			downHandler.sendMessage(msg);
		}
		/*
		 * 当前网络是否连接
		 */
	    public boolean isNetConnecting() {
	            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	            if (networkInfo == null || !networkInfo.isConnected()) {
	                    // info.setConnected(false);
	                    return false;
	            } else {
	                    // info.setConnected(true);
	                    return true;
	            }
	    }
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			final TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			getMainDownLoad(tm);
			super.onPause();
		}

}
