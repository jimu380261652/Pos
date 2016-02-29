package com.lizhi.app.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.centerm.smartpos.sys.ISystemInf;
import com.centerm.smartpos.sys.impl.SysImplFactory;
import com.lizhi.fragment.AdvertisementFragment;
import com.lizhi.fragment.AdvertisementFragment.OnAdvertisementFragmentListener;
import com.lizhi.fragment.IndexFragment;
import com.lizhi.fragment.IndexFragment.OnIndexFragmentListener;
import com.lizhi.push.MyApplication;
import com.lizhi.service.HeartService.HeartSyncCallback;
import com.lizhi.service.WindowService;
import com.pos.lizhiorder.R;

/**
 * 
 * @ClassName: MainActivity.java 
 *
 * @Description: 主界面
 *
 * @author YangTaoLin
 *
 * @date 2013-12-12 上午18:06:09 
 *
 */
public class MainCopyActivity extends FragmentActivity implements OnIndexFragmentListener,OnAdvertisementFragmentListener, HeartSyncCallback{


	private Fragment mIndexFragment,mAdFragment;

	public String userId,loginSign;
	
	public final static int PAYRESULTREQUESTCODE = 8;
	public final static int PAYRESULRESULTTCODE = 8;
	
//	private ICloudPay mCupService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		((MyApplication)getApplication()).addActivity(this);
		
		setContentView(R.layout.activity_main);
		
		userId=getIntent().getStringExtra("userId");
				
		loginSign=getIntent().getStringExtra("loginSign");
				
		IndexFragment.userId=userId;
		
		IndexFragment.loginSign=loginSign;
		
		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
//		mIndexFragment = fm.findFragmentById(R.id.index_fragment);
//		
//		mAdFragment = fm.findFragmentById(R.id.ad_fragment);
				
		ft.hide(mAdFragment);
		
		ft.commit();
		
		
		Intent intent = new Intent();
		intent.setClass(this, WindowService.class);
		this.startService(intent);
		
	}

	@Override
	public void showAdFragment() {
		// TODO Auto-generated method stub
		
		IndexFragment.pause=false;
		
		AdvertisementFragment.pause=true;

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		ft.setCustomAnimations(android.R.anim.fade_in,  
                android.R.anim.fade_out); 
		
		ft.hide(mIndexFragment);
		
		ft.show(mAdFragment);
		
		ft.commit();
	}
	
	public void showPayResultActivity(String CardNum,String payAmount,String memo,String result,boolean isUnBundler){
		Intent intent=new Intent(this,PayResultActivity.class);
		intent.putExtra("CardNum", CardNum);
		intent.putExtra("payAmount", payAmount);
		intent.putExtra("memo", memo);
		intent.putExtra("result", result);
		this.startActivityForResult(intent, PAYRESULTREQUESTCODE);
//		if (isUnBundler) {
//			unbindService(mCupConnection);
//			isUnBundler=false;
//		}
		
		
	}
	
	//如果这里没什么用的话，就把这个绑定给删除了吧
//	private ServiceConnection mCupConnection=new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			// TODO Auto-generated method stub
//			mCupService = ICloudPay.Stub.asInterface(service);
//			if (mCupService == null) {
//				return;
//			}
//			try {
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				
//			} finally {
//				mCupService = null;
//			}
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	};

	@Override
	public void showIndexFragment() {
		// TODO Auto-generated method stub
				
		IndexFragment.pause=true;
		
		AdvertisementFragment.pause=false;
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		ft.setCustomAnimations(android.R.anim.fade_in,  
                android.R.anim.fade_out); 
		
		ft.show(mIndexFragment);
		
		ft.hide(mAdFragment);
		
		ft.commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IndexFragment.pause=true;
		try {
			ISystemInf sysInf = SysImplFactory.getSystemInf();
			sysInf.closePinpadKeyboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		
		IndexFragment.pause=false;
		
		IndexFragment.second=0;
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		((IndexFragment)mIndexFragment).loadReadCount();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_HOME) {
	            return true;
	        }
		
		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	public void onBackPressed() {
		try {
			Intent intent = new Intent();    
	        intent.setAction("com.duiduifu.retail.sync.close");//发出自定义广播
	        this.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.finish();
	}
	
	
	@Override
	protected void onDestroy() {
		
		System.out.println("===========MainActivity-Destroy!	");
		
		try {
//			this.unbindService(conn);
			
			((MyApplication)getApplication()).removeOnly(MainCopyActivity.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Intent intent = new Intent();
			intent.setClass(this, WindowService.class);
			this.stopService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onDestroy();
	}
	
	
	
	
	//==============  Service绑定   ================
	
//	protected HeartService heartService;
//	
//	public HeartService getHeartService(){
//		return heartService;
//	}
	
	// 定义一个ServiceConnection对象
//	private ServiceConnection conn = new ServiceConnection()
//	{
//
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			heartService=((SyncBinder) service).getSyncService();
//			heartService.setHeartSyncCallback(MainActivity.this);
//		}
//
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//			System.out.println("--Service Disconnected--");	
//			heartService = null; 
//		}
//	
//	};
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == IndexFragment.MESSAGE_LIST_REQUEST && resultCode == MessageListActivity.MESSAGE_LIST_RESULT){
			if(mIndexFragment!=null){
//				((IndexFragment)mIndexFragment).setMessageHint();
			}
		}
	};

	
	
	//=============  Service回调  ==============
	
	@Override
	public void setHintNumber(int messageNum, int orderNum) {
		if(mIndexFragment!=null){
			((IndexFragment)mIndexFragment).setHint(orderNum, messageNum);
		}
	}

	
}
