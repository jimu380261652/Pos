package com.lizhi.app.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String action_boot="android.intent.action.BOOT_COMPLETED";  

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){  
	            Intent ootStartIntent=new Intent(context,LoginActivity.class);  
	            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	            context.startActivity(ootStartIntent);  
	        } 

	}

}
