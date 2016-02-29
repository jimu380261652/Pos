package com.lizhi.fragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.HomeAdapter;
import com.lizhi.app.main.KeyDownload;
import com.lizhi.app.main.LoginActivity;
import com.lizhi.bean.Temporary;
import com.lizhi.fragment.CashFragment1.Callback;
import com.pos.lizhiorder.R;

//收银台
public class PosFragment1 extends BaseFragment {
	private Context context;
	private GridView gv_pos;
	private HomeAdapter adapter;
	ConnectivityManager conMgr;
	private Boolean isOpenG = false;
	private Handler mMainHadler;
	private int[] urls = { R.drawable.ic_pos_key, R.drawable.ic_pos_wifi,
			R.drawable.ic_pos_3g, R.drawable.ic_pos_time,
			R.drawable.ic_manu_logout };
	private String[] names = { "主密钥下载", "wifi设置", "3G设置", "时间设置", "打印设置" };
	private Callback callback;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mViewGroup = inflater.inflate(R.layout.fragment_pos1, container,
				false);
		return mViewGroup;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		initView();
	}

	public void initView() {
		gv_pos = (GridView) getView().findViewById(R.id.gv_pos);
		adapter = new HomeAdapter(context, urls, names);
		gv_pos.setAdapter(adapter);
		mMainHadler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					Intent intent = new Intent(context, LoginActivity.class);
					startActivityForResult(intent, 0);
					try {
						Intent i = new Intent();
						intent.setAction("com.duidufu.login.success");// 发出自定义广播
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 2:
					if (gprsIsOpenMethod("getMobileDataEnabled")) {
						isOpenG = true;
						TipsToast.makeText(context, "3G已经打开", 1000).show();
					} else {
						isOpenG = false;
						TipsToast.makeText(context, "3G已经关闭", 1000).show();
					}
					break;
				case 3:

					TipsToast.makeText(context, msg.obj.toString(), 1000)
							.show();
					break;
				default:
					break;
				}
			}
		};
		gv_pos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					Intent i = new Intent(context, KeyDownload.class);
					startActivity(i);
					break;
				case 1:
					Intent localIntent = new Intent();
					localIntent.setComponent(new ComponentName(
							"com.android.settings",
							"com.android.settings.wifi.WifiPickerActivity"));
					startActivityForResult(localIntent, 100);
					break;
				case 2:
					toggleMobileData(context, !isOpenG);
					break;
				case 3:
					Intent intent = new Intent();
					intent.setComponent(new ComponentName(
							"com.android.settings",
							"com.android.settings.DateTimeSettingsSetupWizard"));
					startActivityForResult(intent, 101);
					break;
				case 4:
					callback.callback();
					//LogOut();
					break;
				}
			}
		});

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
					sendMessageToHandler(2, "3G操作成功");
				}
			}, 2000);
		} catch (Exception e) {
			e.printStackTrace();
			sendMessageToHandler(3, "3G操作失败！");
		}

	}

/*	private void LogOut() {
		String userId = Constants.shop.getUserId();
		String loginSign = Constants.shop.getLoginSign();
		String sign = MD5Util.createSign(loginSign + Constants.terminalId
				+ userId);
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("userId", userId);
		mHashMap.put("loginSign", loginSign);
		mHashMap.put("deviceId", "123456");
		mHashMap.put("sign", sign);
		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "ShopLogin/logout.do", "GET",
				mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onComplete(String values) {
						// TODO Auto-generated method stub

						try {

							JSONObject jsonObject = new JSONObject(values);

							String msg = jsonObject.getString("message");

							String code = jsonObject.getString("code");
							Log.i("pos", values);
							if (code.equals(String
									.valueOf(AppConstantByUtil.SUCCESS))) {
								Constants.shop = null;
								sendMessageToHandler(AppConstantByUtil.SUCCESS,
										msg);
							} else if (code.equals("-1")) {
								sendMessageToHandler(AppConstantByUtil.ERROR,
										msg);

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onException(Object o) {
						// TODO Auto-generated method stub
					}

				});
	}
*/
	public void sendMessageToHandler(int what, String msg) {

		Message message = mMainHadler.obtainMessage();
		message.what = what;
		message.obj = msg;
		mMainHadler.sendMessage(message);
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
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public interface Callback {
		public void callback();
	}
}
