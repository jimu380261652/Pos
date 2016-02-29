package com.lizhi.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.centerm.smartpos.bean.PrintDataObject;
import com.centerm.smartpos.dev.IPrintDev;
import com.centerm.smartpos.dev.impl.SmartPayFactory;
import com.centerm.zxing.QRCodeEncoder;
import com.duiduifu.bean.PrintOrder;
import com.duiduifu.bean.Sf_BusinessConfig;
import com.duiduifu.db.DbUtil;
import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.duiduifu.util.ValidateUtil;
import com.duiduifu.view.TipsToast;
import com.lizhi.adapter.GridViewAdapter;
import com.lizhi.adapter.GridViewAdapter.OnSelectButtonListener;
import com.lizhi.adapter.ViewPagerAdapter;
import com.lizhi.app.dialog.PasswordDialog;
import com.lizhi.app.dialog.PasswordDialog.PasswordDialogCallback;
import com.lizhi.app.dialog.SettingDialog;
import com.lizhi.app.dialog.SettingDialog.SettingDialogCallback;
import com.lizhi.app.dialog.UnPrintOrderIdDialog;
import com.lizhi.app.dialog.UnPrintOrderIdDialog.PrintCallBack;
import com.lizhi.app.main.Loading;
import com.lizhi.bean.Commodity;
import com.lizhi.push.MyApplication;
import com.lizhi.push.Utils;
import com.pos.lizhiorder.R;

/**
 * 
 * @ClassName: IndexFragment.java 
 *
 * @Description: 首页
 *
 * @author YangTaoLin
 *
 * @date 2013-12-12 上午18:06:09 
 *
 */
public class IndexFragment extends BaseFragment implements OnPageChangeListener, PasswordDialogCallback, SettingDialogCallback,PrintCallBack{
	
	public final static int MESSAGE_LIST_REQUEST = 0;
	
	public final static int POSITION = 8;
	
	
	
//	public DbUtil dbUtil = null;
	
	//升级数据
	public HashMap<String,String> dataUpMap = new HashMap<String, String>();
	
	//订单数据
	List<Commodity> commodityList=new ArrayList<Commodity>();
	
	String nums,orderDate,realName,shipAddress,shipMobile,shipName,shipTel,statusId,statusName,printTwo,
	   totalAmount,userName,orderId,shopName,shopContact,paymentName,shipTime,businessId,memo="",paymentId,attrName,attrContactName,attrPhone,memoMsg;
	int msgCount=0;
	//实体
	private static OnIndexFragmentListener onIndexFragmentListener;
	public static String userId,loginSign,CardNum,customer;
	
	//APP安装监听者
	AppInstallReceiver appInstallReceiver;
	
	//布局
	Loading loading;
	public TextView vOrderHint,vMessageHint,vMessageTv,vCustomer;
	public Button back_btn;
	public ImageView vSettingImg,vPosImg,vOrderImg,vMessageImg,vMemberImg,vSystemImg,vFeekback;
	public Bitmap settingNor,settingPre,posNor,posPre,orderNor,orderPre,messageNor,messagePre,memberNor,memberPre,systemNor,systemPre;
	RelativeLayout vOrderHintLayer,vMessageHintLayer,vUserHintLayer,vPosHintLayer;
	
	RelativeLayout vSetting;;
	
	ConnectivityManager conMgr;
	boolean isOpenG = false,isPrint=false;
	String currentGString;
	
	//弹出框
	PasswordDialog vPasswordDialog;
	SettingDialog settingDialog;
	
	
	//订单等处理（响铃等）
	SoundPool sound;
	int ring;
	private boolean isUnBundler = false;
	ImageView network;
    NotificationManager mNotificationManager;
    Notification mNotification;
//    public MyPushMessageReceiver messageReceiver;
    
    
    //打印广播接收者
    PrintOrderReceiver printOrderReceiver;
    PushReceiver pushReceiver;
    boolean isOrder=false,isSaveOrder=false,isFirstCheckOrder=true;
    SharedPreferences orderCodeShare;
    ArrayList<String>orderCodes;
    UnPrintOrderIdDialog printOrderIdDialog;
    IPrintDev printDev=SmartPayFactory.getPrinterDev();
	List<PrintDataObject>list=new ArrayList<PrintDataObject>();
	int printCount=0;
	DbUtil dbUtil;
	ArrayList<PrintOrder>printOrders=new ArrayList<PrintOrder>();
	public interface OnIndexFragmentListener {

		public void showAdFragment();
		
		public void showPayResultActivity(String CardNum,String payAmount,String memo,String result,boolean isUnBundler);	//FIXME 这个回调方法已经没用了，暂时保留
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		conMgr = (ConnectivityManager) IndexFragment.this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//		dbUtil = ((MyApplication)this.getActivity().getApplication()).getDbUtil();
		
		loading=new Loading(this.getActivity());
		loading.setCancelable(false);
		
		sound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		ring = sound.load(this.getActivity(), R.raw.ding,1);
		dbUtil = ((MyApplication)this.getActivity().getApplication()).getDbUtil();
		if (dbUtil!=null) {
			ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
			data=dbUtil.QueryAllPrintOrderId();
			for (int i = 0; i < data.size(); i++) {
				PrintOrder printOrder=new PrintOrder();
				printOrder.setOrderId(String.valueOf(data.get(i).get(PrintOrder.ORDERID)));
				printOrders.add(printOrder);
			}
		}
		appInstallReceiver = new AppInstallReceiver ();  
		printOrderIdDialog=new UnPrintOrderIdDialog(this.getActivity(), this);
		if (printOrders.size()>0) {
			printOrderIdDialog.show();
		}
		pushReceiver=new PushReceiver();
		printOrderReceiver = new PrintOrderReceiver();
//		messageReceiver=new MyPushMessageReceiver();
//		messageReceiver.setCallback(new CallbackOrder() {
			
//			@Override
//			public void newOrderClicked(String orderNumber) {
				
//			}
//		});
		//实例化过滤器并设置要过滤的广播  
		try {
			IntentFilter intentFilter = new IntentFilter();  
			intentFilter.addAction("android.intent.action.PACKAGE_ADDED");  
			intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");  
			intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");  
			intentFilter.addDataScheme("package");
			//注册广播  （订单广播）
			this.getActivity().registerReceiver(appInstallReceiver, intentFilter);
		} catch (Exception e2) {
			e2.printStackTrace();
		} 
		
		try {
			IntentFilter intentFilter2=new IntentFilter("com.duiduifu.app.neworder");
			this.getActivity().registerReceiver(pushReceiver, intentFilter2);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
//			Intent intent=new Intent(this.getActivity(),MQTTPushReceiver.class);
//			intent.setAction("com.duiduifu.app.pushOrder");
//			this.getActivity().startService(intent);
//			MQTTPushService.actionStart(this.getActivity());
//			MQTTPushReceiver.actionStart(this.getActivity());
//			MyPushService.actionStart(this.getActivity());
//			Intent intent=new Intent(this.getActivity(), MyPushService.class);
//			IndexFragment.this.getActivity().bindService(intent, comm, Context.BIND_AUTO_CREATE);
//			this.getActivity().startService(intent);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			IntentFilter intentFilter3=new IntentFilter("com.duiduifu.app.PrintOrderReceiver");
			this.getActivity().registerReceiver(printOrderReceiver, intentFilter3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		mNotificationManager=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification=new Notification();
		
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this, "api_key")
		// 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
//		if (!Utils.hasBind(this.getActivity())) {
//			PushManager.startWork(this.getActivity(),
//					PushConstants.LOGIN_TYPE_API_KEY, 
//					Utils.getMetaValue(this.getActivity(), "api_key"));
//			// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
//			PushManager.enableLbs(this.getActivity());
//		}
		
//		initWithApiKey();
		clearNotification();
	}
	
	public ServiceConnection comm = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			
			
		}
		
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			
		}
	};
	
	

	


	// 以apikey的方式绑定
	private void initWithApiKey() {
			// Push: 无账号初始化，用api key绑定
			PushManager.startWork(this.getActivity(),
					PushConstants.LOGIN_TYPE_API_KEY, 
					Utils.getMetaValue(this.getActivity(), "api_key"));
	}
		
	/*
	 * 新订单
	 */
	public void newOrder(String orderNumber) {
		boolean isnumber=orderNumber.substring(0, 1).matches("[0-9]+");
		if (isnumber) {
			mNotification.icon=R.drawable.go;
            
            mNotification.tickerText="有新订单";
            
            mNotification.defaults=Notification.DEFAULT_SOUND;
            
            mNotification.setLatestEventInfo(this.getActivity(), "您有新订单", "您有新订单", null);
            
            mNotificationManager.notify(0, mNotification);
            
            vOrderHint.setVisibility(View.VISIBLE);
            
            vOrderHint.setText(orderNumber);
            TipsToast.makeText(IndexFragment.this.getActivity(), vOrderHint.getText().toString(), 1000).show();
		}
        	
        
	}
	
	@Override
	public void onDestroy() {
		try {
			System.out.println("----------MainonDestroy");
			//注销广播
			this.getActivity().unregisterReceiver(appInstallReceiver);
			this.getActivity().unregisterReceiver(pushReceiver);
			this.getActivity().unregisterReceiver(printOrderReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(vPasswordDialog!=null){
			vPasswordDialog.dismiss();
		}
		if(settingDialog!=null){
			settingDialog.dismiss();
		}
		
//		try {
//			if(mGridViewAdapters!=null && mGridViewAdapters.length>0){
//				for(GridViewAdapter a : mGridViewAdapters){
//					if(a!=null){
//						a.clearMeomery();
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		super.onDestroy();
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		try {

			onIndexFragmentListener = (OnIndexFragmentListener) activity;
			
		} catch (ClassCastException e) {
			

			throw new ClassCastException(activity.toString()+ "must implement OnIndexFragmentListener");
		}
		
	}
	
	public static IndexFragment newInstance(int position) {
				
		IndexFragment fragment = new IndexFragment();

		Bundle args = new Bundle();

		args.putInt("position", position);

		fragment.setArguments(args);

		return fragment;

	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mViewGroup = (ViewGroup) inflater.inflate(R.layout.activity_index,
				container, false);
		
		vPasswordDialog = new PasswordDialog(this.getActivity(), this, this.getActivity());
		
		settingDialog = new SettingDialog(this.getActivity(), false,this);
		
		initView(1,mViewGroup);
		
		vSetting = (RelativeLayout) mViewGroup.findViewById(R.id.setting_layout);
		vSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				settingDialog.showDialog(currentGString);
			}
		});
		
		if(gprsIsOpenMethod("getMobileDataEnabled")){
			isOpenG = true;
			currentGString = "关闭3G";
		}else{
			isOpenG = false;
			currentGString = "打开3G";
		}
		
		

		loadPager();	//TODO 
		
		//接收消息,更新UI
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				final int page = msg.arg1;
				
				int position = msg.arg2;

				switch (msg.what) {
				
					case AppConstantByUtil.ERROR:
						TipsToast.makeText(IndexFragment.this.getActivity(), position + ":下载失败!", 1000).show();
						setUpdateFlagForError(page,position);
						break;
					case AppConstantByUtil.STOP:
						break;
					case AppConstantByUtil.MAX:
						progressBars[page][position].setMax(fileSize[page][position]);
						break;
					case AppConstantByUtil.CONTINUE:
						progressBars[page][position].setProgress(downLoadFileSize[page][position]);
	
						break;
					case AppConstantByUtil.SUCCESS:
						TipsToast.makeText(IndexFragment.this.getActivity(), position + ":下载完成!", 1000).show();
						setUpdateFlag(page,position);
	
						//安装APK
						
						File file=new File(commonUtil.getSDCardPath()+mMainData.get(page).get(position).get(Sf_BusinessConfig.apkName));
						
						openFile(file);  
						//更新本地数据库  
						
						break;
						
					case AppConstantByUtil.LOAD_EXCEPTION:		//XXX 某一页数据加载失败
						
						mLayoutCircleGroup.setVisibility(View.GONE);
						
						mViewPager.setVisibility(View.GONE);
						
						mLayoutNetWordTip.setVisibility(View.VISIBLE);
						
						loading.cancel();
						
						mTextViewExceptionTip.setText(exceptionTip);
						
						pause=false;
						
						break;
	
					case AppConstantByUtil.LOAD_BUSINESS_CONFIG:	//XXX 某一页数据加载成功
	
						mGridViewAdapters[page] = new GridViewAdapter(getActivity(),
								mMainData.get(page), new
	
								OnSelectButtonListener() {
	
									@Override
									public void onSelect(final int position,
											boolean isUpdateFlag,
	
											String downLoadUrl) {
	
	
										// isUpdateFlag为True时,点击图标就进行更新
										
//										if (isUpdateFlag) {
//	
//											// 获取gridview下的view
//	
//											View view = (View) mGridViews[page]
//													.getChildAt(position);
//	
//											textViews[page][position] = (TextView) view
//	
//											.findViewById(R.id.textView);
//	
//											progressBars[page][position] = (ProgressBar) view
//	
//											.findViewById(R.id.progressBar);
//	
//											imageViewsBlack[page][position] = (ImageView) view
//	
//											.findViewById(R.id.imageView_black);
//	
//											// 当点击图标时,开始更新,此时new的图标提示要隐藏
//	
//											mMainData.get(page).get(position).put("newIsVisible",
//													View.GONE);
//	
//											// 当点击图标时,黑色的背景显示
//	
//											mMainData.get(page).get(position).put(
//													"blackIsVisible", View.VISIBLE);
//	
//											// 进度条显示
//	
//											progressBars[page][position]
//													.setVisibility(View.VISIBLE);
//	
//											// 半透明黑色背景显示
//	
//											imageViewsBlack[page][position]
//													.setVisibility(View.VISIBLE);
//	
//											mGridViewAdapters[page].notifyDataSetChanged();
//	
//											// 提示开始下载
//	
//											Toast.makeText(getActivity(),"第"+page+"页,第 "+position + ":开始下载!",Toast.LENGTH_SHORT).show();
//											
//											// 开启线程下载
//	
//											new DownLoadThread(page,position,
//													downLoadUrl,mMainData.get(page).get(position).get(Sf_BusinessConfig.apkName).toString()).start();
//	
//										} else {
//											
//											if(downLoadUrl.equals(""))
//												
//												return;
//											
											if(isAppInstalled(mMainData.get(page).get(position).get(Sf_BusinessConfig.packageName).toString())) {
									
												Intent intent=new Intent(Intent.ACTION_MAIN);
											 	
											 	intent.addCategory(Intent.CATEGORY_LAUNCHER);
											 	
											 	ComponentName cn=new ComponentName(mMainData.get(page).get(position).get(Sf_BusinessConfig.packageName).toString(),mMainData.get(page).get(position).get(Sf_BusinessConfig.className).toString());
											 	
											 	intent.putExtra("terminalId", AppConstantByUtil.terminalId);
											 	
											 	intent.putExtra("userId",AppConstantByUtil.userId);
											 	
											 	intent.putExtra("loginSign",loginSign);
											 	
											 	intent.putExtra("shopName", AppConstantByUtil.shopName);
											 	intent.putExtra("shopId", AppConstantByUtil.shopId);
											 	intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
											 	intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
											 	intent.putExtra("shopContact", AppConstantByUtil.shopContact);
											 	intent.putExtra("payBank", AppConstantByUtil.payBank);
											 	intent.putExtra("payIndex",AppConstantByUtil.payIndex);
											 	
											 	intent.putExtra("payShopNo",AppConstantByUtil.payShopNo);
											 	
											 	intent.putExtra("payTerminalNo", AppConstantByUtil.payTerminalNo);
											 	intent.setComponent(cn);
											 	
											 	startActivity(intent);
											 	IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
												IndexFragment.pause=false;
											}else{
												TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务程序，请联系技术人员！", 1000).show();
											}

											 	
//											} else {
//												
//												 new UIUtil(getActivity()).theClues("检测到还没有安装,请进行安装!");
//												 
//												 File file=new File(commonUtil.getSDCardPath()+mMainData.get(page).get(position).get(Sf_BusinessConfig.apkName).toString());
//												 
//												 if(!file.exists()){
//														View view = (View) mGridViews[page]
//																.getChildAt(position);
//				
//														textViews[page][position] = (TextView) view
//				
//														.findViewById(R.id.textView);
//				
//														progressBars[page][position] = (ProgressBar) view
//				
//														.findViewById(R.id.progressBar);
//				
//														imageViewsBlack[page][position] = (ImageView) view
//				
//														.findViewById(R.id.imageView_black);
//				
//														// 当点击图标时,开始更新,此时new的图标提示要隐藏
//				
//														mMainData.get(page).get(position).put("newIsVisible",
//																View.GONE);
//				
//														// 当点击图标时,黑色的背景显示
//				
//														mMainData.get(page).get(position).put(
//																"blackIsVisible", View.VISIBLE);
//				
//														// 进度条显示
//				
//														progressBars[page][position]
//																.setVisibility(View.VISIBLE);
//				
//														// 半透明黑色背景显示
//				
//														imageViewsBlack[page][position]
//																.setVisibility(View.VISIBLE);
//				
//														mGridViewAdapters[page].notifyDataSetChanged();
//				
//														// 提示开始下载
//				
//														Toast.makeText(getActivity(),"第"+page+"页,第 "+position + ":开始下载!",Toast.LENGTH_SHORT).show();
//				
//														// 开启线程下载
//				
//														new DownLoadThread(page,position,
//																downLoadUrl,mMainData.get(page).get(position).get(Sf_BusinessConfig.apkName).toString()).start();
//												 }else{
//													 openFile(file);
//												 }
//													
//											}
//											
//										}
										
//										 IndexFragment.second=0;
									}
								});
	
						mGridViews[page].setAdapter(mGridViewAdapters[page]);
						initFlag[page]=true;
						pause=true;
						loading.cancel();
						mLayoutCircleGroup.setVisibility(View.VISIBLE);
						mViewPager.setVisibility(View.VISIBLE);
						mLayoutNetWordTip.setVisibility(View.GONE);
						loadReadCount();
						break;
					case 6:
						loading.dismiss();
						IndexFragment.this.getActivity().finish();
						IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
//						Intent intent=new Intent(IndexFragment.this.getActivity(), MyPushService.class);
						
						break;
					case 7:
						loading.dismiss();
						TipsToast.makeText(IndexFragment.this.getActivity(), "退出失败", 1000).show();
						break;
					default:
						break;
				}

			}

		};
		return mViewGroup;

	}
	/*
	 * 获取未读消息数量
	 */
	public void loadReadCount() {
		// TODO Auto-generated method stub
		HashMap<String, Object>mHashMap=new HashMap<String, Object>();
		mHashMap.put("terminalId", AppConstantByUtil.terminalId);
		mHashMap.put("loginSign", loginSign);
		mHashMap.put("userId", AppConstantByUtil.userId);
		mHashMap.put("sign", MD5Util.createSign(loginSign,AppConstantByUtil.userId,AppConstantByUtil.terminalId));
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"shop/SendMessage/queryReadMessageByTerminal.do", "POST", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				// TODO Auto-generated method stub
				toPushHandler(6, "");
			}
			
			@Override
			public void onComplete(String values) {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject=new JSONObject(values);
					if (jsonObject.getString("code").equals("1")) {
						msgCount=Integer.valueOf(jsonObject.getString("count"));
						toPushHandler(7, "");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					toPushHandler(6, "");
				}
			}
		});
	}
	private boolean gprsIsOpenMethod(String methodName) 
    { 
        Class cmClass       = conMgr.getClass(); 
        Class[] argClasses  = null; 
        Object[] argObject  = null; 
         
        Boolean isOpen = false; 
        try 
        { 
            Method method = cmClass.getMethod(methodName, argClasses); 
 
            isOpen = (Boolean) method.invoke(conMgr, argObject); 
        } catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
 
        return isOpen; 
    } 
	
	
	private void toggleMobileData(Context context, boolean enabled) {
		  Class<?> conMgrClass = null;
		  Field iConMgrField = null; 
		  Object iConMgr = null; 
		  Class<?> iConMgrClass = null;
		  Method setMobileDataEnabledMethod = null; 

		  try {
		   conMgrClass = Class.forName(conMgr.getClass().getName());
		   iConMgrField = conMgrClass.getDeclaredField("mService");
		   iConMgrField.setAccessible(true);
		   iConMgr = iConMgrField.get(conMgr);
		   iConMgrClass = Class.forName(iConMgr.getClass().getName());
		   setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		   setMobileDataEnabledMethod.setAccessible(true);
		   setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		   mUpDataLog.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				sendMessageToHandler(12,"3G操作成功");
			}
		}, 2000);
		  } catch (Exception e) {
		   e.printStackTrace();
		   sendMessageToHandler(13,"3G操作失败！");
		  }
		  
	}
	

	//检测APK是否安装
	public boolean isAppInstalled(String uri) {
		
		PackageManager pm = getActivity().getPackageManager();
		
		boolean installed = false;
		
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		
		return installed;
	}
	
		
	/**
	 * 打开APK程序代码
	 * @param file
	 */
	public void openFile(File file) {
	     Log.e("OpenFile", file.getName());
	     Intent intent = new Intent();
	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     intent.setAction(android.content.Intent.ACTION_VIEW);
	     intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
	     startActivity(intent);
	}
	
	
	/**
	 * 初始化参数
	 * @param i1
	 * @param i2
	 */
	public void initParameter(int i1,int i2){
		
		progressBars = new ProgressBar[i1][i2];

		imageViewsBlack = new ImageView[i1][i2];

		textViews = new TextView[i1][i2];

		fileSize = new int[i1][i2];// 文件大小

		downLoadFileSize = new int[i1][i2];// 已下载多少

	}
	/**
	 * 初始View
	 * @param size
	 * @param mViewGroup
	 */
	public void initView(int size,View mViewGroup){
		
		lf = LayoutInflater.from(getActivity());
		
		vSettingImg = (ImageView) mViewGroup.findViewById(R.id.setting_btn);
		vPosImg = (ImageView) mViewGroup.findViewById(R.id.pos_btn);
		vOrderImg = (ImageView) mViewGroup.findViewById(R.id.order_btn);
		vMessageImg = (ImageView) mViewGroup.findViewById(R.id.message_btn);
		vMemberImg = (ImageView) mViewGroup.findViewById(R.id.member_btn);
		vSystemImg = (ImageView) mViewGroup.findViewById(R.id.system_btn);
		
//		settingNor = BitmapFactory.decodeResource(this.getResources(), R.drawable.setting_btn_nol);
//		settingPre = BitmapFactory.decodeResource(this.getResources(), R.drawable.setting_btn_pre);
//		posNor = BitmapFactory.decodeResource(this.getResources(), R.drawable.pos_btn_nol);
//		posPre = BitmapFactory.decodeResource(this.getResources(), R.drawable.pos_btn_pre);
//		orderNor = BitmapFactory.decodeResource(this.getResources(), R.drawable.order_btn_nol);
//		orderPre = BitmapFactory.decodeResource(this.getResources(), R.drawable.order_btn_pre);
//		messageNor = BitmapFactory.decodeResource(this.getResources(), R.drawable.message_btn_nol);
//		messagePre = BitmapFactory.decodeResource(this.getResources(), R.drawable.message_btn_pre);
//		memberNor = BitmapFactory.decodeResource(this.getResources(), R.drawable.member_btn_nol);
//		memberPre = BitmapFactory.decodeResource(this.getResources(), R.drawable.member_btn_pre);
//		systemNor = BitmapFactory.decodeResource(this.getResources(), R.drawable.system_btn_nol);
//		systemPre = BitmapFactory.decodeResource(this.getResources(), R.drawable.system_btn_pre);
		
		vSettingImg.setImageBitmap(settingNor);
		vPosImg.setImageBitmap(posNor);
		vOrderImg.setImageBitmap(orderNor);
		vMessageImg.setImageBitmap(messageNor);
		vMemberImg.setImageBitmap(memberNor);
		vSystemImg.setImageBitmap(systemNor);
		vCustomer=(TextView)mViewGroup.findViewById(R.id.customer);
		vCustomer.setText("操作员:"+customer);
		vMessageTv=(TextView)mViewGroup.findViewById(R.id.message_hint);
		back_btn=(Button)mViewGroup.findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (isPrint) {
					TipsToast.makeText(IndexFragment.this.getActivity(), "请打印完毕再操作", 1000).show();
				}else {
					try {
						Intent intent = new Intent();    
				        intent.setAction("com.duiduifu.retail.sync.close");//发出自定义广播
				        IndexFragment.this.getActivity().sendBroadcast(intent);
//				        IndexFragment.this.getActivity().unbindService(comm);
					} catch (Exception e) {
						e.printStackTrace();
					}
					exitLogin();
				}
				
			}
		});
		
		vOrderHintLayer = (RelativeLayout) mViewGroup.findViewById(R.id.order_layout);
		vOrderHintLayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isAppInstalled("com.erp.order")){
					Intent intent=new Intent();
					
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
				 	
				 	intent.putExtra("userId",AppConstantByUtil.userId);
				 	
				 	intent.putExtra("loginSign",loginSign);
				 	
				 	intent.putExtra("shopName", AppConstantByUtil.shopName);
				 	intent.putExtra("shopId", AppConstantByUtil.shopId);
				 	intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
				 	intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
				 	intent.putExtra("shopContact", AppConstantByUtil.shopContact);
										
					ComponentName componetName = new ComponentName(
			                "com.erp.order",
			                "com.erp.order.activity.MainActivity"); 
					
			        intent.setAction("android.intent.action.VIEW");  
			        
			        intent.setComponent(componetName);

			        startActivity(intent);
			        IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
				}
				
			}
		});
		vOrderHintLayer.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	            	vOrderImg.setImageBitmap(orderPre);
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	                vOrderImg.setImageBitmap(orderNor);
	            } 
				return false;
			}
		});
		vFeekback=(ImageView)mViewGroup.findViewById(R.id.feekback);
		vFeekback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isAppInstalled("com.duiduifu.feekback")){
					Intent intent=new Intent();
					
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
					
					intent.putExtra("userId",AppConstantByUtil.userId);
					
					intent.putExtra("loginSign",loginSign);
					
					intent.putExtra("shopName", AppConstantByUtil.shopName);
					intent.putExtra("shopId", AppConstantByUtil.shopId);
					intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
					intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
					intent.putExtra("shopContact", AppConstantByUtil.shopContact);
					
					ComponentName componetName = new ComponentName(
							"com.duiduifu.feekback",
							"com.duiduifu.feekback.MainActivity"); 
					
					intent.setAction("android.intent.action.VIEW");  
					
					intent.setComponent(componetName);
					
					startActivity(intent);	
					IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
				}
				
			}
		});
		
		vMessageHintLayer = (RelativeLayout) mViewGroup.findViewById(R.id.message_layout);
		vMessageHintLayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isAppInstalled("com.duiduifu.message.activity")) {
					Intent intent = new Intent();
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
					intent.putExtra("userId", AppConstantByUtil.userId);
					intent.putExtra("loginSign", loginSign);
					intent.putExtra("shopId", AppConstantByUtil.shopId);
				 	intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
				 	intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
					intent.putExtra("shopName", AppConstantByUtil.shopName);
					intent.putExtra("shopContact",
							AppConstantByUtil.shopContact);
					ComponentName componetName = new ComponentName(
							"com.duiduifu.message.activity",
							"com.duiduifu.message.activity.MainActivity");
					intent.setAction("android.intent.action.VIEW");
					intent.setComponent(componetName);
					startActivityForResult(intent, 0);
					IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
				}
			}
		});
		
		vMessageHintLayer.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	            	vMessageImg.setImageBitmap(messagePre);
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	            	vMessageImg.setImageBitmap(messageNor);
	            } 
				return false;
			}
		});

		system_hint_layer=(RelativeLayout)mViewGroup.findViewById(R.id.system_layout);
		system_hint_layer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (isAppInstalled("com.duiduifu.app.System")) {
					Intent intent = new Intent();
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
					intent.putExtra("userId", AppConstantByUtil.userId);
					intent.putExtra("loginSign", loginSign);
					intent.putExtra("shopId", AppConstantByUtil.shopId);
				 	intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
				 	intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
					intent.putExtra("shopName", AppConstantByUtil.shopName);
					intent.putExtra("shopContact",
							AppConstantByUtil.shopContact);
					ComponentName componetName = new ComponentName(
							"com.duiduifu.app.System",
							"com.duiduifu.app.System.CountMenuActivity");
					intent.setAction("android.intent.action.VIEW");
					intent.setComponent(componetName);
					startActivityForResult(intent, 0);
					IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
				}
			}
		});
		
		system_hint_layer.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_DOWN){  
		            	vSystemImg.setImageBitmap(systemPre);
		            }else if(event.getAction()==MotionEvent.ACTION_UP){  
		            	vSystemImg.setImageBitmap(systemNor);
		            } 
					return false;
				}
			});
		
		vUserHintLayer=(RelativeLayout)mViewGroup.findViewById(R.id.member_layout);
		vUserHintLayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (isAppInstalled("com.duiduifu.app.member")) {
					Intent intent = new Intent();
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
					intent.putExtra("userId", AppConstantByUtil.userId);
					intent.putExtra("loginSign", loginSign);
					intent.putExtra("shopName", AppConstantByUtil.shopName);
					intent.putExtra("shopContact",
							AppConstantByUtil.shopContact);
					intent.putExtra("shopId", AppConstantByUtil.shopId);
					ComponentName componetName = new ComponentName(
							"com.duiduifu.app.member",
							"com.duiduifu.app.member.activity.MainActivity");
					intent.setAction("android.intent.action.VIEW");
					intent.setComponent(componetName);
					startActivityForResult(intent, 0);
					IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
				}
			}
		});
		
		vUserHintLayer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	            	vMemberImg.setImageBitmap(memberPre);
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	            	vMemberImg.setImageBitmap(memberNor);
	            } 
				return false;
			}
		});
		
		vPosHintLayer=(RelativeLayout)mViewGroup.findViewById(R.id.pos_layout);
		vPosHintLayer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isAppInstalled("com.duiduifu.pos.activity")) {
					Intent intent = new Intent();
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
					intent.putExtra("userId", AppConstantByUtil.userId);
					intent.putExtra("loginSign", loginSign);
					intent.putExtra("shopName", AppConstantByUtil.shopName);
					intent.putExtra("shopContact",
							AppConstantByUtil.shopContact);
					intent.putExtra("shopId", AppConstantByUtil.shopId);
					ComponentName componetName = new ComponentName(
							"com.duiduifu.pos.activity",
							"com.duiduifu.pos.activity.MainActivity");
					intent.setAction("android.intent.action.VIEW");
					intent.setComponent(componetName);
					startActivityForResult(intent, 0);
					IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
				}
			}
		});
		
		vPosHintLayer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){  
	            	vPosImg.setImageBitmap(posPre);
	            }else if(event.getAction()==MotionEvent.ACTION_UP){  
	            	vPosImg.setImageBitmap(posNor);
	            } 
				return false;
			}
		});
		
		mViewPager = (ViewPager) mViewGroup.findViewById(R.id.viewpager);
		
		mTextViewExceptionTip=(TextView) mViewGroup.findViewById(R.id.tip_tv);

		mReloadButton= (Button) mViewGroup.findViewById(R.id.reload_btn);
	
		mReloadButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						mMainData.get(lastCirclePosition).clear();
						
						loadData(lastCirclePosition);
					}
					
		});

		//加载框
		mProgressDialog=(RelativeLayout) mViewGroup.findViewById(R.id.layout_progress_dialog);
		
		mProgressDialog.getBackground().setAlpha(125);	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0&&resultCode==1) {
			loadReadCount();
		}
	}





	/*
	 * 退出登录
	 */
	protected void exitLogin() {
		// TODO Auto-generated method stub
		loading();
		HashMap<String, Object>mHashMap=new HashMap<String, Object>();
		mHashMap.put("terminalId", AppConstantByUtil.terminalId);
		mHashMap.put("userId", AppConstantByUtil.userId);
		mHashMap.put("loginSign", loginSign);
		mHashMap.put("deviceId", AppConstantByUtil.deviceId);
		mHashMap.put("sign", MD5Util.createSign(loginSign,AppConstantByUtil.terminalId,AppConstantByUtil.userId));
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"core/ShopLogin/logout.do", "POST", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				// TODO Auto-generated method stub
				sendMsg(1, 1, 7);
			}
			
			@Override
			public void onComplete(String values) {
				// TODO Auto-generated method stub""
				try {
					JSONObject jsonObject=new JSONObject(values);
					if (jsonObject.getString("code").equals("1")) {
						sendMsg(1, 1, 6);
					}else {
						sendMsg(1, 1, 7);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sendMsg(1, 1, 7);
				}
				
			}
		});
		
		
	}





	public void loading() {
		
		loading.show();
	}
	
	//============  通过请求数据设定Pager数  ===============
	
	/**
	 * 初始化ViewPager
	 * @param size  总共的页数
	 * @param mViewGroup  整体环境
	 */
	public void initPagerView(int size,View mViewGroup){
		
		ArrayList<HashMap<String, Object>> data;
		mGridViews = new GridView[size];
		mGridViewAdapters = new GridViewAdapter[size]; 
		initFlag =  new boolean[size];
		ArrayList<View> aViews = new ArrayList<View>();
		

		for(int i=0;i<size;i++){
			
			View v1 = lf.inflate(R.layout.activity_main_gridview, null);
			
			aViews.add(v1);
			
			mGridViews[i] = (GridView) aViews.get(i).findViewById(R.id.gridview);

			data=new ArrayList<HashMap<String,Object>>();
			
			mMainData.add(data);
		}
		
	
		//底部圆点UI
		mLayoutCircleGroup=(LinearLayout) mViewGroup.findViewById(R.id.group);  
         
		mLayoutNetWordTip=(LinearLayout) mViewGroup.findViewById(R.id.layout_network_tip);	//TODO 18:00
		
		mCircleGroup=new ImageView[aViews.size()];  
		
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin=7;
		layoutParams.width=10;
		layoutParams.height=11;
	    for(int i=0;i<mCircleGroup.length;i++)  
	      {  
	        	mCircleGroup[i]=new ImageView(getActivity());  
	        	mCircleGroup[i].setLayoutParams(layoutParams);  
		            if(i==0)  
		            {  
		            	mCircleGroup[i].setBackgroundResource(R.drawable.page_indicator_focused);  
		            }  
		            else  
		            {  
		            	mCircleGroup[i].setBackgroundResource(R.drawable.page_indicator);  
		            }  
	            mLayoutCircleGroup.addView(mCircleGroup[i]);  
	     }  
	    
	    mViewPagerAdapter=new ViewPagerAdapter(aViews);

		mViewPager.setAdapter(mViewPagerAdapter);

		mViewPager.setOnPageChangeListener(this);
	}
	
	/**
	 * 获取所有的应用需要占用到的页数和数据
	 */
	public void loadPager(){
		
		loading();
		
		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
		mHashMap.put("terminalId", AppConstantByUtil.terminalId);
		mHashMap.put("loginSign", AppConstantByUtil.loginSign);
		mHashMap.put("userId", AppConstantByUtil.userId);
		mHashMap.put("sign", MD5Util.createSign(loginSign,AppConstantByUtil.terminalId,userId));
		
		
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"core/Apk/businessCount.do", "GET", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				sendPagerMessageToHandler(AppConstantByUtil.ERROR,"1");
			}
			
			@Override
			public void onComplete(String values) {
				System.out.println("-----------"+values);
				try {
					
				JSONObject jsonObject=new JSONObject(values);
				
				String msg= jsonObject.getString("message");
				String code=jsonObject.getString("code");
				
					if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
						
						String count = jsonObject.getString("count");
												
						sendPagerMessageToHandler(AppConstantByUtil.SUCCESS,count);
																		
					}else{
						
						sendPagerMessageToHandler(AppConstantByUtil.ERROR,"1");
					}
				
				} catch (Exception e) {
					sendPagerMessageToHandler(AppConstantByUtil.ERROR,"1");

				}
				
			}
		});
	}
	
	public void setPager(int size,int position){
		//来自原来的onCreateView();
		initParameter(size, position);
		initPagerView(size,mViewGroup);	
		loadData(0);
		setTimerTask();
	}
	
	
	public void sendPagerMessageToHandler(int what,String msg) {

		Message message = mPagerNumberLog.obtainMessage();
		message.what = what;
		message.obj=msg;
		mPagerNumberLog.sendMessage(message);
	}
	
	Handler mPagerNumberLog=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
				
				switch (msg.what) {
				
					case AppConstantByUtil.ERROR:
						Integer errorSize = Integer.valueOf(msg.obj.toString());
						setPager(errorSize,POSITION);
						break;
						
					case AppConstantByUtil.SUCCESS:
						Integer sucSize = Integer.valueOf(msg.obj.toString());
						if(sucSize<POSITION){
							setPager(1,POSITION);
						}else{
							if(sucSize%POSITION == 0){
								setPager(sucSize/POSITION,POSITION);
							}else{
								setPager((sucSize/POSITION)+1,POSITION);
							}
						}
						break;
						
					default:
						break;
				}
				
				loading.cancel();
				
			}
		
	};
	
	
	//============  通过请求数据设定Pager数  END  ===============

	/**
	 * 加载数据，获取某一页的数据
	 * @param page 页数
	 */
	public void loadData(final int page){
		
		//FIXME 为什么不要这个呢？
//		loading();
				
		String sign=MD5Util.createSign(AppConstantByUtil.terminalId,AppConstantByUtil.userId, AppConstantByUtil.loginSign);
		
		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
		
		mHashMap.put("terminalId", AppConstantByUtil.terminalId);
		
		mHashMap.put("userId", AppConstantByUtil.userId);
		
		mHashMap.put("loginSign",AppConstantByUtil.loginSign);
		mHashMap.put("currentPage", page+1);
		mHashMap.put("sign",sign);
		
		asyncRunner
		.request(
				AppConstantByUtil.HOST+"core/Apk/apkUpdate.do",
				"POST", mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onException(Object exceptionInfo)

					{
				
						exceptionTip=AppConstantByUtil.app_network_exception_tip;

						sendMsg(page, 0,AppConstantByUtil.LOAD_EXCEPTION);

					}

					@Override
					public void onComplete(String values) {
						System.out.println("------------------"+page+values);
						try {

							JSONObject jsonObject = new JSONObject(
									values);

							JSONArray jsonArray = new JSONArray(
									jsonObject.getString("apks"));

//							totalPage=1;
							String businessId,businessName, apkId, version, apkUrl,apkName, packageName,className,iconUrl, iconVersion,updateTime, memo;

							HashMap<String, Object> mBusinessHashMap;

							for (int i = 0; i < jsonArray.length(); i++) {

								businessId = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.businessId);

								businessName= jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.businessName);
								
								apkId = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.apkId);

								apkUrl = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.apkUrl);
								
								apkName = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.apkName);
								
								packageName= jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.packageName);
								
								className = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.className);
								
								iconUrl = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.iconUrl);

								iconVersion = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.iconVersion);
															
								
								updateTime = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.updateTime);

								version = jsonArray.getJSONObject(i)
										.getString(Sf_BusinessConfig.version);

								
								
								if(!ValidateUtil.isEmpty(businessId) && !ValidateUtil.isEmpty(businessName) && !ValidateUtil.isEmpty(apkUrl) && !ValidateUtil.isEmpty(iconUrl) && !ValidateUtil.isEmpty(packageName) && !ValidateUtil.isEmpty(className) && !ValidateUtil.isEmpty(iconVersion) && !ValidateUtil.isEmpty(version)){
									
									
									HashMap<String, Object> mHashMap = new HashMap<String, Object>();

									mHashMap.put(Sf_BusinessConfig.businessId, businessId);
									
									mHashMap.put(Sf_BusinessConfig.businessName, businessName);


									mHashMap.put(Sf_BusinessConfig.apkId, apkId);

									mHashMap.put(Sf_BusinessConfig.apkUrl, apkUrl);
									
									mHashMap.put(Sf_BusinessConfig.apkName, apkName);
									
									mHashMap.put(Sf_BusinessConfig.packageName, packageName);
									
									mHashMap.put(Sf_BusinessConfig.className, className);

									mHashMap.put(Sf_BusinessConfig.iconUrl, iconUrl);

									mHashMap.put(Sf_BusinessConfig.iconVersion, iconVersion);
									
									mHashMap.put(Sf_BusinessConfig.updateTime, updateTime);

									mHashMap.put(Sf_BusinessConfig.version, version);
									
									mHashMap.put("newText", "new");

									mHashMap.put("newIsVisible",
											View.GONE);

									mHashMap.put("blackIsVisible",
											View.GONE);

									
//									mBusinessHashMap = dbUtil
//											.SelectBusinessIsExist(businessId);
//									// 判断业务是否存在
//									if (mBusinessHashMap!=null && (Boolean) mBusinessHashMap.get("resultCode")!=null && (Boolean) mBusinessHashMap.get("resultCode")) {
//										// 判断版本是否有更新
//										if (!version
//												.equals(mBusinessHashMap
//														.get(Sf_BusinessConfig.version)) || !isAppInstalled(packageName)) {
//
//											mHashMap.put("newIsVisible",
//													View.VISIBLE);
//
//											mHashMap.put("isUpdateFlag",
//													true);
//												
//											// 如果版本不一样的话,需要更新本地数据库(这一步应该在下载完成并且成功安装的时候调用)
//											/*
//											 * dbUtil.UpdateBusinessConfig(
//											 * Integer
//											 * .valueOf(mBusinessHashMap.get
//											 * 
//											 * (Sf_BusinessConfig.
//											 * businessConfigId)
//											 * .toString()), apkId, apkUrl,
//											 * 
//											 * iconUrl, version, updateTime,
//											 * memo);
//											 */
//
//										} else {
//											// 没有版本更新
//
//											mHashMap.put("newIsVisible",
//													View.GONE);
//
//											mHashMap.put("isUpdateFlag",
//													false);
//
//										}
//
//									} else {
//
//										if(businessId!=null && !businessId.equals("")){
//											
//
//											mHashMap.put("newIsVisible",
//													View.VISIBLE);
//
//											mHashMap.put("isUpdateFlag", true);
//
//											// 插入新的业务
//											version="";//插入新业务的时候,版本需要设置是空的,因为这个时候APK还没有进行安装，安装成功再更新version
//											
//											try {
//												dbUtil.InsertBusinessConfig(
//														businessId, businessName,apkId, apkUrl,apkName,packageName,className,
//														iconUrl, version,
//														updateTime);
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//
//										}else{
//											
//											mHashMap.put("newIsVisible",
//													View.GONE);
//
//											mHashMap.put("isUpdateFlag",
//													false);
//											
//										}
//										
//									
//									}
								 
									mMainData.get(page).add(mHashMap);
									
									if(dataUpMap.containsKey(packageName)){
										dataUpMap.remove(packageName);
									}
									String value = page+","+(mMainData.get(page).size()-1);
									dataUpMap.put(packageName, value);
									
								}
								

							}

							
							if(mMainData.get(page).size()<=0){
								
								exceptionTip=getString(R.string.app_null_exception_tip);

								sendMsg(page, 0,AppConstantByUtil.LOAD_EXCEPTION);
								
							}else{
								
								sendMsg(page, 0,AppConstantByUtil.LOAD_BUSINESS_CONFIG);

								
							}

						} catch (Exception e) {

							e.printStackTrace();
							
							exceptionTip=AppConstantByUtil.app_json_exception_tip;

							sendMsg(page, 0,AppConstantByUtil.LOAD_EXCEPTION);

						}

					}

				});

		
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		
		if(!initFlag[arg0]){
			loadData(arg0);

		}
        mCircleGroup[arg0].setBackgroundResource(R.drawable.page_indicator_focused);  
       
        mCircleGroup[lastCirclePosition].setBackgroundResource(R.drawable.page_indicator);  
        
        lastCirclePosition=arg0;
        
        IndexFragment.second=0;
        
	}
	
	public void setHint(int orderHint,int messageHint){
		Message msg = new Message();
		msg.arg1 = orderHint;
		msg.arg2 = messageHint;
		msg.what = 1;
		hintHandle.sendMessage(msg);	
		
	}
	
//	public void setMessageHint(){
//		int messageHint = dbUtil.SelectMessageCountForFlag(Sf_Message.NEW);
//		Message msg = new Message();
//		msg.arg1 = messageHint;
//		msg.what = 2;
//		hintHandle.sendMessage(msg);
//	}
	
	/**
	 * 页面推送提示的数量
	 */
	Handler hintHandle = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 1){
				if(vOrderHint!=null){
					if(msg.arg1==0){
						vOrderHint.setVisibility(View.GONE);
					}else{
						vOrderHint.setVisibility(View.VISIBLE);
						vOrderHint.setText(msg.arg1+"");
					}
				}
				
				if(vMessageHint!=null){
					if(msg.arg2 == 0){
						vMessageHint.setVisibility(View.GONE);
					}else{
						vMessageHint.setVisibility(View.VISIBLE);
						vMessageHint.setText(msg.arg2+"");
					}
				}
			}else if(msg.what == 2){
				if(vMessageHint!=null){
					if(msg.arg1 == 0){
						vMessageHint.setVisibility(View.GONE);
					}else{
						vMessageHint.setVisibility(View.VISIBLE);
						vMessageHint.setText(msg.arg1+"");
					}
				}
			}
			
		}
	};
	

	/**
	 * 关闭下载后设置相关参数
	 * 
	 * @param position
	 */
	public void setUpdateFlag(int page,int position) {

		progressBars[page][position].setProgress(0);

		progressBars[page][position].setVisibility(View.GONE);

		imageViewsBlack[page][position].setVisibility(View.GONE);

		mMainData.get(page).get(position).put("newIsVisible", View.GONE);

		mMainData.get(page).get(position).put("blackIsVisible", View.GONE);

		mGridViewAdapters[page].notifyDataSetChanged();
		
		

	}

	/**
	 * 下载失败后设置相关参数
	 * 
	 * @param position
	 */
	public void setUpdateFlagForError(int page,int position) {

		progressBars[page][position].setProgress(0);

		progressBars[page][position].setVisibility(View.GONE);

		imageViewsBlack[page][position].setVisibility(View.GONE);

		mMainData.get(page).get(position).put("isUpdateFlag", true);

		mMainData.get(page).get(position).put("newIsVisible", View.VISIBLE);

		mMainData.get(page).get(position).put("newText", "restart");

		mMainData.get(page).get(position).put("blackIsVisible", View.GONE);

		mGridViewAdapters[page].notifyDataSetChanged();

	}

	/**
	 * 下载线程
	 * 
	 * @author Administrator
	 * 
	 */
	class DownLoadThread extends Thread {

		private String downLoadUrl;
		private int position;
		private int page;
		private String fileName;

		public DownLoadThread(int page,int position, String downLoadUrl,String fileName) {
			this.page = page;
			this.position = position;
			this.downLoadUrl = downLoadUrl;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			Looper.prepare();

			// 下载开始
			downFile(page,position, downLoadUrl, commonUtil.getSDCardPath(),fileName);

			Looper.loop();

			super.run();
		}

	}

	/**
	 * 下载文件
	 * 
	 * @param position
	 * @param url
	 * @param path
	 * @param fileName
	 */
	public void downFile(int page,int position, String url, String path, String fileName) {

		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();

		// 设置 Http 连接超时为5秒
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);

		// 创建get方法的实例
		GetMethod getMethod = new GetMethod(url);

		// 处理中文乱码
		getMethod.getParams().setParameter(
				HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");

		// 设置请求超时为60 秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);

		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		try {

			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);

			// statusCode=200,请求成功
			if (statusCode == HttpStatus.SC_OK) {

				// 读取输入流
				InputStream is = getMethod.getResponseBodyAsStream();

				// 根据响应获取文件大小
				this.fileSize[page][position] = (int) getMethod
						.getResponseContentLength();

				// 判断输入流是否为空
				if (this.fileSize[page][position] <= 0 || is == null) {

					sendMsg(page,position, AppConstantByUtil.ERROR);
					return;
				}

				FileOutputStream fos = new FileOutputStream(path + fileName);
				
				// 把数据存入路径+文件名
				byte buf[] = new byte[1024];

				// 设置已下载的大小
				downLoadFileSize[page][position] = 0;

				// 发送总大小给进度条
				sendMsg(page,position, AppConstantByUtil.MAX);

				// 默认为下载完成
				int code = AppConstantByUtil.SUCCESS;
				// 循环读取文件
				do {

					int numread = is.read(buf);

					// 读取失败
					if (numread == -1) {
						break;
					}

					// 判断是否关闭了下载
					/*
					 * if (cancelDown[position]) {
					 * 
					 * code = AppConstant.STOP;
					 * 
					 * break; }
					 */

					fos.write(buf, 0, numread);
					
					downLoadFileSize[page][position] += numread;

					// 更新进度条
					sendMsg(page,position, AppConstantByUtil.CONTINUE);

				} while (true);

				// 反馈下载的结果
				sendMsg(page,position, code);

				// 关闭流
				is.close();

				fos.close();
				
			} else {

				sendMsg(page,position, AppConstantByUtil.ERROR);

			}

		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			// log.info("Please check your provided http address!");
			e.printStackTrace();
			sendMsg(page,position, AppConstantByUtil.ERROR);

		} catch (IOException e) {

			// 发生网络异常
			e.printStackTrace();
			sendMsg(page,position, AppConstantByUtil.ERROR);

		} finally {

			// 释放连接
			getMethod.releaseConnection();
		}
	}
	
	public static boolean pause=false;
	
	public static boolean advEmpty = true;
	
	public static int second;
	
	//待机计时器 
	private void setTimerTask(){ 
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(pause && !advEmpty)
				
				{
					second=second+1;
					if(second>AppConstantByUtil.STANDBY_TIME){
						
						
						second=0;
						
						pause=false;
						
						onIndexFragmentListener.showAdFragment();

					}
					
						
				}
				
				
			}
		},0, 1000);
	 
    }
	 
	/**
	 * 发送信息
	 * @param page
	 * @param position
	 * @param flag
	 */
	private void sendMsg(int page,int position, int flag) {
		Message msg = new Message();
		msg.arg1 = page;
		msg.arg2 = position;
		msg.what = flag;
		mHandler.sendMessage(msg);
	}
	
	
	
	//======= 程序更新完成监听=======
	
	public void sendMessageToHandler(int what,String msg) {

		Message message = mUpDataLog.obtainMessage();
		message.what = what;
		message.obj=msg;
		mUpDataLog.sendMessage(message);
	}
	Handler mUpDataLog=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			
			switch (msg.what) {
			
				case 0:
					TipsToast.makeText(IndexFragment.this.getActivity(), msg.obj.toString(), 1000).show();
					break;
					
				case 1:
					TipsToast.makeText(IndexFragment.this.getActivity(), msg.obj.toString(), 1000).show();
					break;
				case 3:		//FIXME 这个分支方法已经没有调用用了，暂时保留
					onIndexFragmentListener.showPayResultActivity(null, null, null, null,isUnBundler);
					break;
					
				case 12:
					loading.cancel();
					if(gprsIsOpenMethod("getMobileDataEnabled")){
						currentGString = "关闭3G";
						isOpenG = true;
						TipsToast.makeText(IndexFragment.this.getActivity(), "3G已经打开", 1000).show();
					}else{
						currentGString = "打开3G";
						isOpenG = false;
						TipsToast.makeText(IndexFragment.this.getActivity(), "3G已经关闭", 1000).show();
					}
					break;
				case 13:
					loading.cancel();
					TipsToast.makeText(IndexFragment.this.getActivity(), msg.obj.toString(), 1000).show();
					break;
				default:
					break;
			}
			
			loading.cancel();
			
		}

		
		
		
	};
	
	private void onInitAPK(int page, int position) {
//		try {
//			dbUtil.InsertBusinessConfig(mMainData.get(page).get(position).get(Sf_BusinessConfig.businessId).toString(),
//					mMainData.get(page).get(position).get(Sf_BusinessConfig.businessName).toString(),
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.apkId).toString(), 
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.apkUrl).toString(),
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.apkName).toString(),
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.packageName).toString(),
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.className).toString(),
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.iconUrl).toString(), 
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.version).toString(),  
//				 mMainData.get(page).get(position).get(Sf_BusinessConfig.updateTime).toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		mMainData.get(page).get(position).put("isUpdateFlag", false);
//		mGridViewAdapters[page].notifyDataSetChanged();
//		
//		onRequestUpApkData(mMainData.get(page).get(position).get(Sf_BusinessConfig.apkId).toString(), mMainData.get(page).get(position).get(Sf_BusinessConfig.version).toString());
	}
	

	private void onUpDataAPK(int page, int position) {
		//更新本地数据库  
//		try {
//			dbUtil.UpdateBusinessConfig(mMainData.get(page).get(position).get(Sf_BusinessConfig.businessId).toString(),
//						mMainData.get(page).get(position).get(Sf_BusinessConfig.businessName).toString(),
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.apkId).toString(), 
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.apkUrl).toString(),
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.apkName).toString(),
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.packageName).toString(),
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.className).toString(),
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.iconUrl).toString(), 
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.version).toString(), 
//					 mMainData.get(page).get(position).get(Sf_BusinessConfig.updateTime).toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		mMainData.get(page).get(position).put("isUpdateFlag", false);
//		mGridViewAdapters[page].notifyDataSetChanged();
//		onRequestUpApkData(mMainData.get(page).get(position).get(Sf_BusinessConfig.apkId).toString(), mMainData.get(page).get(position).get(Sf_BusinessConfig.version).toString());
	}
	
	
	private void onRequestUpApkData(String apkId,String version) {
		loading();
		SharedPreferences sp=this.getActivity().getSharedPreferences("terminalId", this.getActivity().MODE_APPEND);
		
		String terminalId=sp.getString("terminalId", "");
		
		String userId ="" ;

		String loginSign ="";
		
		String sign = MD5Util.createSign(terminalId,userId,apkId,version,loginSign);
		
		HashMap<String, Object> mHashMap = new HashMap<String, Object>();

		
		mHashMap.put("terminalId", terminalId);

		mHashMap.put("userId", userId);

		mHashMap.put("loginSign", loginSign);
		
		mHashMap.put("apkId", apkId);
		
		mHashMap.put("version", version);

		mHashMap.put("sign", sign);

		AsyncRunner.getInstance().request(
				AppConstantByUtil.HOST + "core/ApkLog/addApkLog.do", "GET",
				mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onException(Object exceptionInfo) {
						sendMessageToHandler(AppConstantByUtil.ERROR,
								AppConstantByUtil.app_network_exception_tip);
					}

					@Override
					public void onComplete(String values) {
						try {

							JSONObject jsonObject = new JSONObject(values);

							String msg = jsonObject.getString("message");

							String code = jsonObject.getString("code");

							if (code.equals(String
									.valueOf(AppConstantByUtil.SUCCESS))) {

								sendMessageToHandler(AppConstantByUtil.SUCCESS,
										msg);

							} else {

								sendMessageToHandler(AppConstantByUtil.ERROR,
										msg);
							}

						} catch (Exception e) {
							e.printStackTrace();
							sendMessageToHandler(AppConstantByUtil.ERROR,
									AppConstantByUtil.app_json_exception_tip);

						}

					}
				});

	}

	
	//==========  监听程序安装是否成功   ============
	public class AppInstallReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        PackageManager manager = context.getPackageManager();
	        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
	            String packageName = intent.getData().getSchemeSpecificPart();  
	            if(dataUpMap.containsKey(packageName)){
	            	String ppData = dataUpMap.get(packageName);
	            	String[] ppd = ppData.split(",");
	            	int page = Integer.valueOf(ppd[0]);
	            	int position = Integer.valueOf(ppd[1]);
	            	onInitAPK(page,position);
	            }
	        }
	        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
	            String packageName = intent.getData().getSchemeSpecificPart();
	        }
	        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
	            String packageName = intent.getData().getSchemeSpecificPart();  
	            if(dataUpMap.containsKey(packageName)){
	            	String ppData = dataUpMap.get(packageName);
	            	String[] ppd = ppData.split(",");
	            	int page = Integer.valueOf(ppd[0]);
	            	int position = Integer.valueOf(ppd[1]);
	            	onUpDataAPK(page,position);
	            }
	        }
	        
	    }


	}
	public class PushReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String message = intent.getStringExtra("message");
			if(sound!=null){
//				sound.play(ring, 1, 1, 0, 0, 1);
			}
			
			toPushHandler(1, message);
		}
		
	}
	
	
	public class PrintOrderReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(sound!=null){
				sound.play(ring, 1, 1, 0, 0, 1);
			}
			String OrderId=intent.getStringExtra("orderId");
			msgCount=Integer.valueOf(intent.getStringExtra("count"));
			if (msgCount>0) {
				vMessageTv.setVisibility(View.VISIBLE);
				if (msgCount>99) {
					vMessageTv.setText("99+");
				}else {
					vMessageTv.setText(String.valueOf(msgCount));
				}
			}else {
				vMessageTv.setVisibility(View.GONE);
			}
			if (intent.getStringExtra("orderId")!=null) {
//				String msg=new String();
//				msg=intent.getStringExtra("orderId");
//				orderCodes.add(msg);
//				Editor editor=orderCodeShare.edit();
//				editor.putString("orderId", orderCodes.toString());
//				editor.commit();
			}
			if (intent.getStringExtra("isPrint").equals("0")) {
//				if (isFirstCheckOrder) {
//					printOrderIdDialog.show();
//					isFirstCheckOrder=false;
//					Intent intent10=new Intent();
//					intent10.putExtra("orderId", OrderId);
//					intent10.setAction("com.duiduifu.orderId");
//					IndexFragment.this.getActivity().sendBroadcast(intent10);
//				}else {
					requestPrint(intent.getStringExtra("orderId"));
//				}
			}else {
					Intent intent10=new Intent();
					intent10.setAction("com.duiduifu.unPrintOrderId");
					IndexFragment.this.getActivity().sendBroadcast(intent10);
					toPushHandler(1, "有新消息");
			}
			
		}
		
	}
	
	
	
	
	/**
	 * 通过订单ID请求订单信息
	 * @param oId
	 */
	public void requestPrint(final String oId){
		commodityList.clear();
		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
		
		mHashMap.put("terminalId", AppConstantByUtil.terminalId);
		
		
		mHashMap.put("userId",userId);
		
		
		mHashMap.put("loginSign",loginSign);
		
		mHashMap.put("orderId",oId);
		
		
		String sign=MD5Util.createSign(loginSign,AppConstantByUtil.terminalId,userId,oId);
		
		
		mHashMap.put("sign",sign);
		
		
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"shop/Order/getOrder.do", "GET", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				toPushHandler(9,AppConstantByUtil.app_network_exception_tip);
			}
			
			@Override
			public void onComplete(String values) {
				System.out.println("------------"+values);
				try {
					
					JSONObject jsonObject=new JSONObject(values);
					
					String msg= jsonObject.getString("message");
					
					String code=jsonObject.getString("code");
					
					
					if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
						
						nums = jsonObject.getString("nums");
						orderDate = jsonObject.getString("orderDate");
						realName = jsonObject.getString("realName");
						shipAddress = jsonObject.getString("shipAddress");
						shipMobile = jsonObject.getString("shipMobile");
						shipName = jsonObject.getString("shipName");
						shipTel = jsonObject.getString("shipTel");
						statusId = jsonObject.getString("statusId");
						statusName = jsonObject.getString("statusName");
						totalAmount = jsonObject.getString("totalAmount");
						userName = jsonObject.getString("userName");
						orderId = jsonObject.getString("orderId");;
						printTwo=jsonObject.getString("printTwo");
						businessId=jsonObject.getString("businessId");
						paymentId=jsonObject.getString("paymentId");
						if (businessId.equals("14")) {
							memo=jsonObject.getString("memo");
						}
						memoMsg=jsonObject.getString("leaveMessage");
						if (businessId.equals("16")) {
							memo=jsonObject.getJSONObject("orderItems").getString("memo");
							attrName=jsonObject.getJSONObject("orderItems").getString("merchandiseName");
							attrContactName=jsonObject.getJSONObject("orderItems").getString("contactName");
							attrPhone=jsonObject.getJSONObject("orderItems").getString("mobile");
						}
						if(jsonObject.has("paymentName")){
							paymentName = jsonObject.getString("paymentName");
						}else{
							paymentName = null;
						}
						if(jsonObject.has("shipTime")){
							shipTime = jsonObject.getString("shipTime");
						}else{
							shipTime = null;
						}
						
						
						JSONArray jsonArray=new JSONArray(jsonObject.getString("items"));
						
						for (int i = 0; i < jsonArray.length(); i++) {
							Commodity o=new Commodity();
							o.setCount(jsonArray.getJSONObject(i).getString("amount"));
							o.setStock(jsonArray.getJSONObject(i).getString("inventory"));
							o.setName(jsonArray.getJSONObject(i).getString("merchandiseName"));
							o.setNumber(jsonArray.getJSONObject(i).getString("nums"));
							o.setPrice(jsonArray.getJSONObject(i).getString("price"));
							o.setUnit(jsonArray.getJSONObject(i).getString("unitName"));
							
							commodityList.add(o);
						}
						if (businessId.equals("16")) {
							toPushHandler(8,msg);
							return;
						}
						if (statusId.equals("1")) {
							toPushHandler(3,msg);
							return;
						}else if (statusId.equals("2")) {
							toPushHandler(3,msg);
							return;
						}else if (statusId.equals("3")) {
							toPushHandler(3,msg);
							return;
						}else {
							return;
						}
						
					}else{
						toPushHandler(4,msg);
						return;
					}
				
				} catch (Exception e) {
					e.printStackTrace();
					toPushHandler(4,AppConstantByUtil.app_json_exception_tip);
					return;
				}
				
			}
		});
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if(msg.obj!=null && !"".equals(msg.obj.toString())){
					
					if (isOrder) {
						boolean isnumber=msg.obj.toString().substring(0, 1).matches("[0-9]+");
						if (isnumber) {
							mNotification.icon=R.drawable.go;
							
							mNotification.tickerText="有新订单";
							
							mNotification.defaults=Notification.DEFAULT_SOUND;
							
							mNotification.setLatestEventInfo(IndexFragment.this.getActivity(), "哈哈", "新订单", null);
							
							Intent intent=new Intent();
							
							intent.putExtra("terminalId", AppConstantByUtil.terminalId);
							
							intent.putExtra("userId",AppConstantByUtil.userId);
							
							intent.putExtra("loginSign",AppConstantByUtil.loginSign);
							
							intent.putExtra("shopName", AppConstantByUtil.shopName);
							
							intent.putExtra("shopId", AppConstantByUtil.shopId);
							
							intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
							
							intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
							
							intent.putExtra("shopContact", AppConstantByUtil.shopContact);
							
							intent.setClassName("com.erp.order", "com.erp.order.activity.MainActivity");
							
							intent.setAction("android.intent.action.VIEW");
							
							PendingIntent pendingIntent=PendingIntent.getActivity(IndexFragment.this.getActivity(), 0, intent, 0);
							
							mNotification.setLatestEventInfo(IndexFragment.this.getActivity(), "", "", pendingIntent);
							
							mNotificationManager.notify(0, mNotification);
						}
					}
				}else {
					mNotification.icon=R.drawable.go;
					
					mNotification.tickerText="有新消息";
					
					mNotification.defaults=Notification.DEFAULT_SOUND;
					
					mNotification.setLatestEventInfo(IndexFragment.this.getActivity(), "哈哈", "新消息", null);
					
					Intent intent=new Intent();
					
					intent.putExtra("terminalId", AppConstantByUtil.terminalId);
					
					intent.putExtra("userId",AppConstantByUtil.userId);
					
					intent.putExtra("loginSign",AppConstantByUtil.loginSign);
					
					intent.putExtra("shopName", AppConstantByUtil.shopName);
					
					intent.putExtra("shopId", AppConstantByUtil.shopId);
					
					intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
					
					intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
					
					intent.putExtra("shopContact", AppConstantByUtil.shopContact);
					
					intent.setClassName("com.duiduifu.message.activity", "com.duiduifu.message.activity.MainActivity");
					
					intent.setAction("android.intent.action.VIEW");
					
					PendingIntent pendingIntent=PendingIntent.getActivity(IndexFragment.this.getActivity(), 0, intent, 0);
					
					mNotification.setLatestEventInfo(IndexFragment.this.getActivity(), "", "", pendingIntent);
					
					mNotificationManager.notify(0, mNotification);
				}
				break;
				
			case 2:
				loading.dismiss();
				requestPrint(msg.obj.toString());
				
				break;
				
			case 3:
				if (!isPrint) {
					isPrint=true;
					loading.dismiss();
					String m = msg.obj.toString();
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (printTwo.equals("1")) {
								if (businessId.equals("14")) {
									if (!memo.equals("")) {
										for (int j = 0; j < 3; j++) {
											list.add(new PrintDataObject(memo, "50", "", "center","","true"));
											list.add(new PrintDataObject(AppConstantByUtil.shopName+"\n", "20", "", "center","","true"));
											list.add(new PrintDataObject("订单号:  "+orderId, "10", "", "left","","true"));
											list.add(new PrintDataObject("下单时间:  "+orderDate, "10", "", "left","","true"));
											list.add(new PrintDataObject("操作员:   "+AppConstantByUtil.shopContact, "10", "", "left","","true"));
											if (paymentId.equals("1")) {
												list.add(new PrintDataObject("状态:  "+"现场支付", "10", "", "left","","true"));
											}else {
												list.add(new PrintDataObject("状态:  "+"已在线付款", "10", "", "left","","true"));
											}
											list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
											list.add(new PrintDataObject("", "", "", "","","true"));
											int num = 0;
											double totalsPrice=0.00;
											if (j==0) {
												list.add(new PrintDataObject("名称                    数量", "10", "", "left", "", "true"));
												for (int i = 0; i < commodityList.size(); i++) {
													list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"                          "+commodityList.get(i).getNumber(), "20", "", "left", "", "true"));
													num+=Integer.parseInt(commodityList.get(i).getNumber());
													totalsPrice+=Integer.parseInt(commodityList.get(i).getNumber())*Double.parseDouble(commodityList.get(i).getPrice());
												}
											}else {
												list.add(new PrintDataObject("名称          数量        小计", "10", "", "left", "", "true"));
												for (int i = 0; i < commodityList.size(); i++) {
													list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"               "+commodityList.get(i).getNumber()+"           "+Double.parseDouble(commodityList.get(i).getPrice()), "10", "", "left", "", "true"));
													num+=Integer.parseInt(commodityList.get(i).getNumber());
													totalsPrice+=Integer.parseInt(commodityList.get(i).getNumber())*Double.parseDouble(commodityList.get(i).getPrice());
												}
											}
											list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
											if (j==0) {
												list.add(new PrintDataObject("备注:"+memoMsg+"\n", "10", "", "left","","true"));
											}else if (j==1) {
												list.add(new PrintDataObject("总计:                  "+totalsPrice, "10", "", "left","","true"));
												list.add(new PrintDataObject("折扣:                  "+"0.00", "10", "", "left","","true"));
												list.add(new PrintDataObject("支付金额:              "+totalsPrice, "10", "", "left","","true"));
												list.add(new PrintDataObject("找赎:                  "+"0.00", "10", "", "left","","true"));
											}else if(j==2){
												list.add(new PrintDataObject("总计:                  "+totalsPrice, "10", "", "left","","true"));
												list.add(new PrintDataObject("折扣:                  "+"0.00", "10", "", "left","","true"));
												list.add(new PrintDataObject("支付金额:              "+totalsPrice, "10", "", "left","","true"));
												list.add(new PrintDataObject("找赎:                  "+"0.00", "10", "", "left","","true"));
											}
											List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
											Bitmap bitmap = null;
											BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.icon);
											Bitmap iconBitmap=bd.getBitmap();
											MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(String.valueOf(R.drawable.icon), iconBitmap);
											list2.add(new PrintDataObject("\n\n"));
											if (j==2) {
												list.add(new PrintDataObject("欢迎再次光临"+AppConstantByUtil.shopName, "10", "", "center","","true"));
												list.add(new PrintDataObject("地址:"+AppConstantByUtil.shopAddress, "10", "", "center","","true"));
												list.add(new PrintDataObject("电话"+AppConstantByUtil.shopPhone, "10", "", "center","","true"));
												list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
												list.add(new PrintDataObject("我家微店在这里", "10", "", "center","","true"));
												list.add(new PrintDataObject("http://www.duiduifu.com", "10", "", "center","","true"));
												String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(AppConstantByUtil.shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(AppConstantByUtil.terminalId.getBytes(), Base64.DEFAULT);
												if (bString!=null) {
													Pattern p = Pattern.compile("\\s*|\t|\r|\n");
													Matcher bm = p.matcher(bString);
													bString = bm.replaceAll("");
												}
												bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
												MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(bString, bitmap);
											}
											String str=null;
											try {
												str=printDev.print(list);
												if (j==2) {
													str=printDev.printBmp(bitmap, true);
													str=printDev.printBmp(iconBitmap, true);
													if (bitmap!=null&&!bitmap.isRecycled()) {
														bitmap.recycle();
														bitmap=null;
													}
													if (iconBitmap!=null&&!iconBitmap.isRecycled()) {
														iconBitmap.recycle();
														iconBitmap=null;
													}
												}
												str=printDev.print(list2);
												list.clear();
												list2.clear();
												if (str.indexOf("G013")==-1) {
													if (str.indexOf("G009")!=-1) {
														isPrint=false;
														Intent intent10=new Intent();
														intent10.setAction("com.duiduifu.noOrderId");
														IndexFragment.this.getActivity().sendBroadcast(intent10);
														list.clear();
														list2.clear();
														System.gc();
														toPushHandler(6,"没有打印纸");
														return;
													}else {
														isPrint=false;
														Intent intent10=new Intent();
														intent10.setAction("com.duiduifu.noOrderId");
														IndexFragment.this.getActivity().sendBroadcast(intent10);
														list.clear();
														list2.clear();
														System.gc();
														toPushHandler(6,"准备失败打印机异常");
														return;
													}
												}
												if (j==2) {
													isPrint=false;
													Intent intent=new Intent();
													intent.putExtra("orderId", orderId);
													intent.setAction("com.duiduifu.orderId");
													IndexFragment.this.getActivity().sendBroadcast(intent);
													list.clear();
													list2.clear();
													System.gc();
												}
											} catch (Exception e) {
												isPrint=false;
													Intent intent10=new Intent();
													intent10.setAction("com.duiduifu.noOrderId");
													IndexFragment.this.getActivity().sendBroadcast(intent10);
													list.clear();
													list2.clear();
													System.gc();
													toPushHandler(6,"准备失败打印机异常");
											}
										}
									}else {
										for (int j = 0; j < 2; j++) {
											
											list.add(new PrintDataObject(AppConstantByUtil.shopName, "50", "", "center","","true"));
											if (j==0) {
												list.add(new PrintDataObject("（商户留存联）", "10", "", "center","","true"));
											}else {
												list.add(new PrintDataObject("（顾客留存联）", "10", "", "center","","true"));
											}
											list.add(new PrintDataObject("单号:  "+orderId, "10", "", "left","","true"));
											list.add(new PrintDataObject("时间:  "+orderDate, "10", "", "left","","true"));
											list.add(new PrintDataObject("操作员:   "+AppConstantByUtil.shopContact, "10", "", "left","","true"));
											if(shipName!=null && !"".equals(shipName)){
												list.add(new PrintDataObject("收货人:   "+shipName, "10", "", "left","","true"));
											}
											if(shipMobile!=null && !"".equals(shipMobile)){
												list.add(new PrintDataObject("收货人手机: "+shipMobile, "10", "", "left","","true"));
											}
											if(shipTel!=null && !"".equals(shipTel)){
												list.add(new PrintDataObject("收货人电话: "+shipTel, "10", "", "left","","true"));
											}
											if(shipAddress!=null && !"".equals(shipAddress)){
												list.add(new PrintDataObject("收货地址:  "+shipAddress, "10", "", "left","","true"));
											}
											if(paymentName!=null && !"".equals(paymentName)){
												list.add(new PrintDataObject("支付方式:  "+paymentName, "10", "", "left","","true"));
											}
											if(shipTime!=null && !"".equals(shipTime)){
												list.add(new PrintDataObject("配送时间:  "+shipTime, "10", "", "left","","true"));
											}
											if(memoMsg!=null && !"".equals(memoMsg)){
												list.add(new PrintDataObject("用户留言:  "+memoMsg, "10", "", "left","","true"));
											}
											list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
											list.add(new PrintDataObject("", "", "", "","","true"));
											list.add(new PrintDataObject("名称       数量       单价", "10", "", "left", "", "true"));
											for (int i = 0; i < commodityList.size(); i++) {
												list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"           "+commodityList.get(i).getNumber()+"         "+commodityList.get(i).getPrice(), "10", "", "left", "", "true"));
											}
											list.add(new PrintDataObject("订单状态:"+statusName, "10", "", "left","","true"));
											list.add(new PrintDataObject("", "", "", "","","true"));
											list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "", "center","","true"));
											list.add(new PrintDataObject("", "", "", "","","true"));
											list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
											list.add(new PrintDataObject("总计:                    "+totalAmount, "10", "", "left","","true"));
											if (paymentName.equals("货到付款")) {
												list.add(new PrintDataObject("已付:                    "+"0", "10", "", "left","","true"));
											}else if (statusName.equals("待付款")) {
												list.add(new PrintDataObject("已付:                    "+"0", "10", "", "left","","true"));
											}else {
												list.add(new PrintDataObject("已付:                    "+totalAmount, "10", "", "left","","true"));
											}
											list.add(new PrintDataObject("", "", "", "","","true"));
											list.add(new PrintDataObject("", "", "", "","","true"));
											if (j==0) {
												list.add(new PrintDataObject("客户签名: _____________________\n\n", "10", "left", "", "true"));
											}
											list.add(new PrintDataObject("欢迎再次光临"+AppConstantByUtil.shopName, "10", "", "center","","true"));
											list.add(new PrintDataObject("地址:"+AppConstantByUtil.shopAddress, "10", "", "center","","true"));
											list.add(new PrintDataObject("电话"+AppConstantByUtil.shopPhone, "10", "", "center","","true"));
											list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
											if (j==1) {
												list.add(new PrintDataObject("我家微店在这里", "10", "", "center","","true"));
												list.add(new PrintDataObject("http://www.duiduifu.com", "10", "", "center","","true"));
											}
											List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
											list2.add(new PrintDataObject("\n\n"));
											String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(AppConstantByUtil.shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(AppConstantByUtil.terminalId.getBytes(), Base64.DEFAULT);
											if (bString!=null) {
												Pattern p = Pattern.compile("\\s*|\t|\r|\n");
												Matcher bm = p.matcher(bString);
												bString = bm.replaceAll("");
											}
											Bitmap bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
											MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(bString, bitmap);
											BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.icon);
											Bitmap iconBitmap=bd.getBitmap();
											MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(String.valueOf(R.drawable.icon), iconBitmap);
											String str=null;
											try {
												str=printDev.print(list);
//												if (j==0) {
//													str=printDev.printBmp(iconBitmap, true);  
//												}
												if (j==1) {
													str=printDev.printBmp(bitmap, true);
												}
												str=printDev.printBmp(iconBitmap, true);
												if (bitmap!=null&&!bitmap.isRecycled()) {
													bitmap.recycle();
													bitmap=null;
												}
//												if (iconBitmap!=null&&!iconBitmap.isRecycled()) {
//													iconBitmap.recycle();
//													iconBitmap=null;
//												}
												str=printDev.print(list2);
												list.clear();
												list2.clear();
												if (str.indexOf("G013")==-1) {
													if (str.indexOf("G009")!=-1) {
														isPrint=false;
														Intent intent10=new Intent();
														intent10.setAction("com.duiduifu.noOrderId");
														IndexFragment.this.getActivity().sendBroadcast(intent10);
														list.clear();
														list2.clear();
														System.gc();
														toPushHandler(6,"没有打印纸");
														return;
													}else {
														isPrint=false;
														Intent intent10=new Intent();
														intent10.setAction("com.duiduifu.noOrderId");
														IndexFragment.this.getActivity().sendBroadcast(intent10);
														list.clear();
														list2.clear();
														System.gc();
														toPushHandler(6,"准备失败打印机异常");
														return;
													}
												}
												if (j==1) {
													isPrint=false;
													Intent intent=new Intent();
													intent.putExtra("orderId", orderId);
													intent.setAction("com.duiduifu.orderId");
													IndexFragment.this.getActivity().sendBroadcast(intent);
													list.clear();
													list2.clear();
													System.gc();
												}
											} catch (Exception e) {
												e.printStackTrace();
												isPrint=false;
												Intent intent10=new Intent();
												intent10.setAction("com.duiduifu.noOrderId");
												IndexFragment.this.getActivity().sendBroadcast(intent10);
												list.clear();
												list2.clear();
												System.gc();
												toPushHandler(6,"准备失败打印机异常");
											}
										}
									}
								}else {
									for (int j = 0; j < 2; j++) {
										list.add(new PrintDataObject(AppConstantByUtil.shopName, "50", "", "center","","true"));
										if (j==0) {
											list.add(new PrintDataObject("（商户留存联）", "10", "", "center","","true"));
										}else {
											list.add(new PrintDataObject("（顾客留存联）", "10", "", "center","","true"));
										}
										list.add(new PrintDataObject("单号:  "+orderId, "10", "", "left","","true"));
										list.add(new PrintDataObject("时间:  "+orderDate, "10", "", "left","","true"));
										list.add(new PrintDataObject("操作员:   "+AppConstantByUtil.shopContact, "10", "", "left","","true"));
										if(shipName!=null && !"".equals(shipName)){
											list.add(new PrintDataObject("收货人:   "+shipName, "10", "", "left","","true"));
										}
										if(shipMobile!=null && !"".equals(shipMobile)){
											list.add(new PrintDataObject("收货人手机: "+shipMobile, "10", "", "left","","true"));
										}
										if(shipTel!=null && !"".equals(shipTel)){
											list.add(new PrintDataObject("收货人电话: "+shipTel, "10", "", "left","","true"));
										}
										if(shipAddress!=null && !"".equals(shipAddress)){
											list.add(new PrintDataObject("收货地址:  "+shipAddress, "10", "", "left","","true"));
										}
										if(paymentName!=null && !"".equals(paymentName)){
											list.add(new PrintDataObject("支付方式:  "+paymentName, "10", "", "left","","true"));
										}
										if(shipTime!=null && !"".equals(shipTime)){
											list.add(new PrintDataObject("配送时间:  "+shipTime, "10", "", "left","","true"));
										}
										list.add(new PrintDataObject("用户留言:  "+memoMsg, "10", "", "left","","true"));
										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
										list.add(new PrintDataObject("", "", "", "","","true"));
										list.add(new PrintDataObject("名称       数量       单价", "10", "", "left", "", "true"));
										for (int i = 0; i < commodityList.size(); i++) {
											list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"           "+commodityList.get(i).getNumber()+"         "+commodityList.get(i).getPrice(), "10", "", "left", "", "true"));
										}
										list.add(new PrintDataObject("订单状态:"+statusName, "10", "", "left","","true"));
										list.add(new PrintDataObject("", "", "", "","","true"));
										list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "", "center","","true"));
										list.add(new PrintDataObject("", "", "", "","","true"));
										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
										list.add(new PrintDataObject("总计:                       "+totalAmount, "10", "", "left","","true"));
										if (paymentName.equals("货到付款")) {
											list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
										}else if (statusName.equals("待付款")) {
											list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
										}else {
											list.add(new PrintDataObject("已付:                       "+totalAmount, "10", "", "left","","true"));
										}
										list.add(new PrintDataObject("", "", "", "","","true"));
										list.add(new PrintDataObject("", "", "", "","","true"));
										if (j==0) {
											list.add(new PrintDataObject("客户签名: _____________________\n\n", "10", "left", "", "true"));
										}
										list.add(new PrintDataObject("欢迎再次光临"+AppConstantByUtil.shopName, "10", "", "center","","true"));
										list.add(new PrintDataObject("地址:"+AppConstantByUtil.shopAddress, "10", "", "center","","true"));
										list.add(new PrintDataObject("电话"+AppConstantByUtil.shopPhone, "10", "", "center","","true"));
										list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
										if (j==1) {
											list.add(new PrintDataObject("我家微店在这里", "10", "", "center","","true"));
											list.add(new PrintDataObject("http://www.duiduifu.com", "10", "", "center","","true"));
										}
										List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
										list2.add(new PrintDataObject("\n\n"));
										String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(AppConstantByUtil.shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(AppConstantByUtil.terminalId.getBytes(), Base64.DEFAULT);
										if (bString!=null) {
											Pattern p = Pattern.compile("\\s*|\t|\r|\n");
											Matcher bm = p.matcher(bString);
											bString = bm.replaceAll("");
										}
										Bitmap bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
										MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(bString, bitmap);
										BitmapDrawable bd=(BitmapDrawable)getResources().getDrawable(R.drawable.icon);
										Bitmap iconBitmap=bd.getBitmap();
										MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(String.valueOf(R.drawable.icon), iconBitmap);
										String str=null;
										try {
											str=printDev.print(list);
											if (j==0) {
												str=printDev.printBmp(iconBitmap, true);
											}
											if (j==1) {
												str=printDev.printBmp(bitmap, true);
												str=printDev.printBmp(iconBitmap, true);
//												if (iconBitmap!=null&&!iconBitmap.isRecycled()) {
//													iconBitmap.recycle();
//													iconBitmap=null;
//												}
											}
											if (bitmap!=null&&!bitmap.isRecycled()) {
												bitmap.recycle();
												bitmap=null;
											}
											str=printDev.print(list2);
											list.clear();
											list2.clear();
											if (str.indexOf("G013")==-1) {
												if (str.indexOf("G009")!=-1) {
													isPrint=false;
													Intent intent10=new Intent();
													intent10.setAction("com.duiduifu.noOrderId");
													IndexFragment.this.getActivity().sendBroadcast(intent10);
													list.clear();
													list2.clear();
													System.gc();
													toPushHandler(6,"没有打印纸");
													return;
												}else {
													isPrint=false;
													Intent intent10=new Intent();
													intent10.setAction("com.duiduifu.noOrderId");
													IndexFragment.this.getActivity().sendBroadcast(intent10);
													list.clear();
													list2.clear();
													System.gc();
													toPushHandler(6,"准备失败打印机异常");
													return;
												}
											}
											if (j==1) {
												isPrint=false;
												Intent intent=new Intent();
												intent.putExtra("orderId", orderId);
												intent.setAction("com.duiduifu.orderId");
												IndexFragment.this.getActivity().sendBroadcast(intent);
												System.gc();
											}
										} catch (Exception e) {
											e.printStackTrace();
											isPrint=false;
												Intent intent10=new Intent();
												intent10.setAction("com.duiduifu.noOrderId");
												IndexFragment.this.getActivity().sendBroadcast(intent10);
												list.clear();
												list2.clear();
												System.gc();
												toPushHandler(6,"准备失败打印机异常");
										}
									}
								}
							}else {
								list.add(new PrintDataObject(AppConstantByUtil.shopName, "50", "", "center","","true"));
								list.add(new PrintDataObject("单号:  "+orderId, "10", "", "left","","true"));
								list.add(new PrintDataObject("时间:  "+orderDate, "10", "", "left","","true"));
								list.add(new PrintDataObject("操作员:   "+AppConstantByUtil.shopContact, "10", "", "left","","true"));
								if(shipName!=null && !"".equals(shipName)){
									list.add(new PrintDataObject("收货人:   "+shipName, "10", "", "left","","true"));
								}
								if(shipMobile!=null && !"".equals(shipMobile)){
									list.add(new PrintDataObject("收货人手机: "+shipMobile, "10", "", "left","","true"));
								}
								if(shipTel!=null && !"".equals(shipTel)){
									list.add(new PrintDataObject("收货人电话: "+shipTel, "10", "", "left","","true"));
								}
								if(shipAddress!=null && !"".equals(shipAddress)){
									list.add(new PrintDataObject("收货地址:  "+shipAddress, "10", "", "left","","true"));
								}
								if(paymentName!=null && !"".equals(paymentName)){
									list.add(new PrintDataObject("支付方式:  "+paymentName, "10", "", "left","","true"));
								}
								if(shipTime!=null && !"".equals(shipTime)){
									list.add(new PrintDataObject("配送时间:  "+shipTime, "10", "", "left","","true"));
								}
								list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
								list.add(new PrintDataObject("", "", "", "","","true"));
								list.add(new PrintDataObject("名称       数量       单价", "10", "", "left", "", "true"));
								for (int i = 0; i < commodityList.size(); i++) {
									list.add(new PrintDataObject(commodityList.get(i).getName()+"\n"+"           "+commodityList.get(i).getNumber()+"         "+commodityList.get(i).getPrice(), "10", "", "left", "", "true"));
								}
								list.add(new PrintDataObject("订单状态:"+statusName, "10", "", "left","","true"));
								list.add(new PrintDataObject("", "", "", "","","true"));
								list.add(new PrintDataObject("业务咨询请致电4008838700", "10", "", "center","","true"));
								list.add(new PrintDataObject("", "", "", "","","true"));
								list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
								list.add(new PrintDataObject("总计:                       "+totalAmount, "10", "", "left","","true"));
								if (paymentName.equals("货到付款")) {
									list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
								}else if (statusName.equals("待付款")) {
									list.add(new PrintDataObject("已付:                       "+"0", "10", "", "left","","true"));
								}else {
									list.add(new PrintDataObject("已付:                       "+totalAmount, "10", "", "left","","true"));
								}
								list.add(new PrintDataObject("", "", "", "","","true"));
								list.add(new PrintDataObject("", "", "", "","","true"));
								list.add(new PrintDataObject("欢迎再次光临"+AppConstantByUtil.shopName, "10", "", "center","","true"));
								list.add(new PrintDataObject("地址:"+AppConstantByUtil.shopAddress, "10", "", "center","","true"));
								list.add(new PrintDataObject("电话"+AppConstantByUtil.shopPhone, "10", "", "center","","true"));
								list.add(new PrintDataObject("------------------------------", "", "", "","","true"));
								list.add(new PrintDataObject("扫一扫，有惊喜！", "10", "", "center","","true"));
								List<PrintDataObject>list2=new ArrayList<PrintDataObject>();
								list2.add(new PrintDataObject("\n\n"));
								String bString = AppConstantByUtil.PRINT_IP+"reg?s="+Base64.encodeToString(AppConstantByUtil.shopId.getBytes(), Base64.DEFAULT)+"&tm="+Base64.encodeToString(AppConstantByUtil.terminalId.getBytes(), Base64.DEFAULT);
								if (bString!=null) {
									Pattern p = Pattern.compile("\\s*|\t|\r|\n");
									Matcher bm = p.matcher(bString);
									bString = bm.replaceAll("");
								}
								Bitmap bitmap=QRCodeEncoder.getQRCodeBmp(bString, 300);
								MyApplication.getInstants().getBitmapManger().addBitmapToMemoryCache(bString, bitmap);
								String str=null;
								try {
									str=printDev.print(list);
									str=printDev.printBmp(bitmap, true);
									str=printDev.print(list2);
									if (bitmap!=null&&!bitmap.isRecycled()) {
										bitmap.recycle();
										bitmap=null;
									}
									if (str.indexOf("G013")==-1) {
										if (str.indexOf("G009")!=-1) {
											isPrint=false;
											Intent intent10=new Intent();
											intent10.setAction("com.duiduifu.noOrderId");
											IndexFragment.this.getActivity().sendBroadcast(intent10);
											list.clear();
											list2.clear();
											System.gc();
											toPushHandler(6,"没有打印纸");
											return;
										}else {
											isPrint=false;
											Intent intent10=new Intent();
											intent10.setAction("com.duiduifu.noOrderId");
											IndexFragment.this.getActivity().sendBroadcast(intent10);
											list.clear();
											list2.clear();
											System.gc();
											toPushHandler(6,"准备失败打印机异常");
											return;
										}
									}
									isPrint=false;
									Intent intent=new Intent();
									intent.putExtra("orderId", orderId);
									intent.setAction("com.duiduifu.orderId");
									IndexFragment.this.getActivity().sendBroadcast(intent);
									list.clear();
									list2.clear();
									System.gc();
								} catch (Exception e) {
									e.printStackTrace();
									isPrint=false;
										Intent intent10=new Intent();
										intent10.setAction("com.duiduifu.noOrderId");
										IndexFragment.this.getActivity().sendBroadcast(intent10);
										list.clear();
										list2.clear();
										System.gc();
										toPushHandler(6,"准备失败打印机异常");
								}
							}
						}
					}).start();
					
				}
				break;
			case 4:
				if(!isPrint){
					loading.dismiss();
					Intent intent11=new Intent();
					intent11.setAction("com.duiduifu.orderId");
					IndexFragment.this.getActivity().sendBroadcast(intent11);
				}
				break;
			case 5:
				loading.dismiss();
				break;
			case 6:
				loading.dismiss();
				TipsToast.makeText(IndexFragment.this.getActivity(), msg.obj.toString(), 1000).show();
				break;
			case 7:
				if (msgCount>0) {
					vMessageTv.setVisibility(View.VISIBLE);
					if (msgCount>99) {
						vMessageTv.setText("99+");
					}else {
						vMessageTv.setText(String.valueOf(msgCount));
					}
				}else {
					vMessageTv.setVisibility(View.GONE);
				}
				break;
			case 8:
				if(!isPrint){
					isPrint=true;
					loading.dismiss();
					new Thread(new Runnable() {
						@Override
						public void run() {
							list.add(new PrintDataObject(AppConstantByUtil.shopName, "50", "", "center","","true"));
							list.add(new PrintDataObject("订单号:  "+orderId, "10", "", "left","","true"));
							list.add(new PrintDataObject("下单时间:  "+orderDate+"\n", "10", "", "left","","true"));
							list.add(new PrintDataObject("-------------------------------\n", "10", "", "left","","true"));
							list.add(new PrintDataObject("预约:  "+attrName, "10", "", "left","","true"));
							list.add(new PrintDataObject("联系人:  "+attrContactName, "10", "", "left","","true"));
							list.add(new PrintDataObject("联系电话:  "+attrPhone, "10", "", "left","","true"));
							list.add(new PrintDataObject("备注:  \n"+"        "+memo, "10", "", "left","","true"));
							list.add(new PrintDataObject("\n\n", "10", "", "left","","true"));
							String str=null;
							try {
								str=printDev.print(list);
								printCount++;
								if (printCount>20) {
									printCount=0;
									Thread.sleep(45000);
								}
								if (str.indexOf("G013")==-1) {
									if (str.indexOf("G009")!=-1) {
										Intent intent10=new Intent();
										intent10.setAction("com.duiduifu.noOrderId");
										list.clear();
										IndexFragment.this.getActivity().sendBroadcast(intent10);
										toPushHandler(6,"没有打印纸");
										return;
									}else {
										Intent intent10=new Intent();
										intent10.setAction("com.duiduifu.noOrderId");
										list.clear();
										IndexFragment.this.getActivity().sendBroadcast(intent10);
										System.out.println("------------准备失败打印机异常");
										toPushHandler(6,"准备失败打印机异常");
										return;	
									}
								}
								isPrint=false;
								//发送广播
								Intent intent=new Intent();
								intent.putExtra("orderId", orderId);
								intent.setAction("com.duiduifu.orderId");
								list.clear();
								IndexFragment.this.getActivity().sendBroadcast(intent);
								toPushHandler(6,"");
							} catch (Exception e) {
								e.printStackTrace();
								isPrint=false;
								Intent intent10=new Intent();
								list.clear();
								intent10.setAction("com.duiduifu.noOrderId");
								IndexFragment.this.getActivity().sendBroadcast(intent10);
								toPushHandler(6,"准备失败打印机异常");
							}
						}
					}).start();
				}
				break;
			case 9:
				if(!isPrint){
					Intent intent12=new Intent();
					intent12.setAction("com.duiduifu.noOrderId");
					IndexFragment.this.getActivity().sendBroadcast(intent12);
				}
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	public void onStart() {
		clearNotification();
		super.onStart();
	}



	public void toPushHandler(int what,String msg) {
		Message message=handler.obtainMessage();
		message.obj=msg;
		message.what=what;
		handler.sendMessage(message);
	}
	private void clearNotification() {
		//启动后删除之前我们定义的通知   
        NotificationManager notificationManager = (NotificationManager) getActivity() 
                .getSystemService(Context.NOTIFICATION_SERVICE);   
        notificationManager.cancel(0);  
	}



	@Override
	public void onPasswordInputDialogOK(int currentId) {
		if(currentId == R.id.setting_layer){
			Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS); //系统设置
			this.startActivityForResult( intent , 0);
			return;
		}
		
		if(currentId == R.id.mtms_layer){
			if(isAppInstalled("com.centerm.mtms.tm")){
				Intent intent=new Intent();
				
				ComponentName componetName = new ComponentName(
		                "com.centerm.mtms.tm",
		                "com.centerm.mtms.tm.activity.main.LoginActivity"); 
				
		        intent.setAction("android.intent.action.VIEW");  
		        
		        intent.setComponent(componetName);

		        startActivity(intent);	
			}else{
				TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装MTMS客户端！", 1000).show();
			}
			
			return;
		}
	}





	@Override
	public void onSettingClick() {
		if(vPasswordDialog!=null){
			vPasswordDialog.showDialog(R.id.setting_layer,PasswordDialog.SETTING);
		}
	}

	@Override
	public void onVPNClick() {
		Intent intent = new Intent();
		ComponentName cmp = new ComponentName("com.android.settings","com.android.settings.ApnSettings");
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(cmp);
		startActivity(intent);
	}

	@Override
	public void onGClick() {
		try {
			loading();
			toggleMobileData(IndexFragment.this.getActivity(),!isOpenG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWIFIClick() {
		Intent localIntent = new Intent();
		localIntent.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.wifi.WifiPickerActivity"));
		startActivityForResult(localIntent, 100);
	}

	@Override
	public void onMTMSClick() {
		if(vPasswordDialog!=null){
			vPasswordDialog.showDialog(R.id.mtms_layer,PasswordDialog.MTMS);
		}
	}


	@Override
	public void onDateClick() {
		Intent localIntent = new Intent();
		localIntent.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.DateTimeSettingsSetupWizard"));
		startActivityForResult(localIntent, 101);
							
	}





	@Override
	public void onImageClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkOrder() {
		// TODO Auto-generated method stub
		Intent intent1=new Intent();
		intent1.setAction("com.duiduifu.allOrderId");
		IndexFragment.this.getActivity().sendBroadcast(intent1);
		if (isAppInstalled("com.duiduifu.message.activity")) {
			Intent intent = new Intent();
			intent.putExtra("terminalId", AppConstantByUtil.terminalId);
			intent.putExtra("userId", AppConstantByUtil.userId);
			intent.putExtra("loginSign", loginSign);
			intent.putExtra("shopId", AppConstantByUtil.shopId);
		 	intent.putExtra("shopPhone", AppConstantByUtil.shopPhone);
		 	intent.putExtra("shopAddress", AppConstantByUtil.shopAddress);
			intent.putExtra("shopName", AppConstantByUtil.shopName);
			intent.putExtra("shopContact",
					AppConstantByUtil.shopContact);
			ComponentName componetName = new ComponentName(
					"com.duiduifu.message.activity",
					"com.duiduifu.message.activity.MainActivity");
			intent.setAction("android.intent.action.VIEW");
			intent.setComponent(componetName);
			startActivityForResult(intent, 0);
			IndexFragment.this.getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}else{
			TipsToast.makeText(IndexFragment.this.getActivity(), "尚未安装该业务应用，请检测联网安装或联系技术人员！", 1000).show();
		}
	}
}
