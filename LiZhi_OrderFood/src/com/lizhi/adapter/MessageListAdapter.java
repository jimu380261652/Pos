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
import android.widget.TextView;

import com.duiduifu.bean.Sf_Message;
import com.pos.lizhiorder.R;

/**
 * 消息列表设配器
 * @author ZengPeiWei
 *
 */
public class MessageListAdapter extends BaseAdapter {

	

	private ArrayList<HashMap<String, Object>> mData;
	private Activity mActivity;
	private OnDeleteButtonListener mOnDeleteButtonListener;
	public MessageListAdapter(Activity mActivity,
			ArrayList<HashMap<String, Object>> mData,
			OnDeleteButtonListener mOnDeleteButtonListener) {
		this.mActivity = mActivity;
		this.mData = mData;
		this.mOnDeleteButtonListener = mOnDeleteButtonListener;
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

		LayoutInflater layoutInflater = null;
		ViewHolder viewHolder;
		if (convertView == null) {
			
			viewHolder = new ViewHolder();
			
			convertView = layoutInflater.from(mActivity).inflate(
					R.layout.item_message_list, null);
			
			viewHolder.vMessageName = (TextView) convertView.findViewById(R.id.item_message_name);
			viewHolder.vMessageTime = (TextView) convertView.findViewById(R.id.item_message_time);
			viewHolder.vMessageHint = (ImageView) convertView.findViewById(R.id.item_message_hint);
			viewHolder.vMessageDelete = (Button) convertView.findViewById(R.id.item_message_delete);
			
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.vMessageName.setText((String)mData.get(position).get(Sf_Message.title));
		if(((String)mData.get(position).get(Sf_Message.flag)).equals(Sf_Message.NEW)){
			viewHolder.vMessageHint.setVisibility(View.VISIBLE);
		}else{
			viewHolder.vMessageHint.setVisibility(View.INVISIBLE);
		}

		final int _position = position;
		
		viewHolder.vMessageDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnDeleteButtonListener.onDelete(_position);
			}
		});
		return convertView;
	}
	
	
	private final class ViewHolder {
		public TextView vMessageName,vMessageTime;
		public ImageView vMessageHint;
		public Button vMessageDelete;
	}
	
	public interface OnDeleteButtonListener {
		public void onDelete(int position);
	}
	
}
