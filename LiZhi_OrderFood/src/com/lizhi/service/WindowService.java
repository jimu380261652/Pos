package com.lizhi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import com.androidlibrary.app.view.WindowHolderManager;
import com.androidlibrary.app.view.WindowHolderManager$WindowHolderManagerCallback;
import com.lizhi.app.dialog.WindowDialog;
import com.pos.lizhiorder.R;
/**
 * 锁屏弹出框
 * @author user
 *
 */
public class WindowService extends Service implements WindowHolderManager$WindowHolderManagerCallback{
	WindowHolderManager windowHolderManager;
	WindowDialog windowDialog;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		windowHolderManager = new WindowHolderManager();
		windowHolderManager.setWindowHolderManagerCallback(this);
		windowHolderManager.createSmallWindow(this, R.layout.dialog_window);
		windowDialog=new WindowDialog(this);
	}

	@Override
	public void onClickWindow(View v) {
		windowDialog.show();
	}
	
	@Override
	public void onDestroy() {
		if(windowHolderManager!=null && windowHolderManager.isWindowShowing()){
			windowHolderManager.removeSmallWindow(getApplicationContext());
		}
		super.onDestroy();
	}

}
