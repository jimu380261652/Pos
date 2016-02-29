package com.lizhi.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.CommonUtil;
import com.lizhi.adapter.GridViewAdapter;
import com.lizhi.adapter.ViewPagerAdapter;

public class BaseFragment extends Fragment {

	// 公共参数

	public ViewPager mViewPager;

	public Handler mHandler;

	public AsyncRunner asyncRunner = new AsyncRunner();

	public Timer timer = new Timer();

	public boolean initFlag[];
 
	public ViewGroup mViewGroup;

	public RelativeLayout mProgressDialog;
	
	public RelativeLayout system_hint_layer;

//	public DbUtil dbUtil = ((MyApplication)this.getActivity().getApplication()).getDbUtil();

	public List<ArrayList<HashMap<String, Object>>> mMainData = new ArrayList<ArrayList<HashMap<String, Object>>>();

	public ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

	public TextView mTextViewExceptionTip;

	public String exceptionTip;
	
	// 主界面

	public GridViewAdapter[] mGridViewAdapters;

	public GridView[] mGridViews;

	public ImageView[] mCircleGroup;// 滑动圆点

	public ProgressBar[][] progressBars;// 进度条

	ImageView[][] imageViewsBlack;// 进度条背景

	public TextView[][] textViews;// 更新提示

	public int[][] fileSize;// 文件大小

	public int[][] downLoadFileSize;// 已下载多少

	public CommonUtil commonUtil = new CommonUtil(getActivity());

	public LinearLayout mLayoutCircleGroup, mLayoutNetWordTip;

	public Button mReloadButton;

	public int lastCirclePosition; 
	
	//FIXME 不清楚这个干吗的
//	int totalPage;

	public ViewPagerAdapter mViewPagerAdapter;

	// 广告

	public ImageView[] mImageViews = new ImageView[10];

	public RelativeLayout mLayoutAd;

//	public ImageLoader imageLoader = new ImageLoader(getActivity(), "ad");

	public LayoutInflater lf;

	public int adCount;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	 
	

}
