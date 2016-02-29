package com.lizhi.fragment;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.centerm.zxing.QRCodeEncoder;
import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.app.main.Loading;
import com.lizhi.util.Constants;
import com.lizhi.util.DataUtil;
import com.lizhi.util.ShareUtils;
import com.lizhi.util.StringUtil;
import com.pos.lizhiorder.R;

public class MemberFragment1 extends BaseFragment {
	private Context context;
	private Button bt_send;
	private EditText phoneNum;
	private ImageView iv_code;
	Loading loading;
	Handler mMainHadler;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle SavedInstanceState) {
		View MyViewGroup = inflater.inflate(R.layout.fragment_member1,
				container, false);

		return MyViewGroup;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		initView();
	}

	/**
	 * 验证手机格式
	 */
	public boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			return mobiles.matches(telRegex);
		}
	}

	public void initView() {
		loading = new Loading(context);
		loading.setCancelable(false);
		bt_send = (Button) getView().findViewById(R.id.bt_member_send);
		bt_send.setClickable(false);
		bt_send.setOnClickListener(l);
		phoneNum = (EditText) getView().findViewById(R.id.et_member_phone);
		phoneNum.addTextChangedListener(new TextWatcher() {

			int beforeLen = 0;
			int afterLen = 0;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.toString().replaceAll(" ", "").length() == 11) {
					if (isMobileNO(s.toString().replaceAll(" ", ""))) {
						bt_send.setClickable(true);
						bt_send.setBackgroundResource(R.drawable.btn_blue_selector);

						ShareUtils.putString(context, "hasphone", s.toString()
								.replaceAll(" ", ""));
					} else {
						TipsToast.makeText(context, "请输入正确的手机号码",
								Toast.LENGTH_SHORT);

						// bt_send.setVisibility(View.GONE);
						bt_send.setClickable(false);
						bt_send.setBackgroundResource(R.drawable.btn_gray);
					}
				} else {

					// bt_send.setVisibility(View.GONE);
					bt_send.setClickable(false);
					bt_send.setBackgroundResource(R.drawable.btn_gray);
				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeLen = s.length();

			}

			public void afterTextChanged(Editable s) {

				String txt = phoneNum.getText().toString();
				afterLen = txt.length();
				if (afterLen > beforeLen) {
					if (txt.length() == 4 || txt.length() == 9) {
						phoneNum.setText(new StringBuffer(txt).insert(
								txt.length() - 1, " ").toString());
						phoneNum.setSelection(phoneNum.getText().length());
					}
				} else {
					if (txt.startsWith(" ")) {
						phoneNum.setText(new StringBuffer(txt).delete(
								afterLen - 1, afterLen).toString());
						phoneNum.setSelection(phoneNum.getText().length());

					}
				}

			}
		});
		mMainHadler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					TipsToast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					phoneNum.setText("");
					MemberFragment2 mem = (MemberFragment2) getActivity()
							.getSupportFragmentManager().findFragmentByTag(
									"MemberFragment2");
					mem.setMember();
					break;
				case 2:
					TipsToast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		};
		iv_code = (ImageView) getView().findViewById(R.id.iv_code);
		bt_send.setOnClickListener(l);
		String bString = AppConstantByUtil.PRINT_IP
				+ "reg?s="
				+ Base64.encodeToString(Constants.shop.getShopId().getBytes(),
						Base64.DEFAULT)
				+ "&tm="
				+ Base64.encodeToString(Constants.terminalId.getBytes(),
						Base64.DEFAULT);
		if (bString != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher bm = p.matcher(bString);
			bString = bm.replaceAll("");
		}
		Bitmap bitmap = QRCodeEncoder.getQRCodeBmp(bString,
				DataUtil.dip2px(context, 250));
		iv_code.setImageBitmap(bitmap);

	}

	OnClickListener l = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_member_send: {
				if (StringUtil.isEmpty(phoneNum.getText().toString())) {
					TipsToast.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT)
							.show();
				} else {
					addMember();
				}
			}
				break;
			}
		}
	};

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

	private void addMember() {
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();
		String phone = phoneNum.getText().toString().replace(" ", "");
		mHashMap.put("mobile", phone);
		mHashMap.put("terminalId", Constants.terminalId);
		mHashMap.put("sign", MD5Util.createSign(Constants.terminalId, phone));
		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "Member/addMemberByMobile.do", "GET",
				mHashMap.entrySet(), new AsyncListener() {

					public void onComplete(String values) {

						try {

							JSONObject jsonObject = new JSONObject(values);

							String msg = jsonObject.getString("message");

							String code = jsonObject.getString("code");
							Log.i("pos", values);
							if (code.equals(String.valueOf("1"))) {
								/*
								 * TipsToast.makeText(context, msg,
								 * Toast.LENGTH_SHORT).show();
								 * phoneNum.setText("");
								 */
								sendMessageToHandler(1, "短信发送成功");
								/*sendMessageToHandler(1, "已经给"
										+ phoneNum.getText().toString()
												.replace(" ", "") + "发送验证信息");*/
							} else {
								// TipsToast.makeText(context,msg,
								// Toast.LENGTH_SHORT).show();
								sendMessageToHandler(2, msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					public void onException(Object exceptionInfo) {
						// TipsToast.makeText(context,exceptionInfo.toString(),
						// Toast.LENGTH_SHORT).show();
						sendMessageToHandler(2, exceptionInfo.toString());
					}

				});
	}
}
