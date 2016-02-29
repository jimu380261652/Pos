package com.lizhi.app.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.centerm.smartpos.dev.IScreenDev;
import com.centerm.smartpos.dev.impl.SmartPayFactory;
import com.centerm.smartpos.sys.IInputMethodCtrl;
import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.CommonUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.util.ValidateUtil;
import com.duiduifu.view.TipsToast;
import com.lizhi.app.dialog.PasswordDialog;
import com.lizhi.app.dialog.PasswordDialog.PasswordDialogCallback;
import com.lizhi.app.dialog.SettingDialog;
import com.lizhi.app.dialog.SettingDialog.SettingDialogCallback;
import com.lizhi.push.MyApplication;
import com.lizhi.util.Constants;
import com.lizhi.util.JsonUtil;
import com.lizhi.util.ShareUtils;
import com.lizhi.util.StringUtil;
import com.lizhi.widget.ResizeLayout;
import com.lizhi.widget.ResizeLayout.OnSizeChangedListenner;
import com.pos.lizhiorder.R;

/**
 * 
 * @ClassName: LoginActivity.java
 * 
 * @Description: 商户登录
 * 
 * @author YangTaoLin
 * 
 * @date 2013-12-12 上午18:06:09
 * 
 * Copyright © 2015 广州唐冠网络科技有限公司 版权所有 V1.01.151105A
 * 
 */
public class LoginActivity extends BaseActivity implements
		OnSizeChangedListenner, PasswordDialogCallback, SettingDialogCallback {

	private ResizeLayout root;
	private LinearLayout ll_login;
	private Button mLoginButton;
	private EditText mUserNameEditText, mPasswordEditText, mEditText;
	private TextView mLoaderText,VersionName;
	ImageView network;
	LinearLayout vSetting;
	Loading loading;
	// 弹出框
	PasswordDialog vPasswordDialog;
	private Context context;
	private LoginActivity loginActivity;
	private Handler mMainHadler;

	private CommonUtil mCommonUtil;
	private ValidateUtil mValidateUtil;

	private StringBuffer[] buffer = new StringBuffer[2];
	private String userId, pwd, loginSign, shopName, shopId, shopContact,
			userName, payShopNo, payTerminalNo, payBank, payIndex, shopPhone,
			shopAddress, deviceId;

	InputMethodManager userInputManager, pwdInputManager;
	IInputMethodCtrl iInputMe;

	ConnectivityManager conMgr;
	boolean isOpenG = false;
	String currentGString;

	SettingDialog settingDialog;

	public LoginActivity() {

		loginActivity = LoginActivity.this;

		mCommonUtil = new CommonUtil(loginActivity);

		mValidateUtil = new ValidateUtil();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_login);
		conMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 百度云推送
		initPush();
		// try {
		// ISystemInf sysInf = SysImplFactory.getSystemInf();
		// sysInf.closePinpadKeyboard();//关闭
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		((MyApplication) getApplication()).addActivity(this);
		loading = new Loading(this);
		loading.setCancelable(false);

		settingDialog = new SettingDialog(this, false, this);

		initUI();

		buffer[0] = new StringBuffer();

		buffer[1] = new StringBuffer();
		// toRequestVersion(); // FIXME 从这里入口的代码都是更新主程序和收银台，可以不用管理

		try {
			IScreenDev screen = SmartPayFactory
					.getScreenDev(LoginActivity.this);
			screen = SmartPayFactory.getScreenDev(LoginActivity.this);
			String str = screen.closeMultiTouch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onResume() {
		super.onResume();
		/*
		 * iInputMe = SysImplFactory.getInputMethodCtrl();
		 * iInputMe.initialization(this, 800, 150, 400, 500,
		 * "com.duiduifu.app.main"); iInputMe.setAttrDirect(800, 150, 400, 500);
		 */
		// (this, 600,150, 400, 500, "com.duiduifu.app.main");
		userInputManager = (InputMethodManager) mUserNameEditText.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		pwdInputManager = (InputMethodManager) mPasswordEditText.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		/*
		 * mUserNameEditText .setOnFocusChangeListener(iInputMe
		 * .getInputMethodChangeListener(iInputMe.INPUTMETHOD_TYPE_123_ABC));
		 * mPasswordEditText .setOnFocusChangeListener(iInputMe
		 * .getInputMethodChangeListener(iInputMe.INPUTMETHOD_TYPE_123_ABC));
		 */
		// try {
		// ISystemInf sysInf = SysImplFactory.getSystemInf();
		// sysInf.closePinpadKeyboard();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		if (gprsIsOpenMethod("getMobileDataEnabled")) {
			isOpenG = true;
			currentGString = "关闭3G";
		} else {
			isOpenG = false;
			currentGString = "打开3G";
		}
	}

	/**
	 * 初始化控件
	 */
	public void initUI() {
		// 监听键盘弹出
		 
	 
		VersionName=(TextView) findViewById(R.id.tv_VersionName);
		try {
			VersionName.setText("Copyright © 2015 广州李子网络科技有限公司 版权所有 "+getVersionName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		root = (ResizeLayout) findViewById(R.id.rl_login_root);
		ll_login = (LinearLayout) findViewById(R.id.ll_login);
		root.setOnSizeChangedListenner(this);

		mLoginButton = (Button) findViewById(R.id.login_btn);

		mUserNameEditText = (EditText) findViewById(R.id.userName_et);
		mPasswordEditText = (EditText) findViewById(R.id.password_et);

		mUserNameEditText.setText("13926881112"); 
		mPasswordEditText.setText("123456");

		mUserNameEditText.setSelectAllOnFocus(true);
		mPasswordEditText.setSelectAllOnFocus(true);

		vPasswordDialog = new PasswordDialog(this, this, this);

		vSetting = (LinearLayout) this.findViewById(R.id.setting);
		vSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				settingDialog.showDialog(currentGString);
			}
		});

		// ====== TODO 程序更新 ==========
		vDownLoadLayer = (RelativeLayout) this
				.findViewById(R.id.download_layer);
		vDownLoadProgress = (ProgressBar) this
				.findViewById(R.id.download_progressbar);

		// mUserNameEditText.setFocusableInTouchMode(true);
		// mUserNameEditText.setFocusable(true);

		mLoaderText = (TextView) findViewById(R.id.download_text);

		mLoginButton.setOnClickListener(new ButtonListener());

		// mUserNameEditText.setOnClickListener(new ButtonListener());
		//
		// mPasswordEditText.setOnClickListener(new ButtonListener());

		mMainHadler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {

				case 0:
					TipsToast.makeText(loginActivity, msg.obj.toString(), 1000)
							.show();
					loading.cancel();
					break;
				case 1:
					// mCommonUtil.PreferenceSaveS("pwd",
					// MD5Util.createSign(mPasswordEditText.getText().toString()));
					// mCommonUtil.PreferenceSaveS("loginUser",
					// mUserNameEditText.getText().toString());
					Intent intent = new Intent(LoginActivity.this,
							HomeActivity.class);
					ShareUtils.putString(LoginActivity.this, "temps", "null");
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);
					startActivityForResult(intent, 0);
					mPasswordEditText.setText("");
					try {
						Intent i = new Intent();
						intent.setAction("com.duidufu.login.success");// 发出自定义广播
						LoginActivity.this.sendBroadcast(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
					loading.cancel();
					break;
				case 2:
					((MyApplication) getApplication()).AppExit(context);
					TipsToast.makeText(loginActivity, "退出", 1000).show();
					loading.cancel();
					break;
				case 10:
					checkVersion();
					break;

				case 11:
					checkCashierVersion();
					break;
				case 12:
					loading.cancel();
					if (gprsIsOpenMethod("getMobileDataEnabled")) {
						currentGString = "关闭3G";
						isOpenG = true;
						TipsToast.makeText(loginActivity, "3G已经打开", 1000)
								.show();
					} else {
						currentGString = "打开3G";
						isOpenG = false;
						TipsToast.makeText(loginActivity, "3G已经关闭", 1000)
								.show();
					}
					break;
				case 13:
					loading.cancel();
					TipsToast.makeText(loginActivity, msg.obj.toString(), 1000)
							.show();
					break;

				default:
					break;
				}

				// mProgressDialog.setVisibility(View.GONE);
				// loading.cancel();

			}

		};

	}

	private void initPush() {
		// 百度推送
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, "i8Z7CnXkmmElGFWyYpoxhXXl");// Utils.getMetaValue(context,
																				// "api_key")EG2APKwnGMzVn7mhHvhziFj2
	}

	private boolean gprsIsOpenMethod(String methodName) {
		Class cmClass = conMgr.getClass();
		Class[] argClasses = null;
		Object[] argObject = null;

		Boolean isOpen = false;
		try {
			Method method = cmClass.getMethod(methodName, argClasses);

			isOpen = (Boolean) method.invoke(conMgr, argObject);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isOpen;
	}

	private void toggleMobileData(Context context, boolean enabled) {
		Class<?> conMgrClass = null;
		Field iConMgrField = null;
		Object iConMgr = null;
		Class<?> iConMgrClass = null;
		Method setMobileDataEnabledMethod = null;

		try {
			conMgrClass = Class.forName(conMgr.getClass().getName());
			iConMgrField = conMgrClass.getDeclaredField("mService");
			iConMgrField.setAccessible(true);
			iConMgr = iConMgrField.get(conMgr);
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
			mMainHadler.postDelayed(new Runnable() {

				@Override
				public void run() {
					sendMessageToHandler(12, "3G操作成功");
				}
			}, 2000);
		} catch (Exception e) {
			e.printStackTrace();
			sendMessageToHandler(13, "3G操作失败！");
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void loading() {
		loading.show();
	}

	@Override
	protected void onDestroy() {
		try {
			iInputMe.unestablish(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vPasswordDialog != null) {
			vPasswordDialog.dismiss();
		}
		if (settingDialog != null) {
			settingDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == RESULT_OK) {

		}

	}

	// public void switchEditText(int checkNum){
	// if(checkNum==1){
	// mUserNameEditText.setBackgroundResource(R.drawable.input_edit_bg_press);
	//
	// mPasswordEditText.setBackgroundResource(R.drawable.input_edit_bg_nol);
	// return;
	// }
	//
	// if(checkNum==2){
	//
	// mUserNameEditText.setBackgroundResource(R.drawable.input_edit_bg_nol);
	//
	// mPasswordEditText.setBackgroundResource(R.drawable.input_edit_bg_press);
	// }
	// }

	/**
	 * 按钮监听器
	 */
	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			switch (arg0.getId()) {
			// case R.id.password_et:
			// switchEditText(2);
			// break;
			//
			// case R.id.userName_et:
			// switchEditText(1);
			// break;
			case R.id.login_btn:

				shopLogin();

				/*
				 * ComponentName component = new ComponentName(
				 * "com.centerm.df.main",
				 * "com.centerm.df.main.activity.MainControlActivity"); Intent
				 * intent = new Intent(); intent.setComponent(component); Bundle
				 * bundle = new Bundle(); bundle.putString("trs_tp", "0");// 1
				 * bundle.putString("proc_cd", "009000");
				 * bundle.putString("amt","15");// 15.00
				 * intent.putExtras(bundle); startActivityForResult(intent, 1);
				 */

				break;

			default:
				break;
			}

		}

	}

	/**
	 * 商户登陆
	 */
	public void shopLogin() {

		/*
		 * try { SystemDevInf sysdef= DeviceFactory.getSystemDev();
		 * terminalId=sysdef.readManuSerialNum(); //
		 * terminalId=sysdef.readSerialNum(); //TODO 现在使用的sn //terminalId =
		 * "WP13431000000121"; // System.out.println("SN:"+terminalId); } catch
		 * (Exception e1) { e1.printStackTrace(); }
		 * 
		 * Constants.terminalId = terminalId;
		 */

		userName = mUserNameEditText.getText().toString();

		pwd = mPasswordEditText.getText().toString();

		if (userName == null || userName.equals("")) {
			TipsToast.makeText(loginActivity,
					R.string.app_username_is_not_empty_tip, 1000).show();
			return;
		}

		if (pwd == null || pwd.equals("")) {
			TipsToast.makeText(loginActivity,
					R.string.app_password_is_not_empty_tip, 1000).show();
			return;
		}
		pwd = MD5Util.createSign(pwd);
		String sign = MD5Util.createSign(Constants.terminalId, userName, pwd);
		loading();
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();

		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("model", "03");
		mHashMap.put("userName", userName);
		if (!StringUtil.isEmpty(Constants.channelId))
			mHashMap.put("channelId", Constants.channelId);

		mHashMap.put("pwd", pwd);

		mHashMap.put("sign", sign);

		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "ShopLogin/login.do", "GET",
				mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onException(Object exceptionInfo) {
						sendMessageToHandler(AppConstantByUtil.ERROR,
								AppConstantByUtil.app_network_exception_tip);
					}

					@Override
					public void onComplete(String values) {
						System.out.println("登录："+values);
						try {

							JSONObject jsonObject = new JSONObject(values);

							String msg = jsonObject.getString("message");

							String code = jsonObject.getString("code");
							Log.i("111111111", code + "");
							Log.i("111111111", msg + "");
							// String auths = null;
							//
							// if(jsonObject.has("auths")){
							// auths = jsonObject.getString("auths");
							// }
							//
							// if(auths!=null && !"".equals(auths)){
							// mCommonUtil.PreferenceSaveS(Auths.SPU_AUTHS_STRING,
							// auths);
							// }else{
							// mCommonUtil.PreferenceSaveS(Auths.SPU_AUTHS_STRING,
							// "");
							// }

							if (code.equals(String
									.valueOf(AppConstantByUtil.SUCCESS))) {

								Constants.shop = JsonUtil.getSimpleUser(values);
								Constants.loginSign = Constants.shop
										.getLoginSign();
								Constants.dispatchStatus = Constants.shop
										.getDispatchStatus();
								/*Log.i("配送状态", Constants.shop
										.getDispatchStatus());*/
								sendMessageToHandler(AppConstantByUtil.SUCCESS,
										msg);

							} else if (code.equals("-62730905")) {
								sendMessageToHandler(2, msg);
							} else {

								sendMessageToHandler(AppConstantByUtil.ERROR,
										msg);
							}

						} catch (Exception e) {
							e.printStackTrace();
							sendMessageToHandler(AppConstantByUtil.ERROR,
									AppConstantByUtil.app_json_exception_tip);

						}

					}
				});

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

	@Override
	protected void onStart() {
		super.onStart();
		try {
			Intent intent = new Intent();
			intent.setAction("com.duiduifu.retail.sync.close");// 发出自定义广播
			this.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// =========== 收银台更新 =====================

	public void toRequestCashierVersion() {
		try {
			SharedPreferences sp = getSharedPreferences("terminalId",
					MODE_APPEND);

			// terminalId = sp.getString("terminalId", "");

			String sign = MD5Util.createSign(Constants.terminalId);
			HashMap<String, Object> mHashMap = new HashMap<String, Object>();
			mHashMap.put("terminalId", Constants.terminalId);
			mHashMap.put("sign", sign);

			AsyncRunner.getInstance().request(
					AppConstantByUtil.HOST + "core/Apk/apkPayUpdate.do", "GET",
					mHashMap.entrySet(), new AsyncListener() {

						@Override
						public void onException(Object exceptionInfo) {
							sendMessageToHandler(AppConstantByUtil.ERROR,
									AppConstantByUtil.app_network_exception_tip);
						}

						@Override
						public void onComplete(String values) {

							try {

								JSONObject jsonObject = new JSONObject(values);

								String msg = jsonObject.getString("message");

								String code = jsonObject.getString("code");

								if (code.equals(String
										.valueOf(AppConstantByUtil.SUCCESS))) {

									APK_NAME = jsonObject.getString("APK_NAME");
									URL = jsonObject.getString("APK_URL");
									VERSION = jsonObject.getString("VERSION");
									sendMessageToHandler(11, msg);

								} else {

									sendMessageToHandler(
											AppConstantByUtil.ERROR, msg);
								}

							} catch (Exception e) {
								e.printStackTrace();
								sendMessageToHandler(
										AppConstantByUtil.ERROR,
										AppConstantByUtil.app_json_exception_tip);

							}

						}
					});
		} catch (Exception e) {
			// mProgressDialog.setVisibility(View.GONE);
			e.printStackTrace();
		}
	}

	// ============ 主程序更新 ====================

	String APK_NAME, URL, VERSION;

	private RelativeLayout vDownLoadLayer;
	private ProgressBar vDownLoadProgress;

	public int fileSize = 0;
	public int downLoadFileSize;
	public String apkFile = null;

	public void toRequestVersion() {
		try {
			// loading();
			// SharedPreferences sp=getSharedPreferences("terminalId",
			// MODE_APPEND);

			// terminalId = Constants.terminalId;

			String version = String.valueOf(this.getPackageManager()
					.getPackageInfo(this.getPackageName(), 0).versionCode);
			String sign = MD5Util.createSign(Constants.terminalId + version);
			HashMap<String, Object> mHashMap = new HashMap<String, Object>();
			mHashMap.put("terminalId", Constants.terminalId);
			mHashMap.put("sign", sign);
			mHashMap.put("version", version);
			Log.i("Login", "11111111111111111");
			AsyncRunner.getInstance().request(
					AppConstantByUtil.HOST
							+ "community-api/MainProgram/mainProgramUpdate.do",
					"GET", mHashMap.entrySet(), new AsyncListener() {

						@Override
						public void onException(Object exceptionInfo) {
							sendMessageToHandler(AppConstantByUtil.ERROR,
									AppConstantByUtil.app_network_exception_tip);
						}

						@Override
						public void onComplete(String values) {
							Log.i("Login", values);

							try {
								JSONObject jsonObject = new JSONObject(values);

								String msg = jsonObject.getString("message");

								String code = jsonObject.getString("code");

								if (code.equals(String
										.valueOf(AppConstantByUtil.SUCCESS))) {

									APK_NAME = jsonObject.getString("APK_NAME");
									URL = jsonObject.getString("URL");
									VERSION = jsonObject.getString("VERSION");
									sendMessageToHandler(10, msg);

								} else {

									sendMessageToHandler(
											AppConstantByUtil.ERROR, msg);
								}

							} catch (Exception e) {
								e.printStackTrace();
								sendMessageToHandler(
										AppConstantByUtil.ERROR,
										AppConstantByUtil.app_json_exception_tip);

							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO 检查更新，这里接入服务器接口
	 */
	public void checkVersion() {
		try {

			// TODO 这里获取服务器的主程序版本
			int mainServerVersion = Integer.valueOf(VERSION);
			// 获取本机主程序版本
			int mainLocalVersion = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionCode;

			String downLoadPath = AppConstantByUtil.HOST + URL;
			String fileName = APK_NAME;

			if (mainLocalVersion < mainServerVersion) { // 如果服务器的版本高于本机主程序的版本
				File file = new File(mCommonUtil.getSDCardPath() + fileName);
				if (file.exists()) {
					openFile(file);
					return;
				}
				vDownLoadLayer.setVisibility(View.VISIBLE);
				// loading();
				new DownLoadThread(downLoadPath, fileName).start();// 下载apk
			} else {
				toRequestCashierVersion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查收银台更新，这里接入服务器接口
	 */
	private void checkCashierVersion() {
		try {

			// TODO 这里获取服务器的主程序版本
			int mainServerVersion = Integer.valueOf(VERSION);

			// 获取本机收银台版本
			int mainLocalVersion = this.getPackageManager().getPackageInfo(
					"com.duiduifu.app.pay", 0).versionCode;

			String downLoadPath = AppConstantByUtil.HOST + URL;
			String fileName = APK_NAME;

			if (mainLocalVersion < mainServerVersion) { // 如果服务器的版本高于本机主程序的版本
				File file = new File(mCommonUtil.getSDCardPath() + fileName);
				if (file.exists()) {
					openFile(file);
					return;
				}
				mLoaderText.setText("收银台更新...");
				vDownLoadLayer.setVisibility(View.VISIBLE);
				loading();
				new DownLoadThread(downLoadPath, fileName).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Handler mDownHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case AppConstantByUtil.ERROR:
				TipsToast.makeText(loginActivity, "下载失败!", 1000).show();
				vDownLoadLayer.setVisibility(View.GONE);
				loading.cancel();
				break;
			case AppConstantByUtil.MAX:
				vDownLoadProgress.setMax(fileSize);
				break;
			case AppConstantByUtil.CONTINUE:
				vDownLoadProgress.setProgress(downLoadFileSize);
				break;
			case AppConstantByUtil.SUCCESS:
				vDownLoadLayer.setVisibility(View.GONE);
				loading.cancel();
				TipsToast.makeText(loginActivity, "下载成功!", 1000).show();
				File file = new File(apkFile);
				openFile(file);
				break;
			}
		};
	};

	/**
	 * 下载线程
	 * 
	 * @author Administrator
	 * 
	 */
	class DownLoadThread extends Thread {

		private String downLoadUrl;
		private String fileName;

		public DownLoadThread(String downLoadUrl, String fileName) {
			this.downLoadUrl = downLoadUrl;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			Looper.prepare();
			// 下载开始
			downFile(downLoadUrl, mCommonUtil.getSDCardPath(), fileName);

			Looper.loop();

			super.run();
		}

	}

	/**
	 * 下载文件
	 * 
	 * @param position
	 * @param url
	 * @param path
	 * @param fileName
	 */
	public void downFile(String url, String path, String fileName) {

		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();

		// 设置 Http 连接超时为5秒
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);

		// 创建get方法的实例
		GetMethod getMethod = new GetMethod(url);

		// 处理中文乱码
		getMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 设置请求超时为60 秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);

		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {

			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);

			// statusCode=200,请求成功
			if (statusCode == HttpStatus.SC_OK) {

				// 读取输入流
				InputStream is = getMethod.getResponseBodyAsStream();

				// 根据响应获取文件大小
				this.fileSize = (int) getMethod.getResponseContentLength();

				// 判断输入流是否为空
				if (fileSize <= 0 || is == null) {

					sendMsg(AppConstantByUtil.ERROR);
					return;
				}

				FileOutputStream fos = new FileOutputStream(path + fileName);

				// 把数据存入路径+文件名
				byte buf[] = new byte[1024];

				// 设置已下载的大小
				downLoadFileSize = 0;

				// 发送总大小给进度条
				sendMsg(AppConstantByUtil.MAX);

				// 默认为下载完成
				int code = AppConstantByUtil.SUCCESS;
				// 循环读取文件
				do {

					int numread = is.read(buf);

					// 读取失败
					if (numread == -1) {
						break;
					}

					fos.write(buf, 0, numread);

					downLoadFileSize += numread;

					// 更新进度条
					sendMsg(AppConstantByUtil.CONTINUE);

				} while (true);

				this.apkFile = path + fileName;

				// 反馈下载的结果
				sendMsg(code);

				// 关闭流
				is.close();

				fos.close();

			} else {

				sendMsg(AppConstantByUtil.ERROR);

			}

		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			File file = new File(mCommonUtil.getSDCardPath() + fileName);
			if (file.exists()) {
				file.delete();
			}
			e.printStackTrace();

		} catch (IOException e) {
			File file = new File(mCommonUtil.getSDCardPath() + fileName);
			if (file.exists()) {
				file.delete();
			}
			// 发生网络异常
			e.printStackTrace();

		} finally {

			// 释放连接
			getMethod.releaseConnection();
		}
	}

	// 打开APK程序代码
	public void openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 发送信息
	 * 
	 * @param page
	 * @param position
	 * @param flag
	 */
	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		mDownHandler.sendMessage(msg);
	}

	// @Override
	// public void onBackPressed() {
	// return;
	// }

	@Override
	public void onPasswordInputDialogOK(int currentId) {
		if (currentId == R.id.setting_layer) {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_SETTINGS); // 系统设置
			this.startActivityForResult(intent, 0);
			return;
		}

		if (currentId == R.id.mtms_layer) {
			if (isAppInstalled("com.centerm.mtms.tm")) {
				Intent intent = new Intent();

				ComponentName componetName = new ComponentName(
						"com.centerm.mtms.tm",
						"com.centerm.mtms.tm.activity.main.LoginActivity");

				intent.setAction("android.intent.action.VIEW");

				intent.setComponent(componetName);

				startActivity(intent);
			} else {
				TipsToast.makeText(loginActivity, "尚未安装MTMS客户端！", 1000).show();
			}

			return;
		}

	}

	// 检测APK是否安装
	public boolean isAppInstalled(String uri) {

		PackageManager pm = this.getPackageManager();

		boolean installed = false;

		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}

		return installed;
	}

	@Override
	public void onSettingClick() {
		if (vPasswordDialog != null) {
			vPasswordDialog.showDialog(R.id.setting_layer,
					PasswordDialog.SETTING);
		}
	}

	@Override
	public void onVPNClick() {
		Intent intent = new Intent();
		ComponentName cmp = new ComponentName("com.android.settings",
				"com.android.settings.ApnSettings");
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(cmp);
		startActivity(intent);
	}

	@Override
	public void onGClick() {
		try {
			loading();
			toggleMobileData(LoginActivity.this, !isOpenG);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWIFIClick() {
		Intent localIntent = new Intent();
		localIntent.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.wifi.WifiPickerActivity"));
		startActivityForResult(localIntent, 100);
	}

	@Override
	public void onMTMSClick() {
		if (vPasswordDialog != null) {
			vPasswordDialog.showDialog(R.id.mtms_layer, PasswordDialog.MTMS);
		}
	}

	@Override
	public void onDateClick() {
		Intent localIntent = new Intent();
		localIntent.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.DateTimeSettingsSetupWizard"));
		startActivityForResult(localIntent, 101);

	}

	@Override
	public void onImageClick() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// mCommonUtil.deleteFile(mCommonUtil.getSDCardPath()+"ImageCache");
					File file = new File(mCommonUtil.getSDCardPath()
							+ "ImageCache");
					// Boolean d = f.delete();
					// System.out.println("进来了这里"+d);

					RecursionDeleteFile(file);

				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}).start();
	}

	public void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	@Override
	public void onSizeChange(boolean flag, int w, int h) {
		// TODO Auto-generated method stub
		if (flag) {// 键盘弹出时
			System.out.print("hide");
			root.setPadding(0, 100, 0, 0);
		} else { // 键盘隐藏时
			System.out.print("hide");
		}
	}
	
	private String getVersionName() throws Exception  
	{  
	        // 获取packagemanager的实例  
	        PackageManager packageManager = getPackageManager();  
	        // getPackageName()是你当前类的包名，0代表是获取版本信息  
	        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);  
	        String version = packInfo.versionName;  
	        return version;  
	}

	/** 
	 * 返回当前程序版本名 
	 */  
	/*public static String getAppVersionName(Context context) {  
	    String versionName = "";  
	    int versioncode;
	    try {  
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        versioncode = pi.versionCode;
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    return versionName;  
	} */
}
