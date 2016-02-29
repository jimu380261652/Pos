package com.lizhi.bean;
/**
 * 不打印订单号实体类
 * @author user
 *
 */
public class UnPrintOrder {
	private String orderId;
	private String isPrint;
	private String count;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getIsPrint() {
		return isPrint;
	}
	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
}
