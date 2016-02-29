package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duiduifu.bean.Sf_Advertisement;
import com.duiduifu.db.DbUtil;
import com.duiduifu.http.AsyncListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.MD5Util;
import com.lizhi.app.main.MainCopyActivity;
import com.lizhi.push.MyApplication;
import com.pos.lizhiorder.R;
/**
 * 
 * @ClassName: AdvertisementFragment.java 
 *
 * @Description: 广告
 *
 * @author YangTaoLin
 *
 * @date 2013-12-12 上午18:06:09 
 *
 */
public final class AdvertisementFragment extends BaseFragment implements OnPageChangeListener {
		
	private static OnAdvertisementFragmentListener onAdvertisementFragmentListener;
	
	public ImageView vAdvClose;
	
	public List<String> advIdList = new ArrayList<String>();
	
	public DbUtil dbUtil = null;
	
//	public ImageLoader imageLoader = null;

	public interface OnAdvertisementFragmentListener {

		public void showIndexFragment();
				
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbUtil = ((MyApplication)this.getActivity().getApplication()).getDbUtil();
//		imageLoader = ((MyApplication)this.getActivity().getApplication()).getImageLoader();
//		imageLoader.setCacheFolder("ad");
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		try {

			onAdvertisementFragmentListener = (OnAdvertisementFragmentListener) activity;
			
		} catch (ClassCastException e) {
			

			throw new ClassCastException(activity.toString()+ "must implement OnAdvertisementFragmentListener");
		}
		
	}
	
	public static AdvertisementFragment newInstance(String content) {
		
		AdvertisementFragment fragment = new AdvertisementFragment();

		
		return fragment;
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				
		View mViewGroup = inflater.inflate(R.layout.activity_advertisement,
				
				container, false);
		
		lf = LayoutInflater.from(getActivity());
		
		vAdvClose = (ImageView) mViewGroup.findViewById(R.id.adv_close);
		vAdvClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainCopyActivity)AdvertisementFragment.this.getActivity()).showIndexFragment();
			}
		});

		mViewPager = (ViewPager) mViewGroup.findViewById(R.id.viewpager);			//ViewPager
		
		mLayoutAd = (RelativeLayout) mViewGroup.findViewById(R.id.layout_ad);		//整个xml
		
		mLayoutAd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
	 			
			}
		});

		mProgressDialog=(RelativeLayout) mViewGroup.findViewById(R.id.layout_progress_dialog);
		
		mProgressDialog.getBackground().setAlpha(125);
		
		mProgressDialog.setVisibility(View.GONE);
		
		loadData();

		// 更新下载进度条
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				
				case AppConstantByUtil.LOAD_AD:
					
					initView(mData.size());

					for(int i=0;i<mData.size();i++){						
						ImageLoader.getInstance().displayImage(AppConstantByUtil.FILE_HOST+mData.get(i).get(Sf_Advertisement.adPicUrl).toString(), mImageViews[i]);
					}
					
					mProgressDialog.setVisibility(View.GONE);
					
					if(mData.size()>0){
						setTimerTask();
						IndexFragment.advEmpty = false;
					}else{
						IndexFragment.advEmpty = true;
					}
					
					//如果有广告更新的话，成功后，报告服务器
					if(advIdList.size()>0){
						reportAdv();
					}
					
					
					break;
					
				case AppConstantByUtil.LOAD_EXCEPTION:
					
					mProgressDialog.setVisibility(View.GONE);
					if(mData.size()>0){
						pause = true;
						IndexFragment.advEmpty = false;
					}else{
						pause=false;
						IndexFragment.advEmpty = true;
					}
					
					break;
					
				case AppConstantByUtil.AD_ROTATION:
					
					mViewPager.setCurrentItem(adCount);

					break;

				default:
					break;
				}
				


			}

		};
		return mViewGroup;
	}

	//初始View
	public void initView(int size){
		
		final ArrayList<View> aViews = new ArrayList<View>();

		for(int i=0;i<size;i++){
			
			View v1 = lf.inflate(R.layout.activity_advertisement_item, null);
			
			aViews.add(v1);
			
			mImageViews[i] = (ImageView) aViews.get(i).findViewById(R.id.imageView);			
			
		}
		
		PagerAdapter pa = new PagerAdapter() {

			@Override
			public int getCount() {
				return aViews.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(aViews.get(position));
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void finishUpdate(View arg0) {

			}

			@Override
			public Object instantiateItem(View arg0, int arg1) {
				View view=mImageViews[arg1];
				
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//setResult(RESULT_OK);
						//finish();
						onAdvertisementFragmentListener.showIndexFragment();
					}
				});
				  
				((ViewPager) arg0).addView(aViews.get(arg1), 0);
				
				return aViews.get(arg1);
			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {

			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			@Override
			public void startUpdate(View arg0) {

			}
		};


		mViewPager.setAdapter(pa);

		mViewPager.setOnPageChangeListener(this);

		
	}
	
	//广告下载完之后将信息传给服务器
	public void reportAdv(){
		
		if (advIdList.size()>0) {
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < advIdList.size(); i++) {
				sb.append(advIdList.get(i));
				if (i < advIdList.size() - 1) {
					sb.append(",");
				}
			}
			
			String sign = MD5Util.createSign(AppConstantByUtil.terminalId , AppConstantByUtil.userId , sb.toString() , AppConstantByUtil.loginSign);
			HashMap<String, Object> mHashMap = new HashMap<String, Object>();
			mHashMap.put("terminalId", AppConstantByUtil.terminalId);
			mHashMap.put("userId", AppConstantByUtil.userId);
			mHashMap.put("loginSign", AppConstantByUtil.loginSign);
			mHashMap.put("adId", sb.toString());
			mHashMap.put("sign", sign);
			
			asyncRunner.request(AppConstantByUtil.HOST
					+ "core/AdLog/addAdLog.do", "GET", mHashMap.entrySet(),
					new AsyncListener() {
						@Override
						public void onException(Object exceptionInfo){
						}

						@Override
						public void onComplete(String values) {
						}

					});
		}

	}
	
	//广告下载，加载数据
	public void loadData(){
		
		mData.clear();
		advIdList.clear();
		
		String sign=MD5Util.createSign(AppConstantByUtil.terminalId,AppConstantByUtil.userId, AppConstantByUtil.loginSign);
		
		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
		
		mHashMap.put("terminalId", AppConstantByUtil.terminalId);
		
		mHashMap.put("userId", AppConstantByUtil.userId);
		
		mHashMap.put("loginSign",AppConstantByUtil.loginSign);
		
		mHashMap.put("sign",sign);
		
		asyncRunner
		.request(
				AppConstantByUtil.HOST+"core/Ad/adDownload.do",
				"GET", mHashMap.entrySet(), new AsyncListener() {

					@Override
					public void onException(Object exceptionInfo)

					{
						sendMsg(AppConstantByUtil.LOAD_EXCEPTION);
					}

					@Override
					public void onComplete(String values) {
						try {
							

							JSONObject jsonObject = new JSONObject(
									values);
							
							String code = jsonObject.getString("code");

							if (code.equals(String.valueOf(AppConstantByUtil.SUCCESS))) {
								
								JSONArray jsonArray = new JSONArray(
										jsonObject.getString("ads"));
								String advertisementId, adPicUrl, adPicVersion, adUrl, memo;
								HashMap<String, Object> mBusinessHashMap;
								ArrayList<HashMap<String, Object>> onLineData = new ArrayList<HashMap<String, Object>>();
								HashMap<String, Object> mHashMap;
								for (int i = 0; i < jsonArray.length(); i++) {
									
									 mHashMap = new HashMap<String, Object>();
									 
									advertisementId = jsonArray
											.getJSONObject(i)
											.getString(
													Sf_Advertisement.advertisementId);

									adPicUrl = jsonArray.getJSONObject(i)
											.getString(
													Sf_Advertisement.adPicUrl);

									//								adPicVersion = jsonArray.getJSONObject(i)
									//										.getString(Sf_Advertisement.adPicVersion);

									adUrl = jsonArray.getJSONObject(i)
											.getString(Sf_Advertisement.adUrl);

									memo = jsonArray.getJSONObject(i)
											.getString(Sf_Advertisement.memo);

									advIdList.add(advertisementId);

									//								HashMap<String, Object> mHashMap = new HashMap<String, Object>();
									//
									//								mHashMap.put(Sf_Advertisement.advertisementId, advertisementId);
									//
									//								mHashMap.put(Sf_Advertisement.adPicUrl, adPicUrl);
									//
									//								mHashMap.put(Sf_Advertisement.adPicVersion, adPicVersion);
									//
									//								mHashMap.put(Sf_Advertisement.adUrl, adUrl);
									//
									//								mHashMap.put(Sf_Advertisement.memo, memo);

									mBusinessHashMap = dbUtil
											.SelectAdvertisementIsExist(jsonArray
													.getJSONObject(i)
													.getString(
															Sf_Advertisement.advertisementId));

									// 判断广告是否存在
									if ((Boolean) mBusinessHashMap
											.get("resultCode")) {

										// 判断版本是否有更新
										//									if (!adPicVersion
										//											.equals(mBusinessHashMap
										//													.get(Sf_Advertisement.adPicVersion))) {

										// System.out.println("有版本更新");

										//										mHashMap.put("newIsVisible",
										//												View.VISIBLE);
										//
										//										mHashMap.put("isUpdateFlag",
										//												true);
										// 如果版本不一样的话,需要更新本地数据库
										dbUtil.UpdateAdvertisement(
												advertisementId, adPicUrl, "0",
												adUrl, memo);

										//									} else {
										// 没有版本更新
										// System.out.println("没有版本更新");
										//									}

									} else {
										// 插入新的广告
										dbUtil.InsertAdvertisement(
												advertisementId, adPicUrl, "0",
												adUrl, memo);

									}
									
									
									mHashMap.put(Sf_Advertisement.adPicUrl, adPicUrl);

									mHashMap.put(Sf_Advertisement.adPicVersion, "0");

									mHashMap.put(Sf_Advertisement.adUrl, adUrl);

									mHashMap.put(Sf_Advertisement.memo, memo);
									
									onLineData.add(mHashMap);

									//								mData.add(mHashMap);

								}
								//查询本地的所有广告
//								mData = dbUtil.SelectAllAdvertisement();
								mData = onLineData;	//如果正常有网络，使用网络数据
								sendMsg(AppConstantByUtil.LOAD_AD);
							}else{
								//查询本地的所有广告
								mData = dbUtil.SelectAllAdvertisement();
								sendMsg(AppConstantByUtil.LOAD_EXCEPTION);
							}

						} catch (Exception e) {

							e.printStackTrace();
							//查询本地的所有广告
							mData = dbUtil.SelectAllAdvertisement();
							sendMsg(AppConstantByUtil.LOAD_EXCEPTION);
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
	
	}

	public static boolean pause=false;
	
	private int second; 
	
	//广告图片轮播 
	private void setTimerTask(){ 
		 
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				
				if(pause){
					
					second=second+1;
					
					if(second>10){
						
						second=0;
						
						adCount=adCount+1;
						
						if(adCount>=mData.size())
							adCount=0;
						
						sendMsg(AppConstantByUtil.AD_ROTATION);
						
					}
					
					
				}
				
			}
		},0, 1000);
	 
    }
	 
	/**
	 * 发送信息
	 * 
	 * @param flag
	 */
	private void sendMsg(int flag) {
		
		Message msg = new Message();
		
		msg.what = flag;
		
		mHandler.sendMessage(msg);
	}
	
	@Override
	public void onDestroy() {
//		try {
//			if(imageLoader!=null){
//				imageLoader.clearCache();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		
		
		if(mData.size()<=0 && !hidden){
						
			mProgressDialog.setVisibility(View.VISIBLE);
			
			loadData();
		}
	}

}

