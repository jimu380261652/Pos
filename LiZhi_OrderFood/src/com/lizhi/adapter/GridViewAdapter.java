package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duiduifu.bean.Sf_BusinessConfig;
import com.duiduifu.util.AppConstantByUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pos.lizhiorder.R;

/**
 * 主界面适配器
 * @author YangTaoLin
 *
 */
public class GridViewAdapter extends BaseAdapter {

	/**
	 * 列表视图
	 */
	private final class ViewHolder {
		public TextView textView;
		public ImageView imageView,imageView_black;
		public Button button;
		public ProgressBar progressBar;
	}

	private ArrayList<HashMap<String, Object>> mData;
	private Activity mActivity;
	private OnSelectButtonListener onSelectButtonListener;
//	private ImageLoader imageLoader;
    private int mCount;
	public GridViewAdapter(Activity mActivity,
			ArrayList<HashMap<String, Object>> mData,
			OnSelectButtonListener onSelectButtonListener) {
		this.onSelectButtonListener = onSelectButtonListener;
		this.mActivity = mActivity;
		this.mData = mData;
//		imageLoader=new ImageLoader(mActivity,"icon");
//		try {
//			imageLoader = ((MyApplication)mActivity.getApplication()).getImageLoader();
//			imageLoader.setCacheFolder("icon");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public void clearMeomery(){
//		if(imageLoader!=null){
//			imageLoader.clearCache();
//		}
	}

	public interface OnSelectButtonListener {
		public void onSelect(int position,boolean isUpdateFlag,String downLoadUrl);
	}

	public void setOnSelectListener(
			OnSelectButtonListener onSelectButtonListener) {
		this.onSelectButtonListener = onSelectButtonListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("static-access")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// LayoutInflater是用来找layout下xml布局文件，并且实例化
		// convertView指当前显示的视图
		LayoutInflater layoutInflater = null;
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {

			convertView = layoutInflater.from(mActivity).inflate(
					R.layout.activity_main_item, null);
			viewHolder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.textView);
			viewHolder.button = (Button) convertView
					.findViewById(R.id.button);
			viewHolder.imageView_black= (ImageView) convertView
			.findViewById(R.id.imageView_black);
			
			convertView.setTag(viewHolder);

		} else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		//防止重复执行
		/*if (position == 0)
		{
			mCount++;
		}
		else
		{
			mCount = 0;
		}
		
		if (mCount > 1)
		{
			return convertView;
		}*/
		//System.out.println("position==="+position);

		final int _position = position;
//		final boolean isUpdateFlag =  (Boolean) mData.get(position).get("isUpdateFlag");
		final String apkUrl = AppConstantByUtil.HOST+ (String) mData.get(position).get(Sf_BusinessConfig.apkUrl);
//		imageLoader.DisplayImage(AppConstantByUtil.FILE_HOST+(String) mData.get(position).get(Sf_BusinessConfig.iconUrl),(String) mData.get(position).get(Sf_BusinessConfig.iconVersion), mActivity, viewHolder.imageView);
		ImageLoader.getInstance().displayImage(AppConstantByUtil.FILE_HOST+(String) mData.get(position).get(Sf_BusinessConfig.iconUrl), viewHolder.imageView);
		viewHolder.textView.setText((CharSequence) mData.get(position).get("newText"));
		viewHolder.textView.setVisibility((Integer) mData.get(position).get("newIsVisible"));
		viewHolder.imageView_black.setVisibility((Integer) mData.get(position).get("blackIsVisible"));
		
		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				onSelectButtonListener.onSelect(_position,isUpdateFlag,apkUrl);
				onSelectButtonListener.onSelect(_position,false,apkUrl);
			}
		});
		return convertView;
	}
}
