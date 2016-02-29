package com.lizhi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lizhi.app.main.LoginActivity;
import com.lizhi.push.MyApplication;
import com.lizhi.service.WindowService;

public class EndAllReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		try {
			Intent intent = new Intent();
			intent.setClass(arg0, WindowService.class);
			arg0.stopService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (MyApplication.getInstants()!=null) {
			MyApplication.getInstants().finishAllExitThisActivity(LoginActivity.class);
		}
		
	
	}

}
