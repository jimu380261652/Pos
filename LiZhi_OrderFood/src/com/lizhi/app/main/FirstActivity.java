package com.lizhi.app.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.duiduifu.util.CommonUtil;
import com.duiduifu.util.MD5Util;
import com.lizhi.push.MyApplication;
import com.pos.lizhiorder.R;

public class FirstActivity extends BaseActivity {
	

	private RelativeLayout mProgressDialog;
//	Loading loading;
	CommonUtil mCommonUtil = new CommonUtil(this);
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_first);
		((MyApplication)getApplication()).addActivity(this);
		initUI();
		

	}

	private void initUI() {
		
		mProgressDialog=(RelativeLayout) findViewById(R.id.layout_progress_dialog);
		
		mProgressDialog.getBackground().setAlpha(125);
		
		mProgressDialog.setVisibility(View.VISIBLE);
		
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		check();
		
		
	}
	
	
	public void check(){
//		checkFlag();
		mProgressDialog.postDelayed(new Runnable() {
				@Override
				public void run() {
					checkFlag();
				}
			}, 2000);;
	}

	
	
	/**
	 * 检查标示
	 */
	private void checkFlag() {
		boolean isExist= Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		
		
		
		if(mCommonUtil.isFileExist("fanke_l/fanke.txt")){
			try {
				File file = new File(Environment.getExternalStorageDirectory()+"/fanke_l/fanke.txt");
				String encoding="UTF-8";
				InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				String Text = null;
				lineTxt = bufferedReader.readLine();
				read.close();
				
				String sign=MD5Util.createSign("true");
				if(sign.equals(lineTxt)){
					String str="";
//					String terminalId=android.os.Build.SERIAL;
					String terminalId=((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
					SharedPreferences sp=getSharedPreferences("terminalId", MODE_APPEND);
					
					Editor editor=sp.edit();
					
					editor.putString("terminalId", terminalId);
					
					editor.commit();
					
					Intent intent  = new Intent(this,LoginActivity.class);
					this.startActivity(intent);
					this.finish();
				}else{
					Intent intent  = new Intent(this,ActivateMainActivity.class);
					this.startActivity(intent);
					this.finish();
				}
				
			} catch (Exception e) {
				Intent intent  = new Intent(this,ActivateMainActivity.class);
				this.startActivity(intent);
				e.printStackTrace();
				this.finish();
			} 
		}else{
			if(!isExist){
				check();
				return;
			}
			Intent intent  = new Intent(this,ActivateMainActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		
	}
	
}
