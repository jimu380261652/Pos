package com.lizhi.app.main;




import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.TipsToast;
import com.lizhi.util.Constants;
import com.pos.lizhiorder.R;

public class KeyDownload extends Activity{
	private Context context;
	private Button get;
	private String mer_String,ter_String,trs_tr_String="0",downTypeString="2";
	private TextView merId,terId;
	private Handler mMainHandler;
	private Intent intent;
	private Bundle bundle;
	private Loading loading;
	private ComponentName component;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.key_download);
		init();
	}
	public void init(){
		 context=this;
		 loading=new Loading(context);
		 loading.setCancelable(false);
		 get=(Button)findViewById(R.id.bt_pos_get);
		 merId=(TextView)findViewById(R.id.tv_merid);
		 terId=(TextView)findViewById(R.id.tv_terid);
		 get.setOnClickListener(l);
		 mMainHandler  = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case 0:
						loading.cancel();
						merId.setText(mer_String);
						terId.setText(ter_String);
						onNextClick();
						break;
					case 1:
						loading.cancel();
						TipsToast.makeText(context, msg.obj.toString(),
								Toast.LENGTH_SHORT).show();
						break;
						
					case 2:
						TipsToast.makeText(context, msg.obj.toString(),
								Toast.LENGTH_SHORT).show();
						break;
					case 3:

						TipsToast.makeText(context, msg.obj.toString(),
								Toast.LENGTH_SHORT).show();
						
						break;
					
					default:
						break;
					}
				}

			};
			
	}
	OnClickListener l=new OnClickListener(){
		 public void onClick(View v) {
			 if(v.getId()==R.id.bt_pos_get){
				loading.show();
				toGetMessage();
//				if (mer_String.equals("")&&ter_String.equals("")) {
//					TipsToast.makeText(context, "请先获取商户号和终端编号", 1000).show();
//				}else {
//					onNextClick();
//				}
				return;
			 }
		 }
	 };
	 private void toGetMessage() {
			HashMap<String, Object> mHashMap=new HashMap<String, Object>();
			mHashMap.put("terminalId", Constants.terminalId);
			mHashMap.put("shopType", downTypeString);
			mHashMap.put("userId", Constants.shop.getUserId());
			mHashMap.put("loginSign",Constants.loginSign);
			mHashMap.put("sign", MD5Util.createSign(Constants.shop.getUserId(),Constants.loginSign,Constants.terminalId,downTypeString));
			AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"ActivateTerminal/activate.do", "POST", mHashMap.entrySet(), new AsyncListener() {
				
				@Override
				public void onException(Object exceptionInfo) {
					sendMessageToHandler(1,AppConstantByUtil.app_network_exception_tip);
				}
				
				@Override
				public void onComplete(String values) {
					try {
						JSONObject jsonObject=new JSONObject(values);
						String code=jsonObject.getString("code");
						if (code.equals("1")) {
							mer_String = jsonObject.getString("merchantNO");
							ter_String = jsonObject.getString("terminalNO");
							sendMessageToHandler(0,"数据获取成功！");
						}else{
							sendMessageToHandler(1,jsonObject.getString("message"));
						}
					} catch (Exception e) {
						e.printStackTrace();
						sendMessageToHandler(1,AppConstantByUtil.app_json_exception_tip);
					}
					
				}
			});
		}
	 public void sendMessageToHandler(int what,String content) {
			Message message = mMainHandler.obtainMessage();
			message.what = what;
			message.obj = content;
			mMainHandler.sendMessage(message);
	 }
	 private void onNextClick() {
			component=new ComponentName("com.centerm.df.main", "com.centerm.df.main.activity.MainControlActivity");
			intent=new Intent();
			intent.setComponent(component);
			bundle=new Bundle();
			bundle.putString("trs_tp", trs_tr_String);
			bundle.putString("proc_cd", "002321");
			bundle.putString("merid", mer_String);
			bundle.putString("termid", ter_String);
			intent.putExtras(bundle);
			this.startActivityForResult(intent, 1);
		}
}
