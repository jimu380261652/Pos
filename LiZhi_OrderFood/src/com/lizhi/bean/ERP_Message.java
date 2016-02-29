package com.lizhi.bean;

import java.io.Serializable;
//消息
public class ERP_Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private Boolean unReaded=true;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getUnReaded() {
		return unReaded;
	}
	public void setUnReaded(Boolean unReaded) {
		this.unReaded = unReaded;
	}
	
}
