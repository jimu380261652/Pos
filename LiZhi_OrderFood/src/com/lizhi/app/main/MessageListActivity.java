package com.lizhi.app.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.duiduifu.bean.Sf_Message;
import com.duiduifu.db.DbUtil;
import com.duiduifu.http.AsyncListener;
import com.duiduifu.http.AsyncRunner;
import com.duiduifu.util.AppConstantByUtil;
import com.duiduifu.util.ValidateUtil;
import com.lizhi.adapter.MessageListAdapter;
import com.lizhi.adapter.MessageListAdapter.OnDeleteButtonListener;
import com.lizhi.push.MyApplication;
import com.pos.lizhiorder.R;

public class MessageListActivity extends BaseActivity implements OnDeleteButtonListener, OnScrollListener, OnItemClickListener, OnClickListener {
	
	public final static int MESSAGE_LIST_RESULT = 6;
	
	public Button vBackBtn;
	
	public ListView vListView;
	
	public MessageListAdapter messageListAdapter;
	
	public int currentPager = 0;
	
	public boolean isMore = true;
	
	DbUtil dbUtil = ((MyApplication)getApplication()).getDbUtil();
	
	ArrayList<HashMap<String,Object>> mData = new ArrayList<HashMap<String,Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_message_list);
		((MyApplication)getApplication()).addActivity(this);
		initUI();
		
		
	}

	
	private void initUI() {
	
		vBackBtn = (Button) this.findViewById(R.id.back_btn);
		vListView = (ListView) this.findViewById(R.id.message_list);
		
		messageListAdapter = new MessageListAdapter(this, mData, this);
		vListView.setAdapter(messageListAdapter);
		vListView.setOnScrollListener(this);
		vListView.setOnItemClickListener(this);
		vBackBtn.setOnClickListener(this);
		
		requestData();
		
	}

	/**
	 * 点击事件
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.back_btn){
			this.setResult(MESSAGE_LIST_RESULT);
			this.finish();
			return;
		}
		
	}
	

	private void requestData() {
		
		HashMap<String, Object> messageMap = dbUtil.SelectLastMessage();
		
		HashMap<String, Object> mHashMap=new HashMap<String, Object>();
		
		if((Boolean)messageMap.get("resultCode")){
			
			mHashMap.put("LastId", messageMap.get(Sf_Message.messageId));
			
		}
		
		
		AsyncRunner.getInstance().request(AppConstantByUtil.HOST+"访问得到消息列表", "GET", mHashMap.entrySet(), new AsyncListener() {
			
			@Override
			public void onException(Object exceptionInfo) {
				sendMessageToHandler(AppConstantByUtil.LOAD_EXCEPTION);
			}
			
			@Override
			public void onComplete(String values) {
				
				try {
					
					JSONObject jsonObject = new JSONObject(values);
					String msg= jsonObject.getString("message");
					String code=jsonObject.getString("code");
					
					if(code.equals(String.valueOf(AppConstantByUtil.SUCCESS))){
						
						JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
						
						String messageId,title,message,memo,flag;
						
						HashMap<String,Object> mMessageHashMap;
						
						for(int i = 0;i<jsonArray.length();i++){
							messageId = jsonArray.getJSONObject(i)
									.getString(Sf_Message.messageId);

							title= jsonArray.getJSONObject(i)
									.getString(Sf_Message.title);
							
							message = jsonArray.getJSONObject(i)
									.getString(Sf_Message.message);

							memo = jsonArray.getJSONObject(i)
									.getString(Sf_Message.memo);
							
							flag = Sf_Message.NEW;
							
							if(!ValidateUtil.isEmpty(messageId)){

								mMessageHashMap = dbUtil.SelectMessageIsExist(messageId);
								
								if((Boolean) mMessageHashMap
										.get("resultCode")){
									dbUtil.UpdateMessage(messageId, title, message, memo, flag);
								}else{
									dbUtil.InsertMessage(messageId, title, message, memo, flag);
								}
								
							}
						}
						sendMessageToHandler(AppConstantByUtil.SUCCESS);
						
					}else{
						sendMessageToHandler(AppConstantByUtil.LOAD_EXCEPTION);
					}
				
				
				} catch (Exception e) {
					e.printStackTrace();
					sendMessageToHandler(AppConstantByUtil.LOAD_EXCEPTION);
				}
				
			}
		});
		
		
	}
	
	
	//分页加载显示数据
	private void showData() {
		new Thread(){

			public void run() {
				ArrayList<HashMap<String, Object>> data = dbUtil.SelectMessageForPager(currentPager);
				mData.addAll(data);
				if(data.size()>=15){
					isMore = true;
				}else{
					isMore = false;
				}
				sendMessageToHandler(3);
			};
			
		}.start();
	}
	
	
	
	public void sendMessageToHandler(int what) {

		Message message = mMessageHandler.obtainMessage();
		message.what = what;
		mMessageHandler.sendMessage(message);
	}
	
	Handler mMessageHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			
			switch (msg.what) {
			
				case 0:
					Toast.makeText(MessageListActivity.this, AppConstantByUtil.app_network_exception_tip, Toast.LENGTH_SHORT).show();
					showData();
					break;
					
				case 1:
					showData();
					break;
					
				case 3:
					messageListAdapter.notifyDataSetChanged();
					break;
					
				default:
					break;
			}
			
			
		}

		
	};
	
	
	
	
	//==========  Scoller调用   ==========
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if((firstVisibleItem + visibleItemCount == totalItemCount) && (totalItemCount != 0) && isMore){
			++currentPager;
			showData();
		}
	}
	
	//========= listView的Item点击  =============
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<String, Object>  data = mData.get(position);
		dbUtil.UpdateMessage((String)data.get(Sf_Message.messageId), (String)data.get(Sf_Message.title), (String)data.get(Sf_Message.message), (String)data.get(Sf_Message.memo), Sf_Message.READ);
		messageListAdapter.notifyDataSetChanged();
	}
	
	
	//=============  Adapter删除键的回调  ==============

	@Override
	public void onDelete(int position) {
		HashMap<String, Object>  data = mData.get(position);
		dbUtil.UpdateMessage((String)data.get(Sf_Message.messageId), (String)data.get(Sf_Message.title), (String)data.get(Sf_Message.message), (String)data.get(Sf_Message.memo), Sf_Message.DELETE);
		mData.remove(position);
		messageListAdapter.notifyDataSetChanged();
	}



}
