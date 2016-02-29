package com.lizhi.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BaseListAdapter<T> extends BaseAdapter  {
	

	protected List<T> data = null;
	protected LayoutInflater inflater;
	
	
	
	
	public void setLayoutInflater(LayoutInflater infl){
		inflater = infl;
	}
	
	
	public void update(List<T> list){
		data = list;
		notifyDataSetChanged();
	}
	
	
	public void update(Map<String, T> map){
		data = new ArrayList<T>();
		data.addAll(map.values());
	}

	
	
	public List<T> getData(){
		return data;
	}
	
	
	public int getCount() {
		if(data!=null && data.size()>0){
			return data.size();    
		}
		return 0;
	}

	public T getItem(int position) {
		if(data!=null && data.size()>0){
			return data.get(position);    
		}
		return null;
	}

	
	public long getItemId(int position) {
		return 0;
	}



	
    public interface IMoreList{
    	
    	public Object getHolder();

		
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
}


