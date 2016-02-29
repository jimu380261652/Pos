package com.lizhi.app.main;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.view.KeyBoardView;
import com.duiduifu.view.KeyBoardView.OnKeyBoardButtonListener;
import com.duiduifu.view.KeyBoardView.OnKeyBoardLongButtonListener;
import com.lizhi.push.MyApplication;
import com.pos.lizhiorder.R;

public class ActivateMainActivity extends BaseActivity implements OnClickListener {
	
	private TextView vMachinesId;
	
	private EditText vMerchantId,vInstallId,vInstallPW,mEditText;
	
	private Button vActivateBtn;
	
	private KeyBoardView mKeyBoardView;
	
	private int checkNum = 1;
	
	String terminalId;//机具ID：Mac地址

	private Handler mMainHadler;
		
	private RelativeLayout mProgressDialog;

	private String userId,pwd,shopId;
//	Loading loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_activate_main);
		((MyApplication)getApplication()).addActivity(this);
		vMachinesId = (TextView) this.findViewById(R.id.machines_id);
		vMerchantId = (EditText) this.findViewById(R.id.merchant_id);
		vInstallId = (EditText) this.findViewById(R.id.install_id);
		vInstallPW = (EditText) this.findViewById(R.id.install_pw);
		vActivateBtn = (Button) this.findViewById(R.id.activate_btn);
		//加载框
		mProgressDialog=(RelativeLayout) findViewById(R.id.layout_progress_dialog);
				
		mProgressDialog.getBackground().setAlpha(125);
		
		vMerchantId.setInputType(InputType.TYPE_NULL);
		vInstallId.setInputType(InputType.TYPE_NULL);
		vInstallPW.setFocusable(false);
		
		mEditText = vMerchantId;
		
		mKeyBoardView = (KeyBoardView) findViewById(R.id.keyBoardView);
		
		mKeyBoardView.setOnKeyBoardButtonListener(new OnKeyBoardButtonListener() {

			@Override
			public void OnClick(String kb_type,String kb_text) {
				// TODO Auto-generated method stub
				
				if (kb_text.equals(AppConstantByUtil.CLEAR)) {

					int index = mEditText.getText().toString().length();  

					if(index<=0)
						return;
					
					mEditText.getText().delete(index-1, index);

				}else if(kb_text.equals(AppConstantByUtil.NEXT)){
					
					switchEditText(++checkNum);
				} else {
					
										
					mEditText.getText().append(kb_text);

				}
				
			}

		},true,true, true);
		mKeyBoardView.setOnKeyBoardLongButtonListener(new OnKeyBoardLongButtonListener() {
			
			@Override
			public void OnLongClick(String kb_type, String kb_text) {
				// TODO Auto-generated method stub
				
				if (kb_text.equals(AppConstantByUtil.CLEAR)) {
					int index = mEditText.getText().toString().length();  
//
					if(index<=0)
						return;
//					
					mEditText.getText().delete(0, index);
					mEditText.setText("");
				}
			}
		}, true, false, false);
		vMerchantId.setOnClickListener(this);
		vInstallId.setOnClickListener(this);
		vInstallPW.setOnClickListener(this);
		vActivateBtn.setOnClickListener(this);
		
		mMainHadler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				
				switch (msg.what) {
				
					case 0:
						
						//获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象  
		                LayoutInflater inflater = getLayoutInflater();  
		                //根据指定的布局文件创建一个具有层级关系的View对象  
		                //第二个参数为View对象的根节点，即LinearLayout的ID  
		                View layout = inflater.inflate(R.layout.toask_view, (ViewGroup) findViewById(R.id.toask_layout));  
		                  
		                //查找ImageView控件  
		                //注意是在layout中查找  
		                TextView text = (TextView) layout.findViewById(R.id.toask_text);  
		                text.setText(msg.obj.toString());  
		  
		                Toast toast = new Toast(getApplicationContext());  
		                //设置Toast的位置  
		                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, -30);  
		                toast.setDuration(Toast.LENGTH_LONG);  
		                //让Toast显示为我们自定义的样子  
		                toast.setView(layout);  
		                toast.show();
		                mProgressDialog.setVisibility(View.GONE);
//						loading.cancel();
						break;
						
					case 1:
						mProgressDialog.setVisibility(View.GONE);
//						loading.cancel();
						Intent intent = new Intent(ActivateMainActivity.this, ActivateResultActivity.class);
						intent.putExtra("isSuccess", true);
						startActivity(intent);
						finish();
//						mUiUtil.theClues(msg.obj.toString());
						break;
					case 2:
						vMachinesId.setText(msg.obj.toString());
						mProgressDialog.setVisibility(View.GONE);
					default:
						break;
				}
				
//				mProgressDialog.setVisibility(View.GONE);
//				loading.cancel();
				
			}
			
			
		};
		getMacLocal();
	}

//	public void loading() {
//		loading=new Loading(this);
//		loading.show();
//	}
	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.merchant_id){
			switchEditText(1);
			return;
		}
		if(v.getId()==R.id.install_id){
			switchEditText(2);
			return;
		}
		if(v.getId()==R.id.install_pw){
			switchEditText(3);
			return;
		}
		
		if(v.getId()==R.id.activate_btn){
			if(vMerchantId.getText().toString()==null || vMerchantId.getText().toString().isEmpty()){
				Toast.makeText(this, "请输入商户ID", Toast.LENGTH_SHORT).show();
				return;
			}
			if(vInstallId.getText().toString()==null || vInstallId.getText().toString().isEmpty()){
				Toast.makeText(this, "请输入安装员ID", Toast.LENGTH_SHORT).show();
				return;
			}
			if(vInstallPW.getText().toString()==null || vInstallPW.getText().toString().isEmpty()){
				Toast.makeText(this, "请输入安装员密码", Toast.LENGTH_SHORT).show();
				return;
			}
			
			shopActivate();
			
			return;
		}
		
	}
	
	public void switchEditText(int checkNum){
		if(checkNum==1){
			vInstallId.setBackgroundResource(R.drawable.input_edit_bg_nol);

			vInstallPW.setBackgroundResource(R.drawable.input_edit_bg_nol);
			
			vMerchantId.setBackgroundResource(R.drawable.input_edit_bg_press);
			
			mEditText=vMerchantId;
			
			this.checkNum = 1;
			
			return;
		}
		
		if(checkNum==2){

			vInstallPW.setBackgroundResource(R.drawable.input_edit_bg_nol);
			
			vMerchantId.setBackgroundResource(R.drawable.input_edit_bg_nol);
			
			vInstallId.setBackgroundResource(R.drawable.input_edit_bg_press);
			
			mEditText=vInstallId;
			
			this.checkNum = 2;
			
			return;
		}
		
		if(checkNum>=3){

			
			vMerchantId.setBackgroundResource(R.drawable.input_edit_bg_nol);
			
			vInstallId.setBackgroundResource(R.drawable.input_edit_bg_nol);
			
			vInstallPW.setBackgroundResource(R.drawable.input_edit_bg_press);
			
			mEditText=vInstallPW;
			
			this.checkNum = 3;
			
			return;
		}
	}
	
	//获取Mac地址
	public String getMacLocal() {
		mProgressDialog.setVisibility(View.VISIBLE);
//		String str="";
//		try {
//			TelephonyManager manager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//			terminalId=android.os.Build.SERIAL;
			String terminalId=((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
//			String imsi=manager.getSubscriberId();//IMSI
//			Process pp=Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
//			InputStreamReader ir=new InputStreamReader(pp.getInputStream());
//			LineNumberReader input=new LineNumberReader(ir);
//			for (; null!=str;) {
//				str=input.readLine();
//				if (str!=null) {
//					terminalId=str.trim();//去空格
//					break;
//				}
//			}
//			sendMessageToHandler(2, terminalId);
//		} catch (IOException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		sendMessageToHandler(2, terminalId);
		return terminalId;
	}
	
	
	/**
	 * 商户激活
	 */
	public void shopActivate(){
				
		SharedPreferences sp=getSharedPreferences("terminalId", MODE_APPEND);
		
		Editor editor=sp.edit();
		
		editor.putString("terminalId", vMachinesId.getText().toString());
		
		editor.commit();
		
		shopId=vMerchantId.getText().toString();
		
		userId=vInstallId.getText().toString();
		
		pwd=vInstallPW.getText().toString();
		
		if (shopId==null||shopId.equals("")) {
			//获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象  
            LayoutInflater inflater = getLayoutInflater();  
            //根据指定的布局文件创建一个具有层级关系的View对象  
            //第二个参数为View对象的根节点，即LinearLayout的ID  
            View layout = inflater.inflate(R.layout.toask_view, (ViewGroup) findViewById(R.id.toask_layout));  
              
            //查找ImageView控件  
            //注意是在layout中查找  
            TextView text = (TextView) layout.findViewById(R.id.toask_text);  
            text.setText("用户ID不能为空");  

            Toast toast = new Toast(getApplicationContext());  
            //设置Toast的位置  
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, -30);  
            toast.setDuration(Toast.LENGTH_LONG);  
            //让Toast显示为我们自定义的样子  
            toast.setView(layout);  
            toast.show();
			return;
		}
		
		if(userId==null || userId.equals("")){
			
			//获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象  
            LayoutInflater inflater = getLayoutInflater();  
            //根据指定的布局文件创建一个具有层级关系的View对象  
            //第二个参数为View对象的根节点，即LinearLayout的ID  
            View layout = inflater.inflate(R.layout.toask_view, (ViewGroup) findViewById(R.id.toask_layout));  
              
            //查找ImageView控件  
            //注意是在layout中查找  
            TextView text = (TextView) layout.findViewById(R.id.toask_text);  
            text.setText(R.string.app_username_is_not_empty_tip);  

            Toast toast = new Toast(getApplicationContext());  
            //设置Toast的位置  
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, -30);  
            toast.setDuration(Toast.LENGTH_LONG);  
            //让Toast显示为我们自定义的样子  
            toast.setView(layout);  
            toast.show();
			
			return;
		}
		
		if(pwd==null || pwd.equals("")){
			
			//获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象  
            LayoutInflater inflater = getLayoutInflater();  
            //根据指定的布局文件创建一个具有层级关系的View对象  
            //第二个参数为View对象的根节点，即LinearLayout的ID  
            View layout = inflater.inflate(R.layout.toask_view, (ViewGroup) findViewById(R.id.toask_layout));  
              
            //查找ImageView控件  
            //注意是在layout中查找  
            TextView text = (TextView) layout.findViewById(R.id.toask_text);  
            text.setText(R.string.app_password_is_not_empty_tip);  

            Toast toast = new Toast(getApplicationContext());  
            //设置Toast的位置  
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, -30);  
            toast.setDuration(Toast.LENGTH_LONG);  
            //让Toast显示为我们自定义的样子  
            toast.setView(layout);  
            toast.show();
			return;
		}

		pwd=MD5Util.createSign(pwd);
		
		String sign=MD5Util.createSign(vMachinesId.getText().toString(),shopId,userId, pwd);
		
	

		mProgressDialog.setVisibility(View.VISIBLE);
//		loading();
		
		
		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
		
		mHashMap.put("terminalId", vMachinesId.getText().toString());
		
		mHashMap.put("userId", userId);
		
		mHashMap.put("pwd",pwd);
		
		mHashMap.put("shopId", shopId);
		
		mHashMap.put("sign",sign);
		
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"core/Activate/activeTerminal.do", "GET", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				// TODO Auto-generated method stub
				sendMessageToHandler(AppConstantByUtil.ERROR,AppConstantByUtil.app_network_exception_tip);
			}
			
			@Override
			public void onComplete(String values) {
				// TODO Auto-generated method stub
				
				try {
					
				JSONObject jsonObject=new JSONObject(values);
				
				String msg= jsonObject.getString("message");
				
				String code=jsonObject.getString("code");
				
					if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
						
						sendMessageToHandler(AppConstantByUtil.SUCCESS,msg);
																		
					}else{
						
						sendMessageToHandler(AppConstantByUtil.ERROR,msg);
					}
					
				
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sendMessageToHandler(AppConstantByUtil.ERROR,AppConstantByUtil.app_json_exception_tip);

				}
				
			}
		});
		
	}
	/**
	 * 发消息给主线程更新UI
	 * 
	 * @param what
	 */
	public void sendMessageToHandler(int what,String msg) {

		Message message = mMainHadler.obtainMessage();
		message.what = what;
		message.obj=msg;
		mMainHadler.sendMessage(message);
	}
}
