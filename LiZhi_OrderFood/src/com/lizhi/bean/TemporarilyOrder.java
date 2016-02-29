package com.lizhi.bean;

import java.util.ArrayList;
import java.util.List;

public class TemporarilyOrder {
	public String date;
	public List<Merchandise>mers=new ArrayList<Merchandise>();

	public List<Merchandise> getMers() {
		return mers;
	}

	public void setMers(List<Merchandise> list) {
		this.mers =  list;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	 

	 
}
