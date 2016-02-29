package com.lizhi.bean;

import java.io.Serializable;
 
public class Temporary implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private int num = 1;
	private int nativeLogo;
	private String logo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getNativeLogo() {
		return nativeLogo;
	}

	public void setNativeLogo(int nativeLogo) {
		this.nativeLogo = nativeLogo;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	

}
