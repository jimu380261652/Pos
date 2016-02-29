package com.lizhi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.pos.lizhiorder.R;

public class PosFragment2 extends NoBugFragment implements OnClickListener{
	private Callback callback;
	private Button back;
	private Context context;
	@Override
	public View oncreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view =inflater.inflate(R.layout.fragment_pos2, container,
				false);
 
		return view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		context = get_Activity();
	 
		initUI();
	 
	}
	private void initUI() {
		// TODO Auto-generated method stub
		back=(Button) view.findViewById(R.id.btn_back);
		back.setOnClickListener(this);
	}
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public interface Callback {
		public void callback();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btn_back){
			callback.callback();
		}
	}
}
