package com.lizhi.bean;

import java.io.Serializable;

public class ERP_Account implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ACCOUNT_JOURNAL_ID;
	private String CREATE_TIME;
	private String OPERATION;
	private float ACCOUNT_BALANCE;
	private float AMOUNT;
	
	public float getAMOUNT() {
		return AMOUNT;
	}
	public void setAMOUNT(float aMOUNT) {
		AMOUNT = aMOUNT;
	}
	public String getACCOUNT_JOURNAL_ID() {
		return ACCOUNT_JOURNAL_ID;
	}
	public void setACCOUNT_JOURNAL_ID(String aCCOUNT_JOURNAL_ID) {
		ACCOUNT_JOURNAL_ID = aCCOUNT_JOURNAL_ID;
	}
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public String getOPERATION() {
		return OPERATION;
	}
	public void setOPERATION(String oPERATION) {
		OPERATION = oPERATION;
	}
	public float getACCOUNT_BALANCE() {
		return ACCOUNT_BALANCE;
	}
	public void setACCOUNT_BALANCE(float aCCOUNT_BALANCE) {
		ACCOUNT_BALANCE = aCCOUNT_BALANCE;
	}
	
	
}
