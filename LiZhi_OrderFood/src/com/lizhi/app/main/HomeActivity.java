package com.lizhi.app.main;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.push.MyApplication;
import com.lizhi.util.Constants;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

public class HomeActivity extends BaseActivity implements OnClickListener {
	private HomeActivity homeActivity;
	private Context context;
	// private GridView gv_home;
	private String auths = "";
	private Handler mMainHadler;

	private ImageView iv_account, iv_mem, iv_order, iv_cash, iv_message,
			iv_pos, iv_services, iv_logout;
	Loading loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		loading = new Loading(context);

		((MyApplication) getApplication()).addActivity(this);

		setContentView(R.layout.activity_home);
		init();
	}

	public void init() {
		iv_account = (ImageView) findViewById(R.id.iv_account);
		iv_mem = (ImageView) findViewById(R.id.iv_mem);
		iv_order = (ImageView) findViewById(R.id.iv_order);
		iv_cash = (ImageView) findViewById(R.id.iv_cash);
		iv_message = (ImageView) findViewById(R.id.iv_message);
		iv_pos = (ImageView) findViewById(R.id.iv_pos);
		iv_services = (ImageView) findViewById(R.id.iv_services);
		iv_logout = (ImageView) findViewById(R.id.iv_logout);
		iv_account.setOnClickListener(this);
		iv_mem.setOnClickListener(this);
		iv_order.setOnClickListener(this);
		iv_cash.setOnClickListener(this);
		iv_message.setOnClickListener(this);
		iv_pos.setOnClickListener(this);
		iv_services.setOnClickListener(this);
		iv_logout.setOnClickListener(this);
		mMainHadler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {

				case 0:
					TipsToast.makeText(homeActivity, msg.obj.toString(), 1000)
							.show();
					loading.cancel();
					break;

				case 1:
					// mCommonUtil.PreferenceSaveS("pwd",
					// MD5Util.createSign(mPasswordEditText.getText().toString()));
					// mCommonUtil.PreferenceSaveS("loginUser",
					// mUserNameEditText.getText().toString());
					Constants.temps.clear();
					Intent intent = new Intent(context, LoginActivity.class);
					overridePendingTransition(R.anim.push_right_in,
							R.anim.push_right_out);
					startActivityForResult(intent, 0);
					// mPasswordEditText.setText("");
					try {
						Intent i = new Intent();
						intent.setAction("com.duidufu.login.success");// 发出自定义广播
						// LoginActivity.this.sendBroadcast(i);
					} catch (Exception e) {
						e.printStackTrace();
					}
					loading.cancel();
					break;
				case 2:
					((MyApplication) getApplication()).AppExit(context);
					TipsToast.makeText(homeActivity, "退出", 1000).show();
					loading.cancel();
					break;

				case 13:
					loading.cancel();
					TipsToast.makeText(homeActivity, msg.obj.toString(), 1000)
							.show();
					break;

				default:
					break;
				}
			}
		};

	}

	private void LogOut() {
		String userId = Constants.shop.getUserId();
		String loginSign = Constants.shop.getLoginSign();
		String sign = MD5Util.createSign(loginSign + Constants.terminalId
				+ userId);
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("userId", userId);
		mHashMap.put("loginSign", loginSign);
		mHashMap.put("deviceId", "123456");
		if (!StringUtil.isEmpty(Constants.channelId))
			mHashMap.put("channelId", Constants.channelId);
		mHashMap.put("sign", sign);
		Log.i("pos", Constants.terminalId);
		loading.show();
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

	public void sendMessageToHandler(int what, String msg) {

		Message message = mMainHadler.obtainMessage();
		message.what = what;
		message.obj = msg;
		mMainHadler.sendMessage(message);
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	public void onClick(View v) {
		auths = Constants.shop.getAuths();
		switch (v.getId()) {
		// 账户管理
		case R.id.iv_account: {
			if (auths.contains("pos_account")) {
				Intent intent2 = new Intent(context, MainActivity.class);
				intent2.putExtra("index", 2);
				startActivity(intent2);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}

		}
			break;
		// 会员管理
		case R.id.iv_mem: {
			if (auths.contains("pos_member")) {
				Intent intent3 = new Intent(context, MainActivity.class);
				intent3.putExtra("index", 3);
				startActivity(intent3);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}
		}
			break;
		// 订单管理
		case R.id.iv_order: {
			if (auths.contains("pos_orders")) {
				Intent intent5 = new Intent(context, MainActivity.class);
				intent5.putExtra("index", 5);
				startActivity(intent5);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}
		}
			break;
		// 收银台
		case R.id.iv_cash: {
			if (auths.contains("pos_nogoods")) {
				Intent intent1 = new Intent(context, MainActivity.class);
				intent1.putExtra("index", 1);
				startActivity(intent1);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}

		}
			break;
		// 消息通知
		case R.id.iv_message: {
			if (auths.contains("pos_msg")) {
				Intent intent6 = new Intent(context, MainActivity.class);
				intent6.putExtra("index", 6);
				startActivity(intent6);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}
		}
			break;
		// pos管理
		case R.id.iv_pos: {
			if (auths.contains("pos_pos")) {
				Intent intent7 = new Intent(context, MainActivity.class);
				intent7.putExtra("index", 7);
				startActivity(intent7);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}
		}
			break;
		// 店铺管理
		case R.id.iv_services: {
			if (auths.contains("pos_shop")&&!Constants.shop.getShopTypeId().equals("1001")) {
				Intent intent4 = new Intent(context, MainActivity.class);
				intent4.putExtra("index", 8);
				startActivity(intent4);
			} else {
				TipsToast.makeText(context, "该用户没有这个权限", 1).show();
			}
		}
			break;
		case R.id.iv_logout: {
			LogOut();
		}
			break;
		default:
			break;
		}
	}
}
