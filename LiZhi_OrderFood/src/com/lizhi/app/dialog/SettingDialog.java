package com.lizhi.app.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pos.lizhiorder.R;

public class SettingDialog extends BaseDialog {

	Context mContext;
	SettingDialogCallback mCallback;
	
	LinearLayout vSettingLayer;
	LinearLayout vVPNLayer;
	LinearLayout vGLayer;
	LinearLayout vWifiLayer;
	LinearLayout vMTMSLayer;
	LinearLayout vDateLayer;
	LinearLayout vImageLayer;
	TextView vGText;
	
	boolean isShowImage = false;
	
	public SettingDialog(Context context,boolean isShowImage,SettingDialogCallback callback) {
		super(context);
		mContext = context;
		mCallback = callback;
		this.isShowImage = isShowImage;
		this.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK){
					return true;
				}
				return false;

			}
		});
		initUI();
	}

	@SuppressWarnings("static-access")
	@SuppressLint("InlinedApi")
	private void initUI() {
		View v = getLayoutInflater().inflate(R.layout.dialog_setting, null);	
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(400, 550);
//		
		vSettingLayer = (LinearLayout) v.findViewById(R.id.setting_layer);
		vVPNLayer = (LinearLayout) v.findViewById(R.id.vpn_layer);
		vGLayer = (LinearLayout) v.findViewById(R.id.g_layer);
		vWifiLayer = (LinearLayout) v.findViewById(R.id.wifi_layer);
		vMTMSLayer = (LinearLayout) v.findViewById(R.id.mtms_layer);
		vDateLayer = (LinearLayout) v.findViewById(R.id.date_layer);
		vImageLayer = (LinearLayout) v.findViewById(R.id.image_layer);
		if(isShowImage){
			vImageLayer.setVisibility(View.VISIBLE);
		}else{
			vImageLayer.setVisibility(View.GONE);
		}
		
		vGText = (TextView) v.findViewById(R.id.g_text);
		
		vWifiLayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mCallback.onWIFIClick();
				SettingDialog.this.dismiss();
			}
		});
		vSettingLayer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.onSettingClick();
				SettingDialog.this.dismiss();
			}
		});
		vVPNLayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mCallback.onVPNClick();
				SettingDialog.this.dismiss();
			}
		});
		vGLayer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.onGClick();
				SettingDialog.this.dismiss();
			}
		});
		vMTMSLayer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.onMTMSClick();
				SettingDialog.this.dismiss();
			}
		});
		vDateLayer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.onDateClick();
				SettingDialog.this.dismiss();
			}
		});
		vImageLayer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mCallback.onImageClick();
				SettingDialog.this.dismiss();
			}
		});
		
		
		
		this.addContentView(v, lp);
	}
	
	

	
	
	public void showDialog(String g_text){
		vGText.setText(g_text);
		this.show();
	}
	
	
	public interface SettingDialogCallback{
		public void onSettingClick();
		public void onVPNClick();
		public void onGClick();
		public void onWIFIClick();
		public void onMTMSClick();
		public void onDateClick();
		public void onImageClick();
	}


	
	

}
