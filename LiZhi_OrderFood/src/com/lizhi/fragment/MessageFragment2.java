package com.lizhi.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lizhi.adapter.MessageAdapter;
import com.lizhi.adapter.SystemNotifyAdapter;
import com.lizhi.bean.ERP_Message;
import com.lizhi.bean.MessageList;
import com.pos.lizhiorder.R;

public class MessageFragment2 extends BaseFragment{
	private Context context;
	private ListView lv_type,lv_message;
	private MessageAdapter itemAdapter;
	private SystemNotifyAdapter messageAdapter;
	private ArrayList<ERP_Message> list;
	private ArrayList<MessageList> message;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle SavedInstanceState){
		View MyViewGroup =inflater.inflate(R.layout.fragment_message2,container, false);
		return MyViewGroup;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		context=getActivity();
		list=new ArrayList<ERP_Message>();
		message=new ArrayList<MessageList>();
		initMessageType();
		initView();
	}
	public void initView(){
		itemAdapter=new MessageAdapter(context,0);
		messageAdapter=new SystemNotifyAdapter(context);
		lv_type=(ListView)getView().findViewById(R.id.lv_message2_left);
		lv_message=(ListView)getView().findViewById(R.id.lv_message2_right);
		itemAdapter.update(list);
		lv_type.setAdapter(itemAdapter);
		messageAdapter.update(message);
		lv_message.setAdapter(messageAdapter);
		lv_type.setOnItemClickListener(l_type);
	}
	private OnItemClickListener l_type=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			switch(position){
			case 0:{
				
			}
			break;
			case 1:{
			}
			break;
			case 2:{
			}
			break;
			case 3:{
			}
			break;
			}
			itemAdapter.update(position);
		}
	};
	private void initMessageType(){
		list=new ArrayList<ERP_Message>();
		ERP_Message message1=new ERP_Message();
		message1.setType("充值提醒");
		list.add(message1);
		ERP_Message message2=new ERP_Message();
		message2.setType("系统通知");
		list.add(message2);
		ERP_Message message3=new ERP_Message();
		message3.setType("升级提醒");
		list.add(message3);
		ERP_Message message4=new ERP_Message();
		message4.setType("提现提醒");
		list.add(message4);
		
	 }
}
