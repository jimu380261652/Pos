package com.lizhi.app.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.duiduifu.util.CommonUtil;
import com.duiduifu.util.MD5Util;
import com.lizhi.push.MyApplication;
import com.pos.lizhiorder.R;

public class ActivateResultActivity extends BaseActivity implements OnClickListener {
	
	private boolean isSuccess = false;
	
	private RelativeLayout vSuccessLayer,vErrorLayer;
	
	private Button vAgainBtn,vBackBtn;
	CommonUtil mCommonUtil = new CommonUtil(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_activate_result);
		((MyApplication)getApplication()).addActivity(this);
		initUI();
	}

	
	private void initUI() {
		isSuccess = getIntent().getBooleanExtra("isSuccess", false);
		
		vSuccessLayer = (RelativeLayout) this.findViewById(R.id.activate_success_layer);
		vErrorLayer = (RelativeLayout) this.findViewById(R.id.activate_error_layer);
		vAgainBtn = (Button) this.findViewById(R.id.again_btn);
		vBackBtn = (Button) this.findViewById(R.id.back_btn);
		
		if(isSuccess){
			
			writeFlag();
			vErrorLayer.setVisibility(View.GONE);
			vSuccessLayer.setVisibility(View.VISIBLE);
		}else{
			vSuccessLayer.setVisibility(View.GONE);
			vErrorLayer.setVisibility(View.VISIBLE);
		}
		
		vAgainBtn.setOnClickListener(this);
		
	}


	
	private void writeFlag() {
		
		
		FileOutputStream fOut = null;
		String fileFullName = Environment.getExternalStorageDirectory()+"/"+ "fanke_l/fanke.txt";
		
		try {
			//看目录在不在
			File dirFile = new File(Environment.getExternalStorageDirectory()+"/"+ "fanke_l/");
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}

			File f = new File(fileFullName);
			f.createNewFile();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fOut != null)
				try {
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		RandomAccessFile mm = null;
		FileOutputStream o = null;
		try {
			
			String content = MD5Util.createSign("true");
			o = new FileOutputStream(Environment.getExternalStorageDirectory()+"/"
					+ "fanke_l/fanke.txt");
			o.write(content.getBytes("UTF-8"));
			o.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (mm != null) {
				try {
					mm.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		vSuccessLayer.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(ActivateResultActivity.this,LoginActivity.class);
				ActivateResultActivity.this.startActivity(intent);
				ActivateResultActivity.this.finish();
				
			}
		}, 1000);
	}


	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.again_btn){
			this.finish();
			return;
		}
		
		if(v.getId()==R.id.back_btn){
			this.finish();
			return;
		}
		
	}
	
	@Override
	public void onBackPressed() {
	}

}
